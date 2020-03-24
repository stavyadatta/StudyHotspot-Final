package com.example.studyhotspot;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Session implements Serializable{

    private String title;
    private String description;
    private Timestamp startDateTime;
    private Timestamp endDateTime;
    private int numOfParticipants;
    private boolean privateORpublic;

    public Session() {

    }

    public Session(String title, String description){
        this.title = title;
        this.description = description;
    }

    public Session(String title, String description, Timestamp startDateTime, Timestamp endDateTime , int numOfParticipants, boolean privateORpublic) {
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.numOfParticipants = numOfParticipants;
        this.privateORpublic = privateORpublic;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Timestamp getStartDateTime() {return startDateTime;}

    public Timestamp getEndDateTime() {return endDateTime;}

    public int getNumOfParticipants() {return numOfParticipants;}

    public boolean getprivateORpublic() {return privateORpublic;}
}
