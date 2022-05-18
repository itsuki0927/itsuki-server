package cn.itsuki.blog.services;

import cn.hutool.core.bean.BeanUtil;
import cn.itsuki.blog.constants.PublishState;
import cn.itsuki.blog.entities.Article;
import cn.itsuki.blog.entities.Category;
import cn.itsuki.blog.entities.requests.BaseSearchRequest;
import cn.itsuki.blog.entities.requests.CategoryActionInput;
import cn.itsuki.blog.repositories.ArticleRepository;
import cn.itsuki.blog.repositories.CategoryRepository;
import cn.itsuki.blog.utils.UrlUtil;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.Optional;

/**
 * 分类 服务
 *
 * @author: itsuki
 * @create: 2021-09-21 18:18
 **/
@Service
public class CategoryService extends BaseService<Category, BaseSearchRequest> implements GraphQLQueryResolver, GraphQLMutationResolver {
    @Autowired
    private AdminService adminService;
    @Autowired
    private SeoService seoService;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UrlUtil urlUtil;

    /**
     * 创建一个service实例
     */
    public CategoryService() {
        super("id", new String[]{"id", "sort"});
    }

    @Override
    protected Page<Category> searchWithPageable(BaseSearchRequest criteria, Pageable pageable) {
        return repository.findAll(pageable);
    }

    private void ensureCategoryExist(Category entity) {
        Category probe = new Category();
        probe.setName(entity.getName());
        Optional<Category> optionalEntity = repository.findOne(Example.of(probe));
        if (optionalEntity.isPresent()) {
            throw new EntityExistsException("category exist with name:" + entity.getName());
        }
    }

    public Category getCategoryByNameOrPath(String name) {
        Category probe = new Category();
        probe.setName(name);
        Category category = (((CategoryRepository) repository).findCategoryByNameEqualsOrPathEquals(name, name));
        if (category == null) {
            throw new IllegalArgumentException("category 不存在");
        }
        return category;
    }

    public List<Category> categories() {
        return repository.findAll();
    }

    public Category createCategory(CategoryActionInput entity) {
        Category probe = new Category();
        BeanUtil.copyProperties(entity, probe);
        ensureCategoryExist(probe);
        adminService.ensureAdminOperate();

        seoService.push(urlUtil.getCategoryUrl(probe.getPath()));

        return super.create(probe);
    }

    public Category updateCategory(Long categoryId, CategoryActionInput input) {
        Category oldCategory = ensureExist(repository, categoryId, "Entity");
        Category entity = new Category();
        BeanUtil.copyProperties(input, entity);

        adminService.ensureAdminOperate();

        entity.setId(categoryId);
        entity.setCount(oldCategory.getCount());
        entity.setCreateAt(oldCategory.getCreateAt());

        ensureCategoryExist(entity);
        validateEntity(entity);

        seoService.update(urlUtil.getCategoryUrl(entity.getPath()));

        return repository.saveAndFlush(entity);
    }

    public int deleteCategory(Long categoryId) {
        adminService.ensureAdminOperate();

        Category category = get(categoryId);

        seoService.delete(urlUtil.getCategoryUrl(category.getPath()));

        return super.delete(categoryId);
    }

    /**
     * 更新文章分类count, 因为文章分类比较少, 并且关系是一对一, 所以直接全部更新count
     */
    public void syncAllCategoryCount() {
        categories().forEach(category -> syncCategoryCount(category));
    }

    public void syncCategoryCount(Category category) {
        Article article = new Article();
        article.setCategoryId(category.getId());
        article.setPublish(PublishState.Published);
        int count = (int) articleRepository.count(Example.of(article));
        category.setCount(count);
        update(category.getId(), category);
    }
}
