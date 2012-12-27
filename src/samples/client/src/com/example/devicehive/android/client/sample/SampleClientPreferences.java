package com.example.devicehive.android.client.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class SampleClientPreferences {

	private final static String NAMESPACE = "devicehive.";

	private final Context context;
	private final SharedPreferences preferences;

	private final static String KEY_USERNAME = NAMESPACE
			.concat(".KEY_USERNAME");

	private final static String KEY_PASSWORD = NAMESPACE
			.concat(".KEY_PASSWORD");

	public SampleClientPreferences(final Context context) {
		this.context = context;
		this.preferences = context.getSharedPreferences(
				context.getPackageName() + "_devicehiveprefs",
				Context.MODE_PRIVATE);
	}

	public String getUsername() {
		return preferences.getString(KEY_USERNAME, null);
	}

	public String getPassword() {
		return preferences.getString(KEY_PASSWORD, null);
	}

	public void setCredentialsSync(String username, String password) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(KEY_USERNAME, username);
		editor.putString(KEY_PASSWORD, password);
		editor.commit();
	}

	public void setCredentialsAsync(final String username, final String password) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				setCredentialsSync(username, password);
				return null;
			}

		}.execute();
	}
}
