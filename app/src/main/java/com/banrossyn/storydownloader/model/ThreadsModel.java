package com.banrossyn.storydownloader.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ThreadsModel {
    @SerializedName("image_urls")
    private List<String> imageUrls;

    @SerializedName("video_urls")
    private List<VideoUrl> videoUrls;

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<VideoUrl> getVideoUrls() {
        return videoUrls;
    }

    public void setVideoUrls(List<VideoUrl> videoUrls) {
        this.videoUrls = videoUrls;
    }
}
