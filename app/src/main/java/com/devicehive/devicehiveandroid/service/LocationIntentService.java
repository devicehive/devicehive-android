package com.devicehive.devicehiveandroid.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.devicehive.devicehiveandroid.utils.PreferencesHelper;
import com.github.devicehive.client.model.Parameter;
import com.github.devicehive.client.model.TokenAuth;
import com.github.devicehive.client.service.Device;
import com.github.devicehive.client.service.DeviceHive;
import com.google.android.gms.gcm.GcmNetworkManager;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.Locale;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;


public class LocationIntentService extends IntentService {

    public static final String TAG = "LocationIntentService";

    public static final String LOCATION_FORMAT = "Latitude: %s Longitude: %s";
    public static final String DEVICE_ID_FORMAT = "ANDROID-EXAMPLE-%s";

    public static final String PARAMETER_NAME = "Location";
    public static final String NOTIFICATION_NAME = "Location";

    private DeviceHive deviceHive;
    private PreferencesHelper helper;

    public static void startService(Context context) {
        context.startService(new Intent(context, LocationIntentService.class));
    }

    public LocationIntentService() {
        super(TAG);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (helper == null) {
            helper = PreferencesHelper.getInstance();
        }
        String serverUrl = helper.getServerUrl();
        String refreshToken = helper.getRefreshToken();
        try {
            createDeviceHiveIfNotExists(serverUrl, refreshToken);
        } catch (NullPointerException e) {
            onError(e);
            return;
        }
        String deviceId = getOrCreateDeviceId();

        deviceHive.enableDebug(true);

        Device device = deviceHive.getDevice(deviceId);
        if (device == null) {
            onError(null);
            return;
        }

        helper.putIsServiceWorking(true);
        EventBus.getDefault().post(new MessageEvent());
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(this);
        locationProvider.getLastKnownLocation()
                .subscribe(location ->
                        Observable.just(location).subscribeOn(Schedulers.io()).subscribe(l -> {
                            Parameter locationParams = createLocationParams(l);
                            device.sendNotification(NOTIFICATION_NAME, Collections.singletonList(locationParams));

                        }, this::onError), this::onError);
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

    private String getOrCreateDeviceId() {
        if (helper == null) {
            helper = PreferencesHelper.getInstance();
        }
        String deviceId = helper.getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = String.format(DEVICE_ID_FORMAT, UUID.randomUUID().toString()).substring(0, 48);
            helper.putDeviceId(deviceId);
        }
        return deviceId;
    }

    private Parameter createLocationParams(Location location) {
        return new Parameter(PARAMETER_NAME,
                String.format(Locale.getDefault(), LOCATION_FORMAT,
                        Location.convert(location.getLatitude(), Location.FORMAT_DEGREES),
                        Location.convert(location.getLongitude(), Location.FORMAT_DEGREES)));
    }
}
