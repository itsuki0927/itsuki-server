 package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 基础搜索 请求类
 *
 * @author: itsuki
 * @create: 2021-09-14 19:41
 **/
@Getter
@Setter
@ToString
public class BaseSearchRequest {
    private Integer current;
    private Integer pageSize;
    private String sortBy;
    private String sortOrder;
}
