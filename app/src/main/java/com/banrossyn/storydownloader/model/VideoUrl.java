package com.banrossyn.storydownloader.model;

import com.google.gson.annotations.SerializedName;

public class VideoUrl {

    boolean is_video;
    @SerializedName("download_url")
    private String downloadUrl;

    public VideoUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public VideoUrl() {
    }

    public boolean isIs_video() {
        return is_video;
    }

    public void setIs_video(boolean is_video) {
        this.is_video = is_video;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

}