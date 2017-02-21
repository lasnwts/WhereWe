package ru.nwts.wherewe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import static ru.nwts.wherewe.R.id.map;
import static ru.nwts.wherewe.TODOApplication.*;
import static ru.nwts.wherewe.database.DBConstant.KEY_ID;
import static ru.nwts.wherewe.database.DBConstant.KEY_LATTITUDE;
import static ru.nwts.wherewe.database.DBConstant.KEY_LONGTITUDE;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener,
        OnMapReadyCallback, DialogFragmentYesNo.DialogFragmentYesNoListener, DialogFragmentInputStr.DialogFragmentInputStrListener {

    //LOG
    public static final String TAG = "MyLogs";

    //Map
    LatLngBounds bounds;
    List<Marker> markers;
    private final String ACTION_MAPRECEIVER = "ru.nwts.wherewe.map";
    //test
    int i;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        manager = getSupportFragmentManager();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
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
                                        shareIntent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.subject));
                                        shareIntent.putExtra(Intent.EXTRA_TEXT, putEmailAndFireBasePathtoClient());
                                        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)));
                                        break;
                                    case 4:
                                        DialogFragmentYesNo dialogFragmentYesNo = DialogFragmentYesNo.newInstance(0,0);
                                        dialogFragmentYesNo.show(manager, "dialog");
                                        break;
                                    default:
                                        break;
                                }

                            } else if (selectedDrawerItem == -1) {
                                showAbout();
                                overridePendingTransition(R.anim.open_next, R.anim.close_main);
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

        //Works Databases
        //dbHelper = new DBHelper(this);
        dbHelper = TODOApplication.getInstance().dbHelper;
        if (dbHelper != null) {
            dbHelper.dbDeleteUsers();
            dbHelper.dbInsertUser("Test0 name_115", 1, 1, 1, 1, 1, 198299922, 30.3667, 59.8793, "M0erubbTS6hbInqmOmnZOPelZfE2", null, 0, 999, "i123456789", "o123456789", "test1@mail.ru", "076077669");
            dbHelper.dbInsertUser("Test1 name_1", 1, 1, 1, 1, 1, 198299922, 30.3467,  59.8693, "Fkq0Hze0sXgatHf0dsnkD0gTGiO2", null, 0, 999, "067", "o123456789", "alexl1967@mail.ru", "067");
            dbHelper.dbInsertUser("Test2 name_3", 1, 1, 1, 1, 1, 198299922, 31.8350976, 58.9342803, "c6yJ7FyUUwPHsCKGq4IvtkEZ93f1", null, 0, 999, "i123456789", "o123456789", "atest@mail.ru", "0979799");
//            dbHelper.dbInsertUser("Test3 2 name_115", 1, 1, 1, 1, 1, 198299922, 32.6750986, 60.1055801, "M0erubb9976hbInqmOmnZOPelZfE2", null, 0, 999, "i123456789", "o123456789", "test1@mail.ru", "076077669");
//            dbHelper.dbInsertUser("Test4 2 name_1", 1, 1, 1, 1, 1, 198299922, 32.1450981, 59.9000801, "Fkq99ze0sXgatHf0dsnkD0gTGiO2", null, 0, 999, "067", "o123456789", "alexl1967@mail.ru", "067");
//            dbHelper.dbInsertUser("Test5 2 name_3", 1, 1, 1, 1, 1, 198299922, 31.3350980, 60.9972887, "c8yJ7FyUUwPHsCKGq4IvtkEZ93f1", null, 0, 999, "i123456789", "o123456789", "atest@mail.ru", "0979799");
//            dbHelper.dbInsertUser("Test6  3name_115", 1, 1, 1, 1, 1, 198299922, 30.8950978, 59.9072801, "M0erubbT77hbInqmOmnZOPelZfE2", null, 0, 999, "i123456789", "o123456789", "test1@mail.ru", "076077669");
//            dbHelper.dbInsertUser("Test7 4 name_1", 1, 1, 1, 1, 1, 198299922, 33.0950986, 59.8762564, "Fkq0Hze0sXg77Hf0dsnkD0gTGiO2", null, 0, 999, "067", "o123456789", "alexl1967@mail.ru", "067");
//            dbHelper.dbInsertUser("Test8  5name_3", 1, 1, 1, 1, 1, 198299922, 31.9890990, 59.0942801, "c68J7FyUUwPH7CKGq4IvtkEZ93f1", null, 0, 999, "i123456789", "o123456789", "atest@mail.ru", "0979799");
            dbHelper.dbReadInLog();
            smallModels = dbHelper.getListSmallModel();
        }


        locationService = new Intent(ProfileActivity.this, DeviceLocationService.class);
        //Preference
        initPreferences();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int Id = (int) intent.getIntExtra(KEY_ID,0);
            double Latitude = (double) intent.getDoubleExtra(KEY_LATTITUDE,0);
            double Longtitude = (double) intent.getDoubleExtra(KEY_LONGTITUDE,0);
            Log.d(TAG,"sendMessage:Id="+Id+" Latitude:"+Latitude+" Longtitude:"+Longtitude);
            if (Id != 0 && Latitude != 0 && Longtitude != 0){
                Log.d(TAG,"sendMessage Run Broadcastreceiver");
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
        //Write to SharedPref what ProfActivity Started
        preferenceHelper.putBoolean(KEY_ACTIVITY_READY, true);
        //Tested Run LocationService Or Not
        if (!preferenceHelper.getBoolean(KEY_LOCATION_SERVICE_STARTED)) {
            //Тест координат
            Log.d(TAG, "Start service! from ProfileActivity!");
        }
        startService(locationService);
        //BroadCastReceiver register
        registerReceiver(this.broadcastReceiver,
                new IntentFilter(new IntentFilter(ACTION_MAPRECEIVER)));
    }


    @Override
    protected void onPause() {
        //Write to SharedPref what ProfActivity Started
        Log.d(TAG, "ProfileActivity onPaiused..");
        preferenceHelper.putBoolean(KEY_ACTIVITY_READY, false);
        try {
            this.unregisterReceiver(broadcastReceiver);
            Toast.makeText(getApplicationContext(), "Приёмник автоматически выключён", Toast.LENGTH_LONG)
                    .show();
        }catch(IllegalArgumentException e){
            Toast.makeText(getApplicationContext(), "Приёмник не был выключён", Toast.LENGTH_LONG)
                    .show();
        }
        super.onPause();
    }

    private void initPreferences() {
        //initializing preference
        PreferenceHelper.getInstance().init(getApplicationContext());
        preferenceHelper = PreferenceHelper.getInstance();
    }

    @Override
    public void onClick(View view) {
//        //if logout is pressed
//        if (view == buttonLogout) {
//            //logging out the user
//            firebaseAuth.signOut();
//            //closing activity
//            finish();
//            //starting login activity
//            startActivity(new Intent(this, LoginActivity.class));
//        }
//        if (view == buttonService) {
//            startService(locationService);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_option, menu);
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
        markers = new ArrayList<>();
        for (int j = 0; j < smallModels.size(); j++) {
            SmallModel model = smallModels.get(j);
            if (model.getId()==1){
                i = j;
                Log.d(TAG,"addMarker:Name:"+model.getName()+":getLongtitude:"+model.getLongtitude()+" getLattitude:"+model.getLattitude());
                markers.add(j, Map.addMarker(new MarkerOptions().position(new LatLng(model.getLattitude(), model.getLongtitude()))
                        .title(model.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));
            }else {
                markers.add(j, Map.addMarker(new MarkerOptions().position(new LatLng(model.getLattitude(), model.getLongtitude())).title(model.getName())));
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

    public void showSettings() {
        markers.get(i).setPosition(new LatLng(59.10002, 31.19086));
        Map.moveCamera(CameraUpdateFactory.newLatLng(markers.get(i).getPosition()));
//        Intent intent = new Intent(ProfileActivity.this, PreferenceActivities.class);
//        startActivityForResult(intent, 0);
    }

    private String putEmailAndFireBasePathtoClient() {
        //Log.d(TAG, "EmailAndFireBasePathtoClient:" + dbHelper.getEmailPartFBasePartFromMe());
        String s = dbHelper.getEmailPartFBasePartFromMe();
        byte[] bs = s.getBytes();
        String strEncoded = "00099999" + Base64.encodeToString(bs, Base64.DEFAULT);
        //Log.d(TAG, "EmailAndFireBasePathtoClient:base64 encoded:" + strEncoded);
        return strEncoded;
    }

    private String getEmailAndFireBasePathtoClient(String strEncoded) {
        String email;
        String part_email;
        String fbase_part;
        String strDecoded;
        try{
            strDecoded = new String(Base64.decode(strEncoded.getBytes(), Base64.DEFAULT));
        } catch (IllegalArgumentException error){
            strDecoded = "";
            Log.e(TAG, error.toString());
        }
        //Log.d(TAG, "EmailAndFireBasePathtoClient:base64 decoded:" + strDecoded);
        return strDecoded;
    }

    private String getEmailFromDecoded(String strDecoded) {
        String email = strDecoded.substring(0, strDecoded.indexOf(";"));
        return email;
    }

    //Attention strDecoded include all data from decoded string
    private String getPartEmailFromDecoded(String strDecoded){
        String s = strDecoded.substring(strDecoded.indexOf(";") + 1, strDecoded.length());
        String part_email = s.substring(0, s.indexOf(";"));
        return part_email;
    }

    //Attention strDecoded include all data from decoded string
    private String getFireBasePathFromDecoded(String strDecoded){
        String s = strDecoded.substring(strDecoded.indexOf(";") + 1, strDecoded.length());
        String fbase_part = s.substring(s.indexOf(";") + 1, s.length());
        Log.d(TAG, "EmailAndFireBasePathtoClient:fbase_path:22:"+fbase_part);
        return fbase_part;
    }

    private boolean isEmailValidation(String email) {
        try {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } catch (NullPointerException exception){
            return false;
        }
    }

    private boolean isPartEmailValidation(String part_email){
        if (part_email != null && !part_email.isEmpty() && part_email.length() > 5){
            return true;
        } else {
            return false;

        }
    }

    private boolean isFireBasePathValidation(String fbase_path){
        if (fbase_path != null && !fbase_path.isEmpty() && fbase_path.length() > 20){
            Log.d(TAG,"putInputStrNewSendInformation:"+fbase_path);
            Log.d(TAG,"putInputStrNewSendInformation:"+fbase_path.indexOf("99999"));
            if (fbase_path.indexOf("99999")>0){
                try{
                    new String(Base64.decode(fbase_path.substring(8,fbase_path.length()).getBytes(), Base64.DEFAULT));
                } catch (IllegalArgumentException error){
                    Log.e(TAG, error.toString());
                    return  false;
                }
                return true;
            } else {
                return  false;
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
        if (isEmailValidation(getEmailFromDecoded(getEmailAndFireBasePathtoClient(putEmailAndFireBasePathtoClient())))){
            Log.d(TAG, "EmailAndFireBasePathtoClient:is email:mathes");
        }
        if (isPartEmailValidation(getPartEmailFromDecoded(getEmailAndFireBasePathtoClient(putEmailAndFireBasePathtoClient())))){
            Log.d(TAG, "EmailAndFireBasePathtoClient:part_email:"+getPartEmailFromDecoded(getEmailAndFireBasePathtoClient(putEmailAndFireBasePathtoClient())));
        }
        if (isFireBasePathValidation(getFireBasePathFromDecoded(getEmailAndFireBasePathtoClient(putEmailAndFireBasePathtoClient())))){
            Log.d(TAG, "EmailAndFireBasePathtoClient:fbase_path:"+getFireBasePathFromDecoded(getEmailAndFireBasePathtoClient(putEmailAndFireBasePathtoClient())));
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
        if (!inputText.isEmpty() && inputText.length() > 20){
           // Toast.makeText(getApplicationContext(), "Привет, введено: " + inputText, Toast.LENGTH_SHORT).show();
            if (!putInputStrNewSendInformation(inputText)){
                Toast.makeText(getApplicationContext(), "Привет, введенная строка не проходит проверку!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Привет, вы ничего не ввели!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean putInputStrNewSendInformation(String sendMessage){
        String email;
        String part_email;
        String fbase_part;

        if (isFireBasePathValidation(sendMessage)){
            sendMessage = getEmailAndFireBasePathtoClient(sendMessage.substring(8,sendMessage.length()));
            Log.d(TAG,"EmailAndFireBasePathtoClient: "+sendMessage);

            if (isEmailValidation(getEmailFromDecoded(sendMessage))){
                email = getEmailFromDecoded(sendMessage).trim();
                Log.d(TAG, "EmailAndFireBasePathtoClient:is email:mathes : " + email);
            } else {
                return false;
            }
            if (isPartEmailValidation(getPartEmailFromDecoded(sendMessage))){
                part_email = getPartEmailFromDecoded(sendMessage).trim();
                Log.d(TAG, "EmailAndFireBasePathtoClient:part_email:"+part_email);
            } else {
                return false;
            }
            if ((getFireBasePathFromDecoded(sendMessage).length()>20)){
                fbase_part = getFireBasePathFromDecoded(sendMessage).trim();
                Log.d(TAG, "EmailAndFireBasePathtoClient:fbase_path:"+fbase_part);
            } else {
                return false;
            }
            if (!dbHelper.checkExistClient(email, part_email)) {
                if (dbHelper.dbInsertUser("", 0, 0, 0, 0, 0, preferenceHelper.getLong("Time"), Double.longBitsToDouble(preferenceHelper.getLong("Longtitude")),
                        Double.longBitsToDouble(preferenceHelper.getLong("Latitude")), fbase_part, null, 0, 999, "i123456789", "o123456789", email, part_email)==1){
                    Toast.makeText(getApplicationContext(), "Запись добавлена!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка добавления записи!", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (dbHelper.dbUpdateFBase(email, part_email, fbase_part)==1){
                    Toast.makeText(getApplicationContext(), "Запись обновлена!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Ошибка обновления записи!", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        } else {
            Log.d(TAG,"putInputStrNewSendInformation: error in str!");        }
        return false;
    }

}
