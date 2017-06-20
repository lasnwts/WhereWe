package ru.nwts.wherewe.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import ru.nwts.wherewe.R;

/**
 * Created by Надя on 28.11.2016.
 */

public class PreferencyFragments extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
