package com.example.devicehive.android.client.sample;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

public class ParameterDialog extends DialogFragment {

	public interface ParameterDialogListener {
		void onFinishEditingParameter(String name, String value);
	}

	static final String TAG = "ParameterDialog";

	private EditText nameEdit;
	private EditText valueEdit;

	public ParameterDialog() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.parameter_dialog, container);
		nameEdit = (EditText) view.findViewById(R.id.name_edit);
		valueEdit = (EditText) view.findViewById(R.id.value_edit);
		final Button okButton = (Button) view.findViewById(R.id.ok_button);

		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ParameterDialogListener activity = (ParameterDialogListener) getActivity();
				activity.onFinishEditingParameter(
						nameEdit.getText().toString(), valueEdit.getText()
								.toString());
				ParameterDialog.this.dismiss();
			}
		});
		getDialog().setTitle("New Parameter");

		nameEdit.requestFocus();
		getDialog().getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		return view;
	}
}