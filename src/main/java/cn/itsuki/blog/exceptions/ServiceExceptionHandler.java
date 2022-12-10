package cn.itsuki.blog.exceptions;

import cn.itsuki.blog.entities.responses.WrapperResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import graphql.GraphQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 异常处理
 *
 * @author: itsuki
 * @create: 2021-09-14 19:32
 **/
@RestControllerAdvice
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {
    /**
     * 信息源
     */
    @Autowired
    private MessageSource messageSource;

    /**
     * 非法参数异常
     *
     * @param ex 异常
     * @return 错误实体
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * @param ex
     * @return
     */
    @ExceptionHandler(MultipartException.class)
    @ResponseBody
    public ResponseEntity<Object> handleMultipartException(MultipartException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * 404
     *
     * @param ex 异常
     * @return 错误实体
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * 实体已存在错误
     *
     * @param ex 异常
     * @return 错误实体
     */
    @ExceptionHandler(EntityExistsException.class)
    @ResponseBody
    public ResponseEntity<Object> handleEntityExistException(EntityExistsException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * 处理凭证错误
     *
     * @param ex 异常
     * @return 错误实体
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    /**
     * 处理拒绝访问异常
     *
     * @param ex 异常
     * @return 错误实体
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    /**
     * 处理graphql异常
     *
     * @param e 异常
     * @return 错误实体
     */
    @ExceptionHandler(GraphQLException.class)
    @ResponseBody
    public ResponseEntity<Object> handleGraphqlException(GraphQLException e) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    /**
     * 处理其他异常
     *
     * @param throwable 异常
     * @return 错误实体
     */
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity<Object> handleOtherException(Throwable throwable) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    /**
     * 自定义所有异常类型的响应正文的统一位置
     *
     * @param ex      异常
     * @param body    响应体
     * @param headers 响应头
     * @param status  响应码
     * @param request 当前请求
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = ex.getMessage();

        if (ex instanceof HttpMessageNotReadableException) {
            message = "Request body is missing or invalid";
            if (ex.getCause() != null && ex.getCause() instanceof JsonMappingException && ex.getCause().getCause() != null && ex.getCause().getCause() instanceof IllegalArgumentException) {
                message = ex.getCause().getCause().getMessage();
            }
        } else if (ex instanceof MethodArgumentNotValidException) {
            message = convertErrorsToMessage(((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors());
        } else if (ex instanceof BindException) {
            message = convertErrorsToMessage(((BindException) ex).getBindingResult().getAllErrors());
        } else if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            message = "Internal server error";
        }

        return buildErrorResponse(status, message);
    }

    /**
     * 错误信息转换为字符串
     *
     * @param objectErrors 错误对象列表
     * @return 字符串
     */
    private String convertErrorsToMessage(List<ObjectError> objectErrors) {
        List<String> messages = new ArrayList<>();

        for (ObjectError objectError : objectErrors) {
            messages.add(messageSource.getMessage(objectError, null));
        }

        return StringUtils.collectionToDelimitedString(messages, ", ");
    }

    /**
     * 构建错误响应
     *
     * @param status  响应码
     * @param message 信息
     * @return 包含响应码和信息的响应体
     */
    private static ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", message);
        WrapperResponse<Object> objectWrapperResponse = WrapperResponse.buildFailed(status.value(), message);

        return new ResponseEntity<>((Object) objectWrapperResponse, status);
    }
}
