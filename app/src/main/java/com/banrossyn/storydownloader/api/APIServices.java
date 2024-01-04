package com.banrossyn.storydownloader.api;

import com.banrossyn.storydownloader.model.ThreadsModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIServices {

    @GET("media")
    Call<ThreadsModel> getThreadsData(@Query("url") String url);

}