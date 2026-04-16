package com.oasisfeng.island;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

/**
 * Main Application class for Island.
 *
 * Island is a sandbox environment that isolates apps using Android's managed profile feature.
 * This class handles global application initialization, including setting up core services
 * and managing the application lifecycle.
 */
public class IslandApplication extends Application {

    private static final String TAG = "Island.App";

    private static IslandApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Log.i(TAG, "Island application starting...");
        initializeCore();
    }

    /**
     * Returns the singleton application instance.
     *
     * @return the global {@link IslandApplication} instance
     */
    @NonNull
    public static IslandApplication getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("Application not yet initialized");
        }
        return sInstance;
    }

    /**
     * Returns the application context.
     *
     * @return the application {@link Context}
     */
    @NonNull
    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

    /**
     * Initializes core Island components.
     * This includes setting up the device policy manager bridge,
     * initializing the app list cache, and registering broadcast receivers.
     */
    private void initializeCore() {
        try {
            // Initialize the Island engine which manages the managed profile
            IslandManager.initialize(this);
            Log.i(TAG, "Island core initialized successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Island core", e);
            // Note: We intentionally don't rethrow here — a core init failure should not
            // crash the entire app process; degraded functionality is preferable to a hard crash.
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "Island application terminating.");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, "Low memory warning received.");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // Lowered threshold from TRIM_MEMORY_RUNNING_CRITICAL to TRIM_MEMORY_RUNNING_LOW
        // so we get earlier warnings and can react before things get critical.
        if (level >= TRIM_MEMORY_RUNNING_LOW) {
            Log.w(TAG, "Trimming memory at level: " + level);
        }
    }
}
