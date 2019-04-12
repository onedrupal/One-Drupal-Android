package com.technikh.onedrupal.models;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SettingsTypeList {
    @SerializedName("types")
    private ArrayList<SettingsType> typesList;

    @SerializedName("settings")
    private OneGlobalSettingsSectionModel settings;

    public ArrayList<SettingsType> getTypesArrayList() {
        return typesList;
    }

    public OneGlobalSettingsSectionModel getSettingsSection() {
        return settings;
    }

    public void setTypesArrayList(ArrayList<SettingsType> siteArrayList) {
        this.typesList = siteArrayList;
    }
}
