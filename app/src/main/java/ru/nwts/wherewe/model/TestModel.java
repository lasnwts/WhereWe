package ru.nwts.wherewe.model;

import java.io.Serializable;

/**
 * Created by пользователь on 31.01.2017.
 */

public class TestModel implements Serializable {
    private String test;

    public TestModel() {
    }

    public TestModel(String test){this.test = test;};

    public String getTest(){
        return this.test;
    }


}
