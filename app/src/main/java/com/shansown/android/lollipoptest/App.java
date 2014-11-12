package com.shansown.android.lollipoptest;

import android.app.Application;
import android.content.Context;

import com.squareup.picasso.Picasso;

import static com.shansown.android.lollipoptest.util.LogUtils.makeLogTag;

public class App extends Application {

    private static final String TAG = makeLogTag(App.class);

    private static Context sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = getApplicationContext();
        initImageLoader();
    }

    private void initImageLoader() {
        if (BuildConfig.DEBUG) {
            /* Add debug images sources indicators
               Red - network
               Yellow - disk
               Green - memory */
            Picasso.with(sAppContext).setIndicatorsEnabled(true);
        }
    }

    public static Context getAppContext() {
        return sAppContext;
    }
}