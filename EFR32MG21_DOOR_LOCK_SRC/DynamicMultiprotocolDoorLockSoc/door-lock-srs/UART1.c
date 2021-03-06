/*
 * UART1.c
 *
 *  Created on: July 30, 2020
 *      Author: prassanna.sakore
 */
#include "Global.h"
#include "UART1.h"


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: InitUSART1
* Description  : This function initializes the UART 1
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void InitUSART1(void)
{
	// Default asynchronous initializer (115.2 Kbps, 8N1, no flow control)
	  USART_InitAsync_TypeDef Init = USART_INITASYNC_DEFAULT ;

	  Init.baudrate = (uint32_t)9600 ;

	  // Configure PA5 as an output (TX)
	  GPIO_PinModeSet(UART1_TX_PIN_PORT, UART1_TX_PIN, gpioModePushPull, 0);

	  // Configure PA0 as an input (RX)
	  GPIO_PinModeSet(UART1_RX_PIN_PORT, UART1_RX_PIN, gpioModeInput, 0);


	  // Route USART1 TX and RX to PA5 and PA0 pins, respectively
	  GPIO->USARTROUTE[1].TXROUTE = (UART1_TX_PIN_PORT << _GPIO_USART_TXROUTE_PORT_SHIFT)
	      | (UART1_TX_PIN << _GPIO_USART_TXROUTE_PIN_SHIFT) ;
	  GPIO->USARTROUTE[1].RXROUTE = (UART1_RX_PIN_PORT << _GPIO_USART_RXROUTE_PORT_SHIFT)
	      | (UART1_RX_PIN << _GPIO_USART_RXROUTE_PIN_SHIFT) ;

	  // Enable RX and TX signals now that they have been routed
	  GPIO->USARTROUTE[1].ROUTEEN = GPIO_USART_ROUTEEN_RXPEN | GPIO_USART_ROUTEEN_TXPEN ;

	  // Configure and enable USART1
	  USART_InitAsync(USART1, &Init) ;

	  // Enable NVIC USART 1 sources
	  NVIC_ClearPendingIRQ(USART1_RX_IRQn) ;
	  NVIC_EnableIRQ(USART1_RX_IRQn) ;
	  NVIC_ClearPendingIRQ(USART1_TX_IRQn) ;
	  NVIC_EnableIRQ(USART1_TX_IRQn) ;

}

/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: DisableUSART1
* Description  : This function disables the UART 1
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void DisableUSART1(void)
{
	  // Disable NVIC USART 1 sources
	  NVIC_ClearPendingIRQ(USART1_RX_IRQn) ;
	  NVIC_DisableIRQ(USART1_RX_IRQn) ;
	  NVIC_ClearPendingIRQ(USART1_TX_IRQn) ;
	  NVIC_DisableIRQ(USART1_TX_IRQn) ;

	  CMU_ClockEnable(cmuClock_USART1, FALSE);

}

/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: EnableUSART1
* Description  : This function enables the UART 1
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void EnableUSART1(void)
{
	  // Enable NVIC USART 1 sources
	  NVIC_ClearPendingIRQ(USART1_RX_IRQn) ;
	  NVIC_EnableIRQ(USART1_RX_IRQn) ;
	  NVIC_ClearPendingIRQ(USART1_TX_IRQn) ;
	  NVIC_EnableIRQ(USART1_TX_IRQn) ;

	  CMU_ClockEnable(cmuClock_USART1, TRUE);

}

/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART1_Change_Baudrate
* Description  : This function changes the baudrate of USART 1
* Arguments    : uint32_t ulmBaudrate
* Return Value : None
**************************************************************************************************************************************************************/
void USART1_Change_Baudrate(uint32_t ulmBaudrate)
{
	  // Disable NVIC USART 1 sources
	  NVIC_ClearPendingIRQ(USART1_RX_IRQn) ;
	  NVIC_DisableIRQ(USART1_RX_IRQn) ;
	  NVIC_ClearPendingIRQ(USART1_TX_IRQn) ;
	  NVIC_DisableIRQ(USART1_TX_IRQn) ;

	// Default asynchronous initializer (115.2 Kbps, 8N1, no flow control)
	  USART_InitAsync_TypeDef Init = USART_INITASYNC_DEFAULT ;

	  Init.baudrate = ulmBaudrate ;

	  // Configure PA5 as an output (TX)
	  GPIO_PinModeSet(UART1_TX_PIN_PORT, UART1_TX_PIN, gpioModePushPull, 0);

	  // Configure PA0 as an input (RX)
	  GPIO_PinModeSet(UART1_RX_PIN_PORT, UART1_RX_PIN, gpioModeInput, 0);


	  // Route USART1 TX and RX to PA5 and PA0 pins, respectively
	  GPIO->USARTROUTE[1].TXROUTE = (UART1_TX_PIN_PORT << _GPIO_USART_TXROUTE_PORT_SHIFT)
		      | (UART1_TX_PIN << _GPIO_USART_TXROUTE_PIN_SHIFT) ;
	  GPIO->USARTROUTE[1].RXROUTE = (UART1_RX_PIN_PORT << _GPIO_USART_RXROUTE_PORT_SHIFT)
		      | (UART1_RX_PIN << _GPIO_USART_RXROUTE_PIN_SHIFT) ;

	  // Enable RX and TX signals now that they have been routed
	  GPIO->USARTROUTE[1].ROUTEEN = GPIO_USART_ROUTEEN_RXPEN | GPIO_USART_ROUTEEN_TXPEN ;

	  // Configure and enable USART1
	  USART_InitAsync(USART1, &Init) ;

	  // Enable NVIC USART 1 sources
	  NVIC_ClearPendingIRQ(USART1_RX_IRQn) ;
	  NVIC_EnableIRQ(USART1_RX_IRQn) ;
	  NVIC_ClearPendingIRQ(USART1_TX_IRQn) ;
	  NVIC_EnableIRQ(USART1_TX_IRQn) ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART1_RX_IRQHandler
* Description  : UART 1 RX interrupt handler
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void USART1_RX_IRQHandler(void)
{
	uint8_t ucmRxByte	;

	ucmRxByte =	USART1->RXDATA;		//copy received byte in temporary Variable

	if(Receive_U1.usmNoOfBytes < UART1_MAX_BUFF_LENGTH)				//To avoid Overflow
	{
		Receive_U1.ucmBuffer[Receive_U1.usmNoOfBytes] = ucmRxByte	;	//Place the received byte into Receive data buffer
		Receive_U1.usmNoOfBytes++									;
	}

	USART_IntClear(USART1, USART_IF_RXDATAV);
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART1_TX_IRQHandler
* Description  : UART 1 TX interrupt handler
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void USART1_TX_IRQHandler(void)
{

		if(Transmit_U1.usmIndex < Transmit_U1.usmNoOfBytes)
		{
			USART1->TXDATA	= Transmit_U1.ucmBuffer[Transmit_U1.usmIndex] ;
			Transmit_U1.usmIndex ++ ;

			if(Transmit_U1.usmIndex >= Transmit_U1.usmNoOfBytes)
			{
				Transmit_U1.usmIndex = ZERO ;
				Transmit_U1.usmNoOfBytes = ZERO ;
				Transmit_U1.ucmStatus = COMPLETED ;
				USART_IntDisable(USART1, USART_IEN_TXBL) ;
				USART1_Start_RX() ;
			}
		}

		USART_IntClear(USART1, USART_IF_TXBL);

}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART1_RX_Update_Timeout
* Description  : Decrements UART 1 RX timer and sets timeout flag -  should be called in 1 ms timer
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void USART1_RX_Update_Timeout(void)
{

	if(Receive_U1.usmTimeOut > ZERO)
	{
		Receive_U1.usmTimeOut-- ;

		if(!Receive_U1.usmTimeOut)
		{
			Receive_U1.ucmStatus = COMPLETED ;
			Transmit_U1.ucmStatus = NOT_STARTED ;
			// Disable receive data valid interrupt
			USART_IntDisable(USART1, USART_IEN_RXDATAV);
		}
	}
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART1_Start_TX
* Description  : Starts UART 1 transfer
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void USART1_Start_TX(void)
{
	Transmit_U1.usmIndex		= 0 		;
	Transmit_U1.ucmStatus = IN_PROGRESS 	;
	Receive_U1.ucmStatus = NOT_STARTED 		;
	USART_IntEnable(USART1, USART_IEN_TXBL) ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART1_Start_RX
* Description  : Starts UART 1 receive
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void USART1_Start_RX(void)
{
	Receive_U1.usmNoOfBytes = ZERO ;
	Receive_U1.usmTimeOut = UART1_DEFAULT_RX_TIMEOUT_ms ;
	Receive_U1.ucmStatus = IN_PROGRESS ;
	// Enable receive data valid interrupt
    USART_IntEnable(USART1, USART_IEN_RXDATAV);

}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART1_RX_SET_TIMEOUT
* Description  : Sets timeout for UART receive
* Arguments    : uint16_t usmTimeout
* Return Value : None
**************************************************************************************************************************************************************/
void USART1_RX_SET_TIMEOUT(uint16_t usmTimeout)
{
	Receive_U1.usmTimeOut = usmTimeout ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART1_TX_ADD_SEND_DATA
* Description  : Adds data to buffer send on UART 1
* Arguments    : uint8_t* ucmBytes , uint8_t ucmNoOfBytes
* Return Value : None
**************************************************************************************************************************************************************/
void USART1_TX_ADD_SEND_DATA(uint8_t* ucmBytes , uint8_t ucmNoOfBytes)
{
	uint8_t ucmAddDataIterator;

	if((Transmit_U1.usmNoOfBytes + ucmNoOfBytes) < UART1_MAX_BUFF_LENGTH)
	{
		for(ucmAddDataIterator = ZERO ; ucmAddDataIterator < ucmNoOfBytes ; ucmAddDataIterator++ )
		{
			Transmit_U1.ucmBuffer[Transmit_U1.usmNoOfBytes] = ucmBytes[ucmAddDataIterator] ;

			Transmit_U1.usmNoOfBytes++ ;
		}
	}

}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART1_RX_GET_DATA
* Description  : Get data from RX buffer
* Arguments    : uint8_t* ucmContainer , uint8_t ucmRXBufferLocation , uint8_t ucmNoOfBytes
* Return Value : None
**************************************************************************************************************************************************************/
void USART1_RX_GET_DATA(uint8_t* ucmContainer , uint8_t ucmRXBufferLocation , uint8_t ucmNoOfBytes)
{
	uint8_t ucmAddDataIterator;

	if((ucmRXBufferLocation + ucmNoOfBytes) < UART1_MAX_BUFF_LENGTH)
	{
		for(ucmAddDataIterator = ZERO ; ucmAddDataIterator < ucmNoOfBytes ; ucmAddDataIterator++ )
		{
			ucmContainer[ucmAddDataIterator] = Receive_U1.ucmBuffer[ucmRXBufferLocation + ucmAddDataIterator] ;

		}
	}

}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART1_RX_GET_BUFFER
* Description  : Returns address of RX buffer
* Arguments    : None
* Return Value : Receive_U1.ucmBuffer
**************************************************************************************************************************************************************/
uint8_t* USART1_RX_GET_BUFFER(void)
{
	return Receive_U1.ucmBuffer ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART1_RX_GET_NO_OF_BYTES
* Description  : Returns no of bytes received
* Arguments    : None
* Return Value : Receive_U1.usmNoOfBytes
**************************************************************************************************************************************************************/
uint16_t USART1_RX_GET_NO_OF_BYTES(void)
{
	return Receive_U1.usmNoOfBytes ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART1_Get_Transmit_Status
* Description  : Return transmit status
* Arguments    : None
* Return Value : Transmit_U1.ucmStatus
**************************************************************************************************************************************************************/
uint8_t USART1_Get_Transmit_Status(void)
{
	return Transmit_U1.ucmStatus ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART1_Get_Receive_Status
* Description  : Return receive status
* Arguments    : None
* Return Value : Receive_U1.ucmStatus
**************************************************************************************************************************************************************/
uint8_t USART1_Get_Receive_Status(void)
{
	return Receive_U1.ucmStatus ;
}
