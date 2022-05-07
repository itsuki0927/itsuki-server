package cn.itsuki.blog.entities.responses;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 将所有的响应体做一层包装
 *
 * @author: itsuki
 * @create: 2021-09-15 22:21
 **/
@Getter
@Setter
@ToString
public class WrapperResponse<T> {
    /**
     * 返回的数据
     */
    protected T data;
    /**
     * 状态码
     */
    protected int state;
    /**
     * 描述信息
     */
    protected String message;
    /**
     * 请求是否成功
     */
    protected boolean success;

    public WrapperResponse(T data, int state, String message, boolean success) {
        this.data = data;
        this.state = state;
        this.message = message;
        this.success = success;
    }

    public static <T> WrapperResponse<T> buildSuccess(T data, String message) {
        return new WrapperResponse<>(data, 200, message, true);
    }

    public static <T> WrapperResponse<T> buildFailed(int state, String message) {
        return new WrapperResponse<>(null, state, message, false);
    }

    public static <T> WrapperResponse<T> build(T data) {
        return buildSuccess(data, "请求成功");
    }

    public static <T> WrapperResponse<T> build(T data, String message) {
        return buildSuccess(data, message);
    }

    public static <T> WrapperResponse<T> failed(int state, String message) {
        return failed(state, message);
    }
}
