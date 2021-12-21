package cn.itsuki.blog.services;

import cn.hutool.db.sql.Direction;
import cn.itsuki.blog.entities.Article;
import cn.itsuki.blog.entities.Category;
import cn.itsuki.blog.entities.Tag;
import cn.itsuki.blog.entities.requests.ArticleSearchRequest;
import cn.itsuki.blog.entities.responses.SiteInfoResponse;
import cn.itsuki.blog.entities.responses.SiteSummaryResponse;
import cn.itsuki.blog.entities.responses.SystemSettingsResponse;
import cn.itsuki.blog.repositories.CategoryRepository;
import cn.itsuki.blog.repositories.CommentRepository;
import cn.itsuki.blog.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 网站信息 服务
 *
 * @author: itsuki
 * @create: 2021-10-24 18:16
 **/
@Service
public class SiteInfoService {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private SystemSettingsService settingsService;
    @Autowired
    private CommentRepository commentRepository;

    public SiteInfoResponse get() {
        SiteInfoResponse siteInfoResponse = new SiteInfoResponse();

        List<Tag> tags = tagRepository.findAll();
        siteInfoResponse.setTags(tags);

        List<Category> categories = categoryRepository.findAll();
        siteInfoResponse.setCategories(categories);

        SystemSettingsResponse settings = settingsService.get();
        siteInfoResponse.setSite(settings);

        Page<Article> articles = articleService.getHotArticles();
        siteInfoResponse.setHotArticles(articles.getContent());

        return siteInfoResponse;
    }

    public SiteSummaryResponse getSummary() {
        long article = articleService.count(new ArticleSearchRequest());
        long tag = tagRepository.count();
        long comment = commentRepository.count();
        SiteSummaryResponse response = new SiteSummaryResponse();
        response.setArticle(article);
        response.setTag(tag);
        response.setComment(comment);

        return response;
    }
}
