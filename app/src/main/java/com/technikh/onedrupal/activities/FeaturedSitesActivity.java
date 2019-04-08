package com.technikh.onedrupal.activities;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.technikh.onedrupal.R;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import com.technikh.onedrupal.adapter.SiteAdapter;
import com.technikh.onedrupal.models.Site;
import com.technikh.onedrupal.models.SiteList;
import com.technikh.onedrupal.network.GetSiteDataService;
import com.technikh.onedrupal.network.RetrofitOneDrupalInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeaturedSitesActivity extends AppCompatActivity {

    private SiteAdapter adapter;
    private RecyclerView recyclerView;
    private String TAG = "FeaturedSitesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_sites);

        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setTitle("Featured Sites");

        ArrayList<Site> empDataList = new ArrayList<Site>();
        empDataList.add(new Site("https://", "nikhil.dubbaka.com"));
        empDataList.add(new Site("http://", "app.eschool2go.org"));
        generateEmployeeList(empDataList);
/*
        //Create handle for the RetrofitOneDrupalInstance interface
        GetSiteDataService service = RetrofitOneDrupalInstance.getRetrofitInstance().create(GetSiteDataService.class);

        //Call the method with parameter in the interface to get the employee data
        Call<SiteList> call = service.getEmployeeData(100);

        //Log the URL called
        Log.wtf("URL Called", call.request().url() + "");

        call.enqueue(new Callback<SiteList>() {
            @Override
            public void onResponse(Call<SiteList> call, Response<SiteList> response) {
                Log.d(TAG, "onResponse: "+response.toString());
                Log.d(TAG, "onResponse: body "+response.body().toString());
                ArrayList<Site> empDataList = response.body().getEmployeeArrayList();
                if(empDataList != null) {
                    generateEmployeeList(empDataList);
                }
            }

            @Override
            public void onFailure(Call<SiteList> call, Throwable t) {
                Toast.makeText(FeaturedSitesActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    /*Method to generate List of employees using RecyclerView with custom adapter*/
    private void generateEmployeeList(ArrayList<Site> empDataList) {
        Log.d(TAG, "generateEmployeeList: "+empDataList.size());
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_employee_list);

        adapter = new SiteAdapter(empDataList, FeaturedSitesActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(FeaturedSitesActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }
}
