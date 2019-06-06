package com.technikh.onedrupal.models;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/*
{
title: "75",
term: "Weight",
changed: "1559324599",
created: "1559229053"
}
 */
public class NodeSimpleFields {
    @SerializedName("title")
    public String title;
    @SerializedName("term")
    public String term;
    @SerializedName("changed")
    public long changed;
    @SerializedName("created")
    public long created;

    public NodeSimpleFields(JSONObject jo) {
        try {
            title = jo.getString("title");
            term = jo.getString("term");
            changed = jo.getLong("created");
            created = jo.getLong("created");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}