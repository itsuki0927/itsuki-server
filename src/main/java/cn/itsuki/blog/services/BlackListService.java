package cn.itsuki.blog.services;

import cn.itsuki.blog.entities.BlackList;
import cn.itsuki.blog.repositories.BlackListRepository;
import com.alibaba.fastjson.JSONObject;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 黑名单 服务
 *
 * @author: itsuki
 * @create: 2021-09-28 08:31
 **/
@Service
public class BlackListService implements GraphQLQueryResolver, GraphQLMutationResolver {
    @Autowired
    private BlackListRepository repository;

    private final long blackListId = 1L;

    public BlackList blacklist() {
        return repository.getById(blackListId);
    }

    public BlackList update(BlackList input) {
        input.setId(blackListId);
        repository.save(input);
        return input;
    }

    public void add(String ip, String email) {
        BlackList blackList = new BlackList();
        List<String> ipList = (List<String>) JSONObject.parseObject(blackList.getIp());
        ipList.add(ip);
        List<String> emailList = (List<String>) JSONObject.parseObject(blackList.getEmail());
        emailList.add(email);
        blackList.setIp(ipList.toString());
        blackList.setEmail(emailList.toString());
        blackList.setId(blackListId);
        repository.save(blackList);
    }

    public void delete(String ip, String email) {
        BlackList blackList = new BlackList();
        List<String> ipList = (List<String>) JSONObject.parseObject(blackList.getIp());
        ipList.remove(ip);
        blackList.setIp(ipList.toString());
        List<String> emailList = (List<String>) JSONObject.parseObject(blackList.getEmail());
        emailList.remove(email);
        blackList.setEmail(emailList.toString());
        blackList.setId(blackListId);
        repository.save(blackList);
    }
}
