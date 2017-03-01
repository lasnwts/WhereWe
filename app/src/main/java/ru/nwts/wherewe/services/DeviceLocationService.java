package ru.nwts.wherewe.services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import ru.nwts.wherewe.TODOApplication;
import ru.nwts.wherewe.database.DBHelper;
import ru.nwts.wherewe.model.FbaseModel;
import ru.nwts.wherewe.model.ListFireBasePath;
import ru.nwts.wherewe.model.ModelCheck;
import ru.nwts.wherewe.receivers.BoardReceiverBattery;
import ru.nwts.wherewe.util.PreferenceHelper;

import static ru.nwts.wherewe.database.DBConstant.KEY_DATE;
import static ru.nwts.wherewe.database.DBConstant.KEY_ID;
import static ru.nwts.wherewe.database.DBConstant.KEY_LATTITUDE;
import static ru.nwts.wherewe.database.DBConstant.KEY_LONGTITUDE;

/**
 * Created by Надя on 10.01.2017.
 */

public class DeviceLocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //VARIABBLES MONITORING
    private long timeAccpetWait = 15 * 60 * 1000; //Время (15 мин) через которое необходимо обязательно записать в FB информацию
    private double acceptDistance = 10; //meters from transfer
    private String accprtDistanceString = "meter";
    /*
    "M" -   Miles
    "K" -   Kilometers
    "N" -   Nautical Miles
    "meter" -   meters
 */
    private final String ACTION_MAPRECEIVER = "ru.nwts.wherewe.map";
    private final int WAKEUP_MIN_2  =  2;
    private final int WAKEUP_MIN_5  =  5;
    private final int WAKEUP_MIN_10 =  10;
    private int wakeup_min = 5; //default time alarm

    //AlarmManager for Periodical starts
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    //For FireBaee Object
    private int jCount; //Counters

    //LOG
    public static final String TAG = "MyLogs";
    //SQLite
    public DBHelper dbHelper;
    ModelCheck modelCheck;
    FbaseModel fbaseModel;
    List<ListFireBasePath> listFireBasePaths;

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    //FireBase User
    FirebaseUser user;
    //firebase for write and read
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceId;

    PreferenceHelper preferenceHelper;
    private final String BATTERY_CHARGED = "BATTERY_CHARGED";
    private final String LOCATION_MODE = "LOCATION_MODE"; //1 - Base > 35%;  2- > 18; 3 - < 18%; 0 - not working..
    private final String KEY_ACTIVITY_READY = "PROF_ACTIVITY";
    private final String KEY_LOCATION_SERVICE_STARTED = "LOCATION_SERVICE";
    private final String KEY_EMAIL_SHARED_PREF = "EMAIL_SHARED_PREF";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location lastLocation;

    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Double myLatitude;
    private Double myLongitude;
    private float myAccuracy;
    private long mySpeed;
    private long myTime;
    private long timeWork;

    private BroadcastReceiver receiver;
    BoardReceiverBattery br;
    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "GoogleApiClient onConnected connected");
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectedSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }

    private void sendMessage(double Latitude, double Longtitude, int id, Long myTime){
        Intent intent = new Intent();
        intent.setAction(ACTION_MAPRECEIVER);
        intent.putExtra(KEY_ID, id);
        intent.putExtra(KEY_LATTITUDE,Latitude);
        intent.putExtra(KEY_LONGTITUDE,Longtitude);
        intent.putExtra(KEY_DATE, myTime);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
//        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        Log.d(TAG,"sendMessage");
        sendBroadcast(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();
        //myTime = location.getTime();
        preferenceHelper.putLong("Latitude",Double.doubleToRawLongBits(myLatitude));
        preferenceHelper.putLong("Longtitude",Double.doubleToRawLongBits(myLongitude));
        //preferenceHelper.putLong("Time",myTime);
        myTime = preferenceHelper.getLong("Time"); //get time when last write to firebase
        mySpeed = (long) location.getSpeed();
        //Call function check Write OR not in FireBase
        if (getCheckWriteOrNotInFirerBase(myLatitude, myLongitude, myTime)) {
            //Write to FireBase
            putWriteMyDataToFireBase(myLatitude, myLongitude, myTime, mySpeed);
            wakeup_min = WAKEUP_MIN_2;
        }
        //sendMessage to profile activity
        if (preferenceHelper.getBoolean(KEY_ACTIVITY_READY)) {
            sendMessage(myLatitude, myLongitude, 1, myTime);
        }

        Log.d(TAG, "KEY_ACTIVITY_READY : " + preferenceHelper.getBoolean(KEY_ACTIVITY_READY));
        //if ProfActivity not Start, may disabled service location
        if (!preferenceHelper.getBoolean(KEY_ACTIVITY_READY)) {
            if (myAccuracy == location.getAccuracy()) {
                Log.d(TAG, " Destroy on myAccuracy == location.getAccuracy().");
                this.stopSelf();
                //onDestroy();
            }
            if (System.currentTimeMillis() - timeWork > 60 * 1000) {
                Log.d(TAG, " Destroy on timeWork > 4 min.");
                this.stopSelf();
                //onDestroy();
            }
        }

        myAccuracy = location.getAccuracy();

        Log.d(TAG, "onLocationChanged");
        Log.d(TAG, "Latitude : " + String.valueOf(myLatitude));
        Log.d(TAG, "Longitude : " + String.valueOf(myLongitude));
        Log.d(TAG, "myAccuracy : " + String.valueOf(myAccuracy));
        Log.d(TAG, "myTime : " + String.valueOf(myTime) + " normal time write:" + dateformat.format(myTime));
        Log.d(TAG, "mySpeed : " + String.valueOf(mySpeed));
        Log.d(TAG, " " + BATTERY_CHARGED + preferenceHelper.getInt(BATTERY_CHARGED) + " " + LOCATION_MODE + ":" + preferenceHelper.getInt(LOCATION_MODE));
    }

    private void putWriteMyDataToFireBase(Double myLatitude, Double myLongitude, long myTime, double mySpeed) {
        //get new object
        fbaseModel = new FbaseModel(myLatitude, myLongitude, mySpeed, 0, 0, 0, 0,
                databaseReference.child(user.getUid()).getKey().toString(), modelCheck.getEmail(), modelCheck.getPart_email(), myTime);
        //put myTime
        preferenceHelper.putLong("Time",System.currentTimeMillis());
        //Пишем в SQLite о себеreplaceALL
        dbHelper.dbUpdateMe(1, 0, 0, 0, mySpeed, 0, myTime, myLongitude, myLatitude, databaseReference.child(user.getUid()).getKey().toString());

        //check Allow OR no send you location Global
        if (!preferenceHelper.getBoolean("allowedSendLocation")){
            return;
        }

        //get all path from FireBase
        listFireBasePaths = dbHelper.getListFireBasePath();
        if (listFireBasePaths == null || listFireBasePaths.isEmpty()) {
            return;
        }
        if (listFireBasePaths.size() == 1) {
            ListFireBasePath listFireBaePath = listFireBasePaths.get(0);
            if (listFireBaePath.getId() == 0) {
                return;
            }
        }
        //Пишем в FB
        for (jCount = 0; jCount < (listFireBasePaths.size()); jCount++) {
            //Запись в FireBase 03/02/2017
            Log.d(TAG, "listFireBasePaths:Jcount:FF" + jCount + " size:" + listFireBasePaths.size());
            Log.d(TAG, "listFireBasePaths:" + listFireBasePaths.get(jCount).getId() + " "
                    + listFireBasePaths.get(jCount).getEmail() + ": " + listFireBasePaths.get(jCount).getPartEmail() + "; "
                    + listFireBasePaths.get(jCount).getPathFireBase()
                    + "; " + getNormalizeString(listFireBasePaths.get(jCount).getEmail() + listFireBasePaths.get(jCount).getPartEmail())
                    + " listFireBasePaths.get(j).getPathFireBase() :" + listFireBasePaths.get(jCount).getPathFireBase()
                    + " listFireBasePaths.get(j).getBadCount() :" + listFireBasePaths.get(jCount).getBadCount());
            if (!listFireBasePaths.get(jCount).getPathFireBase().isEmpty()) {
                Log.d(TAG, "listFireBasePaths:Jcount:0" + jCount + " size:" + listFireBasePaths.size());
//                DatabaseReference databaseReferenceOnce = databaseReference.child(listFireBasePaths.get(jCount).getPathFireBase()).child(getNormalizeString(listFireBasePaths.get(jCount).getEmail() + listFireBasePaths.get(jCount).getPartEmail()));
                DatabaseReference databaseReferenceOnce = databaseReference.child(listFireBasePaths.get(jCount).getPathFireBase()).child(getNormalizeString(modelCheck.getEmail() + modelCheck.getPart_email()));
                databaseReferenceOnce.setValue(fbaseModel, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.d(TAG, "listFireBasePaths:" + databaseReference.getRef().toString());
                            //   dbHelper.dbUpdateBadCount(listFireBasePaths.get(jCount).getId(),listFireBasePaths.get(jCount).getBadCount()+1);
                            Log.d(TAG, "listFireBasePaths:onComplete:(databaseError != null)" + databaseError.getMessage());
                        } else {
                            Log.d(TAG, "listFireBasePaths:onComplete:successfully!");
                            Log.d(TAG, "listFireBasePaths:" + databaseReference.getRef().toString());
//                            if (listFireBasePaths.get(jCount).getBadCount() > 0){
//                                Log.d(TAG, "listFireBasePaths:Jcount:PP"+jCount + " size:"+listFireBasePaths.size());
//                                dbHelper.dbUpdateBadCount(listFireBasePaths.get(jCount).getId(),listFireBasePaths.get(jCount).getBadCount()-1);
//                            }
                        }
                    }
                });
            } else {
                //BadCount
                Log.d(TAG, "listFireBasePaths:Jcount:0" + jCount + " size:" + listFireBasePaths.size());
                dbHelper.dbUpdateBadCount(listFireBasePaths.get(jCount).getId(), listFireBasePaths.get(jCount).getBadCount() + 1);
            }
        }
    }

    private boolean getCheckWriteOrNotInFirerBase(Double myLatitude, Double myLongitude, long myTime) {
        //Здесь необходимо из SharedPreferency получить Разрешенную дистанцию
        //Также время записи в FB
        modelCheck = dbHelper.getLatLongTimeFromMe();
        double calcDistance = distance(myLatitude, myLongitude, modelCheck.getLatitude(), modelCheck.getLongtitude(), "meter");
        long calcTimeDistance = System.currentTimeMillis() - myTime;
        if (acceptDistance < calcDistance || timeAccpetWait < calcTimeDistance) {
            //Wirte to FireBasde
            wakeup_min = WAKEUP_MIN_2;
            return true;
        } else {
            switch (wakeup_min){
                case WAKEUP_MIN_2:
                    wakeup_min = WAKEUP_MIN_5;
                    break;
                case WAKEUP_MIN_5:
                    wakeup_min = WAKEUP_MIN_10;
                    break;
                default:
                    wakeup_min = WAKEUP_MIN_5;
                    break;
            }
            return false;
        }
    }

    private String getNormalizeString(String s) {
        //        return  (s.replaceAll("[^A-Za-z0-9+]","_")).replaceAll("[@]+","_");
        return s.replaceAll("[^A-Za-z0-9]+", "_");
    }


    private double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        } else if (unit == "meter") {
            dist = dist * 1609.344;
        }
        Log.d(TAG, "distance : " + dist);
        return dist;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    private void requestLocationUpdates() {
        Log.d(TAG, "requestLocationUpdates()..");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.d(TAG, "test connected requestLocationUpdates()..");
        if (googleApiClient.isConnected()) {
            Log.d(TAG, " LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)");
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
            googleApiClient.connect();
        }
    }

    protected void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates()");
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        locationRequest = null;
    }


    private void initPreferences() {
        PreferenceHelper.getInstance().init(getApplicationContext());
        preferenceHelper = PreferenceHelper.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Location Service onStartCommand " + this.hashCode());
        initService();
        return super.onStartCommand(intent, flags, startId);
    }

    private void changelocationRequestOnBattery() {
        Log.d(TAG, "changelocationRequestOnBattery()");
        Log.d(TAG, "(LOCATION_MODE) = " + preferenceHelper.getInt(LOCATION_MODE));
        if (locationRequest == null) {
            locationRequest = new LocationRequest();
            if (preferenceHelper.getInt(BATTERY_CHARGED) > 35) {
                Log.d(TAG, "(BATTERY_CHARGED) >35");
//                if (preferenceHelper.getInt(LOCATION_MODE)==1){
//                    Log.d(TAG, "Mode = 1");
//                    return;
//                }
                preferenceHelper.putInt(LOCATION_MODE, 1); //Mode location = 1
                locationRequest.setInterval(10 * 1000);
                locationRequest.setFastestInterval(5 * 1000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            } else {
                if (preferenceHelper.getInt(BATTERY_CHARGED) > 18) {
                    Log.d(TAG, "(BATTERY_CHARGED) >18");
//                    if (preferenceHelper.getInt(LOCATION_MODE)==2){
//                        Log.d(TAG, "Mode = 2");
//                        return;
//                    }
                    preferenceHelper.putInt(LOCATION_MODE, 2); //Mode location = 2
                    locationRequest.setInterval(20 * 1000);
                    locationRequest.setFastestInterval(5 * 1000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    //locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                } else if (preferenceHelper.getInt(BATTERY_CHARGED) < 19) {
                    Log.d(TAG, "(BATTERY_CHARGED) <19");
//                    if (preferenceHelper.getInt(LOCATION_MODE)==3){
//                        Log.d(TAG, "Mode = 3");
//                        return;
//                    }
                    preferenceHelper.putInt(LOCATION_MODE, 3); //Mode location = 3
                    locationRequest.setInterval(30 * 1000);
                    locationRequest.setFastestInterval(5 * 1000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    //locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                }
            }
        }
    }

    private void initService() {
        //
        if (preferenceHelper == null) {
            initPreferences();
        }
        preferenceHelper.putBoolean(KEY_LOCATION_SERVICE_STARTED, true);
        Log.d(TAG, "Start service location!");
        if (checkPlayServices()) {
            Log.d(TAG, "buildGoogleApiClient");

            //  createLocationRequest();
            buildGoogleApiClient();
            if (locationRequest == null) {
                //Изменение параметров прогслушивания
                changelocationRequestOnBattery();
/*
                locationRequest = new LocationRequest();
                locationRequest.setInterval(10 * 1000);
                locationRequest.setFastestInterval(5 * 1000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
*/
            }
            Log.d(TAG, "Google API Yастройки законченны");
            if (googleApiClient.isConnected()) {
                requestLocationUpdates();
            }
        } else {
            Log.d(TAG, "Внимание!! = Опрежеление координат невозможно!");
            onDestroy();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "LOcatio Service Started");
        myAccuracy = 0;
        timeWork = System.currentTimeMillis();
        //
        if (preferenceHelper == null) {
            initPreferences();
        }
        preferenceHelper.putInt(LOCATION_MODE, 0);
        //init SQlite
        dbHelper = TODOApplication.getInstance().dbHelper;
        if (dbHelper == null) {
            Log.e(TAG, "SQLite error! Service Application destroyed.");
            onDestroy();
        }

        //initializing firebase authentication object
        //firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth = TODOApplication.getFireBaseAuth();

        //if the user is not logged in
        //that means current user will return null
        if (firebaseAuth.getCurrentUser() == null) {
            //closing this activity
            Log.d(TAG, "Attention FireBase User not Authorizied! DeviceLocationService...Destroy()");
            onDestroy();
        }
        //getting current user
        user = firebaseAuth.getCurrentUser();
        //getting the database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReferenceId = databaseReference.getRef();

        //Проверка правильности SQLite email
        if (dbHelper.getEmail() != null) {
            if (!dbHelper.getEmail().equals(user.getEmail())) {
                dbHelper.dbUpdateEmail(1, user.getEmail());
            }
        }

        Log.d(TAG, "DeviceLocartionService FireBase: " + databaseReference.toString());
        Log.d(TAG, "DeviceLocartionService FireBase Key: " + databaseReference.child(user.getUid()).getKey().toString());
        Log.d(TAG, "DeviceLocartionService FireBase: " + databaseReference.child(user.getUid()).toString());

//        databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.child("test").toString();
//                Log.d(TAG, "FBase value = " + value.toString());
//                if (dataSnapshot.exists()) {
//                    String valueJson = dataSnapshot.getValue().toString();
//                    Log.d(TAG, "FBase value Json =" + valueJson);
//                    //               TestModel testModel = dataSnapshot.child("-KboI4Ia6DSToPRURKmG").getValue(TestModel.class);
//                    //               Log.d(TAG,"FBase TestModel class ="+testModel.getTest());
////                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
////                        TestModel testModel2 = dataSnapshot.child("-KboI4IdtZiiNh921-RB").getValue(TestModel.class);
////                        Log.d(TAG,"FBase TestModel2 class ="+testModel2.getTest());
////                    }
//                }
//            }
//
//            //Ce2N4qWXF5UahlEHiHuLJSBzBzk1
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "FBase value = read Failed.");
//            }
//        });
//
//        databaseReference.child("Fkq0Hze0sXgatHf0dsnkD0gTGiO2").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "addValueEventListener : Fkq0Hze0sXgatHf0dsnkD0gTGiO2 :successfully! ");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "addValueEventListener : Fkq0Hze0sXgatHf0dsnkD0gTGiO2 :error! ");
//            }
//        });

        //FireBase addValue Child Listner
        databaseReference.child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "FBase addChildEventListener onChildAdded value = s." + s);
                Log.d(TAG, "FBase addChildEventListener onChildAdded dataSnapshot key = " + dataSnapshot.getKey());
                if (!dataSnapshot.getKey().isEmpty()) {
                    //TestModel testModel4 = dataSnapshot.getValue(TestModel.class);
                    //Log.d(TAG, "FBase TestModel3 class =" + testModel4.getTest());
                    fbaseModel = dataSnapshot.getValue(FbaseModel.class);
                    databaseReference.child(user.getUid()).child(dataSnapshot.getKey()).removeValue();
                    if (fbaseModel != null) {
                        dbHelper.updateFbaseModel(fbaseModel.getState(),
                                fbaseModel.getMode(), fbaseModel.getRights(),
                                fbaseModel.getSpeed(), fbaseModel.getMoved(),
                                fbaseModel.getDateTime(), fbaseModel.getLongtitude(),
                                fbaseModel.getLattitude(), fbaseModel.getFbase_path(),
                                fbaseModel.getEmail(), fbaseModel.getPart_email());
                        dbHelper.dbReadInLog();
                        if (preferenceHelper.getBoolean(KEY_ACTIVITY_READY)) {
                            if (dbHelper.getId(fbaseModel.getEmail()) != 0) {
                                if (dbHelper.getId(fbaseModel.getEmail())>1){
                                    sendMessage(fbaseModel.getLattitude(), fbaseModel.getLongtitude(),
                                            dbHelper.getId(fbaseModel.getEmail()),fbaseModel.getDateTime());
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "FBase addChildEventListener onChildChanged value = s." + s);
                Log.d(TAG, "FBase addChildEventListener onChildChanged dataSnapshot key = " + dataSnapshot.getKey());
                if (!dataSnapshot.getKey().isEmpty()) {
                    //TestModel testModel3 = dataSnapshot.getValue(TestModel.class);
                    //Log.d(TAG, "FBase TestModel3 class =" + testModel3.getTest());
                    fbaseModel = dataSnapshot.getValue(FbaseModel.class);
                    databaseReference.child(user.getUid()).child(dataSnapshot.getKey()).removeValue();
                    if (fbaseModel != null) {
                        dbHelper.updateFbaseModel(fbaseModel.getState(),
                                fbaseModel.getMode(), fbaseModel.getRights(),
                                fbaseModel.getSpeed(), fbaseModel.getMoved(),
                                fbaseModel.getDateTime(), fbaseModel.getLongtitude(),
                                fbaseModel.getLattitude(), fbaseModel.getFbase_path(),
                                fbaseModel.getEmail(), fbaseModel.getPart_email());
                        dbHelper.dbReadInLog();
                        if (preferenceHelper.getBoolean(KEY_ACTIVITY_READY)) {
                            if (dbHelper.getId(fbaseModel.getEmail()) != 0) {
                                if (dbHelper.getId(fbaseModel.getEmail())>1){
                                    sendMessage(fbaseModel.getLattitude(), fbaseModel.getLongtitude(),
                                            dbHelper.getId(fbaseModel.getEmail()),fbaseModel.getDateTime());
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "FBase addChildEventListener onChildRemoved value");
                Log.d(TAG, "FBase addChildEventListener onChildRemoved dataSnapshot key = " + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "FBase addChildEventListener onChildMoved value = s." + s);
                Log.d(TAG, "FBase addChildEventListener onChildMoved dataSnapshot key = " + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "FBase addChildEventListener onChildAdded value = s." + databaseError.toString());
            }
        });

        /*
        Тестироввание Создадим объект в FireBase`
         */
        //databaseReference.child(user.getUid()).push().setValue(new TestModel("test101", "email@mail.ru", 129l));
       // databaseReference.child(user.getUid()).push().setValue(new TestModel("test202", "email@mail.ru", 129l));

      //  databaseReference.child("M0erubbTS6hbInqmOmnZOPelZfE2").push().setValue(new TestModel("test202", "email@mail.ru", 129l));

      //  databaseReference.child(user.getUid()).child("test83737MAILRU").setValue(new TestModel("For more information see", "email@mail.ru", 129l));
        // Map<String, TestModel> testModels = new HashMap<String, TestModel>();
        // testModels.put("testoviy Rklient", new TestModel("New Test User1"));
        // databaseReference.child(user.getUid()).setValue(testModels); // все стирает в ключе! Остается одна запись!

        /*
        Коненц тестирования
         */
        //  initService();1388714548
        //Ставим broadcast на батарею
        br = new BoardReceiverBattery();
        receiver = br.InitReceiver();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }


    @Override
    public void onDestroy() {
        preferenceHelper.putBoolean(KEY_LOCATION_SERVICE_STARTED, false);
        Log.d(TAG, "onDestroy begining");
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
            locationRequest = null;
            preferenceHelper.putInt(LOCATION_MODE, 0);
            Log.d(TAG, "onDestroy googleAPi disabled " + this.hashCode());
            if (receiver != null) {
                unregisterReceiver(receiver);
                receiver = null;
            }
            Log.d(TAG, ":DeviceServiceLocation:onDestroy() :" + br.GetBatteryInfo());
        }
        onWakeUpInstallation();
        Log.d(TAG, "onDestroy end");
        super.onDestroy();
    }

    private void onWakeUpInstallation(){
        Intent intent = new Intent(this, DeviceLocationService.class);
        pendingIntent = PendingIntent.getService(this,0,intent,0);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, wakeup_min);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        Log.d(TAG,"alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);");
    }
}
