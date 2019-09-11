package com.example.chatapp.api_service;

import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by S.Bagherpour on 1/30/2018.
 */

public class RetroAdapter {
    private static RetroInterface mTService;
    private static Retrofit mRetrofitClient;
    private static String BASE_URL = "http://192.168.1.133:8080";



    public static RetroInterface getApiService() {
        /* ConnectionSpec.MODERN_TLS is the default value */
        List tlsSpecs = Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT);
        /* providing backwards-compatibility for API lower than Lollipop: */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            tlsSpecs = Arrays.asList(ConnectionSpec.COMPATIBLE_TLS);
        }


        Interceptor interceptor = chain -> {
            Request newRequest = chain.request().newBuilder().addHeader("User-Agent", "Retrofit-Sample-App").build();
            return chain.proceed(newRequest);
        };



        OkHttpClient client = new OkHttpClient.Builder()
                .connectionSpecs(tlsSpecs



                ).
                        connectTimeout(19, TimeUnit.SECONDS).
                        readTimeout(19, TimeUnit.SECONDS).build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        mRetrofitClient = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mTService = mRetrofitClient.create(RetroInterface.class);
        return mTService;
    }



    public Retrofit getRetrofitClient() {
        return mRetrofitClient;
    }
}
