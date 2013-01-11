package com.example.devicehive.android.client.sample;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.dataart.android.devicehive.Command;
import com.dataart.android.devicehive.DeviceData;
import com.dataart.android.devicehive.EquipmentData;
import com.dataart.android.devicehive.EquipmentState;
import com.dataart.android.devicehive.Notification;
import com.dataart.android.devicehive.client.commands.DeviceClientCommand;
import com.dataart.android.devicehive.client.commands.GetDeviceClassEquipmentCommand;
import com.dataart.android.devicehive.client.commands.GetDeviceCommand;
import com.dataart.android.devicehive.client.commands.GetDeviceEquipmentStateCommand;
import com.dataart.android.devicehive.network.DeviceHiveResultReceiver;
import com.example.devicehive.android.client.sample.DeviceSendCommandFragment.CommandSender;
import com.example.devicehive.android.client.sample.DeviceSendCommandFragment.ParameterProvider;
import com.example.devicehive.android.client.sample.ParameterDialog.ParameterDialogListener;
import com.example.devicehive.android.client.sample.SampleDeviceClient.CommandListener;
import com.example.devicehive.android.client.sample.SampleDeviceClient.NotificationsListener;

public class DeviceActivity extends BaseActivity implements
		NotificationsListener, CommandListener, CommandSender,
		ParameterProvider, ParameterDialogListener {

	private static final String EXTRA_DEVICE = DeviceActivity.class.getName()
			+ ".EXTRA_DEVICE";

	public static void start(Context context, DeviceData deviceData) {
		final Intent intent = new Intent(context, DeviceActivity.class);
		final Bundle parentExtras = new Bundle(1);
		parentExtras.putParcelable(NetworkDevicesActivity.EXTRA_NETWORK,
				deviceData.getNetwork());
		intent.putExtra(EXTRA_DEVICE, deviceData);
		setParentActivity(intent, NetworkDevicesActivity.class, parentExtras);
		
		context.startActivity(intent);
	}

	private static final String TAG = "DeviceActivity";

	private SampleClientApplication app;
	private DeviceData device;
	private SampleDeviceClient deviceClient;

	private ViewPager viewPager;
	private TabsAdapter tabsAdapter;

	private DeviceInformationFragment deviceInfoFragment;
	private DeviceNotificationsFragment deviceNotificationsFragment;
	private DeviceSendCommandFragment deviceSendCommandFragment;
	private EquipmentListFragment equipmentListFragment;

	private List<Notification> receivedNotifications = new LinkedList<Notification>();
	private List<EquipmentData> equipment = new LinkedList<EquipmentData>();
	private List<EquipmentState> equipmentState = new LinkedList<EquipmentState>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);

		device = getIntent().getExtras().getParcelable(EXTRA_DEVICE);
		if (device == null) {
			throw new IllegalArgumentException(
					"Device extra should be provided");
		}
		setTitle(device.getName());

		app = (SampleClientApplication) getApplication();
		deviceClient = app.setupClientForDevice(device);

		ActionBar ab = getSupportActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		viewPager = (ViewPager) findViewById(R.id.pager);

		tabsAdapter = new TabsAdapter(this, viewPager);

		deviceInfoFragment = new DeviceInformationFragment();
		deviceInfoFragment.setDeviceData(device);

		deviceNotificationsFragment = new DeviceNotificationsFragment();
		deviceSendCommandFragment = new DeviceSendCommandFragment();
		deviceSendCommandFragment.setParameterProvider(this);

		equipmentListFragment = new EquipmentListFragment();

		tabsAdapter.addTab(ab.newTab().setText("Summary"), deviceInfoFragment);
		tabsAdapter.addTab(ab.newTab().setText("Equipment"),
				equipmentListFragment);
		tabsAdapter.addTab(ab.newTab().setText("Notifications"),
				deviceNotificationsFragment);
		tabsAdapter.addTab(ab.newTab().setText("Send Command"),
				deviceSendCommandFragment);

	}

	@Override
	protected void onResume() {
		super.onResume();
		deviceClient.addNotificationsListener(this);
		deviceClient.addCommandListener(this);
		viewPager.postDelayed(new Runnable() {
			@Override
			public void run() {
				startEquipmentRequest();
			}
		}, 10);
	}

	@Override
	protected void onPause() {
		super.onPause();
		deviceClient.stopReceivingNotifications();
		deviceClient.removeCommandListener(this);
		deviceClient.removeNotificationsListener(this);
		if (isFinishing()) {
			app.resetClient();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onReceviceNotification(Notification notification) {
		receivedNotifications.add(notification);
		deviceNotificationsFragment.setNotifications(receivedNotifications);
	}

	@Override
	public void sendCommand(Command command) {
		deviceClient.sendCommand(command);
	}

	@Override
	protected boolean showsRefreshActionItem() {
		return true;
	}

	@Override
	protected void onRefresh() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.getSelectedTab().getText();
		if (actionBar.getSelectedTab().getTag() == deviceInfoFragment) {
			incrementActionBarProgressOperationsCount(1);
			startCommand(new GetDeviceCommand(deviceClient.getDevice().getId()));
		} else if (actionBar.getSelectedTab().getTag() == equipmentListFragment) {
			incrementActionBarProgressOperationsCount(1);
			startCommand(new GetDeviceEquipmentStateCommand(
					deviceClient.getDevice().getId()));
		}
	}

	@Override
	protected boolean showsActionBarProgress() {
		return true;
	}

	private void startEquipmentRequest() {
		incrementActionBarProgressOperationsCount(2);
		startCommand(new GetDeviceClassEquipmentCommand(deviceClient
				.getDevice().getDeviceClass().getId()));
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
			if (tagId == TAG_GET_DEVICE) {
				showErrorDialog("Failed to retrieve device data");
			} else if (tagId == TAG_GET_EQUIPMENT) {
				// retry
				startEquipmentRequest();
			} else if (tagId == TAG_GET_EQUIPMENT_STATE) {
				// retry
				startCommand(new GetDeviceEquipmentStateCommand(
						deviceClient.getDevice().getId()));
			}
			break;
		case DeviceHiveResultReceiver.MSG_STATUS_FAILURE:
			int statusCode = DeviceClientCommand.getStatusCode(resultData);
			showErrorDialog("Server returned status code: " + statusCode);
			break;
		case DeviceHiveResultReceiver.MSG_HANDLED_RESPONSE:
			if (tagId == TAG_GET_DEVICE) {
				final DeviceData deviceData = GetDeviceCommand
						.getDevice(resultData);
				deviceInfoFragment.setDeviceData(deviceData);
			} else if (tagId == TAG_GET_EQUIPMENT) {
				this.equipment = GetDeviceClassEquipmentCommand
						.getEquipment(resultData);
				deviceSendCommandFragment.setEquipment(equipment);
				startCommand(new GetDeviceEquipmentStateCommand(
						deviceClient.getDevice().getId()));
			} else if (tagId == TAG_GET_EQUIPMENT_STATE) {
				this.equipmentState = GetDeviceEquipmentStateCommand
						.getEquipmentState(resultData);
				equipmentListFragment.setEquipment(equipment, equipmentState);
				if (!deviceClient.isReceivingNotifications()) {
					deviceClient.startReceivingNotifications();
				}
			}
			break;
		}
	}

	@Override
	public void onStartSendindCommand(Command command) {
		Log.d(TAG, "Start sending command: " + command.getCommand());
	}

	@Override
	public void onFinishSendindCommand(Command command) {
		Log.d(TAG, "Finish sending command: " + command.getCommand());
		showDialog("Success!", "Command \"" + command.getCommand()
				+ "\" has been sent.");
	}

	@Override
	public void onFailSendindCommand(Command command) {
		Log.d(TAG, "Fail sending command: " + command.getCommand() + "\n");
		showErrorDialog("Failed to send command: " + command.getCommand());
	}

	@Override
	public void queryParameter() {
		FragmentManager fm = getSupportFragmentManager();
		final ParameterDialog parameterDialog = new ParameterDialog();
		parameterDialog.show(fm, ParameterDialog.TAG);
	}

	@Override
	public void onFinishEditingParameter(String name, String value) {
		if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
			deviceSendCommandFragment.addParameter(name, value);
		}
	}

	private final static int TAG_GET_DEVICE = getTagId(GetDeviceCommand.class);
	private final static int TAG_GET_EQUIPMENT = getTagId(GetDeviceClassEquipmentCommand.class);
	private final static int TAG_GET_EQUIPMENT_STATE = getTagId(GetDeviceEquipmentStateCommand.class);

}
