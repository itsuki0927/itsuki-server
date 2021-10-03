package cn.itsuki.blog.repositories;

import cn.itsuki.blog.entities.Comment;
import org.springframework.stereotype.Repository;

/**
 * comment repository
 *
 * @author: itsuki
 * @create: 2021-10-03 16:34
 **/
@Repository
public interface CommentRepository extends BaseRepository<Comment> {
}
