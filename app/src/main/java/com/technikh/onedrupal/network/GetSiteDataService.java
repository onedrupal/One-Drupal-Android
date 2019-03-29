package com.technikh.onedrupal.network;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.database.Observable;

import com.technikh.onedrupal.models.ModelNodeType;
import com.technikh.onedrupal.models.SettingsTypeList;
import com.technikh.onedrupal.models.SiteList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetSiteDataService {
    @GET("onedrupal/api/v1/settings")
    Call<SettingsTypeList> getTypeData();

    @Headers({"Content-Type:application/json"})
    @PATCH("/node/{editNid}?_format=json")
    Call<ModelNodeType> postNodeEdit(@Path("editNid") String nid, @Body ModelNodeType node);

    @GET("/node/{editNid}?_format=json")
    Call<ModelNodeType> getNode(@Path("editNid") String nid);
}
