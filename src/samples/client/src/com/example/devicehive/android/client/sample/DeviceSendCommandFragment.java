package com.example.devicehive.android.client.sample;

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
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.dataart.android.devicehive.Command;
import com.dataart.android.devicehive.EquipmentData;
import android.database.DataSetObserver;

public class DeviceSendCommandFragment extends SherlockFragment {

	private static final String TAG = "DeviceSendCommandFragment";

	private Button sendCommandButton;

	private TextView commandNameEdit;
	private LinearLayout parametersContainer;

	private Spinner equipmentSpinner;

	private CommandSender commandSender;
	private ParameterProvider parameterProvider;
	private ParametersAdapter parametersAdapter;

	private List<EquipmentData> equipment;
	private List<CommandParameter> parameters = new LinkedList<CommandParameter>();

	public static class CommandParameter {
		public final String name;
		public final String value;

		public CommandParameter(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}

	public interface CommandSender {
		void sendCommand(Command command);
	}

	public interface ParameterProvider {
		void queryParameter();
	}

	public void setCommandSender(CommandSender commandSender) {
		this.commandSender = commandSender;
	}

	public void setParameterProvider(ParameterProvider parameterProvider) {
		this.parameterProvider = parameterProvider;
	}

	public void setEquipment(List<EquipmentData> equipment) {
		this.equipment = equipment;
		setupEquipmentSpinner(equipment);
	}

	public void addParameter(String name, String value) {
		this.parameters.add(new CommandParameter(name, value));
		setupParameters(this.parameters);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		commandSender = (CommandSender) activity;
	}

	@Override
	public void onResume() {
		super.onResume();
		setupEquipmentSpinner(equipment);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_send_command, container,
				false);

		sendCommandButton = (Button) view
				.findViewById(R.id.send_command_button);

		commandNameEdit = (EditText) view.findViewById(R.id.command_name_edit);

		parametersContainer = (LinearLayout) view
				.findViewById(R.id.parameters_container);

		sendCommandButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendCommand();
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

	private void setupParameters(List<CommandParameter> parameters) {
		parametersContainer.removeAllViews();
		parametersAdapter = new ParametersAdapter(getActivity(),
				parameters);
		parametersAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				setupParameters(DeviceSendCommandFragment.this.parameters);
			}
		});
		final int count = parametersAdapter.getCount();
		for (int i = 0; i < count; i++) {
			parametersContainer.addView(parametersAdapter.getView(i, null,
					parametersContainer));
		}
	}

	@Override
	public void onDestroyView() {
		sendCommandButton = null;
		commandNameEdit = null;
		equipmentSpinner = null;
		super.onDestroyView();
	}

	private void sendCommand() {
		String command = commandNameEdit.getText().toString();
		if (TextUtils.isEmpty(command)) {
			command = "TestCommandAndroidFramework";
		}

		HashMap<String, Object> parameters = paramsAsMap(this.parameters);
		int selectedItemPosition = equipmentSpinner.getSelectedItemPosition();
		if (selectedItemPosition != 0) {
			final EquipmentData selectedEquipment = equipment
					.get(selectedItemPosition - 1);
			parameters.put("equipment", selectedEquipment.getCode());
		}
		if (commandSender != null) {
			commandSender.sendCommand(new Command(command, parameters));
		}
	}

	private static HashMap<String, Object> paramsAsMap(
			List<CommandParameter> params) {
		HashMap<String, Object> paramsMap = new HashMap<String, Object>();
		for (CommandParameter param : params) {
			paramsMap.put(param.name, param.value);
		}
		return paramsMap;
	}

	private static class ParametersAdapter extends BaseAdapter {

		private final LayoutInflater inflater;
		private final List<CommandParameter> parameters;

		public ParametersAdapter(Context context,
				List<CommandParameter> parameters) {
			this.parameters = parameters;
			this.inflater = LayoutInflater.from(context);
		}
		
		public void removeParameter(CommandParameter parameter) {
			parameters.remove(parameter);
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
		
		private View.OnClickListener clickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				removeParameter((CommandParameter)v.getTag());
			}
		};

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
				holder.deleteButton = convertView.findViewById(R.id.parameter_delete_image_view);
				holder.deleteButton.setOnClickListener(clickListener);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final CommandParameter parameter = parameters.get(position);
			holder.name.setText(parameter.name);
			holder.value.setText(parameter.value);
			holder.deleteButton.setTag(parameter);
			return convertView;
		}

		private class ViewHolder {
			TextView name;
			TextView value;
			View deleteButton;
		}

	}

}
