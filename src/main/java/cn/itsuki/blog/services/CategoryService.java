package cn.itsuki.blog.services;

import cn.itsuki.blog.entities.Category;
import cn.itsuki.blog.entities.requests.BaseSearchRequest;
import cn.itsuki.blog.repositories.CategoryRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.Optional;

/**
 * 分类 服务
 *
 * @author: itsuki
 * @create: 2021-09-21 18:18
 **/
@Service
public class CategoryService extends BaseService<Category, BaseSearchRequest> {
    /**
     * 创建一个service实例
     */
    public CategoryService() {
        super("id", new String[]{"id", "sort"});
    }

    @Override
    public Category create(Category entity) {
        ensureCategoryExist(entity);
        return super.create(entity);
    }

    @Override
    protected Page<Category> searchWithPageable(BaseSearchRequest criteria, Pageable pageable) {
        return repository.findAll(pageable);
    }

    public void ensureCategoryExist(Category entity) {
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
}
