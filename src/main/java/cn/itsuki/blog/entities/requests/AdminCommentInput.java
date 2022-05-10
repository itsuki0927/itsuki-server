package cn.itsuki.blog.entities.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 管理员创建评论 请求类
 *
 * @author: itsuki
 * @create: 2022-05-10 09:47
 **/
@Data
public class AdminCommentInput {
    @NotBlank
    private String content;
    @NotNull
    private Long articleId;
    @NotNull
    private Long parentId;
    @NotBlank
    private String agent;
}
