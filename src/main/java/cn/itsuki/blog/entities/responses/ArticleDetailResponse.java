package cn.itsuki.blog.entities.responses;

import cn.itsuki.blog.entities.Article;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author: itsuki
 * @create: 2022-04-04 21:42
 **/
@Getter
@Setter
@ToString
public class ArticleDetailResponse extends Article {
    private Article prevArticle;
    private Article nextArticle;
}
