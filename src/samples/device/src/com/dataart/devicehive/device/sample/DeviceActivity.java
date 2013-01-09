package com.dataart.devicehive.device.sample;

import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.dataart.android.devicehive.Command;
import com.dataart.android.devicehive.DeviceData;
import com.dataart.android.devicehive.Notification;
import com.dataart.devicehive.device.sample.DeviceSendNotificationFragment.NotificationSender;
import com.dataart.devicehive.device.sample.DeviceSendNotificationFragment.ParameterProvider;
import com.dataart.devicehive.device.sample.ParameterDialog.ParameterDialogListener;
import com.dataart.devicehive.device.sample.TestDevice.CommandListener;
import com.dataart.devicehive.device.sample.TestDevice.NotificationListener;
import com.dataart.devicehive.device.sample.TestDevice.RegistrationListener;

public class DeviceActivity extends SherlockFragmentActivity implements
		RegistrationListener, ParameterProvider, CommandListener,
		NotificationListener, NotificationSender, ParameterDialogListener {

	private static final String EXTRA_DEVICE = DeviceActivity.class.getName()
			+ ".EXTRA_DEVICE";

	public static void start(Context context, DeviceData deviceData) {
		Intent intent = new Intent(context, DeviceActivity.class);
		intent.putExtra(EXTRA_DEVICE, deviceData);
		context.startActivity(intent);
	}

	private static final String TAG = "DeviceActivity";

	private TestDevice device;

	private ViewPager viewPager;
	private TabsAdapter tabsAdapter;

	private DeviceInformationFragment deviceInfoFragment;
	private DeviceCommandsFragment deviceCommandsFragment;
	private DeviceSendNotificationFragment deviceSendNotificationFragment;
	private EquipmentListFragment equipmentListFragment;

	private List<Command> receivedCommands = new LinkedList<Command>();

	private SampleDevicePreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);

		SampleDeviceApplication app = (SampleDeviceApplication) getApplication();
		device = app.getDevice();

		prefs = new SampleDevicePreferences(this);

		ActionBar ab = getSupportActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ab.setTitle("Device Sample");

		viewPager = (ViewPager) findViewById(R.id.pager);

		tabsAdapter = new TabsAdapter(this, viewPager);

		deviceInfoFragment = new DeviceInformationFragment();
		deviceInfoFragment.setDeviceData(device.getDeviceData());

		deviceCommandsFragment = new DeviceCommandsFragment();
		deviceSendNotificationFragment = new DeviceSendNotificationFragment();
		deviceSendNotificationFragment.setParameterProvider(this);
		deviceSendNotificationFragment.setEquipment(device.getDeviceData()
				.getEquipment());

		equipmentListFragment = new EquipmentListFragment();
		equipmentListFragment.setEquipment(device.getDeviceData()
				.getEquipment());

		tabsAdapter.addTab(ab.newTab().setText("Summary"), deviceInfoFragment);
		tabsAdapter.addTab(ab.newTab().setText("Equipment"),
				equipmentListFragment);
		tabsAdapter.addTab(ab.newTab().setText("Commands"),
				deviceCommandsFragment);
		tabsAdapter.addTab(ab.newTab().setText("Send Notification"),
				deviceSendNotificationFragment);

	}

	@Override
	protected void onResume() {
		super.onResume();
		device.addDeviceListener(this);
		device.addCommandListener(this);
		device.addNotificationListener(this);
		deviceInfoFragment.setDeviceData(device.getDeviceData());
		if (!device.isRegistered()) {
			device.registerDevice();
		} else {
			device.startProcessingCommands();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		device.removeDeviceListener(this);
		device.removeCommandListener(this);
		device.removeNotificationListener(this);
		device.stopProcessingCommands();
		if (isFinishing()) {
			device.unregisterDevice();
		}
	}

	@Override
	public void onDeviceRegistered() {
		deviceInfoFragment.setDeviceData(device.getDeviceData());
		device.startProcessingCommands();
	}

	@Override
	public void onDeviceFailedToRegister() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder
				.setTitle("Error")
				.setMessage("Failed to register device")
				.setPositiveButton("Retry",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								device.registerDevice();
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

	@Override
	public void onDeviceReceivedCommand(Command command) {
		receivedCommands.add(command);
		deviceCommandsFragment.setCommands(receivedCommands);
	}

	@Override
	public void onDeviceSentNotification(Notification notification) {
		Log.d(TAG, "Finish sending notification: " + notification.getName());
		showDialog("Success!", "Notification \"" + notification.getName()
				+ "\" has been sent.");
	}

	@Override
	public void onDeviceFailedToSendNotification(Notification notification) {
		Log.d(TAG, "Fail sending notification: " + notification.getName());
		showErrorDialog("Failed to send notification: " + notification.getName());
	}

	@Override
	public void sendNotification(Notification notification) {
		device.sendNotification(notification);
	}

	protected void showDialog(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.setTitle(title).setMessage(message)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
		dialog.show();
	}

	protected void showErrorDialog(String message) {
		showDialog("Error!", message);
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
			deviceSendNotificationFragment.addParameter(name, value);
		}
	}

	private com.actionbarsherlock.view.Menu optionsMenu;

	private static final int MENU_ID_SETTINGS = 0x01;

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		optionsMenu = menu;
		menu.add(com.actionbarsherlock.view.Menu.NONE, MENU_ID_SETTINGS,
				com.actionbarsherlock.view.Menu.NONE, "Settings")
				.setIcon(R.drawable.ic_menu_settings)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return super.onCreateOptionsMenu(menu);
	}

	private static final int SETTINGS_REQUEST_CODE = 0x01;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
			Log.d(TAG, "Changed settings!");
			device.stopProcessingCommands();
			device.unregisterDevice();
			device.setApiEnpointUrl(prefs.getServerUrl());
			receivedCommands = new LinkedList<Command>();
			deviceCommandsFragment.setCommands(receivedCommands);
		}
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		if (item.getItemId() == MENU_ID_SETTINGS) {
			startActivityForResult(new Intent(this, SettingsActivity.class),
					SETTINGS_REQUEST_CODE);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
