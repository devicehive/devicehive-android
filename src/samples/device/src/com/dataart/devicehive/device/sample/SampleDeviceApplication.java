package com.dataart.devicehive.device.sample;

import android.app.Application;

public class SampleDeviceApplication extends Application {

	private TestDevice device;
	
	@Override
	public void onCreate() {
		super.onCreate();
		device = new TestDevice(getApplicationContext());
		device.setApiEnpointUrl("http://ecloud.dataart.com/ecapi6");
		device.setDebugLoggingEnabled(BuildConfig.DEBUG);
	}
	
	public TestDevice getDevice() {
		return device;
	}
	
}
