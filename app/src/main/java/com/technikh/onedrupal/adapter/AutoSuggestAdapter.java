package com.technikh.onedrupal.adapter;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.technikh.onedrupal.R;
import com.technikh.onedrupal.models.OneMultipleTermsItemTermModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AutoSuggestAdapter extends ArrayAdapter<OneMultipleTermsItemTermModel> implements Filterable {
    private List<OneMultipleTermsItemTermModel> mlistData;
    private int itemLayout;
    private static String TAG = "ActivityPost";

    public AutoSuggestAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        mlistData = new ArrayList<>();
        itemLayout = resource;
    }

    public void setData(List<OneMultipleTermsItemTermModel> list) {
        mlistData.clear();
        mlistData.addAll(list);
    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount: "+mlistData.size());
        return mlistData.size();
    }

    @Nullable
    @Override
    public OneMultipleTermsItemTermModel getItem(int position) {
        Log.d(TAG, "getItem: "+position);
        return mlistData.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        Log.d(TAG, "getView: ");
        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }

        TextView strName = (TextView) view.findViewById(R.id.store);
        strName.setText(getItem(position).name);

        if(!getItem(position).parent.isEmpty()) {
            TextView couponCount = (TextView) view.findViewById(R.id.coupon);
            couponCount.setVisibility(View.VISIBLE);
            couponCount.setText("Parent(s) : " + getItem(position).parent);
        }

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Log.d(TAG, "getFilter: ");
        Filter dataFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    filterResults.values = mlistData;
                    filterResults.count = mlistData.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && (results.count > 0)) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return dataFilter;
    }
}