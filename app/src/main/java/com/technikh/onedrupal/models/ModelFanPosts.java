package com.technikh.onedrupal.models;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.technikh.onedrupal.app.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelFanPosts implements Parcelable {

    private String nid;
    private String title;
    private String body="";
    private String node_type="";
    private String field_image="";
    public String field_video="", field_remote_page="";
    private String field_video_thumbnail="";
    private String field_text_category="";
    private boolean isValidNodeType = false;

    private boolean isPublished = false;
    private boolean isPromoted = false;

   /* @SerializedName("status")
    private ArrayList<fieldBooleanValue> isPublished;
    @SerializedName("promote")
    private ArrayList<fieldBooleanValue> isPromoted;
*/
    private static String TAG = "ModelFanPosts";

    public ModelFanPosts(JSONObject jo) {
        try {
            node_type = jo.getJSONArray("type").getJSONObject(0).getString("target_id");
            SettingsType NodeTypeObj = MyApplication.gblGetNodeTypeObj(node_type);
            this.isPublished = jo.getJSONArray("status").getJSONObject(0).getBoolean("value");
            this.isPromoted = jo.getJSONArray("promote").getJSONObject(0).getBoolean("value");

            this.nid = jo.getJSONArray("nid").getJSONObject(0).getString("value");
            this.title = jo.getJSONArray("title").getJSONObject(0).getString("value");

            this.isValidNodeType = MyApplication.gblIsValidNodeType(node_type);
            if(this.isValidNodeType) {
                String bodyField = MyApplication.gblGetBodyFieldName(node_type);
                if (jo.has(bodyField) && !jo.isNull(bodyField) && (jo.getJSONArray(bodyField).length() > 0)) {
                    this.body = jo.getJSONArray(bodyField).getJSONObject(0).getString("value");
                }
                String imageField = MyApplication.gblGetImageFieldName(node_type);
                String remoteImageField = NodeTypeObj.getFieldsList().remote_image;
                Log.d(TAG, "ModelFanPosts: imageField" + imageField);
                if (jo.has(imageField) && !jo.isNull(imageField) && (jo.getJSONArray(imageField).length() > 0)) {
                    this.field_image = jo.getJSONArray(imageField).getJSONObject(0).getString("url");
                } else if (jo.has(remoteImageField) && !jo.isNull(remoteImageField) && (jo.getJSONArray(remoteImageField).length() > 0)) {
                    this.field_image = jo.getJSONArray(remoteImageField).getJSONObject(0).getString("uri");
                }
                String remoteVideoField = NodeTypeObj.getFieldsList().remote_video;
                if (jo.has(remoteVideoField) && !jo.isNull(remoteVideoField) && (jo.getJSONArray(remoteVideoField).length() > 0)) {
                    this.field_video = jo.getJSONArray(remoteVideoField).getJSONObject(0).getString("value");
                    if (this.field_video.contains("youtu.be")) {
                        String youtubeID = extractYTId(this.field_video);
                        this.field_image = "https://i3.ytimg.com/vi/" + youtubeID + "/hqdefault.jpg";
                    }
                }
                String remotePageField = NodeTypeObj.getFieldsList().remote_page;
                if (jo.has(remotePageField) && !jo.isNull(remotePageField) && (jo.getJSONArray(remotePageField).length() > 0)) {
                    this.field_remote_page = jo.getJSONArray(remotePageField).getJSONObject(0).getString("uri");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String extractYTId(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()){
            vId = matcher.group(1);
        }
        return vId;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getTitle() {
        return title;
    }

    public String getNodeType() {
        return node_type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getField_image() {
        return field_image;
    }

    public boolean isValidNodeType() {
        return isValidNodeType;
    }
    public boolean isPublished() {
        return isPublished;
        //return isPublished.get(0).getValue();
    }
    public boolean isPromoted() {
        return isPromoted;
        //return isPromoted.get(0).getValue();
    }

    public void setField_image(String field_image) {
        this.field_image = field_image;
    }

    public String getField_text_category() {
        return field_text_category;
    }

    public void setField_text_category(String field_text_category) {
        this.field_text_category = field_text_category;
    }

    protected ModelFanPosts(Parcel in) {
        nid = in.readString();
        title = in.readString();
        body = in.readString();
        field_image = in.readString();
        field_text_category = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nid);
        dest.writeString(title);
        dest.writeString(body);
        dest.writeString(field_image);
        dest.writeString(field_text_category);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ModelFanPosts> CREATOR = new Parcelable.Creator<ModelFanPosts>() {
        @Override
        public ModelFanPosts createFromParcel(Parcel in) {
            return new ModelFanPosts(in);
        }

        @Override
        public ModelFanPosts[] newArray(int size) {
            return new ModelFanPosts[size];
        }
    };
}