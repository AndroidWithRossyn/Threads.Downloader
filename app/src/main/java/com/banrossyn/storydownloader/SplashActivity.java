package com.banrossyn.storydownloader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.banrossyn.storydownloader.util.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class SplashActivity extends AppCompatActivity {

    Context context;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = this;
        utils = new Utils(SplashActivity.this);

        initRemoteConfig();

        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(SplashActivity.this, "No Network Connection.....Try Again", Toast.LENGTH_LONG).show();
        }
        AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
        alpha.setDuration(1000);

        ImageView splaceImg = findViewById(R.id.logo);
        TextView tv = findViewById(R.id.developerName);
        splaceImg.startAnimation(alpha);
        tv.startAnimation(alpha);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        }, 2000);

    }

    public void initRemoteConfig() {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Boolean> task) {
                if (task.isSuccessful()) {
                    boolean updated = task.getResult();
                    Log.d("TAG", "Config params updated: " + updated);

                    utils.saveBooleanValue(Utils.Adshow, mFirebaseRemoteConfig.getBoolean(Utils.Adshow));
                    utils.saveBooleanValue(Utils.showInterstitial, mFirebaseRemoteConfig.getBoolean(Utils.showInterstitial));
                    utils.saveStringValue(Utils.interstitialAdId, mFirebaseRemoteConfig.getString(Utils.interstitialAdId));
                    utils.saveStringValue(Utils.bannerAdFullId, mFirebaseRemoteConfig.getString(Utils.bannerAdFullId));
                    utils.saveStringValue(Utils.bannerAdId, mFirebaseRemoteConfig.getString(Utils.bannerAdId));
                    utils.saveStringValue(Utils.Version, mFirebaseRemoteConfig.getString(Utils.Version));
                    utils.saveStringValue(Utils.PrivacyPolicy, mFirebaseRemoteConfig.getString(Utils.PrivacyPolicy));
                    utils.saveStringValue(Utils.HowToUse, mFirebaseRemoteConfig.getString(Utils.HowToUse));

//                    Log.d("TAG", "onCreate: isAdShow " + utils.getBooleanValue(Utils.Adshow));
//                    Log.d("TAG", "onCreate: showInterstitial " + utils.getBooleanValue(Utils.showInterstitial));
//                    Log.d("TAG", "onCreate: banner_full " + utils.getStringValue(Utils.bannerAdFullId));
//                    Log.d("TAG", "onCreate: banner_key " + utils.getStringValue(Utils.bannerAdId));
//                    Log.d("TAG", "onCreate: interstitial_ad_key" + utils.getStringValue(Utils.interstitialAdId));


                } else {
                    Log.d("TAG", "onComplete: fetch failed");
                }
            }

        });
        mFirebaseRemoteConfig.fetchAndActivate().addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onComplete: fetch failed");
            }
        });
    }

}
