package cn.itsuki.blog.repositories;

import cn.itsuki.blog.entities.Article;
import cn.itsuki.blog.entities.ArticleArchive;
import cn.itsuki.blog.entities.ArticleId;
import cn.itsuki.blog.entities.ArticleSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 文章 仓库
 *
 * @author: itsuki
 * @create: 2021-09-21 08:35
 **/
@Repository
public interface ArticleRepository extends BaseRepository<Article> {
    @Modifying
    @Transactional
    @Query(value = "update article p set p.publish=:publish where p.id in (:articleIds)")
    int batchUpdateState(@Param("publish") Integer publish, @Param("articleIds") List<Long> articleIds);

    @Query(value = "select new cn.itsuki.blog.entities.ArticleSummary(a.publish , count(a.id)) from article a group by a.publish")
    List<ArticleSummary> summary();

    @Query(value = "select new cn.itsuki.blog.entities.ArticleArchive(a.id , a.title, a.createAt, a.description) from article a where a.publish = 1 order by a.createAt desc")
    List<ArticleArchive> archive();

    @Query(value = "select new cn.itsuki.blog.entities.ArticleId(a.id) from article a where a.publish = 1")
    List<ArticleId> ids();

    @Query(value = "select * from article where id = (select id from article where id>:articleId and publish = 1 order by id asc limit 1)", nativeQuery = true)
    Article next(@Param("articleId") long articleId);

    @Query(value = "select * from article where id = (select id from article where id<:articleId and publish = 1 order by id desc limit 1)", nativeQuery = true)
    Article prev(@Param("articleId") long articleId);

    @Query("select distinct a from article a left join article_tag t on t.articleId = a.id " +
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
    Page<Article> search(@Param("name") String name, @Param("publish") Integer publish, @Param("tag") Long tag,
                         @Param("banner") Integer banner, Pageable pageable);


    Page<Article> queryArticlesByPublish(@Param("publish") Integer publish, Pageable pageable);

    @Query("select count(a.id) from article a left join article_tag t on t.articleId = a.id " +
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

    int countArticlesByIdInAndPublishEquals(List<Long> ids, Integer publish);
}
