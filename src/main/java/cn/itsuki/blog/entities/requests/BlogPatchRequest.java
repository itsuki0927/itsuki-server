package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 文章 patch 请求类
 *
 * @author: itsuki
 * @create: 2021-09-25 18:49
 **/
@Getter
@Setter
public class BlogPatchRequest {
    /**
     * 需要更新的ids
     */
    @NotEmpty
    private List<Long> ids;
    /**
     * 更新的状态
     */
    @NotNull
    private Integer state;
}
