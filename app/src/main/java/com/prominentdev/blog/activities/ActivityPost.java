package com.prominentdev.blog.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.prominentdev.blog.BuildConfig;
import com.prominentdev.blog.R;
import com.prominentdev.blog.helpers.PDRestClient;
import com.prominentdev.blog.helpers.PDUtils;
import com.prominentdev.blog.helpers.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Narender Kumar on 9/30/2018.
 * For Prominent Developers, Faridabad (India)
 */

public class ActivityPost extends ActivityBase implements View.OnClickListener {

    Toolbar toolbar;
    EditText et_f_post;
    ImageView iv_f_post_image, iv_f_post_preview;
    RadioButton rb_f_post_redsox, rb_f_post_eagles, rb_f_post_patriots;
    LinearLayout ll_f_post_publish;
    public static final int REQUEST_CODE_CROP_ACTIVITY_PROFILE = 998;

    private int PICK_IMAGE_REQUEST = 100;
    private String postImagePath = "", xCSRFToken = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        init();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        getXCSRFToken();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.post);

        et_f_post = findViewById(R.id.et_f_post);
        iv_f_post_image = findViewById(R.id.iv_f_post_image);
        iv_f_post_image.setOnClickListener(this);
        iv_f_post_preview = findViewById(R.id.iv_f_post_preview);
        iv_f_post_preview.setOnClickListener(this);
        rb_f_post_redsox = findViewById(R.id.rb_f_post_redsox);
        rb_f_post_eagles = findViewById(R.id.rb_f_post_eagles);
        rb_f_post_patriots = findViewById(R.id.rb_f_post_patriots);
        ll_f_post_publish = findViewById(R.id.ll_f_post_publish);
        ll_f_post_publish.setOnClickListener(this);
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.hasExtra(ActivityImageSelector.REQUEST_SELECTED_IMAGE_PATH)) {
            if (requestCode == REQUEST_CODE_CROP_ACTIVITY_PROFILE) {
                postImagePath = data.getStringExtra(ActivityImageSelector.REQUEST_SELECTED_IMAGE_PATH);
                File file = new File(postImagePath);
                Glide.with(context).load(new File(postImagePath))
                        .apply(RequestOptions.centerInsideTransform())
                        .into(iv_f_post_preview);
                if (PDUtils.isNetworkConnected(context)) {
                    doMultiPartRequest(postImagePath);
                } else {
                    PDUtils.showToast(context, getString(R.string.check_internet_connection));
                }
            }
        }

        //*****

        /*switch (requestCode) {
            case 100:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    try {
                        bitmap = android.provider.MediaStore.Images.Media
                                .getBitmap(cr, selectedImage);

                        iv_f_post_preview.setImageBitmap(bitmap);
                        Toast.makeText(this, selectedImage.toString(),
                                Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                                .show();

                    }
                }
        }*/

        /*if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            final Uri filePath = data.getData();
            File file = new File(filePath.toString());
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                iv_f_post_preview.setImageBitmap(bitmap);
                if (PDUtils.isNetworkConnected(context)) {
                    doMultiPartRequest(file);
                } else {
                    Toast.makeText(this, getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    private void getXCSRFToken() {
        String url = "http://shankarpally-portal.eschool2go.com/index.php/rest/session/token";
        Log.d("makeBlogPost", "in getXCSRFToken "+url);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            xCSRFToken = response.body().string();
            Log.d("makeBlogPost", "in xCSRFToken "+xCSRFToken);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void doMultiPartRequest(String file) {

        byte[] buf = new byte[0];
        try {
            InputStream in = new FileInputStream(new File(file));
            buf = new byte[in.available()];
            while (in.read(buf) != -1) ;
        } catch (IOException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        Log.d("OKHTTP", "Called Actual Request");
        String url = BuildConfig.API_ENPOINT + "file/upload/node/article/field_image?_format=json";
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(Headers.of("Content-Disposition", "file;filename=\" + \"img_0000001.jpg"), RequestBody.create(MediaType.parse("image/jpeg"), buf))
                //.addFormDataPart("image", "Image_01", RequestBody.create(MediaType.parse("image/jpeg"), file))
                .build();
        Log.d("OKHTTP", "Request Body Generated");

        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/octet-stream")
                //.header("Content-Disposition", "file;filename=" + "img_0000001.jpg")
                .addHeader("Google-Access-Token", sessionManager.getParticularField(SessionManager.ACCESS_TOKEN))
                .addHeader("X-CSRF-Token", xCSRFToken)
                .url(url)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            Log.d("OKHTTP", "Request Body Generated");
            Log.d("OKHTTP", response.body().string());
        } catch (IOException e) {
            Log.d("OKHTTP", "Exception Occured");
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_f_post_image:
                showFileChooser();
                /*startActivityForResult(
                        ActivityImageSelector.build(context, ContextCompat.getColor(context, R.color.app_primary)),
                        REQUEST_CODE_CROP_ACTIVITY_PROFILE);*/
                break;
            case R.id.iv_f_post_preview:
                //showFileChooser();
                startActivityForResult(
                        ActivityImageSelector.build(context, ContextCompat.getColor(context, R.color.app_primary)),
                        REQUEST_CODE_CROP_ACTIVITY_PROFILE);
                break;
            case R.id.ll_f_post_publish:
                makeBlogPost();
                break;
        }
    }

    private void makeBlogPost() {
        Log.d("makeBlogPost", "in makeBlogPost");
        Log.d("makeBlogPostAccessToken", sessionManager.getParticularField(SessionManager.ACCESS_TOKEN));
        Log.d("makeBlogPost CSRFToken", xCSRFToken);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Google-Access-Token", sessionManager.getParticularField(SessionManager.ACCESS_TOKEN));
        client.addHeader("X-CSRF-Token", xCSRFToken);
        try {
            JSONObject jsonParams = new JSONObject();
            /*
            {
	"title": [{
		"value": "redsox blog5"
	}],
	"type": [{
		"target_id": "article"
	}],
	"field_text_category": [{
		"value": "redsox"
	}],
             */
            JSONObject titleObject = new JSONObject();
            titleObject.put("value", et_f_post.getText());
            JSONArray titleArray = new JSONArray();
            titleArray.put(titleObject);
            jsonParams.put("title", titleArray);

            JSONObject typeObject = new JSONObject();
            typeObject.put("target_id", "article");
            JSONArray typeArray = new JSONArray();
            typeArray.put(typeObject);
            jsonParams.put("type", typeArray);

            JSONObject categoryObject = new JSONObject();
            categoryObject.put("value", "redsox");
            JSONArray categoryArray = new JSONArray();
            categoryArray.put(categoryObject);
            jsonParams.put("field_text_category", categoryArray);
            Log.d("makeBlogPost", jsonParams.toString());
            StringEntity entity = new StringEntity(jsonParams.toString());
            client.post(context, BuildConfig.API_ENPOINT + "node?_format=json", entity,
                    "application/json", new JsonHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            _progressDialogAsync.show();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            _progressDialogAsync.cancel();
                            PDUtils.showToast(context, "No data");

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            PDUtils.showToast(context, PDRestClient.getHTTPErrorMessage(statusCode));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            PDUtils.showToast(context, PDRestClient.getHTTPErrorMessage(statusCode));
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            _progressDialogAsync.cancel();
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}