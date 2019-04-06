package com.technikh.onedrupal.activities;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
//import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.technikh.onedrupal.BuildConfig;
import com.technikh.onedrupal.R;
import com.technikh.onedrupal.app.MyApplication;
import com.technikh.onedrupal.authenticator.AuthPreferences;
import com.technikh.onedrupal.models.SettingsType;
import com.technikh.onedrupal.models.SettingsTypeList;
import com.technikh.onedrupal.network.DrupalAPI;
import com.technikh.onedrupal.network.GetSiteDataService;
import com.technikh.onedrupal.network.OnSettingsApiGetTaskCompleted;
import com.technikh.onedrupal.network.RetrofitSiteInstance;
import com.technikh.onedrupal.util.AccountUtils;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class SiteLoginActivity extends AccountAuthenticatorActivity {
    public static final String ARG_ACCOUNT_TYPE = "accountType";
    public static final String ARG_AUTH_TOKEN_TYPE = "authTokenType";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "isAddingNewAccount";
    public static final String PARAM_USER_PASSWORD = "password";
    public static final String PARAM_USER_SITE_URL = "siteURL";
    public static final String PARAM_USER_SITE_PROTOCOL = "siteProtocol";

    private String TAG = "SiteLoginActivity";

    private AccountManager mAccountManager;
    private FirebaseAnalytics mFirebaseAnalytics;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private AuthPreferences mAuthPreferences;

    // Values for email and password at the time of the login attempt.
    //private String mUsername;
    //private String mPassword;

    // UI references.
    //private AutoCompleteTextView mEmailView;
    private EditText mPasswordView, mUsernameView, mSiteUrl;
    private Spinner mSiteProtocolSpinner;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_login);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle b = getIntent().getExtras();
        boolean demoMode = false;
        boolean manualMode = false;
        if(b != null) {
            demoMode = b.getBoolean("demoMode");
            manualMode = b.getBoolean("manualMode");
        }
        if(!manualMode){
            // Show trouble shooting & navigation buttons if automatically redirected to login form
            Button btn_view_featured_site = (Button) findViewById(R.id.btn_view_featured_site);
            btn_view_featured_site.setVisibility(View.VISIBLE);
            Button btn_login_demo = (Button) findViewById(R.id.btn_login_demo);
            btn_login_demo.setVisibility(View.VISIBLE);
        }

        mAuthPreferences = new AuthPreferences(this);

        mAccountManager = AccountManager.get(this);

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.email);
        mSiteProtocolSpinner = (Spinner)findViewById(R.id.spinner_site_protocol);

        mSiteUrl = (EditText) findViewById(R.id.siteURL);
        mSiteUrl.setText(mAuthPreferences.getPrimarySiteUrl());

        mPasswordView = (EditText) findViewById(R.id.password);
        if(demoMode) {
            mSiteUrl.setText(BuildConfig.DEMO_SITE_URL);
            List<String> protocolsArray = Arrays.asList(getResources().getStringArray(R.array.array_http_protocols));
            mSiteProtocolSpinner.setSelection(protocolsArray.indexOf("https://"));
            mUsernameView.setText(BuildConfig.DEMO_SITE_USERNAME);
            mPasswordView.setText(BuildConfig.DEMO_SITE_PASSWORD);
        }
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    public void onClickBtnViewFeaturedSite(View v)
    {
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("CLICK_BTN_FEATURED_SITE2", bundle);

        Intent intent = new Intent(this, FeaturedSitesActivity.class);
        startActivity(intent);
    }

    public void onClickBtnLoginDemo(View v)
    {
        mSiteUrl.setText(BuildConfig.DEMO_SITE_URL);
        List<String> protocolsArray = Arrays.asList(getResources().getStringArray(R.array.array_http_protocols));
        mSiteProtocolSpinner.setSelection(protocolsArray.indexOf("https://"));
        mUsernameView.setText(BuildConfig.DEMO_SITE_USERNAME);
        mPasswordView.setText(BuildConfig.DEMO_SITE_PASSWORD);
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent("CLICK_BTN_DEMO_LOGIN2", bundle);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String primarySiteURL = mSiteUrl.getText().toString();
        String primarySiteProtocol = mSiteProtocolSpinner.getSelectedItem().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }
        // site textbox no slash / atleast one period
        if (TextUtils.isEmpty(primarySiteURL) || primarySiteURL.contains("/") || primarySiteURL.contains("@") || !primarySiteURL.contains(".")) {
            mSiteUrl.setError(getString(R.string.error_invalid_site_domain));
            focusView = mSiteUrl;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            mAuthPreferences.setPrimarySiteUrl(primarySiteURL);
            Log.d(TAG, "attemptLogin: setPrimarySiteUrl"+primarySiteURL);
            mAuthPreferences.setPrimarySiteProtocol(primarySiteProtocol);
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            Log.d(TAG, "attemptLogin: primarySiteProtocol"+primarySiteProtocol);
            mAuthTask = new UserLoginTask(primarySiteProtocol+primarySiteURL, username, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 1;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Intent> {

        private final String mUsername;
        private final String mPassword;
        private final String mSitedomain;

        UserLoginTask(String site_domain, String username, String password) {
            mUsername = username;
            mPassword = password;
            mSitedomain = site_domain;
        }

        @Override
        protected Intent doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String authToken = AccountUtils.mServerAuthenticator.signIn(mSitedomain, mUsername, mPassword);

            final Intent res = new Intent();

            res.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername);
            res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AccountUtils.ACCOUNT_TYPE);
            res.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
            res.putExtra(PARAM_USER_PASSWORD, mPassword);

            return res;
        }

        @Override
        protected void onPostExecute(final Intent intent) {
            mAuthTask = null;
            showProgress(false);

            if (null == intent.getStringExtra(AccountManager.KEY_AUTHTOKEN)) {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            } else {
                finishLogin(intent);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        private void finishLogin(Intent intent) {
            final String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            final String accountPassword = intent.getStringExtra(PARAM_USER_PASSWORD);
            Log.d(TAG, "finishLogin: accountName"+accountName);
            String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

            Account account = AccountUtils.getAccount(SiteLoginActivity.this, accountName);
            if(account == null) {
                account = new Account(accountName, AccountUtils.ACCOUNT_TYPE);
                Bundle extraData = new Bundle();
                extraData.putString(PARAM_USER_SITE_URL, mSiteUrl.getText().toString());
                String primarySiteProtocol = mSiteProtocolSpinner.getSelectedItem().toString();
                Log.d(TAG, "finishLogin: primarySiteProtocol"+primarySiteProtocol);
                extraData.putString(PARAM_USER_SITE_PROTOCOL, primarySiteProtocol);
                mAccountManager.addAccountExplicitly(account, accountPassword, extraData);
                mAccountManager.setAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, authToken);

                Bundle fbundle = new Bundle();
                fbundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mSiteUrl.getText().toString());
                mFirebaseAnalytics.logEvent("ACCOUNT_ADD_LOGIN", fbundle);
            }else{
                mAccountManager.setPassword(account, accountPassword);
            }
            mAuthPreferences.setAuthToken(authToken);
            mAuthPreferences.setUsername(accountName);
            Log.d(TAG, "finishLogin: account.Name"+account.name);
            Log.d(TAG, "finishLogin authToken: "+accountName+accountPassword+authToken);

            setAccountAuthenticatorResult(intent.getExtras());
            setResult(AccountAuthenticatorActivity.RESULT_OK, intent);

            Log.d(TAG, "finishLogin: getPrimarySiteUrl "+mAuthPreferences.getPrimarySiteUrl());
            //Create handle for the getRetrofitSiteInstance interface
            GetSiteDataService service = RetrofitSiteInstance.getRetrofitSiteInstance(mAuthPreferences.getPrimarySiteProtocol(), mAuthPreferences.getPrimarySiteUrl(), null).create(GetSiteDataService.class);
            //Call the method with parameter in the interface to get the employee data
            retrofit2.Call<SettingsTypeList> call = service.getTypeData();
            //Log the URL called
            Log.d("URL Called", call.request().url() + "");

            ProgressDialog pd = new ProgressDialog(SiteLoginActivity.this);
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
                        if(nodeTypesList.size() == 0){
                            new AlertDialog.Builder(SiteLoginActivity.this)
                                    .setTitle(getString(R.string.dialog_settings_api_failed_title))
                                    .setMessage("Make sure the 'one' drupal module in the site is configured correctly, with atleast one content type enabled in settings page at /one/settings")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }else {
                            Bundle b = new Bundle();
                            b.putBoolean("publicViewMode", false);
                            b.putInt("nodeTypesSize", nodeTypesList.size());
                            Log.d(TAG, "onResponse: nodeTypesList.size() " + nodeTypesList.size());
                            b.putString("SiteProtocol", mAuthPreferences.getPrimarySiteProtocol());
                            Log.d(TAG, "onResponse: getPrimarySiteUrl " + mAuthPreferences.getPrimarySiteUrl());
                            b.putString("SiteDomain", mAuthPreferences.getPrimarySiteUrl());

                            Intent intent = new Intent(MyApplication.getAppContext(), SiteContentTabsActivity.class);
                            intent.putExtras(b);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MyApplication.getAppContext().startActivity(intent);
                        }
                    }else{
                        String message_prefix = getString(R.string.dialog_api_failed_message_prefix);
                        if(response.code() == 404){
                            message_prefix = "Make sure one_api module is installed and REST resource (/onedrupal/api/v1/settings: GET) is Enabled!";
                        }else if(response.code() == 403){
                            message_prefix = "Make sure user has access to API /onedrupal/api/v1/settings. Check your site permissions table for 'Access GET on One Settings resource'!";
                        }
                        new AlertDialog.Builder(SiteLoginActivity.this)
                                .setTitle(getString(R.string.dialog_settings_api_failed_title))
                                .setMessage(message_prefix+" -> "+response.toString())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        Log.d(TAG, "onResponse: is not Successful");

                        Bundle fbundle = new Bundle();
                        fbundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mAuthPreferences.getPrimarySiteUrl());
                        mFirebaseAnalytics.logEvent("SETTINGS_API_FAIL", fbundle);
                        //Toast.makeText(MyApplication.getAppContext(), "onResponse: is not Successful", Toast.LENGTH_SHORT).show();
                    }
                    pd.dismiss();
                }

                @Override
                public void onFailure(retrofit2.Call<SettingsTypeList> call, Throwable t) {
                    pd.dismiss();
                    new AlertDialog.Builder(SiteLoginActivity.this)
                            .setTitle(getString(R.string.dialog_settings_api_failed_title))
                            .setMessage(getString(R.string.dialog_settings_api_failed_message_prefix)+t.getMessage())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    Log.d(TAG, "onFailure: "+t.toString());
                    Bundle fbundle = new Bundle();
                    fbundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mAuthPreferences.getPrimarySiteUrl());
                    mFirebaseAnalytics.logEvent("SETTINGS_API_THROW", fbundle);
                    //Toast.makeText(MyApplication.getAppContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            });
            //Log.d(TAG, "finishLogin: "+intent.getType() + intent.getScheme());
            //new DrupalAPI(new OnSettingsApiGetTaskCompleted()).execute("/onedrupal/api/v1/settings");
            //Intent intent1 = new Intent(SiteLoginActivity.this, ActivityDashboard.class);
            //startActivity(intent1);
            //finish();
        }
    }
}

