package com.oasisfeng.island;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Main entry point for the Island application.
 * Handles initial setup, device admin activation, and navigation to the main UI.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Island.MainActivity";
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;

    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mAdminComponentName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminComponentName = new ComponentName(this, IslandDeviceAdminReceiver.class);

        if (!isDeviceAdminActive()) {
            Log.i(TAG, "Device admin not active, requesting activation.");
            requestDeviceAdmin();
        } else {
            Log.i(TAG, "Device admin already active.");
            onDeviceAdminReady();
        }
    }

    /**
     * Checks whether Island's device admin component is currently active.
     */
    private boolean isDeviceAdminActive() {
        return mDevicePolicyManager != null
                && mDevicePolicyManager.isAdminActive(mAdminComponentName);
    }

    /**
     * Launches the system prompt to activate device admin privileges for Island.
     */
    private void requestDeviceAdmin() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminComponentName);
        intent.putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                getString(R.string.device_admin_description)
        );
        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
    }

    /**
     * Called when device admin is confirmed active. Proceed with main app initialization.
     */
    private void onDeviceAdminReady() {
        Log.d(TAG, "Device admin ready. Initializing main UI.");
        // TODO: Initialize main fragment/navigation once UI layer is in place
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            if (resultCode == Activity.RESULT_OK) {
                Log.i(TAG, "Device admin activated successfully.");
                onDeviceAdminReady();
            } else {
                Log.w(TAG, "Device admin activation declined by user.");
                Toast.makeText(this, R.string.device_admin_required, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            // TODO: Open settings activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
