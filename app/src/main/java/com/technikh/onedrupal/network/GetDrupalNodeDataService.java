package com.technikh.onedrupal.network;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import com.technikh.onedrupal.models.ModelNodeType;
import com.technikh.onedrupal.models.SettingsTypeList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface GetDrupalNodeDataService {
    @GET("/node/{editNid}?_format=json")
    Call<ModelNodeType> getNode(@Path("editNid") String nid);
}
