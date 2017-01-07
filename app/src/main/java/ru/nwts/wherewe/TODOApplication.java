package ru.nwts.wherewe;

import android.app.Application;
import android.content.Context;

import ru.nwts.wherewe.database.DBHelper;

/**
 * Created by Надя on 27.12.2016.
 */

public class TODOApplication extends Application {

    public final String TEST_STRING = "Test Application";

    private static TODOApplication instance;

    public DBHelper dbHelper;

    public static TODOApplication getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        dbHelper = new DBHelper(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public String getTest(){
        return TEST_STRING;
    }

    public Context getTODOApplication(){
        return getInstance();
    }


}
