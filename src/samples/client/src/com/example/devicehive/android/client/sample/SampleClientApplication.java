package com.example.devicehive.android.client.sample;

import android.app.Application;

import com.dataart.android.devicehive.DeviceData;

public class SampleClientApplication extends Application {

	private SampleDeviceClient client;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public SampleDeviceClient setupClientForDevice(DeviceData device) {
		if (client != null) {
			if (!client.getDevice().getId().equals(device.getId())) {
				client.stopReceivingNotifications();
				client = getClientForDevice(device);
			}
		} else {
			client = getClientForDevice(device);
		}
		return client;
	}

	public SampleDeviceClient getClient() {
		return client;
	}

	private SampleDeviceClient getClientForDevice(DeviceData device) {
		SampleDeviceClient client = new SampleDeviceClient(
				getApplicationContext(), device);
		client.setApiEnpointUrl(DeviceHiveConfig.API_ENDPOINT);
		final SampleClientPreferences prefs = new SampleClientPreferences(this);
		client.setAuthorisation(prefs.getUsername(), prefs.getPassword());
		client.setDebugLoggingEnabled(BuildConfig.DEBUG);
		return client;
	}

}
