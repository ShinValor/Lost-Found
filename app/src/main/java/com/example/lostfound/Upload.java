package com.example.lostfound;

public class Upload {

    private String mImageUrl;

    public Upload() {

    }

    public Upload(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}