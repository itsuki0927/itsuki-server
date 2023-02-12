package cn.itsuki.blog.security;

import cn.itsuki.blog.entities.Member;
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

    public static Member getCurrentMember() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Member) {
            return (Member) principal;
        }
        return null;
    }

    public static void clearCurrentMember() {
        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();
    }
}
