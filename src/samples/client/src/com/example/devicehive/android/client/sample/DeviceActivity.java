package com.example.devicehive.android.client.sample;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dataart.android.devicehive.Command;
import com.dataart.android.devicehive.DeviceData;
import com.dataart.android.devicehive.Notification;
import com.example.devicehive.android.client.sample.SampleDeviceClient.NotificationsListener;

public class DeviceActivity extends BaseActivity implements
		NotificationsListener {

	private static final String EXTRA_DEVICE = DeviceActivity.class.getName()
			+ ".EXTRA_DEVICE";

	public static void start(Context context, DeviceData deviceData) {
		Intent intent = new Intent(context, DeviceActivity.class);
		intent.putExtra(EXTRA_DEVICE, deviceData);
		context.startActivity(intent);
	}

	private static final String TAG = "DeviceActivity";

	private DeviceData device;
	private SampleDeviceClient deviceClient;

	private TextView logTextView;
	private Button sendCommandButton;
	private EditText commandNameEdit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);

		logTextView = (TextView) findViewById(R.id.log_text_view);
		sendCommandButton = (Button) findViewById(R.id.send_command_button);
		commandNameEdit = (EditText) findViewById(R.id.command_name_editText);
		sendCommandButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendCommand();
			}
		});

		device = getIntent().getExtras().getParcelable(EXTRA_DEVICE);
		if (device == null) {
			throw new IllegalArgumentException(
					"Device extra should be provided");
		}
		setTitle(device.getName());

		SampleClientApplication app = (SampleClientApplication) getApplication();
		deviceClient = app.setupClientForDevice(device);
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

	private void sendCommand() {
		String command = commandNameEdit.getText().toString();
		if (TextUtils.isEmpty(command)) {
			command = "TestCommandAndroidFramework";
		}
		final HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("equipment", "Echo iOS Equipment code");
		deviceClient.sendCommand(new Command(command, parameters));
	}

}
