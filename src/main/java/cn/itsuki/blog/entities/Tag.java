package cn.itsuki.blog.entities;

import cn.itsuki.blog.constants.CommonState;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 标签 实体
 *
 * @author: itsuki
 * @create: 2021-09-21 18:13
 **/
@Entity(name = "tag")
@Getter
@Setter
@ToString(callSuper = true)
public class Tag extends IdentifiableEntity {
    /**
     * 标签名称
     */
    @NotBlank
    private String name;

    /**
     * 标签路径
     */
    @NotBlank
    private String path;

    /**
     * 标签描述
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
     * 当前标签下文章数量
     */
    @Min(0)
    private Integer count;

    @Override
    protected void onCreateAction() {
        if (getSort() != null) {
            setSort(CommonState.INIT_VALUE);
        }
        setCount(CommonState.INIT_VALUE);
    }
}

