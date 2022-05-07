package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 评论 patch 请求类
 *
 * @author: itsuki
 * @create: 2021-10-04 06:07
 **/
@Getter
@Setter
public class CommentPatchRequest {
    @NotEmpty
    private List<Long> ids;

    @NotNull
    private Integer state;
}

