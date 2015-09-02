package com.shansown.androidtests.synctest;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.shansown.androidtests.BaseActivity;
import com.shansown.androidtests.R;

public class SyncSettingsActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_container);

    final Toolbar toolbar = getActionBarToolbar();
    toolbar.setNavigationOnClickListener(view -> finish());

    getFragmentManager().beginTransaction()
        .replace(R.id.container, new SyncSettingsFragment())
        .commit();
  }
}
