package cn.itsuki.blog.entities.responses;

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
public class SystemSettingsResponse {
    /**
     * 喜欢
     */
    private int liking;

    /**
     * 站点标题
     */
    private String title;

    /**
     * 副标题
     */
    private String subtitle;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 描述
     */
    private String description;

    /**
     * 关键词
     */
    private String keywords;

    /**
     * 站长地址
     */
    private String domain;

    /**
     * 备案号
     */
    private String record;

    /**
     * ip黑名单
     */
    private List<String> ipBlackList;

    /**
     * 邮箱黑名单
     */
    private List<String> emailBlackList;

    /**
     * 关键字黑名单
     */
    private List<String> keywordBlackList;
}
