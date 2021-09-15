package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 基本搜索请求体
 *
 * @author: itsuki
 * @create: 2021-09-14 19:41
 **/
@Getter
@Setter
@ToString
public class BaseSearchRequest {
    private Integer offset;
    private Integer limit;
    private String sortBy;
    private String sortOrder;
}
