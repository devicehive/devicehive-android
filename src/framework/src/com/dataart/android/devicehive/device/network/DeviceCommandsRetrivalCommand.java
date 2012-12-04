package com.dataart.android.devicehive.device.network;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Parcel;

import com.dataart.android.devicehive.Command;
import com.dataart.android.devicehive.DeviceData;
import com.dataart.android.devicehive.network.DeviceHiveResultReceiver;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public abstract class DeviceCommandsRetrivalCommand extends DeviceCommand {

	private final static String NAMESPACE = DeviceCommandsRetrivalCommand.class
			.getName();

	private static final String COMMANDS_KEY = NAMESPACE
			.concat(".COMMANDS_KEY");

	protected final String lastCommandPollTimestamp;

	public DeviceCommandsRetrivalCommand(DeviceData deviceData,
			String lastCommandPollTimestamp) {
		super(deviceData);
		this.lastCommandPollTimestamp = lastCommandPollTimestamp;
	}

	@Override
	protected RequestType getRequestType() {
		return RequestType.GET;
	}

	@Override
	protected String toJson(Gson gson) {
		return null;
	}

	@Override
	protected int fromJson(final String response, final Gson gson,
			final Bundle resultData) {

		Type listType = new TypeToken<ArrayList<Command>>() {
		}.getType();

		ArrayList<Command> commands = new Gson().fromJson(response, listType);
		resultData.putParcelableArrayList(COMMANDS_KEY, commands);
		return DeviceHiveResultReceiver.MSG_HANDLED_RESPONSE;
	}

	public final static List<Command> getCommands(Bundle resultData) {
		return resultData.getParcelableArrayList(COMMANDS_KEY);
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(lastCommandPollTimestamp);
	}
}
