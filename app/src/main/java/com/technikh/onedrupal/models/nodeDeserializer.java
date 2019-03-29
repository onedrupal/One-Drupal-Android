package com.technikh.onedrupal.models;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

public class nodeDeserializer implements JsonDeserializer<nodeData> {
    @Override
    public nodeData deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
            throws JsonParseException
    {
        Log.d("nodeDeserializer", "deserialize: "+je.getAsString());
        /*JsonElement data = je.getAsJsonObject().get("data");
        JsonElement mainCategory = je.getAsJsonObject().get("main_category");
        JsonElement user = je.getAsJsonObject().get("user");*/

        return new Gson().fromJson(je, nodeData.class);
    }
}