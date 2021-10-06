package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * 管理员更新 请求类
 *
 * @author: itsuki
 * @create: 2021-09-28 15:49
 **/
@Getter
@Setter
@ToString
public class AdminSaveRequest {
    /**
     * 头像
     */
    @NotBlank
    private String avatar;

    /**
     * 昵称
     */
    @NotBlank
    private String nickname;

    /**
     * 描述
     */
    @NotBlank
    private String description;

    /**
     * 密码
     */
    private String password;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 确认密码
     */
    private String confirm;
}
