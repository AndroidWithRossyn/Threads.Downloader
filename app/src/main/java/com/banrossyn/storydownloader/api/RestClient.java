package com.banrossyn.storydownloader.api;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RestClient {
    private static final int fatchTime = 10;

    static {
        System.loadLibrary("native-lib");
    }

    public static final String BASE_URL1 = getBaseUrl1();
    private static Retrofit retrofit1;

    public static native String getBaseUrl1();

    static Gson gson = new GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
            .create();

    public static Retrofit getClient() {
//        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
//        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient build = new OkHttpClient.Builder()
//                .readTimeout(fatchTime, TimeUnit.SECONDS)
//                .connectTimeout(fatchTime, TimeUnit.SECONDS)
//                .writeTimeout(fatchTime, TimeUnit.SECONDS)
//                .addInterceptor(new Interceptor() {
//
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        Response response = null;
//                        try {
//                            response = chain.proceed(chain.request());
//
//                            if (response.code() == 200) {
//                                try {
//                                    return response.newBuilder()
//                                            .header("content-type", "application/json")
//                                            .body(ResponseBody.create(response.body().contentType(), new JSONObject(response.body().string()).toString())).build();
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            } else if (response.code() == 401) {
//                                Log.d("downloadflow", "Response code 401");
//                            }
//                        } catch (SocketTimeoutException e2) {
//                            e2.printStackTrace();
//                        }
//                        return response;
//                    }
//                }).addInterceptor(httpLoggingInterceptor).build();

        if (retrofit1 == null) {
            retrofit1 = new Retrofit.Builder()
                    .baseUrl(BASE_URL1)
                    .addConverterFactory(GsonConverterFactory.create(gson))
//                    .client(build)
                    .build();

            Log.d("downloadflow", "Retrofit base url: "+ BASE_URL1);
        }
        return retrofit1;
    }


}