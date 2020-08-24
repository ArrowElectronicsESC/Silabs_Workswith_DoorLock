package com.siliconlabs.eic.doorlock.constants;

/**
 * Enum for user action type
 *
 * @author Vipul Kole (vipul.kole@einfochips.com).
 * Created on:   19-08-2020.
 * Company:  e-Infochips Pvt Ltd,Pune,India.
 * Copyright (c) 2020 Silicon Labs, All rights reserved.
 */
public enum DoorLockActions {

    AUTHENTICATED(0, "Authenticated"),
    ENROLLED(1, "Enrolled"),
    INVALID(98, "Invalid Key"),
    UNKNOWN(99, "NONE");
    public final int id;
    public final String name;

    DoorLockActions(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static DoorLockActions fromId(int id) {
        for (DoorLockActions type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return null;
    }
}
