package com.siliconlabs.eic.doorlock.activity;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.siliconlabs.eic.doorlock.constants.DoorLockEvents;
import com.siliconlabs.eic.doorlock.fragment.FragmentDoorLock;
import com.siliconlabs.eic.doorlock.fragment.FragmentKeypad;
import com.siliconlabs.eic.doorlock.interfaces.DoorLockCallback;
import com.siliconlabs.eic.doorlock.models.DoorLockNotificationManager;
import com.siliconlabs.eic.doorlock.models.DoorStatusReading;
import com.siliconlabs.eic.doorlock.R;
import com.siliconlabs.eic.doorlock.ble.BlueToothService;
import com.siliconlabs.eic.doorlock.ble.GattCharacteristic;
import com.siliconlabs.eic.doorlock.ble.GattService;
import com.siliconlabs.eic.doorlock.ble.TimeoutGattCallback;
import com.siliconlabs.eic.doorlock.utils.BLEUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.siliconlabs.eic.doorlock.constants.DoorLockEvents.REMOTE_REQUEST_FAILED;


/**
 * It is the main activity created for door lock, unlock related operations.
 *
 * @author Vipul Kole (vipul.kole@einfochips.com).
 * Created on: 06-07-2020.
 * Company:  e-Infochips Pvt Ltd,Pune,India.
 * Copyright (c) 2020 Silicon Labs, All rights reserved.
 */
public class DoorLockActivity extends BaseActivity implements DoorLockCallback {
    /*
    Local variable declarations
     */
    private static final String TAG = "DoorLockActivity";
    private static final int REFRESH_TIMEOUT = 10000;
    /*
    View/widgets object binding
     */
    @InjectView(R.id.list_actions)
    ListView mActionListView;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.refreshing_layout)
    RelativeLayout mRefreshView;
    @InjectView((R.id.txtAppVersion))
    TextView mTxtAppVersion;
    private BlueToothService mBluetoothService;
    private boolean mServiceHasBeenSet;
    private BlueToothService.Binding mBluetoothServiceBinder;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private FragmentDoorLock mFragmentDoorLock;
    private final Runnable mRefreshTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            mRefreshView.setVisibility(View.GONE);
            refreshFragments();
        }
    };
    private FragmentKeypad mFragmentKeypad;
    private boolean mIsSecreteKeyEntered = false;
    private Context mContext;
    private Handler mHandler;
    private BluetoothGatt mBluetoothGatt;
    private volatile DoorStatusReading mDoorStatusReadings;
    private ArrayAdapter<String> mListAdapter;
    private boolean mHasPreviouslyConnectedGatt;
    private String sBluetoothAddress;
    /**
     * BLE Gatt callback
     * receives connection events, read write events from bluetooth
     */
    private final TimeoutGattCallback mGattCallback = new TimeoutGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                runOnUiThread(() -> onDeviceDisconnect());
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            mBluetoothGatt = gatt;
            List<BluetoothGattService> services = gatt.getServices();

            String deviceName = mBluetoothGatt.getDevice().getName();
            if (TextUtils.isEmpty(deviceName)) {
                deviceName = gatt.getDevice().getAddress();
            }
            sBluetoothAddress = gatt.getDevice().getAddress();
            if (services != null) {
                final String mDeviceName = deviceName;
                for (BluetoothGattService s : services) {
                    if (s.getCharacteristics() != null) {
                        for (BluetoothGattCharacteristic ch : s.getCharacteristics()) {
                            if (GattCharacteristic.door_lock_state.uuid.equals(ch.getUuid())) {
                                gatt.readCharacteristic(ch);
                                break;
                            }
                        }
                    }
                }
                mHandler.post(() -> {
                    DoorStatusReading.getInstance().updateDeviceName(mDeviceName);
                    if (!mHasPreviouslyConnectedGatt) {
                        DoorStatusReading.getInstance().startHistory(mDeviceName);
                    }
                    refreshFragments();
                    mListAdapter.notifyDataSetChanged();
                });
            }
        }

        @Override
        public void onCharacteristicChanged(final BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if (GattCharacteristic.fromUuid(characteristic.getUuid()) == GattCharacteristic.door_lock_state) {
                mDoorStatusReadings = DoorStatusReading.fromCharacteristic(characteristic, gatt, true);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (GattCharacteristic.fromUuid(characteristic.getUuid()) == GattCharacteristic.door_lock_state) {
                mDoorStatusReadings = DoorStatusReading.fromCharacteristic(characteristic, gatt, true);
                BLEUtils.SetNotificationForCharacteristic(gatt, GattService.DOOR_LOCK,
                        GattCharacteristic.door_lock_state,
                        BLEUtils.Notifications.INDICATE);

            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, final int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            mHandler.post(() -> mDoorStatusReadings.setWriteResponse(status));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.DoorLockTheme);
        setContentView(R.layout.activity_door_lock);
        initView(savedInstanceState);
        initFragments();
        initDevice(getDeviceAddress(savedInstanceState));
    }

    private void initView(Bundle instanceState) {
        if (instanceState == null) {
            Bundle extra = getIntent().getExtras();
            if (extra != null && extra.containsKey("IS_NEW_CONNECTION")) {
                mHasPreviouslyConnectedGatt = !extra.getBoolean("IS_NEW_CONNECTION", true);
            }
        } else {
            mHasPreviouslyConnectedGatt = !instanceState.getBoolean("IS_NEW_CONNECTION", true);
        }

        mContext = this;
        ButterKnife.inject(this);
        mHandler = new Handler(getMainLooper());
        setSupportActionBar(mToolbar);
        mRefreshView.setVisibility(View.GONE);
        DoorLockNotificationManager.createInstance(this);
        mDoorStatusReadings = DoorStatusReading.getInstance();
        mDoorStatusReadings.addListener(this);
        findViewById(R.id.go_back_button).setOnClickListener(v -> onBackPressed());
        mTxtAppVersion.setText(String.format("%s: %s", mContext.getResources().getString(R.string.app_version), getAppVersion()));
    }

    /**
     * It returns the application version string
     *
     * @return string
     */
    private String getAppVersion() {
        try {
            return mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.x.x";
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsSecreteKeyEntered = false;
        //get out if the service has stopped, or if the gatt connection is dead
        if ((mServiceHasBeenSet && mBluetoothService == null) || (mBluetoothService != null && !mBluetoothService.isGattConnected())) {
            onDeviceDisconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mBluetoothService != null) {
//            mBluetoothService.clearGatt();
//        }
//        mBluetoothServiceBinder.unbind();
    }

    @Override
    public void onBackPressed() {
        if (mFragmentManager.getFragments().contains(mFragmentKeypad) && mIsSecreteKeyEntered) {
            Toast.makeText(mContext, R.string.str_can_not_go_back, Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_door_lock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                performRefreshAction();
                return true;
            case R.id.action_ota:
                Intent intent = new Intent(mContext, DeviceServicesActivity.class);
                if (mBluetoothService != null && mBluetoothService.getConnectedGatt() != null) {
                    intent.putExtra("DEVICE_SELECTED_ADDRESS", mBluetoothService.getConnectedGatt().getDevice().getAddress());
                }
                startActivity(intent);
                return true;

            case R.id.action_exit:
                finish();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Refreshes the fragments view
     */
    private void refreshFragments() {
        if (mFragmentManager.getFragments().contains(mFragmentDoorLock) && mFragmentDoorLock.isInLayout()) {
            mFragmentDoorLock.refresh();
        }
    }

    /**
     * Returns the connected bluetooth device address string if present in received data
     *
     * @param savedInstanceState bundle data received from previous intent
     * @return sting device address or null
     */
    private String getDeviceAddress(final Bundle savedInstanceState) {
        String deviceAddress;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                deviceAddress = null;
            } else {
                deviceAddress = extras.getString("DEVICE_SELECTED_ADDRESS");
                sBluetoothAddress = deviceAddress;
            }
        } else {
            deviceAddress = savedInstanceState.getString("DEVICE_SELECTED_ADDRESS");
            sBluetoothAddress = deviceAddress;
        }
        return deviceAddress;
    }


    /**
     * Initializes the bluetooth service with a context based bluetooth binder
     * If it binds successfully, it will register the gattCallback otherwise it returns to previous screen
     *
     * @param deviceAddress bluetooth device address
     */
    private void initDevice(final String deviceAddress) {
        mBluetoothServiceBinder = new BlueToothService.Binding(mContext) {
            @Override
            protected void onBound(final BlueToothService service) {
                mBluetoothService = service;
                mServiceHasBeenSet = true;
                if (!service.isGattConnected(deviceAddress)) {
                    Toast.makeText(mContext, R.string.toast_htm_gatt_conn_failed, Toast.LENGTH_LONG).show();
                    service.clearGatt();
                    mBluetoothServiceBinder.unbind();
                    finish();
                } else {
                    BluetoothGatt bG = service.getConnectedGatt(deviceAddress);
                    if (bG == null) {
                        Toast.makeText(mContext, R.string.device_not_from_EFR, Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                    service.registerGattCallback(true, mGattCallback);
                    if (bG.getServices() != null && !bG.getServices().isEmpty()) {
                        mBluetoothGatt = bG;
                        onGattFetched();
                    } else {
                        bG.discoverServices();
                    }

                    service.registerGattCallback(true, mGattCallback);
                    service.discoverGattServices();
                }
            }
        };
        BlueToothService.bind(mBluetoothServiceBinder);
    }

    /**
     * changes to be done on UI after gatt is connected
     */
    private void onGattFetched() {
        String deviceName = mBluetoothGatt.getDevice().getName();
        deviceName = TextUtils.isEmpty(deviceName) ? getString(R.string.not_advertising_shortcut) : deviceName;
        if (mDoorStatusReadings != null) {
            mDoorStatusReadings.updateDeviceName(deviceName);
        }
        runOnUiThread(this::refreshFragments);
    }

    public String getBluetoothAddress() {
        return sBluetoothAddress;
    }


    /**
     * Initialization of door lock fragments
     */
    private void initFragments() {
        mIsSecreteKeyEntered = false;
        mFragmentManager = getSupportFragmentManager();
        mFragmentDoorLock = (FragmentDoorLock) mFragmentManager.findFragmentById(R.id.fragment_door_lock);
        mFragmentKeypad = new FragmentKeypad();
        mListAdapter = new ArrayAdapter<>(this, R.layout.user_action_list_item, DoorStatusReading.getInstance().getActionHistory());
        mActionListView.setAdapter(mListAdapter);

    }

    /**
     * Returns to previous intent when disconnected from bluetooth
     */
    private void onDeviceDisconnect() {
        if (!isFinishing()) {
            Toast.makeText(mContext, R.string.device_has_disconnected, Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    /**
     * Custom callback for door lock user actions and bluetooth events
     *
     * @param i     Type of action performed
     * @param extra extra data if present in callback
     */
    @Override
    public void onAction(DoorLockEvents i, Object... extra) {
        mHandler.post(() -> mRefreshView.setVisibility(View.GONE));
        switch (i) {
            case REMOTE_REQUEST_SUCCESS:
                try {
                    readDeviceCharacteristics();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case REMOTE_REQUEST_FAILED:
                closeKeypad();
                mHandler.post(() -> DoorLockNotificationManager.getInstance(DoorLockActivity.this).onNotifyError(REMOTE_REQUEST_FAILED));
                break;
            case REMOTE_MESSAGE_RECEIVED:
                closeKeypad();
                break;
            case REMOTE_EVENT_ENROLLMENT:
                mHandler.post(() -> Toast.makeText(mContext, R.string.str_user_enrolled, Toast.LENGTH_SHORT).show());
                closeKeypad();
                break;
            case UI_EVENT_LOCK_UNLOCK:
                onLockUnlockEvent();
                break;
            case UI_EVENT_NOTIFICATION:
                mHandler.post(() -> DoorLockNotificationManager.getInstance(DoorLockActivity.this).onNotify(mDoorStatusReadings.getDoorStatusString()));
                break;

            case UI_EVENT_PASSKEY_COMPLETE:
                if (extra != null && extra.length > 0) {
                    onKeyEntered((String.valueOf(extra[0])));
                }
                closeKeypad();
                break;
            default:
                break;
        }

        try {
            mHandler.post(() -> {
                if (!isFinishing() && !isDestroyed()) {
                    if (mActionListView != null)
                        mListAdapter.notifyDataSetChanged();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * It will request for latest state of the bluetooth device
     * The main purpose is to sync with the device
     */
    private void performRefreshAction() {
        mHandler.removeCallbacks(mRefreshTimeoutRunnable);
        mHandler.postDelayed(mRefreshTimeoutRunnable, REFRESH_TIMEOUT);
        mRefreshView.setVisibility(View.VISIBLE);
        readDeviceCharacteristics();
    }

    /**
     * It will called when the user completes the entering 4 digit secret key
     *
     * @param key user entered secret key
     */
    private void onKeyEntered(String key) {
        mIsSecreteKeyEntered = true;
        writeDeviceCharacteristics(key);
    }

    /**
     * This will called when user performs any lock,unlock action using the switch on the UI
     * 3x4 keypad matrix will be visible when user wants to lock, unlock.
     */
    private void onLockUnlockEvent() {
        mIsSecreteKeyEntered = false;
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout);
        mFragmentTransaction.replace(R.id.frame_layout_keypad, mFragmentKeypad).addToBackStack("KEYPAD").commitAllowingStateLoss();
    }


    /**
     * It closes the 3x4 keypad matrix screen
     */
    private void closeKeypad() {
        mHandler.post(() -> {
            if (mFragmentManager.getFragments().contains(mFragmentKeypad) && mIsSecreteKeyEntered) {
                mIsSecreteKeyEntered = false;
                mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.setCustomAnimations(R.anim.fade_out, R.anim.fade_in);
                mFragmentTransaction.remove(mFragmentKeypad).commitAllowingStateLoss();
            }

        });
    }


    /**
     * Triggers the read action on connected bluetooth device using Gatt profile
     */
    private synchronized void readDeviceCharacteristics() {
        if (mBluetoothGatt != null) {
            BluetoothGattCharacteristic characteristic = null;

            BluetoothGattService gattService = mBluetoothGatt.getService(GattService.DOOR_LOCK.number);
            if (gattService != null) {
                characteristic = gattService.getCharacteristic(GattCharacteristic.door_lock_state.uuid);
            }
            if (characteristic != null) {
                mBluetoothGatt.readCharacteristic(characteristic);
            }
        }
    }


    /**
     * It will trigger when user want to request for any command from connected bluetooth device.
     *
     * @param extraValue user defined command value
     */
    private synchronized void writeDeviceCharacteristics(final String... extraValue) {
        int state = 0;
        int passkeySplit1 = 0;
        int passkeySplit2 = 0;
        if (mBluetoothGatt != null) {
            BluetoothGattCharacteristic characteristic = null;
            BluetoothGattService gattService = mBluetoothGatt.getService(GattService.DOOR_LOCK.number);
            if (gattService != null) {
                characteristic = gattService.getCharacteristic(GattCharacteristic.door_lock_req.uuid);
            }
            if (mDoorStatusReadings.getLockStatus() == 0) {
                state = 1;
            }
            if (extraValue != null && extraValue.length > 0) {
                passkeySplit1 = Integer.parseInt(extraValue[0].substring(0, 2));
                passkeySplit2 = Integer.parseInt(extraValue[0].substring(2));
            }
            if (characteristic == null) {
                return;
            }

            byte[] data = new byte[]{(byte) state, (byte) passkeySplit1, (byte) passkeySplit2};
            characteristic.setValue(data);
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            mBluetoothGatt.writeCharacteristic(characteristic);
            mBluetoothGatt.setCharacteristicNotification(characteristic, true);
        }
    }

}