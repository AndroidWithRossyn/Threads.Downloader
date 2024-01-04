package com.banrossyn.storydownloader.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.banrossyn.storydownloader.VideoPlayActivity;
import com.banrossyn.storydownloader.model.VideoUrl;
import com.bumptech.glide.Glide;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.Priority;
import com.banrossyn.storydownloader.R;
import com.banrossyn.storydownloader.util.Utils;

import java.io.File;
import java.util.List;


public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {
    private final Activity context;
    private List<VideoUrl> imageArrayList;
    private LayoutInflater layoutInflater;
    private int fileDownloadingId = 0;
    private Dialog dialogDownload;
    private ProgressBar progressBar;
    private TextView progressExport;


    public VideoListAdapter(Activity context, List<VideoUrl> files) {
        this.context = context;
        this.imageArrayList = files;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());

            setDownloadDialog();


        }

        View view = layoutInflater.inflate(R.layout.items_view, viewGroup, false);
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        String downloadurl = imageArrayList.get(i).getDownloadUrl();


        try {

            if (imageArrayList.get(i).isIs_video()) {


                viewHolder.ivPlay.setVisibility(View.VISIBLE);

            } else {
                viewHolder.ivPlay.setVisibility(View.GONE);

            }

            viewHolder.ivPlay.setOnClickListener(view -> {

                Intent intent = new Intent(context, VideoPlayActivity.class);
                intent.putExtra("PathVideo", imageArrayList.get(i).getDownloadUrl());
                context.startActivity(intent);

            });


            Glide.with(context)
                    .load(downloadurl)
                    .into(viewHolder.ivImage);


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        viewHolder.ivdownload.setOnClickListener(view -> downloadFile(imageArrayList.get(i).getDownloadUrl(), Utils.getStoreVideoExternalStorage(context, Utils.DOWNLOAD_DIRECTORY)));


    }

    public void setDownloadDialog() {
        dialogDownload = new Dialog(context, R.style.MyAlertDialog);

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
                dialogDownload.dismiss();
                PRDownloader.cancel(fileDownloadingId);

            } catch (Exception e) {

                e.printStackTrace();

            }
        });
    }


    public void downloadFile(String url, String outPutPath) {


        dialogDownload.show();
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
                        Toast.makeText(context, "Download Complete", Toast.LENGTH_SHORT).show();

                    }


                    @Override
                    public void onError(Error error) {

                        Toast.makeText(context, "Unable to Download. Please try again later.", Toast.LENGTH_SHORT).show();

                        PRDownloader.cancel(fileDownloadingId);
                        dialogDownload.dismiss();

                    }


                });
    }


    @Override
    public int getItemCount() {
        return imageArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout rlMain;
        private final ImageView ivImage;
        private final ImageView ivPlay;

        private final ImageView ivdownload;


        public ViewHolder(View view) {
            super(view);
            rlMain = view.findViewById(R.id.rl_main);
            ivImage = view.findViewById(R.id.iv_image);
            ivPlay = view.findViewById(R.id.iv_play);
            ivdownload = view.findViewById(R.id.iv_download);


        }
    }
}