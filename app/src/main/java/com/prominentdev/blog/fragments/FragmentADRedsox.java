package com.prominentdev.blog.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.prominentdev.blog.BuildConfig;
import com.prominentdev.blog.R;
import com.prominentdev.blog.activities.ActivityFanPostDetails;
import com.prominentdev.blog.adapter.AdapterFanPosts;
import com.prominentdev.blog.helpers.PDRestClient;
import com.prominentdev.blog.helpers.PDUtils;
import com.prominentdev.blog.models.ModelFanPosts;
import com.prominentdev.blog.util.EndlessRecyclerViewScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Narender Kumar on 9/30/2018.
 * For Prominent Developers, Faridabad (India)
 */

public class FragmentADRedsox extends FragmentBase {

    LinearLayoutManager manager;
    RecyclerView rv_f_redsox_recycler;
    SwipeRefreshLayout swipeContainer;
    ArrayList<ModelFanPosts> modelFanPostsArrayList = new ArrayList<>();
    AdapterFanPosts adapter;
    LinearLayout no_connection_ll;
    TextView no_connection_text;

    String nextPageToken;
    String response_error = "";
    int scroll = 1, tab = 0, firstVisibleItem, visibleItemCount, totalItemCount;
    private int visibleThreshold = 2;
    private boolean loading = false;

    public static FragmentADRedsox newInstance(int i) {
        Bundle args = new Bundle();
        args.putInt("tab", i);
        FragmentADRedsox fragment = new FragmentADRedsox();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ad_redsox, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            tab = getArguments().getInt("tab");
        }
        no_connection_ll = view.findViewById(R.id.no_connection_ll);
        no_connection_text = view.findViewById(R.id.no_connection_text);
        rv_f_redsox_recycler = view.findViewById(R.id.rv_f_redsox_recycler);
        swipeContainer=view.findViewById(R.id.swipeContainer);
        rv_f_redsox_recycler.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        rv_f_redsox_recycler.setHasFixedSize(true);
        manager = new LinearLayoutManager(context);
        rv_f_redsox_recycler.setLayoutManager(manager);
        rv_f_redsox_recycler.setItemAnimator(new DefaultItemAnimator());
        adapter = new AdapterFanPosts(context, modelFanPostsArrayList, new AdapterFanPosts.RecyclerViewClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                ModelFanPosts singleModelFanPosts = adapter.getAllModelPost().get(position);
                startActivity(new Intent(context, ActivityFanPostDetails.class)
                        .putExtra("nid", singleModelFanPosts.getNid()));
            }
        });
        rv_f_redsox_recycler.setAdapter(adapter);





        final EndlessRecyclerViewScrollListener scrollListener=new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                Log.e("page", ">>" + page);
                Log.e("totalItemsCount", ">>" + totalItemsCount);

                requestNewsList(page,false);

            }
        };

        rv_f_redsox_recycler.addOnScrollListener(scrollListener);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);
                scrollListener.resetValues(manager);
                requestNewsList(0,true);
            }
        });



        /*rv_f_redsox_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = manager.getChildCount();
                totalItemCount = manager.getItemCount();
                firstVisibleItem = manager.findFirstVisibleItemPosition();

                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    // End has nearly reached, It's based on threshold.
                    if (PDUtils.isNetworkConnected(context)) {
                        if (nextPageToken.equalsIgnoreCase(getString(R.string.no_more_result))) {
                            PDUtils.showToast(context, getString(R.string.no_more_result));
                            loading = true;
                        }*//* else if (!next.equalsIgnoreCase("")) {
                            scroll = 2;
                            loading = true;
                            requestNewsList(next);
                        } else {
                            PDUtils.showToast(context, getString(R.string.no_more_result));
                            loading = true;
                        }*//*
                    } else {
                        PDUtils.showToast(context, getString(R.string.check_internet_connection));
                    }
                }
            }
        });*/

        requestNewsList(0,false);
    }



    private void requestNewsList(final int pageNumber,final boolean swipeRefresh) {
        String url = "";
        if (tab == 0) {
            url = "redsox";
        } else if (tab == 1) {
            url = "eagles";
        } else if (tab == 2) {
            url = "patriots";
        }
        RequestParams requestParams = new RequestParams();
        PDRestClient.get(context, BuildConfig.API_ENPOINT + "myapi/v1/articles/" + url+"?page="+pageNumber, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                if (scroll == 1) {
//                    nextPageToken = "";
//                    modelFanPostsArrayList.clear();
//                    adapter.notifyDataSetChanged();
//                } else {
//                    modelFanPostsArrayList.add(null);
//                }
                response_error = "";
                no_connection_ll.setVisibility(View.GONE);
                _progressDialogAsync.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject result) {
                super.onSuccess(statusCode, headers, result);
                _progressDialogAsync.cancel();
                try {
//                    if (scroll != 1) {
//                        modelFanPostsArrayList.remove(modelFanPostsArrayList.size() - 1);
//                        adapter.notifyItemRemoved(modelFanPostsArrayList.size());
//                    }
                    //nextPageToken = result.has("next") ? result.getString("next") : "";
                    if (!result.has("results")) {
                        response_error = result.getString("");
                    } else {
                      //  modelFanPostsArrayList.clear();

                        if(swipeRefresh)
                        {
                            adapter.clearAll();
                        }
                        JSONArray ja = result.getJSONArray("results");
                        for (int j = 0; j < ja.length(); j++) {
                            JSONObject jo = (JSONObject) ja.get(j);
                            ModelFanPosts modelFanPosts = new ModelFanPosts(jo);
                           // modelFanPostsArrayList.add(modelFanPosts);
                            adapter.addOneRequestData(modelFanPosts);
                        }
                        adapter.notifyDataSetChanged();



                    }
                } catch (JSONException e) {
                    response_error = getString(R.string.unable_to_connect);
                    e.printStackTrace();
                }

                if (response_error == null || response_error.isEmpty()) {
//                    if (scroll == 1) {
//                        rv_f_redsox_recycler.setLayoutManager(manager);
//                        rv_f_redsox_recycler.setAdapter(adapter);
//                    } else {
//                        adapter.notifyItemInserted(modelFanPostsArrayList.size() - 1);
//                        View first = rv_f_redsox_recycler.getChildAt(0);
//                        int position = 0;
//                        if (first != null)
//                            position = first.getTop() - first.getPaddingTop();
//                    }
                } else {
                    if (adapter.getAllModelPost().size() <= 0) {
                        no_connection_ll.setVisibility(View.VISIBLE);
                        no_connection_text.setText(response_error);
                    } else {
                        PDUtils.showToast(context, response_error);
                    }
                }
                loading = false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                no_connection_ll.setVisibility(View.VISIBLE);
                no_connection_text.setText(PDRestClient.getHTTPErrorMessage(statusCode));
                PDUtils.showToast(context, PDRestClient.getHTTPErrorMessage(statusCode));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                no_connection_ll.setVisibility(View.VISIBLE);
                no_connection_text.setText(PDRestClient.getHTTPErrorMessage(statusCode));
                PDUtils.showToast(context, PDRestClient.getHTTPErrorMessage(statusCode));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                _progressDialogAsync.cancel();
            }
        });
    }

/*    @Subscribe
    public void onEventMainThread(final EventsFromFragments eventsFromFragments) {

    }*/
}