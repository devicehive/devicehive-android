package com.dataart.android.devicehive.client.commands;

import android.os.Parcel;
import android.os.Parcelable;

import com.dataart.android.devicehive.DeviceData;
import com.dataart.android.devicehive.Notification;

/**
 * Get all existing device {@link Notification}s starting from given timestamp.
 * This request returns immediately if there have been any notifications since
 * given timestamp. In the case when no notifications were found, the method
 * blocks until new notification is received. The blocking period is limited
 * (currently 30 seconds). As a result returns list of {@link Notification}.
 */
public class PollDeviceNotificationsCommand extends
		PollNotificationsCommand {

	private final DeviceData deviceData;

	/**
	 * Construct a new command.
	 * 
	 * @param deviceData
	 *            {@link DeviceData} instance.
	 * @param lastNotificationPollTimestamp
	 *            Timestamp which defines starting point in the past for
	 *            notifications.
	 */
	public PollDeviceNotificationsCommand(DeviceData deviceData,
			String lastNotificationPollTimestamp) {
		this(deviceData, lastNotificationPollTimestamp, null);
	}

	/**
	 * Construct a new command.
	 * 
	 * @param deviceData
	 *            {@link DeviceData} instance.
	 * @param lastNotificationPollTimestamp
	 *            Timestamp which defines starting point in the past for
	 *            notifications.
	 * @param waitTimeout
	 *            Waiting timeout in seconds.
	 */
	public PollDeviceNotificationsCommand(DeviceData deviceData,
			String lastNotificationPollTimestamp, Integer waitTimeout) {
		super(lastNotificationPollTimestamp, waitTimeout);
		this.deviceData = deviceData;
	}

	@Override
	protected String getRequestPath() {
		String requestPath = String.format("device/%s/notification/poll",
				encodedString(deviceData.getId()));
		if (lastNotificationPollTimestamp != null) {
			requestPath += "?timestamp="
					+ encodedString(lastNotificationPollTimestamp);
		}
		if (waitTimeout != null) {
			requestPath += lastNotificationPollTimestamp != null ? "&" : "?";
			requestPath += "waitTimeout=" + waitTimeout;
		}
		return requestPath;
	}

	public static Parcelable.Creator<PollDeviceNotificationsCommand> CREATOR = new Parcelable.Creator<PollDeviceNotificationsCommand>() {

		@Override
		public PollDeviceNotificationsCommand[] newArray(int size) {
			return new PollDeviceNotificationsCommand[size];
		}

		@Override
		public PollDeviceNotificationsCommand createFromParcel(Parcel source) {
			return new PollDeviceNotificationsCommand(
					(DeviceData) source.readParcelable(CLASS_LOADER),
					source.readString(),
					(Integer) source.readValue(CLASS_LOADER));
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(deviceData, flags);
		super.writeToParcel(dest, flags);
	}
}
