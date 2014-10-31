package com.shansown.android.lollipoptest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shansown.android.lollipoptest.transitions.custom.PicturesActivity;
import com.shansown.android.lollipoptest.transitions.lollipop.HomeActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Launch a class by name.
     *
     * @param clazz
     */
    private void launchClass(Class<?> clazz) {
        final Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    /**
     * Launch Transitions Lollipop demo. Called from onclick=""
     *
     * @param unused
     */
    public void transitionsLollipopDemo(final View unused) {
        launchClass(HomeActivity.class);
    }

    /**
     * Launch Transitions Custom demo. Called from onclick=""
     *
     * @param unused
     */
    public void transitionsCustomDemo(View unused) {
        launchClass(PicturesActivity.class);
    }
}
