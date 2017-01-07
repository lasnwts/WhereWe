package ru.nwts.wherewe.model;

import java.io.Serializable;

/**
 * Created by Надя on 27.12.2016.
 */

public class Model implements Serializable {

    private long id;
    private String name;
    private int state;
    private int mode;
    private int rights;
    private long speed;
    private int moved;
    private long track_date;
    private long longtitude;
    private long lattitude;
    private String fbase_path;
    private String fbase_old;
    private int track_count;
    private int track_count_allowed;
    private String key;
    private String key_old;
    private int contact_id;
    private String email;
    private String part_email;

    public Model() {}

    public Model(long id, String name, int state, int mode, int rights, long speed,
                 int moved, long track_date, long longtitude, long lattitude, String fbase_path,
                 String fbase_old, int track_count, int track_count_allowed, String key,
                 String key_old, int contact_id, String email, String part_email) {
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
        this.fbase_path = fbase_path;
        this.fbase_old = fbase_old;
        this.track_count = track_count;
        this.track_count_allowed = track_count_allowed;
        this.key = key;
        this.key_old = key_old;
        this.contact_id = contact_id;
        this.email = email;
        this.part_email = part_email;
    }

    public long getId() {
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

    public long getLongtitude() {
        return longtitude;
    }

    public long getLattitude() {
        return lattitude;
    }

    public String getFbase_path() {
        return fbase_path;
    }

    public String getFbase_old() {
        return fbase_old;
    }

    public int getTrack_count() {
        return track_count;
    }

    public int getTrack_count_allowed() {
        return track_count_allowed;
    }

    public String getKey() {
        return key;
    }

    public String getKey_old() {
        return key_old;
    }

    public int getContact_id() {
        return contact_id;
    }

    public String getEmail() {
        return email;
    }

    public String getPart_email() {
        return part_email;
    }
}
