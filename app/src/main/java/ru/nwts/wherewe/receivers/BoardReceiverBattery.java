package ru.nwts.wherewe.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.util.Log;

import ru.nwts.wherewe.TODOApplication;
import ru.nwts.wherewe.util.PreferenceHelper;

/**
 * Created by Надя on 16.01.2017.
 */

public class BoardReceiverBattery {

    //Наименование Shared Preference
    PreferenceHelper preferenceHelper;

    public static final String BATTERY_CHARGED = "BATTERY_CHARGED";
    public static final String BATTERY_STATUS = "battery_status";
    public static final String BATTERY_HEALTH = "battery_helth";

    //LOG
    public static final String TAG = "MyLogs";

    private BroadcastReceiver receiver;
    public String batteryInfo;

    private void initPreferences() {
        //initializing preference
        PreferenceHelper.getInstance().init(TODOApplication.getInstance());
        preferenceHelper = PreferenceHelper.getInstance();
    }


    public String GetBatteryInfo() {
        return this.batteryInfo;
    }

    public void SetBatteryInfo(String s) {
        Log.v(TAG, " SetBatteryInfo(String s) :" + s);
        this.batteryInfo = s;
        Log.v(TAG, " SetBatteryInfo:this.batteryInfo :" + this.batteryInfo);
    }


    public BroadcastReceiver InitReceiver() {

        initPreferences();
        receiver = new BroadcastReceiver() {

            private String batteryInfo;

            @SuppressWarnings("static-access")
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(
                        BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(
                        BatteryManager.EXTRA_SCALE, -1);
                int status = intent.getIntExtra(
                        BatteryManager.EXTRA_STATUS, -1);
                int healt = intent.getIntExtra(
                        BatteryManager.EXTRA_HEALTH, -1);
                int plugged = intent.getIntExtra(
                        BatteryManager.EXTRA_PLUGGED, -1);
                String technology = intent.getStringExtra(
                        BatteryManager.EXTRA_TECHNOLOGY);
                //int icon = intent.getIntExtra(
                //		BatteryManager.EXTRA_ICON_SMALL, -1);
                float voltage = (float) intent.getIntExtra(
                        BatteryManager.EXTRA_VOLTAGE, -1) / 1000;
                boolean present = intent.getBooleanExtra(
                        BatteryManager.EXTRA_PRESENT, false);
                float temperature = (float) intent.getIntExtra(
                        BatteryManager.EXTRA_TEMPERATURE, -1) / 10;

                String shealth = "Not reported";
                switch (healt) {
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        shealth = "Dead";
                        break;
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        shealth = "Good";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        shealth = "Over voltage";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        shealth = "Over heating";
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        shealth = "Unknown";
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                        shealth = "Failure, but unknown";
                        break;
                }

                String sStatus = "Not reported";
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        sStatus = "Charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        sStatus = "Discharging";
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        sStatus = "Full";
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        sStatus = "Not Charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        sStatus = "Unknown";
                        break;
                }

                String splugged = "Not Reported";
                switch (plugged) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        splugged = "On AC";
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        splugged = "On USB";
                        break;
                }

                int chargedPct = (level * 100) / scale;

                batteryInfo = "Battery Info:" +
                        "\nHealth: " + shealth +
                        "\nStatus: " + sStatus +
                        "\nCharged: " + chargedPct + "%" +
                        "\nPlugged: " + splugged +
                        "\nVoltage: " + voltage +
                        "\nTechnology: " + technology +
                        "\nTemperature: " + temperature + "C" +
                        "\nBattery present: " + present + "\n";

                Log.v(TAG, " BC:battery :" + sStatus + " " + chargedPct + "%");
                Log.v(TAG, " BC:batteryInfo :" + batteryInfo);
                SetBatteryInfo(sStatus + " " + chargedPct + "%");

                //Разрешаем стартовать сервис
                preferenceHelper.putInt(BATTERY_CHARGED, chargedPct);
                preferenceHelper.putString(BATTERY_STATUS, sStatus);
                preferenceHelper.putString(BATTERY_HEALTH, shealth);
            }
        };
        return receiver;
    }
}
