package ru.nwts.wherewe.aux_ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import ru.nwts.wherewe.R;
import ru.nwts.wherewe.fragments.PreferencyFragments;

public class PreferenceActivities extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_activities);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (null != toolbar) {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary_color, getTheme())));
                } else {
                    toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary_color)));
                }
            } else {
                toolbar.setBackgroundColor(getResources().getColor(R.color.primary_color));
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(R.string.pref_setting);

            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new PreferencyFragments())
                    .commit();
        }
    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
        super.onPause();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition (R.anim.open_main, R.anim.close_next);
    }

}
