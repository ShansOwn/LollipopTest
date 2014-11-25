package com.shansown.android.lollipoptest.provider;

import android.content.ContentResolver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.OpenableColumns;
import android.text.TextUtils;

public class LollipopTestContract {

    public static final String CONTENT_AUTHORITY = "com.shansown.app.lollipoptest";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_ENTRIES = "entries";
    private static final String PATH_PHOTO_VIEW = "photo_view";

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

    interface PhotoViewColumns {
        /**
         * This column is a {@link Uri} that can be queried
         * for this individual image (resulting cursor has one single row for this image).
         */
        public static final String URI = "uri";
        /**
         * This column is a {@link String} that can be queried for this
         * individual image to return a displayable name.
         */
        public static final String NAME = OpenableColumns.DISPLAY_NAME;
        /**
         * This column is a {@link Uri} that points to the downloaded local file.
         * Can be null.
         */
//        public static final String CONTENT_URI = "contentUri";
        /**
         * This column is a {@link Uri} that points to a thumbnail of the image
         * that ideally is a local file.
         * Can be null.
         */
        public static final String THUMBNAIL_URI = "thumbnailUri";
        /**
         * This string column is the MIME type.
         */
//        public static final String CONTENT_TYPE = "contentType";
        /**
         * This boolean column indicates that a loading indicator should display permenantly
         * if no image urls are provided.
         */
        public static final String LOADING_INDICATOR = "loadingIndicator";
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

        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {}

        private static final String DATABASE_CREATE = "CREATE TABLE "
                + LollipopTestDatabase.Tables.ENTRY + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ENTRY_ID + " TEXT,"
                + ENTRY_TITLE + " TEXT,"
                + ENTRY_LINK + " TEXT,"
                + ENTRY_PUBLISHED + " INTEGER" + ");";
    }

    public static class PhotoView implements BaseColumns, PhotoViewColumns {
        /**
         * Fully qualified URI for "photoView" resources.
         */
//        public static final Uri CONTENT_URI =
//                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PHOTO_VIEW).build();
    }

    public static Uri addCallerIsSyncAdapterParameter(Uri uri) {
        return uri.buildUpon().appendQueryParameter(
                ContactsContract.CALLER_IS_SYNCADAPTER, "true").build();
    }

    public static boolean hasCallerIsSyncAdapterParameter(Uri uri) {
        return TextUtils.equals("true",
                uri.getQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER));
    }
}