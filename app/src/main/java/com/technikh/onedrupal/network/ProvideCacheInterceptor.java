package com.technikh.onedrupal.network;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ProvideCacheInterceptor implements Interceptor {
    private String TAG = "ProvideCacheInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response originalResponse = chain.proceed(request);
        String cacheControl = originalResponse.header("Cache-Control");

        if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                cacheControl.contains("must-revalidate") || cacheControl.contains("max-stale=0")) {
            //Log.d(TAG, "intercept: if chain "+cacheControl);
            if(true)
                return originalResponse;
            CacheControl cc = new CacheControl.Builder()
                    .maxStale(1, TimeUnit.DAYS)
                    .build();



            request = request.newBuilder()
                    .cacheControl(cc)
                    .build();
            return originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=" + 5000)
                    .build();
            //return chain.proceed(request);

        } else {
            Log.d(TAG, "intercept: else originalResponse");
            return originalResponse;
        }
    }
}