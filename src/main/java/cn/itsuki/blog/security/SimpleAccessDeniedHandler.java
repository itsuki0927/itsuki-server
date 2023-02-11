package cn.itsuki.blog.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author: itsuki
 * @create: 2021-09-14 21:45
 **/
@Component
public class SimpleAccessDeniedHandler implements AccessDeniedHandler {
    /**
     * 403 响应体
     */
    private static final String ERROR_MESSAGE = "{ \"message\": \"%s\" }";

    @Override
    public void handle(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, jakarta.servlet.ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getOutputStream().println(String.format(ERROR_MESSAGE, accessDeniedException.getMessage()));
    }
}
