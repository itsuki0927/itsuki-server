package cn.itsuki.blog.services;

import cn.hutool.core.bean.BeanUtil;
import cn.itsuki.blog.entities.SystemSettings;
import cn.itsuki.blog.entities.requests.SystemSettingsRequest;
import cn.itsuki.blog.entities.responses.SystemSettingsResponse;
import cn.itsuki.blog.repositories.SystemSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 系统设置 服务
 *
 * @author: itsuki
 * @create: 2021-09-28 08:31
 **/
@Service
public class SystemSettingsService {
    @Autowired
    private SystemSettingsRepository settingsRepository;
    @Autowired
    private BlackIpService ipService;
    @Autowired
    private BlackEmailService emailService;
    @Autowired
    private BlackKeywordService keywordService;

    private Long systemId = 1L;

    public SystemSettingsResponse get() {
        SystemSettings systemSettings = settingsRepository.getById(systemId);
        SystemSettingsResponse response = new SystemSettingsResponse();

        BeanUtil.copyProperties(systemSettings, response);

        response.setIpBlackList(ipService.findAll());
        response.setEmailBlackList(emailService.findAll());
        response.setKeywordBlackList(keywordService.findAll());

        return response;
    }

    public SystemSettingsResponse update(SystemSettingsRequest request) {
        SystemSettings systemSettings = new SystemSettings();
        BeanUtil.copyProperties(request, systemSettings);
        systemSettings.setId(systemId);

        ipService.clearAndSave(request.getIpBlackList());
        emailService.clearAndSave(request.getEmailBlackList());
        keywordService.clearAndSave(request.getKeywordBlackList());

        SystemSettings save = settingsRepository.save(systemSettings);

        SystemSettingsResponse response = new SystemSettingsResponse();
        BeanUtil.copyProperties(save, response);

        response.setIpBlackList(request.getIpBlackList());
        response.setEmailBlackList(request.getEmailBlackList());
        response.setKeywordBlackList(request.getKeywordBlackList());

        return response;
    }
}
