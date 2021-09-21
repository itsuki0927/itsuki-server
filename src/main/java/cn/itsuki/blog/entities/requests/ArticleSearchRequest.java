package cn.itsuki.blog.entities.requests;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 文章搜索请求
 *
 * @author: itsuki
 * @create: 2021-09-20 22:17
 **/
@Getter
@Setter
@ToString(callSuper = true)
public class ArticleSearchRequest extends BaseSearchRequest {
    /**
     * 名称
     */
    private String name;
    /**
     * 状态
     */
    private Integer status;
}
