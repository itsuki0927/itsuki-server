package cn.itsuki.blog.repositories;

import cn.itsuki.blog.entities.ArticleTag;
import org.springframework.stereotype.Repository;

/**
 * 文章-标签 仓库
 *
 * @author: itsuki
 * @create: 2021-09-23 17:08
 **/
@Repository
public interface ArticleTagRepository extends BaseRepository<ArticleTag> {
    int deleteAllByArticleIdEquals(Long articleId);
}
