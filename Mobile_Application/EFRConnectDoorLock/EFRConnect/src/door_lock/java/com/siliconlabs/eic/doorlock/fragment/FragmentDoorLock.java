package com.siliconlabs.eic.doorlock.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


import com.siliconlabs.eic.doorlock.constants.DoorLockEvents;
import com.siliconlabs.eic.doorlock.interfaces.DoorLockCallback;
import com.siliconlabs.eic.doorlock.models.DoorStatusReading;
import com.siliconlabs.eic.doorlock.R;
import com.siliconlabs.eic.doorlock.models.DoorLockSwipeButton;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.siliconlabs.eic.doorlock.constants.DoorLockEvents.UI_EVENT_LOCK_UNLOCK;


/**
 * Fragment that has door lock functioning UI
 *
 * @author Vipul Kole (vipul.kole@einfochips.com).
 * Created on: 06-07-2020.
 * Company:  e-Infochips Pvt Ltd,Pune,India.
 * Copyright (c) 2020 Silicon Labs, All rights reserved.
 */
public class FragmentDoorLock extends Fragment implements DoorLockCallback {
    /*
     * Local variable declaration
     */
    private static final String TAG = "DoorLockFragment";
    /*
     *  View Object binding
     */
    @InjectView(R.id.connected_device_name)
    TextView mConnectedDeviceName;
    @InjectView(R.id.txt_device_status_value)
    TextView mTxtValueDeviceStatus;
    @InjectView(R.id.door_status_value)
    TextView mTxtValueDoorStatus;
    @InjectView(R.id.btn_lock_unlock)
    DoorLockSwipeButton mButtonLockUnlock;
    @InjectView(R.id.txt_last_synced)
    TextView mTxtLastSynced;
    @InjectView(R.id.txt_device_name_value)
    TextView mTxtDeviceName;
    private DoorStatusReading mDoorStatusReading;
    private Context mContext;
    private Handler mHandler;
    private Animation mTextAnimation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_door_lock, container, false);
        initView(v);
        initListeners();
        updateUI();
        return v;
    }

    /**
     * Initializes the UI components
     *
     * @param v view inflater
     */
    private void initView(View v) {
        mHandler = new Handler();
        mContext = getContext();
        ButterKnife.inject(this, v);
        mDoorStatusReading = DoorStatusReading.getInstance();
        mDoorStatusReading.addListener(this);
        mButtonLockUnlock.setDisabledDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.locked_36_px, null));
        mButtonLockUnlock.setEnabledDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.unlocked_36_px, null));
        mButtonLockUnlock.setInnerTextPadding(10, 10, 10, 10);
        mButtonLockUnlock.setSwipeButtonPadding(50, 10, 50, 10);
        mButtonLockUnlock.setText(mContext.getString(R.string.swipe_to_unlock_str));
        mTextAnimation = AnimationUtils.loadAnimation(mContext, R.anim.text_anim);
    }

    /**
     * Initializes the click event, animations
     */
    private void initListeners() {
        mButtonLockUnlock.setOnStateChangeListener(new DoorLockSwipeButton.OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                mDoorStatusReading.onEvent(UI_EVENT_LOCK_UNLOCK);
            }

            @Override
            public void onActive() {
            }
        });

        mTextAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTxtValueDoorStatus.setText(mDoorStatusReading.getDoorStatusString());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }


    /**
     * Refreshes the fragments UI
     */
    private synchronized void updateUI() {
        mHandler.post(() -> {
            if (mDoorStatusReading != null) {
                Log.d(TAG, "Updating UI components");
                updateDeviceStatus();
                updateDoorStatus();
                updateSwitchState();
                mTxtLastSynced.setText(String.format("%s: %s", mContext.getString(R.string.last_synced_str), mDoorStatusReading.getLastSyncedTime()));
            }
        });

    }

    /**
     * Updates the device status text on UI to online/offline
     */
    private void updateDeviceStatus() {
        final boolean state = mDoorStatusReading.getDeviceConnectionStatus();
        if (state) {
            mTxtValueDeviceStatus.setTextColor(mContext.getColor(R.color.silabs_blue));
        } else {
            mTxtValueDeviceStatus.setTextColor(mContext.getColor(R.color.silabs_red));
        }
        mConnectedDeviceName.setText(mDoorStatusReading.getDeviceName());
        mTxtDeviceName.setText(mDoorStatusReading.getDeviceName());
        mTxtValueDeviceStatus.setText(state ? mContext.getString(R.string.online) : mContext.getString(R.string.offline));

    }

    /**
     * Updates the door status text on UI to lock / unlock
     */
    private void updateDoorStatus() {
        final boolean state = mDoorStatusReading.getLockStatus() == 1;
        if (state) {
            mTxtValueDoorStatus.setTextColor(mContext.getColor(R.color.silabs_blue));
        } else {
            mTxtValueDoorStatus.setTextColor(mContext.getColor(R.color.silabs_red));
        }
        mTxtValueDoorStatus.startAnimation(mTextAnimation);
    }

    /**
     * Updates the switch state to locked/unlocked
     */
    private void updateSwitchState() {
        if (mDoorStatusReading != null) {
            if (mDoorStatusReading.getLockStatus() == 1) {
                mButtonLockUnlock.setDisabledStateNotAnimated();
            }
            if (mDoorStatusReading.getLockStatus() == 0) {
                mButtonLockUnlock.setEnabledStateNotAnimated();
            }

        }
    }


    public synchronized void refresh() {
        updateUI();
    }

    /**
     * Custom callback for door lock user actions and bluetooth events
     *
     * @param i     Type of action performed
     * @param extra extra data if present in callback
     */
    @Override
    public void onAction(DoorLockEvents i, Object... extra) {
        switch (i) {
            case REMOTE_MESSAGE_RECEIVED:
            case UI_EVENT_LOCK_UNLOCK:
                updateUI();
                break;
            default:
                break;
        }
    }
}
