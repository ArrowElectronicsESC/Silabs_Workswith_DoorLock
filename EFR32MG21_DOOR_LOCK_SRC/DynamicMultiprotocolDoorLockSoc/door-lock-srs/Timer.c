/*
 * Timer.c
 *
 *  Created on: July 10, 2020
 *      Author: prassanna.sakore
 */
#include "Global.h"
#include "Timer.h"

/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: InitTimer0
* Description  : This function initializes the Timer 0 - 1ms timer
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void InitTimer0(void)
{
	uint32_t ulmTimerFreq = 0 ;
	uint32_t ulmOverFlowValue = 0 ;

	// Initialize the timer
	TIMER_Init_TypeDef timerInit = TIMER_INIT_DEFAULT;

	// Use prescalar value 1024
	timerInit.prescale = timerPrescale16;
	timerInit.enable = FALSE;

	// Configure but do not start the timer
	TIMER_Init(TIMER0, &timerInit);

	// Calculate interrupt overflow value
	ulmTimerFreq = CMU_ClockFreqGet(cmuClock_TIMER0) / (timerInit.prescale + 1);
	ulmOverFlowValue = (ulmTimerFreq * TIMER_0_PERIOD_Sec);

	// Set top value to overflow value
	TIMER_TopSet(TIMER0, ulmOverFlowValue);

	// Start the timer
	TIMER_Enable(TIMER0, TRUE);

	// Enable TIMER0 overflow event interrupt
	TIMER_IntEnable(TIMER0, TIMER_IF_OF);
	NVIC_EnableIRQ(TIMER0_IRQn);
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: InitTimer1
* Description  : This function initializes the Timer 1 - 1ms timer
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void InitTimer1(void)
{
	uint32_t ulmTimerFreq = 0 ;
	uint32_t ulmOverFlowValue = 0 ;

	// Initialize the timer
	TIMER_Init_TypeDef timerInit = TIMER_INIT_DEFAULT;

	// Use prescalar value 1024
	timerInit.prescale = timerPrescale16;
	timerInit.enable = FALSE;

	// Configure but do not start the timer
	TIMER_Init(TIMER1, &timerInit);

	// Calculate interrupt overflow value
	ulmTimerFreq = CMU_ClockFreqGet(cmuClock_TIMER1) / (timerInit.prescale + 1);
	ulmOverFlowValue = (ulmTimerFreq * TIMER_1_PERIOD_Sec);

	// Set top value to overflow value
	TIMER_TopSet(TIMER1, ulmOverFlowValue);

	// Start the timer
	TIMER_Enable(TIMER1, TRUE);

	// Enable TIMER1 overflow event interrupt
	TIMER_IntEnable(TIMER1, TIMER_IF_OF);
	NVIC_EnableIRQ(TIMER1_IRQn);
}


/***********************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: TIMER0_IRQHandler
* Description  : This function is Timer0 interrupt service routine.
* Arguments    : None
* Return Value : None
***********************************************************************************************************************/
/*void TIMER0_IRQHandler(void)
{


	 TIMER_IntClear(TIMER0, TIMER_IF_OF);

}*/


/***********************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: TIMER1_IRQHandler
* Description  : This function is Timer1 interrupt service routine.
* Arguments    : None
* Return Value : None
***********************************************************************************************************************/
void TIMER1_IRQHandler(void)
{
	UpdateButtonRead() ;
	UpdateButtonCheck() ;
	UpdateIdleState() ;
	UpdateButtonIdle() ;
	UpdateLedStatus() ;
	USART0_RX_Update_Timeout() ;
	USART1_RX_Update_Timeout() ;
	UpadateDelayBetweenFingerEnrollment() ;
	UpadateDelayBetweenFaceEnrollment() ;
	UpadateSolenoidLockTime() ;
	//Key read Operation
	//50 ms
	if(Button.ucmButtonReadFlag EQ TRUE)
	{
		Button.ucmButtonReadFlag = FALSE ;
		ButtonDetect() ;
	}

	//Key Check Operation
	//100 ms
	if(Button.ucmButtonCheckFlag EQ TRUE)
	{
		Button.ucmButtonCheckFlag = FALSE ;
		CheckButton() ;
	}

	// Acknowledge the interrupt
	TIMER_IntClear(TIMER1, TIMER_IF_OF);
}

