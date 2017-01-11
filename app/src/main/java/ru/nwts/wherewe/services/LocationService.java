package ru.nwts.wherewe.services;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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

import java.util.Timer;
import java.util.TimerTask;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


/**
 * Created by Надя on 21.12.2016.
 */

public class LocationService extends IntentService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //LOG
    public static final String TAG = "MyLogs";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location lastLocation;

    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Double myLatitude;
    private Double myLongitude;
    private long timeWork;
    private Timer timer;
    private TimerTask timerTask;

    private boolean requestLocationUpdates = false;


    private static int UPDATE_INTERVAL = 10000;
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;

    public LocationService() {
        super("LocationService");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        timeWork = System.currentTimeMillis();
        Log.d(TAG, "Start service location!");
        if (checkPlayServices()) {
            Log.d(TAG, "buildGoogleApiClient");

            //  createLocationRequest();
            buildGoogleApiClient();
            locationRequest = new LocationRequest();
            locationRequest.setInterval(1 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            Log.d(TAG, "Google API Yастройки законченны");
            if (googleApiClient.isConnected()) {
                requestLocationUpdates();
            }
        } else {
            Log.d(TAG, "Dерч  !Опрежеление координат невозможно!");
            onDestroy();
        }
    }

    private void requestLocationUpdates() {
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
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent service");
        if (intent != null) {
            String label = intent.getStringExtra("task");
            Log.d(TAG, "onHandleIntent start: " + label);
            if (label.equals("GetMyLocation")) {
                Log.d(TAG, "Pадержка 30 сек!");
                if (timer != null){
                    timer.cancel();
                    timer = null;
                }
                if (timerTask == null){
                    timerTask = new timerTask();
                }
                timer = new Timer();
                timer.schedule(timerTask,10000);
                startLocationUpdates();
            }
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
        Log.d(TAG, "GoogleApiClient onConnected connected");

    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectedSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();
        Log.d(TAG, "onLocationChanged");
        Log.d(TAG, "Latitude : " + String.valueOf(myLatitude));
        Log.d(TAG, "Longitude : " + String.valueOf(myLongitude));
        if (System.currentTimeMillis() - timeWork > 60 * 1000) {
            onDestroy();
        }
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

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FATEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected void startLocationUpdates() {
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
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy begining");
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
            Log.d(TAG, "onDestroy googleAPi disabled");
        }
        Log.d(TAG, "onDestroy end");
        super.onDestroy();
    }

    class timerTask extends TimerTask{

        @Override
        public void run() {
            Log.d(TAG, "timer task start- end");
        }
    }

}
