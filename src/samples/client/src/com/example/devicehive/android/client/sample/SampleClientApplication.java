package com.example.devicehive.android.client.sample;

import android.app.Application;

import com.dataart.android.devicehive.DeviceData;

public class SampleClientApplication extends Application {

	private SampleDeviceClient client;

	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	public void resetClient() {
		if (client != null) {
			client.stopReceivingNotifications();
			client.clearAllListeners();
			client = null;
		}
	}

	public SampleDeviceClient setupClientForDevice(DeviceData device) {
		if (client != null) {
			if (!client.getDevice().getId().equals(device.getId())) {
				resetClient();
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
		final SampleClientPreferences prefs = new SampleClientPreferences(this);
		String serverUrl = prefs.getServerUrl();
		if (serverUrl == null) {
			serverUrl = DeviceHiveConfig.API_ENDPOINT;
			prefs.setServerUrlSync(serverUrl);
		} 
		client.setApiEnpointUrl(serverUrl);
		client.setAuthorisation(prefs.getUsername(), prefs.getPassword());
		client.setDebugLoggingEnabled(BuildConfig.DEBUG);
		return client;
	}

}
