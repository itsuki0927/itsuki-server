package cn.itsuki.blog.repositories;

import cn.itsuki.blog.entities.Blog;
import cn.itsuki.blog.entities.BlogArchive;
import cn.itsuki.blog.entities.BlogId;
import cn.itsuki.blog.entities.BlogSummary;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文章 仓库
 *
 * @author: itsuki
 * @create: 2021-09-21 08:35
 **/
@Repository
public interface BlogRepository extends BaseRepository<Blog> {
    @Modifying
    @Transactional
    @Query(value = "update blog p set p.publish=:publish where p.id in (:blogIds)")
    int batchUpdateState(@Param("publish") Integer publish, @Param("blogIds") List<Long> blogIds);

    @Query(value = "select new cn.itsuki.blog.entities.BlogSummary(a.publish , count(a.id)) from blog a group by a.publish")
    List<BlogSummary> summary();

    @Query(value = "select new cn.itsuki.blog.entities.BlogArchive(a.id , a.title, a.createAt, a.description) from blog a where a.publish = 1 order by a.createAt desc")
    List<BlogArchive> archive();

    @Query(value = "select new cn.itsuki.blog.entities.BlogId(a.id) from blog a where a.publish = 1")
    List<BlogId> ids();

    @Query(value = "select * from blog where id = (select id from blog where id>:blogId and publish = 1 order by id asc limit 1)", nativeQuery = true)
    Blog next(@Param("blogId") long blogId);

    @Query(value = "select * from blog where id = (select id from blog where id<:blogId and publish = 1 order by id desc limit 1)", nativeQuery = true)
    Blog prev(@Param("blogId") long blogId);

    @Query("select distinct a from blog a left join blog_tag t on t.blogId = a.id " +
            "where " +
            "(" +
            "   :name is null or a.title like %:name% " +
            "                 or a.keywords like %:name% " +
            "                 or a.description like  %:name%" +
            ")" +
            "and (:publish is null or a.publish = :publish)" +
            "and (:banner is null or a.banner = :banner)" +
            "and (:tag is null or t.tagId = :tag)" +
            "")
    Page<Blog> search(@Param("name") String name, @Param("publish") Integer publish, @Param("tag") Long tag,
                      @Param("banner") Integer banner, Pageable pageable);


    Page<Blog> queryBlogsByPublish(@Param("publish") Integer publish, Pageable pageable);

    @Query("select count(a.id) from blog a left join blog_tag t on t.blogId = a.id " +
            "where " +
            "(" +
            "   :name is null or a.title like %:name% " +
            "                 or a.keywords like %:name% " +
            "                 or a.description like  %:name%" +
            ")" +
            "and (:publish is null or a.publish = :publish)" +
            "and (:banner is null or a.banner = :banner)" +
            "and (:tagId is null or t.id = :tagId)" +
            "")
    int count(@Param("name") String name, @Param("publish") Integer publish,
              @Param("tagId") Long tagId, @Param("banner") Integer banner);

    int countBlogsByIdInAndPublishEquals(List<Long> ids, Integer publish);

    @Query("select sum(a.reading) from blog a where a.publish = 1")
    int blogReading();
}
