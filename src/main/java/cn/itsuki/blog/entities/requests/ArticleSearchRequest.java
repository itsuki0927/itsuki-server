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
     * 发布状态
     */
    private Integer publish;

    /**
     * 原创状态
     */
    private Integer origin;

    /**
     * 公开状态
     */
    private Integer open;

    /**
     * 标签
     */
    private Integer tag;

    /**
     * 分类
     */
    private Integer category;
}
