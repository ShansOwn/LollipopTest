package com.shansown.androidtests.synctest;

import android.accounts.Account;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.shansown.android.lollipoptest.R;
import com.shansown.androidtests.provider.LollipopTestContract;
import com.shansown.androidtests.sync.SyncHelper;
import com.shansown.androidtests.util.AccountUtils;

import static com.shansown.androidtests.util.LogUtils.LOGD;
import static com.shansown.androidtests.util.LogUtils.makeLogTag;

public class EntryListFragment extends Fragment
    implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor>,
    SyncStatusObserver {

  private static final String TAG = makeLogTag(EntryListFragment.class);

  /**
   * Projection for querying the content provider.
   */
  private static final String[] PROJECTION = new String[] {
      LollipopTestContract.Entry._ID, LollipopTestContract.Entry.ENTRY_ID,
      LollipopTestContract.Entry.ENTRY_TITLE, LollipopTestContract.Entry.ENTRY_LINK,
      LollipopTestContract.Entry.ENTRY_PUBLISHED
  };

  // Column indexes. The index of a column in the Cursor is the same as its relative position in
  // the projection.
  /** Column index for _ID */
  private static final int COLUMN_ID = 0;
  /** Column index for id */
  private static final int COLUMN_ENTRY_ID = 1;
  /** Column index for title */
  private static final int COLUMN_TITLE = 2;
  /** Column index for link */
  private static final int COLUMN_URL_STRING = 3;
  /** Column index for published */
  private static final int COLUMN_PUBLISHED = 4;

  /**
   * List of Cursor columns to read from when preparing an adapter to populate the ListView.
   */
  private static final String[] FROM_COLUMNS = new String[] {
      LollipopTestContract.Entry.ENTRY_TITLE, LollipopTestContract.Entry.ENTRY_PUBLISHED
  };

  /**
   * List of Views which will be populated by Cursor data.
   */
  private static final int[] TO_FIELDS = new int[] {
      android.R.id.text1, android.R.id.text2
  };

  private SwipeRefreshLayout mSwipeRefresh;

  private ListView mListView;
  private SimpleCursorAdapter mAdapter;

  /**
   * Handle to a SyncObserver. The ProgressBar element is visible until the SyncObserver reports
   * that the sync is complete.
   *
   * <p>This allows us to delete our SyncObserver once the application is no longer in the
   * foreground.
   */
  private Object mSyncMonitor;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_entry_list, container, false);

    mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
    mSwipeRefresh.setColorSchemeResources(R.color.refresh_progress_1, R.color.refresh_progress_2,
        R.color.refresh_progress_3);

    mAdapter = new SimpleCursorAdapter(getActivity(),       // Current context
        android.R.layout.simple_list_item_activated_2,  // Layout for individual rows
        null,                // Cursor
        FROM_COLUMNS,        // Cursor columns to use
        TO_FIELDS,           // Layout fields to use
        0                    // No flags
    );
    mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
      @Override public boolean setViewValue(View view, Cursor cursor, int i) {
        if (i == COLUMN_PUBLISHED) {
          // Convert timestamp to human-readable date
          Time t = new Time();
          t.set(cursor.getLong(i));
          ((TextView) view).setText(t.format("%Y-%m-%d %H:%M"));
          return true;
        } else {
          // Let SimpleCursorAdapter handle other fields automatically
          return false;
        }
      }
    });

    mListView = (ListView) view.findViewById(R.id.list);
    mListView.setAdapter(mAdapter);

    getLoaderManager().initLoader(0, null, this);
    return view;
  }

  @Override public void onResume() {
    super.onResume();
    mSwipeRefresh.setOnRefreshListener(this);
    // Watch for sync state changes
    final int mask =
        ContentResolver.SYNC_OBSERVER_TYPE_PENDING | ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
    mSyncMonitor = ContentResolver.addStatusChangeListener(mask, this);
    AccountUtils.getAccount(getActivity());
  }

  @Override public void onPause() {
    super.onPause();
    mSwipeRefresh.setOnRefreshListener(null);
    ContentResolver.removeStatusChangeListener(mSyncMonitor);
  }

  @Override public void onRefresh() {
    LOGD(TAG, "onRefresh");
    SyncHelper.requestSync(getActivity(), true);
  }

  /**
   * Query the content provider for data.
   *
   * <p>Loaders do queries in a background thread. They also provide a ContentObserver that is
   * triggered when data in the content provider changes. When the sync adapter updates the
   * content provider, the ContentObserver responds by resetting the loader and then reloading
   * it.
   */
  @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    LOGD(TAG, "onCreateLoader");
    // We only have one loader, so we can ignore the value of i.
    // (It'll be '0', as set in onCreate().)
    return new CursorLoader(getActivity(),  // Context
        LollipopTestContract.Entry.CONTENT_URI, // URI
        PROJECTION,                // Projection
        null,                           // Selection
        null,                           // Selection args
        LollipopTestContract.Entry.ENTRY_PUBLISHED + " desc"); // Sort
  }

  /**
   * Move the Cursor returned by the query into the ListView adapter. This refreshes the existing
   * UI with the data in the Cursor.
   */
  @Override public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    LOGD(TAG, "onLoadFinished. Cursor count: " + cursor.getCount());
    mAdapter.swapCursor(cursor);
  }

  /**
   * Called when the ContentObserver defined for the content provider detects that data has
   * changed. The ContentObserver resets the loader, and then re-runs the loader. In the adapter,
   * set the Cursor value to null. This removes the reference to the Cursor, allowing it to be
   * garbage-collected.
   */
  @Override public void onLoaderReset(Loader<Cursor> loader) {
    LOGD(TAG, "onLoaderReset.");
    mAdapter.swapCursor(null);
  }

  @Override public void onStatusChanged(int which) {
    LOGD(TAG, "onStatusChanged");

    Account account = AccountUtils.getAccount(getActivity());

    // Test the ContentResolver to see if the sync adapter is active or pending.
    // Set the state of the refresh accordingly.
    final boolean syncActive =
        ContentResolver.isSyncActive(account, LollipopTestContract.CONTENT_AUTHORITY);
    final boolean syncPending =
        ContentResolver.isSyncPending(account, LollipopTestContract.CONTENT_AUTHORITY);

    mSwipeRefresh.post(new Runnable() {
      @Override public void run() {
        LOGD(TAG, "Set refreshing: "
            + (syncActive || syncPending)
            + " syncActive: "
            + syncActive
            + " syncPending: "
            + syncPending);
        mSwipeRefresh.setRefreshing(syncActive || syncPending);
      }
    });
  }
}