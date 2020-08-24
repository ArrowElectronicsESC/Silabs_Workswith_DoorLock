package com.siliconlabs.eic.doorlock.interfaces;

import com.siliconlabs.eic.doorlock.constants.DoorLockEvents;

/**
 * Callback for door lock/unlock operations.
 *
 * @author Vipul Kole (vipul.kole@einfochips.com).
 * Created on: 23-07-2020.
 * Company:  e-Infochips Pvt Ltd,Pune,India.
 * Copyright (c) 2020 Silicon Labs, All rights reserved.
 */
public interface DoorLockCallback {
    void onAction(DoorLockEvents i, Object... extra);
}
