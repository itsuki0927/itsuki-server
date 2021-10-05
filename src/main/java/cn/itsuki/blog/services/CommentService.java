package cn.itsuki.blog.services;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.itsuki.blog.constants.CommentState;
import cn.itsuki.blog.entities.Article;
import cn.itsuki.blog.entities.Comment;
import cn.itsuki.blog.entities.SystemConfig;
import cn.itsuki.blog.entities.requests.*;
import cn.itsuki.blog.repositories.ArticleRepository;
import cn.itsuki.blog.repositories.CommentRepository;
import cn.itsuki.blog.utils.RequestUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * @author: itsuki
 * @create: 2021-10-03 16:34
 **/
@Service
public class CommentService extends BaseService<Comment, CommentSearchRequest> {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private RequestUtil requestUtil;
    @Autowired
    private AkismetService akismetService;
    @Value("${dev.ip}")
    private String devIP;
    @Value("${mode.isDev}")
    private boolean isDev;

    CommentService() {
        super("id", new String[]{"id"});
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
        SystemConfig systemConfig = systemConfigService.get(1);
        String ipBlackList = systemConfig.getIpBlackList();
        String emailBlackList = systemConfig.getEmailBlackList();
        String keywordBlackList = systemConfig.getKeywordBlackList();

        // if (ipBlackList != null && ipBlackList.contains(ip)) {
        //     throw new IllegalArgumentException("评论失败");
        // } else if (emailBlackList != null && emailBlackList.contains(email)) {
        //     throw new IllegalArgumentException("评论失败");
        // } else if (keywordBlackList != null && keywordBlackList.contains(content)) {
        //     throw new IllegalArgumentException("评论失败");
        // }
        if (ipBlackList.contains(ip) || emailBlackList.contains(email) || keywordBlackList.contains(content)) {
            throw new IllegalArgumentException("评论失败");
        }
    }

    public Comment create(CommentCreateRequest entity) {
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

        // 如果有父类id, 设置父类名称
        Long parentId = comment.getParentId();
        if (parentId != null && parentId != 0) {
            Comment parent = ensureExist(repository, parentId, "comment");
            // 如果当前评论和回复的评论文章不是同一篇
            if (!parent.getArticleId().equals(comment.getArticleId())) {
                throw new IllegalArgumentException("The replied article is not the same, comment article id:"
                        + comment.getArticleId() + " ---> parent comment article id: " + parent.getArticleId());
            }
            comment.setParentNickName(parent.getNickname());
        }

        ensureIsInBlackList(comment);

        JSONObject object = requestUtil.findLocationByIp(devIP);
        comment.setCity((String) object.get("city"));
        comment.setProvince((String) object.get("province"));

        return super.create(comment);
    }

    public Comment update(long id, CommentUpdateRequest request) {
        Comment comment = ensureExist(repository, id, "comment");

        BeanUtil.copyProperties(request, comment, CopyOptions.create().ignoreNullValue());

        return super.update(id, comment);
    }


    @Override
    protected Page<Comment> searchWithPageable(CommentSearchRequest criteria, Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Integer patch(CommentPatchRequest request) {
        return ((CommentRepository) repository).batchPatchStatus(request.getIds(), request.getStatus());
    }

    public List<Comment> get(Long articleId) {
        Comment probe = new Comment();
        probe.setArticleId(articleId);
        // 只有发布了的评论才可以观看
        probe.setStatus(CommentState.Published);
        return repository.findAll(Example.of(probe));
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
}
