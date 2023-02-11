package cn.itsuki.blog.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * 管理员 实体
 *
 * @author: itsuki
 * @create: 2021-09-14 20:42
 **/
@Entity(name = "admin")
@Getter
@Setter
@ToString(callSuper = true)
public class Admin extends IdentifiableEntity {
    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 权限
     */
    @NonNull
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * 用户名
     */
    @NotBlank
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 描述
     */
    private String description;

}
