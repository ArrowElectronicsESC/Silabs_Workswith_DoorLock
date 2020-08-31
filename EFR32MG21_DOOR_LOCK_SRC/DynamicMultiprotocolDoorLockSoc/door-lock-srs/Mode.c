/*
 * Mode.c
 *
 *  Created on: July 10, 2020
 *      Author: prassanna.sakore
 */
#include "Global.h"
#include "Mode.h"

extern void ZigbeeNwFunctionality(uint8_t ucmLongPress) ;
extern void notifyDoorStatus(DoorLockUserNotification_t *doorStateInfo);

/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: ModeProcess
* Description  : This function defines the mode flow
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void ModeProcess(void)
{
	
	switch(Mode.ucmStepOrEnroll) //To process according to the Pressed key 
	{
		case ONE_STEP_AUTHENTICATION :
		
				switch(Mode.ucmModeOfAuthentication) //To process according to the Pressed key 
				{
					case KEYPAD :
						if(SolenoidLock.ucmLockStatus EQ LOCKED)
						{
							KeypadAuthentication() ;

							if(GetAuthenticationState() EQ AUTHENTICATE_SUCCESS_PASSWORD)
							{
								UnlockDoor(Mode.ucmModeOfAuthentication ,Mode.ucmStepOrEnroll , GetAuthenticationUserNo());
								notifyDoorStatus(&doorStateInfoData);


								SetupAuthentication() ;
							}
						}

						break ;
							
					case FINGERPRINT	:
						
						if(SolenoidLock.ucmLockStatus EQ LOCKED)
						{
							FingerAuthentication() ;

							if(GetFingerAuthenticationState() EQ AUTHENTICATE_SUCCESS_FINGER)
							{
								UnlockDoor(Mode.ucmModeOfAuthentication ,Mode.ucmStepOrEnroll , GetFingerAuthenticationUserNo());
								SetupAuthentication() ;
								notifyDoorStatus(&doorStateInfoData);
							}
						}
						break ;
						
					case FACE	:
						if(SolenoidLock.ucmLockStatus EQ LOCKED)
						{
							FaceAuthentication() ;

							if(GetFaceAuthenticationState() EQ AUTHENTICATE_SUCCESS_FACE)
							{
								UnlockDoor(Mode.ucmModeOfAuthentication ,Mode.ucmStepOrEnroll , GetFaceAuthenticationUserNo());
								SetupAuthentication() ;
								notifyDoorStatus(&doorStateInfoData);
							}
						}

						break ;
							
					default :
						   //default
						break ;     
				}
			
			break ;
				
		case TWO_STEP_AUTHENTICATION	:
			
			switch(Mode.ucmModeOfAuthentication) //To process according to the Pressed key 
				{
					case KEYPAD_FINGERPRINT :
						if(SolenoidLock.ucmLockStatus EQ LOCKED)
						{
							KeypadAuthentication() ;

							if(GetAuthenticationState() EQ AUTHENTICATE_SUCCESS_PASSWORD)
							{
								FingerAuthentication() ;

								if(GetFingerAuthenticationState() EQ AUTHENTICATE_SUCCESS_FINGER)
								{
									if((GetFingerAuthenticationUserNo() EQ GetAuthenticationUserNo()) AND
										(GetFingerAuthenticationUserNo() NEQ NOT_AUTHENTICATED) AND
										(GetAuthenticationUserNo() NEQ NOT_AUTHENTICATED))
									{
										UnlockDoor(Mode.ucmModeOfAuthentication ,Mode.ucmStepOrEnroll, GetAuthenticationUserNo());
										SetupAuthentication() ;
										notifyDoorStatus(&doorStateInfoData);

									}
									else
									{
										SetupFingerAuthentication() ;
										SetRedLedForFail() ;
									}
								}
							 }
						   }

						break ;
							
					case KEYPAD_FACE	:
						if(SolenoidLock.ucmLockStatus EQ LOCKED)
						{
							KeypadAuthentication() ;

							if(GetAuthenticationState() EQ AUTHENTICATE_SUCCESS_PASSWORD)
							{
								FaceAuthentication() ;

								if(GetFaceAuthenticationState() EQ AUTHENTICATE_SUCCESS_FACE)
								{
									if((GetFaceAuthenticationUserNo() EQ GetAuthenticationUserNo()) AND
										(GetFaceAuthenticationUserNo() NEQ NOT_AUTHENTICATED) AND
										(GetAuthenticationUserNo() NEQ NOT_AUTHENTICATED))
									{
										UnlockDoor(Mode.ucmModeOfAuthentication ,Mode.ucmStepOrEnroll, GetAuthenticationUserNo());
										SetupAuthentication() ;
										notifyDoorStatus(&doorStateInfoData);

									}
									else
									{
										SetupFaceAuthentication() ;
										SetRedLedForFail() ;
									}
								}
							}
						}
						break ;
							
					default :
						   //default
						break ;     
				}
				
			break ;
			
		case ENROLLMENT	:
		
			switch(Mode.ucmModeOfAuthentication) //To process according to the Pressed key 
				{
					case KEYPAD :
					KeypadEnrollment() ;

					if(GetEnrollmentState() EQ ENROLL_SUCCESS_PASSWORD)
					{
						Mode.ucmModeOfAuthentication = FINGERPRINT ;
					}

						break ;
							
					case FINGERPRINT	:
						
						FingerPrintEnrollment() ;

						if(GetFingerEnrollmentState() EQ ENROLL_SUCCESS_FINGER)
						{
							Mode.ucmModeOfAuthentication = FACE ;
						}

						break ;
						
					case FACE	:
						
						FacePrintEnrollment() ;

						if(GetFaceEnrollmentState() EQ ENROLL_SUCCESS_FACE)
						{
							Users.usmKeypadPassword[Users.usmCurrentEnrollmentNumber] = GetEnrollmentPassword() ;
							Users.usmCurrentEnrollmentNumber ++ ;

							WriteFlash() ;

							SetupAuthentication() ;

							Mode.ucmStepOrEnroll = ONE_STEP_AUTHENTICATION ;
							Mode.ucmModeOfAuthentication = KEYPAD ;
						}

						break ;
							
					default :
						   //default
						break ;     
				}
			
			break ;

		case IDLE_MODE :


			break ;
				
        default :
               //default
            break ;     
	}

	if(SolenoidLock.ucmLockNotify EQ TRUE )
	{
		SolenoidLock.ucmLockNotify = FALSE;
		notifyDoorStatus(&doorStateInfoData);
	}
    
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: ToggleStepOrStartEnrollment
* Description  : This function toggles the step of authentication or starts enrollment
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void ToggleStepOrZigbeeFunctionality(uint8_t ucmLongPress)
{
	if(Mode.ucmStepOrEnroll NEQ IDLE_MODE)
	{
		LockDoor() ;
		SolenoidLock.ucmLockNotify = TRUE ;
		//notifyDoorStatus(&doorStateInfoData);

		if(Mode.ucmStepOrEnroll EQ ONE_STEP_AUTHENTICATION)
		{
			Mode.ucmStepOrEnroll = TWO_STEP_AUTHENTICATION ;
			Mode.ucmModeOfAuthentication = KEYPAD_FINGERPRINT ;
			SetupAuthentication() ;
		}
		else if(Mode.ucmStepOrEnroll EQ TWO_STEP_AUTHENTICATION)
		{
			Mode.ucmStepOrEnroll = ONE_STEP_AUTHENTICATION ;
			Mode.ucmModeOfAuthentication = KEYPAD ;
			SetupAuthentication() ;
		}
		else if(Mode.ucmStepOrEnroll EQ ENROLLMENT)
		{
			ZigbeeNwFunctionality(ucmLongPress) ;
		}
	}
	else
	{
		Mode.ucmStepOrEnroll = ONE_STEP_AUTHENTICATION ;
		Mode.ucmModeOfAuthentication = KEYPAD ;
		SetupAuthentication() ;
		EnableModulesForIdleMode() ;
		EnableUSART0() ;
		EnableUSART1() ;
	}
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: ToggleEnrollmentAndStep
* Description  : This function toggles between enrollment and step of authentication
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void ToggleEnrollmentAndStep(void)
{

	if(Mode.ucmStepOrEnroll NEQ IDLE_MODE)
	{
		LockDoor() ;
		SolenoidLock.ucmLockNotify = TRUE ;
		//notifyDoorStatus(&doorStateInfoData);

		if(Mode.ucmStepOrEnroll EQ ENROLLMENT)
		{
			Mode.ucmStepOrEnroll = ONE_STEP_AUTHENTICATION ;
			Mode.ucmModeOfAuthentication = KEYPAD ;
			SetupAuthentication() ;

			/*if(GetFaceEnrollmentState() NEQ ENROLL_SUCCESS_FACE)
			{
				DeleteTemplate((Users.ucmCurrentEnrollmentNumber + 1) , 1) ;
				HVC_DeleteUser(FACE_DELETE_USER_TIMEOUT , (Users.ucmCurrentEnrollmentNumber + 1) , &ucmOutStatus) ;
			}*/
		}
		else
		{
			Mode.ucmStepOrEnroll = ENROLLMENT ;
			Mode.ucmModeOfAuthentication = KEYPAD ;
			SetupKeypadEnrollment() ;
			SetupFingerEnrollment() ;
			SetupFaceEnrollment() ;
		}
	}
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: IncrementMode
* Description  : This function increments the mode in authentication and mode in enrollment 
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void IncrementMode(void)
{
	if(Mode.ucmStepOrEnroll NEQ IDLE_MODE)
	{
		if(Mode.ucmStepOrEnroll EQ ENROLLMENT)
		{
			return ;
		}
		else
		{
			Mode.ucmModeOfAuthentication++ ;
			LockDoor() ;
			SolenoidLock.ucmLockNotify = TRUE ;
			//notifyDoorStatus(&doorStateInfoData);
	
			SetupAuthentication() ;
		}

		if(Mode.ucmStepOrEnroll EQ ONE_STEP_AUTHENTICATION)
		{
			if(Mode.ucmModeOfAuthentication >= NOOFMODESIN1STEP)
			{
				Mode.ucmModeOfAuthentication = 0 ;
			}
		}
		else if(Mode.ucmStepOrEnroll EQ TWO_STEP_AUTHENTICATION)
		{
			if(Mode.ucmModeOfAuthentication >= NOOFMODESIN2STEP)
			{
				Mode.ucmModeOfAuthentication = 0 ;
			}
		}
	}

}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: UpdateLedStatus
* Description  : This function updates the led status
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void UpdateLedStatus(void)
{
	static uint16_t usmLed1Iterator =	CLEAR ;
	static uint16_t usmLed2Iterator =	CLEAR ;
	
	usmLed1Iterator++ ;
	usmLed2Iterator++ ;

	switch(Mode.ucmStepOrEnroll) //To process according to the Pressed key
	{
		case ONE_STEP_AUTHENTICATION :

				switch(Mode.ucmModeOfAuthentication) //To process according to the Pressed key
				{
					case KEYPAD :
					Led.ucmLed1Status = SLOW_BLINKING;
					Led.ucmLed2Status = SLOW_BLINKING;

						break ;

					case FINGERPRINT	:
					Led.ucmLed1Status = SLOW_BLINKING;
					Led.ucmLed2Status = FAST_BLINKING;

						break ;

					case FACE	:
					Led.ucmLed1Status = SLOW_BLINKING;
					Led.ucmLed2Status = COMPLETE_ON;

						break ;

					default :
						   //default
						break ;
				}

			break ;

		case TWO_STEP_AUTHENTICATION	:

			switch(Mode.ucmModeOfAuthentication) //To process according to the Pressed key
				{
					case KEYPAD_FINGERPRINT :
					Led.ucmLed1Status = FAST_BLINKING;
					Led.ucmLed2Status = SLOW_BLINKING;

						break ;

					case KEYPAD_FACE	:
					Led.ucmLed1Status = FAST_BLINKING;
					Led.ucmLed2Status = FAST_BLINKING;

						break ;

					default :
						   //default
						break ;
				}

			break ;

		case ENROLLMENT	:

			switch(Mode.ucmModeOfAuthentication) //To process according to the Pressed key
				{
					case KEYPAD :
					Led.ucmLed1Status = COMPLETE_ON;
					Led.ucmLed2Status = SLOW_BLINKING;

						break ;

					case FINGERPRINT	:
					Led.ucmLed1Status = COMPLETE_ON;
					Led.ucmLed2Status = FAST_BLINKING;

						break ;

					case FACE	:
					Led.ucmLed1Status = COMPLETE_ON;
					Led.ucmLed2Status = COMPLETE_ON;

						break ;

					default :
						   //default
						break ;
				}

			break ;

		case IDLE_MODE :

			Led.ucmLed1Status = COMPLETE_OFF;
			Led.ucmLed2Status = COMPLETE_OFF;

			break ;

		default :
			   //default
			break ;
	}

	if(Led.ucmLed1Status EQ SLOW_BLINKING)
	{
		if(usmLed1Iterator >= SLOW_BLINKING_mSec)
		{
			GPIO_PinOutToggle(LED1_PIN_PORT, LED1_PIN);
			usmLed1Iterator = CLEAR;
		}

	}
	else if(Led.ucmLed1Status EQ FAST_BLINKING)
	{
		if(usmLed1Iterator >= FAST_BLINKING_mSec)
		{
			GPIO_PinOutToggle(LED1_PIN_PORT, LED1_PIN);
			usmLed1Iterator = CLEAR;
		}
	}
	else if(Led.ucmLed1Status EQ COMPLETE_ON)
	{
		GPIO_PinOutSet(LED1_PIN_PORT, LED1_PIN);
	}
	else if(Led.ucmLed1Status EQ COMPLETE_OFF)
	{
		GPIO_PinOutClear(LED1_PIN_PORT, LED1_PIN);
	}

	if(Led.ucmLed2Status EQ SLOW_BLINKING)
	{
		if(usmLed2Iterator >= SLOW_BLINKING_mSec)
		{
			GPIO_PinOutToggle(LED2_PIN_PORT, LED2_PIN);
			usmLed2Iterator = CLEAR;
		}

	}
	else if(Led.ucmLed2Status EQ FAST_BLINKING)
	{
		if(usmLed2Iterator >= FAST_BLINKING_mSec)
		{
			GPIO_PinOutToggle(LED2_PIN_PORT, LED2_PIN);
			usmLed2Iterator = CLEAR;
		}
	}
	else if(Led.ucmLed2Status EQ COMPLETE_ON)
	{
		GPIO_PinOutSet(LED2_PIN_PORT, LED2_PIN);
	}
	else if(Led.ucmLed2Status EQ COMPLETE_OFF)
	{
		GPIO_PinOutClear(LED2_PIN_PORT, LED2_PIN);
	}
	
	BlinkGreenLed() ;
	BlinkRedLed() ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: InitLedVariables
* Description  : This function initializes LED variables
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void InitLedVariables(void)
{
	Led.ucmLed1Status		= COMPLETE_ON ; // Default: Enrollment
	Led.ucmLed2Status		= SLOW_BLINKING ; // Default: Keypad
	Led.ucmGreenLedStatus	= OFF ;
	Led.ucmRedLedStatus		= OFF ;
	SolenoidLock.ucmLockNotify = TRUE ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: InitModeVariables
* Description  : This function initializes Mode variables
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void InitModeVariables(void)
{
	Mode.ucmStepOrEnroll 			= ONE_STEP_AUTHENTICATION ;
	Mode.ucmModeOfAuthentication	= KEYPAD ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: BlinkRedLed
* Description  : This function blinks red led
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void BlinkRedLed(void)
{
	if(Led.usmRedLedIterator)
	{
		Led.usmRedLedIterator-- ;

		if(Led.ucmRedLedStatus EQ RED_FAIL)
		{
			if(Led.usmRedLedIterator EQ ZERO)
			{
				GPIO_PinOutClear(RED_LED_PIN_PORT , RED_LED_PIN);
			}

		}

	}
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: BlinkGreenLed
* Description  : This function blinks green led
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void BlinkGreenLed(void)
{

	if(Led.usmGreenLedIterator)
	{
		Led.usmGreenLedIterator-- ;

		if(Led.ucmGreenLedStatus EQ USER_ACTION)
		{
			if(Led.usmGreenLedIterator EQ ZERO)
			{
				GPIO_PinOutClear(GREEN_LED_PIN_PORT , GREEN_LED_PIN);

			}

		}
		else if(Led.ucmGreenLedStatus EQ GREEN_SUCCESS)
		{
			if(Led.usmGreenLedIterator EQ ZERO)
			{
				GPIO_PinOutClear(GREEN_LED_PIN_PORT , GREEN_LED_PIN);

			}

		}
	}


}

/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: SetGreenLedForUserAction
* Description  : This function sets green led for user action
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void SetGreenLedForUserAction(void)
{
	Led.usmGreenLedIterator = USER_ACTION_mSec ;
	Led.ucmGreenLedStatus = USER_ACTION ;
	GPIO_PinOutSet(GREEN_LED_PIN_PORT , GREEN_LED_PIN);
}

/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: SetGreenLedForGreenSuccess
* Description  : This function sets green led for green success
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void SetGreenLedForGreenSuccess(void)
{
	Led.usmGreenLedIterator = GREEN_SUCCESS_mSec ;
	Led.ucmGreenLedStatus = GREEN_SUCCESS ;
	GPIO_PinOutSet(GREEN_LED_PIN_PORT , GREEN_LED_PIN);
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: SetRedLedForFail
* Description  : This function sets red led for fail
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void SetRedLedForFail(void)
{
	Led.usmRedLedIterator = RED_FAIL_mSec ;
	Led.ucmRedLedStatus = RED_FAIL ;
	GPIO_PinOutSet(RED_LED_PIN_PORT , RED_LED_PIN);
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: UpadateSolenoidLockTime
* Description  : This function updates the lock time
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void UpadateSolenoidLockTime(void)
{
	if(SolenoidLock.usmLockIterator)
	{
		SolenoidLock.usmLockIterator -- ;

		if(SolenoidLock.usmLockIterator EQ ZERO)
		{
			SetupAuthentication() ;
			LockDoor() ;
			SolenoidLock.ucmLockNotify = TRUE ;
			//notifyDoorStatus(&doorStateInfoData);

		}
	}
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: UnlockDoor
* Description  : This function unlocks the door
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void UnlockDoor(uint8_t ucmAuthType ,uint8_t ucmUserActionType , uint8_t ucmUserID)
{
	GPIO_PinOutSet(SOLENOID_PIN_PORT , SOLENOID_PIN);
	SolenoidLock.usmLockIterator = DOOR_AUTOMATIC_LOCK_TIME ;
	SolenoidLock.ucmLockStatus = UNLOCKED ;
	doorStateInfoData.authType = ucmAuthType;
	doorStateInfoData.lockStatus = DMP_DOOR_UNLOCK;
	doorStateInfoData.userId = ucmUserID ;
	//doorStateInfoData.userActionType = ucmUserActionType;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: LockDoor
* Description  : This function locks the door
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void LockDoor(void)
{
	GPIO_PinOutClear(SOLENOID_PIN_PORT , SOLENOID_PIN);
	SolenoidLock.usmLockIterator = ZERO ;
	SolenoidLock.ucmLockStatus = LOCKED ;
	doorStateInfoData.authType = BLE_DOOR_LOCK_DEFAULT_INFO;
	doorStateInfoData.lockStatus = DMP_DOOR_LOCK;

}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: SetupAuthentication
* Description  : This function sets up for authentication
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void SetupAuthentication(void)
{
	SetupFaceAuthentication() ;
	SetupFingerAuthentication() ;
	SetupKeypadAuthentication() ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: SetControllerToIdleMode
* Description  : This function sets mode to idle
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void SetControllerToIdleMode(void)
{
	Mode.ucmStepOrEnroll = IDLE_MODE ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: UpdateIdleState
* Description  : This function updates time to set the device in IDLE mode
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void UpdateIdleState(void)
{
	//Button Press Timeout to enter sleep
	if(Idle.usmIdleIterator)
	{
		Idle.usmIdleIterator-- ;

		if(Idle.usmIdleIterator EQ ZERO)
		{
			SetControllerToIdleMode() ;
			DisableModulesForIdleMode() ;
			DisableUSART0() ;
			DisableUSART1() ;
		}
	}
}

/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: RefreshIdleStateTimer
* Description  : This function refreshes IDLE state iterator
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void RefreshIdleStateTimer(void)
{
	Idle.usmIdleIterator = IDLE_TIMEOUT ;
}

/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: EnableModulesForIdleMode
* Description  : This function enables modules when exiting Idle mode
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void EnableModulesForIdleMode(void)
{
	GPIO_PinOutSet(MODULE_DISABLE_PIN_PORT , MODULE_DISABLE_PIN);
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: DisableModulesForIdleMode
* Description  : This function disables modules when entering Idle mode
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void DisableModulesForIdleMode(void)
{
	GPIO_PinOutClear(MODULE_DISABLE_PIN_PORT , MODULE_DISABLE_PIN);
}
