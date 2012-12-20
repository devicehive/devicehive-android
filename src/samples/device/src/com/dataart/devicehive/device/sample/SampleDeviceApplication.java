package com.dataart.devicehive.device.sample;

import android.app.Application;

public class SampleDeviceApplication extends Application {

	private TestDevice device;
	
	@Override
	public void onCreate() {
		super.onCreate();
		device = new TestDevice(getApplicationContext());
		device.setDebugLoggingEnabled(BuildConfig.DEBUG);
		
		final SampleDevicePreferences prefs = new SampleDevicePreferences(this);
		String serverUrl = prefs.getServerUrl();
		if (serverUrl == null) {
			serverUrl = DeviceHiveConfig.API_ENDPOINT;
			prefs.setServerUrlSync(serverUrl);
		} 
		device.setApiEnpointUrl(serverUrl);
	}
	
	public TestDevice getDevice() {
		return device;
	}
	
}
