package cn.itsuki.blog.entities;


import cn.itsuki.blog.constants.CommentState;
import cn.itsuki.blog.constants.CommonState;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 评论 实体
 *
 * @author: itsuki
 * @create: 2021-10-03 16:21
 **/
@Entity(name = "comment")
@Getter
@Setter
@ToString(callSuper = true)
public class Comment extends IdentifiableEntity {

    /**
     * 昵称
     */
    @NotBlank
    private String nickname;

    /**
     * 邮箱
     */
    @NotBlank
    @Email
    private String email;

    /**
     * uid
     */
    @NotBlank
    private String uid;

    /**
     * 内容
     */
    @NotBlank
    private String content;

    /**
     * emoji
     */
    private String emoji;

    /**
     * ip
     */
    @NotBlank
    private String ip;

    /**
     * 设备
     */
    @NotBlank
    private String agent;

    /**
     * 省份
     */
    @NotNull
    private String city;

    /**
     * 城市
     */
    @NotNull
    private String province;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 登录方式: 0 -> github、1 -> qq、2 -> wechat
     */
    private String provider;

    /**
     * 文章标题
     */
    @Column(name = "blog_title")
    private String blogTitle;

    /**
     * 文章标题
     */
    @Column(name = "blog_path")
    private String blogPath;

    /**
     * 文章描述
     */
    @Column(name = "blog_description")
    private String blogDescription;

    /**
     * 回复人昵称
     */
    @Column(name = "parent_nickname")
    private String parentNickName;

    /**
     * 父id
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 文章id
     */
    @NotNull
    @Column(name = "blog_id")
    private Long blogId;

    @Override
    protected void onCreateAction() {
        if (getParentId() == null) {
            setParentId(CommonState.NO_PARENT);
        }
        // 没有文章id, 默认为留言板
        if (getBlogId() == null) {
            setBlogId(CommonState.GUESTBOOK);
        }
        // 默认待审核
        if (getState() == null) {
            setState(CommentState.Auditing);
        }
        if (getProvince() == null) {
            setProvince("未知");
        }
        if (getCity() == null) {
            setCity("未知");
        }
    }
}
