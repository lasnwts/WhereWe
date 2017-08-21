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
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Calendar;

import ru.nwts.wherewe.BaseActivity;
import ru.nwts.wherewe.R;
import ru.nwts.wherewe.TODOApplication;
import ru.nwts.wherewe.services.DeviceLocationService;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by пользователь on 09.02.2017.
 */

public class BootReceiverServiceStarted extends BroadcastReceiver {
    private int wakeup_min = 1; //default time alarm
    //LOG
    public static final String TAG = "MyLogs";
    // Идентификатор уведомления
    private final int NOTIFY_ID = 101;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        Context context1 = context.getApplicationContext();
        setNotification(context1);
        onWakeUpInstallation(context1);
        Intent serviceIntent = new Intent(context1, DeviceLocationService.class);
        context1.startService(serviceIntent);
//        Intent baseIntent = new Intent(context1, BaseActivity.class);
//        context1.startActivity(baseIntent);
        Log.d("myLogs","BootReceiverServiceStarted");
    }

    private void onWakeUpInstallation(Context context) {
        Intent intent = new Intent(context, DeviceLocationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, wakeup_min);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Log.d(TAG, "alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);");
    }

    /**
     * Установка уведомления
     * для гарантированног7о запуска
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setNotification(Context context){

        Intent notificationIntent = new Intent(context, BaseActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);

        Notification.Builder builder1 = builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_service_map)
                // большая картинка
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_app))
                //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                .setTicker(context.getString(R.string.service_notification_run))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle(context.getString(R.string.service_notification_remind))
                //.setContentText(res.getString(R.string.notifytext))
                .setContentText(context.getString(R.string.service_notification_remind_desc));// Текст уведомления

        // Notification notification = builder.getNotification(); // до API 16
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        }

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
    }

}
