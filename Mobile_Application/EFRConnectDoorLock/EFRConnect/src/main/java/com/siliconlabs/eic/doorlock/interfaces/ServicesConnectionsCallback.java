package com.siliconlabs.eic.doorlock.interfaces;

import com.siliconlabs.eic.doorlock.ble.BluetoothDeviceInfo;

public interface ServicesConnectionsCallback {
    void onDisconnectClicked(BluetoothDeviceInfo deviceInfo);

    void onDeviceClicked(BluetoothDeviceInfo device);
}
