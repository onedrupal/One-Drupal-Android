package com.prominentdev.blog.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.prominentdev.blog.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Narender Kumar on 11/14/2018.
 * For Prominent, Faridabad (India)
 * narender.kumar.nishad@gmail.com
 */
public class ModelFanPosts implements Parcelable {

    private String nid;
    private String title;
    private String body="";
    private String field_image="";
    private String field_text_category="";

    public ModelFanPosts(String nid, String title, String body, String field_image, String field_text_category) {
        this.nid = nid;
        this.title = title;
        this.body = body;
        this.field_image = field_image;
        this.field_text_category = field_text_category;
    }

    public ModelFanPosts(JSONObject jo) {
        try {
            this.nid = jo.getJSONArray("nid").getJSONObject(0).getString("value");
            this.title = jo.getJSONArray("title").getJSONObject(0).getString("value");
            if (jo.has("body") && !jo.isNull("body") && (jo.getJSONArray("body").length() > 0)) {
                this.body = jo.getJSONArray("body").getJSONObject(0).getString("value");
            }
            if (jo.has("field_image") && !jo.isNull("field_image") && (jo.getJSONArray("field_image").length() > 0)) {
                this.field_image = jo.getJSONArray("field_image").getJSONObject(0).getString("url");
            }
            if (jo.has("field_text_category") && !jo.isNull("field_text_category") && (jo.getJSONArray("field_text_category").length() > 0)) {
                this.field_text_category = jo.getJSONArray("field_text_category").getJSONObject(0).getString("value");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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