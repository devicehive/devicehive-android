package com.dataart.devicehive.device.sample;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.dataart.android.devicehive.EquipmentData;
import com.dataart.android.devicehive.Notification;

public class DeviceSendNotificationFragment extends SherlockFragment {

	private static final String TAG = "DeviceSendNotificationFragment";

	private Button sendNotificationButton;

	private TextView notificationNameEdit;
	private LinearLayout parametersContainer;

	private Spinner equipmentSpinner;

	private NotificationSender notificationSender;
	private ParameterProvider parameterProvider;

	private List<EquipmentData> equipment;
	private List<Parameter> parameters = new LinkedList<Parameter>();

	public static class Parameter {
		public final String name;
		public final String value;

		public Parameter(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}

	public interface NotificationSender {
		void sendNotification(Notification notification);
	}

	public interface ParameterProvider {
		void queryParameter();
	}

	public void setNotificationSender(NotificationSender notificationSender) {
		this.notificationSender = notificationSender;
	}

	public void setParameterProvider(ParameterProvider parameterProvider) {
		this.parameterProvider = parameterProvider;
	}

	public void setEquipment(List<EquipmentData> equipment) {
		this.equipment = equipment;
		setupEquipmentSpinner(equipment);
	}

	public void addParameter(String name, String value) {
		this.parameters.add(new Parameter(name, value));
		setupParameters(this.parameters);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		notificationSender = (NotificationSender) activity;
	}

	@Override
	public void onResume() {
		super.onResume();
		setupEquipmentSpinner(equipment);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_send_notification, container,
				false);

		sendNotificationButton = (Button) view
				.findViewById(R.id.send_notification_button);

		notificationNameEdit = (EditText) view.findViewById(R.id.notification_name_edit);

		parametersContainer = (LinearLayout) view
				.findViewById(R.id.parameters_container);

		sendNotificationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendNotification();
			}
		});

		final Button addParameterButton = (Button) view
				.findViewById(R.id.add_parameter_button);
		addParameterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (parameterProvider != null) {
					parameterProvider.queryParameter();
				}
			}
		});

		equipmentSpinner = (Spinner) view.findViewById(R.id.equipment_spinner);
		equipmentSpinner.setPrompt("Select equipment");
		setupEquipmentSpinner(equipment);
		setupParameters(parameters);
		return view;
	}

	private void setupEquipmentSpinner(List<EquipmentData> equipment) {
		if (equipment != null && equipmentSpinner != null) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					this.getActivity(), android.R.layout.simple_spinner_item,
					getEquipmentItems(equipment));
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			equipmentSpinner.setAdapter(adapter);
		}
	}

	private List<String> getEquipmentItems(List<EquipmentData> equipment) {
		final List<String> equipmentNames = new LinkedList<String>();
		equipmentNames.add("None");
		for (EquipmentData eq : equipment) {
			equipmentNames.add(eq.getName());
		}
		return equipmentNames;
	}

	private void setupParameters(List<Parameter> parameters) {
		parametersContainer.removeAllViews();
		ParametersAdapter paramsAdapter = new ParametersAdapter(getActivity(),
				parameters);
		final int count = paramsAdapter.getCount();
		for (int i = 0; i < count; i++) {
			parametersContainer.addView(paramsAdapter.getView(i, null,
					parametersContainer));
		}
	}

	@Override
	public void onDestroyView() {
		sendNotificationButton = null;
		notificationNameEdit = null;
		equipmentSpinner = null;
		super.onDestroyView();
	}

	private void sendNotification() {
		String notification = notificationNameEdit.getText().toString();
		if (TextUtils.isEmpty(notification)) {
			notification = "TestNotificationAndroidFramework";
		}

		HashMap<String, Object> parameters = paramsAsMap(this.parameters);
		int selectedItemPosition = equipmentSpinner.getSelectedItemPosition();
		if (selectedItemPosition != 0) {
			final EquipmentData selectedEquipment = equipment
					.get(selectedItemPosition - 1);
			parameters.put("equipment", selectedEquipment.getCode());
		}
		if (notificationSender != null) {
			notificationSender.sendNotification(new Notification(notification, parameters));
		}
	}

	private static HashMap<String, Object> paramsAsMap(
			List<Parameter> params) {
		HashMap<String, Object> paramsMap = new HashMap<String, Object>();
		for (Parameter param : params) {
			paramsMap.put(param.name, param.value);
		}
		return paramsMap;
	}

	private static class ParametersAdapter extends BaseAdapter {

		private final LayoutInflater inflater;
		private List<Parameter> parameters;

		public ParametersAdapter(Context context,
				List<Parameter> parameters) {
			this.parameters = parameters;
			this.inflater = LayoutInflater.from(context);
		}

		public void setParameters(List<Parameter> parameters) {
			this.parameters = parameters;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return parameters.size();
		}

		@Override
		public Object getItem(int position) {
			return parameters.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.parameters_list_item,
						null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView
						.findViewById(R.id.parameter_name_text_view);
				holder.value = (TextView) convertView
						.findViewById(R.id.parameter_value_text_view);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final Parameter parameter = parameters.get(position);
			holder.name.setText(parameter.name);
			holder.value.setText(parameter.value);
			return convertView;
		}

		private class ViewHolder {
			TextView name;
			TextView value;
		}

	}

}
