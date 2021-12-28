package cn.itsuki.blog.repositories;

import cn.itsuki.blog.entities.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;

/**
 * 标签 仓库
 *
 * @author: itsuki
 * @create: 2021-09-21 18:12
 **/
@Repository
public interface TagRepository extends BaseRepository<Tag> {
    Page<Tag> findByNameContainingOrPathContaining(@NotBlank String name, @NotBlank String path, Pageable pageable);

    Tag findTagByNameEqualsOrPathEquals(String name, String path);
}
