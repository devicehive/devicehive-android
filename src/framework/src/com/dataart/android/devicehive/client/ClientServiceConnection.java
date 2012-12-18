package com.dataart.android.devicehive.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.dataart.android.devicehive.Command;
import com.dataart.android.devicehive.DeviceData;
import com.dataart.android.devicehive.DeviceHive;
import com.dataart.android.devicehive.Notification;
import com.dataart.android.devicehive.client.network.GetDeviceCommand;
import com.dataart.android.devicehive.client.network.PollDeviceNotificationsCommand;
import com.dataart.android.devicehive.client.network.SendDeviceCommandCommand;
import com.dataart.android.devicehive.network.DeviceHiveResultReceiver;
import com.dataart.android.devicehive.network.NetworkCommand;
import com.dataart.android.devicehive.network.NetworkCommandConfig;
import com.dataart.android.devicehive.network.ServiceConnection;

/* package */class ClientServiceConnection extends ServiceConnection {

	private DeviceClient client;

	private final Queue<Notification> notificationQueue = new LinkedList<Notification>();

	private boolean isReceivingNotifications = false;

	private String username;
	private String password;

	private boolean isPollRequestInProgress = false;

	private String lastNotificationPollTimestamp;

	public ClientServiceConnection(Context context) {
		super(context);
	}

	public void setLastNotificationPollTimestamp(String timestamp) {
		this.lastNotificationPollTimestamp = timestamp;
	}

	public void setAuthorisation(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/* package */void sendCommand(Command command) {
		logD("Sending command: " + command.getCommand());
		client.onStartSendingCommand(command);
		startNetworkCommand(new SendDeviceCommandCommand(client.getDevice(),
				command));
	}
	
	/* package */ void reloadDeviceData() {
		startNetworkCommand(new GetDeviceCommand(client.getDevice().getId()));
	}

	/* package */void startReceivingNotifications() {
		if (isReceivingNotifications) {
			stopReceivingNotifications();
		}
		isReceivingNotifications = true;
		handleNextNotification();
	}

	/* package */void stopReceivingNotifications() {
		isReceivingNotifications = false;
		// mainThreadHandler.removeCallbacks(notificationPollRequestRunnable);
	}

	/* package */void setClient(DeviceClient client) {
		this.client = client;
	}

	/* package */boolean isReceivingNotifications() {
		return isReceivingNotifications;
	}

	private void handleNotification(final Notification notification) {
		if (client.shouldReceiveNotificationAsynchronously(notification)) {
			asyncHandler.post(new Runnable() {
				@Override
				public void run() {
					client.onReceiveNotification(notification);
					mainThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							if (isReceivingNotifications) {
								handleNextNotification();
							}
						}
					});
				}
			});
		} else {
			client.onReceiveNotification(notification);
			if (isReceivingNotifications) {
				handleNextNotification();
			}
		}
	}

	private void handleNextNotification() {
		final Notification notification = notificationQueue.poll();
		if (notification != null) {
			handleNotification(notification);
		} else {
			if (!isPollRequestInProgress) {
				startPollNotificationsRequest();
			}
		}
	}

	private void startPollNotificationsRequest() {
		logD("Starting polling request");
		isPollRequestInProgress = true;
		startNetworkCommand(new PollDeviceNotificationsCommand(
				client.getDevice(), lastNotificationPollTimestamp));
	}

	private int enqueueNotifications(List<Notification> notifications) {
		if (notifications == null || notifications.isEmpty()) {
			return 0;
		}
		int enqueuedCount = 0;
		for (Notification notification : notifications) {
			boolean added = notificationQueue.offer(notification);
			if (!added) {
				Log.e(DeviceHive.TAG, "Failed to add notification to the queue");
			} else {
				enqueuedCount++;
			}
		}
		return enqueuedCount;
	}

	@Override
	protected void onReceiveResult(final int resultCode,
			final int tagId, final Bundle resultData) {
		switch (resultCode) {
		case DeviceHiveResultReceiver.MSG_HANDLED_RESPONSE:
			logD("Handled response");
			if (tagId == TAG_SEND_COMMAND) {
				Command command = SendDeviceCommandCommand
						.getSentCommand(resultData);
				logD("Command sent with response: " + command);
				client.onFinishSendingCommand(command);
			} else if (tagId == TAG_POLL_NOTIFICATIONS) {
				logD("Poll request finished");
				isPollRequestInProgress = false;
				List<Notification> notifications = PollDeviceNotificationsCommand
						.getNotifications(resultData);
				logD("-------Received notifications: " + notifications);
				logD("Notifications count: " + notifications.size());
				int enqueuedCount = enqueueNotifications(notifications);
				logD("Enqueued notifications count: " + enqueuedCount);
				if (!notifications.isEmpty()) {
					lastNotificationPollTimestamp = notifications.get(
							notifications.size() - 1).getTimestamp();
				}
				if (isReceivingNotifications) {
					handleNextNotification();
				}
			} else if (tagId == TAG_GET_DEVICE) {
				logD("Get device request finished");
				final DeviceData deviceData = GetDeviceCommand.getDevice(resultData);
				client.onReloadDeviceDataFinishedInternal(deviceData);
			}
			break;
		case DeviceHiveResultReceiver.MSG_EXCEPTION:
			final Throwable exception = NetworkCommand.getThrowable(resultData);
			Log.e(DeviceHive.TAG, "DeviceHiveResultReceiver.MSG_EXCEPTION",
					exception);
		case DeviceHiveResultReceiver.MSG_STATUS_FAILURE:
			if (tagId == TAG_SEND_COMMAND) {
				SendDeviceCommandCommand command = (SendDeviceCommandCommand) NetworkCommand
						.getCommand(resultData);
				client.onFailSendingCommand(command.getCommand());
			} else if (tagId == TAG_POLL_NOTIFICATIONS) {
				Log.d(DeviceHive.TAG, "Failed to poll notifications");
				isPollRequestInProgress = false;
				if (isReceivingNotifications) {
					handleNextNotification();
				}
			} else if (tagId == TAG_GET_DEVICE) {
				client.onReloadDeviceDataFailedInternal();
			}
			break;
		}

	}

	@Override
	protected NetworkCommandConfig getCommandConfig() {
		final NetworkCommandConfig config = super.getCommandConfig();
		config.setBasicAuthorisation(username, password);
		return config;
	}

	private final static int TAG_SEND_COMMAND = getTagId(SendDeviceCommandCommand.class);
	private final static int TAG_POLL_NOTIFICATIONS = getTagId(PollDeviceNotificationsCommand.class);
	private final static int TAG_GET_DEVICE = getTagId(GetDeviceCommand.class);
}
