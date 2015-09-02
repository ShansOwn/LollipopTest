package com.shansown.androidtests.sync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.shansown.androidtests.provider.LollipopTestContract;
import com.shansown.androidtests.util.AccountUtils;
import com.shansown.androidtests.util.PrefUtils;

import static com.shansown.androidtests.util.LogUtils.LOGD;
import static com.shansown.androidtests.util.LogUtils.makeLogTag;

/**
 * Helper class for working with the sync framework.
 */
public class SyncHelper {

  private static final String TAG = makeLogTag(SyncHelper.class);

  public static final int DATA_KEY_FEED = 1;

  private Context mContext;

  public SyncHelper(Context context) {
    mContext = context;
  }

  public static void enableSync(final Context context, final Account account) {
    final long interval = Long.parseLong(PrefUtils.getCurSyncInterval(context));
    updateSyncInterval(context, account, interval, true);
  }

  public static void disableSync(final Context context, final Account account) {
    ContentResolver.removePeriodicSync(account, LollipopTestContract.CONTENT_AUTHORITY,
        Bundle.EMPTY);
  }

  /**
   * Update sync interval
   *
   * @param context Context
   * @param account Account
   * @param newInterval Interval for auto sync
   * @param fromSettings Is this method invoked from {@link android.preference.PreferenceFragment}
   * or SettingsActivity {@link android.preference.PreferenceActivity}.
   * If true - it's means that {@code newInterval} already have stored
   * to preferences.
   */
  public static void updateSyncInterval(final Context context, final Account account,
      long newInterval, boolean fromSettings) {
    LOGD(TAG, "Checking sync interval for " + account);
    long current = Long.parseLong(PrefUtils.getCurSyncInterval(context));
    LOGD(TAG, "Current sync interval " + current + " new interval: " + newInterval);
    if (fromSettings || newInterval != current) {
      LOGD(TAG, "Setting up sync for account " + account + ", interval: " + newInterval + "s");
      ContentResolver.setIsSyncable(account, LollipopTestContract.CONTENT_AUTHORITY, 1);
      ContentResolver.setSyncAutomatically(account, LollipopTestContract.CONTENT_AUTHORITY, true);
      ContentResolver.addPeriodicSync(account, LollipopTestContract.CONTENT_AUTHORITY, new Bundle(),
          newInterval);
      if (!fromSettings) {
        PrefUtils.setCurSyncInterval(context, newInterval);
      }
    } else {
      LOGD(TAG, "No need to update sync interval.");
    }
  }

  /**
   * Helper method to trigger an immediate sync ("refresh").
   *
   * <p>This should only be used when we need to preempt the normal sync schedule. Typically, this
   * means the user has pressed the "refresh" button.
   *
   * Note that SYNC_EXTRAS_MANUAL will cause an immediate sync, without any optimization to
   * preserve battery life. If you know new data is available (perhaps via a GCM notification),
   * but the user is not actively waiting for that data, you should omit this flag; this will give
   * the OS additional freedom in scheduling your sync request.
   */
  public static void requestSync(Context context, boolean immediately) {
    Bundle bundle = new Bundle();
    if (immediately) {
      // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
      bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
      bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
    }
    ContentResolver.requestSync(AccountUtils.getAccount(context),      // Sync account
        LollipopTestContract.CONTENT_AUTHORITY, // Content authority
        bundle);                                      // Extras
  }

  // Returns whether we are connected to the internet.
  private boolean isOnline() {
    ConnectivityManager cm =
        (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
  }
}