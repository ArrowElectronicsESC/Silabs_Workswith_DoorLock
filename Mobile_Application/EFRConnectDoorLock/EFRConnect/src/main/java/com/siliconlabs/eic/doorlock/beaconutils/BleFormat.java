package com.siliconlabs.eic.doorlock.beaconutils;

import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

import com.siliconlabs.eic.doorlock.R;
import com.siliconlabs.eic.doorlock.beaconutils.eddystone.Beacon;
import com.siliconlabs.eic.doorlock.beaconutils.eddystone.Constants;
import com.siliconlabs.eic.doorlock.beaconutils.eddystone.TlmValidator;
import com.siliconlabs.eic.doorlock.beaconutils.eddystone.UidValidator;
import com.siliconlabs.eic.doorlock.beaconutils.eddystone.UrlValidator;
import com.siliconlabs.eic.doorlock.beaconutils.ibeacon.IBeaconInfo;
import com.siliconlabs.eic.doorlock.beaconutils.ibeacon.Utils;
import com.siliconlabs.eic.doorlock.ble.BluetoothDeviceInfo;
import com.siliconlabs.eic.doorlock.ble.ScanResultCompat;

import java.util.List;

public enum BleFormat {
    UNSPECIFIED(R.string.unspecified, R.drawable.beacon_immediate),
    I_BEACON(R.string.ibeacon, R.drawable.beacon_ibeacon),
    ALT_BEACON(R.string.alt_beacon, R.drawable.beacon_alt),
    EDDYSTONE(R.string.eddystone, R.drawable.beacon_eddystone);

    private static final byte[] IBEACON_BYTES_1 = {0x02, 0x01};
    private static final byte[] IBEACON_BYTES_2 = {0x1A, (byte) 0xFF, 0x4C, 0x00, 0x02, 0x15};
    private static final byte[] IBEACON_BYTES_3 = {0x02, 0x15};
    private static final byte[] ALT_BEACON_BYTES_1 = {(byte) 0x1B, (byte) 0xFF};
    private static final byte[] ALT_BEACON_BYTES_2 = {(byte) 0xBE, (byte) 0xAC};
    private static final String EDDYSTONE_SERVICE_UUID = "feaa";
    final private static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private int nameResId;
    private int iconResId;

    BleFormat(int resId, int iconId) {
        this.nameResId = resId;
        this.iconResId = iconId;
    }

    public static BleFormat getFormat(BluetoothDeviceInfo deviceInfo) {
        try {
            if (isAltBeacon(deviceInfo)) {
                return ALT_BEACON;
            }
            if (isIBeacon(deviceInfo)) {
                return I_BEACON;
            }
            if (isEddyStone(deviceInfo)) {
                return EDDYSTONE;
            }
        } catch (Exception e) {
            return UNSPECIFIED;
        }
        return UNSPECIFIED;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static boolean isAltBeacon(BluetoothDeviceInfo deviceInfo) {
        byte[] bytes = deviceInfo.scanInfo.getScanRecord().getBytes();

        for (int i = 0; i < ALT_BEACON_BYTES_1.length; i++) {
            if (bytes[i] != ALT_BEACON_BYTES_1[i]) {
                return false;
            }
        }
        // number of bytes from beginning of ad length to beacon code (alt beacon code is big endian 0xBEAC)
        int byteSpacing = 4;
        for (int i = 0; i < ALT_BEACON_BYTES_2.length; i++) {
            if (bytes[i + byteSpacing] != ALT_BEACON_BYTES_2[i]) {
                return false;
            }
        }

        return true;
    }

    public static boolean isEddyStone(BluetoothDeviceInfo deviceInfo) {
        List<ParcelUuid> uuidList = deviceInfo.scanInfo.getScanRecord().getServiceUuids();
        if (uuidList != null && !uuidList.isEmpty()) {
            for (ParcelUuid parcelUuid : uuidList) {
                String parcelString = parcelUuid.toString().substring(4, 8).toLowerCase();
                if (EDDYSTONE_SERVICE_UUID.equals(parcelString)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Returns true for Blue Gecko-sourced iBeacons. For other iBeacons, checks using isOtherIBeacon
    public static boolean isIBeacon(BluetoothDeviceInfo deviceInfo) {
        byte[] bytes = deviceInfo.scanInfo.getScanRecord().getBytes();
        for (int i = 0; i < IBEACON_BYTES_1.length; i++) {
            if (bytes[i] != IBEACON_BYTES_1[i]) {
                return isOtherIBeacon(deviceInfo);
            }
        }
        for (int i = 0; i < IBEACON_BYTES_2.length; i++) {
            if (bytes[i + 3] != IBEACON_BYTES_2[i]) {
                return isOtherIBeacon(deviceInfo);
            }
        }
        return true;
    }

    // Used to determine if a beacon is an iBeacon, in the case that it is not recognized as a Blue Gecko-sourced iBeacon
    private static boolean isOtherIBeacon(BluetoothDeviceInfo deviceInfo) {
        byte[] bytes = deviceInfo.scanInfo.getScanRecord().getBytes();
        byte[] bytes2 = deviceInfo.scanInfo.getScanRecord().getManufacturerSpecificData(0x004C);

        if (bytes2 != null) {
            if (indexOf(bytes, bytes2, 0) != -1) {
                int index = indexOf(bytes2, IBEACON_BYTES_3, 0);
                return index != -1;
            }
        }
        return false;
    }

    private static int indexOf(byte[] outerArray, byte[] smallerArray, int start) {
        for (int i = start; i < outerArray.length - smallerArray.length + 1; ++i) {
            boolean found = true;
            for (int j = 0; j < smallerArray.length; ++j) {
                if (outerArray[i + j] != smallerArray[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return i;
            }
        }
        return -1;
    }

    // iBeacon
    public static IBeaconInfo getIBeaconInfo(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        return Utils.getIBeaconInfo(device, rssi, scanRecord);
    }

    // Eddystone beacon
    public static Beacon getEddyStoneBeaconInfo(final BluetoothDeviceInfo deviceInfo, final int rssi, final byte[] scanRecord) {
        ScanResultCompat scanInfo = deviceInfo.scanInfo;
        String deviceAddress = scanInfo.getDevice().getAddress();
        Beacon beacon = new Beacon(deviceAddress, scanInfo.getRssi());
        byte[] serviceData = scanInfo.getScanRecord().getServiceData().get(EDDYSTONE_SERVICE_UUID);
        validateEddyStoneServiceData(deviceInfo, beacon, serviceData);
        return beacon;
    }

    private static void validateEddyStoneServiceData(final BluetoothDeviceInfo deviceInfo, Beacon beacon, byte[] serviceData) {
        if (serviceData == null) {
            String err = "Null Eddystone service data";
            beacon.frameStatus.nullServiceData = err;
            return;
        }

        String deviceAddress = deviceInfo.getAddress();

        switch (serviceData[0]) {
            case Constants.UID_FRAME_TYPE:
                UidValidator.validate(deviceAddress, serviceData, beacon);
                break;
            case Constants.TLM_FRAME_TYPE:
                TlmValidator.validate(deviceAddress, serviceData, beacon);
                break;
            case Constants.URL_FRAME_TYPE:
                UrlValidator.validate(deviceAddress, serviceData, beacon);
                break;
            default:
                String err = String.format("Invalid frame type byte %02X", serviceData[0]);
                beacon.frameStatus.invalidFrameType = err;
                break;
        }
    }

    public int getNameResId() {
        return nameResId;
    }

    public int getIconResId() {
        return iconResId;
    }
}
