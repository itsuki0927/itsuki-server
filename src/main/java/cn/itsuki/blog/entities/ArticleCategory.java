package cn.itsuki.blog.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * 文章-分类 实体
 *
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
    @NotNull
    @Column(name = "article_id")
    private Long articleId;

    /**
     * tag id
     */
    @NotNull
    @Column(name = "category_id")
    private Long categoryId;
}
