package ru.nwts.wherewe.model;

import java.io.Serializable;

/**
 * Created by пользователь on 03.02.2017.
 */

public class ModelCheck implements Serializable {
    private double latitude;
    private double longtitude;
    private long dateTime;

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
