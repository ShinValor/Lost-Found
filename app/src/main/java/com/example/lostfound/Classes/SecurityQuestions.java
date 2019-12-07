package com.example.lostfound.Classes;

// Security class to hold security questions e.g. name, school, id ...

public class SecurityQuestions {

    private String name, school, id, postId;

    public SecurityQuestions(){

    }

    public SecurityQuestions(String name, String school, String id, String postId){
        this.name = name;
        this.school = school;
        this.id = id;
        this.postId = postId;
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

    public String getPostId(){
        return this.postId;
    }
}
