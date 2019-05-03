package com.technikh.onedrupal.network;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.technikh.onedrupal.authenticator.AuthPreferences;

/**
 * Created by MG on 04-03-2018.
 */

public class ApiCall {
    private static ApiCall mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    public ApiCall(Context ctx) {
        mCtx = ctx;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized ApiCall getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ApiCall(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public static void make(Context ctx, String vid, String query, Response.Listener<String>
            listener, Response.ErrorListener errorListener) {
        //String url = "https://itunes.apple.com/search?term=" + query + "&country=US";
        AuthPreferences mAuthPreferences = new AuthPreferences(ctx);
        // http://nikhil.dubbaka.com/onedrupal/api/v1/vocabulary-titles/categories?name=Or
        String url = mAuthPreferences.getPrimarySiteProtocol() + mAuthPreferences.getPrimarySiteUrl()+"/onedrupal/api/v1/vocabulary-titles/"+vid+"?name="+query;
        Log.d("ApiCall", "make: query "+query);
        Log.d("ApiCall", "make: url "+url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener, errorListener);
        ApiCall.getInstance(ctx).addToRequestQueue(stringRequest);
    }
}
