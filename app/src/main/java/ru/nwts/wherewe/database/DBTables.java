package ru.nwts.wherewe.database;

/**
 * Created by Надя on 24.12.2016.
 */

public interface DBTables extends DBConstant {

    //Таблица users
    public static final String SQL_CREATE_ENTRIES_USERS =
            "CREATE TABLE "             + TABLE_USERS + " (" +
                    KEY_ID                      +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    KEY_NAME                    +" TEXT,"  +
                    KEY_STATE                   + " INTEGER," +
                    KEY_MODE                    + " INTEGER," +
                    KEY_RIGHTS                  + " INTEGER," +
                    KEY_SPEED                   + " NUMERIC," +
                    KEY_MOVED                   + " INTEGER," +
                    KEY_DATE                    + " NUMERIC," +
                    KEY_LONGTITUDE              + " REAL," +
                    KEY_LATTITUDE               + " REAL," +
                    KEY_FBASE_PATH              + " TEXT," +
                    KEY_FBASE_OLD               + " TEXT," +
                    KEY_TRACK_COUNT             + " INTEGER," +
                    KEY_TRACK_COUNT_ALLOWED     + " INTEGER," +
                    KEY_ENCRYPTION              + " TEXT," +
                    KEY_ENCRYPTION_OLD          + " TEXT," +
                    KEY_CONTACT_ID              + " INTEGER," +
                    KEY_EMAIL	                + " TEXT," +
                    KEY_PART_EMAIL              + " TEXT," +
                    KEY_BADCOUNT                + " INTEGER" +
                    ")";

    //Таблица groups
    public static final String SQL_CREATE_ENTRIES_GROUPS =
            "CREATE TABLE "             + TABLE_GROUPS + " (" +
                    KEY_ID                      +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    KEY_NAME                    +" TEXT,"  +
                    KEY_MODE                    + " INTEGER " +
                    ")";

    //Таблица links
    public static final String SQL_CREATE_ENTRIES_LINKS =
            "CREATE TABLE "             + TABLE_LINKS + " (" +
                    KEY_ID                      +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    KEY_USER_ID                 +" INTEGER," +
                    KEY_GROUP_ID                +" INTEGER " +
                    ")";

    //Таблица tracks
    public static final String SQL_CREATE_ENTRIES_TRACKS =
            "CREATE TABLE "             + TABLE_TRACKS + " (" +
                    KEY_ID                      +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    KEY_USER_ID                 + " INTEGER," +
                    KEY_DATE                    + " NUMERIC," +
                    KEY_LONGTITUDE              + " REAL," +
                    KEY_LATTITUDE               + " REAL," +
                    KEY_SPEED                   + " NUMERIC," +
                    KEY_MOVED                   + " INTEGER," +
                    KEY_MODE                    + " INTEGER " +
                    ")";


    /**
     *  Таблица Statistics пока не создается....
     */

}
