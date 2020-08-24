package com.siliconlabs.eic.doorlock.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.siliconlabs.eic.doorlock.BuildConfig;
import com.siliconlabs.eic.doorlock.activity.DoorLockActivity;

import timber.log.Timber;

public class SmartLockApplication extends Application implements Application.ActivityLifecycleCallbacks {
    public static SmartLockApplication APP;
    private static boolean isDoorLockActivityIsForeground = false;

    public SmartLockApplication() {
        APP = this;
    }

    public static SmartLockApplication getInstance() {
        return APP;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if (activity instanceof DoorLockActivity) {
            isDoorLockActivityIsForeground = true;
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        if (activity instanceof DoorLockActivity) {
            isDoorLockActivityIsForeground = false;
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    public boolean isActivityVisible() {
        return isDoorLockActivityIsForeground;
    }

}
