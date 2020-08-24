package com.siliconlabs.eic.doorlock.constants;

/**
 * Bluetooth gatt characteristics byte order format
 *
 * @author Vipul Kole (vipul.kole@einfochips.com).
 * Created on:   19-08-2020.
 * Company:  e-Infochips Pvt Ltd,Pune,India.
 * Copyright (c) 2020 Silicon Labs, All rights reserved.
 */
public enum DoorLockMessageFormat {
    LOCK_UNLOCK_STATE,  //  0 = unlocked,       1 = locked
    USER_ACTION_TYPE,   //  enum UserActionType
    USER_ID,            //  user_id
    AUTHENTICATION_TYPE,//  enum AuthenticationType
    USER_DATA0,         //  TBD
    USER_DATA1,         //  TBD
    TOTAL_USERS         //  TBD
}
