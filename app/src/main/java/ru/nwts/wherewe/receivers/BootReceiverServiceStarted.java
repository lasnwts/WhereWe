package ru.nwts.wherewe.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ru.nwts.wherewe.TODOApplication;
import ru.nwts.wherewe.services.DeviceLocationService;

/**
 * Created by пользователь on 09.02.2017.
 */

public class BootReceiverServiceStarted extends BroadcastReceiver {
    public BootReceiverServiceStarted() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Context context1 = context.getApplicationContext();
        Intent serviceIntent = new Intent(context1, DeviceLocationService.class);
        context1.startService(serviceIntent);
        Log.d("myLogs","BootReceiverServiceStarted");
    }
}
