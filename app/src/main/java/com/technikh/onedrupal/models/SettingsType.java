package com.technikh.onedrupal.models;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsType {

    @SerializedName("node_type")
    private String node_type="";
  /*  private String body="";
    private String field_image="";*/
    private String TAG = "ModelNodeType";
    @SerializedName("access_create")
    private boolean userAccesstoCreate;
    @SerializedName("fields")
    private nodeFields mFieldsList;

/*
    public SettingsType(JSONObject jo) {
        try {
            this.node_type = jo.getString("node_type");
            this.userAccesstoCreate = jo.getBoolean("access_create");
            Log.d(TAG, "ModelNodeType: node_type "+this.node_type);
            if (jo.has("fields") && !jo.isNull("fields")){
                JSONObject fieldsObj = jo.getJSONObject("fields");
                if (fieldsObj.has("image") && !fieldsObj.isNull("image")){
                    this.field_image = fieldsObj.getString("image");
                }
                if (fieldsObj.has("body") && !fieldsObj.isNull("body")){
                    this.body = fieldsObj.getString("body");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
*/
    public nodeFields getFieldsList() {
        return mFieldsList;
    }

    public void setFieldsList(nodeFields fieldsList) {
        this.mFieldsList = fieldsList;
    }
    /*
    public String getImageFieldName() {
        return this.field_image;
    }

    public String getBodyFieldName() {
        return this.body;
    }
*/
    public String getNodeType() {
        return this.node_type;
    }

    public boolean userHasAccesstoCreate() {
        return this.userAccesstoCreate;
    }

    @Override
    public String toString() {
        Log.d(TAG, "toString: "+this.node_type);
        return this.node_type;
    }

    @Override
    public boolean equals(Object obj) {
        Log.d(TAG, "equals: ");
        if(obj instanceof SettingsType){
            SettingsType c = (SettingsType)obj;
            if(c.getNodeType().equals(this.node_type) ) return true;
        }

        return false;
    }
}