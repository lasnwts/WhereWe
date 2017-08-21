package ru.nwts.wherewe.aux_ui;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import ru.nwts.wherewe.R;
import ru.nwts.wherewe.TODOApplication;
import ru.nwts.wherewe.database.DBHelper;
import ru.nwts.wherewe.fragments.EditAbonentFragment;
import ru.nwts.wherewe.model.SmallModel;
import ru.nwts.wherewe.settings.Constants;
import ru.nwts.wherewe.util.PreferenceHelper;

import static android.R.attr.id;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static ru.nwts.wherewe.database.DBConstant.KEY_ID;
import static ru.nwts.wherewe.database.DBConstant.KEY_MODE;
import static ru.nwts.wherewe.database.DBConstant.KEY_NAME;
import static ru.nwts.wherewe.settings.Constants.ACTION_EDIT_ABONENT;

public class EditAbonentProperty extends AppCompatActivity implements EditAbonentFragment.SaveListener{

    private Toolbar toolbar;
    public DBHelper dbHelper;
    PreferenceHelper preferenceHelper;
    EditAbonentFragment mEditAbonentFragment;
    private final int ACTION_EDIT_NAME = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_abonent_property);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (null != toolbar) {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary_color, getTheme())));
                }else {
                    toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary_color)));
                }
            } else {
                toolbar.setBackgroundColor(getResources().getColor(R.color.primary_color));
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(R.string.pref_setting);
            toolbar.inflateMenu(R.menu.menu_option_rv);
        }
        dbHelper = TODOApplication.getInstance().dbHelper;
        //
        Intent intent = getIntent();
        TODOApplication.getInstance().setEmail(intent.getStringExtra(Constants.ABONENT_ITEM));
        showFragmentEditAbonent();
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

    public void showSettings() {
        Intent intent = new Intent(EditAbonentProperty.this, PreferenceActivities.class);
        startActivityForResult(intent, 0);
    }

    public void showAbout() {
        String email;
        String part_email;
        String fbase_part;
        /*
        This test code begin
         */

        String s = dbHelper.getEmailPartFBasePartFromMe();
        byte[] bs = s.getBytes();
        String strEncoded = Base64.encodeToString(bs, Base64.DEFAULT);
        s = new String(Base64.decode(strEncoded.getBytes(), Base64.DEFAULT));
        email = s.substring(0, s.indexOf(";"));
        s = s.substring(s.indexOf(";") + 1, s.length());
        part_email = s.substring(0, s.indexOf(";"));
        fbase_part = s.substring(s.indexOf(";") + 1, s.length());
        if (!dbHelper.checkExistClient(email, part_email)) {
            dbHelper.dbInsertUser("", 0, 0, 0, 0, 0, preferenceHelper.getLong("Time"), Double.longBitsToDouble(preferenceHelper.getLong("Longtitude")),
                    Double.longBitsToDouble(preferenceHelper.getLong("Latitude")), fbase_part, null, 0, 999, "i123456789", "o123456789", email, part_email);
        }
        /*
        thies end of test code
         */
        Intent intent = new Intent(EditAbonentProperty.this, About.class);
        startActivity(intent);
    }

    private void showFragmentEditAbonent(){
        if (mEditAbonentFragment == null) {
            mEditAbonentFragment = new EditAbonentFragment();
        }
        if (getFragmentManager().getBackStackEntryCount() != 0) {
            getFragmentManager().popBackStack();
        }
        getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .replace(R.id.content_frame, mEditAbonentFragment, Constants.TAG_EDIT_ABONENT_FRAGMENT).commit();
    }

    @Override
    public void onSaveAbonent(SmallModel updatedModel) {
        if (updatedModel == null) {
            return;
        }
        dbHelper.dbUpdateSmallModel(updatedModel);

        if (dbHelper.dbUpdateSmallModel(updatedModel) == 1) {
            Toast.makeText(this, R.string.update_done, Toast.LENGTH_SHORT).show();
            sendBroadCastEditAbonent(id, updatedModel.getName(), ACTION_EDIT_NAME);
        } else {
            Toast.makeText(this, R.string.update_error, Toast.LENGTH_SHORT).show();
        }
        /**
         * Очищаем шлак от карточки абоенента
         */
        if (preferenceHelper != null){
            preferenceHelper.putString(Constants.KEY_EDIT_EMAIL_ABONENT,"");
            preferenceHelper.putString(Constants.KEY_EDIT_NAME_ABONENT,"");
            preferenceHelper.putString(Constants.KEY_EDIT_FOTO_ABONENT,"");

        }
    }

    private void sendBroadCastEditAbonent(int id, String name, int action) {
        Intent intent = new Intent();
        intent.setAction(ACTION_EDIT_ABONENT);
        intent.putExtra(KEY_ID, id);
        intent.putExtra(KEY_NAME, name);
        intent.putExtra(KEY_MODE, action);
        //intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        Log.d(Constants.TAG, "sendMessage:sendBroadCastEditAbonent:send from Recycler");
        sendBroadcast(intent);
    }
}
