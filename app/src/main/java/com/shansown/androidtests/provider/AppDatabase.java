package com.shansown.androidtests.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.shansown.androidtests.util.LogUtils.LOGD;
import static com.shansown.androidtests.util.LogUtils.makeLogTag;

public class AppDatabase extends SQLiteOpenHelper {

  private static final String TAG = makeLogTag(AppDatabase.class);

  private static final String DATABASE_NAME = "lollipoptest.db";
  private static final int DATABASE_VERSION = 1;

  interface Tables {
    String ENTRY = "entry";
  }

  public AppDatabase(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override public void onCreate(SQLiteDatabase db) {
    LOGD(TAG, "onCreate()");
    AppContract.Entry.onCreate(db);
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    LOGD(TAG, "onUpgrade()");
    AppContract.Entry.onUpgrade(db, oldVersion, newVersion);
  }
}
