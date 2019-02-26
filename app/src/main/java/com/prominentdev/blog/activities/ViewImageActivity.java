package com.prominentdev.blog.activities;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.loopj.android.http.RequestParams;
import com.prominentdev.blog.BuildConfig;
import com.prominentdev.blog.R;
import com.prominentdev.blog.helpers.PDUtils;
import com.prominentdev.blog.models.ModelFanPosts;
import com.prominentdev.blog.widgets.ProgressDialogAsync;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

public class ViewImageActivity extends AppCompatActivity {
    ViewPager viewPager;
    ArrayList<ModelFanPosts> posts = new ArrayList<>();
    int position = 0;
    ImagePagerAdapter adapter;

    ImageView ivBack, ivLeft, ivRight;
    ProgressDialogAsync _progressDialogAsync;
    private String response_error = "";
    private boolean loading = false;
    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        _progressDialogAsync = new ProgressDialogAsync(this);

        viewPager = findViewById(R.id.viewPager);
        position = getIntent().getIntExtra("position", 0);
        page = getIntent().getIntExtra("page", 0);
        posts = (ArrayList<ModelFanPosts>) getIntent().getSerializableExtra("posts");
        adapter = new ImagePagerAdapter();
        viewPager.setAdapter(adapter);
        ivBack = findViewById(R.id.ivBack);
        ivRight = findViewById(R.id.ivRight);
        ivLeft = findViewById(R.id.ivLeft);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (posts.size() > 1) {
            ivRight.setVisibility(View.VISIBLE);
        } else {
            ivRight.setVisibility(View.GONE);
        }
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.arrowScroll(View.FOCUS_LEFT);
            }
        });
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.arrowScroll(View.FOCUS_RIGHT);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == posts.size() - 1) {
                    ivLeft.setVisibility(View.VISIBLE);
                    ivRight.setVisibility(View.GONE);
                } else if (i == 0) {
                    ivLeft.setVisibility(View.GONE);
                    if (posts.size() > 1) {
                        ivRight.setVisibility(View.VISIBLE);
                    } else
                        ivRight.setVisibility(View.GONE);
                } else {
                    ivLeft.setVisibility(View.VISIBLE);
                    ivRight.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        viewPager.setCurrentItem(position);
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(ViewImageActivity.this);
            View layout = (View) inflater.inflate(R.layout.row_pager_image, collection, false);
            collection.addView(layout);

            ImageView ivImage = layout.findViewById(R.id.ivImage);
            Glide.with(ViewImageActivity.this)
                    .load(posts.get(position).getField_image())
                    .into(ivImage);
            ivImage.setOnTouchListener(new ImageMatrixTouchHandler(layout.getContext()));
            if (position == posts.size() - 1) {
                page = page + 1;
                requestNewsList(page);
            }

            return layout;
        }

        public void addOneRequestData(ModelFanPosts model) {
            posts.add(model);
            ivRight.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
        }

        public List<ModelFanPosts> getAllModelPost() {
            return posts;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((LinearLayout) view);
        }

        @Override
        public int getCount() {
            return posts.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


    }

    private void requestNewsList(final int pageNumber) {
        String url = "redsox";
      /*  if (tab == 0) {
            url = "redsox";
        } else if (tab == 1) {
            url = "eagles";
        } else if (tab == 2) {
            url = "patriots";
        }*/


        response_error = "";
        //no_connection_ll.setVisibility(View.GONE);
        _progressDialogAsync.show();
        String newUrl = BuildConfig.API_ENPOINT + "myapi/v1/articles/" + url + "?page=" + pageNumber;

        File httpCacheDirectory = new File(this.getCacheDir(), "offlineCache");
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        //    no_connection_ll.setVisibility(View.VISIBLE);
                        //  no_connection_text.setText(e.toString());
                        PDUtils.showToast(ViewImageActivity.this, e.toString());
                        _progressDialogAsync.cancel();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String respoStr = response.body().string();
                runOnUiThread(new Runnable() {
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
                                //no_connection_ll.setVisibility(View.VISIBLE);
                                //no_connection_text.setText(response_error);
                            } else {
                                PDUtils.showToast(ViewImageActivity.this, response_error);
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
