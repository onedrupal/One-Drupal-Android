package com.technikh.onedrupal.models;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TaxonomyTermModel {
    public String name, vid;
    public int tid;

    public TaxonomyTermModel(JSONObject jo) {
        try {
            this.name = jo.getJSONArray("name").getJSONObject(0).getString("value");
            this.tid = jo.getJSONArray("tid").getJSONObject(0).getInt("value");
            this.vid = jo.getJSONArray("vid").getJSONObject(0).getString("target_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}