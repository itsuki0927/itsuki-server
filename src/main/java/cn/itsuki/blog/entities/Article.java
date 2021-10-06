package cn.itsuki.blog.entities;

import cn.itsuki.blog.constants.*;
import cn.itsuki.blog.security.SecurityUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * 文章 实体
 *
 * @author: itsuki
 * @create: 2021-09-14 20:42
 **/
@Entity(name = "article")
@Getter
@Setter
@ToString(callSuper = true)
public class Article extends IdentifiableEntity {
    /**
     * 标题
     */
    @NotBlank
    private String title;

    /**
     * 描述
     */
    @NotBlank
    private String description;

    /**
     * 关键字
     */
    @NotBlank
    private String keywords;

    /**
     * 内容
     */
    @NotBlank
    private String content;

    /**
     * 封面
     */
    @NotBlank
    private String cover;

    /**
     * 评论数
     */
    @Min(0)
    private Integer commenting;

    /**
     * 作者
     */
    @NotBlank
    private String author;

    /**
     * 喜欢数
     */
    @Min(0)
    private Integer liking;

    /**
     * 观看数
     */
    @Min(0)
    private Integer reading;

    /**
     * 文章密码
     */
    private String password;

    /**
     * 发布状态: 0 -> 草稿, 1 -> 已发布, 2 -> 回收站
     */
    @NonNull
    private Integer publish;

    /**
     * 文章来源: 0 -> 原创, 1 -> 转载, 2 -> 混合
     */
    @NonNull
    private Integer origin;

    /**
     * 公开类型: 0 -> 需要密码, 1 -> 公开, 2 -> 私密
     */
    @NonNull
    private Integer open;

    @ManyToMany
    @JoinTable(
            name = "article_tag",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

    @ManyToMany
    @JoinTable(
            name = "article_category",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Set<Comment> comments;

    @Override
    protected void onCreateAction() {
        setAuthor(SecurityUtils.getCurrentAdmin().getNickname());
        setCommenting(CommonState.INIT_VALUE);
        setLiking(CommonState.INIT_VALUE);
        setReading(CommonState.INIT_VALUE);
        // 默认情况下草稿
        if (getPublish() == null) {
            setPublish(PublishState.Draft);
        }
        // 默认情况下原创
        if (getOpen() == null) {
            setOpen(ArticleOrigin.Original);
        }
        // 默认情况私密
        if (getOrigin() == null) {
            setOrigin(ArticleOpen.Password);
        }
    }
}
