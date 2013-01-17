package com.example.devicehive.android.client.sample;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dataart.android.devicehive.DeviceData;
import com.dataart.android.devicehive.Network;
import com.dataart.android.devicehive.client.commands.DeviceClientCommand;
import com.dataart.android.devicehive.client.commands.GetNetworkDevicesCommand;
import com.dataart.android.devicehive.network.DeviceHiveResultReceiver;

public class NetworkDevicesActivity extends BaseActivity {

	public static final String EXTRA_NETWORK = NetworkDevicesActivity.class
			.getName() + ".EXTRA_NETWORK";

	public static void start(Context context, Network network) {
		Intent intent = new Intent(context, NetworkDevicesActivity.class);
		intent.putExtra(EXTRA_NETWORK, network);
		setParentActivity(intent, NetworksActivity.class);
		context.startActivity(intent);
	}

	private static final String TAG = "NetworkDevicesActivity";

	private ListView networkDevicesListView;

	private Network network;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_networks);

		network = getIntent().getExtras().getParcelable(EXTRA_NETWORK);
		if (network == null) {
			throw new IllegalArgumentException(
					"Network extra should be provided");
		}

		setTitle(network.getName());

		networkDevicesListView = (ListView) findViewById(R.id.networks_listView);
		networkDevicesListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> adapterView,
							View itemView, int position, long arg3) {
						NetworkDevicesAdapter adapter = (NetworkDevicesAdapter) adapterView
								.getAdapter();
						final DeviceData device = (DeviceData) adapter
								.getItem(position);
						DeviceActivity.start(NetworkDevicesActivity.this,
								device);
					}
				});
	}

	protected void onResume() {
		super.onResume();
		Log.d(TAG, "Starting Fetch Network devices request");
		networkDevicesListView.postDelayed(new Runnable() {
			@Override
			public void run() {
				startNetworkDevicesRequest();
			}
		}, 10);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected boolean showsActionBarProgress() {
		return true;
	}

	@Override
	protected boolean showsRefreshActionItem() {
		return true;
	}

	@Override
	protected void onRefresh() {
		startNetworkDevicesRequest();
	}
	
	private void startNetworkDevicesRequest() {
		incrementActionBarProgressOperationsCount(1);
		startCommand(new GetNetworkDevicesCommand(network.getId()));
	}

	private static class NetworkDevicesAdapter extends BaseAdapter {

		private final LayoutInflater inflater;
		private final List<DeviceData> devices;

		public NetworkDevicesAdapter(Context context, List<DeviceData> devices) {
			this.devices = devices;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return devices.size();
		}

		@Override
		public Object getItem(int position) {
			return devices.get(position);
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
						.inflate(R.layout.network_list_item, null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView
						.findViewById(R.id.network_name_text_view);
				holder.description = (TextView) convertView
						.findViewById(R.id.network_description_text_view);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final DeviceData device = devices.get(position);
			holder.name.setText(device.getName());
			holder.description.setText(device.getDeviceClass().getName());
			return convertView;
		}

		private class ViewHolder {
			TextView name;
			TextView description;
		}

	}

	protected void onReceiveResult(final int resultCode, final int tagId,
			final Bundle resultData) {
		switch (resultCode) {
		case DeviceHiveResultReceiver.MSG_COMPLETE_REQUEST:	  
	         decrementActionBarProgressOperationsCount();
	         break;
		case DeviceHiveResultReceiver.MSG_EXCEPTION:
			final Throwable exception = DeviceClientCommand
					.getThrowable(resultData);
			Log.e(TAG, "Failed to execute network command", exception);
			break;
		case DeviceHiveResultReceiver.MSG_STATUS_FAILURE:
			int statusCode = DeviceClientCommand.getStatusCode(resultData);
			Log.e(TAG, "Failed to execute network command. Status code: "
					+ statusCode);
			break;
		case DeviceHiveResultReceiver.MSG_HANDLED_RESPONSE:
			if (tagId == TAG_GET_NETWORK_DEVICES) {
				final List<DeviceData> devices = GetNetworkDevicesCommand
						.getNetworkDevices(resultData);
				Log.d(TAG, "Fetched devices: " + devices);
				if (devices != null) {
					Collections.sort(devices, new Comparator<DeviceData>() {
						@Override
						public int compare(DeviceData lhs, DeviceData rhs) {
							return lhs.getName().compareToIgnoreCase(
									rhs.getName());
						}
					});
					networkDevicesListView
							.setAdapter(new NetworkDevicesAdapter(this, devices));
				}
			}
			break;
		}
	}

	private static final int TAG_GET_NETWORK_DEVICES = getTagId(GetNetworkDevicesCommand.class);
}
