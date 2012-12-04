package com.example.devicehive.android.client.sample;

import android.app.Activity;
import android.os.Bundle;

import com.dataart.android.devicehive.client.network.DeviceClientCommand;
import com.dataart.android.devicehive.network.DeviceHiveResultReceiver;
import com.dataart.android.devicehive.network.NetworkCommandConfig;

public class BaseActivity extends Activity {

	private DeviceHiveResultReceiver resultReceiver = null;

	private final DeviceHiveResultReceiver.ResultListener resultListener = new DeviceHiveResultReceiver.ResultListener() {
		@Override
		public void onReceiveResult(int code, int tag, Bundle data) {
			BaseActivity.this.onReceiveResult(code, tag, data);
		}
	};

	@Override
	protected void onStop() {
		super.onStop();
		if (resultReceiver != null) {
			resultReceiver.detachResultListener();
			resultReceiver = null;
		}
	}

	protected final <T extends DeviceClientCommand> void startCommand(
			final T command) {
		command.start(getApplicationContext(), getNetworkCommandConfig());
	}

	protected DeviceHiveResultReceiver getResultReceiver() {
		if (null == resultReceiver) {
			resultReceiver = new DeviceHiveResultReceiver();
			resultReceiver.setResultListener(resultListener, true);
		}
		return resultReceiver;
	}

	protected NetworkCommandConfig getNetworkCommandConfig() {
		final NetworkCommandConfig config = new NetworkCommandConfig(
				DeviceHiveConfig.API_ENDPOINT, getResultReceiver(),
				BuildConfig.DEBUG);
		config.setBasicAuthorisation(DeviceHiveConfig.CLIENT_USERNAME,
				DeviceHiveConfig.CLIENT_PASSWORD);
		return config;
	}

	protected void onReceiveResult(final int resultCode, final int tagId,
			final Bundle resultData) {

	}

	protected static final int getTagId(final Class<?> tag) {
		return getTagId(tag.getName());
	}

	protected static final int getTagId(final String tag) {
		return DeviceHiveResultReceiver.getIdForTag(tag);
	}

}
