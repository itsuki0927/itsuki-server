package cn.itsuki.blog.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 系统设置 实体
 *
 * @author: itsuki
 * @create: 2021-09-28 08:22
 **/
@Entity(name = "black_list")
@Getter
@Setter
@ToString(callSuper = true)
public class BlackList extends IdentifiableEntity {
    private String ip;

    private String email;

    private String keyword;
}

