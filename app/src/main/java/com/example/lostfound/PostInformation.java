package com.example.lostfound;

import android.util.Log;

public class PostInformation {

    public String title, user, description, phoneNum, userId, postId;

    // Default Constructor
    public PostInformation(){

    }

    // Overloaded Constructor
    public PostInformation(String user, String title, String description, String phoneNum, String userId, String postId) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.phoneNum = phoneNum;
        this.userId = userId;
        this.postId = postId;
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

    public String getPostId() {
        return this.postId;
    }

    public void print(){
        Log.d("User: ",user);
        Log.d("Title: ",title);
        Log.d("Description: ",description);
        Log.d("Phone Number: ",phoneNum);
        Log.d("Post ID: ",postId);
    }
}

