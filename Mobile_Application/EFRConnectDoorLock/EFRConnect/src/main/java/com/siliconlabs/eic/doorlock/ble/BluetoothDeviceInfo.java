package com.siliconlabs.eic.doorlock.ble;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.siliconlabs.eic.doorlock.beaconutils.BleFormat;

import java.util.ArrayList;

public class BluetoothDeviceInfo implements Cloneable {
    public static int MAX_EXTRA_DATA = 3;
    public BluetoothDevice device;
    public boolean isOfInterest;
    public boolean isNotOfInterest;
    public boolean serviceDiscoveryFailed;
    public boolean isConnectable;
    public String rawData;
    public long intervalNanos = 0L;
    public int count = 0;
    public long timestampLast = 0;
    public boolean areServicesBeingDiscovered;
    public ScanResultCompat scanInfo;
    protected boolean hasAdvertDetails;
    Object gattHandle;
    private BleFormat bleFormat = null;
    private boolean connected;

    public BluetoothDeviceInfo() {

    }

    public boolean hasUnknownStatus() {
        return (!serviceDiscoveryFailed && !isNotOfInterest && !isOfInterest);
    }

    boolean isUnDiscovered() {
        return (gattHandle == null) && hasUnknownStatus();
    }

    void discover(BluetoothLEGatt bluetoothLEGatt) {
        serviceDiscoveryFailed = isNotOfInterest = isOfInterest = false;
        gattHandle = bluetoothLEGatt;
    }

    @Override
    public BluetoothDeviceInfo clone() {
        final BluetoothDeviceInfo retVal;
        try {
            retVal = (BluetoothDeviceInfo) super.clone();
            retVal.device = device;
            retVal.scanInfo = scanInfo;
            retVal.isOfInterest = isOfInterest;
            retVal.isNotOfInterest = isNotOfInterest;
            retVal.serviceDiscoveryFailed = serviceDiscoveryFailed;
            retVal.bleFormat = bleFormat;
            retVal.gattHandle = null;
            retVal.connected = connected;
            retVal.isConnectable = isConnectable;
            retVal.rawData = rawData;
            retVal.hasAdvertDetails = hasAdvertDetails;
            retVal.areServicesBeingDiscovered = areServicesBeingDiscovered;
            return retVal;
        } catch (CloneNotSupportedException e) {
            Log.e("clone", "Could not clone" + e);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BluetoothDeviceInfo)) {
            return false;
        }

        final BluetoothDeviceInfo that = (BluetoothDeviceInfo) o;
        return device.equals(that.device) && (isOfInterest == that.isOfInterest) && (isNotOfInterest == that.isNotOfInterest) && (serviceDiscoveryFailed == that.serviceDiscoveryFailed);
    }

    @Override
    public int hashCode() {
        return device.hashCode();
    }

    @Override
    public String toString() {
        return scanInfo.toString();
    }

    public BleFormat getBleFormat() {
        if (bleFormat == null) {
            bleFormat = BleFormat.getFormat(this);
        }
        return bleFormat;
    }

    public void setBleFormat(BleFormat bleFormat) {
        this.bleFormat = bleFormat;
    }

    public void setIntervalIfLower(final long intervalNanos) {
        if (intervalNanos <= 0L)
            return;

        if (this.intervalNanos == 0L)
            this.intervalNanos = intervalNanos;
        else if (intervalNanos < this.intervalNanos * 0.7 && count < 10)
            this.intervalNanos = intervalNanos;
        else if (intervalNanos < this.intervalNanos + 3000000) {
            final int limitedCount = Math.min(count, 10);
            this.intervalNanos = (((this.intervalNanos * (limitedCount - 1) + intervalNanos)) / limitedCount);
        } else if (intervalNanos < this.intervalNanos * 1.4) {
            this.intervalNanos = (((this.intervalNanos * (29) + intervalNanos)) / 30);
        }
    }

    public ArrayList<String> getAdvertData() {
        if (scanInfo != null) {
            return scanInfo.getAdvertData();
        }
        return new ArrayList<>();
    }

    public void setAdvertData(ArrayList<String> advertisements) {
        if (scanInfo != null) {
            scanInfo.setAdvertData(advertisements);
        }
    }

    public boolean hasAdvertDetails() {
        return hasAdvertDetails || getAdvertData().size() > MAX_EXTRA_DATA;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public int getRssi() {
        return scanInfo.getRssi();
    }

    public void setRssi(int rssi) {
        this.scanInfo.setRssi(rssi);
    }

    public String getName() {
        return device.getName();
    }

    public String getAddress() {
        return device.getAddress();
    }

}
