package ru.nwts.wherewe.model;

import java.io.Serializable;

/**
 * Created by пользователь on 01.02.2017.
 */

public class FbaseModel implements Serializable {

    private long flattitude;
    private long flongtitude;
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
                      double mode, int rights, String fbase_path, String fbase_old, String email, String part_email) {
        this.flattitude = flattitude;
        this.flongtitude = flongtitude;
        this.speed = speed;
        this.moved = moved;
        this.state = state;
        this.mode = mode;
        this.rights = rights;
        this.fbase_path = fbase_path;
        this.fbase_old = fbase_old;
        this.email = email;
        this.part_email = part_email;
    }

    public long getLattitude() {
        return flattitude;
    }

    public long getLongtitude() {
        return flongtitude;
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

    public String getFbase_old() {
        return fbase_old;
    }

    public String getEmail() {
        return email;
    }

    public String getPart_email() {
        return part_email;
    }
}
