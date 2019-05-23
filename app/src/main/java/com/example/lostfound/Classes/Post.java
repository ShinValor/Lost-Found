package com.example.lostfound.Classes;

// Post class to hold a post information e.g. user, title, description ...

public class Post {

    private String title, user, description, phoneNum, userId, postId, email;

    public Post(){

    }

    public Post(String user, String title, String description, String phoneNum, String userId, String postId, String email) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.phoneNum = phoneNum;
        this.userId = userId;
        this.postId = postId;
        this.email = email;
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

    public String getEmail() {
        return this.email;
    }
}

