package com.technikh.onedrupal.models;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OneMultipleTermsItemTermModel {

    @SerializedName("name")
    public String name;

    @SerializedName("tid")
    public String tid;

    @SerializedName("count")
    public String count;

    @SerializedName("parent")
    public String parent;

    @Override
    public String toString() {
        return name;

    }
}