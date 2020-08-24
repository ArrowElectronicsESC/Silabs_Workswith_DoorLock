/*
 * Bluegiga’s Bluetooth Smart Android SW for Bluegiga BLE modules
 * Contact: support@bluegiga.com.
 *
 * This is free software distributed under the terms of the MIT license reproduced below.
 *
 * Copyright (c) 2013, Bluegiga Technologies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files ("Software")
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF
 * ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A  PARTICULAR PURPOSE.
 */
package com.siliconlabs.eic.doorlock.bluetoothdatamodel.parsing;

import android.content.Context;

import com.siliconlabs.eic.doorlock.R;
import com.siliconlabs.eic.doorlock.activity.DeviceServicesActivity;
import com.siliconlabs.eic.doorlock.bluetoothdatamodel.datatypes.Service;
import com.siliconlabs.eic.doorlock.utils.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.UUID;


// Common - contains common members and methods for whole application
public class Common {
    public static final String BLUEGIGA_URL_ORIGINAL = "http://www.bluegiga.com/en-US/products/bluetooth-4.0-modules/";

    public static final String PROPERTY_VALUE_WRITE = "WRITE";
    public static final String PROPERTY_VALUE_WRITE_NO_RESPONSE = "WRITE NO RESPONSE";
    public static final String PROPERTY_VALUE_READ = "READ";
    public static final String PROPERTY_VALUE_NOTIFY = "NOTIFY";
    public static final String PROPERTY_VALUE_INDICATE = "INDICATE";
    public static final String PROPERTY_VALUE_SIGNED_WRITE = "SIGNED WRITE";
    public static final String PROPERTY_VALUE_EXTENDED_PROPS = "EXTENDED PROPS";
    public static final String PROPERTY_VALUE_BROADCAST = "BROADCAST";

    public static final int MENU_CONNECT = 0;
    public static final int MENU_DISCONNECT = 1;
    public static final int MENU_SCAN_RECORD_DETAILS = 2;

    private static final int START_INDEX_UUID = 4;
    private static final int END_INDEX_UUID = 8;

    public static int FLOAT_POSITIVE_INFINITY = 0x007FFFFE;
    public static int FLOAT_NaN = 0x007FFFFF;
    public static int FLOAT_NRes = 0x00800000;
    public static int FLOAT_RESERVED = 0x00800001;
    public static int FLOAT_NEGATIVE_INFINITY = 0x00800002;
    static final float[] reservedFloatValues = {FLOAT_POSITIVE_INFINITY, FLOAT_NaN, FLOAT_NaN, FLOAT_NaN,
            FLOAT_NEGATIVE_INFINITY};
    public static int FIRST_FLOAT_RESERVED_VALUE = FLOAT_POSITIVE_INFINITY;
    public static int SFLOAT_POSITIVE_INFINITY = 0x07FE;
    public static int SFLOAT_NaN = 0x07FF;
    public static int SFLOAT_NRes = 0x0800;
    public static int SFLOAT_RESERVED = 0x0801;
    public static int SFLOAT_NEGATIVE_INFINITY = 0x0802;
    static final float[] reservedSFloatValues = {SFLOAT_POSITIVE_INFINITY, SFLOAT_NaN, SFLOAT_NaN, SFLOAT_NaN,
            SFLOAT_NEGATIVE_INFINITY};
    public static float FONT_SCALE_SMALL = 0.85f;
    public static float FONT_SCALE_NORMAL = 1.0f;
    public static float FONT_SCALE_LARGE = 1.15f;
    public static float FONT_SCALE_XLARGE = 1.3f;
    public static int FIRST_SFLOAT_RESERVED_VALUE = SFLOAT_POSITIVE_INFINITY;

    // Converts UUID from 16-bit to 128-bit form
    public static String convert16to128UUID(String uuid) {
        return Consts.BLUETOOTH_BASE_UUID_PREFIX + uuid + Consts.BLUETOOTH_BASE_UUID_POSTFIX;
    }

    // Converts UUID from 128-bit to 16-bit form
    public static String convert128to16UUID(String uuid) {
        return uuid.substring(START_INDEX_UUID, END_INDEX_UUID);
    }

    // Compares two uuid objects
    public static boolean equalsUUID(UUID uuida, UUID uuidb) {
        return (uuida.compareTo(uuidb) == 0);
    }

    // Gets properties as human readable text
    public static String getProperties(Context context, int properties) {
        StringBuilder props = new StringBuilder();
        String[] propertyNames = context.getResources().getStringArray(R.array.properties_array);
        for (int i = 0; i < 8; i++) {
            if (((properties >> i) & 1) != 0) {
                props.append(propertyNames[i]).append(", ");
            }
        }
        // remove last comma
        if (props.length() > 0) {
            props = new StringBuilder(props.substring(0, props.length() - 2));
        }
        return props.toString();
    }

    // Checks if given property is set
    public static boolean isSetProperty(PropertyType property, int properties) {
        boolean isSet = ((properties >> (property.ordinal())) & 1) != 0;
        return isSet;
    }

    // Checks if given bit is set
    public static boolean isBitSet(int bit, int value) {
        return (((value >> bit) & 0x1) != 0);
    }

    // Changes bit to opposite value
    public static int toggleBit(int bit, int value) {
        return (value ^= 1 << bit);
    }

    // Reads SFLOAT type
    public static float readSfloat(byte[] value, int start, int end) {
        int mantissa = 0;
        mantissa = (mantissa << 8) | (Integer.parseInt(Converters.getDecimalValue(value[start + end - 1])));
        mantissa = (mantissa << 8) | ((Integer.parseInt(Converters.getDecimalValue(value[start + end]))) & 0x0F);

        int exponent = value[start + end] & 0xF0;
        if (exponent >= 0x0008) {
            exponent = -((0x000F + 1) - exponent);
        }

        float output = 0;

        if (mantissa >= FIRST_SFLOAT_RESERVED_VALUE && mantissa <= SFLOAT_NEGATIVE_INFINITY) {
            output = reservedSFloatValues[mantissa - FIRST_SFLOAT_RESERVED_VALUE];
        } else {
            if (mantissa >= 0x0800) {
                mantissa = -((0x0FFF + 1) - mantissa);
            }
            double magnitude = Math.pow(10.0f, exponent);
            output = (float) (mantissa * magnitude);
        }
        return output;
    }

    // Reads FLOAT type
    public static float readFloat(byte[] value, int start, int end) {
        int mantissa = 0;
        mantissa = (mantissa << 8) | (Integer.parseInt(Converters.getDecimalValue(value[start + end - 1])));
        mantissa = (mantissa << 8) | (Integer.parseInt(Converters.getDecimalValue(value[start + end - 2])));
        mantissa = (mantissa << 8) | (Integer.parseInt(Converters.getDecimalValue(value[start + end - 3])));

        int exponent = value[start + end] & 0xFF;
        if (exponent >= 0x00080) {
            exponent = -((0x00FF + 1) - exponent);
        }

        float output = 0;

        if (mantissa >= FIRST_FLOAT_RESERVED_VALUE && mantissa <= FLOAT_NEGATIVE_INFINITY) {
            output = reservedFloatValues[mantissa - FIRST_FLOAT_RESERVED_VALUE];
        } else {
            if (mantissa >= 0x7FFFFF) {
                mantissa = -((0xFFFFFF + 1) - mantissa);
            }
            double magnitude = Math.pow(10.0f, exponent);
            output = (float) (mantissa * magnitude);
        }
        return output;
    }

    // Reads float32 type
    public static float readFloat32(byte[] value, int start, int end) {
        byte[] bytes = Arrays.copyOfRange(value, start, end);
        float result = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        return result;
    }

    // Reads float64 type
    public static double readFloat64(byte[] value, int start, int end) {
        byte[] bytes = Arrays.copyOfRange(value, start, end);
        double result = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        return result;
    }

    // Returns UUID text in 16 bits version if it is standard Bluetooth UUID or
    // in 128 bits form if not
    public static String getUuidText(UUID uuid) {
        String strUuid = uuid.toString().toUpperCase();

        if (strUuid.startsWith(Consts.BLUETOOTH_BASE_UUID_PREFIX)
                && strUuid.endsWith(Consts.BLUETOOTH_BASE_UUID_POSTFIX)) {
            return "0x" + Common.convert128to16UUID(strUuid);
        } else {
            return strUuid;
        }
    }

    public static String getServiceName(UUID uuid, Context context) {
        Service service = Engine.getInstance().getService(uuid);
        return service != null ? service.getName().trim() : context.getString(R.string.unknown_service);
    }

    public static String checkOTAService(String serviceUuid, String serviceName) {
        if (serviceUuid.toLowerCase().equals(DeviceServicesActivity.ota_service.toString().toLowerCase())) {
            return Constants.OTA_SERVICE;
        }
        return serviceName;
    }

    public enum PropertyType {
        BROADCAST(1),
        READ(2),
        WRITE_NO_RESPONSE(4),
        WRITE(8),
        NOTIFY(16),
        INDICATE(32),
        SIGNED_WRITE(64),
        EXTENDED_PROPS(128);

        private final int value;

        PropertyType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
