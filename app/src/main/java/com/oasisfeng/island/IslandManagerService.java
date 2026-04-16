package com.oasisfeng.island;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Background service responsible for managing Island profile operations,
 * including app cloning, freezing, and lifecycle management.
 */
public class IslandManagerService extends Service {

    private static final String TAG = "IslandManagerService";

    public static final String ACTION_FREEZE_APP   = "com.oasisfeng.island.action.FREEZE_APP";
    public static final String ACTION_UNFREEZE_APP = "com.oasisfeng.island.action.UNFREEZE_APP";
    public static final String ACTION_CLONE_APP    = "com.oasisfeng.island.action.CLONE_APP";
    public static final String EXTRA_PACKAGE_NAME  = "package";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null) {
            Log.w(TAG, "Received null intent or action");
            return START_NOT_STICKY;
        }

        final String pkg = intent.getStringExtra(EXTRA_PACKAGE_NAME);
        if (pkg == null || pkg.isEmpty()) {
            Log.e(TAG, "No package name provided for action: " + intent.getAction());
            return START_NOT_STICKY;
        }

        switch (intent.getAction()) {
            case ACTION_FREEZE_APP:
                freezeApp(pkg);
                break;
            case ACTION_UNFREEZE_APP:
                unfreezeApp(pkg);
                break;
            case ACTION_CLONE_APP:
                cloneApp(pkg);
                break;
            default:
                Log.w(TAG, "Unknown action: " + intent.getAction());
        }

        return START_NOT_STICKY;
    }

    /**
     * Freeze (disable) an application so it cannot be launched.
     * Requires device owner or profile owner privileges.
     */
    private void freezeApp(final String packageName) {
        Log.i(TAG, "Freezing app: " + packageName);
        try {
            getPackageManager().setApplicationEnabledSetting(
                    packageName,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    0);
            Log.d(TAG, "App frozen successfully: " + packageName);
        } catch (SecurityException e) {
            Log.e(TAG, "Permission denied freezing " + packageName, e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Package not found: " + packageName, e);
        }
    }

    /**
     * Unfreeze (re-enable) a previously frozen application.
     */
    private void unfreezeApp(final String packageName) {
        Log.i(TAG, "Unfreezing app: " + packageName);
        try {
            getPackageManager().setApplicationEnabledSetting(
                    packageName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    0);
            Log.d(TAG, "App unfrozen successfully: " + packageName);
        } catch (SecurityException e) {
            Log.e(TAG, "Permission denied unfreezing " + packageName, e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Package not found: " + packageName, e);
        }
    }

    /**
     * Clone an application into the Island (managed) profile.
     * Actual cross-profile install requires DevicePolicyManager integration.
     */
    private void cloneApp(final String packageName) {
        Log.i(TAG, "Cloning app into Island: " + packageName);
        // TODO: Integrate with DevicePolicyManager to install app in managed profile
        // This is a stub — full implementation requires profile owner setup
    }

    /**
     * Returns a list of currently installed user apps that can be managed.
     */
    public List<ApplicationInfo> getManageableApps() {
        final PackageManager pm = getPackageManager();
        final List<ApplicationInfo> installed = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        final List<ApplicationInfo> result = new ArrayList<>();
        for (ApplicationInfo info : installed) {
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                result.add(info);
            }
        }
        return result;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
