package com.example.studyhotspot.Entity;

import java.util.ArrayList;

public class User {

    private String fName;
    private String email;
    private String phone;
    private ArrayList<String> addedfriends;
    private ArrayList<String> addingfriends;
    private ArrayList<String> awaitingfriends;
    private ArrayList<String> awaitingfriendsname;

    public User(String fName, String email, String phone, ArrayList<String> addedfriends, ArrayList<String> addingfriends, ArrayList<String> awaitingfriends, ArrayList<String> awaitingfriendsname){
        this.fName = fName;
        this.email = email;
        this.phone = phone;
        this.addedfriends = addedfriends;
        this.addingfriends = addingfriends;
        this.awaitingfriends = awaitingfriends;
        this.awaitingfriendsname = awaitingfriendsname;
    }

    public String getfName(){
        return fName;
    }

    public String getEmail(){
        return email;
    }

    public String getPhone(){
        return phone;
    }

    public ArrayList<String> getAddedfriends(){
        return addedfriends;
    }

    public ArrayList<String> getAddingfriends(){
        return addingfriends;
    }

    public ArrayList<String> getAwaitingfriends(){
        return awaitingfriends;
    }

    public ArrayList<String> getAwaitingfriendsname(){
        return awaitingfriendsname;
    }
}