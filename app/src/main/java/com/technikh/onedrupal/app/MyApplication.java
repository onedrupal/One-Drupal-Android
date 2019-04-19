package com.technikh.onedrupal.app;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.technikh.onedrupal.models.BreadcumModel;
import com.technikh.onedrupal.models.OneGlobalSettingsSectionModel;
import com.technikh.onedrupal.models.SettingsType;
import com.technikh.onedrupal.models.SettingsType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyApplication extends MultiDexApplication {

    private static Context context;
    private static String TAG = "MyApplication";
    public static List<SettingsType> gblNodeTypeSettings = new ArrayList<SettingsType>();
    public static OneGlobalSettingsSectionModel gblSettingsSection;

    public static List<BreadcumModel>  breadcumList = new ArrayList<>();


    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    public static boolean gblIsValidNodeType(String node_type) {
        Log.d(TAG, "gblIsValidNodeType: "+gblNodeTypeSettings.size());
        SettingsType NodeTypeObj = gblGetNodeTypeObj(node_type);
        if(NodeTypeObj != null) {
            return true;
        }
        return false;
    }

    public static SettingsType gblGetNodeTypeObj(String node_type) {
        for (int j = 0; j < gblNodeTypeSettings.size(); j++) {
            SettingsType modelNodeType = gblNodeTypeSettings.get(j);
            Log.d(TAG, "gblGetNodeTypeObj: search node_type "+node_type+" modelNodeType.getNodeType() "+modelNodeType.getNodeType());
            //Log.d(TAG, "gblGetBodyFieldName: for"+modelNodeType.getBodyFieldName());
            if(modelNodeType.getNodeType().equals(node_type)){
                return modelNodeType;
            }
        }
        return null;
    }

    public static String gblGetBodyFieldName(String node_type) {
        Log.d(TAG, "gblGetBodyFieldName: "+gblNodeTypeSettings.size());
        SettingsType NodeTypeObj = gblGetNodeTypeObj(node_type);
        if(NodeTypeObj != null) {
            Log.d(TAG, "gblGetBodyFieldName: in if");
            if(NodeTypeObj.getFieldsList() != null)
                return NodeTypeObj.getFieldsList().getFieldBody();
        }
        return null;
    }

    public static int gblGetNumberOfNodeTypes() {
        List<String> postTypeList = new ArrayList<String>();
        for (int j = 0; j < MyApplication.gblNodeTypeSettings.size(); j++) {
            SettingsType modelNodeType = MyApplication.gblNodeTypeSettings.get(j);
            Log.d(TAG, "onClick: "+modelNodeType.getNodeType());
            if(modelNodeType.userHasAccesstoCreate()){
                postTypeList.add(modelNodeType.getNodeType());
            }
        }
        return postTypeList.size();
    }

    public static SettingsType gblGetNodeTypeFromPosition(int pos) {
        if(gblNodeTypeSettings != null && gblNodeTypeSettings.size() > 0) {
            SettingsType modelNodeType = gblNodeTypeSettings.get(pos);
            return modelNodeType;
        }
        return null;
    }

    public static String gblGetImageFieldName(String node_type) {
        SettingsType NodeTypeObj = gblGetNodeTypeObj(node_type);
        if(NodeTypeObj != null) {
            if(NodeTypeObj.getFieldsList() != null)
                return NodeTypeObj.getFieldsList().getFieldImage();
        }
        return null;
    }

    public static boolean gblUserHasAccesstoCreate(String node_type) {
        SettingsType NodeTypeObj = gblGetNodeTypeObj(node_type);
        if(NodeTypeObj.userHasAccesstoCreate()) {
            return true;
        }
        return false;
    }
}
