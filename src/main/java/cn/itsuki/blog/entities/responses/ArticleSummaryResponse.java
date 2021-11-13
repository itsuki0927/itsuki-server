package cn.itsuki.blog.entities.responses;

import cn.itsuki.blog.entities.ArticleSummary;
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
public class ArticleSummaryResponse {
    private ArticleSummary total;
    private ArticleSummary draft;
    private ArticleSummary recycle;
    private ArticleSummary published;
}
