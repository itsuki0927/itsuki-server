package cn.itsuki.blog.entities;


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
            setParentId(-1);
        }
        if (getArticleId() == null) {
            setArticleId(0l);
        }
        setFix(0);
        setStatus(0);
        setLiking(0);
    }
}
