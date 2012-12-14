package com.example.devicehive.android.client.sample;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends BaseActivity {

	private EditText serverUrlEdit;
	private EditText usernameEdit;
	private EditText passwordEdit;

	private SampleClientPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		serverUrlEdit = (EditText) findViewById(R.id.server_url_edit);
		usernameEdit = (EditText) findViewById(R.id.username_edit);
		passwordEdit = (EditText) findViewById(R.id.password_edit);

		prefs = new SampleClientPreferences(SettingsActivity.this);

		findViewById(R.id.undo_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						resetValues();
					}
				});
		findViewById(R.id.save_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						saveValues();
					}
				});

		resetValues();
	}

	private void resetValues() {
		serverUrlEdit.setText(prefs.getServerUrl());
		usernameEdit.setText(prefs.getUsername());
		passwordEdit.setText(prefs.getPassword());
	}

	private void saveValues() {
		final String serverUrl = serverUrlEdit.getText().toString();
		final String username = usernameEdit.getText().toString();
		final String password = passwordEdit.getText().toString();
		if (TextUtils.isEmpty(serverUrl)) {
			serverUrlEdit.setError("Server URL is required");
		} else if (TextUtils.isEmpty(username)) {
			usernameEdit.setError("Username is required");
		} else if (TextUtils.isEmpty(password)) {
			passwordEdit.setError("Password is required");
		} else {
			prefs.setCredentialsSync(username, password);
			prefs.setServerUrlSync(serverUrl);
			finish();
		}
	}
}
