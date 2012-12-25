package com.dataart.android.devicehive.client.commands;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.dataart.android.devicehive.DeviceData;
import com.dataart.android.devicehive.EquipmentState;
import com.dataart.android.devicehive.network.DeviceHiveResultReceiver;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Get state information for equipment of given {@link DeviceData}. As a result
 * returns list of {@link EquipmentState} objects.
 */
public class GetDeviceEquipmentStateCommand extends DeviceClientCommand {

	private final static String NAMESPACE = GetDeviceEquipmentStateCommand.class
			.getName();

	private static final String DEVICE_KEY = NAMESPACE.concat(".DEVICE_KEY");
	private static final String EQUIPMENT_STATE_KEY = NAMESPACE
			.concat(".EQUIPMENT_STATE_KEY");

	private final DeviceData deviceData;

	/**
	 * Construct a new command with given {@link DeviceData} object.
	 * 
	 * @param deviceData
	 *            {@link DeviceData} instance.
	 */
	public GetDeviceEquipmentStateCommand(DeviceData deviceData) {
		this.deviceData = deviceData;
	}

	@Override
	protected RequestType getRequestType() {
		return RequestType.GET;
	}

	@Override
	protected String getRequestPath() {
		return String.format("device/%s/equipment",
				encodedString(deviceData.getId()));
	}

	@Override
	protected String toJson(Gson gson) {
		return null;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(deviceData, flags);
	}

	public static Parcelable.Creator<GetDeviceEquipmentStateCommand> CREATOR = new Parcelable.Creator<GetDeviceEquipmentStateCommand>() {

		@Override
		public GetDeviceEquipmentStateCommand[] newArray(int size) {
			return new GetDeviceEquipmentStateCommand[size];
		}

		@Override
		public GetDeviceEquipmentStateCommand createFromParcel(Parcel source) {
			return new GetDeviceEquipmentStateCommand(
					(DeviceData) source.readParcelable(CLASS_LOADER));
		}
	};

	@Override
	protected int fromJson(final String response, final Gson gson,
			final Bundle resultData) {

		Type listType = new TypeToken<ArrayList<EquipmentState>>() {
		}.getType();
		final ArrayList<EquipmentState> equipmentStates = new Gson().fromJson(
				response, listType);
		resultData.putParcelable(DEVICE_KEY, deviceData);
		resultData.putParcelableArrayList(EQUIPMENT_STATE_KEY, equipmentStates);
		return DeviceHiveResultReceiver.MSG_HANDLED_RESPONSE;
	}

	/**
	 * Get target {@link DeviceData} object.
	 * 
	 * @param resultData
	 *            {@link Bundle} object containing required response data.
	 * @return {@link DeviceData} instance.
	 */
	public final static DeviceData getDeviceData(Bundle resultData) {
		return resultData.getParcelable(DEVICE_KEY);
	}

	/**
	 * Get a list of {@link EquipmentState} which determine current state of
	 * device equipment.
	 * 
	 * @param resultData
	 *            {@link Bundle} object containing required response data.
	 * @return A list of {@link EquipmentState} which determine current state of
	 *         device equipment.
	 */
	public final static List<EquipmentState> getEquipmentState(Bundle resultData) {
		return resultData.getParcelableArrayList(EQUIPMENT_STATE_KEY);
	}

}
