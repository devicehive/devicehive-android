package com.devicehive.devicehiveandroid.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.devicehive.devicehiveandroid.R;
import com.devicehive.devicehiveandroid.service.LocationService;
import com.devicehive.devicehiveandroid.utils.PreferencesHelper;
import com.google.android.gms.gcm.GcmNetworkManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.refreshToken)
    TextInputEditText refreshToken;
    @BindView(R.id.serverAddress)
    TextInputEditText serverAddress;
    @BindView(R.id.container)
    View container;
    @BindView(R.id.status)
    View activeStatusMessage;
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
        refreshToken.setText(helper.getRefreshToken());
        serverAddress.setText(helper.getServerUrl());
        activeStatusMessage.setVisibility(helper.isServiceWorking() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onPause() {
        PreferencesHelper helper = PreferencesHelper.getInstance();
        helper.putServerUrl(serverAddress.getText().toString());
        helper.putRefreshToken(refreshToken.getText().toString().trim());
        super.onPause();

    }

    @OnClick(R.id.start)
    public void startWithCheck() {
        MainActivityPermissionsDispatcher.startWithPermissionCheck(this);
    }

    @OnClick(R.id.stop)
    void stop() {
        PreferencesHelper.getInstance().clearPreferences();
        refreshToken.setText("");
        serverAddress.setText("");
        activeStatusMessage.setVisibility(View.INVISIBLE);
        mGcmNetworkManager.cancelTask(LocationService.TAG, LocationService.class);
    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void start() {
        if (TextUtils.isEmpty(refreshToken.getText().toString()) ||
                TextUtils.isEmpty(serverAddress.getText().toString())) {
            return;
        }
        if (!isLocationPermissionsGranted()) {
            createNoPermissionsDialog().show();
            return;
        }
        mGcmNetworkManager.schedule(
                LocationService.getPeriodicTask(
                        serverAddress.getText().toString()
                        , refreshToken.getText().toString()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
