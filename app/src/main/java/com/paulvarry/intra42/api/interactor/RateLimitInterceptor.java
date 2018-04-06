package com.paulvarry.intra42.api.interactor;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RateLimitInterceptor implements Interceptor {

    private static boolean isRateLimitException(Response ret) {
        return ret.code() == 429;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        //Build new request
        Request.Builder builder = request.newBuilder();

        request = builder.build(); //overwrite old request
        Response response = chain.proceed(request); //perform request, here original request will be executed

        if (isRateLimitException(response)) {

            int i = 1;
            while (i < 10) {

                Log.i("API Rate-Limit", "Exceeded, try again " + String.valueOf(i));

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                request = builder.build();
                Response ret = chain.proceed(request);
                if (!isRateLimitException(ret)) {
                    Log.i("API Rate-Limit", "success");
                    return ret;
                }
                i++;
                Log.i("API Rate-Limit", "failed");
            }
        }

        return response;
    }
}
