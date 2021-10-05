package cn.itsuki.blog.repositories;

import cn.itsuki.blog.entities.Comment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * comment repository
 *
 * @author: itsuki
 * @create: 2021-10-03 16:34
 **/
@Repository
public interface CommentRepository extends BaseRepository<Comment> {
    @Modifying
    @Transactional
    @Query(value = "update comment  c set c.status = :status where c.id in :ids")
    int batchPatchStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    @Query(value = "select c from comment c where c.articleId = :articleId and c.status = 1 or c.status = 0")
    List<Comment> findCommentListByArticleId(@Param("articleId") Long articleId);
}
