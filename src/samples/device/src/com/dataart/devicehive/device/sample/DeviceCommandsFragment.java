package com.dataart.devicehive.device.sample;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.dataart.android.devicehive.Command;

public class DeviceCommandsFragment extends SherlockFragment {

	private List<Command> commands;

	private TextView logTextView;

	public void setCommands(List<Command> commands) {
		this.commands = commands;
		if (isAdded()) {
			setupCommands(commands);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_device_commands,
				container, false);

		logTextView = (TextView) view.findViewById(R.id.log_text_view);
		setupCommands(commands);
		return view;
	}

	private void setupCommands(List<Command> commands) {
		if (commands != null) {
			logTextView.setText("");
			for (Command command : commands) {
				logTextView.append(command.toString() + "\n");
			}
		}
	}

}
