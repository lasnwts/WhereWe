package ru.nwts.wherewe.model;

import java.io.Serializable;

/**
 * Created by пользователь on 03.02.2017.
 */

public class ListFireBasePath implements Serializable {
    private String email;
    private String partEmail;
    private String pathFireBase;
    private int id;
    private int badCount;

    public int getBadCount() {
        return badCount;
    }

    public int getId() {
        return id;
    }

    public ListFireBasePath(String email, String partEmail, String pathFireBase, int id, int badCount) {
        if (email == null){
            email ="";
        }
        this.email = email;
        if (partEmail == null){
            partEmail = "";
        }
        this.partEmail = partEmail;
        if (pathFireBase == null){
            pathFireBase = "";
        }
        this.pathFireBase = pathFireBase;
        this.id = id;
        this.badCount = badCount;
    }

    public String getEmail() {
        return email;
    }

    public String getPartEmail() {
        return partEmail;
    }

    public String getPathFireBase() {
        return pathFireBase;
    }
}
