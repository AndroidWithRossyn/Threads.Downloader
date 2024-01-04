package com.banrossyn.storydownloader.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import android.view.Gravity;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.banrossyn.storydownloader.R;
import java.io.File;
public class Utils {

    //TODO:  change your Onesignal APP ID
    public static final String ONESIGNAL_ID = "e4d666b5-9b97-48b7-97fe-c44be3f4ec43";
    // TODO: Enter your Admob test id
    public static final String ADMOB_TEST_ID = "fsdfdfg";

    // TODO: Change your download directory if you want...
    public static final String DOWNLOAD_DIRECTORY = "ThSaver";

    public static String SHAREDPREFERENCEFILENAME = "THSAVER-SHARED-PREFERENCE";
    public static String ThemeStatus= "ThemeStatus";
    public static String bannerAdId = "bannerAdId";
    public static String bannerAdFullId = "bannerAdFullId";
    public static String showInterstitial = "showInterstitial";
    public static String interstitialAdId = "interstitialAdId";
    public static String Version = "Version";
    public static String Adshow = "Adshow";
    public static String PrivacyPolicy = "PrivacyPolicy";
    public static String HowToUse = "HowToUse";

    private Context context;
    private SharedPreferences preferences;
    private Activity activity;

    public Utils(Activity activity) {
        this.activity = activity;
        context = activity;
        preferences = context.getSharedPreferences(SHAREDPREFERENCEFILENAME, Context.MODE_PRIVATE);
    }
    public void saveBooleanValue(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBooleanValue(String key) {
        return preferences.getBoolean(key, false);
    }

    public void saveStringValue(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }


    public String getStringValue(String key) {
        return preferences.getString(key, "");
    }
    public void saveInt(String key, int value) {
        preferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }
    public static void setToast(Context context, String str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static String handleLinkPasting(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
            ClipData clipData = clipboardManager.getPrimaryClip();
            if (clipData != null && clipData.getItemCount() > 0) {
                CharSequence link = clipData.getItemAt(0).getText();
                if (isLinkValid(link)) {
                    return link.toString();
                }
            }
        }
        return null; // Return null if no valid link is found in the clipboard.
    }

    private static boolean isLinkValid(CharSequence link) {
        // You can implement your own logic to validate the link.
        // For simplicity, we will consider it valid if it starts with "http://" or "https://"
        return link != null && (link.toString().startsWith("http://") || link.toString().startsWith("https://"));
    }

    public static void shareImage(Context context, String filePath) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.share_txt));
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), filePath, "", null);
            Uri screenshotUri = Uri.parse(path);
            intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            intent.setType("image/*");
            context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.share_image_via)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void shareImageVideoOnWhatsapp(Context context, String filePath, boolean isVideo) {
        Uri imageUri = Uri.parse(filePath);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setPackage("com.whatsapp");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        if (isVideo) {
            shareIntent.setType("video/*");
        } else {
            shareIntent.setType("image/*");
        }
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(shareIntent);
        } catch (Exception e) {
            Utils.setToast(context, "Whtasapp not installed.");
        }
    }

    public static void shareVideo(Context context, String filePath) {
        Uri mainUri = Uri.parse(filePath);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("video/mp4");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(Intent.createChooser(sharingIntent, "Share Video using"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_LONG).show();
        }
    }




    public static String getStoreVideoExternalStorage(Activity context, String folder) {

        File file = new File(Environment.getExternalStorageDirectory() + "/Download/", folder);

        if (!file.exists())
            file.mkdirs();

        return file.getAbsolutePath();

    }



    public static boolean isNetworkAvailable(Activity context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities activeNetwork = connectivityManager.getNetworkCapabilities(network);
            return activeNetwork != null && ((activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)));
        }else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }

    public static String getExtension(String url) {

        return MimeTypeMap.getFileExtensionFromUrl(url);

    }

    public static String getPermissionStatus(Activity activity, String androidPermissionName) {
        if (ContextCompat.checkSelfPermission(activity, androidPermissionName) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermissionName)) {
                return "blocked";
            }
            return "denied";
        }
        return "granted";
    }




}