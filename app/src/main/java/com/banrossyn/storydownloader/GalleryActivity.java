package com.banrossyn.storydownloader;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.banrossyn.storydownloader.databinding.ActivityGalleryBinding;
import com.banrossyn.storydownloader.fragments.SaveImageFragment;
import com.banrossyn.storydownloader.fragments.SavedVideoFragment;
import com.banrossyn.storydownloader.util.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    Activity context;

    ActivityGalleryBinding binding;
    private Utils utils;
    private com.google.android.gms.ads.AdView adViewAdmob;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;
        initUI();
        utils = new Utils(GalleryActivity.this);
        // on back button
        binding.imBack.setOnClickListener(v -> onBackPressed());
        if (Utils.isNetworkAvailable(this)) {
            bannerAdmob();
        }
    }


    private void initUI() {
        setupViewPager(binding.viewpager);
        binding.tabLayout.setupWithViewPager(binding.viewpager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new SaveImageFragment(), "Images");
        adapter.addFragment(new SavedVideoFragment(), "Videos");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
    }


    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    @Override
    protected void onDestroy() {
        utils = null;
        if (adViewAdmob != null) {
            adViewAdmob.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adViewAdmob != null) {
            adViewAdmob.resume();
        }
    }

    @Override
    protected void onPause() {
        if (adViewAdmob != null) {
            adViewAdmob.pause();
        }
        super.onPause();
    }


    public final void bannerAdmob() {
        if (utils.getBooleanValue(Utils.Adshow)) {
            if (utils.getStringValue(Utils.bannerAdFullId) != null) {
                String bannerFullId = utils.getStringValue(Utils.bannerAdFullId);

                adViewAdmob = new com.google.android.gms.ads.AdView(GalleryActivity.this);
                com.google.android.gms.ads.AdSize adSize = getAdSize();
                adViewAdmob.setAdSize(adSize);
                adViewAdmob.setAdUnitId(bannerFullId);
                AdRequest adRequest = new AdRequest.Builder().build();
                adViewAdmob.loadAd(adRequest);
                binding.linerAd.setVisibility(View.VISIBLE);
                binding.linerAd.addView(adViewAdmob);


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
        }

    }

    private AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = binding.linerAd.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }
}
