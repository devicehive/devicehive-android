package com.dataart.android.devicehive.device.commands;

import java.util.Map;

import android.os.Parcel;

import com.dataart.android.devicehive.DeviceData;
import com.dataart.android.devicehive.network.JsonNetworkCommand;

/**
 * Base command for Device Hive device-related commands.
 */
public abstract class DeviceCommand extends JsonNetworkCommand {

	protected final DeviceData deviceData;

	/**
	 * Construct new command with given device data.
	 * 
	 * @param deviceData
	 *            {@link DeviceData} object.
	 */
	public DeviceCommand(DeviceData deviceData) {
		this.deviceData = deviceData;
	}

	/**
	 * Get {@link DeviceData} object describing target device.
	 * 
	 * @return {@link DeviceData} object.
	 */
	public DeviceData getDeviceData() {
		return deviceData;
	}

	protected boolean deviceAuthenticationRequired() {
		return true;
	}

	@Override
	protected Map<String, String> getHeaders() {
		final Map<String, String> headers = super.getHeaders();
		if (deviceAuthenticationRequired()) {
			addDeviceAuthentication(headers);
		}
		return headers;
	}

	protected String getEncodedDeviceId() {
		return encodedString(deviceData.getId());
	}

	private void addDeviceAuthentication(Map<String, String> headers) {
		headers.put("Auth-DeviceID", deviceData.getId());
		headers.put("Auth-DeviceKey", deviceData.getKey());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(deviceData, 0);
	}

}
