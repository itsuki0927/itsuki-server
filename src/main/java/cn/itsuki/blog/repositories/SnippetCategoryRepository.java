package cn.itsuki.blog.repositories;

import cn.itsuki.blog.entities.Category;
import cn.itsuki.blog.entities.SnippetCategory;
import org.springframework.stereotype.Repository;

/**
 * 片段分类 仓库
 *
 * @author: itsuki
 * @create: 2021-09-21 18:12
 **/
@Repository
public interface SnippetCategoryRepository extends BaseRepository<SnippetCategory> {
    Category findCategoryByNameEqualsOrPathEquals(String name, String path);
}
