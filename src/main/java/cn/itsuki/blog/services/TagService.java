package cn.itsuki.blog.services;

import cn.itsuki.blog.entities.Tag;
import cn.itsuki.blog.entities.requests.TagSearchRequest;
import cn.itsuki.blog.repositories.TagRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.Optional;

/**
 * 标签 服务
 *
 * @author: itsuki
 * @create: 2021-09-21 18:18
 **/
@Service
public class TagService extends BaseService<Tag, TagSearchRequest> {
    /**
     * 创建一个service实例
     */
    public TagService() {
        super("id", new String[]{"id", "sort"});
    }

    public void ensureTagExist(Tag entity) {
        Tag probe = new Tag();
        probe.setName(entity.getName());
        Optional<Tag> optionalEntity = repository.findOne(Example.of(probe));
        if (optionalEntity.isPresent()) {
            throw new EntityExistsException("tag exist with name:" + entity.getName());
        }
    }

    @Override
    public Tag create(Tag entity) {
        ensureTagExist(entity);
        return super.create(entity);
    }

    @Override
    protected Page<Tag> searchWithPageable(TagSearchRequest criteria, Pageable pageable) {
        String name = criteria.getName();
        if (name != null) {
            return ((TagRepository) repository).findByNameContainingOrPathContaining(name, name, pageable);
        }
        return repository.findAll(pageable);
    }
}
