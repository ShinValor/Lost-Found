package com.example.lostfound.Classes;

import java.util.Date;

// Message class; holds data of one message

public class Message {

    public Message() {

    }

    public Message(String text, String user) {
        this.text = text;
        this.user = user;
        this.time = new Date().getTime();
    }

    public String getText() {
        return text;
    }

    public String getUser() {
        return user;
    }

    public long getTime() {
        return time;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setTime(long time) {
        this.time = time;
    }

    private String text;
    private String user;
    private long time;
}
