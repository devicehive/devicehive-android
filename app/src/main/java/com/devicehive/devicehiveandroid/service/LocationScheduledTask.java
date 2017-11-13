package com.devicehive.devicehiveandroid.service;


import android.annotation.SuppressLint;
import android.location.Location;

import com.devicehive.devicehiveandroid.service.model.LocationParam;
import com.devicehive.devicehiveandroid.utils.PreferencesHelper;
import com.github.devicehive.client.model.Parameter;
import com.github.devicehive.client.model.TokenAuth;
import com.github.devicehive.client.service.Device;
import com.github.devicehive.client.service.DeviceHive;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.TaskParams;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;

public class LocationScheduledTask extends GcmTaskService {
    public static final String TAG = "LocationScheduledTask";

    public static final int PERIOD_IN_SECONDS = 30;
    public static final int FLEX_IN_SECONDS = 2;

    public static final String PARAMETER_NAME = "location";
    public static final String NOTIFICATION_NAME = "Location Notification";

    private DeviceHive deviceHive;
    private PreferencesHelper helper;

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
        if (helper == null) {
            helper = PreferencesHelper.getInstance();
        }
        String serverUrl = helper.getServerUrl();
        String refreshToken = helper.getRefreshToken();
        try {
            createDeviceHiveIfNotExists(serverUrl, refreshToken);
        } catch (NullPointerException e) {
            onError(e);
            return GcmNetworkManager.RESULT_FAILURE;
        }
        String deviceId = helper.getDeviceId();

        deviceHive.enableDebug(true);

        Device device = deviceHive.getDevice(deviceId);
        if (device == null) {
            onError(null);
            return GcmNetworkManager.RESULT_FAILURE ;
        }

        helper.putIsServiceWorking(true);
        EventBus.getDefault().post(new MessageEvent());
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(getApplicationContext());
        locationProvider.getLastKnownLocation()
                .subscribe(location ->
                        Observable.just(location).subscribeOn(Schedulers.io()).subscribe(l -> {
                            Parameter locationParams = createLocationParams(l);
                            device.sendNotification(NOTIFICATION_NAME, Collections.singletonList(locationParams));

                        }, this::onError ), this::onError);
        return GcmNetworkManager.RESULT_SUCCESS;
    }

    private void onError(Throwable throwable) {
        helper.putIsServiceWorking(false);
        EventBus.getDefault().post(new MessageEvent());
        if (throwable != null) {
            throwable.printStackTrace();
        }
        GcmNetworkManager.getInstance(this).cancelTask(LocationScheduledTask.TAG, LocationScheduledTask.class);
    }

    private void createDeviceHiveIfNotExists(String serverUrl, String refreshToken) {

        if (deviceHive == null) {
            deviceHive = DeviceHive.getInstance().init(serverUrl,
                    new TokenAuth(refreshToken.trim()));
        }
    }

    private Parameter createLocationParams(Location location) {
        LocationParam param = new LocationParam();
        param.setLatitude(String.valueOf(location.getLatitude()));
        param.setLongitude(String.valueOf(location.getLongitude()));
        Gson gson = new Gson();
        return new Parameter(PARAMETER_NAME, gson.toJson(param));
    }


}
