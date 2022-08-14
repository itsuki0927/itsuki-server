package cn.itsuki.blog.repositories;

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
 * 评论 仓库
 *
 * @author: itsuki
 * @create: 2021-10-03 16:34
 **/
@Repository
public interface CommentRepository extends BaseRepository<Comment> {
    @Modifying
    @Transactional
    @Query(value = "update comment  c set c.state = :state where c.id = :id")
    void updateState(@Param("id") Long id, @Param("state") Integer state);

    @Query("select count(c.id) from comment c where c.articleId = :articleId and (c.state = 1 or c.state = 0)")
    int countComments(@Param("articleId") Long articleId);

    List<Comment> findCommentsByIdIn(List<Long> ids);

    void deleteCommentsByArticleIdEquals(Long articleId);

    /**
     * @param keyword   关键字
     * @param articleId 文章id
     * @param state     状态
     * @param pageable  分页
     * @return 分页列表
     */
    @Query("select c from comment c where " +
            "(" +
            ":keyword is null or c.nickname like %:keyword%" +
            "                  or c.content like %:keyword%" +
            "                  or c.email like %:keyword%" +
            "                  or c.articleTitle like %:keyword%" +
            ")" +
            "and (:articleId is null or c.articleId = :articleId)" +
            "and (:articlePath is null or c.articlePath = :articlePath)" +
            "and (:state is null or c.state = :state)" +
            "")
    Page<Comment> search(@Param("keyword") String keyword, @Param("articleId") Long articleId, @Param("articlePath") String articlePath,
                         @Param("state") Integer state, Pageable pageable);
}
