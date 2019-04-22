package com.technikh.onedrupal.fragments;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.technikh.onedrupal.R;
import com.technikh.onedrupal.activities.ViewImageActivity;
import com.technikh.onedrupal.adapter.GalleryAdapter;
import com.technikh.onedrupal.helpers.PDUtils;
import com.technikh.onedrupal.models.ModelFanPosts;
import com.technikh.onedrupal.util.EndlessRecyclerViewScrollListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class FragmentADGallery extends FragmentBase {

    private String mSiteDomain, mSiteProtocol;

    StaggeredGridLayoutManager manager;
    RecyclerView rv_f_redsox_recycler;
    SwipeRefreshLayout swipeContainer;
    ArrayList<ModelFanPosts> modelFanPostsArrayList = new ArrayList<>();
    GalleryAdapter adapter;
    LinearLayout no_connection_ll;
    TextView no_connection_text;
    Interceptor cacheInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {

            CacheControl.Builder cacheBuilder = new CacheControl.Builder();
            cacheBuilder.maxAge(0, TimeUnit.SECONDS);
            cacheBuilder.maxStale(365, TimeUnit.DAYS);
            CacheControl cacheControl = cacheBuilder.build();

            Request request = chain.request();
            if (hasNetwork(getActivity())) {
                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
            }
            Response originalResponse = chain.proceed(request);
            if (hasNetwork(getActivity())) {
                int maxAge = 60; // read from cache
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    };
    String nextPageToken;
    String response_error = "";
    int scroll = 1, tab = 0, firstVisibleItem, visibleItemCount, totalItemCount;
    private int visibleThreshold = 2;
    private boolean loading = false;

    public static FragmentADGallery newInstance(int i, String protocol, String domain) {
        Bundle args = new Bundle();
        args.putInt("tab", i);
        args.putString("SiteProtocol", protocol);
        args.putString("SiteDomain", domain);
        FragmentADGallery fragment = new FragmentADGallery();
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
            mSiteProtocol = getArguments().getString("SiteProtocol");
            mSiteDomain = getArguments().getString("SiteDomain");
        }
        no_connection_ll = view.findViewById(R.id.no_connection_ll);
        no_connection_text = view.findViewById(R.id.no_connection_text);
        rv_f_redsox_recycler = view.findViewById(R.id.rv_f_redsox_recycler);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        //rv_f_redsox_recycler.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        rv_f_redsox_recycler.setHasFixedSize(true);
        manager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        rv_f_redsox_recycler.setLayoutManager(manager);
        rv_f_redsox_recycler.setItemAnimator(new DefaultItemAnimator());
        adapter = new GalleryAdapter(context, modelFanPostsArrayList, new GalleryAdapter.RecyclerViewClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                Intent intent = new Intent(getActivity(), ViewImageActivity.class);
                intent.putExtra("posts", modelFanPostsArrayList);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        rv_f_redsox_recycler.setAdapter(adapter);


        final EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                Log.e("page", ">>" + page);
                Log.e("totalItemsCount", ">>" + totalItemsCount);

                requestNewsList(page, false);

            }
        };

        rv_f_redsox_recycler.addOnScrollListener(scrollListener);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);
                //scrollListener.resetValues(manager);
                requestNewsList(0, true);
            }
        });


        requestNewsList(0, false);
    }

    private boolean hasNetwork(Context context) {
        boolean isConnected = false; // Initial Value
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected())
            isConnected = true;
        return isConnected;
    }

    private void requestNewsList(final int pageNumber, final boolean swipeRefresh) {
        String url = "redsox";
      /*  if (tab == 0) {
            url = "redsox";
        } else if (tab == 1) {
            url = "eagles";
        } else if (tab == 2) {
            url = "patriots";
        }*/


        response_error = "";
        no_connection_ll.setVisibility(View.GONE);
        _progressDialogAsync.show();
        String newUrl = "BuildConfig.API_ENPOINT" + "myapi/v1/articles/" + url + "?page=" + pageNumber;

        File httpCacheDirectory = new File(getActivity().getCacheDir(), "offlineCache");
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //10 MB
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(httpLoggingInterceptor)
                .addNetworkInterceptor(provideCacheInterceptor())
                .addInterceptor(provideOfflineCacheInterceptor())
                .build();
        Request request = new Request.Builder().url(newUrl).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        no_connection_ll.setVisibility(View.VISIBLE);
                        no_connection_text.setText(e.toString());
                        PDUtils.showToast(context, e.toString());
                        _progressDialogAsync.cancel();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String respoStr = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        _progressDialogAsync.cancel();
                        try {
//                    if (scroll != 1) {
//                        modelFanPostsArrayList.remove(modelFanPostsArrayList.size() - 1);
//                        adapter.notifyItemRemoved(modelFanPostsArrayList.size());
//                    }
                            //nextPageToken = result.has("next") ? result.getString("next") : "";
                            JSONObject result = new JSONObject(respoStr);

                            if (!result.has("results")) {
                                response_error = result.getString("");
                            } else {
                                //  modelFanPostsArrayList.clear();

                                if (swipeRefresh) {
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
                        } catch (Exception e) {
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
                });

            }
        });

        RequestParams requestParams = new RequestParams();
        /*PDRestClient.get(context, BuildConfig.API_ENPOINT + "myapi/v1/articles/" + url + "?page=" + pageNumber, requestParams, new JsonHttpResponseHandler() {
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

                        if (swipeRefresh) {
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
   */
    }

    private Interceptor provideCacheInterceptor() {

        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response originalResponse = chain.proceed(request);
                String cacheControl = originalResponse.header("Cache-Control");

                if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                        cacheControl.contains("must-revalidate") || cacheControl.contains("max-stale=0")) {


                    CacheControl cc = new CacheControl.Builder()
                            .maxStale(1, TimeUnit.DAYS)
                            .build();


                    request = request.newBuilder()
                            .cacheControl(cc)
                            .build();

                    return chain.proceed(request);

                } else {
                    return originalResponse;
                }
            }
        };

    }


    private Interceptor provideOfflineCacheInterceptor() {

        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                try {
                    return chain.proceed(chain.request());
                } catch (Exception e) {


                    CacheControl cacheControl = new CacheControl.Builder()
                            .onlyIfCached()
                            .maxStale(1, TimeUnit.DAYS)
                            .build();

                    Request offlineRequest = chain.request().newBuilder()
                            .cacheControl(cacheControl)
                            .build();
                    return chain.proceed(offlineRequest);
                }
            }
        };
    }

}