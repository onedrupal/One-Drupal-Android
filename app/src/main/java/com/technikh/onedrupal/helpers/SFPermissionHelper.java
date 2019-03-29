package com.technikh.onedrupal.helpers;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.technikh.onedrupal.R;

public class SFPermissionHelper {


    private static SFPermissionHelperInterface sfPermissionHelperInterface;

    public interface SFPermissionHelperInterface {
        void onDialogCanceled();
    }

    //Ref: http://developer.android.com/training/permissions/requesting.html
    //Define all the manifest permissions here at all costs
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String PERMISSION_READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    public static final String PERMISSION_RECEIVE_SMS = Manifest.permission.RECEIVE_SMS;

    public static final int PERMISSION_REQUEST_FOR_CAMERA = 1;
    public static final int PERMISSION_REQUEST_FOR_EXTERNAL_STORAGE = 2;
    public static final int PERMISSION_REQUEST_FOR_READ_PHONE_STATE = 3;
    public static final int PERMISSION_REQUEST_FOR_READ_CONTACTS = 4;
    public static final int PERMISSION_REQUEST_FOR_RECEIVE_SMS = 513;

    //Define all the messages to be shown to user when he first denies the request
    public static final String MSG_CAMERA = "This let's us to capture your image and access it from storage.";
    public static final String MSG_WRITE_EXTERNAL_STORAGE = "This let's app to store and access information on your phone and it's SD card.";
    public static final String MSG_READ_PHONE_STATE = "This let's app to get your primary mobile number and fill in the form, for your ease.";
    public static final String MSG_READ_READ_CONTACTS = "This let's app to read the contact details you intend to use.";
    public static final String MSG_READ_RECEIVE_SMS = "This let's app to receive the SMS sent containing verification code for this app.";

    public static final String MSG_GENERIC_DENIAL = "It looks like you turned off permissions required for this feature to work.";

    public static void setDialogListener(SFPermissionHelperInterface dialogListener) {
        sfPermissionHelperInterface = dialogListener;
    }

    public static boolean hasPermissionToAccess(Context context, String... permissionsRequiredByDev) {
        boolean allPermissionGranted = true;
        for (String singlePermissionName : permissionsRequiredByDev) {
            if (ContextCompat.checkSelfPermission(context, singlePermissionName) != PackageManager.PERMISSION_GRANTED) {
                allPermissionGranted = false;
                break;
            }
        }
        return allPermissionGranted;
    }

    public static void requestPermissions(Activity activity, String message, int permissionRequestCode,
                                          String... permissionsRequiredByDev) {
        SFTinyDB SFTinyDB = new SFTinyDB(activity);
        boolean permissionAskedForFirstTime = !SFTinyDB.getBoolean(permissionsRequiredByDev[0]);

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionsRequiredByDev[0])) {
            // 2. Permission has been asked earlier but not been granted earlier by user. Show an information dialog to user
            showInformationDialog(activity, message, permissionRequestCode, permissionsRequiredByDev);
        } else {
            if (permissionAskedForFirstTime) {
                // 1. Permission has been asked for first time, it was never asked earlier.
                SFTinyDB.putBoolean(permissionsRequiredByDev[0], true);

                // Ask for actual permission
                requestActualPermission(activity, permissionRequestCode, permissionsRequiredByDev);
            } else {
                // 3. If you asked a couple of times before, and the user has said "no, and stop asking"
                showGoToSettingsDialog(activity);
            }
        }
    }

    private static void requestActualPermission(Activity activity, int permissionRequestCode, String... permissionsRequiredByDev) {
        ActivityCompat.requestPermissions(activity, permissionsRequiredByDev, permissionRequestCode);
    }

    private static void showInformationDialog(final Activity activity, String message, final int permissionRequestCode, final String... permissionsRequiredByDev) {
        SFDialogsHelper.showCallbackDialog(activity, message, "Continue", "Cancel", new SFDialogsHelper.SFSimpleCallbackIFace() {
            @Override
            public void onPositiveButtonClicked() {
                requestActualPermission(activity, permissionRequestCode, permissionsRequiredByDev);
            }

            @Override
            public void onNegativeButtonClicked() {
                //EventBus.getDefault().post(new EventsFromFragments().setPermissionInfoDialog(true));
            }
        });
    }

    public static void requestPermissionsFromFragment(Fragment fragment, String message, int permissionRequestCode,
                                                      String... permissionsRequiredByDev) {
        SFTinyDB SFTinyDB = new SFTinyDB(fragment.getActivity());
        boolean permissionAskedForFirstTime = !SFTinyDB.getBoolean(permissionsRequiredByDev[0]);

        if (fragment.shouldShowRequestPermissionRationale(permissionsRequiredByDev[0])) {
            // 2. Permission has been asked earlier but not been granted earlier by user. Show an information dialog to user
            showInformationDialogForFragment(fragment, message, permissionRequestCode, permissionsRequiredByDev);
        } else {
            if (permissionAskedForFirstTime) {
                // 1. Permission has been asked for first time, it was never asked earlier.
                SFTinyDB.putBoolean(permissionsRequiredByDev[0], true);

                // Ask for actual permission
                requestActualPermissionFromFragment(fragment, permissionRequestCode, permissionsRequiredByDev);
            } else {
                // 3. If you asked a couple of times before, and the user has said "no, and stop asking"
                showGoToSettingsDialog(fragment.getActivity());
            }
        }
    }

    private static void requestActualPermissionFromFragment(Fragment fragment, int permissionRequestCode, String... permissionsRequiredByDev) {
        fragment.requestPermissions(permissionsRequiredByDev, permissionRequestCode);
    }

    private static void showInformationDialogForFragment(final Fragment fragment, String message,
                                                         final int permissionRequestCode, final String... permissionsRequiredByDev) {
        SFDialogsHelper.showCallbackDialog(fragment.getActivity(), message, "Continue", "Cancel", new SFDialogsHelper.SFSimpleCallbackIFace() {
            @Override
            public void onPositiveButtonClicked() {
                requestActualPermissionFromFragment(fragment, permissionRequestCode, permissionsRequiredByDev);
            }

            @Override
            public void onNegativeButtonClicked() {
                if (sfPermissionHelperInterface != null) {
                    sfPermissionHelperInterface.onDialogCanceled();
                }
            }
        });
    }

    private static void showGoToSettingsDialog(final Activity activity) {
        SFDialogsHelper.showCallbackDialog(activity, "It looks like you turned off permissions required for this feature. It can be enabled under Phone Settings > Apps > "
                + activity.getString(R.string.app_name) + " > Permissions", "Settings", "Cancel", new SFDialogsHelper.SFSimpleCallbackIFace() {
            @Override
            public void onPositiveButtonClicked() {
                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(myAppSettings);
                if (sfPermissionHelperInterface != null) {
                    sfPermissionHelperInterface.onDialogCanceled();
                }
            }

            @Override
            public void onNegativeButtonClicked() {
                if (sfPermissionHelperInterface != null) {
                    sfPermissionHelperInterface.onDialogCanceled();
                }
            }
        });
    }
}