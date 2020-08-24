package com.siliconlabs.eic.doorlock.models;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.text.TextUtils;
import android.util.Log;


import com.siliconlabs.eic.doorlock.constants.DoorLockActions;
import com.siliconlabs.eic.doorlock.constants.DoorLockAuthenticationTypes;
import com.siliconlabs.eic.doorlock.constants.DoorLockEvents;
import com.siliconlabs.eic.doorlock.constants.DoorLockMessageFormat;
import com.siliconlabs.eic.doorlock.interfaces.DoorLockCallback;
import com.siliconlabs.eic.doorlock.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.siliconlabs.eic.doorlock.constants.DoorLockEvents.REMOTE_REQUEST_FAILED;
import static com.siliconlabs.eic.doorlock.constants.DoorLockEvents.REMOTE_REQUEST_SUCCESS;


/**
 * Maintains the readings received from connected bluetooth device
 *
 * @author Vipul Kole (vipul.kole@einfochips.com).
 * Created on: 07-08-2020.
 * Company:  e-Infochips Pvt Ltd,Pune,India.
 * Copyright (c) 2020 Silicon Labs, All rights reserved.
 */
public class DoorStatusReading {
    private final static String TAG = "DoorStatusReading";
    private static volatile DoorStatusReading mInstance = null;
    private final List<String> mActionHistory = Collections.synchronizedList(new ArrayList<>());
    private List<DoorLockCallback> mListenerList = new ArrayList<>();
    private long mReadingTime = System.currentTimeMillis();
    private int mLockStatus = -1;
    private String mUserId = "Unknown";
    private String mDeviceName = "Unknown";
    private boolean isAlive = false;
    private DoorLockActions mUserActionTye = DoorLockActions.UNKNOWN;
    private DoorLockAuthenticationTypes mAuthenticationType = DoorLockAuthenticationTypes.MOBILE;
    private int mPreviousLockState;
    private DoorLockActions mResponseType = DoorLockActions.UNKNOWN;


    private DoorStatusReading() {
        //Required Empty Constructor
    }

    /**
     * Creates and Returns the instance of DoorStatusReading
     *
     * @return DoorStatusReading
     */
    public static DoorStatusReading getInstance() {
        if (mInstance == null) {
            synchronized (DoorStatusReading.class) {
                mInstance = new DoorStatusReading();
            }
        }
        return mInstance;
    }

    /**
     * Handles the characteristics updates.
     * Updates the local values
     *
     * @param characteristic GattCharacteristics received from connected device
     * @param gatt           Gatt object
     * @param valid          true if valid characteristics
     * @return DoorStatusReading instance
     */
    public synchronized static DoorStatusReading fromCharacteristic(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt, boolean valid) {
        final byte[] data = characteristic.getValue();
        final long time = System.currentTimeMillis();
        boolean notify = false;

        Log.d(TAG, "---------------------------------"
                + "\nParsing Data"
                + "\nLength: " + data.length
                + "\nValues: " + Arrays.toString(data)
                + "\n-----------------------------------\n");

        String deviceName = null;
        try {
            if (gatt != null) {
                deviceName = gatt.getDevice().getName();
                if (TextUtils.isEmpty(deviceName)) {
                    deviceName = gatt.getDevice().getAddress();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!valid) {
            assert gatt != null;
            getInstance().setBleState(gatt.getDevice().getBondState());
            getInstance().updateDeviceName(deviceName);
            getInstance().setLogTime(time);
            getInstance().mActionHistory.add(getInstance().getFormattedTime(null) + " " + Arrays.toString(data));
            mInstance.onEvent(DoorLockEvents.NONE);
            return getInstance();
        }

        try {
            final int doorState = data[DoorLockMessageFormat.LOCK_UNLOCK_STATE.ordinal()];
            final String userId = String.valueOf(data[DoorLockMessageFormat.USER_ID.ordinal()]);
            final DoorLockAuthenticationTypes type = DoorLockAuthenticationTypes.fromId(data[DoorLockMessageFormat.AUTHENTICATION_TYPE.ordinal()]);
            final DoorLockActions action = DoorLockActions.fromId(data[DoorLockMessageFormat.USER_ACTION_TYPE.ordinal()]);

            getInstance().updateDeviceName(deviceName);
            getInstance().setUserId(userId);
            getInstance().setDoorState(doorState);
            getInstance().setUserActionType(action);
            assert gatt != null;
            getInstance().setBleState(gatt.getDevice().getBondState());
            getInstance().setAuthenticationType(type);
            getInstance().setLogTime(time);


            /*
             * ------------------------------------------------------------------------------------------------------------
             * Maintaining action history
             */
            if (type != DoorLockAuthenticationTypes.UNKNOWN || action == DoorLockActions.ENROLLED) {
                assert type != null;
                if (action == DoorLockActions.ENROLLED) {
                    getInstance().mActionHistory.add(getInstance().getFormattedTime(null) + " " + action.name + " new user with id: " + userId);
                } else {
                    getInstance().mActionHistory.add(getInstance().getFormattedTime(null) + " " + getInstance().getDoorStatusString() + " By: " + userId + " Mode: " + type.name);
                }
            }
            /*
            ------------------------------------------------------------------------------------------------------------------
             */

            if (type != DoorLockAuthenticationTypes.MOBILE) {
                if (getInstance().isLockStateChanged()) {
                    notify = true;
                }
            }
            mInstance.onEvent(DoorLockEvents.REMOTE_MESSAGE_RECEIVED);
        } catch (Exception e) {
            Log.e(TAG, "Exception :" + e.getLocalizedMessage() + "\n" + e.getCause());
        }

        if (notify) {
            Log.d(TAG, "Creating Notification");
            mInstance.onEvent(DoorLockEvents.UI_EVENT_NOTIFICATION);
        }

        return getInstance();
    }

    /**
     * Add the listeners to receive the callbacks
     *
     * @param listener listener class to register
     */
    public void addListener(DoorLockCallback listener) {
        if (mListenerList == null) {
            mListenerList = new ArrayList<>();
        }
        if (!mListenerList.contains(listener)) {
            mListenerList.add(listener);
        }
    }

    /**
     * It will starts maintaining the user actions in a list
     *
     * @param finalDeviceName connected device name
     */
    public void startHistory(String finalDeviceName) {
        mActionHistory.add(getInstance().getFormattedTime(null, System.currentTimeMillis()) + ": Connected to " + finalDeviceName);
    }

    /**
     * Sets the reading time
     *
     * @param time long milliseconds
     */
    private void setLogTime(long time) {
        mInstance.mReadingTime = time;
    }

    /**
     * Sets the connection state
     *
     * @param bondState int state
     */
    private void setBleState(int bondState) {
        mInstance.isAlive = bondState != 0;
    }

    /**
     * Sets the user action type
     *
     * @param userActionType UserActionType
     */
    private void setUserActionType(DoorLockActions userActionType) {
        mInstance.mUserActionTye = userActionType;
    }

    /**
     * Sets the door state value
     *
     * @param doorState int state
     */
    private void setDoorState(int doorState) {
        mInstance.mPreviousLockState = mInstance.mLockStatus;
        mInstance.mLockStatus = doorState;
    }

    /**
     * Sets the user id for actions performed
     *
     * @param userId string
     */
    private void setUserId(String userId) {
        mInstance.mUserId = userId;
    }

    /**
     * returns formatted date time string value
     *
     * @param format format for date time
     * @param millis value to convert into datetime
     * @return string
     */
    private String getFormattedTime(String format, long... millis) {
        if (millis != null && millis.length > 0) {
            if (format == null) {
                return DateUtils.millisToTimeAmPm(millis[0]);
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
                Date today = Calendar.getInstance().getTime();
                return dateFormat.format(today);
            }
        }
        return DateUtils.millisToTimeAmPm(mReadingTime);
    }

    /**
     * Sends event to all the listeners registered
     *
     * @param uiLockUnlockEvent EventAction
     * @param extra             extra data
     */
    public void onEvent(DoorLockEvents uiLockUnlockEvent, Object... extra) {
        if (!mListenerList.isEmpty()) {
            for (DoorLockCallback callback : mListenerList) {
                callback.onAction(uiLockUnlockEvent, extra);
            }
        }
    }

    /**
     * Updates the device name string
     *
     * @param finalDeviceName connected device name
     */
    public synchronized final void updateDeviceName(String finalDeviceName) {
        mInstance.mDeviceName = finalDeviceName;
    }

    /**
     * Returns the authentication type enum
     *
     * @return enum AuthenticationType
     */
    public DoorLockAuthenticationTypes getAuthenticationType() {
        return DoorLockAuthenticationTypes.MOBILE;
    }

    /**
     * Sets the authentication type
     *
     * @param type AuthenticationType
     */
    private void setAuthenticationType(DoorLockAuthenticationTypes type) {
        mInstance.mAuthenticationType = type;
    }

    /**
     * Return the door status string as locked/unlocked
     *
     * @return string
     */
    public String getDoorStatusString() {
        if (mInstance.isAlive) {
            if (mInstance.mLockStatus == 0) {
                return "Unlocked";
            } else if (mInstance.mLockStatus == 1) {
                return "Locked";
            } else {
                return "Unavailable";
            }
        } else {
            return "Unavailable";
        }

    }


    /**
     * Return the current lock status
     *
     * @return int state
     */
    public int getLockStatus() {
        return mLockStatus == 1 ? mLockStatus : 0;
    }


    /**
     * Returns the gattConnection status
     *
     * @return boolean
     */
    public boolean getDeviceConnectionStatus() {
        return isAlive;
    }

    /**
     * Returns the list of action performed with door lock application
     *
     * @return List<String>
     */
    public List<String> getActionHistory() {
        return mInstance.mActionHistory;
    }


    /**
     * Returns the connected bluetooth device name
     *
     * @return string
     */
    public String getDeviceName() {
        return mInstance.mDeviceName;
    }


    /**
     * Returns the characteristics read/change time
     *
     * @return formatted datetime string
     */
    public String getLastSyncedTime() {
        return getFormattedTime("dd-MM-YYYY hh:mm:ss", mInstance.mReadingTime);
    }


    /**
     * Returns true if the lock state is not matched with previous state (lock unlock is happened)
     *
     * @return boolean value
     */
    public boolean isLockStateChanged() {
        return mInstance.mPreviousLockState != mInstance.mLockStatus;
    }

    /**
     * It will called when characteristics write is completed successfully.
     *
     * @param status write request status
     */
    public void setWriteResponse(int status) {
        if (status == DoorLockActions.AUTHENTICATED.id) {
            mInstance.mResponseType = DoorLockActions.AUTHENTICATED;
            mInstance.mActionHistory.add(getInstance().getFormattedTime(null) + " action performed by mobile app");
            mInstance.onEvent(REMOTE_REQUEST_SUCCESS);
        } else {
            mInstance.mResponseType = DoorLockActions.INVALID;
            mInstance.mActionHistory.add(getInstance().getFormattedTime(null) + " " + mInstance.mResponseType.name);
            mInstance.onEvent(REMOTE_REQUEST_FAILED);
        }
    }
}
