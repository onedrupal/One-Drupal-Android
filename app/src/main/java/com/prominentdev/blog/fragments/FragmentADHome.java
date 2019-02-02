package com.prominentdev.blog.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prominentdev.blog.R;
import com.prominentdev.blog.helpers.PDUtils;

/**
 * Created by Narender Kumar on 11/12/2018.
 * For Prominent, Faridabad (India)
 * narender.kumar.nishad@gmail.com
 */
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
            switch (i) {
                case 0:
                    return FragmentADRedsox.newInstance(0);
                case 1:
                    return FragmentADRedsox.newInstance(1);
                case 2:
                    return FragmentADRedsox.newInstance(2);
                default:
                    return FragmentADRedsox.newInstance(0);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int i) {
            switch (i) {
                case 0:
                    return "REDSOX";
                case 1:
                    return "EAGLES";
                case 2:
                    return "PATRIOTS";
                default:
                    return "REDSOX";
            }
        }
    }
}