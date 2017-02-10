package ru.nwts.wherewe.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Надя on 27.11.2016.
 */

public class PreferenceHelper {

    public static final String SPLASH_IS_INVISIBLE = "splash_is_invisible";

    private static PreferenceHelper instance;

    private Context context;

    private SharedPreferences preferences;

    private PreferenceHelper() {
    }

    public static PreferenceHelper getInstance(){
        if (instance==null){
            instance = new PreferenceHelper();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
        //preferences = context.getSharedPreferences("preferences",Context.MODE_PRIVATE);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public void putBoolean (String key, boolean value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public boolean getBoolean (String key){
        return preferences.getBoolean(key,false);
    }

    public void putString (String key, String value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public void putEmail (String key, String value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key,getNormalizeString(value));
        editor.apply();
    }

    private String getNormalizeString(String s){
        return  s.replaceAll("[^A-Za-z0-9,@,.,\\,]+","");
    }

    public String getString(String key){ return preferences.getString(key,"");}

    public void putLong (String key, long value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key,value);
        editor.apply();
    }

    public long getLong(String key){ return preferences.getLong(key,0);}

    public void putInt (String key, int value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key,value);
        editor.apply();
    }

    public int getInt(String key){ return preferences.getInt(key,0);}

    public void putFloat(String key, float value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key,value);
        editor.apply();
    }

    public float getFloat(String key){
        return preferences.getFloat(key,0);
    }
}
