package com.dataart.devicehive.device.sample;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dataart.android.devicehive.EquipmentData;

public class EquipmentListFragment extends ListFragment  {

	private List<EquipmentData> equipment;
	private EquipmentAdapter equipmentAdapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public static EquipmentListFragment newInstance() {
		EquipmentListFragment f = new EquipmentListFragment();
		return f;
	}

	public void setEquipment(List<EquipmentData> equipment) {
		this.equipment = equipment;
		if (getActivity() != null) {
			equipmentAdapter = new EquipmentAdapter(getActivity(), equipment);
			setListAdapter(equipmentAdapter);
		}
	}	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (equipment != null) {
			equipmentAdapter = new EquipmentAdapter(activity, equipment);
			setListAdapter(equipmentAdapter);
		}
	}


	private static class EquipmentAdapter extends BaseAdapter {

		private final LayoutInflater inflater;
		private List<EquipmentData> equipment;

		public EquipmentAdapter(Context context, List<EquipmentData> equipment) {
			this.equipment = equipment;
			this.inflater = LayoutInflater.from(context);
		}
		
		public void setEquipment(List<EquipmentData> equipment) {
			this.equipment = equipment;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return equipment.size();
		}

		@Override
		public Object getItem(int position) {
			return equipment.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater
						.inflate(R.layout.equipment_list_item, null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView
						.findViewById(R.id.equipment_name_text_view);
				holder.code = (TextView) convertView
						.findViewById(R.id.equipment_code_text_view);
				holder.type = (TextView) convertView
						.findViewById(R.id.equipment_type_text_view);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final EquipmentData equipmentData = equipment.get(position);
			holder.name.setText(equipmentData.getName());
			holder.code.setText(equipmentData.getCode());
			holder.type.setText(equipmentData.getType());
			return convertView;
		}

		private class ViewHolder {
			TextView name;
			TextView code;
			TextView type;
		}

	}

}