package com.devicehive.devicehiveandroid.service;


import android.annotation.SuppressLint;

import com.devicehive.devicehiveandroid.utils.PreferencesHelper;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.TaskParams;

public class LocationScheduledTask extends GcmTaskService {
    public static final String TAG = "LocationScheduledTask";

    public static final int PERIOD_IN_SECONDS = 30;
    public static final int FLEX_IN_SECONDS = 2;


    public static PeriodicTask getPeriodicTask(String serverUrl, String refreshToken) {
        PreferencesHelper helper = PreferencesHelper.getInstance();
        helper.putRefreshToken(refreshToken.trim());
        helper.putServerUrl(serverUrl.trim());

        return new PeriodicTask.Builder()
                .setService(LocationScheduledTask.class)
                .setTag(TAG)
                .setRequiredNetwork(OneoffTask.NETWORK_STATE_CONNECTED)
                .setPeriod(PERIOD_IN_SECONDS)
                .setFlex(FLEX_IN_SECONDS)
                .setPersisted(true)
                .build();
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onRunTask(TaskParams taskParams) {
        LocationIntentService.startService(getApplicationContext());
        return GcmNetworkManager.RESULT_SUCCESS;
    }


}
