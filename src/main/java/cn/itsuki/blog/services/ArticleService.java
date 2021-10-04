package cn.itsuki.blog.services;

import cn.itsuki.blog.entities.Article;
import cn.itsuki.blog.entities.ArticleCategory;
import cn.itsuki.blog.entities.ArticleTag;
import cn.itsuki.blog.entities.requests.ArticleCreateRequest;
import cn.itsuki.blog.entities.requests.ArticlePatchRequest;
import cn.itsuki.blog.entities.requests.ArticleSearchRequest;
import cn.itsuki.blog.repositories.ArticleCategoryRepository;
import cn.itsuki.blog.repositories.ArticleRepository;
import cn.itsuki.blog.repositories.ArticleTagRepository;
import cn.itsuki.blog.utils.CloneUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: itsuki
 * @create: 2021-09-20 22:19
 **/
@Service
public class ArticleService extends BaseService<Article, ArticleSearchRequest> {
    @Autowired
    AdminService adminService;
    @Autowired
    ArticleTagRepository tagRepository;
    @Autowired
    ArticleCategoryRepository categoryRepository;

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
            tagRepository.saveAll(tagList);
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
        tagRepository.deleteAllByArticleIdEquals(articleId);
    }

    private void deleteCategory(long articleId) {
        categoryRepository.deleteAllByArticleIdEquals(articleId);
    }

    public Article update(long id, ArticleCreateRequest entity) {
        Article article = ensureExist(repository, id, "article");

        // 删除当前文章的tag、category
        deleteTag(id);
        deleteCategory(id);

        // 添加tag、category
        saveAllTags(entity.getTagIds(), id);
        saveAllCategories(entity.getCategoryIds(), id);

        CloneUtil.copyPropertiesExcludeNullValue(entity, article);

        return super.update(id, article);
    }

    @Override
    public int delete(long id) {
        int result = super.delete(id);

        deleteCategory(id);
        deleteTag(id);

        return result;
    }

    public int patch(ArticlePatchRequest request) {
        return ((ArticleRepository) repository).batchUpdateState(request.getState(), request.getIds());
    }

    private void saveAllCategories(List<Long> categoryIds, Long articleId) {
        if (categoryIds != null) {
            List<ArticleCategory> categoryList = categoryIds.stream().map(categoryId -> {
                ArticleCategory category = new ArticleCategory();
                category.setCategoryId(categoryId);
                category.setArticleId(articleId);
                return category;
            }).collect(Collectors.toList());
            categoryRepository.saveAll(categoryList);
        }
    }

    @Override
    protected Page<Article> searchWithPageable(ArticleSearchRequest criteria, Pageable pageable) {
        return repository.findAll(pageable);
    }
}
