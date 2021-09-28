package cn.itsuki.blog.services;

import cn.itsuki.blog.entities.SystemConfig;
import cn.itsuki.blog.entities.requests.BaseSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author: itsuki
 * @create: 2021-09-28 08:31
 **/
@Service
public class SystemConfigService extends BaseService<SystemConfig, BaseSearchRequest> {
    /**
     * 创建一个service实例
     */
    public SystemConfigService() {
        super("id", new String[]{"id"});
    }

    @Override
    protected Page<SystemConfig> searchWithPageable(BaseSearchRequest criteria, Pageable pageable) {
        return repository.findAll(pageable);
    }
}
