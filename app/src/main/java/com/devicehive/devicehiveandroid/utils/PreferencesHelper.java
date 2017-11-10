package com.devicehive.devicehiveandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PreferencesHelper {

    private static final String DEFAULT_PREFERENCES = "default_preferences";
    private SharedPreferences sharedPreferences;

    private static final String SERVER_URL = "serverUrl";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String DEVICE_ID = "deviceId";
    private static final String IS_WORKING = "isWorking";

    private PreferencesHelper() {
    }

    public static PreferencesHelper getInstance() {
        return PreferencesHelper.InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        static final PreferencesHelper INSTANCE = new PreferencesHelper();
    }

    public void clearPreferences() {
        sharedPreferences.edit().clear().apply();
    }

    public void init(Context context) {
        sharedPreferences = context.getSharedPreferences(DEFAULT_PREFERENCES, MODE_PRIVATE);
    }

    public void putRefreshToken(String refreshToken) {
        sharedPreferences.edit().putString(REFRESH_TOKEN, refreshToken).apply();
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(REFRESH_TOKEN, "");
    }

    public void putServerUrl(String url) {
        sharedPreferences.edit().putString(SERVER_URL, url).apply();
    }

    public String getServerUrl() {
        return sharedPreferences.getString(SERVER_URL, "");

    }

    public void putDeviceId(String deviceId) {
        sharedPreferences.edit().putString(DEVICE_ID, deviceId).apply();
    }

    public String getDeviceId() {
        return sharedPreferences.getString(DEVICE_ID, "");

    }

    public void putIsServiceWorking(boolean isWorking) {
        sharedPreferences.edit().putBoolean(IS_WORKING, isWorking).apply();
    }

    public boolean isServiceWorking() {
       return sharedPreferences.getBoolean(IS_WORKING, false);
    }

}
