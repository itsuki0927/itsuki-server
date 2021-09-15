package cn.itsuki.blog.services;

import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author: itsuki
 * @create: 2021-09-14 20:23
 **/
public class OffsetLimitPageRequest extends AbstractPageRequest {
    private static final long serialVersionUID = 4146362751484532549L;
    private final int offset;
    private final Sort sort;

    public OffsetLimitPageRequest(Integer offset, Integer limit, Sort sort) {
        super(offset / limit, limit);
        this.offset = offset;
        this.sort = sort;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return null;
    }

    @Override
    public Pageable previous() {
        return null;
    }

    @Override
    public Pageable first() {
        return null;
    }

    @Override
    public Pageable withPage(int i) {
        return null;
    }
}
