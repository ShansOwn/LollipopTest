package com.shansown.android.lollipoptest.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shansown.android.lollipoptest.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Launch a class by name.
     *
     * @param clasz
     */
    private void launchClass(Class<?> clasz) {
        final Intent intent = new Intent(this, clasz);
        startActivity(intent);
    }

    /**
     * Launch Transitions demo. Called from onclick=""
     *
     * @param unused
     */
    public void transitionsDemo(final View unused) {
        launchClass(HomeActivity.class);
    }
}
