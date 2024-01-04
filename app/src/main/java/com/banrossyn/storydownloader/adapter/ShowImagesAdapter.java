package com.banrossyn.storydownloader.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.banrossyn.storydownloader.VideoPlayActivity;
import com.banrossyn.storydownloader.model.DownloadedFileModel;
import com.bumptech.glide.Glide;
import com.banrossyn.storydownloader.FullViewActivity;
import com.banrossyn.storydownloader.R;
import com.jsibbold.zoomage.ZoomageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class ShowImagesAdapter extends PagerAdapter {
    FullViewActivity fullViewActivity;
    private Context context;
    private ArrayList<DownloadedFileModel> imageList;
    private LayoutInflater inflater;

    public ShowImagesAdapter(Context context, ArrayList<DownloadedFileModel> imageList1, FullViewActivity fullViewActivity) {
        this.context = context;
        this.imageList = imageList1;
        this.fullViewActivity = fullViewActivity;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
        container.removeView((View) object);
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.item_image_status_view, view, false);

        assert imageLayout != null;
        final ZoomageView imageView = imageLayout.findViewById(R.id.im_fullViewImage);
        final ImageView imVpPlay = imageLayout.findViewById(R.id.im_vpPlay);

        if (imageList != null) {

            if (imageList.get(position).getPath() != null) {

                Glide.with(context).load(imageList.get(position).getPath()).into(imageView);

                String extension = "";
                String fileName = imageList.get(position).getName();
                int extensionIndex = fileName.lastIndexOf(".");
                if (extensionIndex != -1) {
                    extension = fileName.substring(extensionIndex);
                }


                if (extension.equals(".mp4")) {

                    imVpPlay.setVisibility(View.VISIBLE);

                } else {

                    imVpPlay.setVisibility(View.GONE);

                }


                imVpPlay.setOnClickListener(v -> {

                    Intent intent = new Intent(context, VideoPlayActivity.class);
                    intent.putExtra("PathVideo", imageList.get(position).getPath());
                    intent.putExtra("name", imageList.get(position).getName());
                    context.startActivity(intent);


                });

            }

        }

        view.addView(imageLayout, 0);


        return imageLayout;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, @NotNull Object object) {
        return view.equals(object);
    }


}