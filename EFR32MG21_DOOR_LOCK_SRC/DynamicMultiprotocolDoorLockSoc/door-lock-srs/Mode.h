/*
 * Mode.h
 *
 *  Created on: July 10, 2020
 *      Author: prassanna.sakore
 */

#ifndef MODE_H_
#define MODE_H_

#define ONE_STEP_AUTHENTICATION		0
#define TWO_STEP_AUTHENTICATION		1
#define ENROLLMENT					2
#define IDLE_MODE 					3

#define KEYPAD_FINGERPRINT			0
#define KEYPAD_FACE					1

#define SLOW_BLINKING				0
#define FAST_BLINKING				1
#define COMPLETE_ON					2
#define COMPLETE_OFF				3

#define SLOW_BLINKING_mSec 			500
#define FAST_BLINKING_mSec			100
#define USER_ACTION_mSec			50
#define GREEN_SUCCESS_mSec			2000
#define RED_FAIL_mSec				2000

#define NOOFMODESIN1STEP 			3
#define NOOFMODESIN2STEP			2
#define NOOFMODESINENROLL			3

#define USER_ACTION 0
#define GREEN_SUCCESS 1
#define RED_FAIL	  2

#define LOCKED	0
#define UNLOCKED 1

#define DOOR_AUTOMATIC_LOCK_TIME 15000

#define IDLE_TIMEOUT 30000

typedef struct 
{
	uint8_t 	ucmStepOrEnroll				;
	uint8_t 	ucmModeOfAuthentication		;
}TagMode ;

TagMode Mode ;

typedef struct 
{
	uint8_t 	ucmLed1Status		;
	uint8_t 	ucmLed2Status		;
	uint8_t 	ucmGreenLedStatus	;
	uint8_t 	ucmRedLedStatus		;
	uint16_t 	usmGreenLedIterator ;
	uint16_t 	usmRedLedIterator ;
}TagLed ;

TagLed Led ;

typedef struct
{
	uint8_t 	ucmLockStatus		;
	uint16_t 	usmLockIterator 	;
	uint8_t 	ucmLockNotify      	;
}TagSolenoidLock ;

TagSolenoidLock SolenoidLock ;

typedef struct
{
	uint16_t 	usmIdleIterator 	;
}TagIdle ;

TagIdle Idle ;

void BlinkGreenLed(void) ;
void BlinkRedLed(void) ;
void SetupAuthentication(void) ;
void EnableModulesForIdleMode(void) ;
void DisableModulesForIdleMode(void) ;
#endif /* MODE_H_ */
