package cn.itsuki.blog.repositories;

import cn.itsuki.blog.entities.BlackEmail;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 邮箱黑名单 仓库
 *
 * @author: itsuki
 * @create: 2021-10-05 16:30
 **/
@Repository
public interface BlackEmailRepository extends BaseRepository<BlackEmail> {
    int deleteBlackEmailByEmailIn(List<String> emails);
}
