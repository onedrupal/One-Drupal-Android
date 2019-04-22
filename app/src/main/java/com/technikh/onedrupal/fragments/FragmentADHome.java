package com.technikh.onedrupal.fragments;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.technikh.onedrupal.R;
import com.technikh.onedrupal.app.MyApplication;
import com.technikh.onedrupal.helpers.PDUtils;

public class FragmentADHome extends FragmentBase {

    TabLayout tabs_f_home;
    ViewPager pager_f_home;
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    public static FragmentADHome newInstance() {
        Bundle args = new Bundle();
        FragmentADHome fragment = new FragmentADHome();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ad_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        tabs_f_home = rootView.findViewById(R.id.tabs_f_home);
        pager_f_home = rootView.findViewById(R.id.pager_f_home);

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getChildFragmentManager());
        pager_f_home.setAdapter(mAppSectionsPagerAdapter);
        tabs_f_home.setupWithViewPager(pager_f_home);
        pager_f_home.setOffscreenPageLimit(2);
        pager_f_home.setCurrentItem(0);
        pager_f_home.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                PDUtils.hideKeyboard(context);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return FragmentADRedsox.newInstance(i, "", "");
            /*switch (i) {
                case 0:
                    return FragmentADRedsox.newInstance(0);
                case 1:
                    return FragmentADRedsox.newInstance(1);
                case 2:
                    return FragmentADRedsox.newInstance(2);
                case 3:
                    return new FragmentADGallery();
                default:
                    return FragmentADRedsox.newInstance(0);
            }*/
        }

        @Override
        public int getCount() {
            return MyApplication.gblGetNumberOfNodeTypes()+2;
            //return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int i) {
            switch (i) {
                case 0:
                    return "All Content";
                case 1:
                    return "Promoted Content";
                default:
                    return MyApplication.gblGetNodeTypeFromPosition(i-2).getNodeType();
            }
        }
    }
}