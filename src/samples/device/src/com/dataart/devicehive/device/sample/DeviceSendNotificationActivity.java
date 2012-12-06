package com.dataart.devicehive.device.sample;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dataart.android.devicehive.Notification;
import com.dataart.devicehive.device.sample.TestDevice.NotificationListener;

public class DeviceSendNotificationActivity extends Activity implements
		NotificationListener {

	private static final String TAG = "DeviceSendCommandActivity";

	private TestDevice device;

	private Button sendNotificationButton;

	private TextView notificationNameEdit;
	private TextView equipmentCodeEdit;
	private TextView logTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_notification);

		setTitle("Send Notification");

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		sendNotificationButton = (Button) findViewById(R.id.send_notification_button);

		notificationNameEdit = (EditText) findViewById(R.id.notification_name_edit);
		equipmentCodeEdit = (EditText) findViewById(R.id.equipment_code_edit);

		logTextView = (TextView) findViewById(R.id.log_text_view);

		sendNotificationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendCommand();
			}
		});

		SampleDeviceApplication app = (SampleDeviceApplication) getApplication();
		device = app.getDevice();
	}

	@Override
	protected void onResume() {
		super.onResume();
		device.addNotificationListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		device.removeNotificationListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private void sendCommand() {
		String notificationName = notificationNameEdit.getText().toString();
		if (TextUtils.isEmpty(notificationName)) {
			notificationName = "TestNotificationAndroidFramework";
		}

		HashMap<String, Object> parameters = null;
		String equipmentCode = equipmentCodeEdit.getText().toString();
		if (!TextUtils.isEmpty(equipmentCode)) {
			parameters = new HashMap<String, Object>();
			parameters.put("equipment", equipmentCode);
		}

		device.sendNotification(new Notification(notificationName, parameters));
	}

	@Override
	public void onDeviceSentNotification(Notification notification) {
		logTextView.append(notification.toString() + "\n");
	}

	@Override
	public void onDeviceFailedToSendNotification(Notification notification) {
		logTextView.append("Failed to send notification: "
				+ notification.toString() + "\n");
	}

}
