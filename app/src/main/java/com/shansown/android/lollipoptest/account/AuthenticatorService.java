package com.shansown.android.lollipoptest.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import static com.shansown.android.lollipoptest.util.LogUtils.makeLogTag;

public class AuthenticatorService extends Service {

    private static final String TAG = makeLogTag(AuthenticatorService.class);

    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
        mAuthenticator = new Authenticator(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}