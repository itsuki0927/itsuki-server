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
import graphql.kickstart.servlet.context.GraphQLServletContext;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static cn.itsuki.blog.constants.CommonState.GUESTBOOK;

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
    private BlogService blogService;
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
    @Value("${cors.webUrl}")
    private String webUrl;

    CommentService() {
        super("id", "id");
    }

    @Override
    protected Page<Comment> searchWithPageable(CommentSearchRequest criteria, Pageable pageable) {
        if (criteria.getRecent() != null && criteria.getRecent()) {
            return recentComments();
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable newPageable = new OffsetLimitPageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return ((CommentRepository) repository).search(
                criteria.getKeyword(), criteria.getBlogId(), criteria.getBlogPath(), criteria.getState(), newPageable);
    }

    private List<String> humanizeList(String item) {
        return List.of(item);
    }

    private void sendEmailToReplyTarget(Comment comment, String to) {
        List<String> tos = humanizeList(to);
        boolean isComment = isBlogComment(comment.getBlogId());
        String text = isComment ? "评论" : "留言";
        String content = buildEmailContent(comment);
        emailService.sendEmail("你在 itsuki.cn 有一条" + text + "回复", content, tos);
    }

    private void sendEmailToAdmin(Comment comment) {
        List<String> tos = humanizeList(adminEmail);
        boolean isComment = isBlogComment(comment.getBlogId());
        String text = isComment ? "评论" : "留言";
        String content = buildEmailContent(comment);
        emailService.sendEmail("滴滴, 博客添加了一条" + text, content, tos);
    }

    private String buildEmailContent(Comment comment) {
        boolean isComment = isBlogComment(comment.getBlogId());
        String text = isComment ? "评论" : "留言";
        String path = isComment ? urlUtil.getBlogUrl(comment.getBlogPath()) : urlUtil.getGuestBookUrl();
        return "<p>昵称: " + comment.getNickname() + "</p>" + "<p>" + text + "内容: "
                + comment.getContent() + "</p>" + "<a href=\"" + path + "" + "\">[点击查看详情]</a>";
    }

    /**
     * 更新文章评论数
     */
    public void updateBlogCommentCount(Long blogId) {
        if (isBlogComment(blogId)) {
            Blog blog = ensureBlogExist(blogId);
            int count = count(blogId);
            blog.setCommenting(count);
            blogService.update(blogId, blog);
        }
    }

    /**
     * 获取当前文章的评论数(待审核、已发布)
     */
    public Integer count(Long blogId) {
        return ((CommentRepository) repository).countComments(blogId);
    }

    public SearchResponse<Comment> comments(CommentSearchRequest input) {
        return search(input);
    }

    public Page<Comment> recentComments() {
        Sort sort = Sort.by(Sort.Direction.DESC, "createAt");
        Pageable newPageable = new OffsetLimitPageRequest(0, 3, sort);
        return ((CommentRepository) repository).recent(adminEmail, newPageable);
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
            updateBlogCommentCount(comment.getBlogId());
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

    public void deleteBlogComments(long blogId) {
        ((CommentRepository) repository).deleteCommentsByBlogIdEquals(blogId);
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
            blackListService.addIP(ip);
            blackListService.addEmail(email);
            akismetService.submitSpam(comment);
        } else {
            blackListService.deleteIP(ip);
            blackListService.deleteEmail(email);
            akismetService.submitHam(comment);
        }

        ((CommentRepository) repository).updateState(id, state);

        // 更新文章评论数
        updateBlogCommentCount(comment.getBlogId());

        return 1;
    }

    public Comment createComment(CommentCreateRequest input, DataFetchingEnvironment environment) {
        Comment comment = new Comment();
        BeanUtil.copyProperties(input, comment);
        boolean isAdminComment = input.getEmail().equals(adminEmail);

        Comment parentComment = ensureReplyCommentReadPermission(comment);
        setCommentIp(comment, environment);
        ensureIsInBlackList(comment);
        // 检查是否为垃圾评论
        akismetService.checkComment(comment, isAdminComment);

        setCommentBlog(comment);
        setCommentLocation(comment);
        comment.setId(null);

        Comment save = repository.save(comment);

        if (isProd()) {
            if (parentComment != null) {
                sendEmailToReplyTarget(comment, parentComment.getEmail());
            }
            // 如果是管理评论, 不需要发邮箱.
            if (!isAdminComment) {
                sendEmailToAdmin(comment);
            }
        }

        return save;
    }

    private boolean isBlogComment(Long blogId) {
        return blogId != GUESTBOOK;
    }

    private void setCommentBlog(Comment comment) {
        if (isBlogComment(comment.getBlogId())) {
            Blog blog = ensureBlogExist(comment.getBlogId());
            comment.setBlogPath(blog.getPath());
            comment.setBlogTitle(blog.getTitle());
            comment.setBlogDescription(blog.getDescription());
            // 更新文章评论数
            blog.setCommenting(blog.getCommenting() + 1);
            blogService.update(blog.getId(), blog);
        }
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
        setCommentBlog(comment);
        setCommentIp(comment, environment);
        setCommentLocation(comment);

        Comment save = repository.save(comment);

        if (parentComment != null && isProd()) {
            sendEmailToReplyTarget(comment, parentComment.getEmail());
        }

        return save;
    }

    public String likeComment(Long id, String emoji) {
        Comment comment = get(id);
        ensureCommentAllowOperate(comment);
        comment.setEmoji(emoji);
        repository.save(comment);
        return emoji;
    }

    private void setCommentAdmin(Comment comment) {
        Admin admin = adminService.ensureAdminOperate();

        comment.setProvider("github");
        comment.setEmail(adminEmail);
        comment.setAvatar(admin.getAvatar());
        comment.setNickname(admin.getNickname());
    }

    private void setCommentIp(Comment comment, DataFetchingEnvironment environment) {
        GraphQLServletContext context = environment.getContext();
        HttpServletRequest request = context.getHttpServletRequest();
        comment.setIp(requestUtil.getRequestIp(request));
    }

    private Blog ensureBlogExist(Long blogId) {
        return blogService.get(blogId);
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
            throw new IllegalArgumentException("当前评论处于垃圾评论、已删除状态，该操作需要待审核、已发布状态");
        }
    }
}
