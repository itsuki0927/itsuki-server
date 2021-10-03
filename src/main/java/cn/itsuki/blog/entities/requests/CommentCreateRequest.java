package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

/**
 * 评论创建类
 *
 * @author: itsuki
 * @create: 2021-10-03 20:28
 **/
@Getter
@Setter
@ToString
public class CommentCreateRequest {
    /**
     * 昵称
     */
    @NotBlank
    private String nickname;

    /**
     * 邮箱
     */
    @NotBlank
    private String email;

    /**
     * 网址
     */
    @NotBlank
    private String website;

    /**
     * 浏览器agent
     */
    private String agent;

    /**
     * 内容
     */
    @NotBlank
    private String content;

    /**
     * 扩展
     */
    private String expand;

    /**
     * 父id
     */
    @Column(name = "parent_id")
    private Integer parentId;

    /**
     * 文章id
     */
    @NotBlank
    @Column(name = "article_id")
    private Long articleId;
}
