package com.example.lostfound;

public class SecurityQuestions {

    private String name, school, id;

    public SecurityQuestions(){

    }

    public SecurityQuestions(String name, String school, String id){
        this.name = name;
        this.school = school;
        this.id = id;
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
}
