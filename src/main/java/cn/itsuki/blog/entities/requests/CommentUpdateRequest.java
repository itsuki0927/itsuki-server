package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author: itsuki
 * @create: 2021-10-04 05:57
 **/
@Getter
@Setter
public class CommentUpdateRequest {
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
     * 内容
     */
    @NotBlank
    private String content;

    /**
     * 被喜欢数
     */
    @NotBlank
    private Integer liking;

    /**
     * 扩展
     */
    private String expand;

    /**
     * 状态
     */
    private Integer status;
}
