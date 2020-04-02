package com.example.studyhotspot.Entity;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Location {
    private String locName;
    private ArrayList<Double> coordinates;

    public Location(String locName, ArrayList<Double> coordinates){
        this.locName = locName;
        this.coordinates = coordinates;
    }

    public String getLocName(){
        return locName;
    }

    public ArrayList<Double> getCoordinates(){
        return coordinates;
    }
}
