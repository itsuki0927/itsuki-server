package cn.itsuki.blog.entities;


import cn.itsuki.blog.constants.CommentState;
import cn.itsuki.blog.constants.CommonState;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 评论
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
    private String email;

    /**
     * 网址
     */
    @NotBlank
    private String website;

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
    private String ip;

    /**
     *
     */
    private String agent;

    /**
     * 省份
     */
    private String city;

    /**
     * 城市
     */
    private String province;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 是否置顶
     * 0 -> 不置顶, 1 -> 置顶
     */
    @Min(0)
    private Integer fix;

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
     * 父id
     */
    @Column(name = "parent_id")
    private Integer parentId;

    /**
     * 文章id
     */
    @NotBlank
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
        setStatus(CommentState.Auditing);

        // 关闭fix
        setFix(CommonState.SHUT_DOWN);
        // 初始0
        setLiking(CommonState.INIT_VALUE);
    }
}
