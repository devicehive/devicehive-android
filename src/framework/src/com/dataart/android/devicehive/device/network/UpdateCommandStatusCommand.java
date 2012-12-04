package com.dataart.android.devicehive.device.network;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.dataart.android.devicehive.Command;
import com.dataart.android.devicehive.DeviceData;
import com.dataart.android.devicehive.device.CommandResult;
import com.dataart.android.devicehive.network.DeviceHiveResultReceiver;
import com.google.gson.Gson;

public class UpdateCommandStatusCommand extends DeviceCommand {

	private final static String NAMESPACE = UpdateCommandStatusCommand.class
			.getName();

	private static final String COMMAND_KEY = NAMESPACE.concat(".COMMAND_KEY");

	private final Command command;
	private final CommandResult commandResult;

	public UpdateCommandStatusCommand(DeviceData deviceData, Command command,
			CommandResult commandResult) {
		super(deviceData);
		this.command = command;
		this.commandResult = commandResult;
	}

	@Override
	protected RequestType getRequestType() {
		return RequestType.PUT;
	}

	@Override
	protected String getRequestPath() {
		return String.format("device/%s/command/%d", getEncodedDeviceId(),
				command.getId());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeParcelable(command, 0);
		dest.writeParcelable(commandResult, 0);
	}

	public static Parcelable.Creator<UpdateCommandStatusCommand> CREATOR = new Parcelable.Creator<UpdateCommandStatusCommand>() {

		@Override
		public UpdateCommandStatusCommand[] newArray(int size) {
			return new UpdateCommandStatusCommand[size];
		}

		@Override
		public UpdateCommandStatusCommand createFromParcel(Parcel source) {
			return new UpdateCommandStatusCommand(
					(DeviceData) source.readParcelable(CLASS_LOADER),
					(Command) source.readParcelable(CLASS_LOADER),
					(CommandResult) source.readParcelable(CLASS_LOADER));
		}
	};

	@Override
	protected String toJson(Gson gson) {
		return gson.toJson(commandResult);
	}

	@Override
	protected int fromJson(final String response, final Gson gson,
			final Bundle resultData) {
		final Command command = gson.fromJson(response, Command.class);
		resultData.putParcelable(COMMAND_KEY, command);
		return DeviceHiveResultReceiver.MSG_HANDLED_RESPONSE;
	}

	public final static Command getUpdatedCommand(Bundle resultData) {
		return resultData.getParcelable(COMMAND_KEY);
	}
}
