package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;

/**
 * 片段搜索 请求类
 *
 * @author: itsuki
 * @create: 2021-11-18 20:01
 **/
@Getter
@Setter
public class SnippetCategorySearchRequest extends BaseSearchRequest {
    private Long parentId;
}
