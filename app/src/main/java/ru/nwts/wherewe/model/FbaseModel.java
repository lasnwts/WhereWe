package ru.nwts.wherewe.model;

import java.io.Serializable;

/**
 * Created by пользователь on 01.02.2017.
 */

public class FbaseModel implements Serializable {

    private long lattitude;
    private long longtitude;
    private long speed;
    private double moved;
    private double state;
    private double mode;
    private int rights;
    private String fbase_path;
    private String fbase_old;
    private String email;
    private String part_email;

    public FbaseModel() {
    }

    public FbaseModel(long flattitude, long flongtitude, long speed, double moved, double state,
                      double mode, int rights, String fbase_path, String email, String part_email) {
        this.lattitude = flattitude;
        this.longtitude = flongtitude;
        this.speed = speed;
        this.moved = moved;
        this.state = state;
        this.mode = mode;
        this.rights = rights;
        this.fbase_path = fbase_path;
        this.email = email;
        this.part_email = part_email;
    }

    public long getLattitude() {
        return lattitude;
    }

    public long getLongtitude() {
        return longtitude;
    }

    public long getSpeed() {
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
