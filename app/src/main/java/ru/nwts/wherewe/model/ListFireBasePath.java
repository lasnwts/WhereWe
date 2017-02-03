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

    public int getId() {
        return id;
    }

    public ListFireBasePath(String email, String partEmail, String pathFireBase, int id) {
        this.email = email;
        this.partEmail = partEmail;
        this.pathFireBase = pathFireBase;
        this.id = id;

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
