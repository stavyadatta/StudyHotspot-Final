package com.example.studyhotspot;

import java.io.Serializable;

public class Session implements Serializable{

    private String title;
    private String description;

    public Session() {

    }

    public Session(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

}
