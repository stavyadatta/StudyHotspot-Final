package com.example.studyhotspot;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.sql.Time;

public class Session implements Serializable{

    private String title;
    private String description;
    private String startdate;
    private String starttime;
    private String enddate;
    private String endtime;
    private int numOfParticipants;
    private boolean privateORpublic;

    public Session() {

    }

    public Session(String title, String description){
        this.title = title;
        this.description = description;
    }

    public Session(String title, String description, String startdate, String starttime, String enddate, String endtime, int numOfParticipants, boolean privateORpublic) {
        this.title = title;
        this.description = description;
        this.startdate = startdate;
        this.starttime = starttime;
        this.enddate = enddate;
        this.endtime = endtime;
        this.numOfParticipants = numOfParticipants;
        this.privateORpublic = privateORpublic;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStartTime() {return startdate;}

    public String getStarttime() {return starttime;}

    public String getEnddate() {return enddate;}

    public String getEndTime() {return endtime;}

    public int getNumOfParticipants() {return numOfParticipants;}

    public boolean getprivateORpublic() {return privateORpublic;}
}
