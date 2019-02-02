package com.prominentdev.blog.activities;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.prominentdev.blog.R;
import com.prominentdev.blog.helpers.SessionManager;
import com.prominentdev.blog.widgets.ProgressDialogAsync;

/**
 * Created by Narender Kumar on 3/21/2017.
 * For SFWorx Technologies (LLP), Faridabad (India)
 */

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