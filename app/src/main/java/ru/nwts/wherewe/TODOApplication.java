package ru.nwts.wherewe;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.greendao.database.Database;

import io.fabric.sdk.android.Fabric;
import ru.nwts.wherewe.database.DBHelper;
import ru.nwts.wherewe.model.DaoMaster;
import ru.nwts.wherewe.model.DaoSession;

/**
 * Created by Надя on 27.12.2016.
 */

public class TODOApplication extends Application {

    public final String TEST_STRING = "Test Application";

    private static TODOApplication instance;
    private static DaoSession sDaoSession;

    public DBHelper dbHelper;

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    public static TODOApplication getInstance() {
        return instance;
    }

    public static FirebaseAuth getFireBaseAuth(){
         return FirebaseAuth.getInstance();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        dbHelper = new DBHelper(this);
        Fabric.with(this, new Crashlytics());

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "whw_db");
        Database db = helper.getWritableDb();
        sDaoSession = new DaoMaster(db).newSession();
        //**************************************************************************
        // stetho
        //**************************************************************************
        // Create an InitializerBuilder
        Stetho.InitializerBuilder initializerBuilder =
                Stetho.newInitializerBuilder(this);
        // Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(
                Stetho.defaultInspectorModulesProvider(this)
        );

        // Enable command line interface
        initializerBuilder.enableDumpapp(
                Stetho.defaultDumperPluginsProvider(this)
        );

        // Use the InitializerBuilder to generate an Initializer
        Stetho.Initializer initializer = initializerBuilder.build();

        // Initialize Stetho with the Initializer
        Stetho.initialize(initializer);
        //**************************************************************************
        // stetho
        //**************************************************************************

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

    public static DaoSession getDaoSession() {
        return sDaoSession;
    }

}
