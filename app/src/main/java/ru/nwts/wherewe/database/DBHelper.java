package ru.nwts.wherewe.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import ru.nwts.wherewe.model.FbaseModel;
import ru.nwts.wherewe.model.ListFireBasePath;
import ru.nwts.wherewe.model.Model;
import ru.nwts.wherewe.model.ModelCheck;
import ru.nwts.wherewe.model.SmallModel;
import ru.nwts.wherewe.util.PreferenceHelper;

import static android.R.attr.id;
import static android.R.attr.languageTag;
import static android.R.attr.mode;
import static android.R.attr.name;
import static android.R.attr.version;


/**
 * Created by Надя on 03.12.2016.
 */

public class DBHelper extends SQLiteOpenHelper implements DBTables {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "whw_db";
    public static final int TABLE = 1;

    // создаем объект для данных
    ContentValues cv;
    PreferenceHelper preferenceHelper;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        initPreferences();
        db.execSQL(SQL_CREATE_ENTRIES_USERS);
        db.execSQL(SQL_CREATE_ENTRIES_GROUPS);
        db.execSQL(SQL_CREATE_ENTRIES_LINKS);
        db.execSQL(SQL_CREATE_ENTRIES_TRACKS);
        db.execSQL("insert into " + TABLE_USERS + "(" + KEY_NAME + "," + KEY_EMAIL + "," + KEY_PART_EMAIL +
                "," + KEY_DATE + "," + KEY_FBASE_PATH + "," + KEY_LATTITUDE + "," + KEY_LONGTITUDE + ")" +
                "VALUES('Input You Name','" + preferenceHelper.getString("login") + "'," + this.hashCode() + ",0,'fbasepathInstall',0,0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL("DROP TABLE " + TABLE_USERS);
            db.execSQL("DROP TABLE " + TABLE_GROUPS);
            db.execSQL("DROP TABLE " + TABLE_LINKS);
            db.execSQL("DROP TABLE " + TABLE_TRACKS);

            db.execSQL(SQL_CREATE_ENTRIES_USERS);
            db.execSQL(SQL_CREATE_ENTRIES_GROUPS);
            db.execSQL(SQL_CREATE_ENTRIES_LINKS);
            db.execSQL(SQL_CREATE_ENTRIES_TRACKS);
        }
    }

    //Insert record to Users
    public long dbInsertUsers(String name, int state, int mode, int rights,
                              long speed, int moved, long track_date, double longtitude, double lattitude,
                              String fbase_path, String fbase_old, int track_count, int track_count_allowed,
                              String key, String key_old, Object contact_id, String email, String part_email) {

        cv = new ContentValues();
        cv.put(KEY_NAME, name);
        cv.put(KEY_STATE, state);
        cv.put(KEY_MODE, mode);
        cv.put(KEY_RIGHTS, rights);
        cv.put(KEY_SPEED, speed);
        cv.put(KEY_MOVED, moved);
        cv.put(KEY_DATE, track_date);
        cv.put(KEY_LONGTITUDE, longtitude);
        cv.put(KEY_LATTITUDE, lattitude);
        cv.put(KEY_FBASE_PATH, fbase_path);
        cv.put(KEY_FBASE_OLD, fbase_old);
        cv.put(KEY_TRACK_COUNT, track_count);
        cv.put(KEY_TRACK_COUNT_ALLOWED, track_count_allowed);
        cv.put(KEY_ENCRYPTION, key);
        cv.put(KEY_ENCRYPTION_OLD, key_old);
        //cv.put(KEY_CONTACT_ID,contact_id);
        cv.put(KEY_EMAIL, email);
        cv.put(KEY_PART_EMAIL, part_email);


        long rowID = getWritableDatabase().insert(TABLE_USERS, null, cv);
        Log.d(TAG, "row inserted, ID =" + rowID);

        return rowID;
    }

    //Insert simple record to Users
    public long dbInsertUser(String name, int state, int mode, int rights,
                             long speed, int moved, long track_date, double longtitude, double lattitude,
                             String fbase_path, String fbase_old, int track_count, int track_count_allowed,
                             String key, String key_old, String email, String part_email) {

        cv = new ContentValues();
        cv.put(KEY_NAME, name);
        cv.put(KEY_STATE, state);
        cv.put(KEY_MODE, mode);
        cv.put(KEY_RIGHTS, rights);
        cv.put(KEY_SPEED, speed);
        cv.put(KEY_MOVED, moved);
        cv.put(KEY_DATE, track_date);
        cv.put(KEY_LONGTITUDE, longtitude);
        cv.put(KEY_LATTITUDE, lattitude);
        cv.put(KEY_FBASE_PATH, fbase_path);
        cv.put(KEY_FBASE_OLD, fbase_old);
        cv.put(KEY_TRACK_COUNT, track_count);
        cv.put(KEY_TRACK_COUNT_ALLOWED, track_count_allowed);
        cv.put(KEY_ENCRYPTION, key);
        cv.put(KEY_ENCRYPTION_OLD, key_old);
        cv.put(KEY_EMAIL, email);
        cv.put(KEY_PART_EMAIL, part_email);


        long rowID = getWritableDatabase().insert(TABLE_USERS, null, cv);
        Log.d(TAG, "row inserted, ID =" + rowID);

        return rowID;
    }

    //Update record to Users
    public int dbUpdateUsers(long rowID, String name, int state, int mode, int rights,
                             long speed, int moved, long track_date, long longtitude, long lattitude,
                             String fbase_path, String fbase_old, int track_count, int track_count_allowed,
                             String key, String key_old, int contact_id, String email, String part_email) {

        cv = new ContentValues();
        cv.put(KEY_NAME, name);
        cv.put(KEY_STATE, state);
        cv.put(KEY_MODE, mode);
        cv.put(KEY_RIGHTS, rights);
        cv.put(KEY_SPEED, speed);
        cv.put(KEY_MOVED, moved);
        cv.put(KEY_DATE, track_date);
        cv.put(KEY_LONGTITUDE, longtitude);
        cv.put(KEY_LATTITUDE, lattitude);
        cv.put(KEY_FBASE_PATH, fbase_path);
        cv.put(KEY_FBASE_OLD, fbase_old);
        cv.put(KEY_TRACK_COUNT, track_count);
        cv.put(KEY_TRACK_COUNT_ALLOWED, track_count_allowed);
        cv.put(KEY_ENCRYPTION, key);
        cv.put(KEY_ENCRYPTION_OLD, key_old);
        cv.put(KEY_CONTACT_ID, contact_id);
        cv.put(KEY_EMAIL, email);
        cv.put(KEY_PART_EMAIL, part_email);
        String where = KEY_ID + "=" + rowID;


        int updateResult = getWritableDatabase().update(TABLE_USERS, cv, where, null); //uodateResult - count of Updated record
        Log.d(TAG, "rowID =" + rowID + " updated:" + updateResult);

        return updateResult;
    }


    //Delete record to Users
    public int dbDeleteUser(long rowID) {
        if (rowID == 1) {
            return 0;
        }
        String where = KEY_ID + "=" + rowID;
        int deleteCount = getWritableDatabase().delete(TABLE_USERS, where, null);
        Log.d(TAG, "rowID =" + rowID + " delete:" + deleteCount);
        return deleteCount;
    }

    //Delete all record to Users
    public int dbDeleteUsers() {
        int deleteCount = getWritableDatabase().delete(TABLE_USERS, KEY_ID + " !=1", null);
        Log.d(TAG, "Delete count =" + " delete:" + deleteCount);
        return deleteCount;
    }

    private void initPreferences() {
        //initializing preference
        preferenceHelper = PreferenceHelper.getInstance();
    }

    public int dbReadInLog() {
        Log.d(TAG, "--- Rows in mytable: ---");
        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = getReadableDatabase().query(TABLE_USERS, null, null, null, null, null, null);
        int countRet;
        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex(KEY_ID);
            int nameColIndex = c.getColumnIndex(KEY_NAME);
            int emailColIndex = c.getColumnIndex(KEY_EMAIL);
            int partemailColIndex = c.getColumnIndex(KEY_PART_EMAIL);
            int latColIndex = c.getColumnIndex(KEY_LATTITUDE);
            int longColIndex = c.getColumnIndex(KEY_LONGTITUDE);
            int speedColIndex = c.getColumnIndex(KEY_SPEED);
            int fbColIndex = c.getColumnIndex(KEY_FBASE_PATH);
            int dateColIndex = c.getColumnIndex(KEY_DATE);

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d(TAG,
                        "ID = " + c.getInt(idColIndex) +
                                ", name = " + c.getString(nameColIndex) +
                                ", email = " + c.getString(emailColIndex) +
                                ", part_email = " + c.getString(partemailColIndex) +
                                ", latitude = " + c.getString(latColIndex) +
                                ", longtitude = " + c.getString(longColIndex) +
                                ", speed = " + c.getString(speedColIndex) +
                                ", date = " + c.getString(dateColIndex) +
                                ", fbasepath = " + c.getString(fbColIndex));
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
            Log.d(TAG, "Column names = 1 = " + c.getColumnName(0));
            countRet = c.getCount();
        } else {
            Log.d(TAG, "0 rows");
            countRet = 0;
        }
        c.close();
        return countRet;
    }


    public List<SmallModel> getListSmallModel() {
        List<SmallModel> SmallModels = new ArrayList<>();
        Log.d(TAG, "--- Rows in mytable: ---");
        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = getReadableDatabase().query(TABLE_USERS, null, null, null, null, null, null);
        int countRet;
        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex(KEY_ID);
            int nameColIndex = c.getColumnIndex(KEY_NAME);
            int stateColIndex = c.getColumnIndex(KEY_STATE);
            int modeColIndex = c.getColumnIndex(KEY_MODE);
            int rightsColIndex = c.getColumnIndex(KEY_RIGHTS);
            int speedColIndex = c.getColumnIndex(KEY_SPEED);
            int movedColIndex = c.getColumnIndex(KEY_MOVED);
            int dateColIndex = c.getColumnIndex(KEY_DATE);
            int longtitudeColIndex = c.getColumnIndex(KEY_LONGTITUDE);
            int lattitudeColIndex = c.getColumnIndex(KEY_LATTITUDE);
            int contactColIndex = c.getColumnIndex(KEY_CONTACT_ID);
            int emailColIndex = c.getColumnIndex(KEY_EMAIL);
            int i = 0;

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d(TAG,
                        "ID = " + c.getInt(idColIndex) +
                                ", name = " + c.getString(nameColIndex) +
                                ", email = " + c.getString(emailColIndex));
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
                //(long id, String name, int state, int mode, int rights, long speed, int moved, long track_date, long longtitude, long lattitude, int contact_id, String email)
                SmallModels.add(i, new SmallModel(c.getInt(idColIndex), c.getString(nameColIndex), c.getInt(stateColIndex),
                        c.getInt(modeColIndex), c.getInt(rightsColIndex), c.getInt(speedColIndex), c.getInt(movedColIndex),
                        c.getLong(dateColIndex), c.getLong(longtitudeColIndex), c.getLong(lattitudeColIndex),
                        c.getInt(contactColIndex), c.getString(emailColIndex)
                ));
                i++;

            } while (c.moveToNext());
            Log.d(TAG, "Column names = 1 = " + c.getColumnName(0));
            countRet = c.getCount();
        } else {
            Log.d(TAG, "0 rows");
            SmallModels.add(0, new SmallModel(0, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, ""));
            countRet = 0;
        }
        c.close();
        return SmallModels;
    }

    public ModelCheck getLatLongTimeFromMe() {
        ModelCheck modelCheck;
        Cursor c = getReadableDatabase().query(TABLE_USERS, null,
                KEY_ID + "=?", new String[]{Integer.toString(1)}, null, null, null);


        // определяем номера столбцов по имени в выборке
        int lattitudeColIndex = c.getColumnIndex(KEY_LATTITUDE);
        int longtitudeColIndex = c.getColumnIndex(KEY_LONGTITUDE);
        int dateColIndex = c.getColumnIndex(KEY_DATE);
        int idColIndex = c.getColumnIndex(KEY_ID);
        int nameColIndex = c.getColumnIndex(KEY_NAME);
        int stateColIndex = c.getColumnIndex(KEY_STATE);
        int modeColIndex = c.getColumnIndex(KEY_MODE);
        int rightsColIndex = c.getColumnIndex(KEY_RIGHTS);
        int speedColIndex = c.getColumnIndex(KEY_SPEED);
        int movedColIndex = c.getColumnIndex(KEY_MOVED);
        int emailColIndex = c.getColumnIndex(KEY_EMAIL);
        int part_emailColIndex = c.getColumnIndex(KEY_PART_EMAIL);

        if (c != null && c.getCount() > 0 && c.moveToFirst()) {
            Log.d(TAG, "getLatLongTimeFromMe Latitude;" + c.getString(lattitudeColIndex));
            Log.d(TAG, "getLatLongTimeFromMe Name;" + c.getString(nameColIndex));
            Log.d(TAG, "getLatLongTimeFromMe KEY_ID = " + c.getString(idColIndex));
            Log.d(TAG, "getLatLongTimeFromMe LongTitude;" + c.getString(longtitudeColIndex));
            Log.d(TAG, "getLatLongTimeFromMe DateTime;" + c.getString(dateColIndex));
            //  modelCheck = new ModelCheck(c.getDouble(lattitudeColIndex), c.getDouble(longtitudeColIndex), c.getLong(dateColIndex));
            modelCheck = new ModelCheck(c.getDouble(lattitudeColIndex), c.getDouble(longtitudeColIndex), c.getLong(dateColIndex)
                    , c.getInt(stateColIndex), c.getInt(modeColIndex), c.getInt(rightsColIndex), c.getLong(speedColIndex)
                    , c.getInt(modeColIndex), c.getString(emailColIndex), c.getString(part_emailColIndex));
        } else {
            modelCheck = new ModelCheck(0, 0, 0, 0, 0, 0, 0, 0, "none@none.ne", "0000");
        }
        c.close();
        return modelCheck;
    }

    public List<ListFireBasePath> getListFireBasePath() {
        List<ListFireBasePath> listFireBasePaths = new ArrayList<>();
        Cursor c = getReadableDatabase().query(TABLE_USERS, new String[]{KEY_EMAIL, KEY_PART_EMAIL, KEY_FBASE_PATH, KEY_ID, KEY_BADCOUNT},
                KEY_ID + "!=1", null, null, null, null);


        if (c != null && c.getCount() > 0) {
            if (c.moveToFirst()) {

                int emailColIndex = c.getColumnIndex(KEY_EMAIL);
                int part_emailColIndex = c.getColumnIndex(KEY_PART_EMAIL);
                int fbasePathColIndex = c.getColumnIndex(KEY_FBASE_PATH);
                int idColIndex = c.getColumnIndex(KEY_ID);
                int badCountColIndex = c.getColumnIndex(KEY_BADCOUNT);
                int i = 0;

                do {
                    // получаем значения по номерам столбцов и пишем все в лог
                    Log.d(TAG,
                            "email = " + c.getString(emailColIndex) +
                                    ", part_email = " + c.getString(part_emailColIndex) +
                                    ", firebase_pth = " + c.getString(fbasePathColIndex));
                    listFireBasePaths.add(i, new ListFireBasePath(c.getString(emailColIndex),
                            c.getString(part_emailColIndex), c.getString(fbasePathColIndex), c.getInt(idColIndex), c.getInt(badCountColIndex)));
                    i++;

                } while (c.moveToNext());

            } else {
                Log.d(TAG, "0 rows");
                listFireBasePaths.add(0, new ListFireBasePath("", "", "", 0, 0));
            }
        } else {
            Log.d(TAG, "0 rows");
            listFireBasePaths.add(0, new ListFireBasePath("", "", "", 0, 0));
        }
        c.close();
        return listFireBasePaths;
    }

    //Update record to Me
    public int dbUpdateMe(long rowID, int state, int mode, int rights,
                          double speed, int moved, long track_date, double longtitude, double lattitude,
                          String fbase_path) {

        cv = new ContentValues();
        cv.put(KEY_STATE, state);
        cv.put(KEY_MODE, mode);
        cv.put(KEY_RIGHTS, rights);
        cv.put(KEY_SPEED, speed);
        cv.put(KEY_MOVED, moved);
        if (track_date != 0) {       //if 0 значит время обнорвления FB не ставим
            cv.put(KEY_DATE, track_date);
        }
        cv.put(KEY_LONGTITUDE, longtitude);
        cv.put(KEY_LATTITUDE, lattitude);
        cv.put(KEY_FBASE_PATH, fbase_path);
        String where = KEY_ID + "=" + rowID;


        int updateResult = getWritableDatabase().update(TABLE_USERS, cv, where, null); //uodateResult - count of Updated record
        Log.d(TAG, "rowID =" + rowID + " updated:" + updateResult);

        return updateResult;
    }

    public int dbUpdateBadCount(long rowID, int badCount) {
        cv = new ContentValues();
        cv.put(KEY_BADCOUNT, badCount);
        String where = KEY_ID + "=" + rowID;
        int updateResult = getWritableDatabase().update(TABLE_USERS, cv, where, null); //uodateResult - count of Updated record
        Log.d(TAG, "rowID =" + rowID + " updated:" + updateResult);
        return updateResult;
    }

    public int dbUpdateEmail(long rowID, String email) {
        cv = new ContentValues();
        cv.put(KEY_EMAIL, email);
        String where = KEY_ID + "=" + rowID;
        int updateResult = getWritableDatabase().update(TABLE_USERS, cv, where, null); //uodateResult - count of Updated record
        Log.d(TAG, "rowID =" + rowID + " updated:" + updateResult);
        return updateResult;
    }

    public int dbUpdateFBase(String email, String part_email, String fbase) {
        cv = new ContentValues();
        cv.put(KEY_FBASE_PATH, fbase);
        String where =  KEY_EMAIL + "='"+email+"' and "+KEY_PART_EMAIL +"='"+part_email+"'";
        int updateResult = getWritableDatabase().update(TABLE_USERS, cv, where, null); //uodateResult - count of Updated record
        Log.d(TAG, where + " updated:" + updateResult);
        return updateResult;
    }

    //read Email _id = 1
    public String getEmail() {
        Cursor c = getReadableDatabase().query(TABLE_USERS, new String[]{KEY_EMAIL}, KEY_ID + "= 1", null, null, null, null);

        // определяем номера столбцов по имени в выборке
        int emailColIndex = c.getColumnIndex(KEY_EMAIL);

        if (c != null && c.getCount() > 0 && c.moveToFirst()) {
                Log.d(TAG, "getEmail()");
                return c.getString(emailColIndex);
        }
        c.close();
        return null;
    }

    //update information from FireBase for clients
    public int updateFbaseModel(int state, int mode, int rights,
                                double speed, int moved, long track_date, double longtitude, double lattitude,
                                String fbase_path, String email, String part_email) {
        cv = new ContentValues();

        if (email == null || part_email == null){
            return 0;
        }
        if (email.isEmpty() || part_email.isEmpty()){
            return 0;
        }

        cv.put(KEY_STATE, state);
        cv.put(KEY_MODE, mode);
        cv.put(KEY_RIGHTS, rights);
        cv.put(KEY_SPEED, speed);
        cv.put(KEY_MOVED, moved);
        if (track_date != 0) {       //if 0 значит время обнорвления FB не ставим
            cv.put(KEY_DATE, track_date);
        }
        cv.put(KEY_LONGTITUDE, longtitude);
        cv.put(KEY_LATTITUDE, lattitude);
        cv.put(KEY_FBASE_PATH, fbase_path);
        String where = KEY_EMAIL + "='" + email + "' and " + KEY_PART_EMAIL + "='" + part_email + "'";
        Log.d(TAG, "DBHelper:updateFbaseModel: " + cv.toString()+" where :"+where);
        int updateResult = getWritableDatabase().update(TABLE_USERS, cv, where, null); //uodateResult - count of Updated record
        Log.d(TAG, "DBHelper:updateFbaseModel: updated where =" + where + " updated:" + updateResult);
        return updateResult;
    }

    public String getEmailPartFBasePartFromMe(){
        Cursor c = getReadableDatabase().query(TABLE_USERS, new String[]{KEY_EMAIL, KEY_PART_EMAIL, KEY_FBASE_PATH, KEY_ID},KEY_ID + "=1", null, null, null, null);

        if (c != null && c.getCount() > 0 && c.moveToFirst()) {
            Log.d(TAG, "showAbout(YES)");
            // определяем номера столбцов по имени в выборке
            int emailColIndex = c.getColumnIndex(KEY_EMAIL);
            int part_emailColIndex = c.getColumnIndex(KEY_PART_EMAIL);
            int fbaseColIndex = c.getColumnIndex(KEY_FBASE_PATH);
            String s = c.getString(emailColIndex)+";"+c.getString(part_emailColIndex)+";"+c.getString(fbaseColIndex);
            c.close();
            return s;
        } else {
            Log.d(TAG, "showAbout(NO)");
            c.close();
            return "";
        }
    }

    public boolean checkExistClient(String email, String part_email){
        Cursor c = getReadableDatabase().query(TABLE_USERS, new String[]{KEY_EMAIL, KEY_PART_EMAIL, KEY_FBASE_PATH, KEY_ID},
                KEY_EMAIL + "=? and "+KEY_PART_EMAIL +"=?", new String[]{email,part_email},
                null, null, null);

        if (c != null && c.getCount() > 0 && c.moveToFirst()) {
            Log.d(TAG, "showAbout:checkExistClient(YES)");
            c.close();
            return true;
        } else {
            Log.d(TAG, "showAbout:checkExistClient(NO)");
            c.close();
            return false;
        }
    }
}

