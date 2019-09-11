package com.example.chatapp;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message implements Serializable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("user_name")
    @Expose
    private String userName = "";

    @SerializedName("image")
    @Expose
    private String image = "";
    @SerializedName("date")
    @Expose
    private long date = 0;


    private final static long serialVersionUID = -8121417444494344889L;

    public Message(String text, String userName, long date) {
        this.text = text;
        this.userName = userName;
        this.date = date;
    }

    public Message(String text, String userName, String image) {
        this.text = text;
        this.userName = userName;
        this.image = image;
        Date date = new Date();
        this.date = date.getTime();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}