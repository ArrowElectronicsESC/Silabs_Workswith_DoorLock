package com.siliconlabs.eic.doorlock.constants;

/**
 * Enum for actions performed by user with application specific and remote operations
 *
 * @author Vipul Kole (vipul.kole@einfochips.com).
 * Created on:   19-08-2020.
 * Company:  e-Infochips Pvt Ltd,Pune,India.
 * Copyright (c) 2020 Silicon Labs, All rights reserved.
 */
public enum DoorLockEvents {
    NONE(-1, ""),
    REMOTE_EVENT_AUTHENTICATION(11, "Remote Authentication"),//  Authentication is performed remotely(fingerprint,face,keypad)
    REMOTE_EVENT_ENROLLMENT(12, "Remote Enrollment"),    //  New user enrollment is done remotely
    REMOTE_REQUEST_SUCCESS(13, "Remote Request Success"),     //  Request successful
    REMOTE_REQUEST_FAILED(14, "Remote Request Failed"),      //  Write request failed
    REMOTE_MESSAGE_RECEIVED(15, "Message Received"),    //  Characteristics message received
    REMOTE_LOCK_STATE_CHANGED(16, "Lock State Changed"),  //  Lock state is changed remotely
    UI_EVENT_LOCK_UNLOCK(21, "Lock State Changed From Mobile App"),       //  Lock State is changed by user by swipe button on UI
    UI_EVENT_PASSKEY_COMPLETE(22, "Passkey Length Fulfilled"),  //  User completed entering of the 4 digit secret key
    UI_EVENT_NOTIFICATION(23, "Notification");       //  Application needs to display notification

    public final int id;
    public final String name;

    DoorLockEvents(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static DoorLockEvents fromId(int id) {
        for (DoorLockEvents type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return null;
    }

}
