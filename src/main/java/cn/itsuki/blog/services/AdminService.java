package cn.itsuki.blog.services;

import cn.itsuki.blog.entities.Admin;
import cn.itsuki.blog.entities.OperateState;
import cn.itsuki.blog.entities.requests.AdminSaveRequest;
import cn.itsuki.blog.entities.requests.BaseSearchRequest;
import cn.itsuki.blog.entities.requests.LoginRequest;
import cn.itsuki.blog.entities.responses.LoginResponse;
import cn.itsuki.blog.repositories.AdminRepository;
import cn.itsuki.blog.security.SecurityUtils;
import cn.itsuki.blog.security.TokenUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class AdminService extends BaseService<Admin, BaseSearchRequest> {
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

    public Admin getCurrentAdmin() {
        Admin admin = get(SecurityUtils.getCurrentAdmin().getId());
        admin.setPassword(null);
        return admin;
    }

    private void updatePassword(AdminSaveRequest request) {
        String password = request.getPassword();
        String newPassword = request.getNewPassword();
        String confirm = request.getConfirm();
        Admin currentAdmin = ensureExist(repository, SecurityUtils.getCurrentAdmin().getId(), "admin");

        // 参数验证
        if (password != null && newPassword != null && confirm != null) {
            if (password.equals(newPassword) || !newPassword.equals(confirm)) {
                throw new IllegalArgumentException("新旧密码一样");
            }
            // 密码验证
            if (!passwordEncoder.matches(currentAdmin.getPassword(), password)) {
                throw new IllegalArgumentException("旧密码错误");
            }
            // 设置加密后的新密码
            request.setPassword(passwordEncoder.encode(newPassword));
        } else {
            // 如果没有更新，则使用原密码
            request.setPassword(passwordEncoder.encode(password));
        }
    }

    public Admin save(AdminSaveRequest request) {
        Admin currentAdmin = getCurrentAdmin();
        Admin admin = new Admin();

        updatePassword(request);

        BeanUtils.copyProperties(request, admin);
        admin.setId(currentAdmin.getId());
        admin.setRole(currentAdmin.getRole());
        admin.setUsername(currentAdmin.getUsername());

        repository.save(admin);

        return admin;
    }

    @Override
    protected Page<Admin> searchWithPageable(BaseSearchRequest criteria, Pageable pageable) {
        return null;
    }
}
