package com.example.lostfound;

import android.util.Log;

public class LostPostInformation {

    public String title, user, description, phoneNum, userId;

    // Default Constructor
    public LostPostInformation(){

    }

    // Overloaded Constructor
    public LostPostInformation(String user, String title, String description, String phoneNum, String userId) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.phoneNum = phoneNum;
        this.userId = userId;
    }

    public String getUser(){
        return this.user;
    }

    public String getTitle(){
        return this.title;
    }

    public String getDescription(){
        return this.description;
    }

    public String getPhoneNum() {
        return this.phoneNum;
    }

    public String getUserId() {
        return this.userId;
    }

    public void print(){
        Log.d("User: ",user);
        Log.d("Title: ",title);
        Log.d("Description: ",description);
        Log.d("Phone Number: ",phoneNum);
        Log.d("User ID: ",userId);
    }
}
