package com.paulvarry.intra42.api.interactor;

import com.paulvarry.intra42.utils.Analytics;

import java.io.IOException;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AnalyticsInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        Analytics.apiCall(request, response);

        return response;
    }
}
