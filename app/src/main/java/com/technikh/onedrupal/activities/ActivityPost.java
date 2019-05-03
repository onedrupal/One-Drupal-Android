package com.technikh.onedrupal.activities;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.otaliastudios.autocomplete.Autocomplete;
import com.otaliastudios.autocomplete.AutocompleteCallback;
import com.otaliastudios.autocomplete.AutocompletePresenter;
import com.otaliastudios.autocomplete.CharPolicy;
import com.technikh.onedrupal.R;
import com.technikh.onedrupal.adapter.AutoSuggestAdapter;
import com.technikh.onedrupal.app.MyApplication;
import com.technikh.onedrupal.authenticator.AuthPreferences;
import com.technikh.onedrupal.helpers.PDUtils;
import com.technikh.onedrupal.helpers.UserPresenter;
import com.technikh.onedrupal.models.ImageUploadResponse;
import com.technikh.onedrupal.models.ModelNodeType;
import com.technikh.onedrupal.models.OneMultipleTermsItemModel;
import com.technikh.onedrupal.models.OneMultipleTermsItemTermModel;
import com.technikh.onedrupal.models.OneMultipleTermsModel;
import com.technikh.onedrupal.models.SettingsType;
import com.technikh.onedrupal.models.User;
import com.technikh.onedrupal.models.VocabSimpleTerm;
import com.technikh.onedrupal.models.VocabTermsSimpleList;
import com.technikh.onedrupal.network.AddCookiesInterceptor;
import com.technikh.onedrupal.network.ApiCall;
import com.technikh.onedrupal.network.GetSiteDataService;
import com.technikh.onedrupal.network.RetrofitSiteInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.github.mthli.knife.KnifeText;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//import com.android.volley.Response;

public class ActivityPost extends ActivityBase implements View.OnClickListener {

    Toolbar toolbar;
    EditText et_f_post, et_remote_image_url, et_remote_page_url, et_remote_video_url;
    MultiAutoCompleteTextView et_taxonomy_category_auto;
    AutoCompleteTextView et_taxonomy_category_auto_sngle_categ;
    ImageView iv_f_post_preview;
    Button iv_f_post_image;
    //CheckBox cb_published, cb_promoted;
    //RadioButton rb_f_post_redsox, rb_f_post_eagles, rb_f_post_patriots;
    LinearLayout ll_f_post_publish;
    public static final int REQUEST_CODE_CROP_ACTIVITY_PROFILE = 998;

    private int PICK_IMAGE_REQUEST = 100;
    private String postImagePath = "", xCSRFToken = "";
    private ImageUploadResponse imageUploadResponse;
    private static String TAG = "ActivityPost";
    private FirebaseAnalytics mFirebaseAnalytics;

    private Autocomplete userAutocomplete;
    private ArrayList<OneMultipleTermsItemModel> oneMultipleTermsItemsList;
    private Handler autoCompleteHandler, autoCompleteSngleHandler;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Bitmap glideBitmap;


    //private static final String BOLD = "<b>Write blog details here</b>";
//    private static final String ITALIT = "<i>Italic</i><br><br>";
//    private static final String UNDERLINE = "<u>Underline</u><br><br>";
//    private static final String STRIKETHROUGH = "<s>Strikethrough</s><br><br>"; // <s> or <strike> or <del>
//    private static final String BULLET = "<ul><li>asdfg</li></ul>";
//    private static final String QUOTE = "<blockquote>Quote</blockquote>";
//    private static final String LINK = "<a href=\"https://github.com/mthli/Knife\">Link</a><br><br>";
    //private static final String EXAMPLE = BOLD + ITALIT + UNDERLINE + STRIKETHROUGH + BULLET + QUOTE + LINK;
    //private static final String EXAMPLE = BOLD ;

    private KnifeText knife;
    private String nodeType = "";
    private String imageField = "";
    private String bodyField = "";
    private String remotePageField = "";
    private String taxonomyField = "", taxonomyFieldSingleCategory = "", taxonomyFieldVocabulary = "", taxonomyFieldSnglCategVocab = "";
    private String remoteImageField = "";
    private String remoteVideoField = "";
    private boolean editMode = false;
    private String edit_nid = "";

    private AuthPreferences mAuthPreferences;

    private AutoSuggestAdapter autoSuggestAdapter, autoSuggestSnglAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuthPreferences = new AuthPreferences(this);

        autoSuggestAdapter = new AutoSuggestAdapter(this,
                R.layout.row_autocomplete);
        autoSuggestSnglAdapter = new AutoSuggestAdapter(this,
                R.layout.row_autocomplete);

        //fetchTaxonomyTermNames();
        init();

        xCSRFToken = mAuthPreferences.getAuthToken();

        Bundle b = getIntent().getExtras();
        if(b != null) {
            nodeType = b.getString("nodeType");
            editMode = b.getBoolean("editMode");
        }
        if(nodeType!= null && !nodeType.isEmpty()) {
            SettingsType NodeTypeObj = MyApplication.gblGetNodeTypeObj(nodeType);
            if (NodeTypeObj != null) {

                imageField = MyApplication.gblGetImageFieldName(nodeType);
                bodyField = MyApplication.gblGetBodyFieldName(nodeType);
                remotePageField = NodeTypeObj.getFieldsList().remote_page;
                if (NodeTypeObj.getFieldsList().taxonomies.size() > 0) {
                    if (NodeTypeObj.getFieldsList().taxonomies.get(0).mAutoCreate) {
                        // auto create: true -> Tags
                        taxonomyField = NodeTypeObj.getFieldsList().taxonomies.get(0).mFieldName;
                        taxonomyFieldVocabulary = NodeTypeObj.getFieldsList().taxonomies.get(0).mVocabulary;
                    } else {
                        taxonomyFieldSingleCategory = NodeTypeObj.getFieldsList().taxonomies.get(0).mFieldName;
                        taxonomyFieldSnglCategVocab = NodeTypeObj.getFieldsList().taxonomies.get(0).mVocabulary;
                    }
                }
                if (NodeTypeObj.getFieldsList().taxonomies.size() > 1) {
                    if (taxonomyField.isEmpty()) {
                        taxonomyField = NodeTypeObj.getFieldsList().taxonomies.get(1).mFieldName;
                        taxonomyFieldVocabulary = NodeTypeObj.getFieldsList().taxonomies.get(1).mVocabulary;
                    } else {
                        taxonomyFieldSingleCategory = NodeTypeObj.getFieldsList().taxonomies.get(1).mFieldName;
                        taxonomyFieldSnglCategVocab = NodeTypeObj.getFieldsList().taxonomies.get(1).mVocabulary;
                    }
                }
                Log.d(TAG, "onCreate: taxonomyField" + taxonomyField);
                Log.d(TAG, "onCreate: taxonomyFieldSingleCategory" + taxonomyFieldSingleCategory);
                remoteImageField = NodeTypeObj.getFieldsList().remote_image;
                remoteVideoField = NodeTypeObj.getFieldsList().remote_video;
                Log.d(TAG, "onCreate: bodyField " + bodyField + " nodeType " + nodeType);
            }
        }
        setupTaxonomyFieldAutocomplete();

        if(imageField != null && !imageField.isEmpty()){
            iv_f_post_preview.setVisibility(View.VISIBLE);
            iv_f_post_image.setVisibility(View.VISIBLE);
        }
        if(bodyField != null && !bodyField.isEmpty()){
            RelativeLayout lBody = findViewById(R.id.layout_body);
            lBody.setVisibility(View.VISIBLE);
        }
        if(remoteImageField != null && !remoteImageField.isEmpty()){
            et_remote_image_url.setVisibility(View.VISIBLE);
        }
        if(remotePageField != null && !remotePageField.isEmpty()){
            et_remote_page_url.setVisibility(View.VISIBLE);
        }
        if(taxonomyField != null && !taxonomyField.isEmpty()){
            et_taxonomy_category_auto.setVisibility(View.VISIBLE);
        }
        if(taxonomyFieldSingleCategory != null && !taxonomyFieldSingleCategory.isEmpty()){
            et_taxonomy_category_auto_sngle_categ.setVisibility(View.VISIBLE);
            et_taxonomy_category_auto_sngle_categ.setHint(taxonomyFieldSingleCategory);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                et_taxonomy_category_auto_sngle_categ.setTooltipText(taxonomyFieldSingleCategory);
            }
        }
        if(remoteVideoField != null && !remoteVideoField.isEmpty()){
            et_remote_video_url.setVisibility(View.VISIBLE);
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        knife = (KnifeText) findViewById(R.id.knife);
        // ImageGetter coming soon...
        knife.setSelection(knife.getEditableText().length());



        setupBold();
        setupItalic();
        setupUnderline();
        setupStrikethrough();
        setupBullet();
        setupQuote();
        setupLink();
        setupClear();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        //getXCSRFToken();
        if(editMode){
            edit_nid = b.getString("nID");
            Log.d(TAG, "onCreate: "+edit_nid);
            et_f_post.setText(b.getString("nTitle"));
            knife.fromHtml(b.getString("nBody"));
            if(!b.getString("nImage").isEmpty()){
                iv_f_post_preview.setVisibility(View.VISIBLE);
                iv_f_post_image.setVisibility(View.VISIBLE);
                //Log.d(TAG, "onCreate: "+b.getString("nImage"));
                Glide.with(context)
                        .load(b.getString("nImage"))
                        .into(iv_f_post_preview);
            }
            toolbar.setTitle("Edit "+nodeType);
            et_taxonomy_category_auto.setText(b.getString("nTagsMulti"));
        }else{
            //knife.fromHtml(EXAMPLE);
            toolbar.setTitle("POST "+nodeType);
            // Defaults from SharedDataReceiverActivity
            if(b != null) {
                String nodeRemotePage = b.getString("nRemotePage");
                Log.d(TAG, "onCreate: nodeRemotePage "+nodeRemotePage);
                String host = "";
                if(nodeRemotePage != null && !nodeRemotePage.isEmpty()){
                    et_remote_page_url.setText(nodeRemotePage);
                    try {
                        URL url = new URL(nodeRemotePage);
                        host = url.getHost()+", ";
                        et_taxonomy_category_auto.setText(host);
                    }catch(MalformedURLException e){
                        e.printStackTrace();
                    }
                    //et_remote_page_url.setVisibility(View.VISIBLE);
                }
                String nodeRemoteImage = b.getString("nRemoteImage");
                if(nodeRemoteImage != null && !nodeRemoteImage.isEmpty()){
                    et_remote_image_url.setText(nodeRemoteImage);
                    try {
                        URL url = new URL(nodeRemoteImage);
                        host = host + url.getHost()+", ";
                        et_taxonomy_category_auto.setText(host);
                    }catch(MalformedURLException e){
                        e.printStackTrace();
                    }
                    //et_remote_image_url.setVisibility(View.VISIBLE);
                }
                String nodeRemoteVideo = b.getString("nRemoteVideo");
                if(nodeRemoteVideo != null && !nodeRemoteVideo.isEmpty()){
                    et_remote_video_url.setText(nodeRemoteVideo);
                }
                String nodeTitle = b.getString("nTitle");
                if(nodeTitle != null && !nodeTitle.isEmpty()){
                    et_f_post.setText(nodeTitle);
                }
                String nodeBody = b.getString("nBody");
                if(nodeBody != null && !nodeBody.isEmpty()){
                    Log.d(TAG, "onCreate: setting body1 to "+ nodeBody);
                    nodeBody = nodeBody.replaceAll("\\\\n", "<br>");
                    Log.d(TAG, "onCreate: setting body2 to "+ nodeBody);
                    knife.fromHtml(nodeBody);
                }
                String nImage = b.getString("nImage");
                if(nImage != null && !nImage.isEmpty()){
                    Uri uriData = Uri.parse(b.getString("nImage"));
                    iv_f_post_preview.setVisibility(View.VISIBLE);
                    iv_f_post_image.setVisibility(View.VISIBLE);
                    //Log.d(TAG, "onCreate: "+b.getString("nImage"));
                    Glide.with(context)
                            .load(uriData)
                            .into(iv_f_post_preview);
                }else if(nodeRemoteImage != null && !nodeRemoteImage.isEmpty()){
                    Glide.with(context)
                            .asBitmap()
                            .load(nodeRemoteImage)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    iv_f_post_preview.setImageBitmap(resource);
                                    glideBitmap = resource;
                                    postImageTextBodyAppend();
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });
                }

            }
        }
    }

    private void postImageTextBodyAppend() {
        if(glideBitmap == null){
            return;
        }
        FirebaseVisionImage visionImage = FirebaseVisionImage.fromBitmap(glideBitmap);

        FirebaseVisionTextRecognizer visionDetector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        Log.d(TAG, "postImageTextBodyAppend: visionDetector ");
        Task<FirebaseVisionText> result =
                visionDetector.processImage(visionImage)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                Log.d(TAG, "visionDetector onSuccess: " + firebaseVisionText.getText());
                                if(!firebaseVisionText.getText().isEmpty()) {
                                    String origBody = knife.toHtml();
                                    knife.fromHtml(origBody + "<br /><br />"+firebaseVisionText.getText());
                                }
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        Log.d(TAG, "visionDetector onFailure: "+e.getMessage());
                                    }
                                });
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        et_remote_image_url = findViewById(R.id.et_remote_image_url);
        et_remote_page_url = findViewById(R.id.et_remote_page_url);
        et_remote_video_url = findViewById(R.id.et_remote_video_url);
        et_f_post = findViewById(R.id.et_f_post);
        iv_f_post_image = findViewById(R.id.iv_f_post_image);
        iv_f_post_image.setOnClickListener(this);
        iv_f_post_preview = findViewById(R.id.iv_f_post_preview);
        //iv_f_post_preview.setOnClickListener(this);
        /*rb_f_post_redsox = findViewById(R.id.rb_f_post_redsox);
        rb_f_post_eagles = findViewById(R.id.rb_f_post_eagles);
        rb_f_post_patriots = findViewById(R.id.rb_f_post_patriots);*/
        //cb_published = findViewById(R.id.checkBoxPublished);
        //cb_promoted = findViewById(R.id.checkBoxPromoted);
        ll_f_post_publish = findViewById(R.id.ll_f_post_publish);
        ll_f_post_publish.setOnClickListener(this);
        //setupUserAutocomplete();
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
        String url = mAuthPreferences.getPrimarySiteProtocol()+mAuthPreferences.getPrimarySiteUrl()+"/rest/session/token";
        //String url = "http://shankarpally-portal.eschool2go.com/index.php/rest/session/token";
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

        //OkHttpClient client = new OkHttpClient();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new AddCookiesInterceptor())
                //.addInterceptor(new ReceivedCookiesInterceptor())
                .build();
        Log.d("OKHTTP", "Called Actual Request");
        String url = mAuthPreferences.getPrimarySiteProtocol()+mAuthPreferences.getPrimarySiteUrl()+"/file/upload/node/"+nodeType+"/"+imageField+"?_format=json";
        //String url = BuildConfig.API_ENPOINT + "file/upload/node/"+nodeType+"/"+imageField+"?_format=json";
//        RequestBody body = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addPart(Headers.of("Content-Disposition", "file;filename=\" + \"img_0000001.jpg"), RequestBody.create(MediaType.parse("image/jpeg"), buf))
//                //.addFormDataPart("image", "Image_01", RequestBody.create(MediaType.parse("image/jpeg"), file))
//                .build();
        Log.d("OKHTTP", "Request Body Generated");



//        Request request = new Request.Builder()
//                .addHeader("Content-Type", "application/octet-stream")
//                //.header("Content-Disposition", "file;filename=" + "img_0000001.jpg")
//                .addHeader("Google-Access-Token", sessionManager.getParticularField(SessionManager.ACCESS_TOKEN))
//                .addHeader("X-CSRF-Token", xCSRFToken)
//                .url(url)
//                .post(body)
//                .build();

        File partFile = new File(file);
        RequestBody fbody = RequestBody.create(MediaType.parse("image"), partFile);



        Request request = new Request.Builder()
                .header("Content-Type", "application/octet-stream")
                .header("Content-Disposition", "file; filename=\""+partFile.getName()+"\"")
               // .header("Google-Access-Token", sessionManager.getParticularField(SessionManager.ACCESS_TOKEN))
                //.header("X-CSRF-Token", xCSRFToken)
                .url(url)
                .post(fbody)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            Log.e("OKHTTP", "Request Body Generated");

            String serverResponse=response.body().string();

            Log.e("OKHTTP response", serverResponse);

            if(!TextUtils.isEmpty(serverResponse))
            {
                imageUploadResponse=new Gson().fromJson(serverResponse,ImageUploadResponse.class);
                if(imageUploadResponse.getFid() == null){
                    //PDUtils.showToast(context, "Image not available!");
                    String message_prefix = getString(R.string.dialog_api_failed_message_prefix);
                    if(response.code() == 404){
                        message_prefix = "Make sure REST resource (/file/upload/{entity_type_id}/{bundle}/{field_name}: POST) is Enabled!";
                    }else if(response.code() == 403){
                        message_prefix = "Make sure user has access to API /file/upload/{entity_type_id}/{bundle}/{field_name}! Try Logging out and logging back in!!";
                    }
                    Glide.with(context)
                            .clear(iv_f_post_preview);
                    new AlertDialog.Builder(ActivityPost.this)
                            .setTitle(getString(R.string.dialog_api_failed_title))
                            .setMessage(message_prefix+" -> "+serverResponse)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }

        } catch (IOException e) {
            Log.d("OKHTTP", "Exception Occured");
            e.printStackTrace();
            PDUtils.showToast(context, "Image not available!");
        } catch (Exception e) {
            Log.d("OKHTTP", "Exception Occured");
            e.printStackTrace();
            PDUtils.showToast(context, "Image not available!");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_f_post_image:
                startActivityForResult(
                        ActivityImageSelector.build(context, ContextCompat.getColor(context, R.color.app_primary)),
                        REQUEST_CODE_CROP_ACTIVITY_PROFILE);
                //showFileChooser();
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
                if(taxonomyField != null && !taxonomyField.isEmpty() && !et_taxonomy_category_auto.getText().toString().isEmpty()) {
                    postMultipleTaxonomyTermsCreation();
                }else {
                    makeBlogPost();
                }
                //makeBlogPost();
                break;
        }
    }

    private void postMultipleTaxonomyTermsCreation(){
        _progressDialogAsync.show();
        //Log.d(TAG, "drupalNodeEditProperty: "+nid);
        GetSiteDataService service = RetrofitSiteInstance.getRetrofitSiteInstance(mAuthPreferences.getPrimarySiteProtocol(), mAuthPreferences.getPrimarySiteUrl(), null).create(GetSiteDataService.class);
        //Call the method with parameter in the interface to get the employee data
        OneMultipleTermsModel termsModelObj = new OneMultipleTermsModel();

        if(!taxonomyField.isEmpty() && !et_taxonomy_category_auto.getText().toString().isEmpty()) {
            Log.d(TAG, "postMultipleTaxonomyTermsCreation: taxonomyField");
            OneMultipleTermsItemModel termsItemModelObj = new OneMultipleTermsItemModel();
            termsItemModelObj.field_name = taxonomyField;
            termsItemModelObj.tags = et_taxonomy_category_auto.getText().toString();
            termsItemModelObj.vid = taxonomyFieldVocabulary;

            //OneMultipleTermsItemTermModel termsItemTermModelObj = new OneMultipleTermsItemTermModel();
            termsModelObj.items = new ArrayList<OneMultipleTermsItemModel>();
            termsModelObj.items.add(termsItemModelObj);
        }

        if(!taxonomyFieldSingleCategory.isEmpty() && !et_taxonomy_category_auto_sngle_categ.getText().toString().isEmpty()) {
            Log.d(TAG, "postMultipleTaxonomyTermsCreation: taxonomyFieldSingleCategory");
            OneMultipleTermsItemModel termsItemModelObj = new OneMultipleTermsItemModel();
            termsItemModelObj.field_name = taxonomyFieldSingleCategory;
            termsItemModelObj.tags = et_taxonomy_category_auto_sngle_categ.getText().toString();
            termsItemModelObj.vid = taxonomyFieldSnglCategVocab;

            //OneMultipleTermsItemTermModel termsItemTermModelObj = new OneMultipleTermsItemTermModel();
            if(termsModelObj.items == null || termsModelObj.items.isEmpty()) {
                termsModelObj.items = new ArrayList<OneMultipleTermsItemModel>();
            }
            termsModelObj.items.add(termsItemModelObj);
        }
        retrofit2.Call<OneMultipleTermsModel> call = service.postMultipleTerms(termsModelObj);

        Log.d("URL Called", call.request().url() + "");
        call.enqueue(new retrofit2.Callback<OneMultipleTermsModel>() {
            @Override
            public void onResponse(retrofit2.Call<OneMultipleTermsModel> call, retrofit2.Response<OneMultipleTermsModel> response) {
                if(response.isSuccessful()) {
                    _progressDialogAsync.cancel();

                    Log.d(TAG, "onResponse: isSuccessful "+response.body().toString());
                    oneMultipleTermsItemsList = response.body().items;
                    makeBlogPost();

                }else {
                    _progressDialogAsync.cancel();
                    Log.d(TAG, "onResponse: " + response.toString());
                    Log.d(TAG, "onResponse: call body" + call.request().body().toString());
                    new AlertDialog.Builder(ActivityPost.this)
                            .setTitle(getString(R.string.dialog_api_failed_title))
                            .setMessage(getString(R.string.dialog_api_failed_message_prefix) + response.toString())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<OneMultipleTermsModel> call, Throwable t) {
                _progressDialogAsync.cancel();
                Log.d(TAG, "onFailure: "+t.getMessage());
                new AlertDialog.Builder(ActivityPost.this)
                        .setTitle(getString(R.string.dialog_api_failed_title))
                        .setMessage(getString(R.string.dialog_api_failed_message_prefix)+t.getMessage())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                //Toast.makeText(MyApplication.getAppContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
        _progressDialogAsync.cancel();
    }

    private void fetchTaxonomyTermNames() {
//Create handle for the getRetrofitSiteInstance interface
        GetSiteDataService service = RetrofitSiteInstance.getRetrofitSiteInstance(mAuthPreferences.getPrimarySiteProtocol(), mAuthPreferences.getPrimarySiteUrl(), null).create(GetSiteDataService.class);
        //Call the method with parameter in the interface to get the employee data
        retrofit2.Call<VocabTermsSimpleList> call = service.getTaxonomyVocabTitles();
        Log.d(TAG, "requestTaxonomyApiFilters "+call.request().url() + "");
        call.enqueue(new retrofit2.Callback<VocabTermsSimpleList>() {
            @Override
            public void onResponse(retrofit2.Call<VocabTermsSimpleList> call, retrofit2.Response<VocabTermsSimpleList> response) {
                if (response.isSuccessful()) {
                    ArrayList<VocabSimpleTerm> vocabTermsList = response.body().getTypesArrayList();
                    for (int j = 0; j < vocabTermsList.size(); j++) {
                        VocabSimpleTerm vTerm = vocabTermsList.get(j);
                    }
                } else {
                    Log.d(TAG, "fetchTaxonomyTermNames onResponse: fail "+response.errorBody().toString());
                }
            }
            @Override
            public void onFailure(retrofit2.Call<VocabTermsSimpleList> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
                //Toast.makeText(MyApplication.getAppContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTaxonomyFieldAutocomplete() {
        // http://nikhil.dubbaka.com/onedrupal/api/v1/vocabulary-titles/categories?_format=json
        //String[] androidVersionNames = {"Aestro", "Blender", "CupCake", "Donut", "Eclair", "Froyo", "Ginger bread", "Honey Comb", "IceCream Sandwich", "Jelli bean", "Kitkat", "Lollipop", "MarshMallow"};

// initiate a MultiAutoCompleteTextView
        et_taxonomy_category_auto = (MultiAutoCompleteTextView) findViewById(R.id.et_taxonomy_category_auto);
        et_taxonomy_category_auto_sngle_categ = (AutoCompleteTextView) findViewById(R.id.et_taxonomy_category_auto_sngle_categ);
        if(!taxonomyFieldSingleCategory.isEmpty()) {
            et_taxonomy_category_auto_sngle_categ.setAdapter(autoSuggestSnglAdapter);
            et_taxonomy_category_auto_sngle_categ.setThreshold(1);
            //et_taxonomy_category_auto_sngle_categ.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
            et_taxonomy_category_auto_sngle_categ.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int
                        count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    autoCompleteSngleHandler.removeMessages(TRIGGER_AUTO_COMPLETE);
                    autoCompleteSngleHandler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                            AUTO_COMPLETE_DELAY);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            autoCompleteSngleHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.what == TRIGGER_AUTO_COMPLETE) {
                        if (!TextUtils.isEmpty(et_taxonomy_category_auto_sngle_categ.getText())) {
                            String intermediate_text=et_taxonomy_category_auto_sngle_categ.getText().toString();
                            String final_string=intermediate_text.substring(intermediate_text.lastIndexOf(",")+1);
                            Log.d(TAG, "autoCompleteSngleHandler handleMessage: full text "+intermediate_text);
                            Log.d(TAG, "handleMessage: coma text "+final_string);
                            makeApiCall(taxonomyFieldSnglCategVocab, final_string);
                        }
                    }
                    return false;
                }
            });
        }
        if(!taxonomyField.isEmpty()) {
// set adapter to fill the data in suggestion list
            //ArrayAdapter<String> versionNames = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, androidVersionNames);
            //et_taxonomy_category_auto.setAdapter(versionNames);


            et_taxonomy_category_auto.setAdapter(autoSuggestAdapter);

// set threshold value 1 that help us to start the searching from first character
            et_taxonomy_category_auto.setThreshold(1);
// set tokenizer that distinguish the various substrings by comma
            et_taxonomy_category_auto.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

            et_taxonomy_category_auto.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int
                        count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    autoCompleteHandler.removeMessages(TRIGGER_AUTO_COMPLETE);
                    autoCompleteHandler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                            AUTO_COMPLETE_DELAY);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            autoCompleteHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.what == TRIGGER_AUTO_COMPLETE) {
                        if (!TextUtils.isEmpty(et_taxonomy_category_auto.getText())) {
                            String intermediate_text = et_taxonomy_category_auto.getText().toString();
                            String final_string = intermediate_text.substring(intermediate_text.lastIndexOf(",") + 1);
                            Log.d(TAG, "autoCompleteHandler handleMessage: full text " + intermediate_text);
                            Log.d(TAG, "handleMessage: coma text " + final_string);
                            makeApiCall(taxonomyFieldVocabulary, final_string);
                        }
                    }
                    return false;
                }
            });
        }


    }

    private void makeApiCall(String vid, String text) {
        text = text.trim();
        if(text.length() <= 2){
            return;
        }
        ApiCall.make(this, vid, text, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //parsing logic, please change it as per your requirement
                List<OneMultipleTermsItemTermModel> stringList = new ArrayList<>();
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray array = responseObject.getJSONArray("results");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        OneMultipleTermsItemTermModel oneMultipleTermsItemTerm = new OneMultipleTermsItemTermModel();
                        Log.d(TAG, "onResponse: row.getString(name) "+row.getString("name"));
                        oneMultipleTermsItemTerm.name = row.getString("name");
                        oneMultipleTermsItemTerm.parent = row.getString("parent");
                        stringList.add(oneMultipleTermsItemTerm);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(stringList.size() >= 1) {
                    if (vid.equals(taxonomyFieldVocabulary)) {
                        //IMPORTANT: set data here and notify
                        autoSuggestAdapter.setData(stringList);
                        autoSuggestAdapter.notifyDataSetChanged();
                    } else if (vid.equals(taxonomyFieldSnglCategVocab)) {
                        Log.d(TAG, "onResponse: autoSuggestSnglAdapter notifyDataSetChanged");
                        autoSuggestSnglAdapter.setData(stringList);
                        autoSuggestSnglAdapter.notifyDataSetChanged();
                    }
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+error.getMessage());
            }
        });
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
                editable.setSpan(new StyleSpan(Typeface.BOLD), 0, item.getFullname().length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return true;
            }

            public void onPopupVisibilityChanged(boolean shown) {}
        };

        userAutocomplete = Autocomplete.<User>on(et_taxonomy_category_auto)
                .with(elevation)
                .with(new CharPolicy(','))
                .with(backgroundDrawable)
                .with(presenter)
                .with(callback)
                .build();
        //userAutocomplete.showPopup(" ");
    }

    private void makeBlogPost() {
        _progressDialogAsync.show();
        Log.d("makeBlogPost", "in makeBlogPost");
       // Log.d("makeBlogPostAccessToken", sessionManager.getParticularField(SessionManager.ACCESS_TOKEN));
        Log.d("makeBlogPost CSRFToken", xCSRFToken);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/json");
       // client.addHeader("Google-Access-Token", sessionManager.getParticularField(SessionManager.ACCESS_TOKEN));
        client.addHeader("X-CSRF-Token", xCSRFToken);
        JSONObject jsonParams = new JSONObject();
        try {
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
            //title input
            JSONObject titleObject = new JSONObject();
            titleObject.put("value", et_f_post.getText());
            JSONArray titleArray = new JSONArray();
            titleArray.put(titleObject);
            jsonParams.put("title", titleArray);

            //image input
            if ((imageUploadResponse != null)&&(imageUploadResponse.getFid() != null)) {
                JSONObject imageObject = new JSONObject();
                imageObject.put("target_id", imageUploadResponse.getFid().get(0).getValue());
                imageObject.put("description", "The most fascinating image ever!");
                JSONArray imageArray = new JSONArray();
                imageArray.put(imageObject);
                jsonParams.put(imageField, imageArray);
            }

            //body input
            /*
            "field_custom_body": [
        {
            "value": "<b>Write blog details </b><b><u>herevvv</u></b>",
            "format": null,
            "processed": "<p>&lt;b&gt;Write blog details &lt;/b&gt;&lt;b&gt;&lt;u&gt;herevvv&lt;/u&gt;&lt;/b&gt;</p>\n",
            "summary": null
        }
    ],
             */
            if(bodyField != null && !bodyField.isEmpty()) {
                JSONObject bodyObject = new JSONObject();
                bodyObject.put("value", knife.toHtml());
                JSONArray bodyArray = new JSONArray();
                bodyArray.put(bodyObject);
                jsonParams.put(bodyField, bodyArray);
            }
            /*
            field_remote_image: [
{
uri: "http://example.com",
title: "",
options: [ ]
}
],
             */
            if(remotePageField != null && !remotePageField.isEmpty() && !et_remote_page_url.getText().toString().isEmpty()) {
                JSONObject remotePageObject = new JSONObject();
                remotePageObject.put("uri", et_remote_page_url.getText());
                JSONArray remotePageArray = new JSONArray();
                remotePageArray.put(remotePageObject);
                jsonParams.put(remotePageField, remotePageArray);
            }
            if(remoteImageField != null && !remoteImageField.isEmpty() && !et_remote_image_url.getText().toString().isEmpty()) {
                JSONObject remotePageObject = new JSONObject();
                remotePageObject.put("uri", et_remote_image_url.getText());
                JSONArray remotePageArray = new JSONArray();
                remotePageArray.put(remotePageObject);
                jsonParams.put(remoteImageField, remotePageArray);
            }
            if(remoteVideoField != null && !remoteVideoField.isEmpty() && !et_remote_video_url.getText().toString().isEmpty()) {
                JSONObject remotePageObject = new JSONObject();
                remotePageObject.put("value", et_remote_video_url.getText());
                JSONArray remotePageArray = new JSONArray();
                remotePageArray.put(remotePageObject);
                jsonParams.put(remoteVideoField, remotePageArray);
            }
/*
[
{
target_id: 10,
target_type: "taxonomy_term",
target_uuid: "7fc47cbf-ecef-4fca-b2b6-a749fa5840d2",
url: "/taxonomy/term/10"
},
 */
if(oneMultipleTermsItemsList != null) {
    for (int i = 0; i < oneMultipleTermsItemsList.size(); i++) {
        OneMultipleTermsItemModel oneMultipleTermsItem = oneMultipleTermsItemsList.get(i);
        JSONArray remotePageArray = new JSONArray();
        for (int j = 0; j < oneMultipleTermsItem.terms.size(); j++) {
            OneMultipleTermsItemTermModel oneMultipleTermsItemTerm = oneMultipleTermsItem.terms.get(j);
            JSONObject remotePageObject = new JSONObject();
            remotePageObject.put("target_id", oneMultipleTermsItemTerm.tid);
            remotePageObject.put("target_type", "taxonomy_term");
            remotePageArray.put(remotePageObject);
        }
        Log.d(TAG, "taxonomyField remotePageArray " + remotePageArray.toString(4));
        if(remotePageArray.length() > 0) {
            jsonParams.put(oneMultipleTermsItem.field_name, remotePageArray);
        }
        //break;
    }
}

            //Published input
            /*
            "status": [
        {
            "value": true
        }
    ]
             */
        /*    JSONObject publishObject = new JSONObject();
            publishObject.put("value", cb_published.isChecked());
            JSONArray publishArray = new JSONArray();
            publishArray.put(publishObject);
            jsonParams.put("status", publishArray);
*/

                    /*
                    "promote": [
        {
            "value": false
        }
    ],
                     */
       /*     JSONObject promoteObject = new JSONObject();
            promoteObject.put("value", cb_promoted.isChecked());
            JSONArray promoteArray = new JSONArray();
            promoteArray.put(promoteObject);
            jsonParams.put("promote", promoteArray);*/


            JSONObject typeObject = new JSONObject();
            typeObject.put("target_id", nodeType);
            JSONArray typeArray = new JSONArray();
            typeArray.put(typeObject);
            jsonParams.put("type", typeArray);
        }catch (JSONException e){
            e.printStackTrace();
        }

            Log.d("makeBlogPost", jsonParams.toString());
            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            RequestBody api_body = RequestBody.create(JSON, jsonParams.toString());
            //OkHttpClient client1 = new OkHttpClient();
            //client1.interceptors().add(new AddCookiesInterceptor());
            OkHttpClient client1 = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new AddCookiesInterceptor())
                //.addInterceptor(new ReceivedCookiesInterceptor())
                .build();
            String url;
            try {
                Request request;
                Log.d(TAG, "makeBlogPost: skiping xCSRFToken "+xCSRFToken);
                if(editMode){
                    url = mAuthPreferences.getPrimarySiteProtocol()+mAuthPreferences.getPrimarySiteUrl()+"/node/"+edit_nid+"?_format=json";
                    request = new Request.Builder()
                            .header("Content-Type", "application/json")
                           // .addHeader("X-CSRF-Token", xCSRFToken)
                            .url(url)
                            .patch(api_body)
                            .build();
                }else {
                    url = mAuthPreferences.getPrimarySiteProtocol()+mAuthPreferences.getPrimarySiteUrl()+"/node?_format=json";
                    request = new Request.Builder()
                            .header("Content-Type", "application/json")
                           // .addHeader("X-CSRF-Token", xCSRFToken)
                            .url(url)
                            .post(api_body)
                            .build();
                }
                Response response = client1.newCall(request).execute();
                if (response.isSuccessful()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mAuthPreferences.getPrimarySiteUrl());
                    mFirebaseAnalytics.logEvent("POST_NODE_SUCCESS", bundle);
                    JSONObject responseBodyObj = new JSONObject(response.body().string());
                    /*
                    "status": [
        {
            "value": false
        }
    ]
                     */
                    edit_nid = responseBodyObj.getJSONArray("nid").getJSONObject(0).getString("value");
                    boolean isPublished = responseBodyObj.getJSONArray("status").getJSONObject(0).getBoolean("value");
                    String publishActionButtonLabel = "Publish";
                    if(isPublished){
                        // Provide user, button to unpublish
                        publishActionButtonLabel = "Unpublish";
                    }

                    boolean isPromoted = responseBodyObj.getJSONArray("promote").getJSONObject(0).getBoolean("value");
                    String promoteActionButtonLabel = "Promote";
                    if(isPromoted){
                        // Provide user, button to unpublish
                        promoteActionButtonLabel = "Demote";
                    }
                    Log.d("123", "makeBlogPost: "+responseBodyObj.toString());
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Successfully Posted!")
                            .setItems(new CharSequence[]
                                    {publishActionButtonLabel, promoteActionButtonLabel, "Make another Post", "Go back to Home"},
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // The 'which' argument contains the index position
                                    // of the selected item
                                    switch (which) {
                                        case 0:
                                            //Toast.makeText(context, "Publish", Toast.LENGTH_SHORT).show();
                                            drupalNodeEditProperty(edit_nid, !isPublished, "status");
                                            break;
                                        case 1:
                                            //Toast.makeText(context, "Promote", Toast.LENGTH_SHORT).show();
                                            drupalNodeEditProperty(edit_nid, !isPromoted, "promote");
                                            break;
                                        case 2:
                                            et_f_post.setText("");
                                            knife.setText("");
                                            //iv_f_post_preview.refreshDrawableState();
                                            Glide.with(context)
                                                    .clear(iv_f_post_preview);
                                            break;
                                        case 3:
                                            //dialog.cancel();
                                            Intent intent = new Intent(context, SiteContentTabsActivity.class);
                                            startActivity(intent);
                                            break;
                                    }
                                }
                            });
                    builder.create().show();
                    /*builder.setMessage("Successfully Posted!")
                            .setCancelable(false)
                            .setPositiveButton("Make another Post", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    et_f_post.setText("");
                                    knife.setText("");
                                    iv_f_post_preview.refreshDrawableState();
                                }
                            })
                            .setNegativeButton("Go back to Home", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Intent intent = new Intent(context, ActivityDashboard.class);
                                    startActivity(intent);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();*/
                }else{
                    Log.d("123", "makeBlogPost: not successful"+response.toString()+response.body().string());
                    response.body().close();
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mAuthPreferences.getPrimarySiteUrl());
                    mFirebaseAnalytics.logEvent("POST_NODE_FAIL", bundle);
                    _progressDialogAsync.cancel();
                    String message_prefix = getString(R.string.dialog_api_failed_message_prefix);
                    if((response.code() == 404)||(response.code() == 406)){
                        message_prefix = "Make sure REST resource (/node/{node}: GET, PATCH /node: POST) is Enabled!";
                    }else if(response.code() == 403){
                        message_prefix = "Make sure user has access to node(Create or edit own node). Check your site permissions table for '<node_type>: Create new content, <node_type>: Edit own content'!";
                    }
                    new AlertDialog.Builder(ActivityPost.this)
                            .setTitle("Error")
                            .setMessage("API returned error: "+response.toString())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }catch (IOException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        _progressDialogAsync.cancel();
            //RadioGroup radioButtonGroup = (RadioGroup)findViewById(R.id.radioGroup);
            /*int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
            View radioButton = radioButtonGroup.findViewById(radioButtonID);
            int idx = radioButtonGroup.indexOfChild(radioButton);*/
            /*int checkedRadioButtonId = radioButtonGroup.getCheckedRadioButtonId();
            String postCategory = "redsox";
            if (checkedRadioButtonId == R.id.rb_f_post_eagles) {
                postCategory = "eagles";
            }else if (checkedRadioButtonId == R.id.rb_f_post_patriots) {
                postCategory = "patriots";
            }
            JSONObject categoryObject = new JSONObject();
            categoryObject.put("value", postCategory);
            JSONArray categoryArray = new JSONArray();
            categoryArray.put(categoryObject);
            jsonParams.put("field_text_category", categoryArray);*/
            /*
            StringEntity entity = new StringEntity(jsonParams.toString());
            Log.d("makeBlogPost", "makeBlogPost: "+url);
            client.post(context, url, entity,
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
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    private void drupalNodeEditProperty(String nid, boolean status, String field){
        _progressDialogAsync.show();
        Log.d(TAG, "drupalNodeEditProperty: "+nid);
        GetSiteDataService service = RetrofitSiteInstance.getRetrofitSiteInstance(mAuthPreferences.getPrimarySiteProtocol(), mAuthPreferences.getPrimarySiteUrl(), null).create(GetSiteDataService.class);
        //Call the method with parameter in the interface to get the employee data
        ModelNodeType nodeObj = new ModelNodeType();
        if(field.equals("status")) {
            nodeObj.setPublished(status);
        }else if(field.equals("promote")) {
            nodeObj.setPromoted(status);
        }
        nodeObj.setNodeType(nodeType);
        retrofit2.Call<ModelNodeType> call = service.postNodeEdit(nid, nodeObj);

        Log.d("URL Called", call.request().url() + "");
        call.enqueue(new retrofit2.Callback<ModelNodeType>() {
            @Override
            public void onResponse(retrofit2.Call<ModelNodeType> call, retrofit2.Response<ModelNodeType> response) {
                if(response.isSuccessful()) {
                    _progressDialogAsync.cancel();
                    String success_message ="";
                    if(field.equals("status")) {
                        success_message = "Published";
                        if(!status){
                            success_message = "Unpublished";
                        }
                    }else if(field.equals("promote")) {
                        success_message = "Promoted";
                        if(!status){
                            success_message = "Demoted";
                        }
                    }
                    Log.d(TAG, "onResponse: isSuccessful "+response.body().toString());
                    new AlertDialog.Builder(ActivityPost.this)
                            .setTitle(getString(R.string.dialog_api_success_title))
                            .setMessage(getString(R.string.dialog_api_success_message_prefix)+" "+success_message)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show();
                }else {
                    _progressDialogAsync.cancel();
                    Log.d(TAG, "onResponse: " + response.toString());
                    Log.d(TAG, "onResponse: call body" + call.request().body().toString());
                    new AlertDialog.Builder(ActivityPost.this)
                            .setTitle(getString(R.string.dialog_api_failed_title))
                            .setMessage(getString(R.string.dialog_api_failed_message_prefix) + response.toString())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelNodeType> call, Throwable t) {
                _progressDialogAsync.cancel();
                Log.d(TAG, "onFailure: "+t.getMessage());
                new AlertDialog.Builder(ActivityPost.this)
                        .setTitle(getString(R.string.dialog_api_failed_title))
                        .setMessage(getString(R.string.dialog_api_failed_message_prefix)+t.getMessage())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                //Toast.makeText(MyApplication.getAppContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
        _progressDialogAsync.cancel();
    }

    private void setupBold() {
        ImageButton bold = (ImageButton) findViewById(R.id.bold);

        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.bold(!knife.contains(KnifeText.FORMAT_BOLD));
            }
        });

        bold.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ActivityPost.this, R.string.toast_bold, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupItalic() {
        ImageButton italic = (ImageButton) findViewById(R.id.italic);

        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.italic(!knife.contains(KnifeText.FORMAT_ITALIC));
            }
        });

        italic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ActivityPost.this, R.string.toast_italic, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupUnderline() {
        ImageButton underline = (ImageButton) findViewById(R.id.underline);

        underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.underline(!knife.contains(KnifeText.FORMAT_UNDERLINED));
            }
        });

        underline.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ActivityPost.this, R.string.toast_underline, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupStrikethrough() {
        ImageButton strikethrough = (ImageButton) findViewById(R.id.strikethrough);

        strikethrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.strikethrough(!knife.contains(KnifeText.FORMAT_STRIKETHROUGH));
            }
        });

        strikethrough.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ActivityPost.this, R.string.toast_strikethrough, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupBullet() {
        ImageButton bullet = (ImageButton) findViewById(R.id.bullet);

        bullet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.bullet(!knife.contains(KnifeText.FORMAT_BULLET));
            }
        });


        bullet.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ActivityPost.this, R.string.toast_bullet, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupQuote() {
        ImageButton quote = (ImageButton) findViewById(R.id.quote);

        quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.quote(!knife.contains(KnifeText.FORMAT_QUOTE));
            }
        });

        quote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ActivityPost.this, R.string.toast_quote, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupLink() {
        ImageButton link = (ImageButton) findViewById(R.id.link);

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLinkDialog();
            }
        });

        link.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ActivityPost.this, R.string.toast_insert_link, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupClear() {
        ImageButton clear = (ImageButton) findViewById(R.id.clear);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.clearFormats();
            }
        });

        clear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ActivityPost.this, R.string.toast_format_clear, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void showLinkDialog() {
        final int start = knife.getSelectionStart();
        final int end = knife.getSelectionEnd();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dialog_link, null, false);
        final EditText editText = (EditText) view.findViewById(R.id.edit);
        builder.setView(view);
        builder.setTitle(R.string.dialog_title);

        builder.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String link = editText.getText().toString().trim();
                if (TextUtils.isEmpty(link)) {
                    return;
                }

                // When KnifeText lose focus, use this method
                knife.link(link, start, end);
            }
        });

        builder.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // DO NOTHING HERE
            }
        });

        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.undo:
                knife.undo();
                break;
            case R.id.redo:
                knife.redo();
                break;
            case R.id.github:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.app_repo)));
                startActivity(intent);
                break;
            default:
                break;
        }

        return true;
    }
}
