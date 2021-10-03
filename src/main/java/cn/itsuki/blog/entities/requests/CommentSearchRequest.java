package cn.itsuki.blog.entities.requests;

/**
 * 评论搜索请求类
 *
 * @author: itsuki
 * @create: 2021-10-03 21:37
 **/
public class CommentSearchRequest extends BaseSearchRequest {
    /**
     * 名称
     */
    private String name;

    /**
     * 文章id
     */
    private Integer articleId;

    /**
     * 状态
     */
    private Integer status;
}
