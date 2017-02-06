package ru.nwts.wherewe.model;

import java.io.Serializable;

/**
 * Created by пользователь on 03.02.2017.
 */

public class ModelCheck implements Serializable {
    private double latitude;
    private double longtitude;
    private long dateTime;
    private int state;
    private int mode;
    private int rights;
    private long speed;
    private int moved;
    private String email;

    public int getState() {
        return state;
    }

    public int getMode() {
        return mode;
    }

    public int getRights() {
        return rights;
    }

    public long getSpeed() {
        return speed;
    }

    public int getMoved() {
        return moved;
    }

    public String getEmail() {
        return email;
    }

    public String getPart_email() {
        return part_email;
    }

    public ModelCheck(double latitude, double longtitude, long dateTime, int state, int mode, int rights, long speed, int moved, String email, String part_email) {
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.dateTime = dateTime;
        this.state = state;
        this.mode = mode;
        this.rights = rights;
        this.speed = speed;
        this.moved = moved;
        this.email = email;
        this.part_email = part_email;
    }

    private String part_email;


    public ModelCheck(double latitude, double longtitude, long dateTime) {
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.dateTime = dateTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public long getDateTime() {
        return dateTime;
    }
}
