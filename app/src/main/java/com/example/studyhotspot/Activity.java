package com.example.studyhotspot;

import java.util.ArrayList;

public class Activity {
    private String activityID, name, status, startTime, endTime, creator;
    private ArrayList<String> userID;

    public Activity(){
        activityID = "ABC123";
        name = "Study @ Starbucks";
        userID = new ArrayList<String>();
        status = "ongoing";
        startTime = "1100";
        endTime = "1200";
        creator = "Me";

    }

    public Activity(String name, String activityID, String status, String startTime, String endTime, String creator){
        userID = new ArrayList<String>();
        this.name = name;
        this.activityID = activityID;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.creator = creator;
    }



    public String getname(){
        return this.name;
    }
    public String getActivityID() {return this.activityID;}
    public String getStatus(){ return this.status;}
    public String getStartTime(){ return this.startTime;}
    public String getEndTime() {return this.endTime;}
    public String getCreator() {return this.creator;}
}
