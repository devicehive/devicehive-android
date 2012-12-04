package com.dataart.android.devicehive.client.network;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.dataart.android.devicehive.DeviceClass;
import com.dataart.android.devicehive.EquipmentData;
import com.dataart.android.devicehive.network.DeviceHiveResultReceiver;
import com.google.gson.Gson;

/**
 * Command which retrieves equipment for {@link DeviceClass}. As a result
 * returns list of {@link EquipmentData}.
 */
public class GetDeviceClassEquipmentCommand extends DeviceClientCommand {

	private final static String NAMESPACE = GetDeviceClassEquipmentCommand.class
			.getName();

	private static final String DEVICE_CLASS_KEY = NAMESPACE
			.concat(".DEVICE_CLASS_KEY");
	private static final String EQUIPMENT_KEY = NAMESPACE
			.concat(".EQUIPMENT_KEY");

	private final DeviceClass deviceClass;

	/**
	 * Construct a new command with given {@link DeviceClass}.
	 * 
	 * @param deviceClass
	 *            {@link DeviceClass} instance.
	 */
	public GetDeviceClassEquipmentCommand(DeviceClass deviceClass) {
		this.deviceClass = deviceClass;
	}

	@Override
	protected RequestType getRequestType() {
		return RequestType.GET;
	}

	@Override
	protected String getRequestPath() {
		return String.format("device/class/%d", deviceClass.getId());
	}

	@Override
	protected String toJson(Gson gson) {
		return null;
	}

	private class DeviceClassEquipment extends DeviceClass {

		ArrayList<EquipmentData> equipment;

		DeviceClassEquipment(int id, String name, String version,
				boolean isPermanent, int offlineTimeout) {
			super(id, name, version, isPermanent, offlineTimeout);
		}

	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(deviceClass, flags);
	}

	public static Parcelable.Creator<GetDeviceClassEquipmentCommand> CREATOR = new Parcelable.Creator<GetDeviceClassEquipmentCommand>() {

		@Override
		public GetDeviceClassEquipmentCommand[] newArray(int size) {
			return new GetDeviceClassEquipmentCommand[size];
		}

		@Override
		public GetDeviceClassEquipmentCommand createFromParcel(Parcel source) {
			return new GetDeviceClassEquipmentCommand(
					(DeviceClass) source.readParcelable(CLASS_LOADER));
		}
	};

	@Override
	protected int fromJson(final String response, final Gson gson,
			final Bundle resultData) {

		final DeviceClassEquipment deviceClassEquipment = new Gson().fromJson(
				response, DeviceClassEquipment.class);
		resultData.putParcelable(DEVICE_CLASS_KEY, deviceClassEquipment);
		resultData.putParcelableArrayList(EQUIPMENT_KEY,
				deviceClassEquipment.equipment);
		return DeviceHiveResultReceiver.MSG_HANDLED_RESPONSE;
	}

	public final static DeviceClass getDeviceClass(Bundle resultData) {
		return resultData.getParcelable(DEVICE_CLASS_KEY);
	}

	/**
	 * Get a list of {@link EquipmentData} which belong to target
	 * {@link DeviceClass} object.
	 * 
	 * @param resultData
	 *            {@link Bundle} object containing required response data.
	 * @return A list of {@link EquipmentData} which belong to target
	 *         {@link DeviceClass} object.
	 */
	public final static List<EquipmentData> getEquipment(Bundle resultData) {
		return resultData.getParcelableArrayList(EQUIPMENT_KEY);
	}

}
