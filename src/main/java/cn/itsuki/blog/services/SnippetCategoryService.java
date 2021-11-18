package cn.itsuki.blog.services;

import cn.itsuki.blog.entities.Category;
import cn.itsuki.blog.entities.SnippetCategory;
import cn.itsuki.blog.entities.requests.SnippetCategorySearchRequest;
import cn.itsuki.blog.repositories.SnippetCategoryRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.Optional;

/**
 * 片段分类 服务
 *
 * @author: itsuki
 * @create: 2021-09-21 18:18
 **/
@Service
public class SnippetCategoryService extends BaseService<SnippetCategory, SnippetCategorySearchRequest> {
    /**
     * 创建一个service实例
     */
    public SnippetCategoryService() {
        super("id", new String[]{"id", "sort"});
    }

    @Override
    public SnippetCategory create(SnippetCategory entity) {
        ensureCategoryExist(entity);
        return super.create(entity);
    }

    @Override
    protected Page<SnippetCategory> searchWithPageable(SnippetCategorySearchRequest criteria, Pageable pageable) {
        if (criteria.getParentId() != null) {
            SnippetCategory probe = new SnippetCategory();
            probe.setParentId(criteria.getParentId());
            return repository.findAll(Example.of(probe), pageable);
        }
        return repository.findAll(pageable);
    }

    public void ensureCategoryExist(SnippetCategory entity) {
        SnippetCategory probe = new SnippetCategory();
        probe.setName(entity.getName());
        Optional<SnippetCategory> optionalEntity = repository.findOne(Example.of(probe));
        if (optionalEntity.isPresent()) {
            throw new EntityExistsException("category exist with name:" + entity.getName());
        }
    }

    public Category getCategoryByNameOrPath(String name) {
        Category probe = new Category();
        probe.setName(name);
        Category category = (((SnippetCategoryRepository) repository).findCategoryByNameEqualsOrPathEquals(name, name));
        if (category == null) {
            throw new IllegalArgumentException("category 不存在");
        }
        return category;
    }
}
