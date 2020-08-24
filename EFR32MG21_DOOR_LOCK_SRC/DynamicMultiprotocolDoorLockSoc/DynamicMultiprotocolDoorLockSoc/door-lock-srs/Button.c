/*
 * Button.c
 *
 *  Created on: July 10, 2020
 *      Author: prassanna.sakore
 */
#include "Global.h"
#include "Button.h"



static	uint8_t ucsLongButtonPressDetectTime 		= TIME_BETWEEN_SHORT_PRESS_N_LONG_PRESS_TIME_COUNT ;
static	uint8_t ucsPreviousPressedButton			= CLEAR ;		//To hold the Previously Pressed Button
static	uint8_t ucsPreButtonReleaseStatus			= TRUE	;		//To hold the status of Button as released or not
static	uint8_t ucsFirstShortPressProcessed 		= FALSE ;
static	uint8_t ucsButtonLongPressTime_Sec 			= CLEAR ;
static	uint16_t ussButtonPressedTime_mS 			= CLEAR ;
static 	uint8_t ucsButtonReleaseConfirmCount 		= CLEAR ;
static  uint8_t ucsOneTimeButtonLongPress			= FALSE ;


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: ButtonRead
* Description  : Identify which Button(s) is(are) pressed
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void ButtonDetect(void)
{
	uint8_t ucmButtonPressed   	= CLEAR ;	   //To Hold the Currently pressed Button
	uint8_t ucmButtonMode 		= CLEAR ;
	uint8_t ucmButtonStep 		= CLEAR ;

	ucmButtonMode = (uint8_t)( GPIO_PinInGet(MODE_BUTTON_PIN_PORT, MODE_BUTTON_PIN) << 1 ) ;
	ucmButtonStep = (uint8_t)( GPIO_PinInGet(STEP_BUTTON_PIN_PORT, STEP_BUTTON_PIN) ) ;

	ucmButtonPressed 	= ((~( ucmButtonMode | ucmButtonStep )) & 0x03)	;


	if(ucmButtonPressed)
	{
		ucsButtonReleaseConfirmCount	= CLEAR ;

		if(ucsPreButtonReleaseStatus EQ TRUE)
		{
			//new Button pressed after there ideal State i.e All Buttons are not pressed.

			ucsPreviousPressedButton 	= ucmButtonPressed ;
			ucsFirstShortPressProcessed	= FALSE ;
			ussButtonPressedTime_mS 	= CLEAR ;
			ucsButtonLongPressTime_Sec 	= CLEAR ;
			ucsOneTimeButtonLongPress 	= FALSE ;
		}
		else
		{
			if(ucmButtonPressed NEQ ucsPreviousPressedButton)
			{
				ucsPreviousPressedButton  	= ucmButtonPressed ;
				ucsFirstShortPressProcessed	= FALSE ;
				ussButtonPressedTime_mS 	= CLEAR ;
				ucsButtonLongPressTime_Sec 	= CLEAR ;
				ucsOneTimeButtonLongPress 	= FALSE ;
			}
			else
			{
				ussButtonPressedTime_mS += BUTTON_READ_TIME_mS ;

				if(ussButtonPressedTime_mS >= 1000)	// 1000 is milli seconds in 1 Second
				{
					ussButtonPressedTime_mS -= 1000 ;
					ucsButtonLongPressTime_Sec ++ ;
				}

				if(ucsFirstShortPressProcessed	EQ FALSE)
				{
					CalculateButtonForProcess((ucmButtonPressed | BUTTON_SHORT_PRESSED) , ucsButtonLongPressTime_Sec , ucsOneTimeButtonLongPress);

					ucsFirstShortPressProcessed 	= TRUE ;
					ucsLongButtonPressDetectTime = TIME_BETWEEN_SHORT_PRESS_N_LONG_PRESS_TIME_COUNT ; 	//Load the long Button press detection time after first short button press process
				}
				else
				{
					if(ucsLongButtonPressDetectTime)
					{
						ucsLongButtonPressDetectTime -- ;

						if(!ucsLongButtonPressDetectTime)
						{
							if(ucsOneTimeButtonLongPress EQ TRUE)
							{
								ucsButtonLongPressTime_Sec = 0 ;
							}

							if(ucsButtonLongPressTime_Sec >= ONE_TIME_LONG_PRESS_TIME_Sec)
							{
								ucsOneTimeButtonLongPress = TRUE ;
							}

							CalculateButtonForProcess((ucmButtonPressed | BUTTON_LONG_PRESSED) , ucsButtonLongPressTime_Sec , ucsOneTimeButtonLongPress);
							ucsLongButtonPressDetectTime = LONG_PRESS_CHECK_EVENT_TIME_COUNT ;                 //load long Button detection time between two long detection event
						}
					}
				}
			}
		}

		ucsPreButtonReleaseStatus			= FALSE ;
	}
	else
	{
		ucsButtonReleaseConfirmCount++ ;

		if((ucsButtonReleaseConfirmCount >= BUTTON_RELEASE_CONFIRM_COUNT) OR (ucsFirstShortPressProcessed EQ FALSE))
		{
			//Reinitializing Button Status Variables as their default Value
			ucsPreButtonReleaseStatus			= TRUE	;
			ucsPreviousPressedButton			= CLEAR ;
			ucsFirstShortPressProcessed			= FALSE ;
			ussButtonPressedTime_mS				= CLEAR ;
			ucsButtonLongPressTime_Sec 			= CLEAR ;
			ucsOneTimeButtonLongPress 			= FALSE ;
			ucsButtonReleaseConfirmCount		= CLEAR ;
		}
    }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: CalculateButtonForProcess
* Description  : Finalize the Button pressed and type of Button press
* Arguments    : uint8_t ucvButtonPressed , uint8_t ucvButtonPressedTime_Sec , uint8_t ucvOneTimeButtonLongPress
* Return Value : None
**************************************************************************************************************************************************************/
void CalculateButtonForProcess(uint8_t ucvButtonPressed , uint8_t ucvButtonPressedTime_Sec , uint8_t ucvOneTimeButtonLongPress)
{
	if(Button.ucmProcessed EQ TRUE)
	{
	    if(ucvButtonPressed & BUTTON_LONG_PRESSED)          //To check the Button obtained is Long Press or not. if the MSB bit (D7) of "ucmButtonPressed" is "1" then the button is Long Pressed otherwise not.
        {
            Button.ucmButtonPressedType   	= LONG_PRESS  ;   //to indicate that the Button is Long pressed
            ucvButtonPressed           		&= 0x0F ;         //as actual Button pressed is in the Lower 4 bit
        }
        else
        {
            Button.ucmButtonPressedType   	= SHORT_PRESS      ;   //to indicate that the Button is short pressed
        }

		Button.ucmButtonPressedTime			= ucvButtonPressedTime_Sec ;

		Button.ucmOneTimeButtonLongPress 	= ucvOneTimeButtonLongPress ;

        Button.usmFinalProcessingButton   	= (uint16_t)1 << (ucvButtonPressed - 1 ); //Getting Button code for pressed Button

		Button.ucmProcessed 				= FALSE ;
	}
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: InitButtonVariables
* Description  : Initialize Button related global variables
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void InitButtonVariables(void)
{
	Button.ucmProcessed 				= FALSE ;
	Button.ucmButtonCheckFlag			= FALSE ;
	Button.ucmButtonPressedTime			= CLEAR ;
	Button.ucmButtonReadFlag			= FALSE ;
	Button.ucmOneTimeButtonLongPress  	= FALSE ;
	Button.usmFinalProcessingButton		= CLEAR ;
	Button.usmButtonPressTimeout 		= CLEAR ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: UpdateButtonRead
* Description  : Updates button read status and timer. To be called in 1 ms timer
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void UpdateButtonRead(void)
{
	static uint8_t ucmButtonReadIterator =	CLEAR ;

	//Set flag of button for reading every 50ms
	ucmButtonReadIterator++;
	if(ucmButtonReadIterator >= BUTTON_READ_TIME_mS)
	{
		Button.ucmButtonReadFlag = TRUE ;
		ucmButtonReadIterator = CLEAR;
	}
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: UpdateButtonCheck
* Description  : Updates button check status and timer. To be called in 1 ms timer
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void UpdateButtonCheck(void)
{

	static uint8_t ucmButtonCheckIterator = CLEAR ;

	//Set flag of button for checking every 100ms
	ucmButtonCheckIterator++;
	if(ucmButtonCheckIterator >= BUTTON_CHECK_TIME_mS)
	{
		Button.ucmButtonCheckFlag = TRUE ;
		ucmButtonCheckIterator = CLEAR;
	}

}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: UpdateButtonIdle
* Description  : Decrements button timeout value after button is pressed
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void UpdateButtonIdle(void)
{
	//Button Press Timeout to enter sleep
	if(Button.usmButtonPressTimeout)
	{
		Button.usmButtonPressTimeout-- ;
	}
}
