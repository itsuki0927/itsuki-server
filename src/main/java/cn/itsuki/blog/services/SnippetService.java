package cn.itsuki.blog.services;

import cn.hutool.core.bean.BeanUtil;
import cn.itsuki.blog.entities.Snippet;
import cn.itsuki.blog.entities.requests.BaseSearchRequest;
import cn.itsuki.blog.entities.requests.SnippetCreateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author: itsuki
 * @create: 2021-11-05 13:58
 **/
@Service
public class SnippetService extends BaseService<Snippet, BaseSearchRequest> {
    /**
     * 创建一个service实例
     */
    public SnippetService() {
        super("id", new String[]{"id"});
    }

    public Snippet create(SnippetCreateRequest entity) {
        Snippet snippet = new Snippet();
        BeanUtil.copyProperties(entity, snippet);
        return super.create(snippet);
    }

    public Snippet update(Long id, SnippetCreateRequest entity) {
        Snippet snippet = ensureExist(repository, id, "snippet");
        BeanUtil.copyProperties(entity, snippet);
        return super.update(id, snippet);
    }

    @Override
    protected Page<Snippet> searchWithPageable(BaseSearchRequest criteria, Pageable pageable) {
        return repository.findAll(pageable);
    }
}
