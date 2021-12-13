package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 管理员更新密码 请求类
 *
 * @author: itsuki
 * @create: 2021-12-12 08:04
 **/
@Getter
@Setter
public class AdminUpdatePasswordRequest {
    /**
     * 密码
     */
    @NotBlank
    private String password;

    /**
     * 新密码
     */
    @NotBlank
    private String newPassword;

    /**
     * 确认密码
     */
    @NotBlank
    private String confirm;
}
