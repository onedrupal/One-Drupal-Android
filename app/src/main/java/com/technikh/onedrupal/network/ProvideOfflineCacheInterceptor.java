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

public class ProvideOfflineCacheInterceptor implements Interceptor {
    private String TAG = "ProvideOfflineCacheInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Log.d(TAG, "intercept: provideOfflineCacheInterceptor");
        try {
            return chain.proceed(chain.request());
        } catch (Exception e) {


            CacheControl cacheControl = new CacheControl.Builder()
                    .onlyIfCached()
                    .maxStale(1, TimeUnit.DAYS)
                    .build();

            Request offlineRequest = chain.request().newBuilder()
                    .cacheControl(cacheControl)
                    .build();
            return chain.proceed(offlineRequest);
        }
    }
}