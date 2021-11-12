package cn.itsuki.blog.repositories;

import cn.itsuki.blog.entities.Snippet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 片段 仓库
 *
 * @author: itsuki
 * @create: 2021-11-05 14:00
 **/
@Repository
public interface SnippetRepository extends BaseRepository<Snippet> {

    @Query("select s from snippet s " +
            "where " +
            "(" +
            "   :keyword is null or s.author like %:keyword% " +
            "                     or s.name like %:keyword% " +
            "                     or s.email like %:keyword% " +
            ")" +
            "and (:status is null or s.status = :status)" +
            "and (:ranks is null or s.ranks = :ranks)" +
            "")
    Page<Snippet> search(@Param("keyword") String keyword, @Param("status") Integer status, @Param("ranks") Integer ranks, Pageable pageable);
}
