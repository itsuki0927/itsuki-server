package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 评论 patch 类
 *
 * @author: itsuki
 * @create: 2021-10-04 06:07
 **/
@Getter
@Setter
public class CommentPatchRequest {

    private List<Long> ids;

    private Integer status;
}

