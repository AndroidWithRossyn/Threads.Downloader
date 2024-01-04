package com.banrossyn.storydownloader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


import com.banrossyn.storydownloader.databinding.ActivitySettingBinding;
import com.banrossyn.storydownloader.util.Utils;

public class SettingActivity extends AppCompatActivity {

    ActivitySettingBinding binding;
    Activity context;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        utils = new Utils(SettingActivity.this);

        binding.imBack.setOnClickListener(v -> finish());

        binding.rlDownloads.setOnClickListener(v -> startActivity(new Intent(context, GalleryActivity.class)));

        binding.rlRate.setOnClickListener(v -> {

            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));

            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
            }

        });

        binding.rlShare.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_txt) + context.getPackageName());
            startActivity(Intent.createChooser(intent, "Share App..."));

        });

        binding.rlMoreApp.setOnClickListener(v -> {

            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.developerUrl))));

            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));

            }
        });

        binding.rlFeedback.setOnClickListener(v -> {

            try {
                Intent intent = new Intent(Intent.ACTION_SENDTO).setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"banrossyn@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Query for " + getString(R.string.app_name));
                intent.putExtra(Intent.EXTRA_TEXT, "");
                this.startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(context, "Email not installed.", Toast.LENGTH_SHORT).show();
            }

        });

        binding.rlPrivacy.setOnClickListener(v -> {
            Intent intent = new Intent(context, PrivacyActivity.class);
            intent.putExtra("type", "PRIVACY_POLICY");
            startActivity(intent);
        });
        binding.rlHelp.setOnClickListener(v -> {
            Intent intent = new Intent(context, PrivacyActivity.class);
            intent.putExtra("type", "HOW_TO_USE");
            startActivity(intent);
        });

        binding.switchTheme.setChecked(utils.getBooleanValue(Utils.ThemeStatus));
        binding.switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                if (b) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    utils.saveBooleanValue(Utils.ThemeStatus, true);
                    binding.switchTheme.setChecked(true);

                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    utils.saveBooleanValue(Utils.ThemeStatus, false);
                    binding.switchTheme.setChecked(false);
                }
            }
        });
    }
}