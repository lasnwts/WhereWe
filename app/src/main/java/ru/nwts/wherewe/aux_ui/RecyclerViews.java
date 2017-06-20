package ru.nwts.wherewe.aux_ui;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.List;
import java.util.Map;

import ru.nwts.wherewe.ProfileActivity;
import ru.nwts.wherewe.R;
import ru.nwts.wherewe.TODOApplication;
import ru.nwts.wherewe.adapter.adapterClickListener;
import ru.nwts.wherewe.adapter.adapterSmallModel;
import ru.nwts.wherewe.database.DBHelper;
import ru.nwts.wherewe.fragments.dialog.DialogFragmentInputStr;
import ru.nwts.wherewe.model.SmallModel;
import ru.nwts.wherewe.fragments.dialog.DialogFragmentOneItem;
import ru.nwts.wherewe.fragments.dialog.DialogFragmentYesNo;
import ru.nwts.wherewe.settings.Constants;
import ru.nwts.wherewe.util.PreferenceHelper;

import static ru.nwts.wherewe.database.DBConstant.KEY_ID;
import static ru.nwts.wherewe.database.DBConstant.KEY_MODE;
import static ru.nwts.wherewe.database.DBConstant.KEY_NAME;

public class RecyclerViews extends AppCompatActivity implements DialogFragmentOneItem.EditingTaskListener,
        DialogFragmentYesNo.DialogFragmentYesNoListener, DialogFragmentInputStr.DialogFragmentInputStrListener {

    //LOG
    public static final String TAG = "MyLogs";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    // private RecyclerView.Adapter adapter;
    private adapterSmallModel adapter;

    private List<SmallModel> smallModels;
    private final String ACTION_EDIT_ABONENT = "ru.nwts.wherewe.edit";
    private final int ACTION_DELETE = 0;
    private final int ACTION_EDIT_NAME = 1;
    public DBHelper dbHelper;
    private Toolbar toolbar;
    private String[] channelNames;
    private int selectedDrawerItem = 0;
    PreferenceHelper preferenceHelper;
    private Drawer drawer = null;
    FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_views);

        //initializing views
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        manager = getSupportFragmentManager();

        dbHelper = TODOApplication.getInstance().dbHelper;

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        smallModels = dbHelper.getListSmallModel();
        loadRecyclerViewItem();

        channelNames = getResources().getStringArray(R.array.channel_names_RecyclerViewsActivity);
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
                .withActivity(RecyclerViews.this)
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
                                        finish();
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
                                Intent intent = new Intent();
                                intent.putExtra(Constants.EXIT_POOL, true);
                                setResult(RESULT_OK, intent);
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

    }

    private void loadRecyclerViewItem() {

        adapter = new adapterSmallModel(smallModels, this, new adapterClickListener() {
            @Override
            public void adapterOnClickListener(int item) {
                Intent intent = new Intent(RecyclerViews.this, EditAbonentProperty.class);
                intent.putExtra(Constants.ABONENT_ITEM,item);
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

    }

    private void sendBroadCastEditAbonent(int id, String name, int action) {
        Intent intent = new Intent();
        intent.setAction(ACTION_EDIT_ABONENT);
        intent.putExtra(KEY_ID, id);
        intent.putExtra(KEY_NAME, name);
        intent.putExtra(KEY_MODE, action);
        //intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        Log.d(TAG, "sendMessage:sendBroadCastEditAbonent:send from Recycler");
        sendBroadcast(intent);
    }

    @Override
    public void onTaskEdited(SmallModel updatedTask, int position, int id) {
        Log.d(TAG, "onDialogPositiveClick:onTaskEdited:getId()" + id + " position:" + position);
        if (dbHelper.dbUpdateName(id, updatedTask.getName()) == 1) {
            Toast.makeText(this, R.string.update_done, Toast.LENGTH_SHORT).show();
            adapter.setNotifyDataChange(updatedTask.getName(), position);
            sendBroadCastEditAbonent(id, updatedTask.getName(), ACTION_EDIT_NAME);
        } else {
            Toast.makeText(this, R.string.update_error, Toast.LENGTH_SHORT).show();
        }
        Log.v(TAG, "adapterSmallModel: email updated: =" + updatedTask.getEmail());
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int id, int position) {
        Log.d(TAG, "onDialogPositiveClick:position=" + position + "; id=" + id + " ;item count=" + adapter.getItemCount());
        //Yes delete record
        if (id > 1) {
            if (dbHelper.dbDeleteUser(id) > 0) {
                Toast.makeText(this, R.string.delete_completed, Toast.LENGTH_SHORT).show();
                adapter.setNotifyItemRemoved(position);
                sendBroadCastEditAbonent(id, "deleted", ACTION_DELETE);
            } else {
                Toast.makeText(this, R.string.delete_attention, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.delete_attention, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //none todo`
    }

    public void showAbout() {
        String email;
        String part_email;
        String fbase_part;
        /*
        This test code begin
         */

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
        Intent intent = new Intent(RecyclerViews.this, About.class);
        startActivity(intent);
    }

    public void showSettings() {
        Intent intent = new Intent(RecyclerViews.this, PreferenceActivities.class);
        startActivityForResult(intent, 0);
    }

    private void setToolbarAndSelectedDrawerItem(String title, int selectedDrawerItem) {
        toolbar.setTitle(title);
        drawer.setSelection(selectedDrawerItem, false);
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

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
        super.onPause();
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

    private boolean isPartEmailValidation(String part_email) {
        if (part_email != null && !part_email.isEmpty() && part_email.length() > 5) {
            return true;
        } else {
            return false;

        }
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_option_rv, menu);
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


}

