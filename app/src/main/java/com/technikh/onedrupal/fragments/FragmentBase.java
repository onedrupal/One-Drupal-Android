package com.technikh.onedrupal.fragments;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.EditText;

import com.technikh.onedrupal.helpers.PDRestClient;
import com.technikh.onedrupal.helpers.PDTinyDB;
import com.technikh.onedrupal.helpers.PDUtilsJSON;
import com.technikh.onedrupal.widgets.ProgressDialogAsync;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FragmentBase extends Fragment {

    public Context context;
    public int screenHeight = 0, screenWidth = 0, statusBarHeight = 0;
    PDTinyDB sfTinyDB;
    ProgressDialogAsync _progressDialogAsync;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        sfTinyDB = new PDTinyDB(context);
        _progressDialogAsync = new ProgressDialogAsync(context);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;
        Rect rectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        statusBarHeight = rectangle.top;
    }

/*    public RecyclerView.ItemDecoration itemDecorationWhite() {
        DividerItemDecoration itemDecorator = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.recyclerview_divider_white));
        return itemDecorator;
    }*/

    public void setTextChangeListener(EditText editText, final TextInputLayout textInputLayout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textInputLayout.setError(null);
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public boolean ensureNonNullFragment(Fragment fragment) {
        return fragment != null && fragment.isAdded() && fragment.isVisible();
    }

    public JSONObject ensureNonNullJObj(JSONObject rootJsonObject, String key) throws JSONException {
        return PDUtilsJSON.ensureNonNullJObj(rootJsonObject, key);
    }

    public JSONArray ensureNonNullJArray(JSONObject rootJsonObject, String key) throws JSONException {
        return PDUtilsJSON.ensureNonNullJArray(rootJsonObject, key);
    }

    public String ensureNonNullString(JSONObject rootJsonObject, String key) throws JSONException {
        return PDUtilsJSON.ensureNonNullString(rootJsonObject, key);
    }

    public long ensureNonNullLong(JSONObject rootJsonObject, String key) throws JSONException {
        return PDUtilsJSON.ensureNonNullLong(rootJsonObject, key);
    }

    public int ensureNonNullInt(JSONObject rootJsonObject, String key) throws JSONException {
        return PDUtilsJSON.ensureNonNullInt(rootJsonObject, key);
    }

    public boolean ensureNonNullBool(JSONObject rootJsonObject, String key) throws JSONException {
        return PDUtilsJSON.ensureNonNullBool(rootJsonObject, key);
    }

    public float ensureNonNullFloat(JSONObject rootJsonObject, String key) throws JSONException {
        return PDUtilsJSON.ensureNonNullFloat(rootJsonObject, key);
    }

    @Override
    public void onDestroyView() {
        PDRestClient.cancel(context);
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        //EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        //EventBus.getDefault().unregister(this);
        super.onStop();
    }
}