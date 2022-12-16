package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 评论创建 请求类
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
    @Email
    private String email;

    /**
     * uid
     */
    @NotBlank
    private String uid;

    /**
     * 浏览器agent
     */
    @NotBlank
    private String agent;

    /**
     * 内容
     */
    @NotBlank
    private String content;

    /**
     * 登录方式
     */
    private String provider;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 扩展
     */
    private String expand;

    /**
     * 父id
     */
    private Long parentId;

    /**
     * 文章id
     */
    @NotNull
    private Long blogId;
}
