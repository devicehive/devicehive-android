package com.dataart.android.devicehive;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a device notification, a unit of information dispatched from
 * {@link Client}s.
 */
public class Notification implements Parcelable {
	private int id;
	@SerializedName("notification")
	private String name;
	private String timestamp;
	private HashMap<String, Object> parameters;

	/* package */Notification(int id, String name, String timestamp,
			HashMap<String, Object> parameters) {
		this.id = id;
		this.name = name;
		this.timestamp = timestamp;
		this.parameters = parameters;
	}

	/**
	 * Construct a new notification with given name and parameters.
	 * 
	 * @param name
	 *            Notification name.
	 * @param parameters
	 *            Notification parameters.
	 */
	public Notification(String name, HashMap<String, Object> parameters) {
		this(-1, name, null, parameters);
	}

	/**
	 * Get notification identifier.
	 * 
	 * @return Notification identifier.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Get notification name.
	 * 
	 * @return Notification name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get notification timestamp(UTC).
	 * 
	 * @return Notification timestamp(UTC).
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * Get notification parameters dictionary.
	 * 
	 * @return Notification parameters dictionary.
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeSerializable(timestamp);
		dest.writeSerializable(parameters);
	}

	public static Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() {

		@Override
		public Notification[] newArray(int size) {
			return new Notification[size];
		}

		@SuppressWarnings("unchecked")
		@Override
		public Notification createFromParcel(Parcel source) {
			return new Notification(source.readInt(), source.readString(),
					source.readString(),
					(HashMap<String, Object>) source.readSerializable());
		}
	};

	@Override
	public String toString() {
		return "Notification [id=" + id + ", name=" + name + ", timestamp="
				+ timestamp + ", parameters=" + parameters + "]";
	}

}
