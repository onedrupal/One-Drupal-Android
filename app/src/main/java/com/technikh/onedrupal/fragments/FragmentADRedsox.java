package com.technikh.onedrupal.fragments;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.RequestParams;
import com.otaliastudios.autocomplete.Autocomplete;
import com.otaliastudios.autocomplete.AutocompleteCallback;
import com.otaliastudios.autocomplete.AutocompletePolicy;
import com.otaliastudios.autocomplete.AutocompletePresenter;
//import com.stfalcon.imageviewer.StfalconImageViewer;
//import com.stfalcon.imageviewer.loader.ImageLoader;
import com.technikh.onedrupal.R;
import com.technikh.onedrupal.activities.ActivityFanPostDetails;
import com.technikh.onedrupal.activities.ViewImageActivity;
import com.technikh.onedrupal.adapter.AdapterFanPosts;
import com.technikh.onedrupal.adapter.GalleryAdapter;
import com.technikh.onedrupal.app.MyApplication;
import com.technikh.onedrupal.authenticator.AuthPreferences;
import com.technikh.onedrupal.helpers.PDUtils;
import com.technikh.onedrupal.helpers.UserPresenter;
import com.technikh.onedrupal.models.ModelFanPosts;
import com.technikh.onedrupal.models.SettingsType;
import com.technikh.onedrupal.models.User;
import com.technikh.onedrupal.models.VocabTerm;
import com.technikh.onedrupal.models.VocabTermsList;
import com.technikh.onedrupal.models.settingTaxonomyField;
import com.technikh.onedrupal.network.AddCookiesInterceptor;
import com.technikh.onedrupal.network.GetSiteDataService;
import com.technikh.onedrupal.network.RetrofitSiteInstance;
import com.technikh.onedrupal.util.EndlessRecyclerViewScrollListener;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import treeutil.MyObject;
import treeutil.Node;
import treeutil.Tree;
import treeutil.ArrowExpandSelectableHeaderHolder;

public class FragmentADRedsox extends FragmentBase implements View.OnClickListener {

    private String mSiteDomain, mSiteProtocol;

    LinearLayoutManager manager;
    RecyclerView rv_f_redsox_recycler;
    Button btnSearch, btn_filters_toggle, btn_filters_apply,btn_filters_reset;
    EditText et_search_keys;
    SwipeRefreshLayout swipeContainer;
    ArrayList<ModelFanPosts> modelFanPostsArrayList = new ArrayList<>();
    AdapterFanPosts adapterDefault;
    GalleryAdapter adapterGallery;
    Boolean galleryMode = false;
    LinearLayout no_connection_ll;
    TextView no_connection_text;
    private String TAG = "FragmentADRedsox";
    private String search_query = "";
    //private String category_uuid = "";
    private Autocomplete userAutocomplete;

    LinearLayout llRightMenu;
    SlidingPaneLayout slidingPaneLayout;
    //Spinner subjectSpinner;
    //private TreeNode root;
    //ArrayList<MyObject> categories = new ArrayList<>();
    HashMap<String, ArrayList<MyObject>> categoriesMap = new HashMap<String, ArrayList<MyObject>>();
    ArrayList<AndroidTreeView> vocabTreeViews = new ArrayList<>();
    Interceptor cacheInterceptor = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {

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
            okhttp3.Response originalResponse = chain.proceed(request);
            if (hasNetwork(getActivity())) {
                Log.d(TAG, "intercept: if");
                int maxAge = 60; // read from cache
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                Log.d(TAG, "intercept: else");
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

    public static FragmentADRedsox newInstance(int i, String protocol, String domain) {
        Bundle args = new Bundle();
        args.putInt("tab", i);
        args.putString("SiteProtocol", protocol);
        args.putString("SiteDomain", domain);
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
    public void onClick(View v) {
        //do what you want to do when button is clicked
        Log.d(TAG, "onClick: v.getId() "+v.getId());
        switch (v.getId()) {
            case R.id.btn_search:
                search_query = et_search_keys.getText().toString();
                slidingPaneLayout.openPane();
                requestNewsList(0, true);
                break;
            case R.id.btn_filters_toggle:
                Log.d(TAG, "onClick: btn_filters_toggle");
                if(!slidingPaneLayout.isOpen()) {
                    //btn_filters_toggle.setText("Show Filters");
                    slidingPaneLayout.openPane();
                }else {
                    //btn_filters_toggle.setText("Hide Filters");
                    slidingPaneLayout.closePane();
                }
                break;
            case R.id.btn_filters_apply:
                slidingPaneLayout.openPane();
                requestNewsList(0, true);
                break;
            case R.id.btn_filters_reset:
                Set set = categoriesMap.entrySet();
                Iterator iterator = set.iterator();
                while(iterator.hasNext()) {
                    Map.Entry mentry = (Map.Entry)iterator.next();
                    ArrayList<MyObject> categories1 = (ArrayList<MyObject>)mentry.getValue();
                    for (int counter = 0; counter < categories1.size(); counter++) {
                        MyObject category = categories1.get(counter);
                        if(category != null && category.isSelected()){
                            category.setSelected(false);
                        }
                    }
                }

                Log.d(TAG, "onClick: vocabTreeViews.size()"+vocabTreeViews.size());
                for (int counter = 0; counter < vocabTreeViews.size(); counter++) {
                    vocabTreeViews.get(counter).deselectAll();
                }
                requestNewsList(0, true);
                break;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            tab = getArguments().getInt("tab");
            mSiteProtocol = getArguments().getString("SiteProtocol");
            mSiteDomain = getArguments().getString("SiteDomain");
        }
        //subjectSpinner = (Spinner)view.findViewById(R.id.subjectSpinner);
        llRightMenu = view.findViewById(R.id.llRightMenu);
        slidingPaneLayout = view.findViewById(R.id.sliding_pane_layout);
        //slidingPaneLayout.openPane();
        slidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelOpened(View panel) {
                btn_filters_toggle.setText("Show Filters");

            }

            @Override
            public void onPanelClosed(View panel) {
                btn_filters_toggle.setText("Hide Filters");
            }
        });

        btn_filters_toggle = view.findViewById(R.id.btn_filters_toggle);
        btn_filters_toggle.setOnClickListener(this);

        btn_filters_apply = view.findViewById(R.id.btn_filters_apply);
        btn_filters_apply.setOnClickListener(this);
        btn_filters_reset = view.findViewById(R.id.btn_filters_reset);
        btn_filters_reset.setOnClickListener(this);
        btnSearch = view.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(this);
        et_search_keys = view.findViewById(R.id.et_search_keys);
        no_connection_ll = view.findViewById(R.id.no_connection_ll);
        no_connection_text = view.findViewById(R.id.no_connection_text);
        rv_f_redsox_recycler = view.findViewById(R.id.rv_f_redsox_recycler);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        rv_f_redsox_recycler.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        rv_f_redsox_recycler.setHasFixedSize(true);
        manager = new LinearLayoutManager(context);
        rv_f_redsox_recycler.setLayoutManager(manager);
        rv_f_redsox_recycler.setItemAnimator(new DefaultItemAnimator());
        if(!galleryMode) {
            adapterDefault = new AdapterFanPosts(context, modelFanPostsArrayList, new AdapterFanPosts.RecyclerViewClickListener() {
                @Override
                public void onItemClickListener(View v, int position) {
                    Log.d(TAG, "onItemClickListener: v.getId() " + v.getId());
                    Log.d(TAG, "onItemClickListener: tv_row_view_post " + R.id.tv_row_view_post);
                    if (v.getId() == R.id.tv_row_view_post) {
                        ModelFanPosts singleModelFanPosts = adapterDefault.getAllModelPost().get(position);
                        startActivity(new Intent(context, ActivityFanPostDetails.class)
                                .putExtra("nid", singleModelFanPosts.getNid())
                                .putExtra("SiteProtocol", mSiteProtocol)
                                .putExtra("SiteDomain", mSiteDomain)
                        );
                    }
                }
            });
            rv_f_redsox_recycler.setAdapter(adapterDefault);
        }else{

        }



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
                scrollListener.resetValues(manager);
                requestNewsList(0, true);
            }
        });
       // setupUserAutocomplete();

        requestNewsList(0, false);
        Log.d(TAG, "onViewCreated: tab"+tab);
        if(tab >= 2) {
            requestTaxonomyApiFilters();
        }
    }

    private void setupGalleryRecyclerView(){
        /*rv_f_redsox_recycler.setVisibility(View.GONE);
        List<String> RESOURCES = new ArrayList<>();
        RESOURCES.add("https://raw.githubusercontent.com/stfalcon-studio/StfalconImageViewer/master/images/posters/Vincent.jpg");
        RESOURCES.add("https://github.com/stfalcon-studio/StfalconImageViewer/blob/master/images/posters/Driver.jpg?raw=true");
        new StfalconImageViewer.Builder<String>(getContext(), RESOURCES, new ImageLoader<String>() {
            @Override
            public void loadImage(ImageView imageView, String image) {
                Log.d(TAG, "loadImage: "+image);
                Glide.with(getContext())
                        .load(image)
                        .into(imageView);
            }
        }).show();*/
        adapterGallery = new GalleryAdapter(context, modelFanPostsArrayList, new GalleryAdapter.RecyclerViewClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
               /* new StfalconImageViewer.Builder<ModelFanPosts>(getContext(), modelFanPostsArrayList, new ImageLoader<ModelFanPosts>() {
                    @Override
                    public void loadImage(ImageView imageView, ModelFanPosts image) {
                        Log.d(TAG, "loadImage: "+image.getField_image());
                        Glide.with(getContext())
                                .load(image.getField_image())
                                .into(imageView);
                    }
                }).show();*/
                Intent intent = new Intent(getActivity(), ViewImageActivity.class);
                intent.putExtra("posts", modelFanPostsArrayList);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        rv_f_redsox_recycler.setAdapter(adapterGallery);
    }

    private void constructTree(ArrayList<MyObject> categories1, final String vocabularyName) {
        Log.d(TAG, "constructTree: root tab "+tab);
        TreeNode root1 = TreeNode.root();
        Tree tree = new Tree();

        Iterator<Node> iterable = tree.buildTreeAndGetRoots(categories1);
        for (Iterator<Node> nodeIterator = iterable; nodeIterator.hasNext(); ) {
            Node node = nodeIterator.next();
            Log.e("Node", node.toString());
            Log.d(TAG, "constructTree: tab "+tab+" node.associatedObject"+node.associatedObject.name);
            if (node.children.size() == 0) {
                TreeNode treeNode = new TreeNode(node.associatedObject).setViewHolder(new ArrowExpandSelectableHeaderHolder(context, 0));
                root1 = root1.addChild(treeNode);
            } else {
                TreeNode parentNode = new TreeNode(node.associatedObject).setViewHolder(new ArrowExpandSelectableHeaderHolder(context, 0));
                root1 = root1.addChild(parentNode);

                addSubCHildren(parentNode, node.children, 0);
                //addTopicChildren(parentNode, node.children);
            }
        }
        final TreeNode root2 = root1;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AndroidTreeView tView = new AndroidTreeView(context, root2);
                //tView.getView().setId(Integer.parseInt(""));
//                 tView.expandAll();
                TextView valueTV = new TextView(context);
                valueTV.setText(vocabularyName);
                valueTV.setId(Integer.parseInt("5"));
                valueTV.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                llRightMenu.addView(valueTV);

                vocabTreeViews.add(tView);
                llRightMenu.addView(tView.getView());
                Log.d(TAG, "run: llRightMenu.addView");
                //tView.expandAll();
                tView.setDefaultAnimation(true);
                tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
                tView.setSelectionModeEnabled(true);
                //tView.setDefaultViewHolder(IconTreeItemHolder.class);
                /*tView.setDefaultNodeClickListener(new TreeNode.TreeNodeClickListener() {
                    @Override
                    public void onClick(TreeNode node, Object value) {
                        MyObject item = (MyObject) value;
                        // http://one-drupal-demo.technikh.com/onedrupal/content?search_keys=red&category_uuid=a3aeab28-3b45-4afa-a614-04503a94ec0a
                        Toast.makeText(context, item.name+"Node clicked", Toast.LENGTH_SHORT).show();

                        category_uuid = item.id;
                        Log.d(TAG, "onClick: category_uuid"+category_uuid);
                        requestNewsList(0, true);
                    }
                });*/
                llRightMenu.setVisibility(View.VISIBLE);
                btn_filters_toggle.setVisibility(View.VISIBLE);
                slidingPaneLayout.openPane();
            }
        });
        //slidingPaneLayout.closePane();
        btn_filters_toggle.setText("Hide Filters");
    }

    private void addTopicChildren(TreeNode parent, List<Node> myOList) {
        int videoCounter = 0;
        for (Node myO : myOList) {
            //TreeNode tn1 = new TreeNode(myO);
            TreeNode tn1 = new TreeNode(myO.associatedObject).setViewHolder(new ArrowExpandSelectableHeaderHolder(context, 0));
            tn1.setExpanded(true);
            parent.addChild(tn1);
            // too many videos is slowing the page scroll, LIMIT size
            //Log.d(TAG, "SIZZ parent.size():"+parent.size());
            //Log.d(TAG, "SIZZ parent.getLevel():"+parent.getLevel());
            if (myO.children.size() > 0) {
                addTopicChildren(tn1, myO.children);
            }
        }
    }

    private void addSubCHildren(TreeNode parentNode, List<Node> parent, int level) {
        level = level + 1;
        for (Node subparent : parent) {
            if (subparent.children.size() == 0) {
                TreeNode treeNode = new TreeNode(subparent.associatedObject).setViewHolder(new ArrowExpandSelectableHeaderHolder(context, level));
                parentNode.addChild(treeNode);
            } else {
                TreeNode parentsubNode = new TreeNode(subparent.associatedObject).setViewHolder(new ArrowExpandSelectableHeaderHolder(context, level));
                addSubCHildren(parentsubNode, subparent.children, level);
                parentNode.addChild(parentsubNode);
            }
        }
    }

    private void requestTaxonomyApiFilters() {
        Log.d(TAG, "requestTaxonomyApiFilters: tab "+tab);
        //String newUrl = "http://one-drupal-demo.technikh.com/onedrupal/api/v1/vocabulary/categories";
        SettingsType nTypeObj = MyApplication.gblGetNodeTypeFromPosition(tab-2);
        ArrayList<settingTaxonomyField> taxonomies = nTypeObj.getFieldsList().taxonomies;
        if(taxonomies == null || taxonomies.size() == 0){
            Log.d(TAG, "requestTaxonomyApiFilters: empty taxonomies"+nTypeObj.getNodeType());
            return;
        }
        /*String vocabName = nTypeObj.getFieldsList().taxonomies.get(0).mVocabulary;
        Log.d(TAG, "requestTaxonomyApiFilters: vocabName"+vocabName);
        vocabName = "all";*/
        List<String> vocabQuery = new ArrayList<String>();
        for (int counter = 0; counter < taxonomies.size(); counter++) {
            settingTaxonomyField taxonomy = taxonomies.get(counter);
            vocabQuery.add(taxonomy.mVocabulary);
        }
        String vocabName = TextUtils.join("+", vocabQuery);
        Log.d(TAG, "requestTaxonomyApiFilters: vocabName"+vocabName);


        //Create handle for the getRetrofitSiteInstance interface
        GetSiteDataService service = RetrofitSiteInstance.getRetrofitSiteInstance(mSiteProtocol, mSiteDomain, null).create(GetSiteDataService.class);
        //Call the method with parameter in the interface to get the employee data
        retrofit2.Call<VocabTermsList> call = service.getTaxonomyVocab(vocabName);
        Log.d(TAG, "requestTaxonomyApiFilters "+call.request().url() + "");
        call.enqueue(new retrofit2.Callback<VocabTermsList>() {
            @Override
            public void onResponse(retrofit2.Call<VocabTermsList> call, retrofit2.Response<VocabTermsList> response) {
                if (response.isSuccessful()) {
                    ArrayList<VocabTerm> vocabTermsList = response.body().getTypesArrayList();
                    for (int i = 0; i < nTypeObj.getFieldsList().taxonomies.size(); i++) {
                        settingTaxonomyField taxonomySettingField = nTypeObj.getFieldsList().taxonomies.get(i);
                        Log.d(TAG, "1onResponse: taxonomySettingField.mVocabulary"+taxonomySettingField.mVocabulary);
                        ArrayList<MyObject> categories1 = new ArrayList<>();
                        //ArrayList<String> spinnerItems = new ArrayList<>();
                        for (int j = 0; j < vocabTermsList.size(); j++) {
                            VocabTerm vTerm = vocabTermsList.get(j);
                            //Log.d(TAG, "1onResponse: vTerm "+vTerm.name);
                            Log.d(TAG, "1onResponse: vTerm.vocabularyId.get(0).getValue() "+vTerm.vocabularyId.get(0).getValue());
                            if(vTerm.vocabularyId.get(0).getValue().equals(taxonomySettingField.mVocabulary)) {
                                Log.d(TAG, "1onResponse: vid match "+taxonomySettingField.mVocabulary+vTerm.name.get(0).getValue());
                               /* if(vTerm.parentUUID.get(0).getValue() == null || vTerm.parentUUID.get(0).getValue().isEmpty()){
                                    spinnerItems.add(vTerm.name.get(0).getValue());
                                }else {*/
                                    MyObject child = new MyObject();
                                    child.name = vTerm.name.get(0).getValue();
                                    child.tid = vTerm.tid.get(0).getValue();
                                    child.vocabularyId = vTerm.vocabularyId.get(0).getValue();
                                    child.parentId = vTerm.parentTid.get(0).getValue();
                                    categories1.add(child);
                               // }
                            }
                        }
                      /*  if(spinnerItems.size() > 0) {
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                                    android.R.layout.simple_spinner_item, spinnerItems);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            dataAdapter.notifyDataSetChanged();
                            subjectSpinner.setAdapter(dataAdapter);
                        }else {*/
                            String vocabularyName = taxonomySettingField.mVocabulary;
                            categoriesMap.put(vocabularyName, categories1);
                            vocabularyName = vocabularyName.substring(0, 1).toUpperCase() + vocabularyName.substring(1);
                            constructTree(categories1, taxonomySettingField.mVocabulary);
                      //  }
                    }
                } else {

                }
            }
            @Override
            public void onFailure(retrofit2.Call<VocabTermsList> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
                //Toast.makeText(MyApplication.getAppContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });

/*
        Log.e("Url", newUrl);
        long cacheSize = (5 * 1024 * 1024);
        File httpCacheDirectory = new File(context.getCacheDir(), "http-cache");
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
                .cache(cache)
                .addInterceptor(new HttpLoggingInterceptor())
                .build();
        Request request = new Request.Builder().url(newUrl).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, final IOException e) {
            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
                String respoStr = response.body().string();
                try {
                    JSONObject object = new JSONObject(respoStr);
                    JSONArray dataArray = object.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject jsonObject = dataArray.getJSONObject(i);
                        MyObject child = new MyObject();
                        child.name = jsonObject.getJSONObject("attributes").getString("name");
                        child.id = jsonObject.getString("id");
                        child.parentId = jsonObject.getJSONObject("relationships").getJSONObject("parent").getJSONArray("data").getJSONObject(0).getString("id");
                        categories.add(child);
                    }
                    constructTree();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });*/
    }

    private void setupUserAutocomplete() {
        //EditText edit = (EditText) findViewById(R.id.single);
        float elevation = 6f;
        Drawable backgroundDrawable = new ColorDrawable(Color.WHITE);
        AutocompletePresenter<User> presenter = new UserPresenter(context);
        AutocompleteCallback<User> callback = new AutocompleteCallback<User>() {
            @Override
            public boolean onPopupItemClicked(Editable editable, User item) {
                editable.clear();
                editable.append(item.getFullname());
                return true;
            }

            public void onPopupVisibilityChanged(boolean shown) {}
        };

        userAutocomplete = Autocomplete.<User>on(et_search_keys)
                .with(elevation)
                .with(backgroundDrawable)
                .with(presenter)
                .with(callback)
                .build();
        //userAutocomplete.showPopup(" ");
    }

    public class SimplePolicy implements AutocompletePolicy {
        @Override
        public boolean shouldShowPopup(Spannable text, int cursorPos) {
            return text.length() > 0;
        }

        @Override
        public boolean shouldDismissPopup(Spannable text, int cursorPos) {
            return text.length() == 0;
        }

        @Override
        public CharSequence getQuery(Spannable text) {
            return text;
        }

        @Override
        public void onDismiss(Spannable text) {}
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
        String urlSlug = "content";
        if (tab == 0) {
            urlSlug = "content";
        } else if (tab == 1) {
            urlSlug = "promoted-content";
        } else if (tab > 1) {
            SettingsType nTypeObj = MyApplication.gblGetNodeTypeFromPosition(tab-2);
            if(nTypeObj != null) {
                urlSlug = "content/" + nTypeObj.getNodeType();
            }else{
                urlSlug = "content";
            }
            if(nTypeObj.getNodeType().equals("movies") || nTypeObj.getNodeType().equals("tb_page")){
                galleryMode = true;
                setupGalleryRecyclerView();
            }
        }

        response_error = "";
        no_connection_ll.setVisibility(View.GONE);
        _progressDialogAsync.show();
        AuthPreferences mAuthPreferences;
        mAuthPreferences = new AuthPreferences(context);
        String site_domain = mSiteDomain;
        String site_protocol = mSiteProtocol;
        Log.d(TAG, "requestNewsList: "+mSiteDomain);
        if(mSiteDomain == null || mSiteDomain.isEmpty()) {
             site_domain = mAuthPreferences.getPrimarySiteUrl();
             site_protocol = mAuthPreferences.getPrimarySiteProtocol();
        }
        if(site_domain == null){
            return;
        }
        Set set = categoriesMap.entrySet();
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
            // First taxonomy term ID contextual filter is for exact match, depth 0. so send first filter as all
            urlSlug = urlSlug+"/all/"+TextUtils.join("/", tidFiltersAND);
        }

        String newUrl = site_protocol+site_domain + "/onedrupal/api/v1/" + urlSlug + "?page=" + pageNumber;
        if(!search_query.isEmpty()){
            newUrl = newUrl+"&search_keys="+search_query;
        }

        Log.d("GET Content search_keys", "requestNewsList: "+newUrl);
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
                Log.d("GET Content", "onResponse: "+respoStr);
                if(!response.isSuccessful()){
                    if(response.code() == 404) {
                        response_error = "Make sure the drupal site & one_api module is configured correctly with View onedrupal_api_all_published_content enabled!";
                    }else if(response.code() == 403) {
                        response_error = "Make sure user has access to the View onedrupal_api_all_published_content in Drupal site!";
                    }
                }
                //Log.d(TAG, "onResponse: response_error"+response_error);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        _progressDialogAsync.cancel();
                        if(response_error== null || response_error.isEmpty()) {
                            //Log.d(TAG, "run: response_error not null not empty"+response_error);
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
                                        if(galleryMode){
                                            adapterGallery.clearAll();
                                        }else {
                                            adapterDefault.clearAll();
                                        }
                                    }
                                    JSONArray ja = result.getJSONArray("results");
                                    for (int j = 0; j < ja.length(); j++) {
                                        JSONObject jo = (JSONObject) ja.get(j);
                                        ModelFanPosts modelFanPosts = new ModelFanPosts(jo);
                                        // modelFanPostsArrayList.add(modelFanPosts);
                                        if (modelFanPosts.isValidNodeType()) {
                                            if(galleryMode){
                                                Log.d(TAG, "run: galleryMode addOneRequestData "+j);
                                                adapterGallery.addOneRequestData(modelFanPosts);
                                            }else {
                                                adapterDefault.addOneRequestData(modelFanPosts);
                                            }
                                        }
                                    }
                                    if(galleryMode){
                                        adapterGallery.notifyDataSetChanged();
                                       // viewerView.updateImages(images);
                                    }else {
                                        adapterDefault.notifyDataSetChanged();
                                    }

                                }
                            } catch (Exception e) {
                                response_error = getString(R.string.unable_to_connect);
                                e.printStackTrace();
                            }
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
                           /* if (adapter.getAllModelPost().size() <= 0) {
                                no_connection_ll.setVisibility(View.VISIBLE);
                                no_connection_text.setText(response_error);
                            } else {
                                PDUtils.showToast(context, response_error);
                            }*/
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
                    //Log.d(TAG, "intercept: if chain "+cacheControl);
                    if(true)
                        return originalResponse;
                    CacheControl cc = new CacheControl.Builder()
                            .maxStale(1, TimeUnit.DAYS)
                            .build();



                    request = request.newBuilder()
                            .cacheControl(cc)
                            .build();
                    return originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .header("Cache-Control", "public, max-age=" + 5000)
                            .build();
                    //return chain.proceed(request);

                } else {
                    Log.d(TAG, "intercept: else originalResponse");
                    return originalResponse;
                }
            }
        };

    }


    private Interceptor provideOfflineCacheInterceptor() {

        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Log.d(TAG, "intercept: provideOfflineCacheInterceptor");
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