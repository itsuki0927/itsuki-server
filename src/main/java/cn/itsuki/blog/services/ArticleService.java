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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
    private CategoryService categoryService;
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

    public Page<Article> getHotArticles() {
        Sort sort = Sort.by(Sort.Direction.DESC, "reading");
        Pageable pageable = new OffsetLimitPageRequest(0, 8, sort);
        Article probe = new Article();
        probe.setPublish(PublishState.Published);
        return repository.findAll(Example.of(probe), pageable);
    }

    @Override
    protected Page<Article> searchWithPageable(ArticleSearchRequest criteria, Pageable pageable) {
        criteria = Optional.ofNullable(criteria).orElse(new ArticleSearchRequest());

        Long tagId = criteria.getTagId();
        Long categoryId = criteria.getCategoryId();

        Boolean isHot = Optional.ofNullable(criteria.getHot()).orElse(false);
        if (isHot) {
            return getHotArticles();
        }

        if (tagId == null && criteria.getTagPath() != null) {
            tagId = tagService.getTagByNameOrPath(criteria.getTagPath()).getId();
        }

        if (categoryId == null && criteria.getCategoryPath() != null) {
            categoryId = categoryService.getCategoryByNameOrPath(criteria.getCategoryPath()).getId();
        }

        return ((ArticleRepository) repository).search(criteria.getName(), criteria.getPublish(), criteria.getOrigin(),
                criteria.getOpen(), tagId, categoryId, criteria.getBanner(), pageable);
    }

    public Integer count(ArticleSearchRequest criteria) {
        Long tagId = criteria.getTagId();
        Long categoryId = criteria.getCategoryId();
        if (tagId == null && criteria.getTagPath() != null) {
            tagId = tagService.getTagByNameOrPath(criteria.getTagPath()).getId();
        }
        if (categoryId == null && criteria.getCategoryPath() != null) {
            categoryId = categoryService.getCategoryByNameOrPath(criteria.getCategoryPath()).getId();
        }
        return ((ArticleRepository) repository).count(criteria.getName(), criteria.getPublish(), criteria.getOrigin(),
                criteria.getOpen(), tagId, categoryId, criteria.getBanner());
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

    public Article articleDetail(long id) {
        adminService.ensureAdminOperate();
        return get(id);
    }

    public ArticleDetailResponse article(long id) {
        Article article = get(id);

        ArticleDetailResponse response = new ArticleDetailResponse();
        BeanUtil.copyProperties(article, response);

        response.setPassword(null);
        response.setPrevArticle(((ArticleRepository) repository).prev(id));
        response.setNextArticle(((ArticleRepository) repository).next(id));

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

        Category newCategory = categoryService.get(request.getCategoryId());
        // 更新category count
        categoryService.syncCategoryCount(newCategory);
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
        // 删除当前文章的tag
        deleteTag(id);
        // 添加新的tag
        saveAllTags(entity.getTagIds(), id);

        // 更新文章
        BeanUtil.copyProperties(entity, article);
        Article update = super.update(id, article);

        // 更新新的category、tag count
        categoryService.syncAllCategoryCount();
        updateTagCount(entity.getTagIds());
        // 更新旧的tag count
        updateTagCount(oldTagIds);

        seoService.update(urlUtil.getArticleUrl(id));

        return update;
    }

    public int deleteArticle(Long articleId) {
        adminService.ensureAdminOperate();

        Article oldArticle = get(articleId);
        List<Long> oldTagIds = getArticleTagIds(articleId);
        deleteTag(articleId);
        commentService.deleteArticleComments(articleId);

        updateTagCount(oldTagIds);

        Category category = categoryService.get(oldArticle.getCategoryId());
        categoryService.syncCategoryCount(category);

        seoService.delete(urlUtil.getArticleUrl(articleId));

        return super.delete(articleId);
    }

    public int updateArticleState(List<Long> ids, Integer publish) {
        adminService.ensureAdminOperate();

        int state = ((ArticleRepository) repository).batchUpdateState(publish, ids);

        ids.forEach(id -> {
            Article article = get(id);
            List<Long> tagIds = getArticleTagIds(id);
            Category category = categoryService.get(article.getCategoryId());
            categoryService.syncCategoryCount(category);
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

        repository.save(article);
        return article.getReading();
    }

    public int likeArticle(Long id) {
        Article article = get(id);
        ensureArticleAllowOperate(article);
        article.setLiking(article.getLiking() + 1);

        repository.save(article);
        return article.getLiking();
    }

    public int syncArticleCommentCount(List<Long> articleIds) {
        articleIds.forEach(articleId -> commentService.updateArticleCommentCount(articleId));
        return 1;
    }
}