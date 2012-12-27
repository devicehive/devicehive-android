package com.example.devicehive.android.client.sample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.dataart.android.devicehive.DeviceData;

public class DeviceInformationFragment extends SherlockFragment {

	private DeviceData deviceData;

	private TextView deviceIdTextView;
	private TextView deviceStatusTextView;
	private TextView deviceDataTextView;

	private TextView deviceClassNameTextView;
	private TextView deviceClassVersionTextView;
	private TextView deviceClassIsPermanentTextView;
	private TextView deviceClassDataTextView;

	public void setDeviceData(DeviceData deviceData) {
		this.deviceData = deviceData;
		if (isAdded()) {
			setupDeviceData(deviceData);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		setupDeviceData(deviceData);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_device_info, container,
				false);

		deviceIdTextView = (TextView) view
				.findViewById(R.id.device_id_text_view);
		deviceStatusTextView = (TextView) view
				.findViewById(R.id.device_status_text_view);
		deviceDataTextView = (TextView) view
				.findViewById(R.id.device_data_text_view);

		deviceClassNameTextView = (TextView) view
				.findViewById(R.id.device_class_name_text_view);
		deviceClassVersionTextView = (TextView) view
				.findViewById(R.id.device_class_version_text_view);
		deviceClassIsPermanentTextView = (TextView) view
				.findViewById(R.id.device_class_is_permanent_text_view);
		deviceClassDataTextView = (TextView) view
				.findViewById(R.id.device_class_data_text_view);
		return view;
	}

	private void setupDeviceData(DeviceData deviceData) {
		if (deviceData != null) {
			deviceIdTextView.setText(deviceData.getId());
			deviceStatusTextView.setText(deviceData.getStatus());
			deviceDataTextView
					.setText(deviceData.getData() != null ? deviceData
							.getData().toString() : "--");

			deviceClassNameTextView.setText(deviceData.getDeviceClass()
					.getName());
			deviceClassVersionTextView.setText(deviceData.getDeviceClass()
					.getVersion());
			deviceClassIsPermanentTextView.setText(""
					+ deviceData.getDeviceClass().isPermanent());
			deviceClassDataTextView.setText(deviceData.getDeviceClass()
					.getData() != null ? deviceData.getDeviceClass().getData()
					.toString() : "--");
		}
	}

}
