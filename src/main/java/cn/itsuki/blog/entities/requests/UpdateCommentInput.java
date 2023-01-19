package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
public class UpdateCommentInput {
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
     * 内容
     */
    @NotBlank
    private String content;

    /**
     * 扩展
     */
    private String expand;

    /**
     * 置顶
     */
    @NotNull
    private Integer fix;

    /**
     * 状态
     */
    @NotNull
    private Integer state;
}
