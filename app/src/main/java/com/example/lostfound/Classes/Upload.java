package com.example.lostfound.Classes;

// Upload class to hold image url

public class Upload {

    private String imageUrl;

    public Upload() {

    }

    public Upload(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}