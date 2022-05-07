package cn.itsuki.blog.services;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.itsuki.blog.constants.CommentState;
import cn.itsuki.blog.entities.*;
import cn.itsuki.blog.entities.requests.*;
import cn.itsuki.blog.entities.responses.SearchResponse;
import cn.itsuki.blog.repositories.*;
import cn.itsuki.blog.utils.RequestUtil;
import com.alibaba.fastjson.JSONObject;
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
import java.util.ArrayList;
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
    private BlackIpService ipService;
    @Autowired
    private BlackEmailService emailService;
    @Autowired
    private BlackKeywordService keywordService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private RequestUtil requestUtil;
    @Autowired
    private AkismetService akismetService;
    @Autowired
    private AdminService adminService;
    @Value("${mode.isDev}")
    private boolean isDev;
    private String devIP = "220.169.96.10";
    private final List<Integer> states;

    CommentService() {
        super("id", "id");
        states = new ArrayList<>();
        states.add(CommentState.Auditing);
        states.add(CommentState.Published);
    }

    /**
     * 确保是管理员操作
     */
    private void ensureAdminOperate() {
        if (adminService.getCurrentAdmin() == null) {
            throw new IllegalArgumentException("没有权限");
        }
    }

    private Article ensureArticleExist(Long articleId) {
        return articleService.get(articleId);
    }

    private void ensureIsInBlackList(Comment entity) {
        String ip = entity.getIp();
        String email = entity.getEmail();
        String content = entity.getContent();

        if (ipService.isInBlackList(ip) || emailService.isInBlackList(email) || keywordService.isInBlackList(content)) {
            throw new IllegalArgumentException("ip | 邮箱 | 内容 -> 不合法");
        }
    }

    private void ensureReplySameArticle(Comment comment) {
        Long parentId = comment.getParentId();
        if (parentId != null && parentId != 0 && parentId != -1) {
            Comment parent = ensureExist(repository, parentId, "comment");
            // 如果当前评论和回复的评论文章不是同一篇
            if (!parent.getArticleId().equals(comment.getArticleId())) {
                throw new IllegalArgumentException("The replied article is not the same, comment article id:"
                        + comment.getArticleId() + " ---> parent comment article id: " + parent.getArticleId());
            }
            comment.setParentNickName(parent.getNickname());
        }
    }

    public Comment update(long id, CommentUpdateRequest request) {
        Comment comment = ensureExist(repository, id, "comment");

        BeanUtil.copyProperties(request, comment, CopyOptions.create().ignoreNullValue());

        return super.update(id, comment);
    }


    @Override
    protected Page<Comment> searchWithPageable(CommentSearchRequest criteria, Pageable pageable) {
        return ((CommentRepository) repository).search(
                criteria.getKeyword(), criteria.getArticleId(), criteria.getState(), pageable);
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

    public int patchMeta(Long id, CommentMetaPatchRequest request) {
        String meta = request.getMeta();
        if (!meta.equals("liking")) {
            throw new IllegalArgumentException("meta can only be one of reading and liking");
        }

        Comment comment = ensureExist(repository, id, "article");

        comment.setLiking(comment.getLiking() + 1);
        repository.saveAndFlush(comment);
        return 1;
    }

    public Integer count(Long articleId) {
        return ((CommentRepository) repository).countCommentsByArticleIdEqualsAndStateIsIn(articleId, states);
    }

    public int patchLike(Long id) {
        Comment comment = ensureExist(repository, id, "article");

        comment.setLiking(comment.getLiking() + 1);
        repository.saveAndFlush(comment);
        return comment.getLiking();
    }

    public SearchResponse<Comment> comments(CommentSearchRequest input) {
        return search(input);
    }

    public int deleteComment(Long id) {
        Comment comment = ensureExist(repository, id, "Comment");
        ensureAdminOperate();
        ensureCanDelete(comment);
        repository.deleteById(id);
        return 1;
    }

    public int updateCommentState(Long id, Integer state) {
        // 是否为垃圾评论
        boolean isSpam = state == CommentState.Spam;
        Comment comment = ensureExist(repository, id, "Comment");
        String ip = comment.getIp();
        String email = comment.getEmail();

        // 如果是垃圾评论，则加入黑名单，以及 submitSpam
        // 如果不是垃圾评论，则移出黑名单，以及 submitHam
        if (isSpam) {
            ipService.save(ip);
            emailService.save(email);
            akismetService.submitSpam(comment);
        } else {
            ipService.remove(ip);
            emailService.remove(email);
            akismetService.submitHam(comment);
        }

        ((CommentRepository) repository).updateState(id, state);

        // 更新文章评论数
        updateArticleCommentCount(comment.getArticleId());

        return 1;
    }

    public Comment createComment(CommentCreateRequest input, DataFetchingEnvironment environment) {
        if (input.getNickname().length() >= 10) {
            throw new IllegalArgumentException("昵称太长了, 最长: 10, 当前长度:" + input.getNickname().length());
        }

        Comment comment = new Comment();
        BeanUtil.copyProperties(input, comment);

        Article article = ensureArticleExist(comment.getArticleId());
        comment.setArticleTitle(article.getTitle());
        comment.setArticleDescription(article.getDescription());

        GraphQLServletContext context = environment.getContext();
        HttpServletRequest request = context.getHttpServletRequest();
        if (isDev) {
            comment.setIp(devIP);
        } else {
            comment.setIp(requestUtil.getRequestIp(request));
        }

        // 确保回复的是同一篇文章
        ensureReplySameArticle(comment);
        // 是否在黑名单中
        ensureIsInBlackList(comment);
        // 检查是否为垃圾评论
        akismetService.checkComment(comment);

        // 更新文章评论数
        article.setCommenting(article.getCommenting() + 1);
        articleService.update(article.getId(), article);

        JSONObject address = requestUtil.findLocationByIp(comment.getIp());
        if (address != null) {
            comment.setCity((String) address.get("city"));
            comment.setProvince((String) address.get("province"));
        }
        comment.setId(null);

        return repository.save(comment);
    }

    private void ensureCanDelete(Comment comment) {
        if (comment.getState() == CommentState.Auditing || comment.getState() == CommentState.Published) {
            throw new IllegalArgumentException("只有在回收站、已删除的评论才能彻底删除");
        }
    }
}
