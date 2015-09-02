package com.shansown.androidtests.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.shansown.android.lollipoptest.R;

import static com.shansown.androidtests.util.LogUtils.makeLogTag;

/**
 * Utilities and constants related to app preferences.
 */
public class PrefUtils {

  private static final String TAG = makeLogTag(PrefUtils.class);

  public static final String PREF_SYNC = "pref_sync";
  public static final String PREF_CUR_SYNC_INTERVAL = "pref_cur_sync_interval";

  public static final String PREF_ACCOUNT_SETUP_COMPLETE = "pref_account_setup_complete";

  public static boolean isSyncEnabled(final Context context) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    return sp.getBoolean(PREF_SYNC, false);
  }

  public static String getCurSyncInterval(final Context context) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    return sp.getString(PREF_CUR_SYNC_INTERVAL,
        context.getResources().getString(R.string.auto_sync_interval_default));
  }

  public static void setCurSyncInterval(final Context context, long interval) {
    setCurSyncInterval(context, interval, false);
  }

  public static void setCurSyncInterval(final Context context, long interval, boolean sync) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor =
        sp.edit().putString(PREF_CUR_SYNC_INTERVAL, String.valueOf(interval));
    if (sync) {
      editor.commit();
    } else {
      editor.apply();
    }
  }

  public static boolean isAccountSetupComplit(final Context context) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    return sp.getBoolean(PREF_ACCOUNT_SETUP_COMPLETE, false);
  }

  public static void setAccountSetupComplete(final Context context, boolean complete) {
    setAccountSetupComplete(context, complete, false);
  }

  public static void setAccountSetupComplete(final Context context, boolean complete,
      boolean sync) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sp.edit().putBoolean(PREF_ACCOUNT_SETUP_COMPLETE, complete);
    if (sync) {
      editor.commit();
    } else {
      editor.apply();
    }
  }

  public static void registerOnSharedPreferenceChangeListener(final Context context,
      SharedPreferences.OnSharedPreferenceChangeListener listener) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    sp.registerOnSharedPreferenceChangeListener(listener);
  }

  public static void unregisterOnSharedPreferenceChangeListener(final Context context,
      SharedPreferences.OnSharedPreferenceChangeListener listener) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    sp.unregisterOnSharedPreferenceChangeListener(listener);
  }
}