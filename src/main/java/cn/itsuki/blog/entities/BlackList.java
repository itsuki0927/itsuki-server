package cn.itsuki.blog.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


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

