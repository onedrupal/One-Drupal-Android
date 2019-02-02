package com.prominentdev.blog.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.prominentdev.blog.BuildConfig;
import com.prominentdev.blog.R;
import com.prominentdev.blog.helpers.PDRestClient;
import com.prominentdev.blog.helpers.PDUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class ActivityAuthentication extends ActivityBase implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 1;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    private LinearLayout sign_in_success;
    private ImageView profile_image;
    private TextView profile_name, profile_email;
    Button sign_out;
    private String googleAccessToken = "", fullName = "", emailAddress = "";
    private static AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        googleSignInInit();
    }

    private void googleSignInInit() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null) {
            updateUI(account, true);
        }
    }

    private void init() {
        // Set the dimensions of the sign-in button.
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

        sign_in_success = findViewById(R.id.sign_in_success);
        sign_in_success.setVisibility(View.GONE);
        profile_image = findViewById(R.id.profile_image);
        profile_name = findViewById(R.id.profile_name);
        profile_email = findViewById(R.id.profile_email);
        sign_out = findViewById(R.id.sign_out);
        sign_out.setOnClickListener(this);
    }

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
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null, false);
            Toast.makeText(context, "Please try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(GoogleSignInAccount account, boolean isLogin) {
        if (isLogin) {
            googleAccessToken = account.getIdToken();
            fullName = account.getDisplayName();
            emailAddress = account.getEmail();
            Glide
                    .with(context)
                    .load(account.getPhotoUrl())
                    .into(profile_image);
            profile_name.setText(account.getDisplayName());
            profile_email.setText(account.getEmail());
            registerMe();
        } else {
            googleAccessToken = "";
            sign_in_success.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
        }
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
        StringRequest request = new StringRequest(Request.Method.POST, BuildConfig.API_ENPOINT + "google/tokensignin?_format=json", new Response.Listener<String>() {
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
                            signInButton.setVisibility(View.GONE);
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
    }
}