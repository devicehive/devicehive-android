package com.example.devicehive.android.client.sample;

import android.os.Bundle;
import android.widget.TextView;

import com.dataart.android.devicehive.Notification;
import com.example.devicehive.android.client.sample.SampleDeviceClient.NotificationsListener;

public class DeviceNotificationsActivity extends BaseActivity implements
		NotificationsListener {

	private static final String TAG = "DeviceNotificationsActivity";

	private SampleDeviceClient deviceClient;

	private TextView logTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_notifications);
		
		setTitle("Device Notifications");

		logTextView = (TextView) findViewById(R.id.log_text_view);

		SampleClientApplication app = (SampleClientApplication) getApplication();
		deviceClient = app.getClient();
		deviceClient.removeNotificationsListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		deviceClient.addNotificationsListener(this);
		deviceClient.startReceivingNotifications();
	}

	@Override
	protected void onPause() {
		super.onPause();
		deviceClient.stopReceivingNotifications();
		deviceClient.removeNotificationsListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onReceviceNotification(Notification notification) {
		logTextView.append(notification.toString() + "\n");
	}

}
