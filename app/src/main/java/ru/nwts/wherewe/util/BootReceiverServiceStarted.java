package ru.nwts.wherewe.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.nwts.wherewe.TODOApplication;
import ru.nwts.wherewe.services.DeviceLocationService;

/**
 * Created by пользователь on 09.02.2017.
 */

public class BootReceiverServiceStarted extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){
            Intent serviceIntent = new Intent(context, DeviceLocationService.class);
            context.startService(serviceIntent);
        }
    }
}
