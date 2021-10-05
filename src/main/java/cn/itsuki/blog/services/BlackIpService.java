package cn.itsuki.blog.services;

import cn.itsuki.blog.entities.BlackIp;
import cn.itsuki.blog.repositories.BlackIpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: itsuki
 * @create: 2021-10-05 19:03
 **/
@Service
public class BlackIpService {
    @Autowired
    private BlackIpRepository repository;

    public List<String> findAll() {
        List<BlackIp> ipList = repository.findAll();
        return ipList.stream().map(BlackIp::getIp).collect(Collectors.toList());
    }

    public void clear() {
        repository.deleteAll();
    }

    public void save(List<String> ipList) {
        List<BlackIp> blackIpList = ipList.stream().map(v -> {
            BlackIp blackIp = new BlackIp();
            blackIp.setIp(v);
            return blackIp;
        }).collect(Collectors.toList());
        repository.saveAll(blackIpList);
    }

    public void clearAndSave(List<String> ipList) {
        clear();
        save(ipList);
    }

    public void remove(List<String> ipList) {
        repository.deleteBlackIpsByIpIn(ipList);
    }

    public boolean isInBlackList(String ip) {
        return findAll().stream().anyMatch(v -> v.equals(ip));
    }
}


