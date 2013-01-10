package com.dataart.android.devicehive.device.commands;

import android.os.Parcel;
import android.os.Parcelable;

import com.dataart.android.devicehive.DeviceData;

/**
 * Poll for commands for given device starting from given date timestamp. In the
 * case when no commands were found, the server doesn't return response until a
 * new command is received. The blocking period is limited.
 */
public class PollDeviceCommandsCommand extends DeviceCommandsRetrivalCommand {

	private final Integer waitTimeout;

	/**
	 * Construct a new command with given {@link DeviceData} and last received
	 * command timestamp.
	 * 
	 * @param deviceData
	 *            {@link DeviceData} instance.
	 * @param lastCommandPollTimestamp
	 *            Timestamp of the last received command. If null value is
	 *            passed then server's timestamp will be used instead.
	 */
	public PollDeviceCommandsCommand(DeviceData deviceData,
			String lastCommandPollTimestamp) {
		this(deviceData, lastCommandPollTimestamp, null);
	}

	/**
	 * Construct a new command with given {@link DeviceData} and last received
	 * command timestamp.
	 * 
	 * @param deviceData
	 *            {@link DeviceData} instance.
	 * @param lastCommandPollTimestamp
	 *            Timestamp of the last received command. If null value is
	 *            passed then server's timestamp will be used instead.
	 * @param waitTimeout
	 *            Waiting timeout in seconds.
	 */
	public PollDeviceCommandsCommand(DeviceData deviceData,
			String lastCommandPollTimestamp, Integer waitTimeout) {
		super(deviceData, lastCommandPollTimestamp);
		this.waitTimeout = waitTimeout;
	}

	@Override
	protected String getRequestPath() {
		String requestPath = String.format("device/%s/command/poll",
				getEncodedDeviceId());
		if (lastCommandPollTimestamp != null) {
			requestPath += "?timestamp="
					+ encodedString(lastCommandPollTimestamp);
		}
		if (waitTimeout != null) {
			requestPath += lastCommandPollTimestamp != null ? "&" : "?";
			requestPath += "waitTimeout=" + waitTimeout;
		}
		return requestPath;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeValue(waitTimeout);
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
					source.readString(),
					(Integer) source.readValue(CLASS_LOADER));
		}
	};

}
