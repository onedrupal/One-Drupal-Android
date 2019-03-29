package com.technikh.onedrupal.activities;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.technikh.onedrupal.R;
import com.technikh.onedrupal.helpers.PDUtils;
import com.technikh.onedrupal.helpers.SFPermissionHelper;
import com.technikh.onedrupal.helpers.SessionManager;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class ActivityImageSelector extends AppCompatActivity {

    public static final int REQUEST_CODE_CROP_ACTIVITY = 999;
    private static final String REQUEST_ACTIVITY_COLOR = "REQUEST_ACTIVITY_COLOR";
    private static final String REQUEST_ASPECT_X = "REQUEST_ASPECT_X";
    private static final String REQUEST_ASPECT_Y = "REQUEST_ASPECT_Y";
    public static final String REQUEST_SELECTED_IMAGE_PATH = "REQUEST_SELECTED_IMAGE_PATH";

    private Context _context;
    private SessionManager _sessionManager;

    private static final int REQUEST_CODE_SELECT_IMAGE = 501;
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 601;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private int cropActivityThemeColor = -1;

    private boolean isCapture = false;
    private String SAMPLE_CROPPED_IMAGE_NAME = "";
    private Uri fileUri, mDestinationUri;
    private float aspectRatioX = 1, aspectRatioY = 1;

    public static Intent build(Context context, int cropActivityThemeColor) {
        return build(context, cropActivityThemeColor, 1, 1);
    }

    public static Intent build(Context context, int cropActivityThemeColor, float aspectRatioX, float aspectRatioY) {
        Intent intent = new Intent(context, ActivityImageSelector.class);
        intent.putExtra(ActivityImageSelector.REQUEST_ACTIVITY_COLOR, cropActivityThemeColor);
        intent.putExtra(ActivityImageSelector.REQUEST_ASPECT_X, aspectRatioX);
        intent.putExtra(ActivityImageSelector.REQUEST_ASPECT_Y, aspectRatioY);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _context = this;
        _sessionManager = new SessionManager(_context);
        cropActivityThemeColor = getIntent().getIntExtra(REQUEST_ACTIVITY_COLOR, R.color.app_primary);
        aspectRatioX = getIntent().getFloatExtra(REQUEST_ASPECT_X, 1);
        aspectRatioY = getIntent().getFloatExtra(REQUEST_ASPECT_Y, 1);

        openChooserDialog();
    }

    private void openChooserDialog() {
        ArrayList<String> dialogOptions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.select_image_from)));

        new MaterialDialog.Builder(_context)
                //.typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .backgroundColor(Color.WHITE)
                .items(dialogOptions)
                .itemsColor(Color.BLACK)
                .btnSelector(R.drawable.button_selector_dialog)
                .canceledOnTouchOutside(false)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        dialog.dismiss();
                        if (which == 0) {
                            isCapture = true;
                            checkForCameraPermission();
                        } else if (which == 1) {
                            isCapture = false;
                            checkForStoragePermission();
                        }
                    }
                })
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        finishActivity();
                    }
                })
                .build()
                .show();
    }

    private void checkForCameraPermission() {
        if (SFPermissionHelper.hasPermissionToAccess(_context, SFPermissionHelper.PERMISSION_CAMERA)) {
            checkForStoragePermission();
        } else {
            SFPermissionHelper.requestPermissions((Activity) _context,
                    SFPermissionHelper.MSG_CAMERA,
                    SFPermissionHelper.PERMISSION_REQUEST_FOR_CAMERA,
                    SFPermissionHelper.PERMISSION_CAMERA);
            SFPermissionHelper.setDialogListener(new SFPermissionHelper.SFPermissionHelperInterface() {
                @Override
                public void onDialogCanceled() {
                    finishActivity();
                }
            });
        }
    }

    private void checkForStoragePermission() {
        if (SFPermissionHelper.hasPermissionToAccess(_context, SFPermissionHelper.PERMISSION_WRITE_EXTERNAL_STORAGE)) {
            if (isCapture) {
                startImageCapture();
            } else {
                startImagePicker();
            }
        } else {
            SFPermissionHelper.requestPermissions(this,
                    SFPermissionHelper.MSG_WRITE_EXTERNAL_STORAGE,
                    SFPermissionHelper.PERMISSION_REQUEST_FOR_EXTERNAL_STORAGE,
                    SFPermissionHelper.PERMISSION_WRITE_EXTERNAL_STORAGE);
            SFPermissionHelper.setDialogListener(new SFPermissionHelper.SFPermissionHelperInterface() {
                @Override
                public void onDialogCanceled() {
                    finishActivity();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == SFPermissionHelper.PERMISSION_REQUEST_FOR_CAMERA && grantResults.length > 0) {
            boolean hasUserDeniedAnyOnePermission = false;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    hasUserDeniedAnyOnePermission = true;
                    break;
                }
            }
            if (hasUserDeniedAnyOnePermission) {
                PDUtils.showToast(_context, "Unable to perform action!");
                finishActivity();
            } else {
                checkForStoragePermission();
            }
        } else if (requestCode == SFPermissionHelper.PERMISSION_REQUEST_FOR_EXTERNAL_STORAGE && grantResults.length > 0) {
            boolean hasUserDeniedAnyOnePermission = false;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    hasUserDeniedAnyOnePermission = true;
                    break;
                }
            }
            if (hasUserDeniedAnyOnePermission) {
                PDUtils.showToast(_context, "Unable to perform action!");
                finishActivity();
            } else {
                checkForStoragePermission();
            }
        }
    }

    private void startImageCapture() {
        SAMPLE_CROPPED_IMAGE_NAME = String.format(Locale.getDefault(), "%s_image_%s.jpg",
                _sessionManager.getParticularField(SessionManager.USER_ID), System.currentTimeMillis());
        PDUtils.log(SAMPLE_CROPPED_IMAGE_NAME);
        mDestinationUri = Uri.fromFile(new File(_context.getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE);
    }

    private void startImagePicker() {
        SAMPLE_CROPPED_IMAGE_NAME = String.format(Locale.getDefault(), "%s_image_%s.jpg",
                _sessionManager.getParticularField(SessionManager.USER_ID), System.currentTimeMillis());
        PDUtils.log(SAMPLE_CROPPED_IMAGE_NAME);
        mDestinationUri = Uri.fromFile(new File(_context.getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME));
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/png|image/jpg|image/jpeg");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_SELECT_IMAGE);
    }

    /**
     * Creating file uri to store image/video
     */
    private Uri getOutputMediaFileUri(int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(_context, getString(R.string.file_uri), getOutputMediaFile(type));
        } else {
            return Uri.fromFile(getOutputMediaFile(type));
        }
    }

    /**
     * returning image / video
     */
    public static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath());

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpeg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
                final Uri selectedUri = intent.getData();
                if (selectedUri != null) {
                    startCrop((Activity) _context, intent.getData(), mDestinationUri, cropActivityThemeColor);
                } else {
                    PDUtils.showToast(_context, getString(R.string.ucrop_invalid_image));
                    finishActivity();
                }
            } else if (requestCode == REQUEST_CODE_CAPTURE_IMAGE) {
                startCrop((Activity) _context, fileUri, mDestinationUri, cropActivityThemeColor);
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(_context, intent);
            } else {
                finishActivity();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(_context, intent);
        } else {
            finishActivity();
        }
    }

    private void startCrop(Activity activity, Uri sourceUri, Uri destinationUri, int color) {
        UCrop uCrop = UCrop.of(sourceUri, destinationUri);
        UCrop.Options options = new UCrop.Options();
        options.withMaxResultSize(1500, 1500);
        options.withAspectRatio(aspectRatioX, aspectRatioY);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setStatusBarColor(PDUtils.darken(color, 0.1));
        options.setToolbarColor(color);
        options.setActiveWidgetColor(color);
        uCrop.withOptions(options);
        uCrop.start(activity);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(Context _context, @NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            PDUtils.log("handleCropError: " + cropError);
            PDUtils.showToast(_context, cropError.getMessage());
        } else {
            PDUtils.showToast(_context, _context.getResources().getString(R.string.ucrop_unexpected_error));
        }
        finishActivity();
    }

    private void handleCropResult(Context _context, @NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            final String filePath = new File(resultUri.getPath()).getAbsolutePath();
            final String fileExtension = filePath.substring(filePath.lastIndexOf(".") + 1);
            if (fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("jpeg")
                    || fileExtension.equalsIgnoreCase("png")) {
                PDUtils.log(filePath);
                sendResultBackToCaller(filePath);
            } else {
                PDUtils.showToast(_context, _context.getResources().getString(R.string.ucrop_retrieve_error));
                finishActivity();
            }
        } else {
            PDUtils.showToast(_context, _context.getResources().getString(R.string.ucrop_retrieve_error));
            finishActivity();
        }
    }

    private void sendResultBackToCaller(String filePath) {
        PDUtils.log(filePath);
        Intent intent = new Intent();
        intent.putExtra(REQUEST_SELECTED_IMAGE_PATH, filePath);
        setResult(RESULT_OK, intent);
        finishActivity();
    }

    private void finishActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }
}