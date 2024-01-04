package com.banrossyn.storydownloader;

import static com.banrossyn.storydownloader.util.Utils.setToast;
import static com.banrossyn.storydownloader.util.Utils.shareImage;
import static com.banrossyn.storydownloader.util.Utils.shareImageVideoOnWhatsapp;
import static com.banrossyn.storydownloader.util.Utils.shareVideo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.banrossyn.storydownloader.adapter.ShowImagesAdapter;
import com.banrossyn.storydownloader.model.DownloadedFileModel;
import com.banrossyn.storydownloader.util.Utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class FullViewActivity extends AppCompatActivity {
    public String saveFilePath;

    ShowImagesAdapter showImagesAdapter;
    String fileName = "";
    Activity context;
    ArrayList<DownloadedFileModel> fileArrayList;
    private FullViewActivity activity;
    private int position = 0;
    private int isStatus = 0;
    private ViewPager vpView;
    private ImageView imClose;
    private ImageView imShare;
    private ImageView imWhatsappShare;
    private Dialog dialogDelete;
    private ImageView imDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_view);

        initView();
        context = activity = this;

        //get the saved path location
        saveFilePath = Utils.getStoreVideoExternalStorage(activity, Utils.DOWNLOAD_DIRECTORY);

        deleteDialog();

        Intent extras = getIntent();
        if (extras.getExtras() != null) {
            fileArrayList = extras.getParcelableArrayListExtra("ImageDataFile");
            position = extras.getIntExtra("Position", 0);
        }


        fileName = fileArrayList.get(position).getName().substring(12);
        initViews();


    }

    public void initViews() {
        showImagesAdapter = new ShowImagesAdapter(this, fileArrayList, FullViewActivity.this);
        vpView.setAdapter(showImagesAdapter);
        vpView.setCurrentItem(position);
        vpView.setPageTransformer(true, new ZoomOutSlideTransformer());

        vpView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int arg, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                FullViewActivity.this.position = position;

                fileName = fileArrayList.get(FullViewActivity.this.position).getName().substring(12);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        imDelete.setImageResource(R.drawable.ic_delete_black_24dp);
        imDelete.setOnClickListener(view -> {

            dialogDelete.show();
        });



        imShare.setOnClickListener(view -> {
            if (fileArrayList.get(position).getName().contains(".mp4")) {
                Log.d("SSSSS", "onClick: " + fileArrayList.get(position) + "");
                shareVideo(activity, fileArrayList.get(position).getPath());
            } else {
                shareImage(activity, fileArrayList.get(position).getPath());
            }


        });
        imWhatsappShare.setOnClickListener(view -> shareImageVideoOnWhatsapp(activity, fileArrayList.get(position).getPath(), fileArrayList.get(position).getName().contains(".mp4")));

        imClose.setOnClickListener(v -> onBackPressed());
    }


    public void downloadFile() {
        final String path = fileArrayList.get(position).getPath();
        String filename = path.substring(path.lastIndexOf("/") + 1);
        final File file = new File(path);
        File destFile = new File(saveFilePath);


        try {

            FileUtils.copyFileToDirectory(file, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileNameChange = filename.substring(12);
        File newFile = new File(saveFilePath + fileNameChange);

        String contentType = "image/*";
        if (fileArrayList.get(position).getName().endsWith(".mp4")) {
            contentType = "video/*";
        } else {
            contentType = "image/*";
        }
        MediaScannerConnection.scanFile(activity, new String[]{newFile.getAbsolutePath()}, new String[]{contentType},
                new MediaScannerConnection.MediaScannerConnectionClient() {
                    public void onMediaScannerConnected() {
                    }

                    public void onScanCompleted(String path, Uri uri) {
                    }
                });

        File from = new File(saveFilePath, filename);
        File to = new File(saveFilePath, fileNameChange);
        from.renameTo(to);

        imDelete.setImageResource(R.drawable.ic_saved);
        Toast.makeText(activity, "Saved!", Toast.LENGTH_LONG).show();


    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
    }

    public void deleteFileAA(int position) {
        fileArrayList.remove(position);
        showImagesAdapter.notifyDataSetChanged();
        setToast(activity, "File Deleted.");
        if (fileArrayList.isEmpty()) {
            onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        vpView = findViewById(R.id.vp_view);
        imClose = findViewById(R.id.im_close);
        imShare = findViewById(R.id.imShare);
        imWhatsappShare = findViewById(R.id.imWhatsappShare);
        imDelete = findViewById(R.id.imDelete);
    }


    private void deleteDialog() {

        dialogDelete = new Dialog(this, R.style.MyAlertDialog);
        dialogDelete.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogDelete.setContentView(R.layout.delete_dialog);
        LinearLayout yes = dialogDelete.findViewById(R.id.btn_yes);
        LinearLayout no = dialogDelete.findViewById(R.id.btn_no);


        yes.setOnClickListener(view12 -> {
            boolean b = new File(fileArrayList.get(position).getPath()).delete();
            if (b) {
                deleteFileAA(position);
                dialogDelete.dismiss();
            }
        });

        no.setOnClickListener(view12 -> dialogDelete.dismiss());

    }


    public class ZoomOutSlideTransformer implements ViewPager.PageTransformer {

        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;


        @Override
        public void transformPage(@NonNull View view, float position) {
            if (position >= -1 || position <= 1) {
                // Modify the default slide transition to shrink the page as well
                final float height = view.getHeight();
                final float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                final float vertMargin = height * (1 - scaleFactor) / 2;
                final float horzMargin = view.getWidth() * (1 - scaleFactor) / 2;

                // Center vertically
                view.setPivotY(0.5f * height);

                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            }


        }
    }
}
