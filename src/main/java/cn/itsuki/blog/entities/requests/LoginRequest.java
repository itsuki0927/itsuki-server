package cn.itsuki.blog.entities.requests;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 登陆 请求类
 *
 * @author: itsuki
 * @create: 2021-09-15 20:02
 **/
@Getter
@Setter
@ToString(exclude = {"password"})
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank
    @Size(min = 1, max = 255)
    private String username;

    @NotBlank
    @Size(min = 1, max = 255)
    private String password;
}
