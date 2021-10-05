package cn.itsuki.blog.services;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.itsuki.blog.entities.BlackEmail;
import cn.itsuki.blog.entities.BlackIp;
import cn.itsuki.blog.entities.BlackKeyword;
import cn.itsuki.blog.entities.SystemSettings;
import cn.itsuki.blog.entities.requests.BaseSearchRequest;
import cn.itsuki.blog.entities.requests.SystemSettingsRequest;
import cn.itsuki.blog.entities.responses.SystemSettingsResponse;
import cn.itsuki.blog.repositories.BlackEmailRepository;
import cn.itsuki.blog.repositories.BlackIpRepository;
import cn.itsuki.blog.repositories.BlackKeywordRepository;
import cn.itsuki.blog.repositories.SystemSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: itsuki
 * @create: 2021-09-28 08:31
 **/
@Service
public class SystemSettingsService {
    @Autowired
    private SystemSettingsRepository settingsRepository;
    @Autowired
    private BlackIpRepository ipRepository;
    @Autowired
    private BlackEmailRepository emailRepository;
    @Autowired
    private BlackKeywordRepository keywordRepository;

    private Long systemId = 1L;

    public SystemSettingsResponse get() {
        SystemSettings systemSettings = settingsRepository.getById(systemId);
        SystemSettingsResponse response = new SystemSettingsResponse();

        BeanUtil.copyProperties(systemSettings, response);
        List<BlackIp> blackIpList = ipRepository.findAll();
        List<BlackEmail> blackEmailList = emailRepository.findAll();
        List<BlackKeyword> blackKeywordList = keywordRepository.findAll();

        response.setIpBlackList(blackIpList.stream().map(BlackIp::getIp).collect(Collectors.toList()));
        response.setEmailBlackList(blackEmailList.stream().map(BlackEmail::getEmail).collect(Collectors.toList()));
        response.setKeywordBlackList(blackKeywordList.stream().map(BlackKeyword::getKeyword).collect(Collectors.toList()));

        return response;
    }

    public SystemSettingsResponse update(SystemSettingsRequest request) {
        SystemSettings systemSettings = new SystemSettings();
        BeanUtil.copyProperties(request, systemSettings);
        systemSettings.setId(systemId);

        updateIpBlankList(request.getIpBlackList());
        updateEmailBlankList(request.getEmailBlackList());
        updateKeywordBlankList(request.getKeywordBlackList());

        SystemSettings save = settingsRepository.save(systemSettings);

        SystemSettingsResponse response = new SystemSettingsResponse();
        BeanUtil.copyProperties(save, response);
        response.setIpBlackList(request.getIpBlackList());
        response.setEmailBlackList(request.getEmailBlackList());
        response.setKeywordBlackList(request.getKeywordBlackList());

        return response;
    }

    private void updateIpBlankList(List<String> ipList) {
        if (CollUtil.isNotEmpty(ipList)) {
            ipRepository.deleteAll();
            List<BlackIp> blackIpList = ipList.stream().map(v -> {
                BlackIp blackIp = new BlackIp();
                blackIp.setIp(v);
                return blackIp;
            }).collect(Collectors.toList());
            ipRepository.saveAll(blackIpList);
        }
    }

    private void updateEmailBlankList(List<String> emailList) {
        if (CollUtil.isNotEmpty(emailList)) {
            emailRepository.deleteAll();
            List<BlackEmail> blackEmailList = emailList.stream().map(v -> {
                BlackEmail blackEmail = new BlackEmail();
                blackEmail.setEmail(v);
                return blackEmail;
            }).collect(Collectors.toList());
            emailRepository.saveAll(blackEmailList);
        }
    }

    private void updateKeywordBlankList(List<String> keywordList) {
        if (CollUtil.isNotEmpty(keywordList)) {
            keywordRepository.deleteAll();
            List<BlackKeyword> blackKeywordList = keywordList.stream().map(v -> {
                BlackKeyword blackKeyword = new BlackKeyword();
                blackKeyword.setKeyword(v);
                return blackKeyword;
            }).collect(Collectors.toList());
            keywordRepository.saveAll(blackKeywordList);
        }
    }
}
