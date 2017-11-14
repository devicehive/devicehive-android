package com.devicehive.devicehiveandroid.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.devicehive.devicehiveandroid.R;
import com.devicehive.devicehiveandroid.service.GCMEvent;
import com.devicehive.devicehiveandroid.service.LocationScheduledTask;
import com.devicehive.devicehiveandroid.utils.PreferencesHelper;
import com.devicehive.devicehiveandroid.utils.TextWatcherCreator;
import com.google.android.gms.gcm.GcmNetworkManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.deviceId)
    TextInputEditText deviceId;
    @BindView(R.id.refreshToken)
    TextInputEditText refreshToken;
    @BindView(R.id.serverAddress)
    TextInputEditText serverAddress;
    @BindView(R.id.deviceIdLayout)
    TextInputLayout deviceIdLayout;
    @BindView(R.id.refreshTokenLayout)
    TextInputLayout refreshTokenLayout;
    @BindView(R.id.serverAddressLayout)
    TextInputLayout serverAddressLayout;
    @BindView(R.id.container)
    View container;
    @BindView(R.id.status)
    TextView activeStatusMessage;
    @BindView(R.id.start)
    Button startButton;
    @BindView(R.id.stop)
    Button stopButton;
    private boolean isLocationPermissionDeniedForeverShown;
    private GcmNetworkManager mGcmNetworkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mGcmNetworkManager = GcmNetworkManager.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferencesHelper helper = PreferencesHelper.getInstance();
        deviceId.setText(helper.getDeviceId());
        refreshToken.setText(helper.getRefreshToken());
        serverAddress.setText(helper.getServerUrl());
        deviceId.addTextChangedListener(TextWatcherCreator.getWatcher(deviceIdLayout));
        serverAddress.addTextChangedListener(TextWatcherCreator.getWatcher(serverAddressLayout));
        refreshToken.addTextChangedListener(TextWatcherCreator.getWatcher(refreshTokenLayout));

        updateTextViewStates();
        enableButtons(helper.isServiceWorking());
    }

    @Override
    protected void onPause() {
        PreferencesHelper helper = PreferencesHelper.getInstance();
        helper.putDeviceId(deviceId.getText().toString());
        helper.putServerUrl(serverAddress.getText().toString());
        helper.putRefreshToken(refreshToken.getText().toString().trim());
        super.onPause();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGSMEvent(GCMEvent event) {
        updateTextViewStates();
        enableButtons(PreferencesHelper.getInstance().isServiceWorking());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.start)
    public void startWithCheck() {
        MainActivityPermissionsDispatcher.startWithPermissionCheck(this);
    }

    @OnClick(R.id.stop)
    void stop() {
        PreferencesHelper.getInstance().putIsServiceWorking(false);
        enableButtons(false);
        updateTextViewStates();
        mGcmNetworkManager.cancelTask(LocationScheduledTask.TAG, LocationScheduledTask.class);
    }

    void enableButtons(boolean isServiceWorking) {
        startButton.setEnabled(!isServiceWorking);
        serverAddress.setEnabled(!isServiceWorking);
        refreshToken.setEnabled(!isServiceWorking);
        deviceId.setEnabled(!isServiceWorking);
        if (isServiceWorking) {
            serverAddress.clearFocus();
            refreshToken.clearFocus();
        }
        stopButton.setEnabled(isServiceWorking);
    }

    private void updateTextViewStates() {
        PreferencesHelper helper = PreferencesHelper.getInstance();
        if (helper.isServiceWorking()) {
            activeStatusMessage.setText(getString(R.string.service_is_working));
            activeStatusMessage.setVisibility(View.VISIBLE);
        } else {
            activeStatusMessage.setVisibility(View.GONE);
        }
    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void start() {
        if (isAnyFiledEmpty()) {
            setErrorIfNeeded();
            PreferencesHelper.getInstance().putIsServiceWorking(false);
            mGcmNetworkManager.cancelAllTasks(LocationScheduledTask.class);
            return;
        }

        if (!isLocationPermissionsGranted()) {
            createNoPermissionsDialog().show();
            return;
        }
        mGcmNetworkManager.schedule(
                LocationScheduledTask.getPeriodicTask(
                        serverAddress.getText().toString()
                        , refreshToken.getText().toString(),
                        deviceId.getText().toString()));
        enableButtons(true);
        activeStatusMessage.setText(getString(R.string.service_is_scheduled));
        activeStatusMessage.setVisibility(View.VISIBLE);
    }

    private boolean isAnyFiledEmpty() {
        return TextUtils.isEmpty(refreshToken.getText().toString()) ||
                TextUtils.isEmpty(serverAddress.getText().toString()) ||
                TextUtils.isEmpty(deviceId.getText().toString());

    }

    private void setErrorIfNeeded() {
        if (TextUtils.isEmpty(refreshToken.getText().toString())) {
            refreshTokenLayout.setError(getString(R.string.text_error));
        }
        if (TextUtils.isEmpty(serverAddress.getText().toString())) {
            serverAddressLayout.setError(getString(R.string.text_error));
        }
        if (TextUtils.isEmpty(deviceId.getText().toString())) {
            deviceIdLayout.setError(getString(R.string.text_error));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void showLocationPermissionDeniedForever() {
        if (isLocationPermissionDeniedForeverShown) {
            createNoPermissionsDialog().show();
            return;
        }
        isLocationPermissionDeniedForeverShown = true;
        createNoPermissionsDialog()
                .show();
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void showLocationPermissionDenied() {
        Snackbar.make(container, R.string.location_permission_not_granted_message,
                Snackbar.LENGTH_LONG).show();
    }

    private boolean isLocationPermissionsGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private AlertDialog createNoPermissionsDialog() {
        return new AlertDialog.Builder(this)
                .setTitle(R.string.permission_dialog_title)
                .setMessage(R.string.location_permission_not_granted_message)
                .setPositiveButton(R.string.ok, null).create();
    }
}
