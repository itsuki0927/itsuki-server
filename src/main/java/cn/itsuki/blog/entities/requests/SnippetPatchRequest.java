package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 片段 patch 请求类
 *
 * @author: itsuki
 * @create: 2021-11-06 21:12
 **/
@Getter
@Setter
public class SnippetPatchRequest {
    private Integer status;
    private List<Long> ids;
}
