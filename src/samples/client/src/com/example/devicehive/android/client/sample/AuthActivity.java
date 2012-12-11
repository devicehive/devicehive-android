package com.example.devicehive.android.client.sample;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AuthActivity extends BaseActivity {

	private Button authButton;
	private EditText usernameEdit;
	private EditText passwordEdit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);

		authButton = (Button) findViewById(R.id.auth_button);
		usernameEdit = (EditText) findViewById(R.id.username_edit);
		passwordEdit = (EditText) findViewById(R.id.password_edit);
		
		final SampleClientPreferences prefs = new SampleClientPreferences(
				AuthActivity.this);
		
		if (prefs.getUsername() != null) {
			usernameEdit.setText(prefs.getUsername());
		}
		
		if (prefs.getPassword() != null) {
			passwordEdit.setText(prefs.getPassword());
		}

		authButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(usernameEdit.getText())) {
					usernameEdit.setError("Username is required");
				} else if (TextUtils.isEmpty(passwordEdit.getText())) {
					passwordEdit.setError("Password is required");
				} else {
					prefs.setCredentialsSync(usernameEdit.getText().toString(),
							passwordEdit.getText().toString());
					startActivity(new Intent(AuthActivity.this,
							NetworksActivity.class));
					finish();
				}
			}
		});
		
		
	}
}
