package com.siliconlabs.eic.doorlock.models;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.siliconlabs.eic.doorlock.R;
import com.siliconlabs.eic.doorlock.activity.DoorLockActivity;
import com.siliconlabs.eic.doorlock.activity.HomeActivity;
import com.siliconlabs.eic.doorlock.application.SmartLockApplication;
import com.siliconlabs.eic.doorlock.constants.DoorLockEvents;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Door Lock Notification Manager.
 * Purpose is to show popup messages if activity is in foreground. show notifications if activity is in background.
 *
 * @author Vipul Kole (vipul.kole@einfochips.com).
 * Created on: 07-08-2020.
 * Company:  e-Infochips Pvt Ltd,Pune,India.
 * Copyright (c) 2020 Silicon Labs, All rights reserved.
 */
public class DoorLockNotificationManager {
    @SuppressLint("StaticFieldLeak")
    private static volatile DoorLockNotificationManager mInstance = null;
    private final Vibrator mVibrator;
    private final long[] mVibrationPattern = {0, 500, 1000};
    private Context mContext;
    private NotificationManagerCompat mNotificationManager;
    private PendingIntent mIntentToActivate;
    private DoorLockActivity mActivity;
    private AlertDialog mAlertDialog;

    private DoorLockNotificationManager(Context context) {
        this.mContext = context;
        mVibrator = (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);
    }

    /**
     * Creates an instance of notification manager
     *
     * @param mActivity onscreen activity object
     */
    public static void createInstance(Activity mActivity) {
        if (mInstance == null) {
            synchronized (DoorLockNotificationManager.class) {
                mInstance = new DoorLockNotificationManager(mActivity);
            }
        }
        mInstance.mActivity = (DoorLockActivity) mActivity;
        mInstance.createNotificationChannel();
        Intent intent = new Intent(mInstance.mContext.getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        mInstance.mIntentToActivate = PendingIntent.getActivity(mActivity, 0, intent, 0);
    }

    /**
     * Returns the DoorLockNotificationManager instance
     *
     * @param activities onscreen activity object
     * @return DoorLockNotificationManager instance
     */
    public static DoorLockNotificationManager getInstance(Activity... activities) {
        if (activities != null && activities.length > 0) {
            if (activities[0] != null) {
                mInstance.mActivity = (DoorLockActivity) activities[0];
                mInstance.mContext = mInstance.mActivity;
            }
        }
        return mInstance;
    }

    /**
     * Creates a notification channel
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = mContext.getString(R.string.channel_name);
            String description = mContext.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(mContext.getString(R.string.CHANNEL_ID), name, importance);
            channel.setDescription(description);
            channel.setLockscreenVisibility(1);
            channel.enableVibration(true);
            channel.setVibrationPattern(mVibrationPattern);
            channel.setShowBadge(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            mInstance.mNotificationManager = NotificationManagerCompat.from(mContext);
            mInstance.mNotificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Shows notification in top of the screen (Notification layout)
     *
     * @param extra Message to display as notification
     */
    private void showNotification(String... extra) {
        // Create an explicit intent for an Activity in your app
        Drawable d = ContextCompat.getDrawable(mContext, R.drawable.silicon_labs_logo);
        Bitmap bitmap = null;
        if (d != null) {
            bitmap = ((BitmapDrawable) d).getBitmap();
        }
        vibration();
        String content = "Door state is changed.";
        if (extra != null && extra.length > 0 && !TextUtils.isEmpty(extra[0])) {
            content = extra[0];
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, mContext.getString(R.string.CHANNEL_ID))
                .setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.silicon_labs_logo)
                .setContentTitle(mContext.getString(R.string.title_home))
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(mIntentToActivate)
                .setLights(Color.WHITE, 3000, 3000)
                .setVibrate(mVibrationPattern)
                .setAutoCancel(true);

        mNotificationManager.notify(1, builder.build());
    }

    /**
     * Shows the onscreen popup notification
     *
     * @param state popup type
     * @param extra message to display in popup screen
     */
    private void showAlertDialog(DialogState state, String... extra) {
        boolean isDoorUnlocked = DoorStatusReading.getInstance().getLockStatus() == 0;
        String content = "Door state is changed.";

        if (extra != null && extra.length > 0 && !TextUtils.isEmpty(extra[0])) {
            content = extra[0];
        }

        if (!mActivity.isDestroyed() && !mActivity.isFinishing()) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
            View layoutView = mActivity.getLayoutInflater().inflate(R.layout.dialog_popup, null);
            Button dialogButton = layoutView.findViewById(R.id.btnDialog);
            TextView text = layoutView.findViewById(R.id.txtDetails);
            ImageView image = layoutView.findViewById(R.id.imageView);
            if (state == DialogState.ERROR) {
                image.setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.invalid_passkey, mContext.getApplicationContext().getTheme()));
            } else {
                if (isDoorUnlocked) {
                    image.setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.unlocked_36_px, mContext.getApplicationContext().getTheme()));
                } else {
                    image.setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.locked_36_px, mContext.getApplicationContext().getTheme()));
                }

            }

            text.setText(content);
            dialogBuilder.setView(layoutView);
            if (mAlertDialog != null) {
                mAlertDialog.dismiss();
            }
            mAlertDialog = dialogBuilder.create();
            mAlertDialog.show();
            mInstance.vibration();
            dialogButton.setOnClickListener(view -> {
                mAlertDialog.dismiss();
                mInstance.mVibrator.cancel();
            });
        }
    }

    /**
     * Closes the popup message if activity goes in background
     */
    public void closeAlertDialog() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
    }

    /**
     * shows the popups with message if application is in foreground else shows the notification message
     *
     * @param extra message to be displayed
     */
    public synchronized void onNotify(String... extra) {
        String text;
        if (extra != null && extra.length > 0) {
            if (!extra[0].trim().equalsIgnoreCase("unavailable")) {
                text = "Door is " + extra[0];

            } else {
                text = "Door status " + extra[0];
            }
        } else {
            text = null;
        }

        if (SmartLockApplication.getInstance().isActivityVisible()) {
            showAlertDialog(DialogState.SUCCESS, text);
        } else {
            showNotification(text);
        }
    }

    /**
     * shows the error popups if application is in foreground
     *
     * @param remoteRequestFailed type of error
     */
    public synchronized void onNotifyError(DoorLockEvents remoteRequestFailed) {
        if (SmartLockApplication.getInstance().isActivityVisible()) {
            showAlertDialog(DialogState.ERROR, "Invalid key.\nPlease try again");
        } else {
            try {
                Toast.makeText(mContext, "Invalid Key\n Please try again.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e("NOTIFICATIONUtils", "Exception " + e.getCause());
            }
        }
    }

    /**
     * Vibrates the device
     */
    private void vibration() {
        mVibrator.vibrate(mVibrationPattern, -1);
    }

    private enum DialogState {
        SUCCESS,
        ERROR
    }
}
