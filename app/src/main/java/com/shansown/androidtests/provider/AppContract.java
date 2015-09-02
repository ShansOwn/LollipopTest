package com.shansown.androidtests.provider;

import android.content.ContentResolver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.text.TextUtils;

public class AppContract {

  public static final String CONTENT_AUTHORITY = "com.shansown.app.lollipoptest";
  public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

  private static final String PATH_ENTRIES = "entries";

  interface EntryColumns {
    /**
     * Atom ID. (Note: Not to be confused with the database primary key, which is _ID.
     */
    String ENTRY_ID = "entry_id";

    /**
     * Article title
     */
    String ENTRY_TITLE = "entry_title";

    /**
     * Article hyperlink. Corresponds to the rel="alternate" link in the
     * Atom spec.
     */
    String ENTRY_LINK = "entry_link";

    /**
     * Date article was published.
     */
    String ENTRY_PUBLISHED = "entry_published";
  }

  /**
   * Columns supported by "entries" records.
   */
  public static class Entry implements BaseColumns, EntryColumns {
    /**
     * Fully qualified URI for "entry" resources.
     */
    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRIES).build();
    /**
     * MIME type for lists of entries.
     */
    public static final String CONTENT_TYPE =
        ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.lollipoptest.entries";
    /**
     * MIME type for individual entries.
     */
    public static final String CONTENT_ITEM_TYPE =
        ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.lollipoptest.entry";

    /** Build {@link Uri} for requested {@link #ENTRY_ID}. */
    public static Uri buildEntryUri(String entryId) {
      return CONTENT_URI.buildUpon().appendPath(entryId).build();
    }

    public static String getEntryId(Uri uri) {
      return uri.getPathSegments().get(1);
    }

    public static void onCreate(SQLiteDatabase database) {
      database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    }

    private static final String DATABASE_CREATE = "CREATE TABLE "
        + AppDatabase.Tables.ENTRY
        + " ("
        + _ID
        + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + ENTRY_ID
        + " TEXT,"
        + ENTRY_TITLE
        + " TEXT,"
        + ENTRY_LINK
        + " TEXT,"
        + ENTRY_PUBLISHED
        + " INTEGER"
        + ");";
  }

  public static Uri addCallerIsSyncAdapterParameter(Uri uri) {
    return uri.buildUpon()
        .appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true")
        .build();
  }

  public static boolean hasCallerIsSyncAdapterParameter(Uri uri) {
    return TextUtils.equals("true", uri.getQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER));
  }
}