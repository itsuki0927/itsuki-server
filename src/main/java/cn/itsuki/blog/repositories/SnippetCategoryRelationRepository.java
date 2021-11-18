package cn.itsuki.blog.repositories;

import cn.itsuki.blog.entities.SnippetCategoryRelation;
import org.springframework.stereotype.Repository;

/**
 * 片段-分类-中间 仓库
 *
 * @author: itsuki
 * @create: 2021-09-23 17:08
 **/
@Repository
public interface SnippetCategoryRelationRepository extends BaseRepository<SnippetCategoryRelation> {
    void deleteAllBySnippetIdEquals(Long snippetId);
}
