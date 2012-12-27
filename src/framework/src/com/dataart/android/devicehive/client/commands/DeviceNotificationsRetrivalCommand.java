package com.dataart.android.devicehive.client.commands;

import java.lang.reflect.Type;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Parcel;

import com.dataart.android.devicehive.DeviceData;
import com.dataart.android.devicehive.Notification;
import com.dataart.android.devicehive.network.DeviceHiveResultReceiver;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Abstract base class for commands which retrieve notifications for given
 * device.
 * 
 */
public abstract class DeviceNotificationsRetrivalCommand extends
		NotificationsRetrivalCommand {

	protected final DeviceData deviceData;

	/**
	 * Construct command for given device and last received notification
	 * timestamp.
	 * 
	 * @param deviceData
	 *            {@link DeviceData} instance.
	 * @param lastNotificationPollTimestamp
	 *            Last received notification timestamp.
	 */
	public DeviceNotificationsRetrivalCommand(DeviceData deviceData,
			String lastNotificationPollTimestamp) {
		super(lastNotificationPollTimestamp);
		this.deviceData = deviceData;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(deviceData, flags);
		super.writeToParcel(dest, flags);
	}

	@Override
	protected String toJson(Gson gson) {
		return null;
	}

	@Override
	protected int fromJson(final String response, final Gson gson,
			final Bundle resultData) {

		Type listType = new TypeToken<ArrayList<Notification>>() {
		}.getType();

		ArrayList<Notification> notifications = gson.fromJson(response,
				listType);
		resultData.putParcelableArrayList(NOTIFICATIONS_KEY, notifications);
		return DeviceHiveResultReceiver.MSG_HANDLED_RESPONSE;
	}
}
