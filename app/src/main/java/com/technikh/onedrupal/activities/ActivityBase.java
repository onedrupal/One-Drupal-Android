package com.technikh.onedrupal.activities;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Context;
import android.os.Build;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.technikh.onedrupal.R;
import com.technikh.onedrupal.helpers.SessionManager;
import com.technikh.onedrupal.widgets.ProgressDialogAsync;

public class ActivityBase extends AppCompatActivity {

    Context context;
    SessionManager sessionManager;
    ProgressDialogAsync _progressDialogAsync;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        context = this;
        sessionManager = new SessionManager(context);
        _progressDialogAsync = new ProgressDialogAsync(context);
    }

    protected FragmentTransaction getDefaultFragmentTransaction() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.activity_open_enter, R.anim.activity_close_exit,
                R.anim.activity_open_enter, R.anim.activity_close_exit);
        return fragmentTransaction;
    }

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

    @Override
    public void finishAfterTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.finishAfterTransition();
        } else {
            super.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}