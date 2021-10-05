package cn.itsuki.blog.services;

import cn.itsuki.blog.entities.BlackEmail;
import cn.itsuki.blog.repositories.BlackEmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: itsuki
 * @create: 2021-10-05 19:03
 **/
@Service
public class BlackEmailService {
    @Autowired
    private BlackEmailRepository repository;

    public List<String> findAll() {
        List<BlackEmail> ipList = repository.findAll();
        return ipList.stream().map(BlackEmail::getEmail).collect(Collectors.toList());
    }

    public void clear() {
        repository.deleteAll();
    }

    public void save(List<String> emailList) {
        List<BlackEmail> blackIpList = emailList.stream().map(v -> {
            BlackEmail blackEmail = new BlackEmail();
            blackEmail.setEmail(v);
            return blackEmail;
        }).collect(Collectors.toList());
        repository.saveAll(blackIpList);
    }

    public void clearAndSave(List<String> emailList) {
        clear();
        save(emailList);
    }

    public void remove(List<String> ipList) {
        repository.deleteBlackEmailByEmailIn(ipList);
    }

    public boolean isInBlackList(String email) {
        return findAll().stream().anyMatch(v -> v.equals(email));
    }
}


