package com.banrossyn.storydownloader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayActivity extends AppCompatActivity {

    private ImageView imClose;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_player);
        VideoView videoView = findViewById(R.id.videoView);
        imClose = findViewById(R.id.im_close);

        Intent intent = getIntent();
        String videoPath = intent.getStringExtra("PathVideo");


        // Initialize the video player...
        imClose.setOnClickListener(v -> onBackPressed());


        try {
            MediaController mediaController = new MediaController(VideoPlayActivity.this);
            mediaController.setAnchorView(videoView);
            Uri video = Uri.parse(videoPath);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
            videoView.start();
            videoView.setOnPreparedListener(mp -> {

            });
            videoView.setOnCompletionListener(mediaPlayer -> {

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
