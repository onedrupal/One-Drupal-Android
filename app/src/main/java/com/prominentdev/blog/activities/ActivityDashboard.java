package com.prominentdev.blog.activities;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.RequestParams;
import com.prominentdev.blog.BuildConfig;
import com.prominentdev.blog.R;
import com.prominentdev.blog.fragments.FragmentADHome;
import com.prominentdev.blog.helpers.PDUtils;
import com.prominentdev.blog.models.ModelFanPosts;
import com.prominentdev.blog.models.TreeChild;
import com.prominentdev.blog.provider.PersistData;
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

import static com.prominentdev.blog.models.ConstantData.EMAIL;
import static com.prominentdev.blog.models.ConstantData.FULL_NAME;
import static com.prominentdev.blog.models.ConstantData.PROFILE_PICTURE;

/**
 * Created by Narender Kumar on 11/12/2018.
 * For Prominent, Faridabad (India)
 * narender.kumar.nishad@gmail.com
 */
public class ActivityDashboard extends ActivityBase {

    Toolbar toolbar;
    FloatingActionButton ad_fab;
    boolean doubleBackToExitPressedOnce = false;
    private DrawerLayout mDrawerLayout;

    private ImageView profilePicture;
    ArrayList<MyObject> categories = new ArrayList<>();
    private TextView fullName, email;
    GoogleSignInClient mGoogleSignInClient;
    private TreeNode root;
    LinearLayout llRightMenu;

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
                startActivity(new Intent(context, ActivityPost.class));
            }
        });
        mDrawerLayout = findViewById(R.id.drawer_layout);
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
                });
        if (findViewById(R.id.ad_fl_root) != null) {
            if (savedInstanceState != null) {
                return;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.ad_fl_root, FragmentADHome.newInstance(), FragmentADHome.class.getSimpleName()).commit();
        }
        googleSignInInit();
        requestNewsList();
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
        String newUrl = BuildConfig.API_ENPOINT + "jsonapi/taxonomy_term/category";
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
                        child.id = jsonObject.getString("id");
                        child.parentId = jsonObject.getJSONObject("relationships").getJSONObject("parent").getJSONArray("data").getJSONObject(0).getString("id");
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

    private void signOut() {
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
    private void googleSignInInit() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
//        if (account != null) {
//            updateUI(account, true);
//        }
    }
}