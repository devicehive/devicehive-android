package com.devicehive.devicehiveandroid;

import android.app.Application;

import com.devicehive.devicehiveandroid.utils.PreferencesHelper;

import timber.log.Timber;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferencesHelper.getInstance().init(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
