package com.technikh.onedrupal.activities;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.technikh.onedrupal.R;
import com.technikh.onedrupal.app.MyApplication;
import com.technikh.onedrupal.authenticator.AuthPreferences;
import com.technikh.onedrupal.models.OneMultipleTermsItemModel;
import com.technikh.onedrupal.models.OneMultipleTermsModel;
import com.technikh.onedrupal.models.SettingsType;
import com.technikh.onedrupal.network.GetSiteDataService;
import com.technikh.onedrupal.network.RetrofitSiteInstance;

import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;

/**
 * The configuration screen for the {@link SliderInputControlWidget SliderInputControlWidget} AppWidget.
 */
public class SliderInputControlWidgetConfigureActivity extends ActivityBase {

    private static final String PREFS_NAME = "com.technikh.onedrupal.SliderInputControlWidget";
    private static final String TAG = "WidgetConfigureActivity";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private static final String PREF_PREFIX_NUMBER_KEY = "widget_number_";
    private static final String PREF_PREFIX_STEP_INCREMENT_KEY = "widget_stepincrement_";
    private static final String PREF_PREFIX_NODE_TYPE_KEY = "widget_nodetype_";
    private static final String PREF_PREFIX_LABEL_TID_KEY = "widget_labeltid_";
    AlertDialog.Builder mAlertDialogBuilder;

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mAppWidgetText, et_default_value, et_step_increments;
    Spinner contentTypeSpinner;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = SliderInputControlWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String widgetText = mAppWidgetText.getText().toString();
            saveTitlePref(context, mAppWidgetId, widgetText);

            String widgetNodeType = contentTypeSpinner.getSelectedItem().toString();
            Log.d(TAG, "onClick: widgetNodeType "+widgetNodeType);
            saveNodeTypePref(context, mAppWidgetId, widgetNodeType);

            // Make API call to get taxonomy term tid and save tid within widget
            postMultipleTaxonomyTermsCreation(widgetText, widgetNodeType);

            saveNumberValuePref(context, mAppWidgetId, Float.parseFloat(et_default_value.getText().toString()));
            saveStepIncrementPref(context, mAppWidgetId, Float.parseFloat(et_step_increments.getText().toString()));

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            SliderInputControlWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    private void postMultipleTaxonomyTermsCreation(String termName, String nodeType) {
        _progressDialogAsync.show();
        SettingsType NodeTypeObj = MyApplication.gblGetNodeTypeObj(nodeType);
        if (NodeTypeObj != null) {
            if (NodeTypeObj.getFieldsList().taxonomies.size() > 0) {
                String taxonomyField = NodeTypeObj.getFieldsList().taxonomies.get(0).mFieldName;
                String taxonomyFieldVocabulary = NodeTypeObj.getFieldsList().taxonomies.get(0).mVocabulary;

                AuthPreferences mAuthPreferences;
                mAuthPreferences = new AuthPreferences(this);
                GetSiteDataService service = RetrofitSiteInstance.getRetrofitSiteInstance(mAuthPreferences.getPrimarySiteProtocol(), mAuthPreferences.getPrimarySiteUrl(), null).create(GetSiteDataService.class);
                //Call the method with parameter in the interface to get the employee data
                OneMultipleTermsModel termsModelObj = new OneMultipleTermsModel();

                if(!taxonomyField.isEmpty()) {
                    Log.d(TAG, "postMultipleTaxonomyTermsCreation: taxonomyField");
                    OneMultipleTermsItemModel termsItemModelObj = new OneMultipleTermsItemModel();
                    termsItemModelObj.field_name = taxonomyField;
                    termsItemModelObj.tags = termName;
                    termsItemModelObj.vid = taxonomyFieldVocabulary;

                    //OneMultipleTermsItemTermModel termsItemTermModelObj = new OneMultipleTermsItemTermModel();
                    termsModelObj.items = new ArrayList<OneMultipleTermsItemModel>();
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
                            //oneMultipleTermsItemsList = response.body().items;
                            if(response.body().items.size() > 0) {
                                int tid = Integer.parseInt(response.body().items.get(0).terms.get(0).tid);
                                Log.d(TAG, "onResponse: postMultipleTaxonomyTermsCreation "+tid);
                                saveWidgetLabelTermIdPref(SliderInputControlWidgetConfigureActivity.this, mAppWidgetId, tid);
                            }
                            //makeBlogPost();

                        }else {
                            _progressDialogAsync.cancel();
                            Log.d(TAG, "onResponse: " + response.toString());
                            Log.d(TAG, "onResponse: call body" + call.request().body().toString());
                            Toast.makeText(MyApplication.getAppContext(), "Something went wrong...Please try later!\n" +call.request().body().toString(), Toast.LENGTH_SHORT).show();
                            //mAlertDialogBuilder.setMessage(getString(R.string.dialog_api_failed_message_prefix) + response.toString()).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<OneMultipleTermsModel> call, Throwable t) {
                        _progressDialogAsync.cancel();
                        Log.d(TAG, "onFailure: "+t.getMessage());
                        Toast.makeText(MyApplication.getAppContext(), "Something went wrong...Please try later!\n"+t.getMessage(), Toast.LENGTH_SHORT).show();
                        //mAlertDialogBuilder.setMessage(getString(R.string.dialog_api_failed_message_prefix)+t.getMessage()).show();
                    }
                });
                _progressDialogAsync.cancel();
            }
        }
    }

    public SliderInputControlWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    static void saveNumberValuePref(Context context, int appWidgetId, Float text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putFloat(PREF_PREFIX_NUMBER_KEY + appWidgetId, text);
        prefs.apply();
    }

    static void saveWidgetLabelTermIdPref(Context context, int appWidgetId, int tid) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_LABEL_TID_KEY + appWidgetId, tid);
        prefs.apply();
    }

    static void saveStepIncrementPref(Context context, int appWidgetId, float text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putFloat(PREF_PREFIX_STEP_INCREMENT_KEY + appWidgetId, text);
        prefs.apply();
    }

    static void saveNodeTypePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_NODE_TYPE_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static Float loadStepIncrementPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        float titleValue = prefs.getFloat(PREF_PREFIX_STEP_INCREMENT_KEY + appWidgetId, 1);
        return titleValue;
    }

    static int loadWidgetLabelTermIdPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int titleValue = prefs.getInt(PREF_PREFIX_LABEL_TID_KEY + appWidgetId, 1);
        return titleValue;
    }

    static Float loadNumberValuePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Float titleValue = prefs.getFloat(PREF_PREFIX_NUMBER_KEY + appWidgetId, 0);
        return titleValue;/*
        if (titleValue != 0) {
            return titleValue;
        } else {
            return null;
        }*/
    }

    static String loadNodeTypePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_NODE_TYPE_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteAllPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.remove(PREF_PREFIX_STEP_INCREMENT_KEY + appWidgetId);
        prefs.remove(PREF_PREFIX_NODE_TYPE_KEY + appWidgetId);
        prefs.remove(PREF_PREFIX_NUMBER_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);


        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.slider_input_control_widget_configure);
        mAppWidgetText = (EditText) findViewById(R.id.appwidget_text);
        et_default_value = (EditText) findViewById(R.id.et_default_value);
        et_step_increments = (EditText) findViewById(R.id.et_step_increments);

        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setTitle("Configure Widget");

        contentTypeSpinner = findViewById(R.id.contentTypeSpinner);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        Log.d(TAG, "onCreate: mAppWidgetId "+mAppWidgetId);
        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.d(TAG, "onCreate: INVALID_APPWIDGET_ID"+AppWidgetManager.INVALID_APPWIDGET_ID);
            finish();
            return;
        }

        mAppWidgetText.setText(loadTitlePref(SliderInputControlWidgetConfigureActivity.this, mAppWidgetId));
        et_default_value.setText("test");
        Float defaultValue = loadNumberValuePref(SliderInputControlWidgetConfigureActivity.this, mAppWidgetId);
        if(defaultValue != null) {
            et_default_value.setText(defaultValue.toString());
        }
        Float stepIncrements = loadStepIncrementPref(SliderInputControlWidgetConfigureActivity.this, mAppWidgetId);
        if(stepIncrements != null) {
            et_step_increments.setText(stepIncrements.toString());
        }

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item);
        int spinner_selected_pos = 0;
        String selected_node_type = loadNodeTypePref(SliderInputControlWidgetConfigureActivity.this, mAppWidgetId);
        for (int j = 0; j < MyApplication.gblNodeTypeSettings.size(); j++) {
            SettingsType modelNodeType = MyApplication.gblNodeTypeSettings.get(j);
            Log.d(TAG, "onClick: "+modelNodeType.getNodeType());
            if(modelNodeType.userHasAccesstoCreate()){
                Log.d(TAG, "onCreate: modelNodeType.getNodeType() "+modelNodeType.getNodeType());
                arrayAdapter.add(modelNodeType.getNodeType());
                if(modelNodeType.getNodeType().equals(selected_node_type)){
                    spinner_selected_pos  = j;
                }
            }
        }
        contentTypeSpinner.setAdapter(arrayAdapter);
        contentTypeSpinner.setSelection(spinner_selected_pos);

        mAlertDialogBuilder = new AlertDialog.Builder(SliderInputControlWidgetConfigureActivity.this)
                .setTitle(getString(R.string.dialog_api_failed_title))
                .setMessage(getString(R.string.dialog_api_failed_message_prefix))
                .setIcon(android.R.drawable.ic_dialog_alert);
    }
}

