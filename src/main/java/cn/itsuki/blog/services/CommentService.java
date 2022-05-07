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
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 评论 服务
 *
 * @author: itsuki
 * @create: 2021-10-03 16:34
 **/
@Service
public class CommentService extends BaseService<Comment, CommentSearchRequest> implements GraphQLQueryResolver {

    @Autowired
    private BlackIpService ipService;
    @Autowired
    private BlackEmailService emailService;
    @Autowired
    private BlackKeywordService keywordService;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private RequestUtil requestUtil;
    @Autowired
    private AkismetService akismetService;
    @Value("${mode.isDev}")
    private boolean isDev;
    private String devIP = "220.169.96.10";
    private List<Integer> states;

    CommentService() {
        super("id", new String[]{"id"});
        states = new ArrayList<>();
        states.add(CommentState.Auditing);
        states.add(CommentState.Published);
    }

    private Article ensureArticleExist(Long articleId) {
        if (articleId == null) {
            throw new IllegalArgumentException("article id must be not null");
        }
        Optional<Article> optionalArticle = articleRepository.findById(articleId);
        if (!optionalArticle.isPresent()) {
            throw new IllegalArgumentException("article id: " + articleId + " not exist");
        }
        return optionalArticle.get();
    }

    private void ensureIsInBlackList(Comment entity) {
        String ip = entity.getIp();
        String email = entity.getEmail();
        String content = entity.getContent();

        if (ipService.isInBlackList(ip) || emailService.isInBlackList(email) || keywordService.isInBlackList(content)) {
            throw new IllegalArgumentException("ip | 邮箱 | 内容 -> 不合法");
        }
    }

    public Comment create(CommentCreateRequest entity) {
        if (entity.getNickname().length() >= 10) {
            throw new IllegalArgumentException("昵称太长了, 最长: 10, 当前长度:" + entity.getNickname().length());
        }
        Comment comment = new Comment();
        BeanUtil.copyProperties(entity, comment);

        Article article = ensureArticleExist(comment.getArticleId());
        comment.setArticleTitle(article.getTitle());
        comment.setArticleDescription(article.getDescription());

        if (isDev) {
            comment.setIp(devIP);
        } else {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            comment.setIp(requestUtil.getRequestIp(request));
        }

        // 确保回复的是同一篇文章
        // 检查是否为垃圾评论
        ensureReplySameArticle(comment);
        ensureIsInBlackList(comment);
        akismetService.checkComment(comment);

        // 更新文章评论数
        article.setCommenting(article.getCommenting() + 1);
        articleRepository.save(article);

        JSONObject object = requestUtil.findLocationByIp(devIP);
        comment.setCity((String) object.get("city"));
        comment.setProvince((String) object.get("province"));

        return super.create(comment);
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
                criteria.getKeyword(), criteria.getArticleId(), criteria.getStatus(), pageable);
    }

    public Integer patch(CommentPatchRequest request) {
        // 是否为垃圾评论
        boolean isSpam = request.getStatus() == CommentState.Spam;

        List<Comment> comments = ((CommentRepository) repository).findCommentsByIdIn(request.getIds());
        List<String> ipList = comments.stream().map(Comment::getIp).collect(Collectors.toList());
        List<String> emailList = comments.stream().map(Comment::getEmail).collect(Collectors.toList());

        // 如果是垃圾评论，则加入黑名单，以及 submitSpam
        // 如果不是垃圾评论，则移出黑名单，以及 submitHam
        if (isSpam) {
            ipService.save(ipList);
            emailService.save(emailList);
            comments.forEach(comment -> akismetService.submitSpam(comment));
        } else {
            ipService.remove(ipList);
            emailService.remove(emailList);
            comments.forEach(comment -> akismetService.submitHam(comment));
        }

        updateArticleCommentCount(comments.stream().map(Comment::getArticleId).collect(Collectors.toList()));

        return ((CommentRepository) repository).batchPatchStatus(request.getIds(), request.getStatus());
    }

    /**
     * 更新文章评论数
     *
     * @param articleIdList 文章id列表
     */
    private void updateArticleCommentCount(List<Long> articleIdList) {
        articleIdList.forEach(articleId -> {
            Article article = articleRepository.getById(articleId);
            int count = ((CommentRepository) repository).countCommentsByArticleIdEqualsAndStatusIsIn(articleId, states);
            article.setCommenting(count);
            articleRepository.save(article);
        });
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
        return ((CommentRepository) repository).countCommentsByArticleIdEqualsAndStatusIsIn(articleId, states);
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
}
