package ru.nwts.wherewe.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import ru.nwts.wherewe.TODOApplication;
import ru.nwts.wherewe.util.PreferenceHelper;


/**
 * Created by пользователь on 16.02.2017.
 */

public class SmsReceiver extends BroadcastReceiver {

    //Телефон владельца машины
    public static final String MASTER = "master";
    public static final String SMS_TEL = "tel";
    public static final String SMS_Originating = "Originating";
    public static final String SMS_MessageBody = "MessageBody";
    public static final String SMS_Body = "Body";
    public static final String SMS_Class = "Class";
    private static final String SMS_REC_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    public String sms_master = "";
    public String tel_master = "";
    public String sms_body = "";

    //Наименование Shared Preference
    PreferenceHelper preferenceHelper;

    //LOG
    public static final String TAG = "MyLogs";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SmsReceiver.SMS_REC_ACTION)) {

            //initializing preference
            PreferenceHelper.getInstance().init(TODOApplication.getInstance());
            preferenceHelper = PreferenceHelper.getInstance();

            if (!preferenceHelper.getBoolean("allowedSMSRead")) {
                return;
            }

            StringBuilder sb = new StringBuilder();

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);

                    sms_master = smsMessage.getOriginatingAddress();
                    sms_body = smsMessage.getMessageBody();


                    if (sms_body.indexOf("00099999") == 0) {

                        sb.append("\nAddress:" +
                                smsMessage.getOriginatingAddress());
                        //ed.putString(SMS_TEL, smsMessage.getOriginatingAddress());

                        sb.append("\nDisplay Originating:" +
                                smsMessage.getDisplayOriginatingAddress());

                        sb.append("\nDisplay MessageBody: " +
                                smsMessage.getDisplayMessageBody());
                        sb.append("\nMessage Body: " +
                                smsMessage.getMessageBody());
                        //ed.putString(SMS_Body, smsMessage.getMessageBody());
                        //ed.commit();
                        sb.append("\nMessage Class: " +
                                smsMessage.getMessageClass());
                        sb.append("\nProtocol Identifier: " +
                                smsMessage.getProtocolIdentifier());
                        sb.append("\nPseudo Subject: " +
                                smsMessage.getPseudoSubject());
                        sb.append("\nService Center Address: " +
                                smsMessage.getServiceCenterAddress());
                        sb.append("\nStatus: " +
                                smsMessage.getStatus());
                        sb.append("\nStatus On ICC: " +
                                smsMessage.getStatusOnIcc());

                        Log.v(TAG, " Receiver SMS:" + sb.toString());
                        Log.v(TAG, " Receiver tel_master:" + tel_master);
                        Log.v(TAG, " Receiver sms_master:" + sms_master);
                        Toast.makeText(context, "SMS Received message" + sb.toString(), Toast.LENGTH_LONG).show();
                        abortBroadcast();
                    }
                }
            }
        }
    }
}
