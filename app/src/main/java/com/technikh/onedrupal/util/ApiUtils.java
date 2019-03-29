package com.technikh.onedrupal.util;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

import com.technikh.onedrupal.authenticator.AuthPreferences;
import com.technikh.onedrupal.provider.IServerAuthenticator;
import com.technikh.onedrupal.provider.MyServerAuthenticator;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.technikh.onedrupal.app.MyApplication.getAppContext;

public class ApiUtils {

    private static String TAG = "ApiUtils";

	public static String callDrupalUserUnAuthenticatedGetApiSync(String urlSlug) {
	    // http://one-drupal-demo.technikh.com/onedrupal/api/v1/settings
        AuthPreferences mAuthPreferences;
        mAuthPreferences = new AuthPreferences(getAppContext());
        String site_domain = mAuthPreferences.getPrimarySiteUrl();
        String site_protocol = mAuthPreferences.getPrimarySiteProtocol();
		String newUrl = site_protocol+site_domain + urlSlug;
		Log.d(TAG, "requestNewsList: "+newUrl);
		File httpCacheDirectory = new File(getAppContext().getCacheDir(), "offlineCache");
		HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
		httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

		//10 MB
		Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
		OkHttpClient client = new OkHttpClient.Builder()
				//.cache(cache)
				//.addInterceptor(httpLoggingInterceptor)
				//.addNetworkInterceptor(provideCacheInterceptor())
				//.addInterceptor(provideOfflineCacheInterceptor())
				.build();
		okhttp3.Request request;
        try {
            request = new okhttp3.Request.Builder().url(newUrl).build();
            Request request1 = new Request.Builder()
                    .url(newUrl)
                    .build();
            OkHttpClient client1 = new OkHttpClient();
            Response response = client1.newCall(request1).execute();
            //okhttp3.Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.d(TAG, "callDrupalUserUnAuthenticatedGetApiSync: "+response.body().string());
                return response.body().string();
            }else{
                response.body().close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
	}

    private static Interceptor provideCacheInterceptor() {

        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response originalResponse = chain.proceed(request);
                String cacheControl = originalResponse.header("Cache-Control");

                if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                        cacheControl.contains("must-revalidate") || cacheControl.contains("max-stale=0")) {
                    //Log.d(TAG, "intercept: if chain "+cacheControl);

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
        };

    }


    private static Interceptor provideOfflineCacheInterceptor() {

        return new Interceptor() {
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
        };
    }
}
