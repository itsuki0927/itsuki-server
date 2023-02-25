package cn.itsuki.blog.entities;

import cn.itsuki.blog.constants.*;
import cn.itsuki.blog.security.SecurityUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

/**
 * 文章 实体
 *
 * @author: itsuki
 * @create: 2021-09-14 20:42
 **/
@Entity(name = "blog")
@Getter
@Setter
@ToString(callSuper = true)
public class Blog extends IdentifiableEntity {
    /**
     * 标题
     */
    @NotBlank
    private String title;

    /**
     * 路径
     */
    @NotBlank
    private String path;

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
     * 发布状态: 0 -> 草稿, 1 -> 已发布, 2 -> 回收站
     */
    @NonNull
    private Integer publish;

    @Column(name = "card_style")
    private Integer cardStyle;

    @Override
    protected void onCreateAction() {
        setAuthor(Objects.requireNonNull(SecurityUtils.getCurrentMember()).getNickname());
        setCommenting(CommonState.INIT_VALUE);
        setLiking(CommonState.INIT_VALUE);
        setReading(CommonState.INIT_VALUE);
        // 默认情况下草稿
        if(getCardStyle() == null){
            setCardStyle(0);
        }
    }
}
