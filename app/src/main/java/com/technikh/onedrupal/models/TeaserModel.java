package com.technikh.onedrupal.models;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.technikh.onedrupal.app.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TeaserModel implements Parcelable {

    private String id;
    public String vid;
    private String title;
    private String entityType;
    private String body="";
    private String entityBundle="";
    private String field_image="";
    public String field_video="", field_remote_page="";
    private String field_video_thumbnail="";
    private String field_text_category="";
    private boolean isValidNodeType = false;

    public boolean isPublished = false;
    public boolean isPromoted = false;

   /* @SerializedName("status")
    private ArrayList<fieldBooleanValue> isPublished;
    @SerializedName("promote")
    private ArrayList<fieldBooleanValue> isPromoted;
*/
    private static String TAG = "TeaserModel";

    public TeaserModel() {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getNodeType() {
        return entityBundle;
    }

    public void setEntityType(String lentityType) {
        this.entityType = lentityType;
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

    protected TeaserModel(Parcel in) {
        id = in.readString();
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
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(body);
        dest.writeString(field_image);
        dest.writeString(field_text_category);
    }

    @SuppressWarnings("unused")
    public static final Creator<TeaserModel> CREATOR = new Creator<TeaserModel>() {
        @Override
        public TeaserModel createFromParcel(Parcel in) {
            return new TeaserModel(in);
        }

        @Override
        public TeaserModel[] newArray(int size) {
            return new TeaserModel[size];
        }
    };
}