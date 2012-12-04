package com.dataart.android.devicehive.device;

import java.util.HashMap;

import com.dataart.android.devicehive.Notification;

/**
 * Represents a {@link Notification} which is usually sent by an
 * {@link Equipment}.
 */
public class EquipmentNotification extends Notification {

	/**
	 * Construct a notification with given equipment code and additional
	 * equipment parameters dictionary and it's name inside {@link Notification}
	 * 's parameters dictionary.
	 * 
	 * @param equipmentCode
	 *            Equipment code.
	 * @param parametersName
	 *            Equipment parameters dictionary name inside
	 *            {@link Notification}'s parameters dictionary.
	 * @param equipmentParameters
	 *            Equipment parameters dictionary.
	 */
	public EquipmentNotification(String equipmentCode, String parametersName,
			HashMap<String, Object> equipmentParameters) {
		super("equipment", equipmentParameters(equipmentCode, parametersName,
				equipmentParameters));
	}

	private static HashMap<String, Object> equipmentParameters(
			String equipmentCode, String parametersName,
			HashMap<String, Object> equipmentParameters) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("equipment", equipmentCode);
		parameters.put(parametersName, equipmentParameters);
		return parameters;
	}
}
