package com.example.devicehive.android.client.sample;

import java.util.HashMap;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dataart.android.devicehive.Command;
import com.example.devicehive.android.client.sample.SampleDeviceClient.CommandListener;

public class DeviceSendCommandActivity extends BaseActivity implements
		CommandListener {

	private static final String TAG = "DeviceSendCommandActivity";

	private SampleDeviceClient deviceClient;

	private Button sendCommandButton;

	private TextView commandNameEdit;
	private TextView equipmentCodeEdit;
	private TextView logTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_command);
		
		setTitle("Send Command");
		
		getWindow().setSoftInputMode(
			      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		sendCommandButton = (Button) findViewById(R.id.send_command_button);

		commandNameEdit = (EditText) findViewById(R.id.command_name_edit);
		equipmentCodeEdit = (EditText) findViewById(R.id.equipment_code_edit);
		
		logTextView = (TextView) findViewById(R.id.log_text_view);

		sendCommandButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendCommand();
			}
		});

		SampleClientApplication app = (SampleClientApplication) getApplication();
		deviceClient = app.getClient();
		deviceClient.removeCommandListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		deviceClient.addCommandListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		deviceClient.removeCommandListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	public void onStartSendindCommand(Command command) {
		logTextView.append("Start sending command: " + command.getCommand() + "\n");
	}
	
	@Override
	public void onFinishSendindCommand(Command command) {
		logTextView.append("Finish sending command: " + command.getCommand() + "\n");
	}
	
	@Override
	public void onFailSendindCommand(Command command) {
		logTextView.append("Fail sending command: " + command.getCommand() + "\n");
	}

	private void sendCommand() {
		String command = commandNameEdit.getText().toString();
		if (TextUtils.isEmpty(command)) {
			command = "TestCommandAndroidFramework";
		}
		
		HashMap<String, Object> parameters = null;
		String equipmentCode = equipmentCodeEdit.getText().toString();
		if (!TextUtils.isEmpty(equipmentCode)) {
			parameters = new HashMap<String, Object>();
			parameters.put("equipment", equipmentCode);
		}
		
		deviceClient.sendCommand(new Command(command, parameters));
	}

}
