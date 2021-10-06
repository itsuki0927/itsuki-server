package cn.itsuki.blog.entities.responses;

import cn.itsuki.blog.entities.IdentifiableEntity;
import cn.itsuki.blog.entities.requests.BaseSearchRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 搜索 响应类
 *
 * @author: itsuki
 * @create: 2021-09-14 19:45
 **/
@Getter
@Setter
@ToString(callSuper = true)
public class SearchResponse<T extends IdentifiableEntity> {
    private long total;
    private List<T> data;
    private BaseSearchRequest filter;
}
