/*
 * TwoWire.c
 *
 *  Created on: July 25, 2020
 *      Author: prassanna.sakore
 */
#include "Global.h"
#include "TwoWire.h"

#ifndef KEYPAD_I2C


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: InitTwoWire
* Description  : This function initializes the I2C 0
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void InitTwoWire(void)
{
	// Using PC4 (SDA) and PC5 (SCL)
	GPIO_PinModeSet(I2C0_SDA_PIN_PORT, I2C0_SDA_PIN, gpioModeInputPullFilter , 1) ;
	GPIO_PinModeSet(I2C0_SCL_PIN_PORT, I2C0_SCL_PIN, gpioModePushPull , 0) ;

	GPIO_IntConfig(I2C0_SDA_PIN_PORT , I2C0_SDA_PIN , FALSE , TRUE , TRUE )  ;

	/* Enable EVEN interrupt */
	NVIC_ClearPendingIRQ(GPIO_EVEN_IRQn);
	NVIC_EnableIRQ(GPIO_EVEN_IRQn);

	GPIO_PinOutSet(I2C0_SCL_PIN_PORT, I2C0_SCL_PIN) ;

}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: ReadKeys16
* Description  : This function return keys read
* Arguments    : None
* Return Value : TWTTP229.usmKeys16
**************************************************************************************************************************************************************/
uint16_t ReadKeys16(void)
{
	  /* Enable EVEN interrupt */
	  NVIC_EnableIRQ(GPIO_EVEN_IRQn);

	if(TWTTP229.ucmKeyPressInterrupt EQ TRUE)
	{
		TWTTP229.ucmKeyPressInterrupt = FALSE ;
	}
	else
	{
		TWTTP229.usmKeys16 = 0 ;
	}

	return TWTTP229.usmKeys16 ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: Keys16
* Description  : This function gets the bits of keys pressed using bit banging for two wire
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void Keys16(void)
{
	TWTTP229.usmKeys16 = 0 ;

	for (uint8_t i = 0 ; i < 16 ; i++)
	{
		if (GetBit())
		{
			TWTTP229.usmKeys16 |= 1 << i ;
		}
	}

	DelayMicroseconds(2000) ; // Tout
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: GetBit
* Description  : This function gets bit of key pressed by generating SCL and reading SDA
* Arguments    : None
* Return Value : ucmRetVal
**************************************************************************************************************************************************************/
uint8_t GetBit(void)
{
	uint8_t ucmRetVal = 0 ;

	GPIO_PinOutClear(I2C0_SCL_PIN_PORT, I2C0_SCL_PIN) ;
	DelayMicroseconds(10) ;

	ucmRetVal = !GPIO_PinInGet(I2C0_SDA_PIN_PORT, I2C0_SDA_PIN) ;

	GPIO_PinOutSet(I2C0_SCL_PIN_PORT, I2C0_SCL_PIN) ;
	DelayMicroseconds(10) ;

	return ucmRetVal;
}

/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: InitTwoWireVariables
* Description  : This function initializes two wire vaiables
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void InitTwoWireVariables(void)
{
	TWTTP229.ucmKey16 = CLEAR ;
	TWTTP229.usmKeys16 = CLEAR ;
	TWTTP229.ucmKeyPressInterrupt = FALSE ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: WaitForTouch
* Description  : This function waits while SDA signal of key press is not received
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void WaitForTouch(void)
{
	while (GPIO_PinInGet(I2C0_SDA_PIN_PORT, I2C0_SDA_PIN)) ; // DV LOW
	while (!GPIO_PinInGet(I2C0_SDA_PIN_PORT, I2C0_SDA_PIN)) ; // DV HIGH
	DelayMicroseconds(20) ; // Tw
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: GPIO_EVEN_IRQHandler
* Description  : This function is ISR for SDA signal / Even interrupts
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void GPIO_EVEN_IRQHandler(void)
{

 /* Get and clear all pending GPIO interrupts */
  uint32_t interruptMask = GPIO_IntGet();
  GPIO_IntClear(interruptMask);

  /* Check if button 0 was pressed */
  if (interruptMask & (1 << I2C0_SDA_PIN))
  {

	  NVIC_DisableIRQ(GPIO_EVEN_IRQn);

	  TWTTP229.ucmKeyPressInterrupt = TRUE ;

	  GPIO_IntConfig(I2C0_SDA_PIN_PORT , I2C0_SDA_PIN , FALSE , FALSE , FALSE )  ;
	  Keys16() ;
	  GPIO_IntConfig(I2C0_SDA_PIN_PORT , I2C0_SDA_PIN , FALSE , TRUE , TRUE )  ;


  }
}


#endif
