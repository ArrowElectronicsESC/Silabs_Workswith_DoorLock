/*
 * Mode_g.h
 *
 *  Created on: July 10, 2020
 *      Author: prassanna.sakore
 */

#ifndef MODE_G_H_
#define MODE_G_H_

#define KEYPAD						0
#define FINGERPRINT					1
#define FACE						2

void ModeProcess(void) ;
void ToggleStepOrZigbeeFunctionality(uint8_t ucmLongPress) ;
void ToggleEnrollmentAndStep(void) ;
void IncrementMode(void) ;
void UpdateLedStatus(void) ;
void InitModeVariables(void) ;
void InitLedVariables(void) ;
void SetGreenLedForUserAction(void) ;
void SetGreenLedForGreenSuccess(void) ;
void SetRedLedForFail(void) ;
void UpadateSolenoidLockTime(void) ;
void UnlockDoor(uint8_t ucmAuthType ,uint8_t ucmUserActionType , uint8_t ucmUserID) ;
void LockDoor(void) ;
void SetControllerToIdleMode(void) ;
void UpdateIdleState(void) ;
void RefreshIdleStateTimer(void) ;
void DeleteAllUsers(void) ;

#endif /* MODE_G_H_ */
