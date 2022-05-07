package cn.itsuki.blog.repositories;

import cn.itsuki.blog.entities.ArticleTag;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文章-标签 仓库
 *
 * @author: itsuki
 * @create: 2021-09-23 17:08
 **/
@Repository
public interface ArticleTagRepository extends BaseRepository<ArticleTag> {
    List<ArticleTag> deleteAllByArticleIdEquals(Long articleId);

    List<ArticleTag> findAllByTagIdEquals(Long tagId);

    List<ArticleTag> findAllByArticleIdEquals(Long articleId);
}
