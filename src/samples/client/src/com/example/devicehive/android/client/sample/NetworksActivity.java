package com.example.devicehive.android.client.sample;

import java.util.Collections;
import java.util.Comparator;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.dataart.android.devicehive.Network;
import com.dataart.android.devicehive.client.commands.DeviceClientCommand;
import com.dataart.android.devicehive.client.commands.GetNetworksCommand;
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

		ActionBar actionbar = getSupportActionBar();
		actionbar.setTitle("Networks");
	}

	protected void onResume() {
		super.onResume();
		Log.d(TAG, "Starting Get Networks request");
		networksListView.postDelayed(new Runnable() {
			@Override
			public void run() {
				startNetworksRequest();
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
	protected boolean showsSettingsActionItem() {
		return true;
	}

	@Override
	protected boolean showsRefreshActionItem() {
		return true;
	}

	@Override
	protected void onShowSettings() {
		startSettingsActivity();
	}

	@Override
	protected void onRefresh() {
		startNetworksRequest();
	}
	
	@Override
	protected boolean showsHomeAsUpButton() {
		return false;
	} 

	private void startSettingsActivity() {
		startActivity(new Intent(this, SettingsActivity.class));
	}

	private void startNetworksRequest() {
		incrementActionBarProgressOperationsCount(1);
		startCommand(new GetNetworksCommand());
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
		case DeviceHiveResultReceiver.MSG_COMPLETE_REQUEST:	  
	         decrementActionBarProgressOperationsCount();
	         break;
		case DeviceHiveResultReceiver.MSG_EXCEPTION:
			final Throwable exception = DeviceClientCommand
					.getThrowable(resultData);
			Log.e(TAG, "Failed to execute network command", exception);
			if (exception instanceof ClientProtocolException
					&& exception.getCause() instanceof MalformedChallengeException) {
				showSettingsDialog("Authentication error!",
						"Looks like your credentials are not valid.");
			} else {
				showSettingsDialog("Error", "Failed to connect to the server.");
			}
			break;
		case DeviceHiveResultReceiver.MSG_STATUS_FAILURE:
			int statusCode = DeviceClientCommand.getStatusCode(resultData);
			Log.e(TAG, "Failed to execute network command. Status code: "
					+ statusCode);
			if (statusCode == 404) {
				showSettingsDialog("Error", "Failed to connect to the server.");
			}
			break;
		case DeviceHiveResultReceiver.MSG_HANDLED_RESPONSE:
			if (tagId == TAG_GET_NETWORKS) {
				final List<Network> networks = GetNetworksCommand
						.getNetworks(resultData);
				Log.d(TAG, "Fetched networks: " + networks);
				if (networks != null) {
					Collections.sort(networks, new Comparator<Network>() {
						@Override
						public int compare(Network lhs, Network rhs) {
							return lhs.getName().compareToIgnoreCase(
									rhs.getName());
						}
					});
					networksListView.setAdapter(new NetworksAdapter(this,
							networks));
				}
			}
			break;
		}
	}

	private void showSettingsDialog(String title, String message) {
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
