package cn.itsuki.blog.services;

import cn.itsuki.blog.entities.Article;
import cn.itsuki.blog.entities.requests.ArticleSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author: itsuki
 * @create: 2021-09-20 22:19
 **/
@Service
public class ArticleService extends BaseService<Article, ArticleSearchRequest> {
    @Autowired
    AdminService adminService;

    /**
     * 创建一个service实例
     */
    public ArticleService() {
        super("liking", new String[]{"commenting", "liking", "reading"});
    }

    @Override
    public Article create(Article entity) {
        entity.setAuthor(adminService.getCurrentAdmin().getNickname());
        entity.setCreateAt(new Date());
        entity.setUpdateAt(new Date());
        return super.create(entity);
    }

    @Override
    protected Page<Article> searchWithPageable(ArticleSearchRequest criteria, Pageable pageable) {
        return null;
    }
}
