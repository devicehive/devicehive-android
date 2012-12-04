package com.dataart.android.devicehive.device.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.dataart.android.devicehive.DeviceData;

public class GetDeviceCommandsCommand extends DeviceCommandsRetrivalCommand {

	public GetDeviceCommandsCommand(DeviceData deviceData,
			String lastCommandPollTimestamp) {
		super(deviceData, lastCommandPollTimestamp);
	}

	@Override
	protected String getRequestPath() {
		String requestPath = String.format("device/%s/command",
				getEncodedDeviceId());
		if (lastCommandPollTimestamp != null) {
			requestPath += "?start=" + encodedString(lastCommandPollTimestamp);
		}
		return requestPath;
	}

	public static Parcelable.Creator<GetDeviceCommandsCommand> CREATOR = new Parcelable.Creator<GetDeviceCommandsCommand>() {

		@Override
		public GetDeviceCommandsCommand[] newArray(int size) {
			return new GetDeviceCommandsCommand[size];
		}

		@Override
		public GetDeviceCommandsCommand createFromParcel(Parcel source) {
			return new GetDeviceCommandsCommand(
					(DeviceData) source.readParcelable(CLASS_LOADER),
					source.readString());
		}
	};

}
