package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: itsuki
 * @create: 2021-09-21 18:17
 **/
@Getter
@Setter
public class TagSearchRequest extends BaseSearchRequest {
    /**
     * 标签名称搜索
     */
    private String name;
}
