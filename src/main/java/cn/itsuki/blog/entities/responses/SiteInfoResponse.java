package cn.itsuki.blog.entities.responses;

import cn.itsuki.blog.entities.Category;
import cn.itsuki.blog.entities.Tag;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: itsuki
 * @create: 2021-10-24 18:18
 **/
@Getter
@Setter
public class SiteInfoResponse {
    private List<Tag> tags;
    private List<Category> categories;
    private SystemSettingsResponse site;
}
