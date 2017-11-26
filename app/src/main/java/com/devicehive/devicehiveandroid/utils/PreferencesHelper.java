/*
 *
 *
 *   PreferencesHelper.java
 *
 *   Copyright (C) 2017 DataArt
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.devicehive.devicehiveandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class PreferencesHelper {

    private static final String DEFAULT_PREFERENCES = "default_preferences";
    private SharedPreferences sharedPreferences;

    private static final String SERVER_URL = "serverUrl";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String DEVICE_ID = "deviceId";
    private static final String IS_WORKING = "isWorking";

    private static final String DEVICE_ID_FORMAT = "ANDROID-EXAMPLE-%s";

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
        String deviceId = sharedPreferences.getString(DEVICE_ID, "");
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = String.format(DEVICE_ID_FORMAT, UUID.randomUUID().toString()).substring(0, 48);
            putDeviceId(deviceId);
        }
        return deviceId;

    }

    public void putIsServiceWorking(boolean isWorking) {
        sharedPreferences.edit().putBoolean(IS_WORKING, isWorking).apply();
    }

    public boolean isServiceWorking() {
        return sharedPreferences.getBoolean(IS_WORKING, false);
    }

}
