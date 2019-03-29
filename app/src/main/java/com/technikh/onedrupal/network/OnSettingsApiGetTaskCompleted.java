package com.technikh.onedrupal.network;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Intent;
import android.util.Log;

import com.technikh.onedrupal.activities.ActivityDashboard;
import com.technikh.onedrupal.activities.SiteContentTabsActivity;
import com.technikh.onedrupal.app.MyApplication;
import com.technikh.onedrupal.models.ModelNodeType;
import com.technikh.onedrupal.models.SettingsType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OnSettingsApiGetTaskCompleted implements OnApiGetTaskCompleted{
    private String TAG = "OnSettingsApiGetTaskCompleted";
    @Override
    public void onTaskCompleted(String responseStr) {
        // do something with result here!
        //Log.d(TAG, "onTaskCompleted: "+responseStr);
        Log.d(TAG, "fetchSettingsAPI: responseStr "+responseStr);
        if(responseStr == null){
            return;
        }
        try {
            JSONObject responseBodyObj = new JSONObject(responseStr);
            if (responseBodyObj.has("types")) {
                Log.d(TAG, "fetchSettingsAPI: in types");
                JSONArray ja = responseBodyObj.getJSONArray("types");
                MyApplication.gblNodeTypeSettings.clear();
                for (int j = 0; j < ja.length(); j++) {
                    Log.d(TAG, "fetchSettingsAPI: in for");
                    JSONObject jo = (JSONObject) ja.get(j);
                    //SettingsType modelNodeType = new SettingsType(jo);
                    //MyApplication.gblNodeTypeSettings.add(modelNodeType);
                }
                Intent intent1 = new Intent(MyApplication.getAppContext(), SiteContentTabsActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getAppContext().startActivity(intent1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}