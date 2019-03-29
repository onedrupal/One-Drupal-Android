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

import java.util.ArrayList;

public class ModelNodeType {

    @SerializedName("type")
    public ArrayList<fieldTargetIdValue> nodeType;
    @SerializedName("status")
    private ArrayList<fieldBooleanValue> isPublished;
    @SerializedName("promote")
    private ArrayList<fieldBooleanValue> isPromoted;
    @SerializedName("nid")
    public ArrayList<fieldIntValue> nid;
    @SerializedName("title")
    public ArrayList<fieldStringValue> title;

    //InvalidArgumentException: Field TAG is unknown. in Drupal\Core\Entity\ContentEntityBase->getTranslatedField() (line 586 of /opt/bitnami/apps/drupal/htdocs/core/lib/Drupal/Core/Entity/ContentEntityBase.php).
    //private String TAG = "ModelNodeType";

    public boolean isPublished() {
        return isPublished.get(0).getValue();
    }

    public int getInt(ArrayList<fieldIntValue> int_i) {
        return int_i.get(0).getValue();
    }
    public String getString(ArrayList<fieldStringValue> i) {
        return i.get(0).getValue();
    }
    public String getTargetId(ArrayList<fieldTargetIdValue> i) {
        return i.get(0).getValue();
    }

    public boolean isPromoted() {
        return isPromoted.get(0).getValue();
    }

    public void setPublished(boolean status) {
        fieldBooleanValue fbValue = new fieldBooleanValue();
        fbValue.setValue(status);
        isPublished = new ArrayList<fieldBooleanValue>();
        isPublished.add(fbValue);
    }

    public void setPromoted(boolean status) {
        fieldBooleanValue fbValue = new fieldBooleanValue();
        fbValue.setValue(status);
        isPromoted = new ArrayList<fieldBooleanValue>();
        isPromoted.add(fbValue);
    }

    public void setNodeType(String type) {
        fieldTargetIdValue fbValue = new fieldTargetIdValue();
        fbValue.setValue(type);
        nodeType = new ArrayList<fieldTargetIdValue>();
        nodeType.add(fbValue);
    }


}