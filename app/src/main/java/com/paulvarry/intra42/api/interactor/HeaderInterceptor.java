package com.paulvarry.intra42.api.interactor;

import androidx.annotation.NonNull;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.AccessToken;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class HeaderInterceptor implements Interceptor {

    private static String HEADER_KEY_USER_AGENT = "User-Agent";
    private static String HEADER_KEY_ACCEPT = "Accept";
    private static String HEADER_KEY_API_AUTH = "Authorization";
    private static String HEADER_VALUE_ACCEPT = "application/json";
    private static String HEADER_CONTENT_TYPE = "Content-type";

    private AccessToken accessToken;
    private com.paulvarry.intra42.api.tools42.AccessToken accessToken42Tools;

    public HeaderInterceptor() {

    }

    public HeaderInterceptor(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public HeaderInterceptor(com.paulvarry.intra42.api.tools42.AccessToken accessToken42Tools) {
        this.accessToken42Tools = accessToken42Tools;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder()
                .header(HEADER_KEY_ACCEPT, HEADER_VALUE_ACCEPT)
                .header(HEADER_CONTENT_TYPE, HEADER_VALUE_ACCEPT)
                .header(HEADER_KEY_USER_AGENT, ServiceGenerator.getUserAgent());
        if (accessToken != null)
            requestBuilder.header(HEADER_KEY_API_AUTH, accessToken.tokenType + " " + accessToken.accessToken);
        else if (accessToken42Tools != null)
            requestBuilder.header(HEADER_KEY_API_AUTH, "Bearer " + accessToken42Tools.accessToken);
        requestBuilder.method(original.method(), original.body());

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
