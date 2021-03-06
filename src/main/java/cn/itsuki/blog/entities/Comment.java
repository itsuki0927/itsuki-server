package cn.itsuki.blog.entities;


import cn.itsuki.blog.constants.CommentState;
import cn.itsuki.blog.constants.CommonState;
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
     * 内容
     */
    @NotBlank
    private String content;

    /**
     * 喜欢数
     */
    @Min(0)
    private Integer liking;

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
     * 是否置顶
     * 0 -> 不置顶, 1 -> 置顶
     */
    @Min(0)
    private Integer fix;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 登录方式: 0 -> github、1 -> qq、2 -> wechat
     */
    @Column(name = "login_type")
    private String loginType;

    /**
     * 扩展
     */
    private String expand;

    /**
     * 文章标题
     */
    @Column(name = "article_title")
    private String articleTitle;

    /**
     * 文章描述
     */
    @Column(name = "article_description")
    private String articleDescription;

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
    @Column(name = "article_id")
    private Long articleId;

    @Override
    protected void onCreateAction() {
        if (getParentId() == null) {
            setParentId(CommonState.NO_PARENT);
        }
        // 没有文章id, 默认为留言板
        if (getArticleId() == null) {
            setArticleId(CommonState.GUESTBOOK);
        }
        // 默认待审核
        if (getState() == null) {
            setState(CommentState.Auditing);
        }
        // 关闭fix
        if (getFix() == null) {
            setFix(CommonState.SHUT_DOWN);
        }
        if (getProvince() == null) {
            setProvince("未知");
        }
        if (getCity() == null) {
            setCity("未知");
        }
        // 初始0
        setLiking(CommonState.INIT_VALUE);
    }
}
