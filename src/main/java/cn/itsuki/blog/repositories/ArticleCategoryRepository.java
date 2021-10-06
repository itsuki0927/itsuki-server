package cn.itsuki.blog.repositories;

import cn.itsuki.blog.entities.ArticleCategory;
import org.springframework.stereotype.Repository;

/**
 * 文章-分类 仓库
 *
 * @author: itsuki
 * @create: 2021-09-23 17:08
 **/
@Repository
public interface ArticleCategoryRepository extends BaseRepository<ArticleCategory> {
    void deleteAllByArticleIdEquals(Long articleId);
}
