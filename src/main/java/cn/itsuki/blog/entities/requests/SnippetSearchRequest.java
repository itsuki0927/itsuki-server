package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: itsuki
 * @create: 2021-11-05 20:41
 **/
@Getter
@Setter
public class SnippetSearchRequest extends BaseSearchRequest {
    private String keywords;
    private Integer status;
}
