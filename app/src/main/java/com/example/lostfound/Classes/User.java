package com.example.lostfound.Classes;

// User class to hold user's information e.g. name, school, id, email. phone number

public class User {

    private String name, school, id, email, phoneNum;

    public User(){

    }

    public User(String name, String school, String id, String email, String phoneNum) {
        this.name = name;
        this.school = school;
        this.id = id;
        this.email = email;
        this.phoneNum = phoneNum;
    }

    public String getName(){
        return this.name;
    }

    public String getSchool(){
        return this.school;
    }

    public String getId(){
        return this.id;
    }

    public String getEmail(){
        return this.email;
    }

    public String getPhoneNum(){
        return this.phoneNum;
    }
}
