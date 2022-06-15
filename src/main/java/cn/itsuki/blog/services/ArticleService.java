package cn.itsuki.blog.services;

import cn.hutool.core.bean.BeanUtil;
import cn.itsuki.blog.constants.PublishState;
import cn.itsuki.blog.entities.*;
import cn.itsuki.blog.entities.requests.*;
import cn.itsuki.blog.entities.ArticleSummary;
import cn.itsuki.blog.entities.responses.ArticleDetailResponse;
import cn.itsuki.blog.entities.responses.ArticleSummaryResponse;
import cn.itsuki.blog.entities.responses.SearchResponse;
import cn.itsuki.blog.repositories.*;
import cn.itsuki.blog.utils.UrlUtil;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章 服务
 *
 * @author: itsuki
 * @create: 2021-09-20 22:19
 **/
@Service
public class ArticleService extends BaseService<Article, ArticleSearchRequest> implements GraphQLQueryResolver, GraphQLMutationResolver {
    @Autowired
    private AdminService adminService;
    @Autowired
    private ArticleTagRepository articleTagRepository;
    @Autowired
    private TagService tagService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private SeoService seoService;
    @Autowired
    private UrlUtil urlUtil;

    /**
     * 创建一个service实例
     */
    public ArticleService() {
        super("createAt", "commenting", "liking", "reading", "createdAt");
    }

    private void saveAllTags(List<Long> tagIds, Long articleId) {
        if (tagIds != null) {
            List<ArticleTag> tagList = tagIds.stream().map(tagId -> {
                ArticleTag tag = new ArticleTag();
                tag.setTagId(tagId);
                tag.setArticleId(articleId);
                return tag;
            }).collect(Collectors.toList());
            articleTagRepository.saveAll(tagList);
        }
    }

    private void deleteTag(long articleId) {
        articleTagRepository.deleteAllByArticleIdEquals(articleId);
    }

    private void ensureArticleAllowOperate(Article article) {
        if (article.getPublish() != PublishState.Published) {
            throw new IllegalArgumentException("文章还没发布");
        }
    }

    public Page<Article> recentArticles() {
        Sort sort = Sort.by(Sort.Direction.DESC, "createAt");
        Pageable pageable = new OffsetLimitPageRequest(0, 6, sort);
        Article probe = new Article();
        probe.setPublish(PublishState.Published);
        return repository.findAll(Example.of(probe), pageable);
    }

    public Page<Article> hotArticles() {
        Sort sort = Sort.by(Sort.Direction.DESC, "reading");
        Pageable pageable = new OffsetLimitPageRequest(0, 3, sort);
        Article probe = new Article();
        probe.setPublish(PublishState.Published);
        return repository.findAll(Example.of(probe), pageable);
    }

    private Tag getTag(String name) {
        Tag tag = tagService.getTagByNameOrPath(name);
        if (tag == null) {
            throw new EntityNotFoundException("Tag 不存在");
        }
        return tag;
    }

    private Long getSearchTagId(ArticleSearchRequest request) {
        Long tagId = request.getTagId();
        if (tagId == null && request.getTagPath() != null) {
            tagId = getTag(request.getTagPath()).getId();
        }
        return tagId;
    }

    @Override
    protected Page<Article> searchWithPageable(ArticleSearchRequest criteria, Pageable pageable) {
        criteria = Optional.ofNullable(criteria).orElse(new ArticleSearchRequest());

        if (criteria.getRecent() != null && criteria.getRecent()) {
            return recentArticles();
        }

        if (criteria.getHot() != null && criteria.getHot()) {
            return hotArticles();
        }

        Long tagId = getSearchTagId(criteria);

        return ((ArticleRepository) repository).search(criteria.getName(), criteria.getPublish(), tagId, criteria.getBanner(), pageable);
    }

    public Integer count(ArticleSearchRequest criteria) {
        Long tagId = criteria.getTagId();
        if (tagId == null && criteria.getTagPath() != null) {
            tagId = tagService.getTagByNameOrPath(criteria.getTagPath()).getId();
        }
        return ((ArticleRepository) repository).count(criteria.getName(), criteria.getPublish(), tagId, criteria.getBanner());
    }

    public ArticleSummaryResponse getSummary() {
        List<ArticleSummary> summaries = ((ArticleRepository) repository).summary();

        ArticleSummaryResponse response = new ArticleSummaryResponse();
        summaries.forEach(summary -> {
            if (summary.getPublish() == PublishState.Draft) {
                summary.setTitle("草稿");
                summary.setState("warning");
                response.setDraft(summary);
            } else if (summary.getPublish() == PublishState.Published) {
                summary.setTitle("已发布");
                summary.setState("success");
                response.setPublished(summary);
            } else if (summary.getPublish() == PublishState.Recycle) {
                summary.setTitle("回收站");
                summary.setState("error");
                response.setRecycle(summary);
            }
        });
        ArticleSummary total = new ArticleSummary(0, summaries.stream().map(ArticleSummary::getValue).reduce(0L, Long::sum));
        total.setState("processing");
        total.setTitle("全部");
        response.setTotal(total);
        return response;
    }

    public ArticleDetailResponse article(long id, DataFetchingEnvironment environment) {
        DataFetchingFieldSelectionSet selectionSet = environment.getSelectionSet();
        Article article = get(id);

        ArticleDetailResponse response = new ArticleDetailResponse();
        BeanUtil.copyProperties(article, response);

        if (selectionSet.contains("prevArticle")) {
            response.setPrevArticle(((ArticleRepository) repository).prev(id));
        }
        if (selectionSet.contains("nextArticle")) {
            response.setNextArticle(((ArticleRepository) repository).next(id));
        }

        return response;
    }

    public SearchResponse<Article> articles(ArticleSearchRequest criteria) {
        return search(criteria);
    }

    public List<Long> getArticleTagIds(Long articleId) {
        return articleTagRepository.findAllByArticleIdEquals(articleId).stream().map(ArticleTag::getTagId).collect(Collectors.toList());
    }

    public Article createArticle(ArticleCreateRequest request) {
        adminService.ensureAdminOperate();

        Article entity = new Article();
        BeanUtils.copyProperties(request, entity);
        Article article = super.create(entity);

        saveAllTags(request.getTagIds(), article.getId());

        // 更新tag count
        updateTagCount(request.getTagIds());

        if (request.getPublish() == PublishState.Published) {
            seoService.push(urlUtil.getArticleUrl(article.getId()));
        }

        return article;
    }

    /**
     * 更新文章标签count
     *
     * @param tagIds 标签id数组
     */
    private void updateTagCount(List<Long> tagIds) {
        tagIds.forEach(tagId -> {
            Tag tag = tagService.get(tagId);
            tagService.syncTagCount(tag);
        });
    }

    public Article updateArticle(Long id, ArticleCreateRequest entity) {
        adminService.ensureAdminOperate();

        Article article = get(id);

        List<Long> oldTagIds = getArticleTagIds(id);
        List<Long> newTagIds = entity.getTagIds();
        // 删除当前文章的tag
        deleteTag(id);
        // 添加新的tag
        saveAllTags(newTagIds, id);

        // 更新文章
        BeanUtil.copyProperties(entity, article);
        Article update = super.update(id, article);

        updateTagCount(newTagIds);
        // 更新旧的tag count
        updateTagCount(oldTagIds);

        seoService.update(urlUtil.getArticleUrl(id));

        return update;
    }

    public int deleteArticle(Long articleId) {
        adminService.ensureAdminOperate();

        List<Long> oldTagIds = getArticleTagIds(articleId);
        deleteTag(articleId);
        commentService.deleteArticleComments(articleId);

        updateTagCount(oldTagIds);

        seoService.delete(urlUtil.getArticleUrl(articleId));

        return super.delete(articleId);
    }

    public int updateArticleState(List<Long> ids, Integer publish) {
        adminService.ensureAdminOperate();

        int state = ((ArticleRepository) repository).batchUpdateState(publish, ids);

        ids.forEach(id -> {
            List<Long> tagIds = getArticleTagIds(id);
            updateTagCount(tagIds);
        });

        return state;
    }

    public int updateArticleBanner(List<Long> ids, Integer banner) {
        adminService.ensureAdminOperate();

        List<Article> articles = repository
                .findAllById(ids).stream()
                .filter(v -> v.getPublish() == PublishState.Published)
                .peek(article -> article.setBanner(banner)).collect(Collectors.toList());

        repository.saveAll(articles);
        return articles.size();
    }

    public int readArticle(Long id) {
        Article article = get(id);
        ensureArticleAllowOperate(article);
        article.setReading(article.getReading() + 1);

        repository.saveAndFlush(article);
        return article.getReading();
    }

    public int likeArticle(Long id) {
        Article article = get(id);
        ensureArticleAllowOperate(article);
        article.setLiking(article.getLiking() + 1);

        repository.saveAndFlush(article);
        return article.getLiking();
    }

    public int syncArticleCommentCount(List<Long> articleIds) {
        articleIds.forEach(articleId -> commentService.updateArticleCommentCount(articleId));
        return 1;
    }
}