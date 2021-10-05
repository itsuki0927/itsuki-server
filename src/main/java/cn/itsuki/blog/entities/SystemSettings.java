package cn.itsuki.blog.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;

/**
 * 基本设置
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
    private int liking;

    /**
     * 站点标题
     */
    private String title;

    /**
     * 副标题
     */
    private String subtitle;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 描述
     */
    private String description;

    /**
     * 关键词
     */
    private String keywords;

    /**
     * 站长地址
     */
    private String domain;

    /**
     * 备案号
     */
    private String record;
}

