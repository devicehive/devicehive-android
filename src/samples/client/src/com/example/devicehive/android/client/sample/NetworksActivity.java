package com.example.devicehive.android.client.sample;

import java.util.List;

import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.ClientProtocolException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dataart.android.devicehive.Network;
import com.dataart.android.devicehive.client.network.DeviceClientCommand;
import com.dataart.android.devicehive.client.network.GetNetworksCommand;
import com.dataart.android.devicehive.network.DeviceHiveResultReceiver;

public class NetworksActivity extends BaseActivity {

	private static final String TAG = "NetworksActivity";

	private ListView networksListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_networks);

		networksListView = (ListView) findViewById(R.id.networks_listView);
		networksListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> adapterView,
							View itemView, int position, long arg3) {
						NetworksAdapter adapter = (NetworksAdapter) adapterView
								.getAdapter();
						Network network = (Network) adapter.getItem(position);
						// start network devices activity
						NetworkDevicesActivity.start(NetworksActivity.this,
								network);
					}
				});
	}

	protected void onResume() {
		super.onResume();
		Log.d(TAG, "Starting Get Networks request");
		startCommand(new GetNetworksCommand());
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_settings) {
			startSettingsActivity();
			return true;
		}
		return false;
	}

	private void startSettingsActivity() {
		startActivity(new Intent(this, SettingsActivity.class));
	}

	private static class NetworksAdapter extends BaseAdapter {

		private final LayoutInflater inflater;
		private final List<Network> networks;

		public NetworksAdapter(Context context, List<Network> networks) {
			this.networks = networks;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return networks.size();
		}

		@Override
		public Object getItem(int position) {
			return networks.get(position);
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
			final Network network = networks.get(position);
			holder.name.setText(network.getName());
			holder.description.setText(network.getDescription());
			return convertView;
		}

		private class ViewHolder {
			TextView name;
			TextView description;
		}

	}

	@Override
	protected void onReceiveResult(final int resultCode, final int tagId,
			final Bundle resultData) {
		switch (resultCode) {
		case DeviceHiveResultReceiver.MSG_EXCEPTION:
			final Throwable exception = DeviceClientCommand
					.getThrowable(resultData);
			Log.e(TAG, "Failed to execute network command", exception);
			if (exception instanceof ClientProtocolException
					&& exception.getCause() instanceof MalformedChallengeException) {
				showDialog("Authentication error!", "Looks like your credentials are not valid.");
			} else {
				showDialog("Error", "Failed to connect to the server.");
			}
			break;
		case DeviceHiveResultReceiver.MSG_STATUS_FAILURE:
			int statusCode = DeviceClientCommand.getStatusCode(resultData);
			Log.e(TAG, "Failed to execute network command. Status code: "
					+ statusCode);
			if (statusCode == 404) {
				showDialog("Error", "Failed to connect to the server.");
			}
			break;
		case DeviceHiveResultReceiver.MSG_HANDLED_RESPONSE:
			if (tagId == TAG_GET_NETWORKS) {
				final List<Network> networks = GetNetworksCommand
						.getNetworks(resultData);
				Log.d(TAG, "Fetched networks: " + networks);
				if (networks != null) {
					networksListView.setAdapter(new NetworksAdapter(this,
							networks));
				}
			}
			break;
		}
	}

	private void showDialog(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton("Edit settings",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								startSettingsActivity();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
		dialog.show();
	}

	private static final int TAG_GET_NETWORKS = getTagId(GetNetworksCommand.class);
}
