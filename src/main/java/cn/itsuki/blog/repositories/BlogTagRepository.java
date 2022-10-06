package cn.itsuki.blog.repositories;

import cn.itsuki.blog.entities.BlogTag;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文章-标签 仓库
 *
 * @author: itsuki
 * @create: 2021-09-23 17:08
 **/
@Repository
public interface BlogTagRepository extends BaseRepository<BlogTag> {
    List<BlogTag> deleteAllByBlogIdEquals(Long blogId);

    List<BlogTag> findAllByTagIdEquals(Long tagId);

    List<BlogTag> findAllByBlogIdEquals(Long blogId);
}
