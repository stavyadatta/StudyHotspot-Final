package com.example.studyhotspot.Entity;

import java.util.ArrayList;

/**
 * Each user object encapsulates his     fullname, email address, and phone number. These data are collected upon
 * registration. Note that it has been enforced during registration that the email address must be <b>unique</b>.
 *
 * <p>Additionally, we are also keeping track his network status through the following lists:</p>
 *<p>1. addedfriends: a list of emails belonging to user's current friends</p>
 * <p>2. addingfriends: a list of emails belonging to the users to which the current user has sent friend requests to</p>
 * <p>3 .awaitingfriends: a list of emails belonging to the users who have sent friend requests to the user</p>
 */
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