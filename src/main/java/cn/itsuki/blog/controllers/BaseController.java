package cn.itsuki.blog.controllers;

import cn.itsuki.blog.entities.IdentifiableEntity;
import cn.itsuki.blog.entities.requests.BaseSearchRequest;
import cn.itsuki.blog.entities.responses.SearchResponse;
import cn.itsuki.blog.services.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * controller 基类
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
    public SearchResponse<T> search(@Valid @ModelAttribute S criteria) {
        return service.search(criteria);
    }

    @GetMapping("/{id}")
    public T get(@PathVariable("id") long id) {
        return service.get(id);
    }

    @PutMapping
    public T update(@PathVariable("id") long id, @Valid @RequestBody T entity) {
        return service.update(id, entity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long id) {
        service.delete(id);
    }
}
