package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: itsuki
 * @create: 2021-09-23 18:10
 **/
@Getter
@Setter
@ToString
public class ArticleCreateRequest {
    /**
     * 标题
     */
    @NotBlank
    private String title;

    /**
     * 描述
     */
    @NotBlank
    private String description;

    /**
     * 关键字
     */
    @NotBlank
    private String keywords;

    /**
     * 内容
     */
    @NotBlank
    private String content;

    /**
     * 封面
     */
    private String cover;

    /**
     * 分类id
     */
    private List<Long> categoryIds;

    /**
     * 标签id
     */
    private List<Long> tagIds;

    /**
     * 文章密码
     */
    private String password;

    /**
     * 发布状态: 0 -> 草稿, 1 -> 已发布, 2 -> 回收站
     */
    @NotNull
    private Integer publish;

    /**
     * 文章来源: 0 -> 原创, 1 -> 转载, 2 -> 混合
     */
    @NotNull
    private Integer origin;

    /**
     * 公开类型: 0 -> 需要密码, 1 -> 公开, 2 -> 私密
     */
    @NotNull
    private Integer open;

}
