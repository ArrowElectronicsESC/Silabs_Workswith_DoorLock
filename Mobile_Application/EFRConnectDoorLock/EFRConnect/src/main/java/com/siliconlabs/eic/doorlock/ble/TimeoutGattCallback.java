package com.siliconlabs.eic.doorlock.ble;

import android.bluetooth.BluetoothGattCallback;

public abstract class TimeoutGattCallback extends BluetoothGattCallback {
    public void onTimeout() {
    }
}
