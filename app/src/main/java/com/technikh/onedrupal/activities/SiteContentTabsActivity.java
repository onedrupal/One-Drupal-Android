package com.technikh.onedrupal.activities;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.technikh.onedrupal.R;
import com.technikh.onedrupal.app.MyApplication;
import com.technikh.onedrupal.authenticator.AuthPreferences;
import com.technikh.onedrupal.fragments.FragmentADRedsox;
import com.technikh.onedrupal.models.ModelNodeType;
import com.technikh.onedrupal.models.SettingsType;
import com.technikh.onedrupal.models.SettingsTypeList;
import com.technikh.onedrupal.models.Site;
import com.technikh.onedrupal.network.GetSiteDataService;
import com.technikh.onedrupal.network.RetrofitSiteInstance;
import com.technikh.onedrupal.util.AccountUtils;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import treeutil.MyObject;
import treeutil.Node;
import treeutil.Tree;
import treeutil.TreeHolder;

import static com.technikh.onedrupal.network.AddCookiesInterceptor.APP_PREFERENCES;

public class SiteContentTabsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private String mSiteDomain, mSiteProtocol;
    private boolean mPublicViewMode = false;
    private String TAG = "SiteContentTabsActivity";
    private int mNodeTypesSize;
    private ActionBarDrawerToggle mDrawerToggle;

    private AccountManager mAccountManager;
    private AuthPreferences mAuthPreferences;
    private String authToken;


    private NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener =  new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            // set item as selected to persist highlight
            menuItem.setChecked(true);
            // close drawer when item is tapped
            //mDrawerLayout.closeDrawers();
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
                    Intent intent = new Intent(SiteContentTabsActivity.this, ActivityAuthentication.class);
                    intent.putExtras(b);
                    startActivity(intent);
                    Log.d(TAG, "onNavigationItemSelected: finish");
                    finish();
                    break;
                case R.id.featuredSites:
                    Intent intent1 = new Intent(SiteContentTabsActivity.this, FeaturedSitesActivity.class);
                    startActivity(intent1);
                    break;
               /* case R.id.closeSession:
                    // Clear session and ask for new auth token
                    if(authToken != null) {
                        mAccountManager.invalidateAuthToken(AccountUtils.ACCOUNT_TYPE, authToken);
                        mAuthPreferences.setAuthToken(null);
                        mAuthPreferences.setUsername(null);
                        Intent intent2 = new Intent(SiteContentTabsActivity.this, ActivityAuthentication.class);
                        startActivity(intent2);
                        finish();
                        //mAccountManager.getAuthTokenByFeatures(AccountUtils.ACCOUNT_TYPE, AccountUtils.AUTH_TOKEN_TYPE, null, this, null, null, new GetAuthTokenCallback(), null);
                    }
                    break;*/
                case R.id.removeAccount:
                    if(mAuthPreferences.getAccountName() != null) {
                        Account account = AccountUtils.getAccount(SiteContentTabsActivity.this, mAuthPreferences.getAccountName());
                        if(account != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                                mAccountManager.removeAccountExplicitly(account);
                            }
                        }
                    }

                    // Clear cookies
                    SharedPreferences mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                    HashSet<String> cookies = (HashSet<String>) mSettings.getStringSet("PREF_COOKIES", new HashSet<String>());
                    cookies.clear();

                    //Clear Authpreferences
                    mAuthPreferences.setAuthToken(null);
                    mAuthPreferences.setUsername(null);

                    Bundle fbundle = new Bundle();
                    fbundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mAuthPreferences.getPrimarySiteUrl());
                    mFirebaseAnalytics.logEvent("ACCOUNT_LOGOUT_AUTH", fbundle);

                    finish();
                    break;
            }
            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_content_tabs);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            mNodeTypesSize = b.getInt("nodeTypesSize");
            mSiteProtocol = b.getString("SiteProtocol");
            mSiteDomain = b.getString("SiteDomain");
            mPublicViewMode = b.getBoolean("publicViewMode");
        }

        mAccountManager = AccountManager.get(this);
        mAuthPreferences = new AuthPreferences(this);
        authToken = mAuthPreferences.getAuthToken();

        // TODO: https://github.com/googlesamples/android-DirectShare
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
            List<ShortcutInfo> shortcuts = new ArrayList<>();
            for (int j = 0; j < MyApplication.gblNodeTypeSettings.size(); j++) {
                SettingsType modelNodeType = MyApplication.gblNodeTypeSettings.get(j);
                Log.d(TAG, "onClick: " + modelNodeType.getNodeType());
                if (modelNodeType.userHasAccesstoCreate()) {
                    ShortcutInfo shortcut = new ShortcutInfo.Builder(getApplicationContext(), "id1_" + modelNodeType.getNodeType())
                            .setShortLabel("Website " + modelNodeType.getNodeType())
                            .setLongLabel("Open the website " + modelNodeType.getNodeType())
                            .setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_photo))
                            .setIntent(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://www.mysite.example.com/")))
                            .build();
                    shortcuts.add(shortcut);
                }
            }
            shortcutManager.setDynamicShortcuts(shortcuts);
        }*/

        DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        Menu nav_Menu = navigationView.getMenu();

        ImageView profilePicture = (ImageView) headerView.findViewById(R.id.profile_header);
        TextView fullName = (TextView) headerView.findViewById(R.id.full_name);
        TextView email = (TextView) headerView.findViewById(R.id.email_address);

        String accountName = mAuthPreferences.getAccountName();
        if(accountName!=null && !accountName.isEmpty()) {
            nav_Menu.findItem(R.id.removeAccount).setTitle("Logout: "+accountName);
            fullName.setText(accountName);
        }
        if(mPublicViewMode){
            nav_Menu.findItem(R.id.removeAccount).setVisible(false);
            fullName.setText("Anonymous(uid = 0)");
            email.setText(mSiteProtocol+mSiteDomain);
        }else{
            email.setText(mAuthPreferences.getPrimarySiteProtocol()+mAuthPreferences.getPrimarySiteUrl());
        }

        Account account = AccountUtils.getAccount(SiteContentTabsActivity.this, accountName);
        if(account != null) {
            AccountManager am = AccountManager.get(SiteContentTabsActivity.this);
            String site_domain = am.getUserData(account, SiteLoginActivity.PARAM_USER_SITE_URL);
            String site_protocol = am.getUserData(account, SiteLoginActivity.PARAM_USER_SITE_PROTOCOL);
            Log.d(TAG, "refreshNavigationView: site_domain"+site_domain);
            //email.setText(site_domain);
            mAuthPreferences.setPrimarySiteUrl(site_domain);
            mAuthPreferences.setPrimarySiteProtocol(site_protocol);
        }else {
            //email.setText(mAuthPreferences.getPrimarySiteUrl());
        }

        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_format_bullet);

        String mDrawerTitle = (String)(getSupportActionBar().getTitle());
        Log.d(TAG, "onCreate: mDrawerTitle"+mDrawerTitle);

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getSupportActionBar().setTitle("closed");
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle("opened");
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton ad_fab = (FloatingActionButton) findViewById(R.id.ad_fab);
        ad_fab = findViewById(R.id.ad_fab);
        ad_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> postTypeList = new ArrayList<String>();
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SiteContentTabsActivity.this, android.R.layout.select_dialog_singlechoice);
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
                    Intent intent = new Intent(SiteContentTabsActivity.this, ActivityPost.class);
                    intent.putExtras(b);
                    startActivity(intent);
                }else if(postTypeList.size() > 1) {
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(SiteContentTabsActivity.this);
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
                            Intent intent = new Intent(SiteContentTabsActivity.this, ActivityPost.class);
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    });
                    builderSingle.show();
                }else{
                    new AlertDialog.Builder(SiteContentTabsActivity.this)
                            .setTitle("No Access")
                            .setMessage("You don't have access to post content here! If this is in error, Please make sure you are logged in and contact your site administrator!!")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });/*
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_site_content_tabs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_site_content_tabs, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
           // textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            Log.d(TAG, "SectionsPagerAdapter getItem: "+mSiteDomain);
            return FragmentADRedsox.newInstance(position, mSiteProtocol, mSiteDomain);
        }

        @Override
        public CharSequence getPageTitle(int i) {
            Log.d(TAG, "getPageTitle: "+i);
            switch (i) {
                case 0:
                    return "All Content";
                case 1:
                    return "Promoted Content";
                default:
                    try {
                        Log.d(TAG, "getPageTitle: i "+i);
                        if(i > 0 && MyApplication.gblNodeTypeSettings.size() > (i-2)) {
                            SettingsType mStype = MyApplication.gblNodeTypeSettings.get(i - 2);
                            Log.d(TAG, "getPageTitle: mStype.getNodeType() " + mStype.getNodeType());
                            return mStype.getNodeType();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    return "Error"+i;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            Log.d(TAG, "getCount: "+mNodeTypesSize+2);
            return mNodeTypesSize+2;
        }
    }
}
