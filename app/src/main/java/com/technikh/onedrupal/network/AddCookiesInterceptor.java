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
import com.technikh.onedrupal.authenticator.AuthPreferences;

import java.io.IOException;
import java.util.HashSet;
import java.util.prefs.Preferences;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This interceptor put all the Cookies in Preferences in the Request.
 * Your implementation on how to get the Preferences MAY VARY.
 * <p>
 * Created by tsuharesu on 4/1/15.
 */
public class AddCookiesInterceptor implements Interceptor {

    private Context context;
    private String TAG = "AddCookiesInterceptor";
    private SharedPreferences mSettings;
    public static final String PREF_COOKIES = "PREF_COOKIES";
    public static final String APP_PREFERENCES = "mysettings";
    /*public AddCookiesInterceptor(Context context) {
        this.context = context;
    }*/

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        //AuthPreferences mAuthPreferences = new AuthPreferences(context);
        //HashSet<String> preferences =
        context = MyApplication.getAppContext();
        mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        HashSet<String> preferences = (HashSet<String>) mSettings.getStringSet(PREF_COOKIES, new HashSet<String>());
        //HashSet<String> preferences = (HashSet) Preferences.getDefaultPreferences().getStringSet(Preferences.PREF_COOKIES, new HashSet<>());
        for (String cookie : preferences) {
            builder.addHeader("Cookie", cookie);
            Log.v(TAG, "Adding Header: " + cookie); // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
        }
        // X-CSRF-Token
        AuthPreferences mAuthPreferences = new AuthPreferences(context);
        Log.d(TAG, "intercept: mAuthPreferences.getAuthToken() "+mAuthPreferences.getAuthToken());
        builder.addHeader("X-CSRF-Token", mAuthPreferences.getAuthToken());

        return chain.proceed(builder.build());
    }
}