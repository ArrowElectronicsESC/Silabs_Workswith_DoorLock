/*
 * Init.c
 *
 *  Created on: July 10, 2020
 *      Author: prassanna.sakore
 */
#include "Global.h"
#include "Init.h"

/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: initCMU
* Description  : Initializes clock for the modules
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void initCMU(void)
{
  // Enabling clock to the I2C and GPIO
#ifdef KEYPAD_I2C
  CMU_ClockEnable(cmuClock_I2C0, TRUE) ;
#endif
  CMU_ClockEnable(cmuClock_GPIO, TRUE) ;
  //CMU_ClockEnable(cmuClock_TIMER0, TRUE) ;
  CMU_ClockEnable(cmuClock_TIMER1, TRUE) ;
  CMU_ClockEnable(cmuClock_USART0, TRUE);
  CMU_ClockEnable(cmuClock_USART1, TRUE);
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: StartPeripherals
* Description  : Starts the peripheral operation
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void StartPeripherals(void)
{

}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: InitVariables
* Description  : Initializes All the Global variables
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void InitVariables(void)
{
	InitButtonVariables() ;
	InitLedVariables() ;
	InitModeVariables() ;
	InitTTP229Variables() ;
	InitR30XFingerprintVariables(FPS_DEFAULT_PASSWORD , FPS_DEFAULT_ADDRESS) ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: InitPeripherals
* Description  : Initializes All the Peripherals
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void InitPeripherals(void)
{
	initCMU() ;
	InitPort() ;
	//InitTimer0() ;
	InitTimer1() ;
#ifdef KEYPAD_I2C
	InitI2C0() ;
#else
	InitTwoWire() ;
#endif
	InitUSART0() ;
	InitUSART1() ;
}

