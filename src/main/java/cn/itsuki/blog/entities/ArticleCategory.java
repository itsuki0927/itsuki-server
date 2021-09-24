package cn.itsuki.blog.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author: itsuki
 * @create: 2021-09-23 16:51
 **/
@Entity(name = "article_category")
@Getter
@Setter
@ToString(callSuper = true)
public class ArticleCategory extends IdentifiableEntity {
    /**
     * article id
     */
    @Column(name = "article_id")
    private Long articleId;

    /**
     * tag id
     */
    @Column(name = "category_id")
    private Long categoryId;
}
