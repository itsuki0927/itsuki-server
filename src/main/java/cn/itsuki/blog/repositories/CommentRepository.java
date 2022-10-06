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

    @Query("select count(c.id) from comment c where c.blogId = :blogId and c.state <> 2")
    int countComments(@Param("blogId") Long blogId);

    @Query("select count(c.id) from comment c where c.blogId <> 10000 and c.state <> 2")
    int totalComment();

    @Query("select count(c.id) from comment c where c.blogId = 10000 and c.state <> 2")
    int totalGuestbook();

    List<Comment> findCommentsByIdIn(List<Long> ids);

    void deleteCommentsByBlogIdEquals(Long blogId);

    /**
     * @param keyword   关键字
     * @param blogId 文章id
     * @param state     状态
     * @param pageable  分页
     * @return 分页列表
     */
    @Query("select c from comment c where " +
            "(" +
            ":keyword is null or c.nickname like %:keyword%" +
            "                  or c.content like %:keyword%" +
            "                  or c.email like %:keyword%" +
            "                  or c.blogTitle like %:keyword%" +
            ")" +
            "and (:blogId is null or c.blogId = :blogId)" +
            "and (:blogPath is null or c.blogPath = :blogPath)" +
            "and (:state is null or c.state = :state)" +
            "")
    Page<Comment> search(@Param("keyword") String keyword, @Param("blogId") Long blogId, @Param("blogPath") String blogPath,
                         @Param("state") Integer state, Pageable pageable);

    @Query("select c from comment c where c.blogId = 10000 and c.email <> :adminEmail and c.state <> 2")
    Page<Comment> recent(@Param("adminEmail") String adminEmail,Pageable pageable);
}
