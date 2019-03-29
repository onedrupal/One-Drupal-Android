package com.technikh.onedrupal.network;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.technikh.onedrupal.app.MyApplication;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This Interceptor add all received Cookies to the app DefaultPreferences.
 * Your implementation on how to save the Cookies on the Preferences MAY VARY.
 * <p>
 * Created by tsuharesu on 4/1/15.
 */
public class ReceivedCookiesInterceptor implements Interceptor {
    private Context context;
    public static final String APP_PREFERENCES = "mysettings";
    private SharedPreferences mSettings;
    private String TAG = "ReceivedCookiesInterceptor";
    @Override
    public Response intercept(Chain chain) throws IOException {
        context = MyApplication.getAppContext();
        Response originalResponse = chain.proceed(chain.request());
        mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = (HashSet<String>) mSettings.getStringSet("PREF_COOKIES", new HashSet<String>());

            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }
            Log.d(TAG, "intercept: "+cookies.toString());

            SharedPreferences.Editor memes = mSettings.edit();
            memes.putStringSet("PREF_COOKIES", cookies).apply();
            memes.commit();
        }

        return originalResponse;
    }
}