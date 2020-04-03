package com.example.studyhotspot.Entity;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Each "Study Hospot" is in fact a place with access to Wireless@SG (WIFI).
 * For each of these places, we treat it as a Location object with a name and a set of coordinates.
 */
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
