package cn.itsuki.blog.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @author: itsuki
 * @create: 2021-09-14 20:42
 **/
@Entity(name = "Article")
@Getter
@Setter
@ToString(callSuper = true)
public class Article extends IdentifiableEntity {
    /**
     * 标题
     */
    @NotBlank
    private String nickname;

    /**
     * 描述
     */
    private String description;

    /**
     * 内容
     */
    private String content;

    /**
     * 封面
     */
    private String cover;

    /**
     * 文章状态
     */
    @Min(0)
    private Integer status;

    /**
     * 评论数
     */
    @Min(0)
    private int commenting;

    /**
     * 作者
     */
    private String author;

    /**
     * 喜欢数
     */
    @Min(0)
    private int liking;

    /**
     * 观看数
     */
    @Min(0)
    private int reading;

    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    /**
     * 更新时间
     */
    private Date updateAt;
}
