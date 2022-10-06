package cn.itsuki.blog.entities.responses;

import cn.itsuki.blog.entities.BlogSummary;
import lombok.Getter;
import lombok.Setter;

/**
 * 文章统计 响应类
 *
 * @author: itsuki
 * @create: 2021-11-13 16:52
 **/
@Getter
@Setter
public class BlogSummaryResponse {
    private BlogSummary total;
    private BlogSummary draft;
    private BlogSummary recycle;
    private BlogSummary published;
}
