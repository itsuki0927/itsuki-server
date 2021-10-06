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
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Admin) {
            return (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return null;
    }
}
