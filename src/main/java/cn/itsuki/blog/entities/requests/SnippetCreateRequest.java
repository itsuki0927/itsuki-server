package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建片段 请求类
 *
 * @author: itsuki
 * @create: 2021-11-05 20:30
 **/
@Getter
@Setter
public class SnippetCreateRequest {
    /**
     * 名称
     */
    @NotBlank
    private String name;

    /**
     * 描述
     */
    @NotBlank
    private String description;

    /**
     * 用什么技巧实现的
     */
    @NotBlank
    private String skill;

    /**
     * code
     */
    @NotBlank
    private String code;

    /**
     * 示例
     */
    @NotBlank
    private String example;

    /**
     * 等级: 0 -> 简单, 1 -> 中等, 2 -> 困难
     */
    @NotNull
    private Integer ranks;

    /**
     * 状态: 0
     */
    @NotNull
    private Integer status;

    /**
     * 分类
     */
    @NotNull
    private List<Long> categoryIds;
}
