package ru.nwts.wherewe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ru.nwts.wherewe.aux_ui.About;
import ru.nwts.wherewe.aux_ui.RecyclerViews;
import ru.nwts.wherewe.database.DBHelper;
import ru.nwts.wherewe.model.SmallModel;
import ru.nwts.wherewe.services.DeviceLocationService;
import ru.nwts.wherewe.util.DialogFragmentInputStr;
import ru.nwts.wherewe.util.DialogFragmentYesNo;
import ru.nwts.wherewe.util.PreferenceActivities;
import ru.nwts.wherewe.util.PreferenceHelper;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker;
import static ru.nwts.wherewe.R.drawable.ic_view_list_white_18dp;
import static ru.nwts.wherewe.R.id.map;
import static ru.nwts.wherewe.TODOApplication.*;
import static ru.nwts.wherewe.database.DBConstant.KEY_DATE;
import static ru.nwts.wherewe.database.DBConstant.KEY_ID;
import static ru.nwts.wherewe.database.DBConstant.KEY_LATTITUDE;
import static ru.nwts.wherewe.database.DBConstant.KEY_LONGTITUDE;
import static ru.nwts.wherewe.database.DBConstant.KEY_MODE;
import static ru.nwts.wherewe.database.DBConstant.KEY_NAME;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener,
        OnMapReadyCallback, DialogFragmentYesNo.DialogFragmentYesNoListener, DialogFragmentInputStr.DialogFragmentInputStrListener {

    //LOG
    public static final String TAG = "MyLogs";


    //Map
    LatLngBounds bounds;
    List<Marker> markers;
    private final String ACTION_MAPRECEIVER = "ru.nwts.wherewe.map";
    private final String ACTION_EDIT_ABONENT = "ru.nwts.wherewe.edit";
    private final int ACTION_DELETE = 0;
    private final int ACTION_EDIT_NAME = 1;
    //test
    int i;

    PreferenceHelper preferenceHelper;

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //view objects
    private TextView textViewUserEmail;
    private ImageButton buttonScale;

    private Drawer drawer = null;
    private Toolbar toolbar;

    private String[] channelNames;
    private int selectedDrawerItem = 0;

    FragmentManager manager;
    //
    private GoogleMap Map;
    public DBHelper dbHelper;
    //Db
    List<SmallModel> smallModels;


    //
    Intent locationService;

    //CONSTANT Shared Preference
    private final String KEY_ACTIVITY_READY = "PROF_ACTIVITY";
    private final String KEY_LOCATION_SERVICE_STARTED = "LOCATION_SERVICE";
    private final String KEY_EMAIL_SHARED_PREF = "EMAIL_SHARED_PREF";

    //date format
    SimpleDateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy' 'HH:mm:ss");
    private String wordTimeOnMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        manager = getSupportFragmentManager();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        firebaseAuth = TODOApplication.getFireBaseAuth();

        if (firebaseAuth.getCurrentUser() == null) {
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
        wordTimeOnMarker = getResources().getString(R.string.word_marker_time);

        channelNames = getResources().getStringArray(R.array.channel_names);
        PrimaryDrawerItem[] primaryDrawerItems = new PrimaryDrawerItem[channelNames.length];

        int[] drawableId = new int[]{R.drawable.ic_view_list_white_18dp,
                R.drawable.ic_add_box_white_18dp, R.drawable.ic_mail_white_18dp,
                R.drawable.ic_location_disabled_white_18dp, R.drawable.ic_zoom_out_map_white_18dp,
                R.drawable.ic_android_white_18dp, R.drawable.ic_settings_applications_white_18dp};


        for (int i = 0; i < channelNames.length; i++) {
            primaryDrawerItems[i] = new PrimaryDrawerItem()
                    .withName(channelNames[i])
                    .withIdentifier(i)
                    .withIcon(drawableId[i])
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
                                .withName(getString(R.string.exit))
                                .withIdentifier(channelNames.length - 1)
                                .withIcon(R.drawable.ic_exit_to_app_white_18dp)
                                .withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //
                        selectedDrawerItem = position;
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() >= 0 && selectedDrawerItem != -1) {
                                setToolbarAndSelectedDrawerItem(
                                        channelNames[selectedDrawerItem - 1],
                                        (selectedDrawerItem - 1)
                                );

                                Log.d(TAG, "Drwaer:selectedDrawerItem:" + selectedDrawerItem);

                                switch (selectedDrawerItem) {
                                    case 1:
                                        startActivity(new Intent(getApplicationContext(), RecyclerViews.class));
                                        break;
                                    case 2:
                                        DialogFragmentInputStr editNameDialogFragment = new DialogFragmentInputStr();
                                        editNameDialogFragment.show(manager, "fragment_edit_name");
                                        break;
                                    case 3:
                                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                        shareIntent.setType("text/plain");
                                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject));
                                        shareIntent.putExtra(Intent.EXTRA_TEXT, putEmailAndFireBasePathtoClient());
                                        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)));
                                        break;
                                    case 4:
                                        DialogFragmentYesNo dialogFragmentYesNo = DialogFragmentYesNo.newInstance(0, 0, getResources().getString(R.string.dialog_title_yes_no_logout));
                                        dialogFragmentYesNo.show(manager, "dialog");
                                        break;
                                    case 5: //масштабировать
                                        setScale();
                                        break;
                                    case 6: //About
                                        showAbout();
                                        break;
                                    case 7: //About
                                        showSettings();
                                        break;
                                    default:
                                        break;
                                }

                            } else if (selectedDrawerItem == -1) {
                                overridePendingTransition(R.anim.open_next, R.anim.close_main);
                                finish();
                            }
                        }
                        return false;
                        //
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();


        //displaying logged in user name
        textViewUserEmail.setText("Welcome " + user.getEmail());
        buttonScale = (ImageButton) findViewById(R.id.buttonProfileScale);

        //Works Databases
        //dbHelper = new DBHelper(this);
        dbHelper = TODOApplication.getInstance().dbHelper;
        if (dbHelper != null) {
            dbHelper.dbReadInLog();
            smallModels = dbHelper.getListSmallModel();
        }


        locationService = new Intent(ProfileActivity.this, DeviceLocationService.class);
        //Preference
        initPreferences();
        registerReceiver(this.broadcastReceiverEdit,
                new IntentFilter(ACTION_EDIT_ABONENT));

        //BroadCastReceiver register
        registerReceiver(this.broadcastReceiver,
                new IntentFilter(new IntentFilter(ACTION_MAPRECEIVER)));

        //Write to SharedPref what ProfActivity Started
        preferenceHelper.putBoolean(KEY_ACTIVITY_READY, true);

        buttonScale.setOnClickListener(this);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int Id = (int) intent.getIntExtra(KEY_ID, 0);
            double Latitude = (double) intent.getDoubleExtra(KEY_LATTITUDE, 0);
            double Longtitude = (double) intent.getDoubleExtra(KEY_LONGTITUDE, 0);
            long dateTime = (long) intent.getLongExtra(KEY_DATE, 0);
            Log.d(TAG, "broadcastReceiver:sendMessage:Id=" + Id + " Latitude:" + Latitude + " Longtitude:" + Longtitude);
            if (Id != 0 && Latitude != 0 && Longtitude != 0) {
                Log.d(TAG, "broadcastReceiver:sendMessage Run Broadcastreceiver:length:" + markers.size());
                if (Id == 1) {
                    markers.get(i).setPosition(new LatLng(Latitude, Longtitude));
                    if (dateTime != 0) {
                        markers.get(i).setSnippet(wordTimeOnMarker + dateformat.format(dateTime));
                    }
                } else {
                    //others marker
                    for (int m = 0; m < smallModels.size(); m++) {
                        Log.d(TAG, "broadcastReceiver:sendMessage Run Broadcastreceiver:m:" + m);
                        Log.d(TAG, "broadcastReceiver:sendMessage Run Broadcastreceiver:smallModels.get(m).getId():" + smallModels.get(m).getId());
                        Log.d(TAG, "broadcastReceiver:sendMessage Run Broadcastreceiver:markers.get(m).getId():" + markers.get(m).getId());
                        if (smallModels.get(m).getId() == Id) {
                            markers.get(m).setPosition(new LatLng(Latitude, Longtitude));
                            if (dateTime != 0) {
                                markers.get(m).setSnippet(wordTimeOnMarker + dateformat.format(dateTime));
                            }
                        }
                    }
                }
            }
        }
    };


    private BroadcastReceiver broadcastReceiverEdit = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "sendBroadcast():sendBroadCastEditAbonent:ProfileActivity");
            int Id = (int) intent.getIntExtra(KEY_ID, 0);
            int action = (int) intent.getIntExtra(KEY_MODE, 1);
            String name = (String) intent.getStringExtra(KEY_NAME);
            //edit name
            if (action == ACTION_EDIT_NAME) {
                for (int m = 0; m < smallModels.size(); m++) {
                    if (smallModels.get(m).getId() == Id) {
                        markers.get(m).setTitle(name);
                        smallModels.get(m).setName(name);
                    }
                }
            }
            if (action == ACTION_DELETE) {
                for (int m = 0; m < smallModels.size(); m++) {
                    if (smallModels.get(m).getId() == Id) {
                        markers.get(m).remove();
                    }
                }
            }
        }
    };

    private void setToolbarAndSelectedDrawerItem(String title, int selectedDrawerItem) {
        toolbar.setTitle(title);
        drawer.setSelection(selectedDrawerItem, false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (smallModels != null && markers != null) {
            for (int m = 0; m < smallModels.size(); m++) {
                Log.d(TAG, "broadcastReceiver:sendMessage Run Broadcastreceiver:m:" + m);
                Log.d(TAG, "broadcastReceiver:sendMessage Run Broadcastreceiver:smallModels.get(m).getId():" + smallModels.get(m).getId());
                Log.d(TAG, "broadcastReceiver:sendMessage Run Broadcastreceiver:markers.get(m).getId():" + markers.get(m).getId());
            }
        }

        //Write to SharedPref what ProfActivity Started
        preferenceHelper.putBoolean(KEY_ACTIVITY_READY, true);
        //Tested Run LocationService Or Not
        if (!preferenceHelper.getBoolean(KEY_LOCATION_SERVICE_STARTED)) {
            //Тест координат
            Log.d(TAG, "Start service! from ProfileActivity!");
        }
        startService(locationService);
    }


    @Override
    protected void onPause() {
        //Write to SharedPref what ProfActivity Started
        Log.d(TAG, "ProfileActivity onPaiused..");
        super.onPause();
    }

    private void initPreferences() {
        //initializing preference
        PreferenceHelper.getInstance().init(getApplicationContext());
        preferenceHelper = PreferenceHelper.getInstance();
    }

    @Override
    public void onClick(View view) {
        if (view == buttonScale) {
            setScale();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_option, menu);
        //This visible icon on menu, google design hide icon on menu
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuPref:
                showSettings();
                return true;
            case R.id.menuAbout:
                showAbout();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Map = googleMap;

        if (Map == null) {
            Toast.makeText(getApplicationContext(),
                    "Error creating map", Toast.LENGTH_SHORT).show();
            return;
        }

        if (smallModels == null) {
            Toast.makeText(getApplicationContext(),
                    "Error SmallModel empty Error!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Map
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        Map.getUiSettings().setZoomControlsEnabled(true);
        Map.getUiSettings().setCompassEnabled(true);
        //Check permission
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        markers = new ArrayList<>();
        for (int j = 0; j < smallModels.size(); j++) {
            SmallModel model = smallModels.get(j);
            String snippets = wordTimeOnMarker + dateformat.format(model.getTrack_date());
            if (model.getId() == 1) {
                i = j;
                Log.d(TAG, "addMarker:Name:" + model.getName() + ":getLongtitude:" + model.getLongtitude() + " getLattitude:" + model.getLattitude());
                markers.add(j, Map.addMarker(new MarkerOptions().position(new LatLng(model.getLattitude(), model.getLongtitude()))
                        .title(model.getName()).snippet(snippets).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));
            } else {
                markers.add(j, Map.addMarker(new MarkerOptions().position(new LatLng(model.getLattitude(),
                        model.getLongtitude())).title(model.getName()).snippet(snippets)));
            }
            builder.include(markers.get(j).getPosition());
            bounds = builder.build();
            Map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    int padding = 10; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    //Map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
                    Map.moveCamera(cu);
                }
            });
        }
    }


    private void setScale() {
        //Map
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int j = 0; j < markers.size(); j++) {
            builder.include(markers.get(j).getPosition());
        }
        bounds = builder.build();
        Map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                int padding = 10; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                //Map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
                Map.moveCamera(cu);
            }
        });
    }

    public void showSettings() {
        Intent intent = new Intent(ProfileActivity.this, PreferenceActivities.class);
        startActivityForResult(intent, 0);
    }

    private String putEmailAndFireBasePathtoClient() {
        String s = dbHelper.getEmailPartFBasePartFromMe();
        byte[] bs = s.getBytes();
        String strEncoded = "00099999" + Base64.encodeToString(bs, Base64.DEFAULT);
        return strEncoded;
    }

    private String getEmailAndFireBasePathtoClient(String strEncoded) {
        String email;
        String part_email;
        String fbase_part;
        String strDecoded;
        try {
            strDecoded = new String(Base64.decode(strEncoded.getBytes(), Base64.DEFAULT));
        } catch (IllegalArgumentException error) {
            strDecoded = "";
            Log.e(TAG, error.toString());
        }
        return strDecoded;
    }

    private String getEmailFromDecoded(String strDecoded) {
        String email = strDecoded.substring(0, strDecoded.indexOf(";"));
        return email;
    }

    //Attention strDecoded include all data from decoded string
    private String getPartEmailFromDecoded(String strDecoded) {
        String s = strDecoded.substring(strDecoded.indexOf(";") + 1, strDecoded.length());
        String part_email = s.substring(0, s.indexOf(";"));
        return part_email;
    }

    //Attention strDecoded include all data from decoded string
    private String getFireBasePathFromDecoded(String strDecoded) {
        String s = strDecoded.substring(strDecoded.indexOf(";") + 1, strDecoded.length());
        String fbase_part = s.substring(s.indexOf(";") + 1, s.length());
        Log.d(TAG, "EmailAndFireBasePathtoClient:fbase_path:22:" + fbase_part);
        return fbase_part;
    }

    private boolean isEmailValidation(String email) {
        try {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } catch (NullPointerException exception) {
            return false;
        }
    }

    private boolean isPartEmailValidation(String part_email) {
        if (part_email != null && !part_email.isEmpty() && part_email.length() > 5) {
            return true;
        } else {
            return false;

        }
    }

    private boolean isFireBasePathValidation(String fbase_path) {
        if (fbase_path != null && !fbase_path.isEmpty() && fbase_path.length() > 20) {
            Log.d(TAG, "putInputStrNewSendInformation:" + fbase_path);
            Log.d(TAG, "putInputStrNewSendInformation:" + fbase_path.indexOf("99999"));
            if (fbase_path.indexOf("99999") > 0) {
                try {
                    new String(Base64.decode(fbase_path.substring(8, fbase_path.length()).getBytes(), Base64.DEFAULT));
                } catch (IllegalArgumentException error) {
                    Log.e(TAG, error.toString());
                    return false;
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;

        }
    }

    public void showAbout() {
        String email;
        String part_email;
        String fbase_part;
        /*
        This test code begin
         */
        Log.d(TAG, "EmailAndFireBasePathtoClient:" + putEmailAndFireBasePathtoClient());
        Log.d(TAG, "EmailAndFireBasePathtoClient:" + getEmailAndFireBasePathtoClient(putEmailAndFireBasePathtoClient()));
        Log.d(TAG, "EmailAndFireBasePathtoClient:email:" + getEmailFromDecoded(getEmailAndFireBasePathtoClient(putEmailAndFireBasePathtoClient())));
        if (isEmailValidation(getEmailFromDecoded(getEmailAndFireBasePathtoClient(putEmailAndFireBasePathtoClient())))) {
            Log.d(TAG, "EmailAndFireBasePathtoClient:is email:mathes");
        }
        if (isPartEmailValidation(getPartEmailFromDecoded(getEmailAndFireBasePathtoClient(putEmailAndFireBasePathtoClient())))) {
            Log.d(TAG, "EmailAndFireBasePathtoClient:part_email:" + getPartEmailFromDecoded(getEmailAndFireBasePathtoClient(putEmailAndFireBasePathtoClient())));
        }
        if (isFireBasePathValidation(getFireBasePathFromDecoded(getEmailAndFireBasePathtoClient(putEmailAndFireBasePathtoClient())))) {
            Log.d(TAG, "EmailAndFireBasePathtoClient:fbase_path:" + getFireBasePathFromDecoded(getEmailAndFireBasePathtoClient(putEmailAndFireBasePathtoClient())));
        }

        Log.d(TAG, "showAbout:" + dbHelper.getEmailPartFBasePartFromMe());
        String s = dbHelper.getEmailPartFBasePartFromMe();
        byte[] bs = s.getBytes();
        String strEncoded = Base64.encodeToString(bs, Base64.DEFAULT);
        Log.d(TAG, "showAbout:base64 encoded:" + strEncoded);
        s = new String(Base64.decode(strEncoded.getBytes(), Base64.DEFAULT));
        Log.d(TAG, "showAbout:base64 decoded:" + s);
        email = s.substring(0, s.indexOf(";"));
        Log.d(TAG, "showAbout:email:" + email);
        s = s.substring(s.indexOf(";") + 1, s.length());
        Log.d(TAG, "showAbout:s:" + s);
        part_email = s.substring(0, s.indexOf(";"));
        Log.d(TAG, "showAbout:part_email:" + part_email);
        fbase_part = s.substring(s.indexOf(";") + 1, s.length());
        Log.d(TAG, "showAbout:Fbase_part:" + fbase_part);
        if (!dbHelper.checkExistClient(email, part_email)) {
            dbHelper.dbInsertUser("", 0, 0, 0, 0, 0, preferenceHelper.getLong("Time"), Double.longBitsToDouble(preferenceHelper.getLong("Longtitude")),
                    Double.longBitsToDouble(preferenceHelper.getLong("Latitude")), fbase_part, null, 0, 999, "i123456789", "o123456789", email, part_email);
        }
        /*
        thies end of test code
         */
        Intent intent = new Intent(ProfileActivity.this, About.class);
        startActivity(intent);
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int id, int position) {
        Toast.makeText(getApplicationContext(), "Вы выбрали кнопку OK!",
                Toast.LENGTH_LONG).show();
        firebaseAuth.signOut();
        //closing activity
        finish();
        //starting login activity
        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Toast.makeText(getApplicationContext(), "Вы выбрали кнопку отмены!",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        if (!inputText.isEmpty() && inputText.length() > 20) {
            if (!putInputStrNewSendInformation(inputText)) {
                Toast.makeText(getApplicationContext(), "Привет, введенная строка не проходит проверку!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Привет, вы ничего не ввели!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        preferenceHelper.putBoolean(KEY_ACTIVITY_READY, false);
        try {
            this.unregisterReceiver(broadcastReceiverEdit);
        } catch (IllegalArgumentException e) {
        }
        try {
            this.unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
        }
        super.onDestroy();
    }

    private boolean putInputStrNewSendInformation(String sendMessage) {
        String email;
        String part_email;
        String fbase_part;

        if (isFireBasePathValidation(sendMessage)) {
            sendMessage = getEmailAndFireBasePathtoClient(sendMessage.substring(8, sendMessage.length()));
            Log.d(TAG, "EmailAndFireBasePathtoClient: " + sendMessage);

            if (isEmailValidation(getEmailFromDecoded(sendMessage))) {
                email = getEmailFromDecoded(sendMessage).trim();
                Log.d(TAG, "EmailAndFireBasePathtoClient:is email:mathes : " + email);
            } else {
                return false;
            }
            if (isPartEmailValidation(getPartEmailFromDecoded(sendMessage))) {
                part_email = getPartEmailFromDecoded(sendMessage).trim();
                Log.d(TAG, "EmailAndFireBasePathtoClient:part_email:" + part_email);
            } else {
                return false;
            }
            if ((getFireBasePathFromDecoded(sendMessage).length() > 20)) {
                fbase_part = getFireBasePathFromDecoded(sendMessage).trim();
                Log.d(TAG, "EmailAndFireBasePathtoClient:fbase_path:" + fbase_part);
            } else {
                return false;
            }
            if (!dbHelper.checkExistClient(email, part_email)) {
                if (dbHelper.dbInsertUser("", 0, 0, 0, 0, 0, preferenceHelper.getLong("Time"), Double.longBitsToDouble(preferenceHelper.getLong("Longtitude")),
                        Double.longBitsToDouble(preferenceHelper.getLong("Latitude")), fbase_part, null, 0, 999, "i123456789", "o123456789", email, part_email) > 1) {
                    Toast.makeText(getApplicationContext(), "Запись добавлена!", Toast.LENGTH_SHORT).show();
                    //Here function add new marker
                    if (dbHelper.getSmallModelFromEmail(email).getId() != 0) {
                        smallModels.add(dbHelper.getSmallModelFromEmail(email));
                        markers.add(Map.addMarker(new MarkerOptions().position(new LatLng(dbHelper.getSmallModelFromEmail(email).getLattitude(),
                                dbHelper.getSmallModelFromEmail(email).getLongtitude())).title(dbHelper.getSmallModelFromEmail(email).getName())));
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка добавления записи!", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (dbHelper.dbUpdateFBase(email, part_email, fbase_part) == 1) {
                    Toast.makeText(getApplicationContext(), "Запись обновлена!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка обновления записи!", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        } else {
            Log.d(TAG, "putInputStrNewSendInformation: error in str!");
        }
        return false;
    }

}
