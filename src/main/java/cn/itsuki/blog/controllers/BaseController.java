package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.IdentifiableEntity;
import cn.itsuki.blog.entities.requests.BaseSearchRequest;
import cn.itsuki.blog.entities.responses.SearchResponse;
import cn.itsuki.blog.entities.responses.WrapperResponse;
import cn.itsuki.blog.services.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 基本 控制器
 *
 * @author: itsuki
 * @create: 2021-09-14 22:49
 **/
public class BaseController<T extends IdentifiableEntity, S extends BaseSearchRequest> {
    @Autowired
    protected BaseService<T, S> service;

    @PostMapping
    public T create(@Valid @RequestBody T entity) {
        return service.create(entity);
    }

    @GetMapping
    public WrapperResponse<SearchResponse<T>> search(@Valid @ModelAttribute S criteria) {
        return WrapperResponse.build(service.search(criteria));
    }

    @GetMapping("/{id}")
    public WrapperResponse<T> get(@PathVariable("id") Long id) {
        return WrapperResponse.build(service.get(id));
    }

    @PutMapping("/{id}")
    public WrapperResponse<T> update(@PathVariable("id") Long id, @Valid @RequestBody T entity) {
        return WrapperResponse.build(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    public WrapperResponse<Integer> delete(@PathVariable("id") Long id) {
        return WrapperResponse.build(service.delete(id));
    }
}
