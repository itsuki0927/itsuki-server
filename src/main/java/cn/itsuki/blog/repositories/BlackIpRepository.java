package cn.itsuki.blog.repositories;

import cn.itsuki.blog.entities.BlackIp;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ip黑名单 仓库
 *
 * @author: itsuki
 * @create: 2021-10-05 16:30
 **/
@Repository
public interface BlackIpRepository extends BaseRepository<BlackIp> {
    void deleteBlackIpsByIpIn(List<String> ips);
}
