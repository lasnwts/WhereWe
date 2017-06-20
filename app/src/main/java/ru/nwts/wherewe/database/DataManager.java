package ru.nwts.wherewe.database;

import android.content.Context;

import com.squareup.picasso.Picasso;

import ru.nwts.wherewe.TODOApplication;
import ru.nwts.wherewe.model.DaoSession;
import ru.nwts.wherewe.util.PicassoCache;

/**
 * Created by пользователь on 19.06.2017.
 */

public class DataManager {
    private static DataManager INSTANCE = null;
    private Context mContext;
    private Picasso mPicasso;
    private DaoSession mDaoSession;


    public DataManager(Context context) {
        this.mContext = context;
        mPicasso = new PicassoCache(mContext).getPicassoInstance();
        mDaoSession = TODOApplication.getDaoSession();
    }

    public static DataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager(TODOApplication.getInstance());
        }
        return INSTANCE;
    }

    public Picasso getPicasso() {
        /**
         * Индикация загрузки
         */
        mPicasso.setIndicatorsEnabled(true);
        mPicasso.setLoggingEnabled(true);
        return mPicasso;
    }

    public Context getContext() {
        return mContext;
    }

//    public DaoSession getDaoSession() {
//        return mDaoSession;
//    }

}
