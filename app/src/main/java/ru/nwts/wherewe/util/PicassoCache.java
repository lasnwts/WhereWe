package ru.nwts.wherewe.util;

import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

/**
 * Created by пользователь on 19.06.2017.
 */

public class PicassoCache {
    private Context mContext;
    private Picasso mPicassoInstance;

    public PicassoCache(Context context) {
        mContext = context;
        OkHttp3Downloader okHttp3Downloader = new OkHttp3Downloader(context, Integer.MAX_VALUE);
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(okHttp3Downloader);
        builder.indicatorsEnabled(true);
//        .setIndicatorsEnabled(true);
//        builder.setLoggingEnabled(true);

        mPicassoInstance = builder.build();
        if (mPicassoInstance == null) {
            Picasso.setSingletonInstance(mPicassoInstance);
        }
    }

    public Picasso getPicassoInstance() {
        if (mPicassoInstance == null) {
            new PicassoCache(mContext);
        }
        return mPicassoInstance;
    }


}
