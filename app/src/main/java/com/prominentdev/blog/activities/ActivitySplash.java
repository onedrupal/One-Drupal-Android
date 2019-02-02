package com.prominentdev.blog.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.prominentdev.blog.R;

public class ActivitySplash extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sessionManager.isLoggedIn()) {
                    startActivity(new Intent(context, ActivityDashboard.class));
                    finish();
                } else {
                    startActivity(new Intent(context, ActivityAuthentication.class));
                    finish();
                }
            }
        }, 1000);
    }
}