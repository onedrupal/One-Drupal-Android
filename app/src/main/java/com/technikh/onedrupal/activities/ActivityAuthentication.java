package com.technikh.onedrupal.activities;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
/*import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;*/
import com.google.firebase.analytics.FirebaseAnalytics;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.technikh.onedrupal.BuildConfig;
import com.technikh.onedrupal.R;
import com.technikh.onedrupal.app.MyApplication;
import com.technikh.onedrupal.authenticator.AuthPreferences;
import com.technikh.onedrupal.helpers.PDRestClient;
import com.technikh.onedrupal.helpers.PDUtils;
import com.technikh.onedrupal.models.ModelNodeType;
import com.technikh.onedrupal.models.SettingsType;
import com.technikh.onedrupal.models.SettingsTypeList;
import com.technikh.onedrupal.network.DrupalAPI;
import com.technikh.onedrupal.network.GetSiteDataService;
import com.technikh.onedrupal.network.OnSettingsApiGetTaskCompleted;
import com.technikh.onedrupal.network.RetrofitSiteInstance;
import com.technikh.onedrupal.provider.PersistData;
import com.technikh.onedrupal.util.AccountUtils;
import com.technikh.onedrupal.util.ApiUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.technikh.onedrupal.activities.SiteLoginActivity.PARAM_USER_SITE_PROTOCOL;
import static com.technikh.onedrupal.activities.SiteLoginActivity.PARAM_USER_SITE_URL;
import static com.technikh.onedrupal.models.ConstantData.EMAIL;
import static com.technikh.onedrupal.models.ConstantData.FULL_NAME;
import static com.technikh.onedrupal.models.ConstantData.PROFILE_PICTURE;

//public class ActivityAuthentication extends ActivityBase implements View.OnClickListener {
public class ActivityAuthentication extends ActivityBase {
    private static final String TAG = "ActivityAuthentication";
    private static final int RC_SIGN_IN = 1;
    /*SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;*/
    private LinearLayout sign_in_success;
    private ImageView profile_image;
    private TextView profile_name, profile_email;
    //Button sign_out;
    private String googleAccessToken = "", fullName = "", emailAddress = "";
    private static AsyncHttpClient client = new AsyncHttpClient();

    private static final int REQ_SIGNUP = 1;
    private Intent sharedIntent;
    private AccountManager mAccountManager;
    private AuthPreferences mAuthPreferences;
    private String authToken;
    private boolean receivingSharedDataMode = false;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            receivingSharedDataMode =  true;
            sharedIntent = intent;
        }
        //init();
        //googleSignInInit();

        authToken = null;
        mAuthPreferences = new AuthPreferences(this);
        if(mAuthPreferences.getPrimarySiteUrl() != null) {
            Bundle b = getIntent().getExtras();
            boolean skipTokenCheck = false;
            if(b != null)
                skipTokenCheck = b.getBoolean("skipTokenCheck");
            Log.d(TAG, "onCreate: "+skipTokenCheck);
            if(!skipTokenCheck) {
                mAccountManager = AccountManager.get(this);
                // Ask for an auth token
                Log.d(TAG, "onCreate: getAuthTokenByFeatures");
                mAccountManager.getAuthTokenByFeatures(AccountUtils.ACCOUNT_TYPE, AccountUtils.AUTH_TOKEN_TYPE, null, this, null, null, new ActivityAuthentication.GetAuthTokenCallback(), null);
            }
        }
    }



    private class GetAuthTokenCallback implements AccountManagerCallback<Bundle> {

        private void fetchSettingsAPI() {
            Log.d(TAG, "fetchSettingsAPI: before callDrupalUserUnAuthenticatedGetApiSync");
            //String responseStr = ApiUtils.callDrupalUserUnAuthenticatedGetApiSync("/onedrupal/api/v1/settings");
            //new DrupalAPI(new OnSettingsApiGetTaskCompleted()).execute("/onedrupal/api/v1/settings");
//Create handle for the getRetrofitSiteInstance interface
            GetSiteDataService service = RetrofitSiteInstance.getRetrofitSiteInstance(mAuthPreferences.getPrimarySiteProtocol(), mAuthPreferences.getPrimarySiteUrl(), null).create(GetSiteDataService.class);
            //Call the method with parameter in the interface to get the employee data
            retrofit2.Call<SettingsTypeList> call = service.getTypeData();
            //Log the URL called
            Log.d("URL Called", call.request().url() + "");

            ProgressDialog pd = new ProgressDialog(ActivityAuthentication.this);
            pd.setMessage(getString(R.string.dialog_api_progress));
            pd.show();
            call.enqueue(new retrofit2.Callback<SettingsTypeList>() {
                @Override
                public void onResponse(retrofit2.Call<SettingsTypeList> call, retrofit2.Response<SettingsTypeList> response) {
                    if(response.isSuccessful()) {
                        Log.d(TAG, "onResponse: " + response.toString());
                        Log.d(TAG, "onResponse: body " + response.body().toString());
                        ArrayList<SettingsType> nodeTypesList = response.body().getTypesArrayList();
                        MyApplication.gblNodeTypeSettings.clear();
                        for (int j = 0; j < nodeTypesList.size(); j++) {
                            MyApplication.gblNodeTypeSettings.add(nodeTypesList.get(j));
                        }

                        Bundle b = new Bundle();
                        b.putBoolean("publicViewMode", false);
                        b.putInt("nodeTypesSize", nodeTypesList.size());
                        Log.d(TAG, "onResponse: nodeTypesList.size() " + nodeTypesList.size());
                        b.putString("SiteProtocol", mAuthPreferences.getPrimarySiteProtocol());
                        b.putString("SiteDomain", mAuthPreferences.getPrimarySiteUrl());

                        Intent intent;
                        if(receivingSharedDataMode){
                            intent = new Intent(MyApplication.getAppContext(), SharedDataReceiverActivity.class);
                            Log.d(TAG, "onResponse: sharedIntent.getAction() "+sharedIntent.getAction());
                            intent.setAction(sharedIntent.getAction());
                            intent.setType(sharedIntent.getType());
                            intent.putExtras(sharedIntent.getExtras());
                            receivingSharedDataMode = false;
                        }else {
                            intent = new Intent(MyApplication.getAppContext(), SiteContentTabsActivity.class);
                            intent.putExtras(b);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MyApplication.getAppContext().startActivity(intent);
                    }else{
                        //Log.d(TAG, "onResponse: is not Successful");
                        new AlertDialog.Builder(ActivityAuthentication.this)
                                .setTitle(getString(R.string.dialog_api_failed_title))
                                .setMessage(getString(R.string.dialog_api_failed_message_prefix)+response.message()+"Code: "+response.code())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        // TODO: throw alert dialog as the user doesn't have access to settings API REST resource permission
                    }
                    pd.dismiss();
                }

                @Override
                public void onFailure(retrofit2.Call<SettingsTypeList> call, Throwable t) {
                    pd.dismiss();
                    new AlertDialog.Builder(getApplicationContext())
                            .setTitle(getString(R.string.dialog_api_failed_title))
                            .setMessage(getString(R.string.dialog_api_failed_message_prefix)+t.getMessage())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    //Toast.makeText(MyApplication.getAppContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            Bundle bundle;
            Log.d(TAG, "GetAuthTokenCallback run: ");
            try {
                bundle = result.getResult();

                final Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (null != intent) {
                    Log.d(TAG, "run: if"+AccountManager.KEY_INTENT);
                    startActivityForResult(intent, REQ_SIGNUP);
                } else {
                    Log.d(TAG, "run: else"+AccountManager.KEY_INTENT);
                    authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                    //refreshNavigationView();
                    final String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);

                    // Save session username & auth token
                    mAuthPreferences.setAuthToken(authToken);
                    mAuthPreferences.setUsername(accountName);

                    Log.d(TAG, "Retrieved auth token: authToken " + authToken);
                    Log.d(TAG, "Saved account name: " + accountName + mAuthPreferences.getAccountName());
                    Log.d(TAG, "Saved auth token: " + mAuthPreferences.getAuthToken());

                    // If the logged account didn't exist, we need to create it on the device
                    Account account = AccountUtils.getAccount(ActivityAuthentication.this, accountName);
                    if (null == account) {
                        Log.d(TAG, "run: account null");
                      /*  account = new Account(accountName, AccountUtils.ACCOUNT_TYPE);
                        mAccountManager.addAccountExplicitly(account, bundle.getString(SiteLoginActivity.PARAM_USER_PASSWORD), null);
                        mAccountManager.setAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, authToken);
*/
                        account = new Account(accountName, AccountUtils.ACCOUNT_TYPE);
                        Bundle extraData = new Bundle();
                        extraData.putString(PARAM_USER_SITE_URL, mAuthPreferences.getPrimarySiteUrl());
                        extraData.putString(PARAM_USER_SITE_PROTOCOL, mAuthPreferences.getPrimarySiteProtocol());
                        mAccountManager.addAccountExplicitly(account, bundle.getString(SiteLoginActivity.PARAM_USER_PASSWORD), extraData);
                        mAccountManager.setAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, authToken);

                        Bundle fbundle = new Bundle();
                        fbundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mAuthPreferences.getPrimarySiteUrl());
                        fbundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "addAccountAuth");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, fbundle);
                    }else{
                        AccountManager am = AccountManager.get(context);
                        String site_domain = am.getUserData(account, SiteLoginActivity.PARAM_USER_SITE_URL);
                        String site_protocol = am.getUserData(account, SiteLoginActivity.PARAM_USER_SITE_PROTOCOL);
                        Log.d(TAG, "AccountManagerFuture: site_domain"+site_domain);
                        mAuthPreferences.setPrimarySiteUrl(site_domain);
                        mAuthPreferences.setPrimarySiteProtocol(site_protocol);
                        Log.d(TAG, "run: setAuthToken "+authToken);
                        mAccountManager.setAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, authToken);
                    }
                    //finish();
                    fetchSettingsAPI();/*
                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try  {
                                //Your code goes here
                                fetchSettingsAPI();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
*/
                }
            } catch(OperationCanceledException e) {
                // If signup was cancelled, force activity termination
                finish();
            } catch(Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void onClickBtnViewFeaturedSite(View v)
    {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "FeaturedSite");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Intent intent = new Intent(context, FeaturedSitesActivity.class);
        startActivity(intent);
    }

    public void onClickBtnLoginDemo(View v)
    {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "LoginDemo");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Bundle b = new Bundle();
        b.putBoolean("demoMode", true);
        b.putBoolean("manualMode", true);
        Intent intent = new Intent(context, SiteLoginActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void onClickBtnLoginCustomSite(View v)
    {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "LoginCustom");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        //Toast.makeText(this, "Clicked on Button", Toast.LENGTH_LONG).show();
        Bundle b = new Bundle();
        b.putBoolean("demoMode", false);
        b.putBoolean("manualMode", true);
        Intent intent = new Intent(context, SiteLoginActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }
/*
    private void googleSignInInit() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.google_auth_client_id)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null) {
            updateUI(account, true);
        }
    }*/

    private void init() {
        // Set the dimensions of the sign-in button.
       /* signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);*/

        sign_in_success = findViewById(R.id.sign_in_success);
        sign_in_success.setVisibility(View.GONE);
        profile_image = findViewById(R.id.profile_image);
        profile_name = findViewById(R.id.profile_name);
        profile_email = findViewById(R.id.profile_email);
        //sign_out = findViewById(R.id.sign_out);
        //sign_out.setOnClickListener(this);
    }
/*
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                if (PDUtils.isNetworkConnected(context))
                    signIn();
                else
                    PDUtils.showToast(context, getString(R.string.check_internet_connection));
                break;
            case R.id.sign_out:
                if (PDUtils.isNetworkConnected(context))
                    signOut();
                else
                    PDUtils.showToast(context, getString(R.string.check_internet_connection));
                break;
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null, false);
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from context call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.

            if (account != null) {
                Log.w(TAG, "signInId" + account.getId());
                Log.w(TAG, "signInIdToken" + account.getIdToken());
                updateUI(account, true);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode()+e.getMessage());
            updateUI(null, false);
            Toast.makeText(context, "Please try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(GoogleSignInAccount account, boolean isLogin) {
        if (isLogin) {
            googleAccessToken = account.getIdToken();
            fullName = account.getDisplayName();
            emailAddress = account.getEmail();

            String url=account.getPhotoUrl().toString();
            Log.e(TAG, "updateUI: "+url );

            storeUserInformation(fullName,emailAddress,account.getPhotoUrl());

            Glide
                    .with(context)
                    .load(account.getPhotoUrl())
                    .into(profile_image);
            profile_name.setText(account.getDisplayName());
            profile_email.setText(account.getEmail());
            //registerMe();
        } else {
            googleAccessToken = "";
            sign_in_success.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
        }
    }
*/
    private void storeUserInformation(final String fullName,final String emailAddress,final Uri uri)
    {
        PersistData.setStringData(context,FULL_NAME,fullName);
        PersistData.setStringData(context,EMAIL,emailAddress);
        PersistData.setStringData(context,PROFILE_PICTURE,uri.toString());
    }


    private void registerUser() {
        JSONObject object, nameObj, emailObj;
        JSONArray nameArr, emailArr;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        /*
         * Send user details to server in json format
         */
        _progressDialogAsync.show();
/*        object = new JSONObject();
        nameObj = new JSONObject();
        emailObj = new JSONObject();
        nameArr = new JSONArray();
        emailArr = new JSONArray();
        try {
            nameObj.put("value", fullName);
            emailObj.put("value", emailAddress);

            nameArr.put(nameObj);
            emailArr.put(emailArr);
            object.put("name", nameArr);
            object.put("mail", emailArr);
            Log.e("response", object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        StringRequest request = new StringRequest(Request.Method.POST, "BuildConfig.API_ENPOINT" + "google/tokensignin?_format=json", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                _progressDialogAsync.show();
                Log.d(TAG, "Login Response" + response);
                JSONObject obj = null;
                if (response != null) {
                    try {
                        obj = new JSONObject(response);
                        if (obj.has("data")) {
                            sign_in_success.setVisibility(View.VISIBLE);
                            //signInButton.setVisibility(View.GONE);
                            /*JSONObject userIdObject = obj.getJSONArray("uid").getJSONObject(0);
                            JSONObject clientIdToken = obj.getJSONArray("uuid").getJSONObject(0);
                            JSONObject userNameObject = obj.getJSONArray("name").getJSONObject(0);
                            sessionManager.createLoginSession(emailAddress,
                                    userIdObject.getString("value"),
                                    userNameObject.getString("value"),
                                    clientIdToken.getString("value"),
                                    googleAccessToken, "");*/
                            PDUtils.showToast(context, "Login");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    PDUtils.showToast(context, "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errortype = null;
                if ((error instanceof TimeoutError) || (error instanceof NoConnectionError)) {
                    errortype = "No Internet Access, check your internet connection.";
                } else if (error instanceof AuthFailureError) {
                    errortype = "Authentication error, please try again.";
                } else if (error instanceof ServerError) {
                    errortype = "Server error, please try again.";
                } else if (error instanceof NetworkError) {
                    errortype = "Network error, please try again.";
                } else if (error instanceof ParseError) {
                    errortype = "Internal error, please try again.";
                }
                Toast.makeText(context, errortype, Toast.LENGTH_SHORT).show();
                _progressDialogAsync.dismiss();
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                String str = "{\"access_token\":\"" + googleAccessToken + "\"}";
                return str.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        requestQueue.add(request);
    }
/*
    private void registerMe() {
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        try {
            jsonParams.put("access_token", googleAccessToken);
            entity = new StringEntity(jsonParams.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(context, BuildConfig.API_ENPOINT + "google/tokensignin?_format=json", entity,
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
                        try {
                            if (response.has("data")) {
                                JSONObject accessToken = response.getJSONObject("data");
                                JSONObject accountDetails = response.getJSONObject("account");
                                JSONObject userIdObject = accountDetails.getJSONArray("uid").getJSONObject(0);
                                JSONObject clientIdToken = accountDetails.getJSONArray("uuid").getJSONObject(0);
                                sign_in_success.setVisibility(View.VISIBLE);
                                signInButton.setVisibility(View.GONE);
                                sessionManager.createLoginSession(emailAddress,
                                        userIdObject.getString("value"),
                                        fullName,
                                        clientIdToken.getString("value"),
                                        accessToken.getString("access_token"),
                                        "");
                                startActivity(new Intent(context, ActivityDashboard.class));
                                finish();
                            } else {
                                PDUtils.showToast(context, "No data");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
    }*/
}