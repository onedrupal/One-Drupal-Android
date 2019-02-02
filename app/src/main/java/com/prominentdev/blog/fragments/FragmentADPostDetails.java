package com.prominentdev.blog.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prominentdev.blog.R;

/**
 * Created by Narender Kumar on 9/30/2018.
 * For Prominent Developers, Faridabad (India)
 */

public class FragmentADPostDetails extends FragmentBase {

    public static FragmentADPostDetails newInstance() {
        Bundle args = new Bundle();
        FragmentADPostDetails fragment = new FragmentADPostDetails();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
    }

/*    @Subscribe
    public void onEventMainThread(final EventsFromFragments eventsFromFragments) {

    }*/
}