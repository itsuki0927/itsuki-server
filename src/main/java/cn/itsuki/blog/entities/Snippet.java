package cn.itsuki.blog.entities;

import cn.itsuki.blog.security.SecurityUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * 片段 实体
 *
 * @author: itsuki
 * @create: 2021-11-05 13:47
 **/
@Entity(name = "snippet")
@Getter
@Setter
@ToString(callSuper = true)
public class Snippet extends IdentifiableEntity {
    /**
     * 名称
     */
    @NotBlank
    private String name;

    /**
     * 描述
     */
    @NotBlank
    private String description;

    /**
     * 用什么技巧实现的
     */
    @NotBlank
    private String skill;

    /**
     * code
     */
    @NotBlank
    private String code;

    /**
     * 示例
     */
    @NotBlank
    private String example;

    /**
     * 等级: 0 -> 简单, 1 -> 中等, 2 -> 困难
     */
    @NotNull
    private Integer ranks;

    /**
     * 状态: 0
     */
    @NotNull
    private Integer status;

    /**
     * 作者
     */
    @NotBlank
    private String author;

    /**
     * 作者网址
     */
    @NotBlank
    private String website;

    /**
     * 头像
     */
    @NotBlank
    private String avatar;

    /**
     * 邮箱
     */
    @NotBlank
    private String email;

    @ManyToMany
    @JoinTable(
            name = "snippet_category_relation",
            joinColumns = @JoinColumn(name = "snippet_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<SnippetCategory> categories;


    @Override
    protected void onCreateAction() {
        Admin currentAdmin = SecurityUtils.getCurrentAdmin();
        System.out.println(currentAdmin.toString());
        setAuthor(currentAdmin.getNickname());
        setAvatar(currentAdmin.getAvatar());
        setEmail("2309899048@qq.com");
        setWebsite("https://itsuki.cn");
    }
}
