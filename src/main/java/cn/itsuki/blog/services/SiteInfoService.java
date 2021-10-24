package cn.itsuki.blog.services;

import cn.itsuki.blog.entities.Category;
import cn.itsuki.blog.entities.Tag;
import cn.itsuki.blog.entities.requests.BaseSearchRequest;
import cn.itsuki.blog.entities.requests.TagSearchRequest;
import cn.itsuki.blog.entities.responses.SearchResponse;
import cn.itsuki.blog.entities.responses.SiteInfoResponse;
import cn.itsuki.blog.entities.responses.SystemSettingsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: itsuki
 * @create: 2021-10-24 18:16
 **/
@Service
public class SiteInfoService {
    @Autowired
    private TagService tagService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SystemSettingsService settingsService;

    public SiteInfoResponse get() {
        SiteInfoResponse siteInfoResponse = new SiteInfoResponse();

        SearchResponse<Tag> tagSearchResponse = tagService.search(new TagSearchRequest());
        siteInfoResponse.setTags(tagSearchResponse.getData());

        SearchResponse<Category> categorySearchResponse = categoryService.search(new BaseSearchRequest());
        siteInfoResponse.setCategories(categorySearchResponse.getData());

        SystemSettingsResponse settingsResponse = settingsService.get();
        siteInfoResponse.setSite(settingsResponse);

        return siteInfoResponse;
    }
}
