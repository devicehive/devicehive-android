package com.dataart.devicehive.device.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dataart.android.devicehive.DeviceData;
import com.dataart.devicehive.device.sample.TestDevice.RegistrationListener;

public class DeviceActivity extends Activity implements RegistrationListener {

	private static final String EXTRA_DEVICE = DeviceActivity.class.getName()
			+ ".EXTRA_DEVICE";

	public static void start(Context context, DeviceData deviceData) {
		Intent intent = new Intent(context, DeviceActivity.class);
		intent.putExtra(EXTRA_DEVICE, deviceData);
		context.startActivity(intent);
	}

	private static final String TAG = "DeviceActivity";

	private TestDevice device;

	private Button sendNotificationButton;
	private Button viewCommandsButton;

	private TextView deviceIdTextView;
	private TextView deviceStatusTextView;

	private TextView deviceClassNameTextView;
	private TextView deviceClassVersionTextView;
	private TextView deviceClassIsPermanentTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);
		
		Log.d(TAG, "Activity recreated");

		sendNotificationButton = (Button) findViewById(R.id.send_notification_button);
		viewCommandsButton = (Button) findViewById(R.id.view_commands_button);

		deviceIdTextView = (TextView) findViewById(R.id.device_id_text_view);
		deviceStatusTextView = (TextView) findViewById(R.id.device_status_text_view);

		deviceClassNameTextView = (TextView) findViewById(R.id.device_class_name_text_view);
		deviceClassVersionTextView = (TextView) findViewById(R.id.device_class_version_text_view);
		deviceClassIsPermanentTextView = (TextView) findViewById(R.id.device_class_is_permanent_text_view);

		sendNotificationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(DeviceActivity.this,
						DeviceSendNotificationActivity.class));
			}
		});

		viewCommandsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(DeviceActivity.this,
						DeviceCommandsActivity.class));
			}
		});

		SampleDeviceApplication app = (SampleDeviceApplication) getApplication();
		device = app.getDevice();
	}

	@Override
	protected void onStart() {
		super.onStart();
		device.addDeviceListener(this);
		if (!device.isRegistered()) {
			device.registerDevice();
		} else {
			setDeviceData(device.getDeviceData());
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		device.removeDeviceListener(this);
	}

	@Override
	public void onDeviceRegistered() {
		setDeviceData(device.getDeviceData());
	}

	@Override
	public void onDeviceFailedToRegister() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder
				.setTitle("Error")
				.setMessage("Failed to register device")
				.setPositiveButton("Retry",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								device.registerDevice();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
		dialog.show();
	}

	private void setDeviceData(DeviceData deviceData) {
		setTitle(device.getDeviceData().getName());

		deviceIdTextView.setText(device.getDeviceData().getId());
		deviceStatusTextView.setText(device.getDeviceData().getStatus());

		deviceClassNameTextView.setText(device.getDeviceData().getDeviceClass()
				.getName());
		deviceClassVersionTextView.setText(device.getDeviceData()
				.getDeviceClass().getVersion());
		deviceClassIsPermanentTextView.setText(""
				+ device.getDeviceData().getDeviceClass().isPermanent());
	}

}
