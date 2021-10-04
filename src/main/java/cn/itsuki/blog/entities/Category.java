package cn.itsuki.blog.entities;

import cn.itsuki.blog.constants.CommonState;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 分类管理
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

    /**
     * 父类id
     */
    @Column(name = "parent_id")
    private Long parentId;

    @Override
    protected void onCreateAction() {
        setSort(CommonState.INIT_VALUE);
        setCount(CommonState.INIT_VALUE);
        if (getParentId() == null) {
            setParentId((long) -1);
        }
    }
}

