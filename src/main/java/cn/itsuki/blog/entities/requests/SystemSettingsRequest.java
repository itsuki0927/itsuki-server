package cn.itsuki.blog.entities.requests;

import cn.itsuki.blog.entities.SystemSettings;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: itsuki
 * @create: 2021-10-05 16:35
 **/
@Getter
@Setter
public class SystemSettingsRequest {
    private String title;
    private String subtitle;
    private String email;
    private String description;
    private String keywords;
    private String domain;
    private String record;
    private List<String> ipBlackList;
    private List<String> emailBlackList;
    private List<String> keywordBlackList;
}
