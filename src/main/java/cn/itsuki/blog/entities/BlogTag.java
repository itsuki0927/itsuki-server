package cn.itsuki.blog.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * 文章-标签 实体
 *
 * @author: itsuki
 * @create: 2021-09-23 16:26
 **/
@Entity(name = "blog_tag")
@Getter
@Setter
@ToString(callSuper = true)
public class BlogTag extends IdentifiableEntity {
    /**
     * blog id
     */
    @NotNull
    @Column(name = "blog_id")
    private Long blogId;

    /**
     * tag id
     */
    @NotNull
    @Column(name = "tag_id")
    private Long tagId;
}
