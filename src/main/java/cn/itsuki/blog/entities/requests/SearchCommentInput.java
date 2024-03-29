package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 评论搜索 请求类
 *
 * @author: itsuki
 * @create: 2021-10-03 21:37
 **/
@Getter
@Setter
@ToString
public class SearchCommentInput extends BaseSearchRequest {
    /**
     * 关键字
     */
    private String keyword;

    /**
     * 文章id
     */
    private Long blogId;

    /**
     * 文章path
     */
    private String blogPath;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 最近的留言
     */
    private Boolean recent;
}
