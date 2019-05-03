package com.technikh.onedrupal.models;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import com.google.gson.annotations.SerializedName;

public class Site {
    @SerializedName("protocol")
    private String mProtocol;
    @SerializedName("domain")
    private String mDomain;
    @SerializedName("title")
    private String mTitle;

    public Site(String protocol, String domain, String title) {
        this.mProtocol = protocol;
        this.mDomain = domain;
        this.mTitle = title;
    }

    public String getProtocol() {
        return mProtocol;
    }

    public void setProtocol(String name) {
        this.mProtocol = name;
    }

    public String getDomain() {
        return mDomain;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setDomain(String email) {
        this.mDomain = email;
    }
}