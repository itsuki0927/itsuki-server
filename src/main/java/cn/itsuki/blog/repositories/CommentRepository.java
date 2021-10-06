package cn.itsuki.blog.repositories;

import cn.itsuki.blog.entities.Article;
import cn.itsuki.blog.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    List<Comment> findCommentsByArticleIdAndStatusIsIn(Long articleId, List<Integer> status);

    int countCommentsByArticleIdEqualsAndStatusIsIn(Long articleId, List<Integer> status);

    List<Comment> findCommentsByIdIn(List<Long> ids);

    /**
     * @param keyword   关键字
     * @param articleId 文章id
     * @param status    状态
     * @param pageable  分页
     * @return 分页列表
     */
    @Query("select c from comment c where " +
            "(" +
            ":keyword is null or c.nickname like %:keyword%" +
            "                  or c.content like %:keyword%" +
            "                  or c.email like %:keyword%" +
            "                  or c.website like %:keyword%" +
            "                  or c.articleTitle like %:keyword%" +
            ")" +
            "and (:articleId is null or c.articleId = :articleId)" +
            "and (:status is null or c.status = :status)" +
            "")
    Page<Comment> search(@Param("keyword") String keyword, @Param("articleId") Long articleId,
                         @Param("status") Integer status, Pageable pageable);
}
