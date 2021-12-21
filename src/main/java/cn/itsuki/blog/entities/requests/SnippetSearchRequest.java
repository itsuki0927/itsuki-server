package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 片段搜索 请求类
 *
 * @author: itsuki
 * @create: 2021-11-05 20:41
 **/
@Getter
@Setter
@ToString
public class SnippetSearchRequest extends BaseSearchRequest {
    private String keyword;
    private Integer status;
    private Integer ranks;
    private String categoryPath;
    private String categoryName;
    private Long categoryId;
    private Integer pinned;
}
