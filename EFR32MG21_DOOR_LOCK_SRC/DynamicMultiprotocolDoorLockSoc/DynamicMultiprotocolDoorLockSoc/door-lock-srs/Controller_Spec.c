/*
 * Controller_Spec.c
 *
 *  Created on: July 10, 2020
 *      Author: prassanna.sakore
 */
#include "Global.h"
#include "Controller_Spec.h"

/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: InitPort
* Description  : Initializes Controller Port Pins
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void InitPort(void)
{
	  // Configure Button 1 and Button 2 as digital inputs
	  GPIO_PinModeSet(MODE_BUTTON_PIN_PORT, MODE_BUTTON_PIN, gpioModeInputPullFilter, 1);
	  GPIO_PinModeSet(STEP_BUTTON_PIN_PORT, STEP_BUTTON_PIN, gpioModeInputPullFilter, 1);

	  // Configure LED1 and LED2 as digital output
	  GPIO_PinModeSet(LED1_PIN_PORT, LED1_PIN, gpioModePushPull, 0);
	  GPIO_PinModeSet(LED2_PIN_PORT, LED2_PIN, gpioModePushPull, 0);

	  // Configure GREEN and RED LEDs as digital output
	  GPIO_PinModeSet(GREEN_LED_PIN_PORT, GREEN_LED_PIN, gpioModePushPull, 0);
	  GPIO_PinModeSet(RED_LED_PIN_PORT, RED_LED_PIN, gpioModePushPull, 0);

	  // Configure Solenoid pin as digital output
	  GPIO_PinModeSet(SOLENOID_PIN_PORT, SOLENOID_PIN, gpioModePushPull, 0);
	
}

void DelayMicroseconds(uint32_t ulmDelayInMicroSec)
{
	uint32_t ulmInstructionsPerMicroSec , ulmMicroSec , ulmCount;

	ulmCount =  MICRO_SEC_IN_SEC_COUNT ;

	for(ulmMicroSec = 0 ; ulmMicroSec < ulmDelayInMicroSec ; ulmMicroSec++)
	{
		for(ulmInstructionsPerMicroSec = 0 ; ulmInstructionsPerMicroSec < ulmCount ; ulmInstructionsPerMicroSec++)
		{

		}
	}
}

