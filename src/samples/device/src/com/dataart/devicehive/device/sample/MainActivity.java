package com.dataart.devicehive.device.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dataart.android.devicehive.Command;
import com.dataart.android.devicehive.device.DeviceStatusNotification;
import com.dataart.devicehive.device.sample.TestDevice.TestDeviceListener;

public class MainActivity extends Activity implements TestDeviceListener {

	private TestDevice device;
	
	private TextView logTextView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SampleDeviceApplication app = (SampleDeviceApplication)getApplication();
        device = app.getDevice();
        
        logTextView = (TextView)findViewById(R.id.log_text_view);

        final Button sendNotificationButton = (Button)findViewById(R.id.send_notification);
        sendNotificationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendNotification();
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	protected void onStart() {
		super.onStart();
		device.addDeviceListener(this);
		if (!device.isRegistered()) {
			device.registerDevice();
		} else {
			beginExecutingCommands();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		device.removeDeviceListener(this);
		stopExecutingCommands();
	}
	
	private void sendNotification() {
		device.sendNotification(DeviceStatusNotification.STATUS_OK);
	}
	
	private void beginExecutingCommands() {
		if (!device.isProcessingCommands()) {
			logTextView.append("Starting executing commands\n");
			device.startProcessingCommands();
		}
	}
	
	private void stopExecutingCommands() {
		if (device.isProcessingCommands()) {
			logTextView.append("Stopping executing commands\n");
			device.stopProcessingCommands();
		}
	}

	@Override
	public void testDeviceReceivedCommand(Command command) {
		logTextView.append(command.toString() + "\n");
	}

	@Override
	public void testDeviceRegistered() {
		logTextView.append("Device registered!\n");
		beginExecutingCommands();
	}

	@Override
	public void testDeviceFailedToRegister() {
		logTextView.append("Device failed to register!\n");
	}
    
}
