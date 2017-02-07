package ru.nwts.wherewe;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.List;

import ru.nwts.wherewe.database.DBHelper;
import ru.nwts.wherewe.model.Model;
import ru.nwts.wherewe.model.SmallModel;
import ru.nwts.wherewe.services.DeviceLocationService;
import ru.nwts.wherewe.services.LocationService;
import ru.nwts.wherewe.util.PreferenceHelper;

import static ru.nwts.wherewe.TODOApplication.*;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    //LOG
    public static final String TAG = "MyLogs";

    PreferenceHelper preferenceHelper;

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //view objects
    private TextView textViewUserEmail;
    private Button buttonLogout;
    private Button buttonService;

    private Drawer drawer = null;
    private Toolbar toolbar;

    private String[] channelNames;
//    private String[] channelId;
//    private String[] videoTypes;

    private int selectedDrawerItem = 0;

    //
    private GoogleMap Map;
    public DBHelper dbHelper;
    //Db
    List<SmallModel> smallModels;

    //
    Intent locationService;

    private final String KEY_ACTIVITY_READY="PROF_ACTIVITY";
    private final String KEY_LOCATION_SERVICE_STARTED="LOCATION_SERVICE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        //initializing firebase authentication object
        //firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth = TODOApplication.getFireBaseAuth();

        //if the user is not logged in
        //that means current user will return null
        if (firebaseAuth.getCurrentUser() == null) {
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();


        //initializing views
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonService = (Button) findViewById(R.id.buttonService);

        channelNames = getResources().getStringArray(R.array.channel_names);
        PrimaryDrawerItem[] primaryDrawerItems = new PrimaryDrawerItem[channelNames.length];

        for (int i = 0; i < channelNames.length; i++) {
            primaryDrawerItems[i] = new PrimaryDrawerItem()
                    .withName(channelNames[i])
                    .withIdentifier(i)
                    .withSelectable(false);
        }

        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .build();

        drawer = new DrawerBuilder(this)
                .withActivity(ProfileActivity.this)
                .withAccountHeader(accountHeader)
                .withDisplayBelowStatusBar(true)
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withSavedInstance(savedInstanceState)
                .addDrawerItems(primaryDrawerItems)
                .addStickyDrawerItems(
                        new SecondaryDrawerItem()
                                .withName(getString(R.string.about))
                                .withIdentifier(channelNames.length - 1)
                                .withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        return false;
                        //
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();


        //displaying logged in user name
        textViewUserEmail.setText("Welcome " + user.getEmail()+" "+TODOApplication.getInstance().TEST_STRING);

        //adding listener to button
        buttonLogout.setOnClickListener(this);
        buttonService.setOnClickListener(this);

        //Works Databases
        //dbHelper = new DBHelper(this);
        dbHelper = TODOApplication.getInstance().dbHelper;
        if (dbHelper !=null){
            dbHelper.dbDeleteUsers();
            dbHelper.dbInsertUser("Test name_115", 1,1,1,1,1,198299922,33.35324905,65.84073992,"M0erubbTS6hbInqmOmnZOPelZfE2", null, 0, 999,"i123456789", "o123456789", "test1@mail.ru", "09");
            dbHelper.dbInsertUser("Test name_1", 1,1,1,1,1,198299922,28.55324905,68.14073992,"Fkq0Hze0sXgatHf0dsnkD0gTGiO2", null, 0, 999,"i123456789", "o123456789", "test1@mail.ru", "09");
            dbHelper.dbInsertUser("Test name_3", 1,1,1,1,1,198299922,30.35324905,64.84073992,null, null, 0, 999,"i123456789", "o123456789", "test1@mail.ru", "09");
            dbHelper.dbReadInLog();
            smallModels = dbHelper.getListSmallModel();
        }


        locationService = new Intent(ProfileActivity.this, DeviceLocationService.class);
        //Preference
        initPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Write to SharedPref what ProfActivity Started
        preferenceHelper.putBoolean(KEY_ACTIVITY_READY,true);
        //Tested Run LocationService Or Not
        if (!preferenceHelper.getBoolean(KEY_LOCATION_SERVICE_STARTED)){
            //Тест координат
            Log.d(TAG,"Start service! from ProfileActivity!");
//
//        locationService = new Intent(ProfileActivity.this, LocationService.class);
//        locationService.putExtra("task", "GetMyLocation");
//        startService(locationService);
        }
        startService(locationService);
    }

    @Override
    protected void onPause() {
        //Write to SharedPref what ProfActivity Started
        Log.d(TAG,"ProfileActivity onPaiused..");
        preferenceHelper.putBoolean(KEY_ACTIVITY_READY,false);
        super.onPause();
    }

    private void initPreferences() {
        //initializing preference
        PreferenceHelper.getInstance().init(getApplicationContext());
        preferenceHelper = PreferenceHelper.getInstance();
    }

    @Override
    public void onClick(View view) {
        //if logout is pressed
        if (view == buttonLogout) {
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }
        if (view == buttonService){
            startService(locationService);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_option, menu);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Map = googleMap;

        if(Map == null){
            Toast.makeText(getApplicationContext(),
                    "Error creating map",Toast.LENGTH_SHORT).show();
            return;
        }

        if (smallModels == null){
            Toast.makeText(getApplicationContext(),
                    "Error SmallModel empty Error!",Toast.LENGTH_SHORT).show();
            return;
        }

        for(int j=0;j < smallModels.size();j++){
            SmallModel model = smallModels.get(j);
            Map.addMarker(new MarkerOptions().position(new LatLng(model.getLattitude(), model.getLongtitude())).title(model.getName()));
            Map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(model.getLattitude(), model.getLongtitude())));
        }

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //Map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //Map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
