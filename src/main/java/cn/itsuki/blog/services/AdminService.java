package cn.itsuki.blog.services;

import cn.hutool.core.bean.BeanUtil;
import cn.itsuki.blog.entities.Admin;
import cn.itsuki.blog.entities.OperateState;
import cn.itsuki.blog.entities.requests.AdminSaveRequest;
import cn.itsuki.blog.entities.requests.AdminUpdatePasswordRequest;
import cn.itsuki.blog.entities.requests.BaseSearchRequest;
import cn.itsuki.blog.entities.requests.LoginRequest;
import cn.itsuki.blog.entities.responses.LoginResponse;
import cn.itsuki.blog.repositories.AdminRepository;
import cn.itsuki.blog.security.SecurityUtils;
import cn.itsuki.blog.security.TokenUtils;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 管理员 服务
 *
 * @author: itsuki
 * @create: 2021-09-15 19:57
 **/
@Service
public class AdminService extends BaseService<Admin, BaseSearchRequest> implements GraphQLMutationResolver {
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
        super("id", "id");
    }

    public LoginResponse login(LoginRequest request) {
        Admin probe = new Admin();
        probe.setUsername(request.getUsername());
        Optional<Admin> optionalAdmin = repository.findOne(Example.of(probe));

        if (optionalAdmin.isEmpty()) {
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
        response.setState(OperateState.OK);

        return response;
    }

    public Admin getCurrentAdmin() {
        Admin currentAdmin = SecurityUtils.getCurrentAdmin();
        if (currentAdmin == null) {
            throw new AccessDeniedException("请先登录");
        }
        return get(currentAdmin.getId());
    }

    public Admin save(AdminSaveRequest request) {
        Admin currentAdmin = getCurrentAdmin();

        currentAdmin.setNickname(request.getNickname());
        currentAdmin.setAvatar(request.getAvatar());
        currentAdmin.setDescription(request.getDescription());

        repository.saveAndFlush(currentAdmin);
        return currentAdmin;
    }

    public Admin updateAdmin(AdminSaveRequest request) {
        Admin currentAdmin = getCurrentAdmin();

        currentAdmin.setNickname(request.getNickname());
        currentAdmin.setAvatar(request.getAvatar());
        currentAdmin.setDescription(request.getDescription());

        repository.saveAndFlush(currentAdmin);
        return currentAdmin;
    }

    public Admin updateAdminPassword(AdminUpdatePasswordRequest request) {
        String password = request.getPassword();
        String newPassword = request.getNewPassword();
        String confirm = request.getConfirm();
        Admin currentAdmin = getCurrentAdmin();
        Admin probe = new Admin();
        BeanUtil.copyProperties(currentAdmin, probe);

        if (password.equals(newPassword)) {
            throw new IllegalArgumentException("新旧密码一样");
        }
        if (!newPassword.equals(confirm)) {
            throw new IllegalArgumentException("新密码确认密码错误");
        }
        // 密码验证
        if (!passwordEncoder.matches(password, currentAdmin.getPassword())) {
            throw new IllegalArgumentException("旧密码错误");
        }
        SecurityUtils.clearCurrentAdmin();
        // 设置加密后的新密码
        probe.setPassword(passwordEncoder.encode(newPassword));
        repository.saveAndFlush(probe);
        return probe;
    }

    public Admin updatePassword(AdminUpdatePasswordRequest request) {
        String password = request.getPassword();
        String newPassword = request.getNewPassword();
        String confirm = request.getConfirm();
        Admin currentAdmin = getCurrentAdmin();
        Admin probe = new Admin();
        BeanUtil.copyProperties(currentAdmin, probe);

        System.out.println(currentAdmin.toString());
        if (password.equals(newPassword)) {
            throw new IllegalArgumentException("新旧密码一样");
        }
        if (!newPassword.equals(confirm)) {
            throw new IllegalArgumentException("新密码确认密码错误");
        }
        // 密码验证
        if (!passwordEncoder.matches(password, currentAdmin.getPassword())) {
            throw new IllegalArgumentException("旧密码错误");
        }
        // 设置加密后的新密码
        probe.setPassword(passwordEncoder.encode(newPassword));
        repository.saveAndFlush(probe);
        return probe;
    }

    @Override
    protected Page<Admin> searchWithPageable(BaseSearchRequest criteria, Pageable pageable) {
        return null;
    }
}
