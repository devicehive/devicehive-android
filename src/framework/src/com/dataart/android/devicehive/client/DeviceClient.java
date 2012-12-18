package com.dataart.android.devicehive.client;

import android.content.Context;

import com.dataart.android.devicehive.Command;
import com.dataart.android.devicehive.DeviceData;
import com.dataart.android.devicehive.Notification;

/**
 * Represents a device client which provides high-level API for communication
 * with particular device. This class is abstract and designed to be subclassed
 * in order to handle incoming notifications. Also this class provides plenty of
 * they various callbacks: {@link #onStartReceivingNotifications()},
 * {@link #onStopReceivingNotifications()},
 * {@link #onStartSendingCommand(Command)},
 * {@link #onFinishSendingCommand(Command)},
 * {@link #onFailSendingCommand(Command)}, etc.
 * 
 */
public abstract class DeviceClient {

	private DeviceData device;
	private final ClientServiceConnection serviceConnection;

	/**
	 * Construct client with given {@link Context} and {@link DeviceData}
	 * objects.
	 * 
	 * @param context
	 *            {@link Context} object. In most cases this should be
	 *            application context which stays alive during the entire life
	 *            of an application.
	 * @param deviceData
	 *            {@link DeviceData} object which describes device to
	 *            communicate with.
	 */
	public DeviceClient(Context context, DeviceData device) {
		this.device = device;
		this.serviceConnection = new ClientServiceConnection(context);
		this.serviceConnection.setClient(this);
	}

	/**
	 * Get corresponding device.
	 * 
	 * @return {@link DeviceData} object.
	 */
	public DeviceData getDevice() {
		return device;
	}

	/**
	 * Set Device Hive service URL. This method <b>MUST</b> be called before
	 * performing registration and other subsequent network communications.
	 * 
	 * @param url
	 *            URL of Device Hive service.
	 */
	public void setApiEnpointUrl(String url) {
		serviceConnection.setApiEndpointUrl(url);
	}

	/**
	 * Get previously set Device Hive service URL.
	 * 
	 * @return URL of Device Hive service.
	 */
	public String getApiEndpointUrl() {
		return serviceConnection.getApiEndpointUrl();
	}

	/**
	 * Set Basic Authorisation credentials.
	 * 
	 * @param username
	 *            Username string.
	 * @param password
	 *            Password strung.
	 */
	public void setAuthorisation(String username, String password) {
		serviceConnection.setAuthorisation(username, password);
	}

	/**
	 * Enable or disable debug log messages.
	 * 
	 * @param enabled
	 *            Whether debug log messages enabled or not.
	 */
	public void setDebugLoggingEnabled(boolean enabled) {
		serviceConnection.setDebugLoggingEnabled(enabled);
	}

	/**
	 * Set timestamp of the last received notification. This value is used to
	 * reduce amount of notifications received from the server as a result of
	 * poll request to only those of them which were received by the server
	 * later than the time defined by given timestamp. If not specified, the
	 * server's timestamp is taken instead.
	 * 
	 * @param timestamp
	 *            Timestamp of the last received notification.
	 */
	public void setLastNotificationsPollTimestamp(String timestamp) {
		serviceConnection.setLastNotificationPollTimestamp(timestamp);
	}

	/**
	 * Check if this client is receiving notifications, i.e. performs
	 * notification polling.
	 * 
	 * @return true, if this client is performing notification polling,
	 *         otherwise returns false.
	 */
	public boolean isReceivingNotifications() {
		return serviceConnection.isReceivingNotifications();
	}

	/**
	 * Send command to the device.
	 * 
	 * @param command
	 *            {@link Command} to be sent.
	 */
	public void sendCommand(final Command command) {
		serviceConnection.sendCommand(command);
	}

	/**
	 * Start receiving notifications. Client will start polling server for new
	 * notifications.
	 */
	public void startReceivingNotifications() {
		onStartReceivingNotifications();
		serviceConnection.startReceivingNotifications();
	}

	/**
	 * Stop receiving notifications.
	 */
	public void stopReceivingNotifications() {
		serviceConnection.stopReceivingNotifications();
		onStopReceivingNotifications();
	}

	/**
	 * Reload device data. Current device data is updated with instance of
	 * {@link DeviceData} retrieved from the server.
	 * 
	 * @see #onFinishReloadingDeviceData(DeviceData)
	 * @see #onFailReloadingDeviceData()
	 */
	public void reloadDeviceData() {
		serviceConnection.reloadDeviceData();
	}

	/**
	 * Get context which was used to create this client.
	 * 
	 * @return {@link Context} was used to create this client.
	 */
	public Context getContext() {
		return serviceConnection.getContext();
	}

	/**
	 * Run given runnable on main thread. Helper method.
	 * 
	 * @param runnable
	 *            Instance which implements {@link Runnable} interface.
	 */
	protected void runOnMainThread(Runnable runnable) {
		serviceConnection.runOnMainThread(runnable);
	}

	/**
	 * Called right after {@link #startReceivingNotifications()} method is
	 * called. Override this method to perform additional actions before the
	 * client starts receiving notifications.
	 */
	protected void onStartReceivingNotifications() {
		// no op
	}

	/**
	 * Called right after {@link #stopReceivingNotifications()} method is
	 * called. Override this method to perform additional actions right after
	 * the device stops receiving notifications.
	 */
	protected void onStopReceivingNotifications() {
		// no op
	}

	/**
	 * Called when {@link Command} is about to be sent. Override this method to
	 * perform additional actions before a command is sent.
	 * 
	 * @param command
	 *            {@link Command} object.
	 */
	protected void onStartSendingCommand(Command command) {
		// no op
	}

	/**
	 * Called when {@link Command} has been sent to the device. Override this
	 * method to perform additional actions after a command is sent to the
	 * device.
	 * 
	 * @param command
	 *            {@link Command} object.
	 */
	protected void onFinishSendingCommand(Command command) {
		// no op
	}

	/**
	 * Called when client failed to send command to the device. Override this
	 * method to perform any extra actions.
	 * 
	 * @param command
	 *            {@link Command} object.
	 */
	protected void onFailSendingCommand(Command command) {
		// no op
	}

	/**
	 * Called when device client finishes reloading device data from the server.
	 * 
	 * @param deviceData
	 *            {@link DeviceData} instance returned by the server.
	 */
	protected void onFinishReloadingDeviceData(DeviceData deviceData) {
		// no op
	}

	/**
	 * Called when device client fails to reload device data from the server.
	 */
	protected void onFailReloadingDeviceData() {
		// no op
	}

	/* package */void onReloadDeviceDataFinishedInternal(DeviceData deviceData) {
		this.device = deviceData;
		onFinishReloadingDeviceData(deviceData);
	}

	/* package */void onReloadDeviceDataFailedInternal() {
		onFailReloadingDeviceData();
	}

	/**
	 * Check whether given notification should be handled asynchronously. If so
	 * {@link #onReceiveNotification(Notification)} method is called on some
	 * other, not UI thread.
	 * 
	 * @param notification
	 *            {@link Notification} instance.
	 * @return true, if given notification should be handled asynchronously,
	 *         otherwise return false.
	 */
	protected abstract boolean shouldReceiveNotificationAsynchronously(
			final Notification notification);

	/**
	 * Handle received notification. Can be called either on main (UI) thread or
	 * some background thread depending on
	 * {@link #shouldReceiveNotificationAsynchronously(Notification)} method
	 * return value.
	 * 
	 * @param notification
	 *            {@link Notification} instance to handle by the client.
	 */
	protected abstract void onReceiveNotification(
			final Notification notification);
}
