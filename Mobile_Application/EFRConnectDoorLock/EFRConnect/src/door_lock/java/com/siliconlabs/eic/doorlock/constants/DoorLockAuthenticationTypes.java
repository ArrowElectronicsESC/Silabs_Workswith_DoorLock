package com.siliconlabs.eic.doorlock.constants;

/**
 * Enum for authentication type
 *
 * @author Vipul Kole (vipul.kole@einfochips.com).
 * Created on:   19-08-2020.
 * Company:  e-Infochips Pvt Ltd,Pune,India.
 * Copyright (c) 2020 Silicon Labs, All rights reserved.
 */
public enum DoorLockAuthenticationTypes {
    KEYPAD(0, "Keypad"),
    FINGERPRINT(1, "Fingerprint"),
    FACE(2, "Face Recognition"),
    MOBILE(3, "Mobile App"),
    SWITCH(4, "Switch"),
    UNKNOWN(99, "Unknown");

    public final int id;
    public final String name;

    DoorLockAuthenticationTypes(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static DoorLockAuthenticationTypes fromId(int id) {
        for (DoorLockAuthenticationTypes type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return null;
    }
}
