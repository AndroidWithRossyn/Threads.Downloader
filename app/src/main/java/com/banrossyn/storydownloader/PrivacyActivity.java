package com.banrossyn.storydownloader;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.banrossyn.storydownloader.databinding.ActivityPrivacyBinding;
import com.banrossyn.storydownloader.util.Utils;

public class PrivacyActivity extends AppCompatActivity {

    ActivityPrivacyBinding binding;
    String type;
    String privacy;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        utils = new Utils(PrivacyActivity.this);

        if (getIntent().getExtras() != null) {
            type = getIntent().getExtras().getString("type");
            assert type != null;
            if(type.equals("PRIVACY_POLICY")){
                privacy = utils.getStringValue(Utils.PrivacyPolicy);
                binding.title.setText(getResources().getString(R.string.menu_privacy_policy));
            }else {
                privacy = utils.getStringValue(Utils.HowToUse);
                binding.title.setText(getResources().getString(R.string.how_to_use));
            }

            setData();
        }


    }

    private void setData() {


        binding.imBack.setOnClickListener(v -> onBackPressed());

        binding.wvPrivacy.getSettings().setJavaScriptEnabled(true);

        binding.wvPrivacy.loadUrl(privacy);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        binding.wvPrivacy.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                progressDialog.dismiss();


            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

    }
}