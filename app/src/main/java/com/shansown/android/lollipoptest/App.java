package com.shansown.android.lollipoptest;

import android.app.Application;
import android.content.Context;

import com.squareup.picasso.Picasso;

public class App extends Application {

    private static Context sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader();
    }

    private void initImageLoader() {
        if (BuildConfig.DEBUG) {
            /* Add debug images sources indicators
               Red - network
               Yellow - disk
               Green - memory */
            Picasso.with(App.getAppContext()).setIndicatorsEnabled(true);
        }
    }

    public static Context getAppContext() {
        return sAppContext;
    }
}
