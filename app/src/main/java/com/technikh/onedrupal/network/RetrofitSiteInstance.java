package com.technikh.onedrupal.network;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.util.Log;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSiteInstance {

    private static Retrofit retrofit = null;
    private static String retrofitBASE_URL = null;
    private static String BASE_URL;
    private static String TAG = "RetrofitSiteInstance";

    private RetrofitSiteInstance(String protocol, String domain) {
        BASE_URL = protocol+domain+"/";
    }

    public static Retrofit getRetrofitSiteInstance(String protocol, String domain, GsonConverterFactory gson) {
        BASE_URL = protocol+domain+"/";
        Log.d(TAG, "getRetrofitSiteInstance: BASE_URL "+BASE_URL);
        if (retrofit == null || retrofitBASE_URL == null || !BASE_URL.equals(retrofitBASE_URL) || (gson != null)) {
            Log.d(TAG, "getRetrofitSiteInstance: retrofitBASE_URL"+retrofitBASE_URL);
            Log.d(TAG, "getRetrofitSiteInstance: BASE_URL"+BASE_URL);
            retrofitBASE_URL = BASE_URL;
            OkHttpClient okClient = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .addInterceptor(new AddCookiesInterceptor())
                    //.addInterceptor(new ReceivedCookiesInterceptor())
                    .build();
            //
            if(gson == null) {
                retrofit = new retrofit2.Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(okClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }else{
                Log.d(TAG, "getRetrofitSiteInstance: adding gson as not null");
                Log.d(TAG, "getRetrofitSiteInstance: gson"+gson.toString());
                retrofit = new retrofit2.Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(okClient)
                        .addConverterFactory(gson)
                        .build();
            }
        }
        return retrofit;
    }
}
