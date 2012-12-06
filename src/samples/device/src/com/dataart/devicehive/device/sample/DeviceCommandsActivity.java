package com.dataart.devicehive.device.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.dataart.android.devicehive.Command;
import com.dataart.devicehive.device.sample.TestDevice.CommandListener;

public class DeviceCommandsActivity extends Activity implements
		CommandListener {

	private static final String TAG = "DeviceCommandsActivity";

	private TestDevice device;

	private TextView logTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_commands);

		setTitle("Device Commands");

		logTextView = (TextView) findViewById(R.id.log_text_view);

		SampleDeviceApplication app = (SampleDeviceApplication) getApplication();
		device = app.getDevice();
	}

	@Override
	protected void onResume() {
		super.onResume();
		device.addCommandListener(this);
		device.startProcessingCommands();
	}

	@Override
	protected void onPause() {
		super.onPause();
		device.stopProcessingCommands();
		device.removeCommandListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onDeviceReceivedCommand(Command command) {
		logTextView.append(command.toString() + "\n");
	}

}
