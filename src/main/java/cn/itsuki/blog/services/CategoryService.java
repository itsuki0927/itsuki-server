package cn.itsuki.blog.services;

import cn.itsuki.blog.entities.Category;
import cn.itsuki.blog.entities.requests.BaseSearchRequest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.Optional;

/**
 * 分类service
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
    protected void beforeCreateInitialAction(Category entity) {
        entity.setSort(0);
        entity.setCount(0);
        if (entity.getParentId() == null) {
            entity.setParentId((long) -1);
        }
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
            throw new EntityExistsException("tag exist with name:" + entity.getName());
        }
    }
}
