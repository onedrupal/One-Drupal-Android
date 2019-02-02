package com.prominentdev.blog.network;

import android.content.Context;

import com.google.gson.GsonBuilder;
import com.prominentdev.blog.helpers.SessionManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TipkrAPI {

    private static TipkrAPI ourInstance = new TipkrAPI();

    public static TipkrAPI getInstance() {
        return ourInstance;
    }

    private TipkrAPI() {
    }

    private static OkHttpClient okHttpClient;

    private TipkrService service;

    private Context appContext;

    private SessionManager sessionManager;

    public void init(Context context, String baseUrl, boolean debug) {

        this.appContext = context.getApplicationContext();
        sessionManager = new SessionManager(appContext);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(authHeader);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient = builder.build())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .build();
        service = retrofit.create(TipkrService.class);
    }

    public TipkrService getService() {
        return service;
    }

    private Interceptor authHeader = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request newRequest;
            Request original = chain.request();

            Request.Builder requestBuilder = original.newBuilder();


            if (sessionManager.isLoggedIn()) {
                requestBuilder.header("Google-Access-Token", sessionManager.getParticularField(SessionManager.ACCESS_TOKEN));
                requestBuilder.header("X-CSRF-Token", "");
            }

            //if app has been logged in
            //  all the following apis will be attached user ref id as header
            //if not
            //  all the following apis will be attached client generated UUID as ref id as header
            /*String refId = UserManager.getUserRefId(appContext);
            if (!TextUtils.isEmpty(refId)) {
                // authenticated user
                requestBuilder.header("in-uref", refId);
            } else {
                //guest mode
                requestBuilder.header("in-guref", Utils.getUUID(appContext));
            }

            //in-app-version
            String inAppVersionHeader = "g." + Utils.getVersionName(appContext);
            requestBuilder.header("in-app-version", inAppVersionHeader);*/

            newRequest = requestBuilder.build();
            return chain.proceed(newRequest);
        }
    };

    public static void cancel() {
        if (okHttpClient != null) {
            okHttpClient.dispatcher().cancelAll();
        }
    }
}
