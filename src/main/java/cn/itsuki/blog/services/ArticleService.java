package cn.itsuki.blog.services;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
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

import java.time.LocalDateTime;
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
    private CommentRepository commentRepository;
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

    private void deleteComment(long articleId) {
        commentRepository.deleteCommentsByArticleIdEquals(articleId);
    }

    private void ensureArticleAllowOperate(Article article) {
        if (article.getPublish() != PublishState.Published) {
            throw new IllegalArgumentException("文章还没发布");
        }
    }

    public Page<Article> getHotArticles() {
        Sort sort = Sort.by(Sort.Direction.DESC, "reading");
        Pageable pageable = new OffsetLimitPageRequest(0, 8, sort);

        return ((ArticleRepository) repository).queryArticlesByPublish(PublishState.Published, pageable);
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

    public TreeMap<String, TreeMap<String, List<ArticleArchive>>> getArchive() {
        List<ArticleArchive> archives = ((ArticleRepository) repository).archive();
        TreeMap<String, TreeMap<String, List<ArticleArchive>>> response = new TreeMap<>(Comparator.reverseOrder());
        archives.forEach(archive -> {
            String year = LocalDateTimeUtil.format(archive.getCreateAt(), "yyyy");
            String date = LocalDateTimeUtil.format(archive.getCreateAt(), "MM月");

            TreeMap<String, List<ArticleArchive>> map = response.getOrDefault(year, new TreeMap<>(Comparator.reverseOrder()));
            List<ArticleArchive> articleArchiveList = map.getOrDefault(date, new ArrayList<>());
            articleArchiveList.add(archive);
            map.put(date, articleArchiveList);
            response.put(year, map);
        });
        return response;
    }

    public List<ArticleId> getPaths() {
        return ((ArticleRepository) repository).ids();
    }

    public ArticleDetailResponse article(long id) {
        Article article = get(id);
        ArticleDetailResponse response = new ArticleDetailResponse();
        BeanUtil.copyProperties(article, response);

        response.setPrevArticle(((ArticleRepository) repository).prev(id));
        response.setNextArticle(((ArticleRepository) repository).next(id));

        return response;
    }

    public int patchRead(Long id) {
        Article article = get(id);
        if (article.getPublish() != PublishState.Published) {
            throw new RuntimeException("文章未发布");
        }
        article.setReading(article.getReading() + 1);

        repository.saveAndFlush(article);
        return article.getReading();
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

        // 更新category count
        updateCategoryCount(request.getCategoryId());
        // 更新tag count
        updateTagCount(request.getTagIds());

        if (request.getPublish() == PublishState.Published) {
            seoService.push(urlUtil.getArticleUrl(article.getId()));
        }

        return article;
    }

    /**
     * 更新文章分类count, 因为文章分类比较少, 并且关系是一对一, 所以直接全部更新count
     */
    private void updateAllCategoryCount() {
        categoryService.categories().forEach(category -> {
            updateCategoryCount(category.getId());
        });
    }

    private void updateCategoryCount(Long categoryId) {
        Category category = categoryService.get(categoryId);
        Article article = new Article();
        article.setCategoryId(categoryId);
        article.setPublish(PublishState.Published);
        int count = (int) repository.count(Example.of(article));
        category.setCount(count);
        categoryService.update(category.getId(), category);
    }

    /**
     * 更新文章标签count
     *
     * @param tagIds 标签id数组
     */
    private void updateTagCount(List<Long> tagIds) {
        tagIds.forEach(tagId -> {
            Tag tag = tagService.get(tagId);
            // 找到当前tag的记录
            List<ArticleTag> articleTags = articleTagRepository.findAllByTagIdEquals(tagId);
            List<Long> ids = articleTags.stream().map(ArticleTag::getArticleId).collect(Collectors.toList());
            // 获取当前tag的已发布文章数
            int count = ((ArticleRepository) repository).countArticlesByIdInAndPublishEquals(ids, PublishState.Published);
            tag.setCount(count);
            tagService.update(tagId, tag);
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
        updateAllCategoryCount();
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
        deleteComment(articleId);

        updateTagCount(oldTagIds);
        updateCategoryCount(oldArticle.getCategoryId());

        seoService.delete(urlUtil.getArticleUrl(articleId));

        return super.delete(articleId);
    }

    public int updateArticleState(List<Long> ids, Integer publish) {
        adminService.ensureAdminOperate();

        int state = ((ArticleRepository) repository).batchUpdateState(publish, ids);

        ids.forEach(id -> {
            Article article = get(id);
            List<Long> tagIds = getArticleTagIds(id);
            updateCategoryCount(article.getCategoryId());
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

    public int incrementArticleReading(Long id) {
        Article article = get(id);
        ensureArticleAllowOperate(article);
        article.setReading(article.getReading() + 1);

        repository.save(article);
        return article.getReading();
    }

    public int incrementArticleLiking(Long id) {
        Article article = get(id);
        ensureArticleAllowOperate(article);
        article.setLiking(article.getLiking() + 1);

        repository.save(article);
        return article.getLiking();
    }
}