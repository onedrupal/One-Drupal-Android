package com.prominentdev.blog.widgets;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.prominentdev.blog.R;

/**
 * Created by Narender on 10/13/2015.
 **/
public class ProgressDialogAsync extends Dialog {

    public ProgressDialogAsync(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getAttributes().windowAnimations = R.style.dialog_animation_fade;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.fw_general_progress);
        setCancelable(false);
    }

    public void cancel() {
        if (isShowing())
            dismiss();
    }
}