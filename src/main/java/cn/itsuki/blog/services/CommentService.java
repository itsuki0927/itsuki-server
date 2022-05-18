package cn.itsuki.blog.services;

import cn.hutool.core.bean.BeanUtil;
import cn.itsuki.blog.constants.CommentState;
import cn.itsuki.blog.entities.*;
import cn.itsuki.blog.entities.requests.*;
import cn.itsuki.blog.entities.responses.SearchResponse;
import cn.itsuki.blog.repositories.*;
import cn.itsuki.blog.utils.RequestUtil;
import cn.itsuki.blog.utils.UrlUtil;
import com.alibaba.fastjson.JSONObject;
import graphql.GraphQLException;
import graphql.kickstart.servlet.context.GraphQLServletContext;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 评论 服务
 *
 * @author: itsuki
 * @create: 2021-10-03 16:34
 **/
@Service
public class CommentService extends BaseService<Comment, CommentSearchRequest> implements GraphQLQueryResolver, GraphQLMutationResolver {

    @Autowired
    private BlackListService blackListService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private RequestUtil requestUtil;
    @Autowired
    private AkismetService akismetService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UrlUtil urlUtil;
    @Value("${mail.admin}")
    private String adminEmail;
    @Value("${mode.isDev}")
    private boolean isDev;
    @Value("${mode.isProd}")
    private boolean isProd;
    @Value("${cors.webUrl}")
    private String webUrl;

    CommentService() {
        super("id", "id");
    }

    @Override
    protected Page<Comment> searchWithPageable(CommentSearchRequest criteria, Pageable pageable) {
        return ((CommentRepository) repository).search(
                criteria.getKeyword(), criteria.getArticleId(), criteria.getState(), pageable);
    }

    private List<String> humanizeList(String item) {
        return List.of(item);
    }

    private void sendEmailToReplyTarget(Comment comment, String to) {
        List<String> tos = humanizeList(to);
        boolean isComment = comment.getArticleId() == null;
        String text = isComment ? "评论" : "留言";
        String content = buildEmailContent(comment);
        emailService.sendEmail("你在 itsuki.cn 有一条" + text + "回复", content, tos);
    }

    private void sendEmailToAdmin(Comment comment) {
        List<String> tos = humanizeList(adminEmail);
        boolean isComment = comment.getArticleId() == null;
        String text = isComment ? "评论" : "留言";
        String content = buildEmailContent(comment);
        emailService.sendEmail("滴滴, 博客添加了一条" + text, content, tos);
    }

    private String buildEmailContent(Comment comment) {
        boolean isComment = comment.getArticleId() == null;
        String text = isComment ? "评论" : "留言";
        String path = isComment ? urlUtil.getArticleUrl(comment.getArticleId()) : urlUtil.getGuestBookUrl();
        return "<p>昵称: " + comment.getNickname() + "</p>" + "<p>" + text + "内容: "
                + comment.getContent() + "</p>" + "<a href=\"" + path + "" + "\">[点击查看详情]</a>";
    }

    /**
     * 更新文章评论数
     */
    private void updateArticleCommentCount(Long articleId) {
        Article article = ensureArticleExist(articleId);
        int count = count(articleId);
        article.setCommenting(count);
        articleService.update(articleId, article);
    }

    /**
     * 获取当前文章的评论数(待审核、已发布)
     */
    public Integer count(Long articleId) {
        return ((CommentRepository) repository).countComments(articleId);
    }

    public SearchResponse<Comment> comments(CommentSearchRequest input) {
        return search(input);
    }

    public Comment comment(Long id) {
        return get(id);
    }

    public Comment updateComment(Long id, UpdateCommentInput input) {
        adminService.ensureAdminOperate();

        Comment comment = get(id);
        Integer oldState = comment.getState();
        Integer newState = input.getState();
        BeanUtil.copyProperties(input, comment);

        Comment update = super.update(id, comment);

        if (newState != null && !newState.equals(oldState)) {
            updateArticleCommentCount(comment.getArticleId());
        }

        return update;
    }

    public int deleteComment(Long id) {
        Comment comment = get(id);
        adminService.ensureAdminOperate();
        if (comment.getState() == CommentState.Auditing || comment.getState() == CommentState.Published) {
            throw new IllegalArgumentException("当前评论不能进行操作");
        }
        repository.deleteById(id);
        return 1;
    }

    public int updateCommentState(Long id, Integer state) {
        adminService.ensureAdminOperate();
        // 是否为垃圾评论
        boolean isSpam = state == CommentState.Spam;
        Comment comment = get(id);
        String ip = comment.getIp();
        String email = comment.getEmail();

        // 如果是垃圾评论，则加入黑名单，以及 submitSpam
        // 如果不是垃圾评论，则移出黑名单，以及 submitHam
        if (isSpam) {
            blackListService.add(ip, email);
            akismetService.submitSpam(comment);
        } else {
            blackListService.delete(ip, email);
            akismetService.submitHam(comment);
        }

        ((CommentRepository) repository).updateState(id, state);

        // 更新文章评论数
        updateArticleCommentCount(comment.getArticleId());

        return 1;
    }

    public Comment createComment(CommentCreateRequest input, DataFetchingEnvironment environment) {
        if (input.getNickname().length() >= 10) {
            throw new GraphQLException("昵称太长了, 最长: 10, 当前长度:" + input.getNickname().length());
        }

        Comment comment = new Comment();
        BeanUtil.copyProperties(input, comment);

        Comment parentComment = ensureReplyCommentReadPermission(comment);
        setCommentIp(comment, environment);
        ensureIsInBlackList(comment);
        // 检查是否为垃圾评论
        akismetService.checkComment(comment, false);

        setCommentArticle(comment);
        setCommentLocation(comment);
        comment.setId(null);

        if (isProd) {
            if (parentComment != null) {
                sendEmailToReplyTarget(comment, parentComment.getEmail());
            }
            sendEmailToAdmin(comment);
        }

        return repository.save(comment);
    }

    private void setCommentArticle(Comment comment) {
        Article article = ensureArticleExist(comment.getArticleId());
        comment.setArticleTitle(article.getTitle());
        comment.setArticleDescription(article.getDescription());
        // 更新文章评论数
        article.setCommenting(article.getCommenting() + 1);
        articleService.update(article.getId(), article);
    }

    private void setCommentLocation(Comment comment) {
        JSONObject address = requestUtil.findLocationByIp(comment.getIp());
        if (address != null) {
            comment.setCity((String) address.get("city"));
            comment.setProvince((String) address.get("province"));
        }
    }

    public Comment adminComment(AdminCommentInput input, DataFetchingEnvironment environment) {
        Comment comment = new Comment();
        BeanUtil.copyProperties(input, comment);

        Comment parentComment = ensureReplyCommentReadPermission(comment);
        akismetService.checkComment(comment, true);
        comment.setState(CommentState.Published);
        setCommentAdmin(comment);
        setCommentArticle(comment);
        setCommentIp(comment, environment);
        setCommentLocation(comment);

        if (parentComment != null && isProd) {
            sendEmailToReplyTarget(comment, parentComment.getEmail());
        }

        return repository.save(comment);
    }

    public int likeComment(Long id) {
        Comment comment = get(id);
        ensureCommentAllowOperate(comment);
        comment.setLiking(comment.getLiking() + 1);
        repository.save(comment);
        return comment.getLiking();
    }

    private void setCommentAdmin(Comment comment) {
        Admin admin = adminService.ensureAdminOperate();

        comment.setEmail(adminEmail);
        comment.setNickname(admin.getNickname());
        comment.setWebsite(webUrl);
    }

    private void setCommentIp(Comment comment, DataFetchingEnvironment environment) {
        GraphQLServletContext context = environment.getContext();
        HttpServletRequest request = context.getHttpServletRequest();
        comment.setIp(requestUtil.getRequestIp(request));
    }

    private Article ensureArticleExist(Long articleId) {
        return articleService.get(articleId);
    }

    private void ensureIsInBlackList(Comment entity) {
        String ip = entity.getIp();
        String email = entity.getEmail();
        String content = entity.getContent();
        BlackList blackList = blackListService.blacklist();

        if (blackList.getIp().contains(ip) || blackList.getEmail().contains(email) || blackList.getKeyword().contains(content)) {
            throw new IllegalArgumentException("ip | 邮箱 | 内容 -> 不合法");
        }
    }

    private Comment ensureReplyCommentReadPermission(Comment comment) {
        Long parentId = comment.getParentId();
        if (parentId != null && parentId != 0 && parentId != -1) {
            Comment parentComment = get(parentId);
            ensureCommentAllowOperate(parentComment);
            comment.setParentNickName(parentComment.getNickname());
            return parentComment;
        }
        return null;
    }

    private void ensureCommentAllowOperate(Comment comment) {
        if (comment.getState() == CommentState.Spam || comment.getState() == CommentState.Deleted) {
            throw new IllegalArgumentException("当前评论不能进行操作");
        }
    }
}
