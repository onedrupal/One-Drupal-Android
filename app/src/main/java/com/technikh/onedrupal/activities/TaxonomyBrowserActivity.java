package com.technikh.onedrupal.activities;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.technikh.onedrupal.R;
import com.technikh.onedrupal.adapter.BreadcumAdapter;
import com.technikh.onedrupal.models.BreadcumModel;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.technikh.onedrupal.app.MyApplication.breadcumList;


public class TaxonomyBrowserActivity extends AppCompatActivity  implements BreadcumAdapter.buttonEventListenr{

    private RecyclerView breadcum_rv;
    private BreadcumAdapter breadcumAdapter;
    private TextView browser ;
    FloatingActionButton fab ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxonomy_browser);

        Toolbar mtoolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mtoolbar);

        browser = (TextView) mtoolbar.findViewById(R.id.browser);

        breadcum_rv = (RecyclerView) mtoolbar.findViewById(R.id.breadcum_rv);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL, false);

        breadcum_rv.setLayoutManager(mLayoutManager);

        Intent intent = getIntent();

        if (intent != null) {

            String sitetitle = intent.getStringExtra("sitetitle");

            String tid = intent.getStringExtra("tid");

            String vid = intent.getStringExtra("vid");


            if (sitetitle == null ){

                breadcum_rv.setVisibility(View.GONE);

            } else {

                browser.setBackgroundResource(R.drawable.breadcumbtn);

                BreadcumModel getSet = new BreadcumModel(sitetitle, vid, tid);

                breadcumList.add(getSet);

                breadcumAdapter = new BreadcumAdapter(breadcumList, this);

                breadcum_rv.setAdapter(breadcumAdapter);

                breadcumAdapter.notifyDataSetChanged();

            }

        }

        onClickistners();

    }

    public void onClickistners(){

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                breadcumList.clear();

                Intent myIntent = new Intent(TaxonomyBrowserActivity.this, TaxonomyBrowserActivity.class);
                myIntent.putExtra("SiteProtocol", "http://");
                myIntent.putExtra("SiteDomain", "one-drupal-demo.technikh.com/");
                myIntent.putExtra("tid", "0");
                myIntent.putExtra("vid", "all");



                startActivity(myIntent);

            }
        });
    }

    @Override
    public void buttonEvent(int position,String SiteTitle,String titleid,String vocabularyid) {

        breadcumList.subList(position, breadcumList.size()).clear();

        Intent myIntent = new Intent(this, TaxonomyBrowserActivity.class);
        myIntent.putExtra("SiteProtocol", "http://");
        myIntent.putExtra("SiteDomain", "one-drupal-demo.technikh.com/");
        myIntent.putExtra("tid", titleid);
        myIntent.putExtra("vid", vocabularyid);

        startActivity(myIntent);

    }
}
