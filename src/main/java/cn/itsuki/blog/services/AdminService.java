package cn.itsuki.blog.services;

import cn.itsuki.blog.entities.Admin;
import cn.itsuki.blog.entities.OperateState;
import cn.itsuki.blog.entities.requests.AdminSearchRequest;
import cn.itsuki.blog.entities.requests.LoginRequest;
import cn.itsuki.blog.entities.responses.LoginResponse;
import cn.itsuki.blog.repositories.AdminRepository;
import cn.itsuki.blog.security.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 管理员 service
 *
 * @author: itsuki
 * @create: 2021-09-15 19:57
 **/
@Service
public class AdminService extends BaseService<Admin, AdminSearchRequest> {
    @Autowired
    private AdminRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenUtils tokenUtils;

    /**
     * 创建一个service实例
     */
    public AdminService() {
        super("id", new String[]{"id"});
    }

    public LoginResponse login(LoginRequest request) {
        Admin probe = new Admin();
        probe.setUsername(request.getUsername());
        Optional<Admin> optionalAdmin = repository.findOne(Example.of(probe));

        if (!optionalAdmin.isPresent()) {
            throw new BadCredentialsException("用户名不存在");
        }

        Admin admin = optionalAdmin.get();

        // 如果密码不正确
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new BadCredentialsException("密码不正确");
        }

        LoginResponse response = new LoginResponse();
        response.setToken(tokenUtils.createJwtToken(admin));
        response.setExpiration(tokenUtils.getExpirationInSeconds());
        response.setStatus(OperateState.OK);

        return response;
    }

    public Admin get(Long id) {
        Admin admin = ensureExist(repository, id, "Admin");
        admin.setPassword(null);
        return admin;
    }

    @Override
    protected Page<Admin> searchWithPageable(AdminSearchRequest criteria, Pageable pageable) {
        return null;
    }
}
