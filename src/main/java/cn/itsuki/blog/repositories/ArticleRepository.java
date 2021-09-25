package cn.itsuki.blog.repositories;

import cn.itsuki.blog.entities.Article;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author: itsuki
 * @create: 2021-09-21 08:35
 **/
@Repository
public interface ArticleRepository extends BaseRepository<Article> {
    @Modifying
    @Transactional
    @Query(value = "update article p set p.publish=:publish where p.id in (:articleIds)")
    int batchUpdateState(@Param("publish") Integer publish, @Param("articleIds") List<Long> articleIds);
}
