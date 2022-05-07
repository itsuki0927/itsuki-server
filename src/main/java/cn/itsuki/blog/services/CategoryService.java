package cn.itsuki.blog.services;

import cn.hutool.core.bean.BeanUtil;
import cn.itsuki.blog.entities.Category;
import cn.itsuki.blog.entities.requests.BaseSearchRequest;
import cn.itsuki.blog.entities.requests.CategoryActionInput;
import cn.itsuki.blog.repositories.CategoryRepository;
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

    /**
     * 创建一个service实例
     */
    public CategoryService() {
        super("id", new String[]{"id", "sort"});
    }

    /**
     * 确保是管理员操作
     */
    private void ensureAdminOperate() {
        if (adminService.getCurrentAdmin() == null) {
            throw new IllegalArgumentException("没有权限");
        }
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
        ensureAdminOperate();
        return super.create(probe);
    }

    public Category updateCategory(Long categoryId, CategoryActionInput input) {
        Category oldCategory = ensureExist(repository, categoryId, "Entity");
        Category entity = new Category();
        BeanUtil.copyProperties(input, entity);

        ensureAdminOperate();

        entity.setId(categoryId);
        entity.setCount(oldCategory.getCount());
        entity.setCreateAt(oldCategory.getCreateAt());

        ensureCategoryExist(entity);
        validateEntity(entity);
        return repository.saveAndFlush(entity);
    }

    public int deleteCategory(Long categoryId) {
        ensureAdminOperate();
        return super.delete(categoryId);
    }
}
