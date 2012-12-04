package com.dataart.android.devicehive.device.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.dataart.android.devicehive.DeviceData;

/**
 * Poll for commands for given device starting from given date timestamp. In the
 * case when no commands were found, the server doesn't return response until a
 * new command is received. The blocking period is limited.
 */
public class PollDeviceCommandsCommand extends DeviceCommandsRetrivalCommand {

	public PollDeviceCommandsCommand(DeviceData deviceData,
			String lastCommandPollTimestamp) {
		super(deviceData, lastCommandPollTimestamp);
	}

	@Override
	protected String getRequestPath() {
		String requestPath = String.format("device/%s/command/poll",
				getEncodedDeviceId());
		if (lastCommandPollTimestamp != null) {
			requestPath += "?timestamp="
					+ encodedString(lastCommandPollTimestamp);
		}
		return requestPath;
	}

	public static Parcelable.Creator<PollDeviceCommandsCommand> CREATOR = new Parcelable.Creator<PollDeviceCommandsCommand>() {

		@Override
		public PollDeviceCommandsCommand[] newArray(int size) {
			return new PollDeviceCommandsCommand[size];
		}

		@Override
		public PollDeviceCommandsCommand createFromParcel(Parcel source) {
			return new PollDeviceCommandsCommand(
					(DeviceData) source.readParcelable(CLASS_LOADER),
					source.readString());
		}
	};

}
