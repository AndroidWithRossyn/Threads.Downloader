package com.banrossyn.storydownloader;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.banrossyn.storydownloader.adapter.VideoListAdapter;
import com.banrossyn.storydownloader.model.ThreadsModel;
import com.banrossyn.storydownloader.model.VideoUrl;
import com.banrossyn.storydownloader.util.AdManager;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Priority;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;
import com.google.gson.Gson;
import com.banrossyn.storydownloader.api.APIServices;
import com.banrossyn.storydownloader.api.RestClient;
import com.banrossyn.storydownloader.databinding.ActivityMainBinding;
import com.banrossyn.storydownloader.util.Utils;
import com.banrossyn.storydownloader.util.PermissionUtils;
import com.banrossyn.storydownloader.util.URLConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    MainActivity activity;
    Activity context;
    String url = null;
    ActivityMainBinding binding;
    VideoListAdapter videoListAdapter;
    ThreadsModel threadsModel;
    List<VideoUrl> videoUrls = new ArrayList<>();
    int currentVideo = 0;
    private int fileDownloadingId = 0;
    private Dialog dialogDownload;
    private ProgressBar progressBar;
    private TextView progressExport;
    private Dialog dialogExit, dialogPermission;
    private Intent intent;
    private String intentAction, intentType;
    private ClipboardManager clipboardManager;
    AppUpdateManager appUpdateManager;
    Context update;
    private com.google.android.gms.ads.AdView adViewAdmob;

    private void loadData() {

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        APIServices apiServices = RestClient.getClient().create(APIServices.class);
        apiServices.getThreadsData(url).enqueue(new Callback<ThreadsModel>() {
            @Override
            public void onResponse(Call<ThreadsModel> call, Response<ThreadsModel> response) {

                if (response.isSuccessful() && response.body() != null) {

                    //if its a image
                    threadsModel = response.body();
                    Log.d("downloadflow", "onResponse: urssls:  " + new Gson().toJson(response.body()));

                    if (response.body() != null && !response.body().getImageUrls().isEmpty()) {

                        for (String url : threadsModel.getImageUrls()) {

                            VideoUrl videoUrl = new VideoUrl();
                            videoUrl.setDownloadUrl(url);
                            videoUrl.setIs_video(false);

                            videoUrls.add(videoUrl);
                        }


                    }


                    if (response.body().getVideoUrls() != null && !response.body().getVideoUrls().isEmpty()) {

                        for (VideoUrl url : threadsModel.getVideoUrls()) {

                            VideoUrl videoUrl = new VideoUrl();
                            videoUrl.setDownloadUrl(url.getDownloadUrl());
                            videoUrl.setIs_video(true);
                            videoUrls.add(videoUrl);
                        }

                    }

                    binding.tvDownloadCount.setText("" + videoUrls.size() + "");
                    Log.d("downloadflow", "items count :" + videoUrls.size());

                    binding.tvDownloadCount.setVisibility(View.VISIBLE);

                    videoListAdapter = new VideoListAdapter(context, videoUrls);
                    binding.rvList.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
                    binding.rvList.setAdapter(videoListAdapter);
                    binding.llAllPreview.setVisibility(View.VISIBLE);
                    binding.llDownloder.setVisibility(View.VISIBLE);

                    progressDialog.dismiss();

                } else {

                    progressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<ThreadsModel> call, Throwable t) {

                Log.e("url", "on: " + url);

                progressDialog.dismiss();

                binding.llAllPreview.setVisibility(View.GONE);
            }
        });

    }


    public interface OnDownloadDone {
        void onDownloadDone();
    }

    private Utils utils;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d("TAG", "onWindowFocusChanged: " + hasFocus);
        if (hasFocus) {
            if (Intent.ACTION_SEND.equals(intentAction) && "text/plain".equals(intentType)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                binding.pastlink.setText(sharedText);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, sharedText));
                intentType = null;
                intentAction = null;
            } else onClick(binding.paste);
        }

        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private ConsentInformation consentInformation;
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);

    public void GDPRMessage() {
//        ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(this)
//                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
//                .addTestDeviceHashedId("4F45E161A3144238FA2A0869C6BB2EE1")
//                .build();
        // Set tag for under age of consent. false means users are not under age
        // of consent.
        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
//                .setConsentDebugSettings(debugSettings)
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                (ConsentInformation.OnConsentInfoUpdateSuccessListener) () -> {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                            this,
                            (ConsentForm.OnConsentFormDismissedListener) loadAndShowError -> {
                                if (loadAndShowError != null) {
                                    // Consent gathering failed.
                                    Log.w("TAG", String.format("%s: %s",
                                            loadAndShowError.getErrorCode(),
                                            loadAndShowError.getMessage()));
                                }

                                // Consent has been gathered.
                                if (consentInformation.canRequestAds()) {
                                    initializeMobileAdsSdk();
                                }
                            }
                    );
                },
                (ConsentInformation.OnConsentInfoUpdateFailureListener) requestConsentError -> {
                    // Consent gathering failed.
                    Log.w("TAG", String.format("%s: %s",
                            requestConsentError.getErrorCode(),
                            requestConsentError.getMessage()));
                });

        // Check if you can initialize the Google Mobile Ads SDK in parallel
        // while checking for new consent information. Consent obtained in
        // the previous session can be used to request ads.
        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk();
        }
    }


    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }

        // Initialize the Google Mobile Ads SDK.
        MobileAds.initialize(this);

        // TODO: Request an ad.
        // InterstitialAd.load(...);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        update = activity = this;
        context = this;

        utils = new Utils(MainActivity.this);

        GDPRMessage();
        setDownloadDialog();
        checkPermission();

        exitDialog();
        if (!Utils.isNetworkAvailable(MainActivity.this)) {
            Toast.makeText(MainActivity.this, "No Network Connection.....Try Again", Toast.LENGTH_LONG).show();
        } else {
            bannerAdmob();
            if (utils.getBooleanValue(Utils.showInterstitial) && utils.getBooleanValue(Utils.Adshow)) {
                AdManager.loadInterAd(MainActivity.this, utils.getStringValue(Utils.interstitialAdId));
            }
        }
        appUpdateManager = AppUpdateManagerFactory.create(update);

        //        If user open this app using ShareTo option
        intent = getIntent();
        intentAction = intent.getAction();
        intentType = intent.getType();
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        binding.generate.setOnClickListener(this);
        binding.menu.setOnClickListener(this);
        binding.imageViewDownloads.setOnClickListener(this);
        binding.threads.setOnClickListener(this);
        binding.llDownloder.setOnClickListener(this);

        binding.paste.setOnClickListener(this);
        binding.generate.setOnClickListener(this);
        binding.generate.setOnClickListener(this);


        //Edittext - add the text change listener
        binding.pastlink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (binding.pastlink.getText().toString().isEmpty()) {
                    binding.generate.setBackground(getResources().getDrawable(R.drawable.pastlink_drawable));
                    binding.txtGenerate.setTextColor(getResources().getColor(R.color.creame_light));
                    binding.generateIcon.setColorFilter(ContextCompat.getColor(context, R.color.creame_light));


                } else {
                    binding.generate.setBackground(getResources().getDrawable(R.drawable.pastcolor_drawable));
                    binding.txtGenerate.setTextColor(getResources().getColor(R.color.white));
                    binding.generateIcon.setColorFilter(ContextCompat.getColor(context, R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (utils.getStringValue(Utils.Version) != null && !Objects.equals(utils.getStringValue(Utils.Version), info.versionName)) {
                binding.updateNow.setVisibility(View.VISIBLE);
                binding.updateNow.setOnClickListener(this);
            } else {
                binding.updateNow.setVisibility(View.GONE);
            }
        } catch (PackageManager.NameNotFoundException e) {
            binding.updateNow.setVisibility(View.GONE);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.generate) {
            //generate the url
            videoUrls.clear();
            binding.llAllPreview.setVisibility(View.GONE);
            binding.llDownloder.setVisibility(View.GONE);

            url = binding.pastlink.getText().toString();

            if (Utils.isNetworkAvailable(this)) {

                if (url.contains("threads.net")) {
                    Log.d("downloadflow", "main pasted url :" + url);

                    url = URLConverter.convertURL(url);
                    Log.d("downloadflow", "url after URL Converter:" + url);

                    // load data
                    loadData();
                } else {

                    Toast.makeText(activity, "Enter invalid link", Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }

        } else if (v == binding.menu) {
            startActivity(new Intent(context, SettingActivity.class));
        } else if (v == binding.imageViewDownloads) {
            startActivityEs(new Intent(context, GalleryActivity.class));
        } else if (v == binding.threads) {
            Uri uri = Uri.parse("https://www.threads.net/");
            Intent instagram = new Intent(Intent.ACTION_VIEW, uri);
            instagram.setPackage("com.instagram.barcelona");
            try {
                startActivity(instagram);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(MainActivity.this, "App Not Found", Toast.LENGTH_SHORT).show();
            }
        } else if (v == binding.llDownloder) {
            if (videoUrls != null && !videoUrls.isEmpty()) {
                dialogDownload.show();
                for (VideoUrl url : videoUrls) {
                    downloadFile(url.getDownloadUrl(), Utils.getStoreVideoExternalStorage(context, Utils.DOWNLOAD_DIRECTORY), () -> {

                        currentVideo++;
                        if (currentVideo == videoUrls.size()) {
                            AdManager.adCounter++;
                            AdManager.showInterAdDownload(MainActivity.this, utils.getStringValue(Utils.interstitialAdId));
                            Toast.makeText(MainActivity.this, "Download Complete", Toast.LENGTH_SHORT).show();
                            dialogDownload.dismiss();
                        } else {
                            dialogDownload.show();
                        }

                    });

                }


            } else {
                Toast.makeText(activity, "Link Not Valid", Toast.LENGTH_SHORT).show();
            }
        } else if (v == binding.paste) {
            String text = Utils.handleLinkPasting(context);
            if (text != null && text.contains("threads.net")) {
                binding.pastlink.setText("" + Utils.handleLinkPasting(context));
            } else {
                Toast.makeText(activity, "No url Found", Toast.LENGTH_SHORT).show();
            }
        } else if (v == binding.updateNow) {

            UpdateApp();
        }

    }

    @Override
    public void onBackPressed() {
        if (dialogExit != null) {
            dialogExit.show();
        }
    }

    public void setDownloadDialog() {
        dialogDownload = new Dialog(this, R.style.MyAlertDialog);

        this.dialogDownload.requestWindowFeature(1);
        dialogDownload.setContentView(R.layout.dialog_download);
        dialogDownload.setCanceledOnTouchOutside(false);
        dialogDownload.setCancelable(false);

        progressBar = dialogDownload.findViewById(R.id.progress_download_video);

        progressExport = dialogDownload.findViewById(R.id.progressExported);

        progressBar.setProgress(0);

        ImageView cancel = dialogDownload.findViewById(R.id.ivClose);

        cancel.setOnClickListener(view -> {
            try {

                if (!isFinishing()) {
                    dialogDownload.dismiss();
                }
                PRDownloader.cancel(fileDownloadingId);

            } catch (Exception e) {

                e.printStackTrace();

            }
        });
    }

    private void exitDialog() {

        dialogExit = new Dialog(this);
        this.dialogExit.requestWindowFeature(1);
        dialogExit.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogExit.setContentView(R.layout.exit_dialog);
        dialogExit.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialogExit.setCanceledOnTouchOutside(false);
        dialogExit.setCancelable(false);

        LinearLayout yes = dialogExit.findViewById(R.id.btn_yes);
        LinearLayout no = dialogExit.findViewById(R.id.btn_no);

        yes.setOnClickListener(view12 -> finish());

        no.setOnClickListener(view12 -> {

            if (dialogExit.isShowing())
                dialogExit.dismiss();

        });

    }

    public void downloadFile(String url, String outPutPath, OnDownloadDone onDownloadDone) {


        String filePath = new File(outPutPath, System.currentTimeMillis() + "." + Utils.getExtension(url)).getAbsolutePath();

        fileDownloadingId = PRDownloader.download(url, outPutPath, System.currentTimeMillis() + "." + Utils.getExtension(url)).setPriority(Priority.HIGH).build()
                .setOnProgressListener(progress -> {
                    int bytesWritten = (int) ((progress.currentBytes * 100) / progress.totalBytes);
                    if (bytesWritten != 100) {
                        progressBar.setProgress(bytesWritten);
                        progressExport.setText(bytesWritten + "%");
                    }


                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        MediaScannerConnection.scanFile(context, new String[]{filePath}, new String[]{null}, null);
                        dialogDownload.dismiss();
                        onDownloadDone.onDownloadDone();

                    }


                    @Override
                    public void onError(Error error) {
                        Toast.makeText(context, "Unable to Download template. Please try again later.", Toast.LENGTH_SHORT).show();
                        PRDownloader.cancel(fileDownloadingId);
                        dialogDownload.dismiss();

                    }


                });
    }

    private ActivityResultLauncher<String[]> mPermissionResult = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear = true;
                    List<String> blockPermissionCheck = new ArrayList<>();

                    for (String key : result.keySet()) {
                        Log.d("premissionsCheck", "For Loop key " + key+ "  result" + result.keySet());
                        if (android.os.Build.VERSION.SDK_INT >= 33 && !(result.get("android.permission.POST_NOTIFICATIONS"))) {
                            allPermissionClear = false;
                            blockPermissionCheck.add(Utils.getPermissionStatus(MainActivity.this, key));
                            Log.d("premissionsCheck", "keys" + key);
                        }
                    }

                    if (blockPermissionCheck.contains("blocked")) {
                        showPermissionSettingsDialog();
                    }

                }
            });


    private void showPermissionSettingsDialog() {

        dialogPermission = new Dialog(this);
        this.dialogPermission.requestWindowFeature(1);
        dialogPermission.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogPermission.setContentView(R.layout.exit_dialog);
        dialogPermission.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialogPermission.setCanceledOnTouchOutside(true);
        dialogPermission.setCancelable(true);

        LinearLayout yes = dialogPermission.findViewById(R.id.btn_yes);
        LinearLayout no = dialogPermission.findViewById(R.id.btn_no);

        TextView title = dialogPermission.findViewById(R.id.txterorr);
        TextView text = dialogPermission.findViewById(R.id.txt_free);

        TextView tyes = dialogPermission.findViewById(R.id.tv_yes);
        TextView tno = dialogPermission.findViewById(R.id.tv_ok);

        title.setText("Permissions Required!");
        text.setText("ThSaver requires certain permissions to function. Please grant the permissions from the app settings.");

        tyes.setText("Exit");
        tno.setText("Open Settings");

        yes.setOnClickListener(v -> {
            dialogPermission.dismiss();
        });

        no.setOnClickListener(v -> {
            openAppSettings();
            dialogPermission.dismiss();

        });

        dialogPermission.show();

    }


    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    private void checkPermission() {

        PermissionUtils permissionUtils = new PermissionUtils(this, mPermissionResult);

        if (!permissionUtils.isPermissiongGranted()) {
            permissionUtils.requestPermissions();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adViewAdmob != null) {
            adViewAdmob.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adViewAdmob != null) {
            adViewAdmob.destroy();
        }
    }

    public void UpdateApp() {
        try {
            Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo, AppUpdateType.FLEXIBLE, activity, 101);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }).addOnFailureListener(Throwable::printStackTrace);

        } catch (Exception e) {
            e.printStackTrace();
        }

        appUpdateManager.registerListener(listener);
    }

    InstallStateUpdatedListener listener = installState -> {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            popUp();
        }

    };

    private void popUp() {
        Snackbar snackbar = Snackbar.make(
                findViewById(android.R.id.content), "App Update Almost Done", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(
                "Reload", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appUpdateManager.completeUpdate();
                    }
                });
        snackbar.setTextColor(Color.parseColor("#FF0000"));
        snackbar.show();
    }

    @Override
    protected void onPause() {
        if (adViewAdmob != null) {
            adViewAdmob.pause();
        }
        super.onPause();

    }

    public void startActivityEs(Intent intent) {
        AdManager.adCounter++;
        AdManager.showInterAd(MainActivity.this, utils.getStringValue(Utils.interstitialAdId), intent, 0);
    }


    public final void bannerAdmob() {

        if (utils.getBooleanValue(Utils.Adshow)) {
            if (utils.getStringValue(Utils.bannerAdFullId) != null) {
                String bannerFullId = utils.getStringValue(Utils.bannerAdFullId);

                adViewAdmob = new AdView(MainActivity.this);

                adViewAdmob.setAdSize(com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE);
                adViewAdmob.setAdUnitId(bannerFullId);
                AdRequest adRequest = new AdRequest.Builder().build();
                adViewAdmob.loadAd(adRequest);
                binding.linerAdFull.setVisibility(View.VISIBLE);
                binding.linerAdFull.addView(adViewAdmob);
                adViewAdmob.setAdListener(new com.google.android.gms.ads.AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                    }

                    @Override
                    public void onAdFailedToLoad(com.google.android.gms.ads.LoadAdError adError) {
                        super.onAdFailedToLoad(adError);

                    }

                    @Override
                    public void onAdClosed() {

                        super.onAdClosed();
                    }
                });
            }
        } else {
            Log.d("TAG", "adShow false");
            binding.linerAdFull.setVisibility(View.GONE);
        }


    }
}
