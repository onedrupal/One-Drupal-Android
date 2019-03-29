package com.technikh.onedrupal.helpers;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;


public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Sharedpref file name
    private static final String PREF_NAME = "BlogCredentials";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsUserLoggedIn";
    // All Shared Preferences Keys
    public static final String USER_EMAIL = "userEmail";
    public static final String USER_ID = "userId";
    public static final String USER_ID_TOKEN = "userIdToken";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String USER_NAME = "userName";
    public static final String USER_NOTIFICATIONS_ID = "gcm_id";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String user_email, String user_id, String user_name, String user_id_token, String access_token, String user_gcmid) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing user id in pref

        // Storing user name in pref
        editor.putString(USER_EMAIL, user_email);
        editor.putString(USER_ID, user_id);
        editor.putString(USER_NAME, user_name);
        editor.putString(USER_ID_TOKEN, user_id_token);
        editor.putString(ACCESS_TOKEN, access_token);
        editor.putString(USER_NOTIFICATIONS_ID, user_gcmid);

        // commit changes
        editor.commit();
    }

    public String getParticularField(String key) {
        return pref.getString(key, "");
    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(USER_ID, pref.getString(USER_ID, null));
        user.put(USER_NAME, pref.getString(USER_NAME, null));
        user.put(USER_EMAIL, pref.getString(USER_EMAIL, null));
        user.put(USER_ID_TOKEN, pref.getString(USER_ID_TOKEN, null));
        user.put(ACCESS_TOKEN, pref.getString(ACCESS_TOKEN, null));
        user.put(USER_NOTIFICATIONS_ID, pref.getString(USER_NOTIFICATIONS_ID, null));
        // return user
        return user;
    }

    public int notificationSubscribed() {
        return pref.getInt(USER_NOTIFICATIONS_ID, 0);
    }

    public void updateNotificationDetails(int notifications) {
        editor = pref.edit();
        // Storing user_notification_prefrence in pref
        editor.putInt(USER_NOTIFICATIONS_ID, notifications);

        editor.commit();
    }

    public void updateGCMID(String gcm_id) {
        editor = pref.edit();
        // Storing user_notification_prefrence in pref
        editor.putString(USER_NOTIFICATIONS_ID, gcm_id);

        editor.commit();
    }

    public void updateTripId(String trip_id) {
        editor = pref.edit();
        // Storing user_notification_prefrence in pref
        editor.putString(ACCESS_TOKEN, trip_id);

        editor.commit();
    }

    public void updateUserName(String username) {
        editor = pref.edit();
        // Storing user_notification_prefrence in pref
        editor.putString(USER_NAME, username);

        editor.commit();
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

		/*// After logout redirect user to Login Activity
        Intent i = new Intent(_context, LoginUserActivity.class);

		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// Staring Login Activity
		_context.startActivity(i);*/
    }

    public void finish_all_data_of_user() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
