package cn.itsuki.blog.entities.responses;

import cn.itsuki.blog.entities.OperateState;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 登陆响应体
 *
 * @author: itsuki
 * @create: 2021-09-15 20:01
 **/
@Getter
@Setter
@ToString(exclude = {"token"})
public class LoginResponse {
    /**
     * 登陆状态： OK->登陆成功, FALSE->登陆失败
     */
    private OperateState status;
    /**
     * 生成的token
     */
    private String token;
    /**
     * token过期时间
     */
    private long expiration;
}
