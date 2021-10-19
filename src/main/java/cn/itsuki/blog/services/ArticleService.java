package cn.itsuki.blog.services;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.itsuki.blog.constants.PublishState;
import cn.itsuki.blog.entities.*;
import cn.itsuki.blog.entities.requests.*;
import cn.itsuki.blog.repositories.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
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

    /**
     * 创建一个service实例
     */
    public ArticleService() {
        super("liking", new String[]{"commenting", "liking", "reading"});
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
        if (!meta.equals("reading") && !meta.equals("liking") && !meta.equals("banner")) {
            throw new IllegalArgumentException("meta can only be one of reading and liking");
        }

        Article article = ensureExist(repository, id, "article");

        switch (meta) {
            case "reading":
                article.setReading(article.getReading() + 1);
                break;
            case "liking":
                article.setLiking(article.getLiking() + 1);
                break;
            case "banner":
                if (adminService.getCurrentAdmin() == null) {
                    throw new IllegalArgumentException("没有权限");
                }
                if (article.getPublish() != PublishState.Published) {
                    throw new IllegalArgumentException("文章还没发布");
                }
                article.setBanner(request.getValue());
                break;
        }
        repository.saveAndFlush(article);
        return 1;
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

    @Override
    protected Page<Article> searchWithPageable(ArticleSearchRequest criteria, Pageable pageable) {
        Long tagId = null;
        Long categoryId = null;
        if (criteria.getTag() != null) {
            tagId = tagService.getTagByName(criteria.getTag()).getId();
        }
        if (criteria.getCategory() != null) {
            categoryId = categoryService.getCategoryByNameOrPath(criteria.getCategory()).getId();
        }
        return ((ArticleRepository) repository).search(criteria.getName(), criteria.getPublish(), criteria.getOrigin(),
                criteria.getOpen(), tagId, categoryId, criteria.getBanner(), pageable);
    }
}
