package ru.nwts.wherewe.receivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Calendar;

import ru.nwts.wherewe.BaseActivity;
import ru.nwts.wherewe.R;
import ru.nwts.wherewe.services.DeviceLocationService;

/**
 * Created by пользователь on 21.08.2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    //LOG
    public static final String TAG = "MyLogs";

    @Override
    public void onReceive(Context context, Intent intent) {
        Context context1 = context.getApplicationContext();
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() ||
                mobile != null && mobile.isConnectedOrConnecting();
        if (isConnected) {
            Log.d(TAG, "NetworkChangeReceiver Network Available YES");
            Log.d("myLogs","BootReceiverServiceStarted");
        } else {
            Log.d(TAG, "NetworkChangeReceiver Network Available NO");
        }
        Log.d("myLogs","BootReceiverServiceStarted");
        Intent serviceIntent = new Intent(context1, DeviceLocationService.class);
        context1.startService(serviceIntent);
    }
}
