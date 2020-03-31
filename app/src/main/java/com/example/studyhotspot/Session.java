package com.example.studyhotspot;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Session implements Serializable{

    private String title;
    private String description;
    private String creatorName;
    private Timestamp startDateTime;
    private Timestamp endDateTime;
    private int numOfParticipants;
    private boolean privateORpublic;
    private Map<String, Boolean> participantStatus;

    public Session(String title, String description, String creatorName, Timestamp startDateTime, Timestamp endDateTime , Map<String,Boolean> participantStatus, boolean privateORpublic) {
        this.title = title;
        this.description = description;
        this.creatorName = creatorName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.participantStatus = participantStatus;
        this.numOfParticipants = participantStatus.size();
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

    public String getCreatorName() {
        return creatorName;
    }

    public Map<String, Boolean> getParticipantStatus() {
        return participantStatus;
    }


    public void updateParticipantStatus(String participantName, Boolean response){
        participantStatus.put(participantName, response);
        //write to DB
    }
}
