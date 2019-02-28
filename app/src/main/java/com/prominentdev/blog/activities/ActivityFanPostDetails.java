package com.prominentdev.blog.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.prominentdev.blog.BuildConfig;
import com.prominentdev.blog.R;
import com.prominentdev.blog.helpers.PDRestClient;
import com.prominentdev.blog.helpers.PDUtils;
import com.prominentdev.blog.models.ModelFanPosts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Narender Kumar on 11/22/2018.
 * For Prominent, Faridabad (India)
 * narender.kumar.nishad@gmail.com
 */
public class ActivityFanPostDetails extends ActivityBase {

    Toolbar toolbar;
    ImageView iv_a_fan_post_details_user_image, iv_a_fan_post_details_image;
    TextView tv_a_fan_post_details_title, tv_a_fan_post_details_body, tv_a_fan_post_details_category, no_connection_text;
    LinearLayout no_connection_ll;
    ScrollView sv_main;
    String response_error = "";
    String nid = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan_posts_details);

        nid = getIntent().getStringExtra("nid");
        init();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Post Detail");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        iv_a_fan_post_details_user_image = findViewById(R.id.iv_a_fan_post_details_user_image);
        iv_a_fan_post_details_image = findViewById(R.id.iv_a_fan_post_details_image);
        tv_a_fan_post_details_title = findViewById(R.id.tv_a_fan_post_details_title);
        tv_a_fan_post_details_body = findViewById(R.id.tv_a_fan_post_details_body);
        tv_a_fan_post_details_category = findViewById(R.id.tv_a_fan_post_details_category);
        no_connection_ll = findViewById(R.id.no_connection_ll);
        no_connection_text = findViewById(R.id.no_connection_text);
        sv_main = findViewById(R.id.sv_main);
        requestNewsList(nid);
    }

    private void requestNewsList(String nodeId) {
        String url = BuildConfig.API_ENPOINT + "node/" + nodeId + "?_format=json";
        RequestParams requestParams = new RequestParams();
        PDRestClient.get(context, url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                response_error = "";
                no_connection_ll.setVisibility(View.GONE);
                _progressDialogAsync.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject result) {
                super.onSuccess(statusCode, headers, result);
                _progressDialogAsync.cancel();
                try {
                    if (!result.has("nid")) {
                        response_error = "No response from server.";
                    } else {
                        ModelFanPosts modelFanPosts = new ModelFanPosts(result);
                        /*JSONArray title = result.getJSONArray("title");
                        JSONArray body = result.getJSONArray("body");
                        JSONArray category = result.getJSONArray("field_text_category");
                        JSONArray image = result.getJSONArray("field_image");
                        Log.d("123qwe", image.getJSONObject(0).getString("url"));*/
                        tv_a_fan_post_details_title.setText(modelFanPosts.getTitle());
                        tv_a_fan_post_details_body.setText(Html.fromHtml(modelFanPosts.getBody()));
                        tv_a_fan_post_details_category.setText(modelFanPosts.getField_text_category());
                        Glide.with(context)
                                .load(modelFanPosts.getField_image())
                                .into(iv_a_fan_post_details_image);
                    }
                } catch (Exception e) {
                    response_error = getString(R.string.unable_to_connect);
                    e.printStackTrace();
                }

                if (response_error == null || response_error.isEmpty()) {
                    no_connection_ll.setVisibility(View.GONE);
                    no_connection_text.setText("");
                    sv_main.setVisibility(View.VISIBLE);
                } else {
                    sv_main.setVisibility(View.GONE);
                    no_connection_ll.setVisibility(View.VISIBLE);
                    no_connection_text.setText(response_error);
                }
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
}