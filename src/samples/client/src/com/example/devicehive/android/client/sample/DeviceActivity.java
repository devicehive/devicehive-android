package com.example.devicehive.android.client.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dataart.android.devicehive.DeviceData;

public class DeviceActivity extends BaseActivity {

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

	private Button sendCommandButton;
	private Button viewNotificationsButton;

	private TextView deviceIdTextView;
	private TextView deviceStatusTextView;

	private TextView deviceClassNameTextView;
	private TextView deviceClassVersionTextView;
	private TextView deviceClassIsPermanentTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);

		sendCommandButton = (Button) findViewById(R.id.send_command_button);
		viewNotificationsButton = (Button) findViewById(R.id.view_notifications_button);

		deviceIdTextView = (TextView) findViewById(R.id.device_id_text_view);
		deviceStatusTextView = (TextView) findViewById(R.id.device_status_text_view);

		deviceClassNameTextView = (TextView) findViewById(R.id.device_class_name_text_view);
		deviceClassVersionTextView = (TextView) findViewById(R.id.device_class_version_text_view);
		deviceClassIsPermanentTextView = (TextView) findViewById(R.id.device_class_is_permanent_text_view);

		sendCommandButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(DeviceActivity.this,
						DeviceSendCommandActivity.class));
			}
		});

		viewNotificationsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(DeviceActivity.this,
						DeviceNotificationsActivity.class));
			}
		});

		device = getIntent().getExtras().getParcelable(EXTRA_DEVICE);
		if (device == null) {
			throw new IllegalArgumentException(
					"Device extra should be provided");
		}
		setTitle(device.getName());

		deviceIdTextView.setText(device.getId());
		deviceStatusTextView.setText(device.getStatus());

		deviceClassNameTextView.setText(device.getDeviceClass().getName());
		deviceClassVersionTextView
				.setText(device.getDeviceClass().getVersion());
		deviceClassIsPermanentTextView.setText(""
				+ device.getDeviceClass().isPermanent());

		SampleClientApplication app = (SampleClientApplication) getApplication();
		deviceClient = app.setupClientForDevice(device);
	}

}
