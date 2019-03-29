package com.technikh.onedrupal.network;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.os.AsyncTask;
import android.util.Log;


import com.technikh.onedrupal.activities.ActivityAuthentication;
import com.technikh.onedrupal.authenticator.AuthPreferences;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.technikh.onedrupal.app.MyApplication.getAppContext;

public class DrupalAPI extends AsyncTask<String, String, String> {
    private OnSettingsApiGetTaskCompleted listener;
    private static String TAG = "DrupalAPI";
    private String result;
    public DrupalAPI(OnSettingsApiGetTaskCompleted listener){
        this.listener=listener;
    }

    @Override
    protected String doInBackground(String... urlSlugs) {
        AuthPreferences mAuthPreferences;
        mAuthPreferences = new AuthPreferences(getAppContext());
        String site_domain = mAuthPreferences.getPrimarySiteUrl();
        String site_protocol = mAuthPreferences.getPrimarySiteProtocol();
        String newUrl = site_protocol+site_domain + urlSlugs[0];
        Log.d(TAG, "doInBackground: "+newUrl+" getAuthToken "+mAuthPreferences.getAuthToken());
        try {
            Request request1 = new Request.Builder()
                    .addHeader("X-CSRF-Token", mAuthPreferences.getAuthToken())
                    .url(newUrl)
                    .build();
            OkHttpClient client1 = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .addInterceptor(new AddCookiesInterceptor())
                    //.addInterceptor(new ReceivedCookiesInterceptor())
                    .build();
            //OkHttpClient client1 = new OkHttpClient();
            Response response = client1.newCall(request1).execute();
            if (response.isSuccessful()) {
                String responseStr = response.body().string();
                Log.d(TAG, "callDrupalUserUnAuthenticatedGetApiSync: "+responseStr);
                this.result = responseStr;
                return responseStr;
            }else{
                response.body().close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        this.result =  null;
        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
        // update progress here
    }
    // called after doInBackground finishes
    @Override
    protected void onPostExecute(String result1) {
        Log.v("result, yay!", "res "+this.result+" result1 "+result1);
        listener.onTaskCompleted(result1);
    }
}
