package cn.itsuki.blog.services;

import cn.hutool.core.bean.BeanUtil;
import cn.itsuki.blog.entities.BlackList;
import cn.itsuki.blog.entities.requests.UpdateBlackListInput;
import cn.itsuki.blog.repositories.BlackListRepository;
import com.alibaba.fastjson.JSONArray;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Autowired
    private AdminService adminService;

    public BlackList blacklist() {
        long blackListId = 1L;
        return repository.getById(blackListId);
    }

    public BlackList updateBlackList(UpdateBlackListInput input) {
        adminService.ensureAdminOperate();
        BlackList probe = blacklist();
        BeanUtil.copyProperties(input, probe);
        repository.save(probe);
        return probe;
    }

    public void addIP(String ip) {
        BlackList blackList = blacklist();
        JSONArray ipList = JSONArray.parseArray(blackList.getIp());
        ipList.add(ip);
        blackList.setIp(ipList.toString());
        repository.save(blackList);
    }

    public void addEmail(String email) {
        BlackList blackList = blacklist();
        JSONArray emailList = JSONArray.parseArray(blackList.getEmail());
        emailList.add(email);
        blackList.setEmail(emailList.toString());
        repository.save(blackList);
    }

    public void deleteIP(String ip) {
        BlackList blackList = blacklist();
        JSONArray ipList = JSONArray.parseArray(blackList.getIp());
        ipList.remove(ip);
        blackList.setIp(ipList.toString());
        repository.save(blackList);
    }

    public void deleteEmail(String email) {
        BlackList blackList = blacklist();
        JSONArray emailList = JSONArray.parseArray(blackList.getEmail());
        emailList.remove(email);
        blackList.setEmail(emailList.toString());
        repository.save(blackList);
    }
}
