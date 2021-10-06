package cn.itsuki.blog.services;

import cn.itsuki.blog.entities.BlackKeyword;
import cn.itsuki.blog.repositories.BlackKeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 关键字黑名单 服务
 *
 * @author: itsuki
 * @create: 2021-10-05 19:03
 **/
@Service
public class BlackKeywordService {
    @Autowired
    private BlackKeywordRepository repository;

    public List<String> findAll() {
        List<BlackKeyword> ipList = repository.findAll();
        return ipList.stream().map(BlackKeyword::getKeyword).collect(Collectors.toList());
    }

    public void clear() {
        repository.deleteAll();
    }

    public void save(List<String> emailList) {
        List<BlackKeyword> keywordList = emailList.stream().map(v -> {
            BlackKeyword keyword = new BlackKeyword();
            keyword.setKeyword(v);
            return keyword;
        }).collect(Collectors.toList());
        repository.saveAll(keywordList);
    }

    public void clearAndSave(List<String> keywordList) {
        clear();
        save(keywordList);
    }

    public boolean isInBlackList(String keywords) {
        return findAll().stream().anyMatch(v -> v.contains(keywords));
    }
}


