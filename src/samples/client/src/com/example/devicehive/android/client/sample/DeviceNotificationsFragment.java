package com.example.devicehive.android.client.sample;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.dataart.android.devicehive.Notification;

public class DeviceNotificationsFragment extends SherlockFragment {

	private List<Notification> notifications;

	private TextView logTextView;

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
		if (isAdded()) {
			setupNotifications(notifications);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_device_notifications,
				container, false);

		logTextView = (TextView) view.findViewById(R.id.log_text_view);
		setupNotifications(notifications);
		return view;
	}

	private void setupNotifications(List<Notification> notifications) {
		if (notifications != null) {
			logTextView.setText("");
			for (Notification notification : notifications) {
				logTextView.append(notification.toString() + "\n");
			}
		}
	}

}
