package cn.itsuki.blog.services;

import cn.hutool.core.bean.BeanUtil;
import cn.itsuki.blog.entities.Snippet;
import cn.itsuki.blog.entities.requests.SnippetCreateRequest;
import cn.itsuki.blog.entities.requests.SnippetPatchRequest;
import cn.itsuki.blog.entities.requests.SnippetSearchRequest;
import cn.itsuki.blog.repositories.SnippetRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 片段 服务
 *
 * @author: itsuki
 * @create: 2021-11-05 13:58
 **/
@Service
public class SnippetService extends BaseService<Snippet, SnippetSearchRequest> {
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
    protected Page<Snippet> searchWithPageable(SnippetSearchRequest criteria, Pageable pageable) {
        Page<Snippet> snippets = ((SnippetRepository) repository).search(criteria.getKeyword(), criteria.getStatus(), criteria.getRanks(), pageable);
        return snippets.map(snippet -> {
            Snippet result = new Snippet();
            BeanUtils.copyProperties(snippet, result);
            result.setCode("code placeholder");
            result.setExample("example placeholder");
            result.setSkill("skill placeholder");
            return result;
        });
    }

    public Integer patch(SnippetPatchRequest request) {
        List<Snippet> snippetList = request.getIds().stream().map(id -> {
            Snippet snippet = ensureExist(repository, id, "snippet");
            snippet.setStatus(request.getStatus());
            return snippet;
        }).collect(Collectors.toList());
        repository.saveAllAndFlush(snippetList);
        return snippetList.size();
    }
}
