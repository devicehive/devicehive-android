package com.dataart.android.devicehive.device.commands;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.dataart.android.devicehive.DeviceData;
import com.dataart.android.devicehive.network.DeviceHiveResultReceiver;
import com.google.gson.Gson;

/**
 * Get device object with given identifier. As a result returns
 * {@link DeviceData} instance.
 */
public class GetDeviceCommand extends DeviceCommand {

	private final static String NAMESPACE = GetDeviceCommand.class.getName();

	private static final String DEVICE_KEY = NAMESPACE.concat(".DEVICE_KEY");

	/**
	 * Construct a new command.
	 * 
	 * @param deviceId
	 *            Device unique identifier.
	 */
	public GetDeviceCommand(DeviceData deviceData) {
		super(deviceData);
	}

	@Override
	protected String toJson(Gson gson) {
		return null;
	}

	@Override
	protected RequestType getRequestType() {
		return RequestType.GET;
	}

	@Override
	protected String getRequestPath() {
		String requestPath = String.format("/device/%s",
				encodedString(deviceData.getId()));
		return requestPath;
	}

	public static Parcelable.Creator<GetDeviceCommand> CREATOR = new Parcelable.Creator<GetDeviceCommand>() {

		@Override
		public GetDeviceCommand[] newArray(int size) {
			return new GetDeviceCommand[size];
		}

		@Override
		public GetDeviceCommand createFromParcel(Parcel source) {
			return new GetDeviceCommand((DeviceData)source.readParcelable(CLASS_LOADER));
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(deviceData, 0);
	}

	@Override
	protected int fromJson(final String response, final Gson gson,
			final Bundle resultData) {

		final DeviceData device = gson.fromJson(response,
				DeviceData.class);
		resultData.putParcelable(DEVICE_KEY, device);
		return DeviceHiveResultReceiver.MSG_HANDLED_RESPONSE;
	}

	/**
	 * Get {@link DeviceData} object from response {@link Bundle} container.
	 * 
	 * @param resultData
	 *            {@link Bundle} object containing required response data.
	 * @return {@link DeviceData} instance.
	 */
	public final static DeviceData getDevice(Bundle resultData) {
		return resultData.getParcelable(DEVICE_KEY);
	}

}
