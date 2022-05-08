package cn.itsuki.blog.security;

import cn.itsuki.blog.entities.Admin;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * security 工具类
 *
 * @author: itsuki
 * @create: 2021-09-15 20:12
 **/
public abstract class SecurityUtils {
    private SecurityUtils() {

    }

    public static Admin getCurrentAdmin() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Admin) {
            return (Admin) principal;
        }
        return null;
    }

    public static void clearCurrentAdmin() {
        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();
    }
}
