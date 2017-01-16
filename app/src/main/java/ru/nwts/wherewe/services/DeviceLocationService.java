package ru.nwts.wherewe.services;

import android.Manifest;
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

import java.text.SimpleDateFormat;

import ru.nwts.wherewe.util.BoardReceiverBattery;
import ru.nwts.wherewe.util.PreferenceHelper;

/**
 * Created by Надя on 10.01.2017.
 */

public class DeviceLocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //LOG
    public static final String TAG = "MyLogs";


    PreferenceHelper preferenceHelper;
    private final String BATTERY_CHARGED = "BATTERY_CHARGED";
    private final String KEY_ACTIVITY_READY="PROF_ACTIVITY";
    private final String KEY_LOCATION_SERVICE_STARTED="LOCATION_SERVICE";

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

    @Override
    public void onLocationChanged(Location location) {
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();
        myTime = location.getTime();
        mySpeed = (long) location.getSpeed();
        Log.d(TAG,"KEY_ACTIVITY_READY : "+preferenceHelper.getBoolean(KEY_ACTIVITY_READY));
        //if ProfActivity not Start, may disabled service location
        if (!preferenceHelper.getBoolean(KEY_ACTIVITY_READY)){
            if (myAccuracy == location.getAccuracy()){
                Log.d(TAG," Destroy on myAccuracy == location.getAccuracy().");
                this.stopSelf();
                //onDestroy();
            }
            if (System.currentTimeMillis() - timeWork > 60 * 1000) {
                Log.d(TAG," Destroy on timeWork > 4 min.");
                this.stopSelf();
                //onDestroy();
            }
        }

        myAccuracy = location.getAccuracy();

        Log.d(TAG, "onLocationChanged");
        Log.d(TAG, "Latitude : " + String.valueOf(myLatitude));
        Log.d(TAG, "Longitude : " + String.valueOf(myLongitude));
        Log.d(TAG, "myAccuracy : " + String.valueOf(myAccuracy));
        Log.d(TAG, "myTime : " + String.valueOf(myTime) +" normal time write:"+dateformat.format(myTime));
        Log.d(TAG, "mySpeed : " + String.valueOf(mySpeed));
    }

    private void requestLocationUpdates() {
        Log.d(TAG,"requestLocationUpdates()..");
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
        Log.d(TAG,"test connected requestLocationUpdates()..");
        if(googleApiClient.isConnected()){
            Log.d(TAG," LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)");
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
        Log.d(TAG,"stopLocationUpdates()");
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }


    private void initPreferences() {
        //initializing preference
        PreferenceHelper.getInstance().init(getApplicationContext());
        preferenceHelper = PreferenceHelper.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"Location Service onStartCommand " + this.hashCode());
        initService();
        return super.onStartCommand(intent, flags, startId);
    }

    private void changelocationRequestOnBattery(){
        Log.d(TAG, "changelocationRequestOnBattery()");
        if (locationRequest == null){
            locationRequest = new LocationRequest();
            if (preferenceHelper.getInt(BATTERY_CHARGED) >35){
                Log.d(TAG, "(BATTERY_CHARGED) >35");
                locationRequest.setInterval(10 * 1000);
                locationRequest.setFastestInterval(5 * 1000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            }else {
                if(preferenceHelper.getInt(BATTERY_CHARGED) > 18){
                    Log.d(TAG, "(BATTERY_CHARGED) >18");
                    locationRequest.setInterval(20 * 1000);
                    locationRequest.setFastestInterval(5 * 1000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    //locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                }else if(preferenceHelper.getInt(BATTERY_CHARGED)<19){
                    Log.d(TAG, "(BATTERY_CHARGED) <19");
                    locationRequest.setInterval(30 * 1000);
                    locationRequest.setFastestInterval(5 * 1000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    //locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                }
            }
        }
    }

    private void initService(){
        //
        if (preferenceHelper == null){
            initPreferences();
        }
        preferenceHelper.putBoolean(KEY_LOCATION_SERVICE_STARTED,true);
        Log.d(TAG, "Start service location!");
        if (checkPlayServices()) {
            Log.d(TAG, "buildGoogleApiClient");

            //  createLocationRequest();
            buildGoogleApiClient();
            if (locationRequest == null){
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
        Log.d(TAG,"LOcatio Service Started");
        myAccuracy = 0;
        timeWork = System.currentTimeMillis();
      //  initService();1388714548
        //Ставим broadcast на батарею
        br = new BoardReceiverBattery();
        receiver = br.InitReceiver();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }


    @Override
    public void onDestroy() {
        preferenceHelper.putBoolean(KEY_LOCATION_SERVICE_STARTED,false);
        Log.d(TAG, "onDestroy begining");
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
            locationRequest = null;
            Log.d(TAG, "onDestroy googleAPi disabled "+ this.hashCode());
            if (receiver != null) {
                unregisterReceiver(receiver);
                receiver = null;
            }
            Log.d(TAG, ":DeviceServiceLocation:onDestroy() :"+br.GetBatteryInfo());
        }
        Log.d(TAG, "onDestroy end");
        super.onDestroy();
    }
}
