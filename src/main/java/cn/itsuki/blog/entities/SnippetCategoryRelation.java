package cn.itsuki.blog.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * 片段-分类-中间 实体
 *
 * @author: itsuki
 * @create: 2021-11-17 21:18
 **/
@Entity(name = "snippet_category_relation")
@Getter
@Setter
@ToString(callSuper = true)
public class SnippetCategoryRelation extends IdentifiableEntity {
    /**
     * article id
     */
    @NotNull
    @Column(name = "snippet_id")
    private Long snippetId;

    /**
     * tag id
     */
    @NotNull
    @Column(name = "category_id")
    private Long categoryId;

}
