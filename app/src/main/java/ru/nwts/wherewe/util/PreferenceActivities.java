package ru.nwts.wherewe.util;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.nwts.wherewe.R;

public class PreferenceActivities extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_activities);


        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferencyFragments())
                .commit();


    }
}
