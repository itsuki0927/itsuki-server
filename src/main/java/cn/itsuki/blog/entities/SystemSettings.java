package cn.itsuki.blog.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 系统设置 实体
 *
 * @author: itsuki
 * @create: 2021-09-28 08:22
 **/
@Entity(name = "system_settings")
@Getter
@Setter
@ToString(callSuper = true)
public class SystemSettings extends IdentifiableEntity {

    /**
     * 喜欢
     */
    @NotNull
    private Integer liking;

    /**
     * 站点标题
     */
    @NotBlank
    private String title;

    /**
     * 副标题
     */
    @NotBlank
    private String subtitle;

    /**
     * 邮箱
     */
    @NotBlank
    @Email
    private String email;

    /**
     * 描述
     */
    @NotBlank
    private String description;

    /**
     * 关键词
     */
    @NotBlank
    private String keywords;

    /**
     * 站长地址
     */
    @NotBlank
    private String domain;

    /**
     * 备案号
     */
    @NotBlank
    private String record;
}

