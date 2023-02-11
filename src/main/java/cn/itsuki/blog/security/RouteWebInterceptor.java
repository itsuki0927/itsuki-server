package cn.itsuki.blog.security;

import cn.itsuki.blog.utils.RequestUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
public class RouteWebInterceptor implements WebGraphQlInterceptor {
    @Autowired
    private RequestUtil requestUtil;

    @NotNull
    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        request.configureExecutionInput((executionInput, builder) -> {
            Map<String, String> headers = request.getHeaders().toSingleValueMap();
            Map<Object, Object> context = new HashMap<>();

//            context.put("myheader", "myheader");
//            context.put("ip", "ip");

            return builder.graphQLContext(context).build();
        });

        return chain.next(request);
    }
}