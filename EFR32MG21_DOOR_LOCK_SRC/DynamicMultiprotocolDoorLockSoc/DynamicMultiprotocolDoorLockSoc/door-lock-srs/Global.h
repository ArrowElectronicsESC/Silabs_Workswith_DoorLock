/*
 * Global.h
 *
 *  Created on: July 10, 2020
 *      Author: prassanna.sakore
 */

#ifndef GLOBAL_H_
#define GLOBAL_H_

//#define KEYPAD_I2C


#include <string.h>
#include <stdlib.h>
#include "Macro.h"
#include "em_device.h"
#include "em_chip.h"
#include "em_cmu.h"
#include "em_i2c.h"
#include "em_timer.h"
#include "em_usart.h"
#include "hal-config.h"
#include "Button_g.h"
#include "ButtonProcess_g.h"
#include "Controller_Spec_g.h"
#include "Timer_g.h"
#include "Init_g.h"
#include "Mode_g.h"

#ifdef KEYPAD_I2C
#include "I2C_g.h"
#else
#include "TwoWire_g.h"
#endif

#include "Users_g.h"
#include "TTP229Keypad_g.h"
#include "TTP229KeypadProcess_g.h"
#include "UART0_g.h"
#include "UART1_g.h"
#include "R30X_Fingerprint_g.h"
#include "R30X_FingerprintProcess_g.h"
#include "HVC_g.h"
#include "HVC_Process_g.h"

#define BLE_DOOR_LOCK_USER_DATA_LEN 2

/** GATT Server Attribute Value Notification .
 *  Structure to register for  Notification events. */
typedef struct {
  uint8_t lockStatus; 		/**< lock status */
  uint8_t userActionType; 	/**< user action type */
  uint8_t userId; 			/**< ID of the user. */
  uint8_t authType; 		/**< authentication type */
  uint8_t userData[BLE_DOOR_LOCK_USER_DATA_LEN]; 		/**< user data */
  uint8_t totalEnrollUser; 	/**< total enroll user */
}DoorLockUserNotification_t;


enum {
	KEYPAD_MODE,
	FINGERPRINT_MODE,
	FACE_MODE,
	MOBILE_MODE,
	BLE_DOOR_LOCK_DEFAULT_INFO = 99
};


enum {
  DMP_DOOR_UNLOCK,
  DMP_DOOR_LOCK
};
// DOOR state
extern DoorLockUserNotification_t doorStateInfoData;

#endif /* GLOBAL_H_ */
