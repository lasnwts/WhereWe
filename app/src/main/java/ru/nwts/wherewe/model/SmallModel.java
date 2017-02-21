package ru.nwts.wherewe.model;

import java.io.Serializable;

/**
 * Created by Надя on 06.01.2017.
 */

public class SmallModel implements Serializable {

    private int id;
    private String name;
    private int state;
    private int mode;
    private int rights;
    private long speed;
    private int moved;
    private long track_date;
    private double longtitude;
    private double lattitude;
    private int contact_id;
    private String email;

    public SmallModel() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public SmallModel(int id, String name, int state, int mode, int rights, long speed, int moved, long track_date, double longtitude, double lattitude, int contact_id, String email) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.mode = mode;
        this.rights = rights;
        this.speed = speed;
        this.moved = moved;
        this.track_date = track_date;
        this.longtitude = longtitude;
        this.lattitude = lattitude;
        this.contact_id = contact_id;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

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

    public long getTrack_date() {
        return track_date;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public double getLattitude() {
        return lattitude;
    }

    public int getContact_id() {
        return contact_id;
    }

    public String getEmail() {
        return email;
    }
}
