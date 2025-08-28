package org.jeecg.modules.sms.utils;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @Author: KKKKK
 * @Date: 2025/8/13 12:23
 **/
public class BasicAuthInterceptor implements Interceptor {
    private final String username;
    private final String password;

    public BasicAuthInterceptor(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // 创建原始请求
        Request originalRequest = chain.request();

        // 创建包含认证信息的新请求
        Request authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", Credentials.basic(username, password))
                .build();

        // 继续处理带有认证信息的请求
        return chain.proceed(authenticatedRequest);
    }
}
