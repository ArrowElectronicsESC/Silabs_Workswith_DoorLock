package com.siliconlabs.eic.doorlock.ble;

import android.annotation.TargetApi;
import android.bluetooth.le.ScanFilter;
import android.os.Build;
import android.os.ParcelUuid;

import com.siliconlabs.eic.doorlock.utils.Objects;

import java.util.Arrays;

/**
 * Represents a compatible version of {@link ScanFilter} from Lollipop or higher.
 */
public class ScanFilterCompat {
    private String deviceName;
    private ParcelUuid serviceUuid;
    private ParcelUuid serviceUuidMask;
    private String deviceAddress;
    private byte[] serviceData;
    private byte[] serviceDataMask;
    private ParcelUuid serviceDataUuid;
    private int manufacturerId = -1;
    private byte[] manufacturerData;
    private byte[] manufacturerDataMask;

    public ScanFilterCompat() {
    }

    String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    ParcelUuid getServiceUuid() {
        return serviceUuid;
    }

    public void setServiceUuid(ParcelUuid serviceUuid) {
        this.serviceUuid = serviceUuid;
    }

    ParcelUuid getServiceUuidMask() {
        return serviceUuidMask;
    }

    public void setServiceUuidMask(ParcelUuid serviceUuidMask) {
        this.serviceUuidMask = serviceUuidMask;
    }

    String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    byte[] getServiceData() {
        return serviceData;
    }

    public void setServiceData(byte[] serviceData) {
        this.serviceData = serviceData;
    }

    byte[] getServiceDataMask() {
        return serviceDataMask;
    }

    public void setServiceDataMask(byte[] serviceDataMask) {
        this.serviceDataMask = serviceDataMask;
    }

    ParcelUuid getServiceDataUuid() {
        return serviceDataUuid;
    }

    public void setServiceDataUuid(ParcelUuid serviceDataUuid) {
        this.serviceDataUuid = serviceDataUuid;
    }

    int getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(int manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    byte[] getManufacturerData() {
        return manufacturerData;
    }

    public void setManufacturerData(byte[] manufacturerData) {
        this.manufacturerData = manufacturerData;
    }

    byte[] getManufacturerDataMask() {
        return manufacturerDataMask;
    }

    public void setManufacturerDataMask(byte[] manufacturerDataMask) {
        this.manufacturerDataMask = manufacturerDataMask;
    }

    @Override
    public String toString() {
        return "BluetoothLeScanFilter [mDeviceName=" + deviceName + ", mDeviceAddress="
                + deviceAddress
                + ", mUuid=" + serviceUuid + ", mUuidMask=" + serviceUuidMask
                + ", mServiceDataUuid=" + Objects.toString(serviceDataUuid) + ", mServiceData="
                + Arrays.toString(serviceData) + ", mServiceDataMask="
                + Arrays.toString(serviceDataMask) + ", mManufacturerId=" + manufacturerId
                + ", mManufacturerData=" + Arrays.toString(manufacturerData)
                + ", mManufacturerDataMask=" + Arrays.toString(manufacturerDataMask) + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceName, deviceAddress, manufacturerId, manufacturerData,
                manufacturerDataMask, serviceDataUuid, serviceData, serviceDataMask,
                serviceUuid, serviceUuidMask);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ScanFilterCompat)) {
            return false;
        }

        final ScanFilterCompat other = (ScanFilterCompat) obj;
        return Objects.equals(deviceName, other.deviceName) &&
                Objects.equals(deviceAddress, other.deviceAddress) &&
                manufacturerId == other.manufacturerId &&
                Objects.deepEquals(manufacturerData, other.manufacturerData) &&
                Objects.deepEquals(manufacturerDataMask, other.manufacturerDataMask) &&
                Objects.deepEquals(serviceDataUuid, other.serviceDataUuid) &&
                Objects.deepEquals(serviceData, other.serviceData) &&
                Objects.deepEquals(serviceDataMask, other.serviceDataMask) &&
                Objects.equals(serviceUuid, other.serviceUuid) &&
                Objects.equals(serviceUuidMask, other.serviceUuidMask);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScanFilter createScanFilter() {
        ScanFilter.Builder builder = new ScanFilter.Builder()
                .setDeviceAddress(deviceAddress)
                .setDeviceName(deviceName);

        if (serviceUuid != null) {
            builder.setServiceUuid(serviceUuid, serviceUuidMask);
        }
        if (serviceDataUuid != null) {
            builder.setServiceData(serviceDataUuid, serviceData, serviceDataMask);
        }
        if (manufacturerId >= 0) {
            builder.setManufacturerData(manufacturerId, manufacturerData, manufacturerDataMask);
        }

        return builder.build();
    }
}
