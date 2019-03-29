package com.technikh.onedrupal.helpers;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.technikh.onedrupal.BuildConfig;
import com.technikh.onedrupal.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PDUtils {

    private SFUtilsCallback sfUtilsCallback;

    public interface SFUtilsCallback {
        void onSuccess(JSONObject rootJsonObject, String mediaURL);

        void onFailure(int statusCode, String errorResponse);
    }

    public static void showToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void shareApp(Context context) {
        String currentPackageName = "", marketURL = "http://play.google.com/store/apps/details?id=";

        try {
            currentPackageName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        marketURL = marketURL + currentPackageName;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Download " + context.getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey, I'm using " + context.getString(R.string.app_name) +
                " , download to your android smart-phone here. " + marketURL);
        try {
            context.startActivity(shareIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void rateApp(Context context) {
        String marketURI = "market://details?id=", currentPackageName = "";
        String marketURL = "http://play.google.com/store/apps/details?id=";

        try {
            currentPackageName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        marketURI = marketURI + currentPackageName;
        marketURL = marketURL + currentPackageName;

        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(marketURI)));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(marketURL)));
        }
    }

    public static void shareText(Context context, String sharingText) {
        String currentPackageName = "", marketURL = "http://play.google.com/store/apps/details?id=";

        try {
            currentPackageName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        marketURL = marketURL + currentPackageName;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sharingText);
        try {
            context.startActivity(shareIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return target != null && !target.toString().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidUserName(String testStr) {
        return testStr != null && testStr.length() > 2 && !testStr.contains(".") && !testStr.contains(" ");
    }

    public static int parseString(String inputText) {
        return inputText.matches("\\d+") ? Integer.parseInt(inputText) : -1;
    }

    public static void log(String message) {
        if (null == message)
            return;

        if (BuildConfig.DEBUG)
            Log.e(":::LOGGER:::", message);
    }

    public static void logToFile(String message) {
        if (BuildConfig.DEBUG) {
            String formattedData = String.format("%s", (new SimpleDateFormat("dd-MM HH:mm:ss", Locale.getDefault())
                    .format(new Date())) + "\t\t\t" + message + "\n");

            FileOutputStream stream = null;
            String path = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DOWNLOADS;
            try {
                File file = new File(path + "/Logger.txt");
                if (!file.exists()) {
                    file.createNewFile();
                }
                stream = new FileOutputStream(file, true);
                stream.write(formattedData.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveLogcatToFile() {
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
                log.append("\n");
            }

            FileOutputStream stream = null;
            String path = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DOWNLOADS;

            File file = new File(path + "/Logger.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            stream = new FileOutputStream(file, true);
            stream.write(log.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String formatDate(String originalDate, String sourceFormat, String targetFormat) throws ParseException {
        SimpleDateFormat sourceSimpleDateFormat = new SimpleDateFormat(sourceFormat, Locale.getDefault());
        SimpleDateFormat requiredSimpleDateFormat = new SimpleDateFormat(targetFormat, Locale.getDefault());

        return requiredSimpleDateFormat.format(sourceSimpleDateFormat.parse(originalDate));
    }

    public static String formatUnixDate(long unixTimeStamp, String targetFormat) {
        Date sourceDate = new Date(unixTimeStamp * 1000L);
        SimpleDateFormat requiredSimpleDateFormat = new SimpleDateFormat(targetFormat, Locale.getDefault());
        return requiredSimpleDateFormat.format(sourceDate);
    }

    public static String formatTimeAgo(long originalDate) {
        return DateUtils.getRelativeTimeSpanString(originalDate * 1000L, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int pixel) {
        return (int) (pixel / Resources.getSystem().getDisplayMetrics().density);
    }

    public static String[] getAppVersion(Context context) {
        try {
            int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            return new String[]{String.valueOf(versionCode), versionName};
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return new String[]{"#0", "version 0"};
        }
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getVersionCode(final Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static String getPackageName(final Context context) {
        return context.getPackageName();
    }

    public static int[] getDisplayDimen(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new int[]{metrics.heightPixels, metrics.widthPixels};
    }

    public static void setCustomFontOnToolbar(Context context, Toolbar toolbar) {
        TextView toolbarTitle = null;
        Typeface externalFont = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        for (int i = 0; i < toolbar.getChildCount(); ++i) {
            View child = toolbar.getChildAt(i);

            // assuming that the title is the first instance of TextView
            // you can also check if the title string matches
            if (child instanceof TextView) {
                toolbarTitle = (TextView) child;
                toolbarTitle.setTypeface(externalFont);
                break;
            }
        }
    }

    public static Spanned encodeToHTML(String stringToEncode) {
        if (null == stringToEncode)
            return new SpannableString("");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(stringToEncode, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(stringToEncode);
        }
    }

    public static void openLink(Context context, String urlToOpen) {
        if (null == context || urlToOpen.isEmpty())
            return;

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen));
        context.startActivity(browserIntent);
    }

    public static void hideKeyboard(Context context) {
        View view = ((Activity) context).getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static void contactUs(Context context) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name) + " v" + getVersionName(context) + " Feedback");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constants.SUPPORT_MAIL});
        try {
            context.startActivity(Intent.createChooser(emailIntent, "Choose Client"));
        } catch (ActivityNotFoundException e) {
            showToast(context, "Sorry no email client found.");
        }
    }

    public static void expandView(final View view) {
        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        view.getLayoutParams().height = 1;
        view.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                view.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(a);
    }

    public static void collapseView(final View view) {
        final int initialHeight = view.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(a);
    }

    public static void overrideOpenTransitionFade(Context context) {
        ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void overrideCloseTransitionFade(Context context) {
        ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void printSignatures(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                String keyHash = "Key Hash: " + Base64.encodeToString(md.digest(), Base64.DEFAULT);
                String keySHA1 = "Key SHA1: " + getCertificateSHA1Fingerprint(context);

                PDUtils.log("Key Hash: " + keyHash);
                PDUtils.log("Key SHA1: " + keySHA1);
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String getCertificateSHA1Fingerprint(Context mContext) {
        PackageManager pm = mContext.getPackageManager();
        String packageName = mContext.getPackageName();
        int flags = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();
        InputStream input = new ByteArrayInputStream(cert);
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        X509Certificate c = null;
        try {
            c = (X509Certificate) cf.generateCertificate(input);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        String hexString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(c.getEncoded());
            hexString = byte2HexFormatted(publicKey);
        } catch (NoSuchAlgorithmException | CertificateEncodingException e) {
            e.printStackTrace();
        }
        return hexString;
    }

    public static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1) h = "0" + h;
            if (l > 2) h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1)) str.append(':');
        }
        return str.toString();
    }

    public static int darken(int color, double fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        red = darkenColor(red, fraction);
        green = darkenColor(green, fraction);
        blue = darkenColor(blue, fraction);
        int alpha = Color.alpha(color);

        return Color.argb(alpha, red, green, blue);
    }

    private static int darkenColor(int color, double fraction) {
        return (int) Math.max(color - (color * fraction), 0);
    }
    /*public static String encodeString(String s) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(s.replace(" ", "%20"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return encoded;
    }

    public static String decodeString(String s) {
        String decoded = "";
        try {
            decoded = URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return decoded;
    }*/

    /*public void resolveMediaItem(final Context context, String clipBoardLink, SFUtilsCallback sfUtilsCallbackArg) {
        sfUtilsCallback = sfUtilsCallbackArg;
        SFRestClient.cancel(context);

        if (!isNetworkConnected(context)) {
            sfUtilsCallback.onFailure(0, SFRestClient.getHTTPErrorMessage(0));
            return;
        } else if (clipBoardLink.length() > 0 && clipBoardLink.contains(".")) {
            clipBoardLink = clipBoardLink.substring(0, clipBoardLink.lastIndexOf("."));
            if (clipBoardLink.length() > 0 && clipBoardLink.contains("/")) {
                clipBoardLink = clipBoardLink.substring(clipBoardLink.lastIndexOf("/") + 1);
            } else {
                sfUtilsCallback.onFailure(-1, SFRestClient.getHTTPErrorMessage(-1));
                return;
            }
        } else {
            sfUtilsCallback.onFailure(-1, SFRestClient.getHTTPErrorMessage(-1));
            return;
        }

        final String mediaURL = String.format(Locale.getDefault(), Constants.MUSICALLY_MEDIA_URL, clipBoardLink);
        SFRestClient.get(context, mediaURL, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                log(response.toString());
                sfUtilsCallback.onSuccess(response, mediaURL);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                sfUtilsCallback.onFailure(statusCode, SFRestClient.getHTTPErrorMessage(statusCode));
            }
        });
    }

    public static CardView generateFeedsAds(Context mContext) {
        int displayWidth = SFUtils.getDisplayDimen((Activity) mContext)[1];
        int displayHeight = SFUtils.getDisplayDimen((Activity) mContext)[0];

        int adWidth = SFUtils.pxToDp(displayWidth) - 10;
        int adHeight = SFUtils.pxToDp(displayHeight) - 30;

        if (adWidth < Constants.AD_MIN_WIDTH) {
            adWidth = Constants.AD_MIN_WIDTH;
        } else if (adWidth > Constants.AD_MAX_WIDTH) {
            adWidth = Constants.AD_MAX_WIDTH;
        }

        if (adHeight < Constants.AD_MIN_HEIGHT) {
            adHeight = Constants.AD_MIN_HEIGHT;
        } else if (adHeight > Constants.AD_MAX_HEIGHT) {
            adHeight = Constants.AD_MAX_HEIGHT;
        }

        SFUtils.log(String.valueOf(adWidth) + " " + String.valueOf(adHeight));

        CardView cardView = new CardView(mContext);
        cardView.setCardBackgroundColor(Color.BLACK);
        cardView.setCardElevation(0.0f);
        cardView.setUseCompatPadding(true);
        cardView.setLayoutParams(new CardView.LayoutParams(displayWidth, dpToPx(SFUtils.pxToDp(displayHeight) - 20)));

        final NativeExpressAdView adView = new NativeExpressAdView(mContext);
        adView.setAdSize(new AdSize(adWidth, adHeight));
        adView.setAdUnitId(Constants.NATIVE_FULL_SCREEN);

        final AdRequest adRequest;
        if (BuildConfig.DEBUG) {
            adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        } else {
            adRequest = new AdRequest.Builder().build();
        }
        adView.postDelayed(new Runnable() {
            @Override
            public void run() {
                adView.loadAd(adRequest);
            }
        }, 250);

        cardView.addView(adView);
        return cardView;
    }*/
}