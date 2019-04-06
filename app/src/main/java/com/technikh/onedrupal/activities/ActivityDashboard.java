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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
/*import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;*/
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.RequestParams;
import com.technikh.onedrupal.BuildConfig;
import com.technikh.onedrupal.R;
import com.technikh.onedrupal.app.MyApplication;
import com.technikh.onedrupal.authenticator.AuthPreferences;
import com.technikh.onedrupal.fragments.FragmentADHome;
import com.technikh.onedrupal.helpers.PDUtils;
import com.technikh.onedrupal.models.ModelFanPosts;
import com.technikh.onedrupal.models.ModelNodeType;
import com.technikh.onedrupal.models.SettingsType;
import com.technikh.onedrupal.models.TreeChild;
import com.technikh.onedrupal.provider.PersistData;
import com.technikh.onedrupal.util.AccountUtils;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import treeutil.MyObject;
import treeutil.Node;
import treeutil.Tree;
import treeutil.TreeHolder;

import static com.technikh.onedrupal.models.ConstantData.EMAIL;
import static com.technikh.onedrupal.models.ConstantData.FULL_NAME;
import static com.technikh.onedrupal.models.ConstantData.PROFILE_PICTURE;

public class ActivityDashboard extends ActivityBase {

    Toolbar toolbar;
    FloatingActionButton ad_fab;
    boolean doubleBackToExitPressedOnce = false;
    private DrawerLayout mDrawerLayout;

    private ImageView profilePicture;
    ArrayList<MyObject> categories = new ArrayList<>();
    private TextView fullName, email;
    //GoogleSignInClient mGoogleSignInClient;
    private TreeNode root;
    LinearLayout llRightMenu;
    private String TAG = "ActivityDashboard";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        llRightMenu = findViewById(R.id.llRightMenu);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.dashboard);
        ad_fab = findViewById(R.id.ad_fab);
        ad_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> postTypeList = new ArrayList<String>();
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ActivityDashboard.this, android.R.layout.select_dialog_singlechoice);
                for (int j = 0; j < MyApplication.gblNodeTypeSettings.size(); j++) {
                    SettingsType modelNodeType = MyApplication.gblNodeTypeSettings.get(j);
                    Log.d(TAG, "onClick: "+modelNodeType.getNodeType());
                    if(modelNodeType.userHasAccesstoCreate()){
                        postTypeList.add(modelNodeType.getNodeType());
                        arrayAdapter.add(modelNodeType.getNodeType());
                    }
                }
                Log.d(TAG, "onClick: size "+postTypeList.size());
                if(postTypeList.size() == 1) {
                    Bundle b = new Bundle();
                    b.putString("nodeType", postTypeList.get(0));
                    Intent intent = new Intent(context, ActivityPost.class);
                    intent.putExtras(b);
                    startActivity(intent);
                }else if(postTypeList.size() > 1) {
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(ActivityDashboard.this);
                    builderSingle.setIcon(R.mipmap.ic_launcher);
                    builderSingle.setTitle("Select A content type to POST:-");

                    builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String nodeType = arrayAdapter.getItem(which);
                            Bundle b = new Bundle();
                            b.putString("nodeType", nodeType);
                            Intent intent = new Intent(context, ActivityPost.class);
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    });
                    builderSingle.show();
                }
            }
        });
        mDrawerLayout = findViewById(R.id.drawer_layout);




        /*mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        profilePicture = (ImageView) headerView.findViewById(R.id.profile_header);
        fullName = (TextView) headerView.findViewById(R.id.full_name);
        email = (TextView) headerView.findViewById(R.id.email_address);
        if (!TextUtils.isEmpty(PersistData.getStringData(context, FULL_NAME))) {
            fullName.setText(PersistData.getStringData(context, FULL_NAME));
        }
        if (!TextUtils.isEmpty(PersistData.getStringData(context, EMAIL))) {
            email.setText(PersistData.getStringData(context, EMAIL));
        }
        if (!TextUtils.isEmpty(PersistData.getStringData(context, PROFILE_PICTURE))) {
            Glide
                    .with(context)
                    .load(PersistData.getStringData(context, PROFILE_PICTURE))
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePicture);
        }
        Menu nav_Menu = navigationView.getMenu();
        if (sessionManager.isLoggedIn()) {
            nav_Menu.findItem(R.id.loginButton).setVisible(false);
            nav_Menu.findItem(R.id.logoutButton).setVisible(true);
        } else {
            nav_Menu.findItem(R.id.loginButton).setVisible(true);
            nav_Menu.findItem(R.id.logoutButton).setVisible(false);
        }
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            case R.id.loginButton:
                                startActivity(new Intent(context, ActivityAuthentication.class));
                                finish();
                                break;
                            case R.id.logoutButton:
                                if (mGoogleSignInClient != null) {
                                    signOut();
                                }
                                break;
                        }
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        return true;
                    }
                });*/
        if (findViewById(R.id.ad_fl_root) != null) {
            if (savedInstanceState != null) {
                return;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.ad_fl_root, FragmentADHome.newInstance(), FragmentADHome.class.getSimpleName()).commit();
        }
        //googleSignInInit();
        //requestNewsList();
        refreshNavigationView();
    }

    private void refreshNavigationView(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        profilePicture = (ImageView) headerView.findViewById(R.id.profile_header);
        fullName = (TextView) headerView.findViewById(R.id.full_name);
        email = (TextView) headerView.findViewById(R.id.email_address);
        AuthPreferences mAuthPreferences = new AuthPreferences(this);
        fullName.setText(mAuthPreferences.getAccountName()+mAuthPreferences.getAuthToken());
        Account account = AccountUtils.getAccount(getApplicationContext(), mAuthPreferences.getAccountName());
        if(account != null) {
            AccountManager am = AccountManager.get(context);
            String site_domain = am.getUserData(account, SiteLoginActivity.PARAM_USER_SITE_URL);
            String site_protocol = am.getUserData(account, SiteLoginActivity.PARAM_USER_SITE_PROTOCOL);
            Log.d(TAG, "refreshNavigationView: site_domain"+site_domain);
            //email.setText(site_domain);
            mAuthPreferences.setPrimarySiteUrl(site_domain);
            mAuthPreferences.setPrimarySiteProtocol(site_protocol);
        }else {
            //email.setText(mAuthPreferences.getPrimarySiteUrl());
        }
        email.setText(mAuthPreferences.getPrimarySiteProtocol()+mAuthPreferences.getPrimarySiteUrl());

        /*if (!TextUtils.isEmpty(PersistData.getStringData(context, EMAIL))) {
            email.setText(PersistData.getStringData(context, EMAIL));
        }*
        if (!TextUtils.isEmpty(PersistData.getStringData(context, PROFILE_PICTURE))) {
            Glide
                    .with(context)
                    .load(PersistData.getStringData(context, PROFILE_PICTURE))
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePicture);
        }

        Menu nav_Menu = navigationView.getMenu();
        //Log.d(TAG, "onCreate: authToken "+authToken);
       /* if (authToken != null) {
            nav_Menu.findItem(R.id.loginButton).setVisible(false);
            nav_Menu.findItem(R.id.logoutButton).setVisible(true);
        } else {
            nav_Menu.findItem(R.id.loginButton).setVisible(true);
            nav_Menu.findItem(R.id.logoutButton).setVisible(false);
        }*/

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            /*case R.id.loginButton:
                                startActivity(new Intent(context, ActivityAuthentication.class));
                                finish();
                                break;
                            case R.id.logoutButton:
                                if (mGoogleSignInClient != null) {
                                    signOut();
                                }
                                break;*/
                            case R.id.connectDrupal:
                                Log.d(TAG, "onNavigationItemSelected: ");
                                Bundle b = new Bundle();
                                b.putBoolean("skipTokenCheck", true);
                                Intent intent = new Intent(context, ActivityAuthentication.class);
                                intent.putExtras(b);
                                startActivity(intent);
                                Log.d(TAG, "onNavigationItemSelected: finish");
                                finish();
                                break;
                            case R.id.featuredSites:
                                Intent intent1 = new Intent(context, FeaturedSitesActivity.class);
                                startActivity(intent1);
                                break;
                        }
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        return true;
                    }
                });
    }



    private void constructTree() {
        root = TreeNode.root();
        Tree tree = new Tree();

        Iterator<Node> iterable = tree.buildTreeAndGetRoots(categories);
        for (Iterator<Node> nodeIterator = iterable; nodeIterator.hasNext(); ) {
            Node node = nodeIterator.next();
            Log.e("Node", node.toString());
            if (node.children.size() == 0) {
                TreeNode treeNode = new TreeNode(node.associatedObject.name).setViewHolder(new TreeHolder(this, 0));
                root = root.addChild(treeNode);
            } else {
                TreeNode parentNode = new TreeNode(node.associatedObject.name).setViewHolder(new TreeHolder(this, 0));
                root = root.addChild(parentNode);

                addSubCHildren(parentNode, node.children, 0);
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AndroidTreeView tView = new AndroidTreeView(ActivityDashboard.this, root);
//                 tView.expandAll();
                llRightMenu.addView(tView.getView());
                tView.expandAll();
                tView.setSelectionModeEnabled(true);
                tView.setDefaultNodeClickListener(new TreeNode.TreeNodeClickListener() {
                    @Override
                    public void onClick(TreeNode node, Object value) {
                        Toast.makeText(context, "Node clicked", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void addSubCHildren(TreeNode parentNode, List<Node> parent, int level) {
        level = level + 1;
        for (Node subparent : parent) {
            if (subparent.children.size() == 0) {
                TreeNode treeNode = new TreeNode(subparent.associatedObject.name).setViewHolder(new TreeHolder(this, level));
                parentNode.addChild(treeNode);
            } else {
                TreeNode parentsubNode = new TreeNode(subparent.associatedObject.name).setViewHolder(new TreeHolder(this, level));
                addSubCHildren(parentsubNode, subparent.children, level);
                parentNode.addChild(parentsubNode);
            }
        }
    }

    private void requestNewsList() {
        String newUrl = "BuildConfig.API_ENPOINT" + "jsonapi/taxonomy_term/category";
        Log.e("Url", newUrl);
        long cacheSize = (5 * 1024 * 1024);
        File httpCacheDirectory = new File(this.getCacheDir(), "http-cache");
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
                .cache(cache)
                .addInterceptor(new HttpLoggingInterceptor())
                .build();
        Request request = new Request.Builder().url(newUrl).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String respoStr = response.body().string();
                try {
                    JSONObject object = new JSONObject(respoStr);
                    JSONArray dataArray = object.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject jsonObject = dataArray.getJSONObject(i);
                        MyObject child = new MyObject();
                        child.name = jsonObject.getJSONObject("attributes").getString("name");
                        //child.id = jsonObject.getString("id");
                        //child.parentId = jsonObject.getJSONObject("relationships").getJSONObject("parent").getJSONArray("data").getJSONObject(0).getString("id");
                        categories.add(child);
                    }
                    constructTree();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //Checking for fragment count on backstack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            doubleBackToExitPressedOnce = true;
            PDUtils.showToast(context, getString(R.string.back_pressed));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 1000);
        } else {
            super.onBackPressed();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            ad_fab.hide();
        } else {
            ad_fab.show();
        }
    }
/*
    private void googleSignOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        sessionManager.logoutUser();
                        startActivity(new Intent(context, ActivityAuthentication.class));
                        finish();
                    }
                });
    }
    private void signOut() {
        // Clear session and ask for new auth token
        mAccountManager.invalidateAuthToken(AccountUtils.ACCOUNT_TYPE, authToken);
        mAuthPreferences.setAuthToken(null);
        mAuthPreferences.setUsername(null);
    }
    private void googleSignInInit() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.google_auth_client_id)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
//        if (account != null) {
//            updateUI(account, true);
//        }
    }*/
}