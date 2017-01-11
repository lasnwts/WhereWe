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

import ru.nwts.wherewe.model.Model;
import ru.nwts.wherewe.model.SmallModel;

import static android.R.attr.id;
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


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_USERS);
        db.execSQL(SQL_CREATE_ENTRIES_GROUPS);
        db.execSQL(SQL_CREATE_ENTRIES_LINKS);
        db.execSQL(SQL_CREATE_ENTRIES_TRACKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion == 1 && newVersion == 2){
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
                              String key, String key_old, Object contact_id, String email, String part_email){

        cv = new ContentValues();
        cv.put(KEY_NAME,name);
        cv.put(KEY_STATE,state);
        cv.put(KEY_MODE,mode);
        cv.put(KEY_RIGHTS,rights);
        cv.put(KEY_SPEED,speed);
        cv.put(KEY_MOVED,moved);
        cv.put(KEY_DATE,track_date);
        cv.put(KEY_LONGTITUDE,longtitude);
        cv.put(KEY_LATTITUDE,lattitude);
        cv.put(KEY_FBASE_PATH,fbase_path);
        cv.put(KEY_FBASE_OLD,fbase_old);
        cv.put(KEY_TRACK_COUNT,track_count);
        cv.put(KEY_TRACK_COUNT_ALLOWED,track_count_allowed);
        cv.put(KEY_ENCRYPTION,key);
        cv.put(KEY_ENCRYPTION_OLD,key_old);
        //cv.put(KEY_CONTACT_ID,contact_id);
        cv.put(KEY_EMAIL,email);
        cv.put(KEY_PART_EMAIL,part_email);


        long rowID = getWritableDatabase().insert(TABLE_USERS,null,cv);
        Log.d(TAG,"row inserted, ID ="+rowID);

        return  rowID;
    }

    //Insert simple record to Users
    public long dbInsertUser(String name, int state, int mode, int rights,
                              long speed, int moved, long track_date, double longtitude, double lattitude,
                              String fbase_path, String fbase_old, int track_count, int track_count_allowed,
                              String key, String key_old, String email, String part_email){

        cv = new ContentValues();
        cv.put(KEY_NAME,name);
        cv.put(KEY_STATE,state);
        cv.put(KEY_MODE,mode);
        cv.put(KEY_RIGHTS,rights);
        cv.put(KEY_SPEED,speed);
        cv.put(KEY_MOVED,moved);
        cv.put(KEY_DATE,track_date);
        cv.put(KEY_LONGTITUDE,longtitude);
        cv.put(KEY_LATTITUDE,lattitude);
        cv.put(KEY_FBASE_PATH,fbase_path);
        cv.put(KEY_FBASE_OLD,fbase_old);
        cv.put(KEY_TRACK_COUNT,track_count);
        cv.put(KEY_TRACK_COUNT_ALLOWED,track_count_allowed);
        cv.put(KEY_ENCRYPTION,key);
        cv.put(KEY_ENCRYPTION_OLD,key_old);
        cv.put(KEY_EMAIL,email);
        cv.put(KEY_PART_EMAIL,part_email);


        long rowID = getWritableDatabase().insert(TABLE_USERS,null,cv);
        Log.d(TAG,"row inserted, ID ="+rowID);

        return  rowID;
    }

    //Update record to Users
    public int dbUpdateUsers(long rowID,String name, int state,int mode,int rights,
                             long speed,int moved,long track_date,long longtitude,long lattitude,
                             String fbase_path, String fbase_old, int track_count,  int track_count_allowed,
                             String key, String key_old, int contact_id, String email, String part_email){

        cv = new ContentValues();
        cv.put(KEY_NAME,name);
        cv.put(KEY_STATE,state);
        cv.put(KEY_MODE,mode);
        cv.put(KEY_RIGHTS,rights);
        cv.put(KEY_SPEED,speed);
        cv.put(KEY_MOVED,moved);
        cv.put(KEY_DATE,track_date);
        cv.put(KEY_LONGTITUDE,longtitude);
        cv.put(KEY_LATTITUDE,lattitude);
        cv.put(KEY_FBASE_PATH,fbase_path);
        cv.put(KEY_FBASE_OLD,fbase_old);
        cv.put(KEY_TRACK_COUNT,track_count);
        cv.put(KEY_TRACK_COUNT_ALLOWED,track_count_allowed);
        cv.put(KEY_ENCRYPTION,key);
        cv.put(KEY_ENCRYPTION_OLD,key_old);
        cv.put(KEY_CONTACT_ID,contact_id);
        cv.put(KEY_EMAIL,email);
        cv.put(KEY_PART_EMAIL,part_email);
        String where = KEY_ID + "=" + rowID;


        int updateResult = getWritableDatabase().update(TABLE_USERS,cv,where,null); //uodateResult - count of Updated record
        Log.d(TAG,"rowID ="+rowID + " updated:"+updateResult);

        return  updateResult;
    }


    //Delete record to Users
    public int dbDeleteUser(long rowID){
        String where = KEY_ID + "=" + rowID;
        int deleteCount = getWritableDatabase().delete(TABLE_USERS,where,null);
        Log.d(TAG,"rowID ="+rowID + " delete:"+deleteCount);
        return  deleteCount;
    }

    //Delete all record to Users
    public int dbDeleteUsers(){
        int deleteCount = getWritableDatabase().delete(TABLE_USERS,null,null);
        Log.d(TAG,"Delete count ="+ " delete:"+deleteCount);
        return  deleteCount;
    }


    public int dbReadInLog(){
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

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d(TAG,
                        "ID = " +  c.getInt(idColIndex) +
                                ", name = " + c.getString(nameColIndex) +
                                ", email = " + c.getString(emailColIndex));
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
            Log.d(TAG,"Column names = 1 = "+c.getColumnName(0));
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
                        "ID = " +  c.getInt(idColIndex) +
                                ", name = " + c.getString(nameColIndex) +
                                ", email = " + c.getString(emailColIndex));
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
                //(long id, String name, int state, int mode, int rights, long speed, int moved, long track_date, long longtitude, long lattitude, int contact_id, String email)
                SmallModels.add(i,new SmallModel(c.getInt(idColIndex),c.getString(nameColIndex),c.getInt(stateColIndex),
                        c.getInt(modeColIndex),c.getInt(rightsColIndex),c.getInt(speedColIndex),c.getInt(movedColIndex),
                        c.getLong(dateColIndex),c.getLong(longtitudeColIndex),c.getLong(lattitudeColIndex),
                        c.getInt(contactColIndex),c.getString(emailColIndex)
                        ));
                i++;

            } while (c.moveToNext());
            Log.d(TAG,"Column names = 1 = "+c.getColumnName(0));
            countRet = c.getCount();
        } else {
            Log.d(TAG, "0 rows");
            SmallModels.add(0,new SmallModel(0, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, ""));
            countRet = 0;
        }
        c.close();
        return SmallModels;
    }

}
