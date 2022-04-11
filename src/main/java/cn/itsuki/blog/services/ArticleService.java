package cn.itsuki.blog.services;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import cn.itsuki.blog.constants.CommentState;
import cn.itsuki.blog.constants.PublishState;
import cn.itsuki.blog.entities.*;
import cn.itsuki.blog.entities.requests.*;
import cn.itsuki.blog.entities.ArticleSummary;
import cn.itsuki.blog.entities.responses.ArticleDetailResponse;
import cn.itsuki.blog.entities.responses.ArticleSummaryResponse;
import cn.itsuki.blog.repositories.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ArticleService extends BaseService<Article, ArticleSearchRequest> {
    @Autowired
    private AdminService adminService;
    @Autowired
    private ArticleTagRepository articleTagRepository;
    @Autowired
    private ArticleCategoryRepository articleCategoryRepository;
    @Autowired
    private TagService tagService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CommentRepository commentRepository;
    private List<Integer> states;

    /**
     * 创建一个service实例
     */
    public ArticleService() {
        super("createAt", new String[]{"commenting", "liking", "reading", "createdAt"});
        states = new ArrayList<>();
        states.add(CommentState.Auditing);
        states.add(CommentState.Published);
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

    public Article create(ArticleCreateRequest request) {
        Article entity = new Article();
        BeanUtils.copyProperties(request, entity);
        Article article = super.create(entity);

        saveAllTags(request.getTagIds(), article.getId());
        saveAllCategories(request.getCategoryIds(), article.getId());

        return article;
    }

    private void deleteTag(long articleId) {
        articleTagRepository.deleteAllByArticleIdEquals(articleId);
    }

    private void deleteCategory(long articleId) {
        articleCategoryRepository.deleteAllByArticleIdEquals(articleId);
    }

    private void deleteComment(long articleId) {
        commentRepository.deleteCommentsByArticleIdEquals(articleId);
    }

    public Article update(long id, ArticleCreateRequest entity) {
        Article article = ensureExist(repository, id, "article");

        // 删除当前文章的tag、category
        deleteTag(id);
        deleteCategory(id);

        // 添加tag、category
        saveAllTags(entity.getTagIds(), id);
        saveAllCategories(entity.getCategoryIds(), id);

        BeanUtil.copyProperties(entity, article, CopyOptions.create().ignoreNullValue());

        return super.update(id, article);
    }

    @Override
    public int delete(long id) {
        int result = super.delete(id);

        deleteCategory(id);
        deleteTag(id);
        deleteComment(id);

        return result;
    }

    public int patch(ArticlePatchRequest request) {
        return ((ArticleRepository) repository).batchUpdateState(request.getState(), request.getIds());
    }

    public int patchMeta(Long id, ArticleMetaPatchRequest request) {
        String meta = request.getMeta();
        ensureArticleMetaExist(meta);

        Article article = ensureExist(repository, id, "article");

        switch (meta) {
            case "reading":
                article.setReading(article.getReading() + 1);
                break;
            case "liking":
                article.setLiking(article.getLiking() + 1);
                break;
            case "banner":
                ensureAdminOperate(article);
                article.setBanner(request.getValue());
                break;
            case "pinned":
                ensureAdminOperate(article);
                article.setPinned(request.getValue());
                break;
        }

        repository.saveAndFlush(article);
        return 1;
    }

    public int patchLike(Long id) {
        Article article = ensureExist(repository, id, "article");
        if (article.getPublish() != PublishState.Published) {
            throw new RuntimeException("文章未发布");
        }

        article.setLiking(article.getLiking() + 1);

        repository.saveAndFlush(article);
        return article.getLiking();
    }

    /**
     * 确保是管理员操作
     */
    private void ensureAdminOperate(Article article) {
        if (adminService.getCurrentAdmin() == null) {
            throw new IllegalArgumentException("没有权限");
        }
        if (article.getPublish() != PublishState.Published) {
            throw new IllegalArgumentException("文章还没发布");
        }
    }

    /**
     * 确保更新的meta是允许更新的
     */
    private void ensureArticleMetaExist(String meta) {
        if (!meta.equals("reading") && !meta.equals("liking") && !meta.equals("banner") && !meta.equals("pinned")) {
            throw new IllegalArgumentException("meta can only be one of reading、liking、banner and pinned");
        }
    }

    private void saveAllCategories(List<Long> categoryIds, Long articleId) {
        if (categoryIds != null) {
            List<ArticleCategory> categoryList = categoryIds.stream().map(categoryId -> {
                ArticleCategory category = new ArticleCategory();
                category.setCategoryId(categoryId);
                category.setArticleId(articleId);
                return category;
            }).collect(Collectors.toList());
            articleCategoryRepository.saveAll(categoryList);
        }
    }

    public Page<Article> getHotArticles() {
        Sort sort =
                Sort.by(Sort.Direction.DESC, "reading");
        Pageable pageable = new OffsetLimitPageRequest(0, 8, sort);

        return normalizeArticles(((ArticleRepository) repository).queryArticlesByPublish(PublishState.Published, pageable));
    }

    @Override
    protected Page<Article> searchWithPageable(ArticleSearchRequest criteria, Pageable pageable) {
        Long tagId = null;
        Long categoryId = null;
        Integer publish = Optional.ofNullable(criteria.getPublish()).orElse(PublishState.Published);

        if (criteria.getHot() != null && criteria.getHot() == 1) {
            return getHotArticles();
        }

        if (criteria.getTag() != null) {
            tagId = tagService.getTagByNameOrPath(criteria.getTag()).getId();
        }

        if (criteria.getCategory() != null) {
            categoryId = categoryService.getCategoryByNameOrPath(criteria.getCategory()).getId();
        }

        Page<Article> articles = ((ArticleRepository) repository).search(criteria.getName(), criteria.getPublish(), criteria.getOrigin(),
                criteria.getOpen(), tagId, categoryId, criteria.getBanner(), criteria.getPinned(), pageable);
        return normalizeArticles(articles);
    }

    private Page<Article> normalizeArticles(Page<Article> articles) {
        return articles.map(article -> {
            Article result = new Article();
            BeanUtils.copyProperties(article, result);
            result.setContent("content placeholder");
            result.setComments(result.getComments().stream().map(item -> {
                Comment comment = new Comment();
                BeanUtils.copyProperties(item, comment);
                comment.setContent("comment placeholder");
                return comment;
            }).collect(Collectors.toSet()));
            return result;
        });
    }

    public Integer count(ArticleSearchRequest criteria) {
        Long tagId = null;
        Long categoryId = null;
        if (criteria.getTag() != null) {
            tagId = tagService.getTagByNameOrPath(criteria.getTag()).getId();
        }
        if (criteria.getCategory() != null) {
            categoryId = categoryService.getCategoryByNameOrPath(criteria.getCategory()).getId();
        }
        return ((ArticleRepository) repository).count(criteria.getName(), criteria.getPublish(), criteria.getOrigin(),
                criteria.getOpen(), tagId, categoryId, criteria.getBanner());
    }

    public List<Comment> getComments(Long articleId) {
        return commentRepository.findCommentsByArticleIdAndStatusIsIn(articleId, states);
    }

    public ArticleSummaryResponse getSummary() {
        List<ArticleSummary> summaries = ((ArticleRepository) repository).summary();

        ArticleSummaryResponse response = new ArticleSummaryResponse();
        summaries.forEach(summary -> {
            if (summary.getPublish() == PublishState.Draft) {
                summary.setTitle("草稿");
                summary.setStatus("warning");
                response.setDraft(summary);
            } else if (summary.getPublish() == PublishState.Published) {
                summary.setTitle("已发布");
                summary.setStatus("success");
                response.setPublished(summary);
            } else if (summary.getPublish() == PublishState.Recycle) {
                summary.setTitle("回收站");
                summary.setStatus("error");
                response.setRecycle(summary);
            }
        });
        ArticleSummary total = new ArticleSummary(0, summaries.stream().map(ArticleSummary::getValue).reduce(0L, Long::sum));
        total.setStatus("processing");
        total.setTitle("全部");
        response.setTotal(total);
        return response;
    }

    public TreeMap<String, TreeMap<String, List<ArticleArchive>>> getArchive() {
        List<ArticleArchive> archives = ((ArticleRepository) repository).archive();
        TreeMap<String, TreeMap<String, List<ArticleArchive>>> response = new TreeMap<>(Comparator.reverseOrder());
        archives.forEach(archive -> {
            String year = DateUtil.format(archive.getCreateAt(), "yyyy");
            String date = DateUtil.format(archive.getCreateAt(), "MM月");

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

    public ArticleDetailResponse get(long id) {
        Article article = ensureExist(repository, id, "Article");
        ArticleDetailResponse response = new ArticleDetailResponse();
        BeanUtil.copyProperties(article, response);

        response.setPrevArticle(((ArticleRepository) repository).prev(id));
        response.setNextArticle(((ArticleRepository) repository).next(id));

        return response;
    }

    public int patchRead(Long id) {
        Article article = ensureExist(repository, id, "article");
        if (article.getPublish() != PublishState.Published) {
            throw new RuntimeException("文章未发布");
        }
        article.setReading(article.getReading() + 1);

        repository.saveAndFlush(article);
        return article.getReading();
    }

}
