package com.shansown.androidtests.provider;

import com.shansown.androidtests.provider.AppContract.Entry;
import com.shansown.androidtests.provider.AppDatabase.Tables;
import com.shansown.androidtests.util.SelectionBuilder;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import static com.shansown.androidtests.util.LogUtils.LOGD;
import static com.shansown.androidtests.util.LogUtils.LOGV;
import static com.shansown.androidtests.util.LogUtils.makeLogTag;

public class AppProvider extends ContentProvider {

  private static final String TAG = makeLogTag(AppProvider.class);

  private static final UriMatcher sUriMatcher = buildUriMatcher();

  /**
   * URI ID for route: /entries
   */
  public static final int ENTRIES = 100;
  /**
   * URI ID for route: /entries/{ID}
   */
  public static final int ENTRIES_ID = 101;

  private AppDatabase mDbHelper;

  @Override public boolean onCreate() {
    mDbHelper = new AppDatabase(getContext());
    return true;
  }

  @Override public String getType(Uri uri) {
    final int match = sUriMatcher.match(uri);
    switch (match) {
      case ENTRIES:
        return Entry.CONTENT_TYPE;
      case ENTRIES_ID:
        return Entry.CONTENT_ITEM_TYPE;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
  }

  /** {@inheritDoc} */
  @Override public Cursor query(Uri uri, String[] projection, String selection,
      String[] selectionArgs, String sortOrder) {
    // avoid the expensive string concatenation below if not loggable
    if (Log.isLoggable(TAG, Log.VERBOSE)) {
      LOGV(TAG, "uri=" + uri + ", proj=" + Arrays.toString(projection) +
          ", selection=" + selection + ", args=" + Arrays.toString(selectionArgs) + ")");
    }

    final SQLiteDatabase db = mDbHelper.getReadableDatabase();
    final int match = sUriMatcher.match(uri);

    switch (match) {
      default:
        // Most cases are handled with simple SelectionBuilder
        final SelectionBuilder builder = buildExpandedSelection(uri, match);
        Cursor cursor = builder.where(selection, selectionArgs).query(db, projection, sortOrder);
        Context context = getContext();
        if (context != null) {
          cursor.setNotificationUri(context.getContentResolver(), uri);
        }
        return cursor;
    }
  }

  /** {@inheritDoc} */
  @Override public Uri insert(Uri uri, ContentValues values) {
    // avoid the expensive string concatenation below if not loggable
    if (Log.isLoggable(TAG, Log.VERBOSE)) {
      LOGV(TAG, "uri=" + uri + ", values=" + values.toString());
    }

    final SQLiteDatabase db = mDbHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);

    switch (match) {
      case ENTRIES:
        db.insertOrThrow(Tables.ENTRY, null, values);
        notifyChange(uri);
        return Entry.buildEntryUri(values.getAsString(Entry.ENTRY_ID));
      default:
        throw new UnsupportedOperationException("Unknown insert uri: " + uri);
    }
  }

  /** {@inheritDoc} */
  @Override public int delete(Uri uri, String selection, String[] selectionArgs) {
    // avoid the expensive string concatenation below if not loggable
    if (Log.isLoggable(TAG, Log.VERBOSE)) {
      LOGV(TAG, "uri="
          + uri
          + ", selection="
          + selection
          + ", args="
          + Arrays.toString(selectionArgs)
          + ")");
    }

    final SQLiteDatabase db = mDbHelper.getWritableDatabase();
    final SelectionBuilder builder = buildSimpleSelection(uri);

    int count = builder.where(selection, selectionArgs).delete(db);
    notifyChange(uri);
    return count;
  }

  /** {@inheritDoc} */
  @Override public int update(Uri uri, ContentValues values, String selection,
      String[] selectionArgs) {
    // avoid the expensive string concatenation below if not loggable
    if (Log.isLoggable(TAG, Log.VERBOSE)) {
      LOGV(TAG, "uri="
          + uri
          + ", selection="
          + selection
          + ", args="
          + Arrays.toString(selectionArgs)
          + ")");
    }

    final SQLiteDatabase db = mDbHelper.getWritableDatabase();
    final SelectionBuilder builder = buildSimpleSelection(uri);

    int count = builder.where(selection, selectionArgs).update(db, values);
    notifyChange(uri);
    return count;
  }

  private void notifyChange(Uri uri) {
    LOGD(TAG, "notifyChange");
    // We only notify changes if the caller is not the sync adapter.
    // The sync adapter has the responsibility of notifying changes (it can do so
    // more intelligently than we can -- for example, doing it only once at the end
    // of the sync instead of issuing thousands of notifications for each record).
    if (!AppContract.hasCallerIsSyncAdapterParameter(uri)) {
      LOGD(TAG, "Really notify change");
      Context context = getContext();
      if (context != null) {
        context.getContentResolver().notifyChange(uri, null, false);
      }

      // (NOT implemented but for future)
      // Widgets can't register content observers so we refresh widgets separately.
      // context.sendBroadcast(ScheduleWidgetProvider.getRefreshBroadcastIntent(context, false));
    }
  }

  /**
   * Apply the given set of {@link android.content.ContentProviderOperation}, executing inside
   * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
   * any single one fails.
   */
  @Override public ContentProviderResult[] applyBatch(
      ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
    final SQLiteDatabase db = mDbHelper.getWritableDatabase();
    db.beginTransaction();
    try {
      final int numOperations = operations.size();
      final ContentProviderResult[] results = new ContentProviderResult[numOperations];
      for (int i = 0; i < numOperations; i++) {
        results[i] = operations.get(i).apply(this, results, i);
      }
      db.setTransactionSuccessful();
      return results;
    } finally {
      db.endTransaction();
    }
  }

  /**
   * Build a simple {@link SelectionBuilder} to match the requested
   * {@link Uri}. This is usually enough to support {@link #insert},
   * {@link #update}, and {@link #delete} operations.
   */
  private SelectionBuilder buildSimpleSelection(Uri uri) {
    final SelectionBuilder builder = new SelectionBuilder();
    final int match = sUriMatcher.match(uri);
    switch (match) {
      case ENTRIES:
        return builder.table(Tables.ENTRY);
      case ENTRIES_ID:
        final String entryId = Entry.getEntryId(uri);
        return builder.table(Tables.ENTRY).where(Entry.ENTRY_ID + "=?", entryId);
      default:
        throw new UnsupportedOperationException("Unknown uri for " + match + ": " + uri);
    }
  }

  /**
   * Build an advanced {@link SelectionBuilder} to match the requested
   * {@link Uri}. This is usually only used by {@link #query}, since it
   * performs table joins useful for {@link Cursor} data.
   */
  private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
    final SelectionBuilder builder = new SelectionBuilder();
    switch (match) {
      case ENTRIES:
        return builder.table(Tables.ENTRY);
      case ENTRIES_ID:
        final String entry_id = Entry.getEntryId(uri);
        return builder.table(Tables.ENTRY).where(Entry.ENTRY_ID + "=?", entry_id);
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
  }

  /**
   * Build and return a {@link UriMatcher} that catches all {@link Uri}
   * variations supported by this {@link ContentProvider}.
   */
  private static UriMatcher buildUriMatcher() {
    final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    final String authority = AppContract.CONTENT_AUTHORITY;

    matcher.addURI(authority, "entries", ENTRIES);
    matcher.addURI(authority, "entries/*", ENTRIES_ID);

    return matcher;
  }
}