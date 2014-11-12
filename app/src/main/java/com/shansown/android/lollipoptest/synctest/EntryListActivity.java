package com.shansown.android.lollipoptest.synctest;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.shansown.android.lollipoptest.BaseActivity;
import com.shansown.android.lollipoptest.R;
import com.shansown.android.lollipoptest.util.AccountUtils;

public class EntryListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        final Toolbar toolbar = getActionBarToolbar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new EntryListFragment())
                .commit();
    }
}