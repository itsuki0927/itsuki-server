package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * 文章 meta patch 请求类
 *
 * @author: itsuki
 * @create: 2021-10-04 20:42
 **/
@Getter
@Setter
@ToString
public class ArticleMetaPatchRequest {

    /**
     * liking、reading
     */
    @NotBlank
    private String meta;
}
