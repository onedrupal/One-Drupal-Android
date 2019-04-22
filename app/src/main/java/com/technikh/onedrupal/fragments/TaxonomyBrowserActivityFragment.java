package com.technikh.onedrupal.fragments;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.RequestParams;

import com.technikh.onedrupal.R;
import com.technikh.onedrupal.activities.ActivityFanPostDetails;
import com.technikh.onedrupal.activities.TaxonomyBrowserActivity;
import com.technikh.onedrupal.adapter.TeaserAdapter;
import com.technikh.onedrupal.authenticator.AuthPreferences;
import com.technikh.onedrupal.models.ModelFanPosts;
import com.technikh.onedrupal.models.TaxonomyTermModel;
import com.technikh.onedrupal.models.TeaserModel;
import com.technikh.onedrupal.network.AddCookiesInterceptor;
import com.technikh.onedrupal.network.ProvideCacheInterceptor;
import com.technikh.onedrupal.network.ProvideOfflineCacheInterceptor;
import com.technikh.onedrupal.util.EndlessRecyclerViewScrollListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import treeutil.MyObject;
import static android.content.Context.MODE_PRIVATE;

import static com.technikh.onedrupal.app.MyApplication.gblSettingsSection;

/**
 * A placeholder fragment containing a simple view.
 */

public class TaxonomyBrowserActivityFragment extends Fragment {

    private String TAG = "TaxonomyBrowserActivityFragment";
    ArrayList<TeaserModel> dataList = new ArrayList<>();
    ArrayList<TeaserModel> termsList = new ArrayList<>();
    RecyclerView rv_f_explorer_items;
    SwipeRefreshLayout swipeContainer;
    TeaserAdapter adapterDefault;
    Context mContext;
    LinearLayoutManager manager;
    private String mSiteDomain, mSiteProtocol, mTid, mVocabularyId ,location,sitetitle;

    public TaxonomyBrowserActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_taxonomy_browser, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv_f_explorer_items = view.findViewById(R.id.rview_explorer_items_list);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        //rv_f_explorer_terms = view.findViewById(R.id.rview_explorer_terms_list);
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            Log.d(TAG, "onViewCreated: bundle not null");
            mSiteProtocol = bundle.getString("SiteProtocol");
            mSiteDomain = bundle.getString("SiteDomain");
            sitetitle = bundle.getString("sitetitle");
            mTid = bundle.getString("tid");
            mVocabularyId = bundle.getString("vid");

            Log.d(TAG, "onBreadcum: mTid "+mTid+" mVocabularyId "+mVocabularyId + mSiteProtocol);

            if(mTid == null || mTid.isEmpty()){
                mTid = "0";

            }

            if(mTid == null || mTid.isEmpty()){
                mTid = "0";

            }
            if(mVocabularyId == null || mVocabularyId.isEmpty()){
                mVocabularyId = "all";
            }

        }
        Log.d(TAG, "onViewCreated: mTid "+mTid+" mVocabularyId "+mVocabularyId);
        // First screen mTid 0 mVocabularyId all
        // second mTid 0 mVocabularyId language
        if(mSiteDomain == null || mSiteDomain.isEmpty()) {
            AuthPreferences mAuthPreferences;
            mAuthPreferences = new AuthPreferences(mContext);
            mSiteDomain = mAuthPreferences.getPrimarySiteUrl();
            mSiteProtocol = mAuthPreferences.getPrimarySiteProtocol();
        }
        Log.d(TAG, "onViewCreated: mSiteProtocol "+mSiteProtocol);
        mContext = getContext();
        setupNodeRecyclerView();
        //setupTaxonomyRecyclerView();
        if(mTid.equals("0") && mVocabularyId.equals("all") && gblSettingsSection != null) {
            ArrayList<String> taxonomyExplorerVocabularies = gblSettingsSection.taxonomy_explorer_vocabularies;
            Log.d(TAG, "onViewCreated: taxonomyExplorerVocabularies size"+taxonomyExplorerVocabularies.size());
            for (int j = 0; j < taxonomyExplorerVocabularies.size(); j++) {
                String vid = taxonomyExplorerVocabularies.get(j);
                Log.d(TAG, "onViewCreated: vid"+vid);
                TeaserModel teaserModel = new TeaserModel();
                teaserModel.setEntityType("taxonomy");
                teaserModel.setId(String.valueOf(0));
                teaserModel.vid = vid;
                teaserModel.setTitle(vid);

                adapterDefault.addOneRequestData(teaserModel, true);
                adapterDefault.notifyDataSetChanged();
            }
        } else if(!mVocabularyId.equals("all")) {
            requestApiTaxonomyList(0, false);
            requestApiNodeList(0, false);
        }
    }

    /*Method to generate List of employees using RecyclerView with custom adapter*/
    private void setupNodeRecyclerView() {

        rv_f_explorer_items.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        rv_f_explorer_items.setHasFixedSize(true);
        manager = new LinearLayoutManager(mContext);
        rv_f_explorer_items.setLayoutManager(manager);
        rv_f_explorer_items.setItemAnimator(new DefaultItemAnimator());

        System.out.println("breadcumcheckimhere");

        adapterDefault = new TeaserAdapter(mContext, dataList, new TeaserAdapter.RecyclerViewClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {

                    TeaserModel teaserModel = adapterDefault.getAllModelPost().get(position);

                    if(teaserModel.getEntityType().equals("taxonomy")){

                       Intent i = new Intent(mContext, TaxonomyBrowserActivity.class);
                        i.putExtra("tid", teaserModel.getId());
                        i .putExtra("vid", teaserModel.vid);
                        i .putExtra("SiteProtocol", mSiteProtocol);
                        i  .putExtra("SiteDomain", mSiteDomain);
                        i.putExtra("sitetitle",teaserModel.getTitle());
                        mContext.startActivity(i);

                        }else{

                        mContext.startActivity(new Intent(mContext, ActivityFanPostDetails.class)
                                .putExtra("nid", teaserModel.getId())
                                .putExtra("SiteProtocol", mSiteProtocol)
                                .putExtra("SiteDomain", mSiteDomain)
                                .putExtra("sitetitle",teaserModel.getTitle())
                                .putExtra("vid", teaserModel.vid)
                        );
                    }
            }
        });

        rv_f_explorer_items.setAdapter(adapterDefault);

        final EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d(TAG, "onLoadMore: page "+page+" totalItemsCount "+totalItemsCount+" getItemCount "+adapterDefault.getItemCount());

                if(!mVocabularyId.equals("all") && (totalItemsCount < adapterDefault.getItemCount())) {

                    requestApiTaxonomyList(page, false);
                    requestApiNodeList(page, false);
                }

            }
        };

        rv_f_explorer_items.addOnScrollListener(scrollListener);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeContainer.setRefreshing(false);
                scrollListener.resetValues(manager);
                if(!mVocabularyId.equals("all")) {
                    requestApiTaxonomyList(0, true);
                    requestApiNodeList(0, true);
                }
            }
        });
    }
/*
    private void setupTaxonomyRecyclerView() {
        rv_f_explorer_terms.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        rv_f_explorer_terms.setHasFixedSize(true);
        managerTerms = new LinearLayoutManager(mContext);
        rv_f_explorer_terms.setLayoutManager(managerTerms);
        rv_f_explorer_terms.setItemAnimator(new DefaultItemAnimator());
        adapterDefault = new TeaserAdapter(mContext, termsList, new TeaserAdapter.RecyclerViewClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                //Log.d(TAG, "onItemClickListener: v.getId() " + v.getId());
                //Log.d(TAG, "onItemClickListener: tv_row_view_post " + R.id.tv_row_view_post);
                //if (v.getId() == R.id.tv_row_view_post) {
                //ModelFanPosts singleModelFanPosts = adapterDefault.getAllModelPost().get(position);
                TeaserModel teaserModel = adapterDefault.getAllModelPost().get(position);
                if(teaserModel.getEntityType().equals("taxonomy")){
                    startActivity(new Intent(mContext, TaxonomyBrowserActivity.class)
                            .putExtra("tid", teaserModel.getId())
                            .putExtra("SiteProtocol", mSiteProtocol)
                            .putExtra("SiteDomain", mSiteDomain)
                    );
                }else {
                    startActivity(new Intent(mContext, ActivityFanPostDetails.class)
                            .putExtra("nid", teaserModel.getId())
                            .putExtra("SiteProtocol", mSiteProtocol)
                            .putExtra("SiteDomain", mSiteDomain)
                    );
                }
                //}
            }
        });
        rv_f_explorer_terms.setAdapter(adapterDefault);
    }*/

    private void requestApiTaxonomyList(final int pageNumber, final boolean swipeRefresh) {
        if(mSiteDomain == null){
            return;
        }
        Log.d(TAG, "requestApiTaxonomyList onViewCreated: mTid "+mTid+" mVocabularyId "+mVocabularyId);

        System.out.println(mSiteProtocol+mSiteDomain + "/onedrupal/api/v1/vocabulary/"+mVocabularyId+"/"+mTid+"?page=" + pageNumber   + "gghh");

        String newUrl = mSiteProtocol+mSiteDomain + "/onedrupal/api/v1/vocabulary/"+mVocabularyId+"/"+mTid+"?page=" + pageNumber;
        Log.d(TAG, "requestApiTaxonomyList: newUrl"+newUrl);
        File httpCacheDirectory = new File(getActivity().getCacheDir(), "offlineCache");
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(httpLoggingInterceptor)
                .addNetworkInterceptor(new ProvideCacheInterceptor())
                .addInterceptor(new ProvideOfflineCacheInterceptor())
                .addInterceptor(new AddCookiesInterceptor())
                .build();
        Request request;
        try{
            request = new Request.Builder().url(newUrl).build();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //_progressDialogAsync.cancel();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String respoStr = response.body().string();
                Log.d("GET Content", "onResponse: " + respoStr);
                if (response.isSuccessful()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject result = new JSONObject(respoStr);

                                if (!result.has("results")) {
                                    //response_error = result.getString("");
                                } else {
                                    if (swipeRefresh) {
                                        Log.d(TAG, "requestApiTaxonomyList run: swipeRefresh clearAll");
                                        adapterDefault.clearAll();

                                    }
                                    JSONArray ja = result.getJSONArray("results");
                                    for (int j = 0; j < ja.length(); j++) {
                                        JSONObject jo = (JSONObject) ja.get(j);
                                        TaxonomyTermModel taxonomyTermModel = new TaxonomyTermModel(jo);
                                        TeaserModel teaserModel = new TeaserModel();
                                        teaserModel.setEntityType("taxonomy");
                                        teaserModel.setId(String.valueOf(taxonomyTermModel.tid));
                                        teaserModel.vid = taxonomyTermModel.vid;
                                        teaserModel.setTitle(taxonomyTermModel.name);
                                        adapterDefault.addOneRequestData(teaserModel, true);
                                        adapterDefault.notifyDataSetChanged();
                                    }
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    private void requestApiNodeList(final int pageNumber, final boolean swipeRefresh) {
        if(mSiteDomain == null){
            return;
        }
        Log.d(TAG, "requestApiNodeList onViewCreated: mTid "+mTid+" mVocabularyId "+mVocabularyId);
       /* Set set = categoriesMap.entrySet();
        Iterator iterator = set.iterator();
        List<String> tidFiltersAND = new ArrayList<String>();
        while(iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry)iterator.next();
            ArrayList<MyObject> categories1 = (ArrayList<MyObject>)mentry.getValue();
            List<Integer> tidFiltersOR = new ArrayList<Integer>();
            for (int counter = 0; counter < categories1.size(); counter++) {
                MyObject category = categories1.get(counter);
                if(category != null && category.isSelected()){
                    Log.d(TAG, "onClick: selected "+category.name);
                    tidFiltersOR.add(category.tid);
                }
            }
            if(!tidFiltersOR.isEmpty()){
                tidFiltersAND.add(TextUtils.join("+", tidFiltersOR));
            }
        }
        if(!tidFiltersAND.isEmpty()){
            urlSlug = urlSlug+"/"+TextUtils.join("/", tidFiltersAND);
        }

        String newUrl = site_protocol+site_domain + "/onedrupal/api/v1/" + urlSlug + "?page=" + pageNumber;
        if(!search_query.isEmpty()){
            newUrl = newUrl+"&search_keys="+search_query;
        }*/
       // http://app.eschool2go.org/onedrupal/api/v1/content/all/3
        String newUrl = mSiteProtocol+mSiteDomain + "/onedrupal/api/v1/content/all/"+mTid+"?page=" + pageNumber;
        Log.d(TAG, "newUrl: "+newUrl);
        File httpCacheDirectory = new File(getActivity().getCacheDir(), "offlineCache");
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //10 MB
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(httpLoggingInterceptor)
                .addNetworkInterceptor(new ProvideCacheInterceptor())
                .addInterceptor(new ProvideOfflineCacheInterceptor())
                .addInterceptor(new AddCookiesInterceptor())
                .build();
        Request request;
        try{
            request = new Request.Builder().url(newUrl).build();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //_progressDialogAsync.cancel();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String respoStr = response.body().string();
                Log.d("GET Content", "onResponse: "+respoStr);
                if(!response.isSuccessful()){
                    /*if(response.code() == 404) {
                        response_error = "Make sure the drupal site & one_api module is configured correctly with View onedrupal_api_all_published_content enabled!";
                    }else if(response.code() == 403) {
                        response_error = "Make sure user has access to the View onedrupal_api_all_published_content in Drupal site!";
                    }*/
                }
                //Log.d(TAG, "onResponse: response_error"+response_error);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                       // _progressDialogAsync.cancel();
                      //  if(response_error== null || response_error.isEmpty()) {
                            //Log.d(TAG, "run: response_error not null not empty"+response_error);
                            try {
//                    if (scroll != 1) {
//                        modelFanPostsArrayList.remove(modelFanPostsArrayList.size() - 1);
//                        adapter.notifyItemRemoved(modelFanPostsArrayList.size());
//                    }
                                //nextPageToken = result.has("next") ? result.getString("next") : "";
                                JSONObject result = new JSONObject(respoStr);

                                if (!result.has("results")) {
                                    //response_error = result.getString("");
                                } else {
                                    //  modelFanPostsArrayList.clear();

                                    if (swipeRefresh) {
                                        Log.d(TAG, "requestApiNodeList run: swipeRefresh clearAll");
                                            adapterDefault.clearAll();

                                    }
                                    JSONArray ja = result.getJSONArray("results");
                                    for (int j = 0; j < ja.length(); j++) {
                                        JSONObject jo = (JSONObject) ja.get(j);
                                        ModelFanPosts modelFanPosts = new ModelFanPosts(jo);
                                        TeaserModel teaserModel = new TeaserModel();
                                        teaserModel.setEntityType("node");
                                        teaserModel.setId(modelFanPosts.getNid());
                                        teaserModel.setTitle(modelFanPosts.getTitle());
                                        teaserModel.setField_image(modelFanPosts.getField_image());
                                        teaserModel.isPublished = modelFanPosts.isPublished();
                                        teaserModel.field_video = modelFanPosts.field_video;
                                        // modelFanPostsArrayList.add(modelFanPosts);
                                        if (modelFanPosts.isValidNodeType()) {

                                                adapterDefault.addOneRequestData(teaserModel, false);

                                        }
                                    }
                                        adapterDefault.notifyDataSetChanged();


                                }
                            } catch (Exception e) {
                                //response_error = getString(R.string.unable_to_connect);
                                e.printStackTrace();
                            }
                        }
                  //  }
                });

            }
        });
    }
}
