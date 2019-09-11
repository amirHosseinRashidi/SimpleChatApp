package com.example.chatapp.api_service;


import com.example.chatapp.Message;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.POST;

public interface RetroInterface {
    @POST("read-messages")
    Call<ArrayList<Message>> getMessagesList();
}