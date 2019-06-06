package com.technikh.onedrupal.activities;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.technikh.onedrupal.R;
import com.technikh.onedrupal.app.MyApplication;
import com.technikh.onedrupal.authenticator.AuthPreferences;
import com.technikh.onedrupal.models.OneMultipleTermsItemTermModel;
import com.technikh.onedrupal.models.SettingsType;
import com.technikh.onedrupal.network.AddCookiesInterceptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.technikh.onedrupal.activities.SliderInputControlWidgetConfigureActivity.loadNodeTypePref;
import static com.technikh.onedrupal.activities.SliderInputControlWidgetConfigureActivity.loadNumberValuePref;
import static com.technikh.onedrupal.activities.SliderInputControlWidgetConfigureActivity.loadStepIncrementPref;
import static com.technikh.onedrupal.activities.SliderInputControlWidgetConfigureActivity.loadWidgetLabelTermIdPref;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link SliderInputControlWidgetConfigureActivity SliderInputControlWidgetConfigureActivity}
 */
public class SliderInputControlWidget extends AppWidgetProvider {

    public static final String TAP_ON_WIDGET_ACTION = "TAPPED_ON_WIDGET";
    public static final String TAP_ON_DECREMENT_ACTION = "TAPPED_ON_DECREMENT_BUTTON";
    public static final String TAP_ON_SAVE_ACTION = "TAP_ON_SAVE_ACTION";
    public static final String TAP_ON_CONFIGURE_ACTION = "TAP_ON_CONFIGURE_ACTION";
    private static final String TAG = "SliderInputWidget";
    private static final String CHANNEL_ID_API = "NOTIFY_API";
    private static Context mContext;

    // Flag for knowing whether onUpdate is called from a tap on widget action
    // Used for showing/not showing toasts during onUpdate
    private boolean cameFromTap = false;
    private static Map<Integer, Float> weightCounter = new HashMap<Integer, Float>();
    private static String apiResponseMessage = "";
    private static AuthPreferences mAuthPreferences;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        mContext = context;

        mAuthPreferences = new AuthPreferences(context);
        String url = mAuthPreferences.getPrimarySiteUrl();
        Log.d(TAG, "updateAppWidget: getPrimarySiteUrl "+url);
        CharSequence widgetText = SliderInputControlWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.slider_input_control_widget);
        Float defaultValue = loadNumberValuePref(context, appWidgetId);
        Log.d(TAG, "updateAppWidget: defaultValue"+defaultValue);
        if((weightCounter.get(appWidgetId) == null)||(weightCounter.get(appWidgetId) == 0.0)){
            weightCounter.put(appWidgetId, defaultValue);
        }
        Log.d(TAG, "updateAppWidget: weightCounter "+weightCounter.get(appWidgetId).toString());
        views.setTextViewText(R.id.appwidget_label, widgetText);

        Random rnd = new Random();
        int color = Color.argb(150, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        views.setInt(R.id.widget_wrapper, "setBackgroundColor", color);

        views.setViewVisibility(R.id.appwidget_status, View.GONE);
        if(!apiResponseMessage.isEmpty()) {
            views.setViewVisibility(R.id.appwidget_status, View.VISIBLE);
            views.setTextViewText(R.id.appwidget_status, apiResponseMessage);
            apiResponseMessage = "";
        }
        views.setTextViewText(R.id.appwidget_text, weightCounter.get(appWidgetId).toString());

        // Set on tap pending intent
        Intent widgetTapIntent = new Intent(context, SliderInputControlWidget.class);
        widgetTapIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        widgetTapIntent.setAction(TAP_ON_WIDGET_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, widgetTapIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.button_increment, pendingIntent);

        widgetTapIntent.setAction(TAP_ON_DECREMENT_ACTION);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, appWidgetId, widgetTapIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.button_decrement, pendingIntent2);

        widgetTapIntent.setAction(TAP_ON_SAVE_ACTION);
        PendingIntent pendingIntentSave = PendingIntent.getBroadcast(context, appWidgetId, widgetTapIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.button_save, pendingIntentSave);

        Intent intent = new Intent(context, SliderInputControlWidgetConfigureActivity.class);
        Bundle b = new Bundle();
        Log.d(TAG, "WidgetConfigureActivity updateAppWidget: appWidgetId"+appWidgetId);
        b.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtras(b);
        PendingIntent pendingIntentConfigure = PendingIntent.getActivity(context, appWidgetId, intent, 0);
        views.setOnClickPendingIntent(R.id.configure_button, pendingIntentConfigure);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        mContext = context;
        Log.d(TAG, "in onReceive with intent action: " + intent.getAction());
        int idOfTappedWidget = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        Log.d(TAG, "Tapped or resized widget with widget id: " + idOfTappedWidget);
        int[] appWidgetIds = {idOfTappedWidget};
        Float defaultValue = loadNumberValuePref(context, idOfTappedWidget);
        if(weightCounter.get(idOfTappedWidget) == null){
            weightCounter.put(idOfTappedWidget, defaultValue);
        }
        Float stepIncrements = loadStepIncrementPref(context, idOfTappedWidget);
        if(intent.getAction().equals(TAP_ON_WIDGET_ACTION)) {
            // Widget was tapped so update that widget
            cameFromTap = true;
            weightCounter.put(idOfTappedWidget, weightCounter.get(idOfTappedWidget)+stepIncrements);
            onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
        } else if(intent.getAction().equals(TAP_ON_DECREMENT_ACTION)) {
            weightCounter.put(idOfTappedWidget, weightCounter.get(idOfTappedWidget)-stepIncrements);
            onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
        } else if(intent.getAction().equals(TAP_ON_SAVE_ACTION)) {
            SliderInputControlWidgetConfigureActivity.saveNumberValuePref(context, idOfTappedWidget, weightCounter.get(idOfTappedWidget));
            makeBlogPost(idOfTappedWidget);
        } else if(intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED)) {
            // Widget's size was changed so update that widget
            onUpdate(context,AppWidgetManager.getInstance(context), appWidgetIds);
        }
    }

    private void makeBlogPost(int idOfTappedWidget) {
        PostApiCallTask mApiCallTask = new PostApiCallTask(idOfTappedWidget);
        mApiCallTask.execute((Void) null);
    }

    public class PostApiCallTask extends AsyncTask<Void, Void, JSONObject> {

        private int idOfTappedWidget;

        PostApiCallTask(int mIdOfTappedWidget) {
            idOfTappedWidget = mIdOfTappedWidget;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject jsonParams = new JSONObject();
            try {
                //title input
                JSONObject titleObject = new JSONObject();
                titleObject.put("value", loadNumberValuePref(mContext, idOfTappedWidget));
                JSONArray titleArray = new JSONArray();
                titleArray.put(titleObject);
                jsonParams.put("title", titleArray);

                JSONObject typeObject = new JSONObject();
                typeObject.put("target_id", loadNodeTypePref(mContext, idOfTappedWidget));
                JSONArray typeArray = new JSONArray();
                typeArray.put(typeObject);
                jsonParams.put("type", typeArray);

                if(loadWidgetLabelTermIdPref(mContext, idOfTappedWidget) > 0) {
                    SettingsType NodeTypeObj = MyApplication.gblGetNodeTypeObj(loadNodeTypePref(mContext, idOfTappedWidget));
                    if (NodeTypeObj != null) {
                        if (NodeTypeObj.getFieldsList().taxonomies.size() > 0) {
                            String taxonomyField = NodeTypeObj.getFieldsList().taxonomies.get(0).mFieldName;
                            JSONObject remotePageObject = new JSONObject();
                            remotePageObject.put("target_id", loadWidgetLabelTermIdPref(mContext, idOfTappedWidget));
                            remotePageObject.put("target_type", "taxonomy_term");
                            JSONArray remotePageArray = new JSONArray();
                            remotePageArray.put(remotePageObject);
                            jsonParams.put(taxonomyField, remotePageArray);
                        }
                    }
                }

                makePostApiCall(jsonParams, idOfTappedWidget);
            }catch (JSONException e){
                e.printStackTrace();
            }
            return jsonParams;
        }
    }
    private void makePostApiCall(JSONObject jsonParams, int idOfTappedWidget) {
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
                .build();
        String url;
        try {
            Request request;
                url = mAuthPreferences.getPrimarySiteProtocol()+mAuthPreferences.getPrimarySiteUrl()+"/node?_format=json";
                request = new Request.Builder()
                        .header("Content-Type", "application/json")
                        // .addHeader("X-CSRF-Token", xCSRFToken)
                        .url(url)
                        .post(api_body)
                        .build();
            Response response = client1.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.d(TAG, "makePostApiCall: isSuccessful");
                apiResponseMessage = "Saved!";
                /*
                createApiNotificationChannel();
                // Create an explicit intent for an Activity in your app
                Intent intent = new Intent(mContext, SiteContentTabsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID_API)
                        .setSmallIcon(R.drawable.ic_post)
                        .setContentTitle("Saved!")
                        .setContentText("Successfully saved node: "+nodeTitle)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(90001, builder.build());*/
            }else{
                Log.d(TAG, "makePostApiCall: fail");
                apiResponseMessage = "Failed to Save!\nError Code: " + response.code();
            }
        }catch (IOException e){
            apiResponseMessage = "Failed to Save!\n"+e.getMessage();
            e.printStackTrace();
        }catch (Exception e){
            apiResponseMessage = "Failed to Save!\n"+e.getMessage();
            e.printStackTrace();
        }
        int[] appWidgetIds = {idOfTappedWidget};
        onUpdate(mContext,AppWidgetManager.getInstance(mContext), appWidgetIds);
    }

    private void createApiNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = mContext.getString(R.string.channel_name);
            String description = mContext.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_API, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            SliderInputControlWidgetConfigureActivity.deleteAllPref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

