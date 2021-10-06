package cn.itsuki.blog.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

/**
 * 关键字黑名称 实体
 *
 * @author: itsuki
 * @create: 2021-10-05 16:29
 **/
@Entity(name = "black_keyword")
@Getter
@Setter
@ToString(callSuper = true)
public class BlackKeyword extends IdentifiableEntity {
    @NotBlank
    private String keyword;
}
