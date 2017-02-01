package ru.nwts.wherewe.model;

import java.io.Serializable;

/**
 * Created by пользователь on 31.01.2017.
 */

public class TestModel implements Serializable {
    public String getEmail() {
        return email;
    }

    public long getAttitude() {
        return attitude;
    }

    public TestModel(String test, String email, long attitude) {
        this.test = test;
        this.email = email;
        this.attitude = attitude;
    }

    private String test;
    private String email;
    private long attitude;

    public TestModel() {
    }

    public TestModel(String test){this.test = test;};

    public String getTest(){
        return this.test;
    }


}
