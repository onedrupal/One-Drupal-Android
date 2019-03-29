package com.technikh.onedrupal.authenticator;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class AuthPreferences {

	private static final String PREFS_NAME = "auth";
	private static final String KEY_ACCOUNT_NAME = "account_name";
	private static final String KEY_AUTH_TOKEN = "auth_token";
	private static final String KEY_PRI_SITE_URL = "primary_site_url";
    private static final String KEY_PRI_SITE_PROTOCOL = "primary_site_protocol";
	private String TAG = "AuthPreferences";
	
	private SharedPreferences preferences;
	
	public AuthPreferences(Context context) {
		preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	}
	
	public String getAccountName() {
		return preferences.getString(KEY_ACCOUNT_NAME, null);
	}

    public String getPrimarySiteUrl() {
		Log.d(TAG, "getPrimarySiteUrl: "+preferences.getString(KEY_PRI_SITE_URL, null));
		return preferences.getString(KEY_PRI_SITE_URL, null);
    }
    public String getPrimarySiteProtocol() {
        return preferences.getString(KEY_PRI_SITE_PROTOCOL, null);
    }
	
	public String getAuthToken() {
		return preferences.getString(KEY_AUTH_TOKEN, null);
	}
	
	public void setUsername(String accountName) {
		final Editor editor = preferences.edit();
		editor.putString(KEY_ACCOUNT_NAME, accountName);
		editor.commit();
	}

    public void setPrimarySiteUrl(String accountName) {
        final Editor editor = preferences.edit();
        editor.putString(KEY_PRI_SITE_URL, accountName);
        editor.commit();
    }
    public void setPrimarySiteProtocol(String accountName) {
        final Editor editor = preferences.edit();
        editor.putString(KEY_PRI_SITE_PROTOCOL, accountName);
        editor.commit();
    }
	
	public void setAuthToken(String authToken) {
		final Editor editor = preferences.edit();
		editor.putString(KEY_AUTH_TOKEN, authToken);
		editor.commit();
	}
	
}
