package cn.itsuki.blog.entities;

import cn.itsuki.blog.constants.CommonState;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 分类 实体
 *
 * @author: itsuki
 * @create: 2021-09-21 18:13
 **/
@Entity(name = "category")
@Getter
@Setter
@ToString(callSuper = true)
public class Category extends IdentifiableEntity {
    /**
     * 分类名称
     */
    @NotBlank
    private String name;

    /**
     * 分类路径
     */
    @NotBlank
    private String path;

    /**
     * 分类描述
     */
    @NotBlank
    private String description;

    /**
     * 自定义扩展
     */
    private String expand;

    /**
     * 排序
     */
    @Min(0)
    private Integer sort;

    /**
     * 当前分类下文章数量
     */
    @Min(0)
    private Integer count;
//
//    @OneToMany
//    @JoinColumn(name = "category_id")
//    @OrderBy(value = "createAt")
//    private List<Article> articles;

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
//    private List<Article> articles;

    @Override
    protected void onCreateAction() {
        setSort(CommonState.INIT_VALUE);
        setCount(CommonState.INIT_VALUE);
    }
}

