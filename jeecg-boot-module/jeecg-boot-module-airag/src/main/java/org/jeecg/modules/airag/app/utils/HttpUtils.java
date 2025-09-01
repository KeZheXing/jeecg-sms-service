package org.jeecg.modules.airag.app.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: KKKKK
 * @Date: 2025/8/27 2:53
 **/
@Slf4j
public class HttpUtils {

    public static String doPostByAuth(String url,String json,String userName,String password) {
        log.info("POST请求:{}",json);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(userName, password))
                .build();

        OkHttpClient.Builder builder = okHttpClient.newBuilder();
        OkHttpClient client = builder.connectTimeout(5, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            String string = response.body().string();
            log.info("请求uRL:{} \n 内容：{} 响应:{} {}",url,json,string,response.code());
            if (response.code()!=200 && response.code()!=201 && response.code()!=202){
                throw new RuntimeException("请求失败");
            }

            return string;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
