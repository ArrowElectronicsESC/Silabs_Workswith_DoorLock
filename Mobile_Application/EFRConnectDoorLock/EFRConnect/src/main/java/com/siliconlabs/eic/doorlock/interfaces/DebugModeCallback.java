package com.siliconlabs.eic.doorlock.interfaces;

import com.siliconlabs.eic.doorlock.ble.BluetoothDeviceInfo;

public interface DebugModeCallback {

    void connectToDevice(BluetoothDeviceInfo device);

    void addToFavorite(String deviceAddress);

    void removeFromFavorite(String deviceAddress);

    void addToTemporaryFavorites(String deviceAddress);

    void updateCountOfConnectedDevices();

    void gotoDeviceProperty(BluetoothDeviceInfo device);
}