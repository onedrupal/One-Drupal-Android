package com.technikh.onedrupal.helpers;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class PDUtilsJSON {

    public static JSONObject ensureNonNullJObj(JSONObject rootJsonObject, String key) throws JSONException {
        return (rootJsonObject != null && key != null && rootJsonObject.has(key) && !rootJsonObject.isNull(key)) ?
                rootJsonObject.getJSONObject(key) : new JSONObject();
    }

    public static JSONArray ensureNonNullJArray(JSONObject rootJsonObject, String key) throws JSONException {
        return (rootJsonObject != null && key != null && rootJsonObject.has(key) && !rootJsonObject.isNull(key)) ?
                rootJsonObject.getJSONArray(key) : new JSONArray();
    }

    public static String ensureNonNullString(JSONObject rootJsonObject, String key) throws JSONException {
        return (rootJsonObject != null && key != null && rootJsonObject.has(key) && !rootJsonObject.isNull(key)) ?
                rootJsonObject.getString(key) : "";
    }

    public static long ensureNonNullLong(JSONObject rootJsonObject, String key) throws JSONException {
        return (rootJsonObject != null && key != null && rootJsonObject.has(key) && !rootJsonObject.isNull(key)) ?
                rootJsonObject.getLong(key) : 0L;
    }

    public static int ensureNonNullInt(JSONObject rootJsonObject, String key) throws JSONException {
        return (rootJsonObject != null && key != null && rootJsonObject.has(key) && !rootJsonObject.isNull(key)) ?
                rootJsonObject.getInt(key) : 0;
    }

    public static boolean ensureNonNullBool(JSONObject rootJsonObject, String key) throws JSONException {
        return rootJsonObject != null && key != null && rootJsonObject.has(key) && !rootJsonObject.isNull(key) && rootJsonObject.getBoolean(key);
    }

    public static float ensureNonNullFloat(JSONObject rootJsonObject, String key) throws JSONException {
        if (rootJsonObject != null && key != null && rootJsonObject.has(key) && !rootJsonObject.isNull(key)) {
            if (rootJsonObject.get(key) instanceof Integer) {
                return rootJsonObject.getInt(key);
            } else {
                return BigDecimal.valueOf(rootJsonObject.getDouble(key)).floatValue();
            }
        } else {
            return 0f;
        }
    }
}