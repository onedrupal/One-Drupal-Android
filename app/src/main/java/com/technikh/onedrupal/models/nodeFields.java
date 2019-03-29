package com.technikh.onedrupal.models;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class nodeFields {
    @SerializedName("image")
    private String mImage="";

    @SerializedName("body")
    private String mBody="";

    @SerializedName("remote_image")
    public String remote_image="";

    @SerializedName("remote_page")
    public String remote_page="";

    @SerializedName("embedded_video")
    public String remote_video="";

    public String getFieldImage() {
        return mImage;
    }

    public void setFieldImage(String image) {
        this.mImage = image;
    }

    public String getFieldBody() {
        return mBody;
    }

    public void setFieldBody(String body) {
        this.mBody = body;
    }
}
