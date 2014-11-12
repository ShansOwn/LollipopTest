package com.shansown.android.lollipoptest.synctest;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import com.shansown.android.lollipoptest.R;
import com.shansown.android.lollipoptest.sync.SyncHelper;
import com.shansown.android.lollipoptest.util.AccountUtils;
import com.shansown.android.lollipoptest.util.PrefUtils;

public class SyncSettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.sync_prefs);
        final ListPreference interval = (ListPreference) getPreferenceManager()
                .findPreference(PrefUtils.PREF_CUR_SYNC_INTERVAL);
        interval.setSummary(interval.getEntry());
    }

    @Override
    public void onResume() {
        super.onResume();
        PrefUtils.registerOnSharedPreferenceChangeListener(getActivity(), this);
    }

    @Override
    public void onPause() {
        super.onPause();
        PrefUtils.unregisterOnSharedPreferenceChangeListener(getActivity(), this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        Context context = getActivity();
        Account account = AccountUtils.getAccount(context);
        switch (key) {
            case PrefUtils.PREF_SYNC:
                if (!PrefUtils.isSyncEnabled(context)) {
                    SyncHelper.enableSync(context, account);
                } else {
                    SyncHelper.disableSync(context, account);
                }
                break;
            case PrefUtils.PREF_CUR_SYNC_INTERVAL:
                final ListPreference interval = (ListPreference) getPreferenceManager()
                        .findPreference(PrefUtils.PREF_CUR_SYNC_INTERVAL);
                interval.setSummary(interval.getEntry());

                /*final long interval = Long.parseLong(
                        ((ListPreference) getPreferenceManager().findPreference(key)).getValue());*/
                SyncHelper.updateSyncInterval(context, account,
                        Long.parseLong(interval.getValue()), true);
                break;
        }
    }
}