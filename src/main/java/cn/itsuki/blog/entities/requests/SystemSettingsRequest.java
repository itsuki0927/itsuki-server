package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 系统设置 请求类
 *
 * @author: itsuki
 * @create: 2021-10-05 16:35
 **/
@Getter
@Setter
public class SystemSettingsRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String subtitle;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String description;
    @NotBlank
    private String keywords;
    @NotBlank
    private String domain;
    @NotBlank
    private String record;
    private List<String> ipBlackList;
    private List<String> emailBlackList;
    private List<String> keywordBlackList;
}
