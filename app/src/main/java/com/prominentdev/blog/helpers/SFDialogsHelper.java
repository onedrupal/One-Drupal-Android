package com.prominentdev.blog.helpers;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.SparseArray;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.prominentdev.blog.R;

import java.util.ArrayList;
import java.util.Arrays;

public class SFDialogsHelper {

    private static SFSimpleCallbackIFace mCallback;
    private static SFSingleButtonCallBackIFace sfSingleButtonCallBackIFace;
    private static SFInputDialogCallbackIFace sfInputDialogCallbackIFace;
    private static SFListDialogCallbackIFace sfListDialogCallbackIFace;
    private static SFMultiListDialogCallbackIFace sfMultiListDialogCallbackIFace;

    public interface SFSimpleCallbackIFace {
        void onPositiveButtonClicked();

        void onNegativeButtonClicked();
    }

    public interface SFSingleButtonCallBackIFace {
        void onPositiveButtonClicked();
    }

    public interface SFInputDialogCallbackIFace {
        void onPositiveButtonClicked(String userMessage);

        void onNegativeButtonClicked();
    }

    public interface SFListDialogCallbackIFace {
        void onItemSelected(String selectedText, int dialogItemPosition);
    }

    public interface SFMultiListDialogCallbackIFace {
        void onItemSelected(ArrayList<Integer> selectedIndices);
    }

    public static MaterialDialog showSimpleDialog(Context context, String message) {

        return new MaterialDialog.Builder(context)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .content(message != null ? PDUtils.encodeToHTML(message) : "Message not specified.")
                .backgroundColor(Color.WHITE)
                .contentColor(ContextCompat.getColor(context, R.color.app_text_black_light))
                .build();
    }

    public static void showCallbackDialog(Context context, String message, String positiveText,
                                          String negativeText, SFSimpleCallbackIFace sfSimpleCallbackIFace) {
        mCallback = sfSimpleCallbackIFace;

        new MaterialDialog.Builder(context)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .content(message != null ? PDUtils.encodeToHTML(message) : "Message not specified.")
                .positiveText(positiveText)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .autoDismiss(false)
                .negativeText(negativeText)
                .btnSelector(R.drawable.button_selector_dialog)
                .positiveColor(ContextCompat.getColor(context, R.color.app_primary))
                .negativeColor(ContextCompat.getColor(context, R.color.app_text_black_light))
                .backgroundColor(Color.WHITE)
                .contentColor(ContextCompat.getColor(context, R.color.app_text_black_light))
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

    public static void showCallbackDialog(Context context, String title, String message, String positiveText,
                                          String negativeText, SFSimpleCallbackIFace sfSimpleCallbackIFace) {
        mCallback = sfSimpleCallbackIFace;

        new MaterialDialog.Builder(context)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .title(title != null ? title : "Title not specified.")
                .content(message != null ? PDUtils.encodeToHTML(message) : "Message not specified.")
                .positiveText(positiveText)
                .negativeText(negativeText)
                .btnSelector(R.drawable.button_selector_dialog)
                .titleColor(ContextCompat.getColor(context, R.color.app_text_black_light))
                .positiveColor(ContextCompat.getColor(context, R.color.app_green))
                .negativeColor(ContextCompat.getColor(context, R.color.app_text_black_light))
                .backgroundColor(Color.WHITE)
                .contentColor(ContextCompat.getColor(context, R.color.app_text_black_light))
                .canceledOnTouchOutside(false)
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

    public static void showErrorDialog(Context context, String title, String message) {
        new MaterialDialog.Builder(context)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .title(title)
                .content((message != null && !message.isEmpty()) ? message : "Message not specified.")
                .positiveText(android.R.string.ok)
                .titleColor(ContextCompat.getColor(context, R.color.app_primary))
                .positiveColor(ContextCompat.getColor(context, R.color.app_primary))
                .btnSelector(R.drawable.button_selector_dialog)
                .backgroundColor(Color.WHITE)
                .contentColor(ContextCompat.getColor(context, R.color.app_text_black_light))
                .positiveText("Dismiss")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static void showSingleButtonDialog(Context context, String title, String message,
                                              SFSingleButtonCallBackIFace sfSingleButtonCallBackIFace) {
        SFDialogsHelper.sfSingleButtonCallBackIFace = sfSingleButtonCallBackIFace;

        new MaterialDialog.Builder(context)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .title(title)
                .titleColor(ContextCompat.getColor(context, R.color.app_text_black_light))
                .content(message != null ? PDUtils.encodeToHTML(message) : "Message not specified.")
                .positiveText(android.R.string.ok)
                .positiveColor(ContextCompat.getColor(context, R.color.app_primary))
                .btnSelector(R.drawable.button_selector_dialog)
                .backgroundColor(Color.WHITE)
                .contentColor(ContextCompat.getColor(context, R.color.app_text_black_light))
                .cancelable(false)
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog,
                                        @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                        SFDialogsHelper.sfSingleButtonCallBackIFace.onPositiveButtonClicked();
                    }
                })
                .build()
                .show();
    }

    public static void showInputDialog(final Context context, String title, String message, String currentReview,
                                       String positiveText, String negativeText, int colorCategory,
                                       SFInputDialogCallbackIFace sfInputDialogCallbackIFace) {
        SFDialogsHelper.sfInputDialogCallbackIFace = sfInputDialogCallbackIFace;

        new MaterialDialog.Builder(context)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .title(title)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .input(message, currentReview, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog materialDialog, CharSequence input) {
                        materialDialog.dismiss();
                        PDUtils.hideKeyboard(context);
                        SFDialogsHelper.sfInputDialogCallbackIFace.onPositiveButtonClicked(input.toString());
                    }
                })
                .inputRange(100, 2000, Color.RED)
                .widgetColor(colorCategory)
                .titleColor(ContextCompat.getColor(context, R.color.app_text_black_light))
                .positiveText(positiveText)
                .negativeText(negativeText)
                .btnSelector(R.drawable.button_selector_dialog)
                .positiveColor(colorCategory)
                .negativeColor(ContextCompat.getColor(context, R.color.app_text_black_light))
                .backgroundColor(Color.WHITE)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                        PDUtils.hideKeyboard(context);
                        SFDialogsHelper.sfInputDialogCallbackIFace.onNegativeButtonClicked();
                    }
                })
                .build()
                .show();
    }

    public static void showListDialog(Context context, SparseArray<String> dialogOptions,
                                      SFListDialogCallbackIFace sfListDialogCallbackIFace) {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (int i = 0; i < dialogOptions.size(); i++) {
            stringArrayList.add(dialogOptions.valueAt(i));
        }
        showListDialog(context, stringArrayList, sfListDialogCallbackIFace);
    }

    public static void showListDialog(Context context, ArrayList<String> dialogOptions,
                                      SFListDialogCallbackIFace sfListDialogCallbackIFace) {
        SFDialogsHelper.sfListDialogCallbackIFace = sfListDialogCallbackIFace;

        new MaterialDialog.Builder(context)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .backgroundColor(Color.WHITE)
                .items(dialogOptions)
                .itemsColor(ContextCompat.getColor(context, R.color.app_text_black_light))
                .btnSelector(R.drawable.button_selector_dialog)
                .canceledOnTouchOutside(true)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        dialog.dismiss();
                        SFDialogsHelper.sfListDialogCallbackIFace.onItemSelected(text.toString(), which);
                    }
                })
                .build()
                .show();
    }


    public static void showMultiListDialog(Context context, String title, ArrayList<String> dialogOptions,
                                           ArrayList<String> presentSelection, String neutralText,
                                           String positiveText, String negativeText, int colorCategory,
                                           SFMultiListDialogCallbackIFace sfMultiListDialogCallbackIFace) {
        SFDialogsHelper.sfMultiListDialogCallbackIFace = sfMultiListDialogCallbackIFace;

        ArrayList<Integer> previousSelectedIndices = new ArrayList<>();

        for (int i = 0; i < dialogOptions.size(); i++) {
            for (String presentSelectedString : presentSelection) {
                if (presentSelectedString.equalsIgnoreCase(dialogOptions.get(i))) {
                    previousSelectedIndices.add(i);
                }
            }
        }

        new MaterialDialog.Builder(context)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .title(title)
                .titleColor(Color.BLACK)
                .backgroundColor(Color.WHITE)
                .items(dialogOptions)
                .itemsColor(Color.BLACK)
                .btnSelector(R.drawable.button_selector_dialog)
                .canceledOnTouchOutside(true)
                .choiceWidgetColor(ColorStateList.valueOf(colorCategory))
                .neutralColor(Color.BLACK)
                .neutralText(neutralText)
                .positiveColor(colorCategory)
                .positiveText(positiveText)
                .negativeColor(Color.BLACK)
                .negativeText(negativeText)
                .btnSelector(R.drawable.button_selector_dialog)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SFDialogsHelper.sfMultiListDialogCallbackIFace.onItemSelected(new ArrayList<Integer>());
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (dialog.getSelectedIndices() != null) {
                            SFDialogsHelper.sfMultiListDialogCallbackIFace.onItemSelected(
                                    new ArrayList<>(Arrays.asList(dialog.getSelectedIndices()))
                            );
                        } else {
                            SFDialogsHelper.sfMultiListDialogCallbackIFace.onItemSelected(new ArrayList<Integer>());
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .itemsCallbackMultiChoice(previousSelectedIndices.toArray(new Integer[previousSelectedIndices.size()]), new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                        return false;
                    }
                })
                .build()
                .show();
    }
}