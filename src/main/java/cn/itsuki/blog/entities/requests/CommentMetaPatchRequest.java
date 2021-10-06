package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * 评论 meta patch 请求类
 *
 * @author: itsuki
 * @create: 2021-10-04 22:50
 **/
@Getter
@Setter
@ToString
public class CommentMetaPatchRequest {

    /**
     * liking
     */
    @NotBlank
    private String meta;
}
