package com.technikh.onedrupal.helpers;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.technikh.onedrupal.R;

public class PDCallbackDialogs {

    static PDCallbackDialogsIFace mCallback;
    private static PDSingleButtonCallBackIFace pdSingleButtonCallBackIFace;
    private static PDInputDialogCallbackIFace pdInputDialogCallbackIFace;
    private static PDListDialogCallbackIFace pdListDialogCallbackIFace;

    public interface PDCallbackDialogsIFace {
        void onPositiveButtonClicked();

        void onNegativeButtonClicked();
    }

    public interface PDSingleButtonCallBackIFace {
        void onPositiveButtonClicked();
    }

    private interface PDInputDialogCallbackIFace {
        void onPositiveButtonClicked(String userMessage);

        void onNegativeButtonClicked();
    }

    public interface PDListDialogCallbackIFace {
        void onItemSelected(String selectedText, int dialogItemPosition);
    }

    public static void showdialog(Context context, String message, String positiveText,
                                  String negativeText, PDCallbackDialogsIFace pdCallbackDialogsIFace) {
        mCallback = pdCallbackDialogsIFace;
        new MaterialDialog.Builder(context)
                .content(message != null ? message : "Message not specified.")
                .positiveText(positiveText)
                .negativeText(negativeText)
                .btnSelector(R.drawable.md_btn_selector)
                .positiveColor(ContextCompat.getColor(context, R.color.app_primary))
                .negativeColor(Color.BLACK)
                .backgroundColor(Color.WHITE)
                .contentColor(Color.BLACK)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                        mCallback.onPositiveButtonClicked();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                        mCallback.onNegativeButtonClicked();
                    }
                }).show();
    }

    public static void showSingleButtonDialog(Context context, String title, String message,
                                              PDSingleButtonCallBackIFace pdSingleButtonCallBackIFace) {
        PDCallbackDialogs.pdSingleButtonCallBackIFace = pdSingleButtonCallBackIFace;

        new MaterialDialog.Builder(context)
                .title(title)
                .titleColor(Color.BLACK)
                .content(message != null ? PDUtils.encodeToHTML(message) : "Message not specified.")
                .positiveText(android.R.string.ok)
                .positiveColor(ContextCompat.getColor(context, R.color.app_primary))
                .btnSelector(R.drawable.md_btn_selector)
                .backgroundColor(Color.WHITE)
                .contentColor(Color.BLACK)
                .cancelable(false)
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog,
                                        @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                        PDCallbackDialogs.pdSingleButtonCallBackIFace.onPositiveButtonClicked();
                    }
                })
                .build()
                .show();
    }

    public static void showCallbackDialog(Context context, String message, String positiveText,
                                          String negativeText, PDCallbackDialogsIFace pdSimpleCallbackIFace) {
        mCallback = pdSimpleCallbackIFace;

        new MaterialDialog.Builder(context)
                .content(message != null ? PDUtils.encodeToHTML(message) : "Message not specified.")
                .positiveText(positiveText)
                .negativeText(negativeText)
                .btnSelector(R.drawable.md_btn_selector)
                .positiveColor(ContextCompat.getColor(context, R.color.app_primary))
                .negativeColor(Color.BLACK)
                .backgroundColor(Color.WHITE)
                .contentColor(Color.BLACK)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                        mCallback.onPositiveButtonClicked();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                        mCallback.onNegativeButtonClicked();
                    }
                })
                .build()
                .show();
    }

    public static void showCallbackDialog(Context context, CharSequence title, String message, String positiveText,
                                          String negativeText, PDCallbackDialogsIFace pdSimpleCallbackIFace) {
        mCallback = pdSimpleCallbackIFace;

        new MaterialDialog.Builder(context)
                .title(title != null ? title : "Title not specified.")
                .content(message != null ? PDUtils.encodeToHTML(message) : "Message not specified.")
                .positiveText(positiveText)
                .negativeText(negativeText)
                .btnSelector(R.drawable.md_btn_selector)
                .titleColor(ContextCompat.getColor(context, R.color.app_primary))
                .positiveColor(ContextCompat.getColor(context, R.color.app_primary))
                .negativeColor(Color.BLACK)
                .backgroundColor(Color.WHITE)
                .contentColor(Color.BLACK)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                        mCallback.onPositiveButtonClicked();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                        mCallback.onNegativeButtonClicked();
                    }
                })
                .build()
                .show();
    }
}