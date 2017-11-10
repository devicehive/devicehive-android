package com.devicehive.devicehiveandroid.service;


import android.annotation.SuppressLint;
import android.location.Location;
import android.text.TextUtils;

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

import java.util.Collections;
import java.util.Locale;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;
import timber.log.Timber;

public class LocationService extends GcmTaskService {
    public static final String TAG = "LocationService";
    public static final String LOCATION_FORMAT = "Latitude: %s Longitude: %s";
    public static final String DEVICE_ID_FORMAT = "ANDROID-EXAMPLE-%s";

    public static final String PARAMETER_NAME = "Location";
    public static final String NOTIFICATION_NAME = "Location";
    public static final int PERIOD_IN_SECONDS = 30;
    public static final int FLEX_IN_SECONDS = 2;
    private DeviceHive deviceHive;


    public static PeriodicTask getPeriodicTask(String serverUrl, String refreshToken) {
        PreferencesHelper helper = PreferencesHelper.getInstance();
        helper.putRefreshToken(refreshToken.trim());
        helper.putServerUrl(serverUrl.trim());

        return new PeriodicTask.Builder()
                .setService(LocationService.class)
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
        PreferencesHelper helper = PreferencesHelper.getInstance();

        String serverUrl = helper.getServerUrl();
        String refreshToken = helper.getRefreshToken();
        createDeviceHiveIfNotExists(serverUrl, refreshToken);

        String deviceId = helper.getDeviceId();

        if (TextUtils.isEmpty(deviceId)) {
            deviceId = String.format(DEVICE_ID_FORMAT, UUID.randomUUID().toString()).substring(0, 48);
            helper.putDeviceId(deviceId);
        }
        Device device = deviceHive.getDevice(deviceId);

        if (device == null) {
            return GcmNetworkManager.RESULT_FAILURE;
        }

        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(getApplicationContext());
        locationProvider.getLastKnownLocation()
                .subscribe(location ->
                        Observable.just(location).subscribeOn(Schedulers.io()).subscribe(l -> {
                            Parameter locationParams = new Parameter(PARAMETER_NAME,
                                    String.format(Locale.getDefault(), LOCATION_FORMAT,
                                            Location.convert(l.getLatitude(), Location.FORMAT_DEGREES),
                                            Location.convert(l.getLongitude(), Location.FORMAT_DEGREES)));
                            device.sendNotification(NOTIFICATION_NAME, Collections.singletonList(locationParams));

                        }, Throwable::printStackTrace), Throwable::printStackTrace);
        helper.putIsServiceWorking(true);
        return GcmNetworkManager.RESULT_SUCCESS;
    }

    private void createDeviceHiveIfNotExists(String serverUrl, String refreshToken) {
        if (deviceHive == null) {
            deviceHive = DeviceHive.getInstance().init(serverUrl,
                    new TokenAuth(refreshToken.trim()));
        }
    }
}
