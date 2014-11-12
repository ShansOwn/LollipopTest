package com.shansown.android.lollipoptest.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.shansown.android.lollipoptest.util.LogUtils.LOGD;
import static com.shansown.android.lollipoptest.util.LogUtils.makeLogTag;

public class LollipopTestDatabase extends SQLiteOpenHelper {

    private static final String TAG = makeLogTag(LollipopTestDatabase.class);

    private static final String DATABASE_NAME = "lollipoptest.db";
    private static final int DATABASE_VERSION = 1;

    interface Tables {
        String ENTRY = "entry";
    }

    public LollipopTestDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LOGD(TAG, "onCreate()");
        LollipopTestContract.Entry.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LOGD(TAG, "onUpgrade()");
        LollipopTestContract.Entry.onUpgrade(db, oldVersion, newVersion);
    }
}
