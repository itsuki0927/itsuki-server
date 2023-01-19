package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 评论更新 请求类
 *
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
     * 内容
     */
    @NotBlank
    private String content;

    /**
     * 扩展
     */
    private String expand;

    /**
     * 状态
     */
    @NotNull
    private Integer state;
}
