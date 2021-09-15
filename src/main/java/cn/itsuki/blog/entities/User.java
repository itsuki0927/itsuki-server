package cn.itsuki.blog.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @author: itsuki
 * @create: 2021-09-14 20:42
 **/
@Entity(name = "User")
@Getter
@Setter
@ToString(callSuper = true)
public class User extends IdentifiableEntity {
    /**
     * 标题
     */
    private String nickname;
    /**
     * 描述
     */
    private String description;

    /**
     * 评论数
     */
    @Min(0)
    private int comment;

    /**
     * 喜欢数
     */
    @Min(0)
    private int liker;


    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 更新时间
     */
    private Date updateAt;
}
