package cn.itsuki.blog.entities.requests;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 文章搜索 请求类
 *
 * @author: itsuki
 * @create: 2021-09-20 22:17
 **/
@Getter
@Setter
@ToString(callSuper = true)
public class BlogSearchRequest extends BaseSearchRequest {
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
     * 轮播状态
     */
    private Integer banner;

    /**
     * 公开状态
     */
    private Integer open;

    /**
     * 标签id
     */
    private Long tagId;

    /**
     * 标签路径
     */
    private String tagPath;

    /**
     * 排行榜
     */
    private Boolean hot;

    /**
     * 最近
     */
    private Boolean recent;
}
