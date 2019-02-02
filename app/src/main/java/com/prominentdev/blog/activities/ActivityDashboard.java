package com.prominentdev.blog.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.prominentdev.blog.R;
import com.prominentdev.blog.fragments.FragmentADHome;
import com.prominentdev.blog.helpers.PDUtils;

/**
 * Created by Narender Kumar on 11/12/2018.
 * For Prominent, Faridabad (India)
 * narender.kumar.nishad@gmail.com
 */
public class ActivityDashboard extends ActivityBase {

    Toolbar toolbar;
    FloatingActionButton ad_fab;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.dashboard);
        ad_fab = findViewById(R.id.ad_fab);
        ad_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ActivityPost.class));
            }
        });

        if (findViewById(R.id.ad_fl_root) != null) {
            if (savedInstanceState != null) {
                return;
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.ad_fl_root, FragmentADHome.newInstance(), FragmentADHome.class.getSimpleName())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        //Checking for fragment count on backstack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            doubleBackToExitPressedOnce = true;
            PDUtils.showToast(context, getString(R.string.back_pressed));

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 1000);
        } else {
            super.onBackPressed();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            ad_fab.hide();
        } else {
            ad_fab.show();
        }
    }
}