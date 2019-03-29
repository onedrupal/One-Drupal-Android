package com.technikh.onedrupal.activities;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.technikh.onedrupal.R;

public class ActivitySplash extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //if (true) {
                if (sessionManager.isLoggedIn()) {
                    startActivity(new Intent(context, SiteContentTabsActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(context, ActivityAuthentication.class));
                    finish();
                }
            }
        }, 1000);
    }
}