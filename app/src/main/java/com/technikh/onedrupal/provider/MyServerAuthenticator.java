package com.technikh.onedrupal.provider;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.util.Log;
import android.widget.Toast;

import com.technikh.onedrupal.activities.SiteLoginActivity;
import com.technikh.onedrupal.authenticator.AuthPreferences;
import com.technikh.onedrupal.network.AddCookiesInterceptor;
import com.technikh.onedrupal.network.ReceivedCookiesInterceptor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyServerAuthenticator implements IServerAuthenticator {

	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private String TAG = "MyServerAuthenticator";

	@Override
	public String signUp(String email, String username, String password) {
		// TODO: register new user on the server and return its auth token
		return null;
	}

	@Override
	public String signIn(String site_domain, String username, String password) {
        return callDrupalSiteLoginAPI(site_domain, username, password);
	}


	private String callDrupalSiteLoginAPI(String site_domain, String username, String password) {
		// http://one-drupal-demo.technikh.com/user/login?_format=json
		String api_url = site_domain + "/user/login?_format=json";
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("name", username);
			jsonObject.put("pass", password);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		MediaType JSON
				= MediaType.parse("application/json; charset=utf-8");
		RequestBody api_body = RequestBody.create(JSON, jsonObject.toString());
		//OkHttpClient client = new OkHttpClient();
		//client.interceptors().add(new AddCookiesInterceptor());
        //client.interceptors().add(new ReceivedCookiesInterceptor());
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
              //  .addInterceptor(new AddCookiesInterceptor())
                .addInterceptor(new ReceivedCookiesInterceptor())
                .build();

        try {
		Request request = new Request.Builder()
				.header("Content-Type", "application/json")
				.url(api_url)
				.post(api_body)
				.build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                JSONObject responseBodyObj = new JSONObject(response.body().string());
                /*
                {
    "current_user": {
        "uid": "2",
        "name": "demo1"
    },
    "csrf_token": "Smto-mdbbAc7N2yZgRpQiZaNaDz03n2Cb3i7kWCPwX0",
    "logout_token": "oGBiMtDdXqsZC2Q-LdeeOH3YdBMi4QwLdOnhtIp9dD8"
}
                 */
                if(responseBodyObj.has("csrf_token")) {
                    Log.d(TAG, "callDrupalSiteLoginAPI: "+responseBodyObj.getString("csrf_token"));
                    return responseBodyObj.getString("csrf_token");
                }
            }
        }catch (IOException e){
		    e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
		/*client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(SiteLoginActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (response.isSuccessful()) {
							Toast.makeText(SiteLoginActivity.this, "API call success!", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(SiteLoginActivity.this, "API call failed.", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});*/
        return null;
	}

}
