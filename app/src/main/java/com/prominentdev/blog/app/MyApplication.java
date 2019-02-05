package com.prominentdev.blog.app;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by Mahmudul Hasan Swapon on 2/5/2019.
 * Company: AAPBD
 * contact me if you've any issues at: swaponsust@gmail.com or Skype: swapon.sust
 */
public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }
}
