package ru.nwts.wherewe.model;

import java.io.Serializable;

/**
 * Created by пользователь on 01.02.2017.
 */

public class FbaseModel implements Serializable {

    private double lattitude;
    private double longtitude;
    private double speed;
    private double moved;
    private double state;
    private double mode;
    private int rights;
    private String fbase_path;
    private long dateTime;
    private String email;
    private String part_email;


    public FbaseModel() {
    }

    public FbaseModel(double flattitude, double flongtitude, double speed, double moved, double state,
                      double mode, int rights, String fbase_path, String email, String part_email, long dateTime) {

        this.lattitude = flattitude;
        this.longtitude = flongtitude;
        this.speed = speed;
        this.moved = moved;
        this.state = state;
        this.mode = mode;
        this.rights = rights;
        if (fbase_path == null) {
            fbase_path = "";
        }
        this.fbase_path = fbase_path;
        if (email == null){
            email ="";
        }
        this.email = email;
        if (part_email == null){
            part_email = "";
        }
        this.part_email = part_email;
        this.dateTime = dateTime;
    }

    public double getLattitude() {
        return lattitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public double getSpeed() {
        return speed;
    }

    public double getMoved() {
        return moved;
    }

    public double getState() {
        return state;
    }

    public double getMode() {
        return mode;
    }

    public int getRights() {
        return rights;
    }

    public String getFbase_path() {
        return fbase_path;
    }

    public String getEmail() {
        return email;
    }

    public String getPart_email() {
        return part_email;
    }
}
