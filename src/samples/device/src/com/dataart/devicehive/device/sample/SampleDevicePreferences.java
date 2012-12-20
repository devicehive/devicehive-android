package com.dataart.devicehive.device.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class SampleDevicePreferences {

	private final static String NAMESPACE = "devicehive.";

	private final Context context;
	private final SharedPreferences preferences;
	
	private final static String KEY_SERVER_URL = NAMESPACE
			.concat(".KEY_SERVER_URL");

	public SampleDevicePreferences(final Context context) {
		this.context = context;
		this.preferences = context.getSharedPreferences(
				context.getPackageName() + "_devicehiveprefs",
				Context.MODE_PRIVATE);
	}
	
	public String getServerUrl() {
		return preferences.getString(KEY_SERVER_URL, null);
	}
	
	public void setServerUrlSync(String serverUrl) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(KEY_SERVER_URL, serverUrl);
		editor.commit();
	}
}
