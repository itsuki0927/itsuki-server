package cn.itsuki.blog.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;

/**
 * Article Tag 中间表
 *
 * @author: itsuki
 * @create: 2021-09-23 16:26
 **/
@Entity(name = "ArticleTag")
@Getter
@Setter
@ToString(callSuper = true)
public class ArticleTag extends IdentifiableEntity {
    /**
     * article id
     */
    private Long articleId;

    /**
     * tag id
     */
    private Long tagId;
}
