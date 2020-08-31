/*
 * UART0.c
 *
 *  Created on: July 17, 2020
 *      Author: prassanna.sakore
 */
#include "Global.h"
#include "UART0.h"


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: InitUSART0
* Description  : This function initializes the UART 0
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void InitUSART0(void)
{
	// Default asynchronous initializer (115.2 Kbps, 8N1, no flow control)
	  USART_InitAsync_TypeDef Init = USART_INITASYNC_DEFAULT ;

	  Init.baudrate = FPS_DEFAULT_BAUDRATE ;

	  // Configure PC0 as an output (TX)
	  GPIO_PinModeSet(UART0_TX_PIN_PORT, UART0_TX_PIN, gpioModePushPull, 0);

	  // Configure PC1 as an input (RX)
	  GPIO_PinModeSet(UART0_RX_PIN_PORT, UART0_RX_PIN, gpioModeInput, 0);


	  // Route USART0 TX and RX to PC0 and PC1 pins, respectively
	  GPIO->USARTROUTE[0].TXROUTE = (UART0_TX_PIN_PORT << _GPIO_USART_TXROUTE_PORT_SHIFT)
	      | (UART0_TX_PIN << _GPIO_USART_TXROUTE_PIN_SHIFT) ;
	  GPIO->USARTROUTE[0].RXROUTE = (UART0_RX_PIN_PORT << _GPIO_USART_RXROUTE_PORT_SHIFT)
	      | (UART0_RX_PIN << _GPIO_USART_RXROUTE_PIN_SHIFT) ;

	  // Enable RX and TX signals now that they have been routed
	  GPIO->USARTROUTE[0].ROUTEEN = GPIO_USART_ROUTEEN_RXPEN | GPIO_USART_ROUTEEN_TXPEN ;

	  // Configure and enable USART0
	  USART_InitAsync(USART0, &Init) ;

	  // Enable NVIC USART 0 sources
	  NVIC_ClearPendingIRQ(USART0_RX_IRQn) ;
	  NVIC_EnableIRQ(USART0_RX_IRQn) ;
	  NVIC_ClearPendingIRQ(USART0_TX_IRQn) ;
	  NVIC_EnableIRQ(USART0_TX_IRQn) ;

}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART0_Change_Baudrate
* Description  : This function changes the baudrate of USART 0
* Arguments    : uint32_t ulmBaudrate
* Return Value : None
**************************************************************************************************************************************************************/
void USART0_Change_Baudrate(uint32_t ulmBaudrate)
{
	  // Disable NVIC USART 0 sources
	  NVIC_ClearPendingIRQ(USART0_RX_IRQn) ;
	  NVIC_DisableIRQ(USART0_RX_IRQn) ;
	  NVIC_ClearPendingIRQ(USART0_TX_IRQn) ;
	  NVIC_DisableIRQ(USART0_TX_IRQn) ;

	// Default asynchronous initializer (115.2 Kbps, 8N1, no flow control)
	  USART_InitAsync_TypeDef Init = USART_INITASYNC_DEFAULT ;

	  Init.baudrate = ulmBaudrate ;

	  // Configure PC0 as an output (TX)
	  GPIO_PinModeSet(UART0_TX_PIN_PORT, UART0_TX_PIN, gpioModePushPull, 0);

	  // Configure PC1 as an input (RX)
	  GPIO_PinModeSet(UART0_RX_PIN_PORT, UART0_RX_PIN, gpioModeInput, 0);


	  // Route USART0 TX and RX to PC0 and PC1 pins, respectively
	  GPIO->USARTROUTE[0].TXROUTE = (UART0_TX_PIN_PORT << _GPIO_USART_TXROUTE_PORT_SHIFT)
		      | (UART0_TX_PIN << _GPIO_USART_TXROUTE_PIN_SHIFT) ;
	  GPIO->USARTROUTE[0].RXROUTE = (UART0_RX_PIN_PORT << _GPIO_USART_RXROUTE_PORT_SHIFT)
		      | (UART0_RX_PIN << _GPIO_USART_RXROUTE_PIN_SHIFT) ;

	  // Enable RX and TX signals now that they have been routed
	  GPIO->USARTROUTE[0].ROUTEEN = GPIO_USART_ROUTEEN_RXPEN | GPIO_USART_ROUTEEN_TXPEN ;

	  // Configure and enable USART0
	  USART_InitAsync(USART0, &Init) ;

	  // Enable NVIC USART 0 sources
	  NVIC_ClearPendingIRQ(USART0_RX_IRQn) ;
	  NVIC_EnableIRQ(USART0_RX_IRQn) ;
	  NVIC_ClearPendingIRQ(USART0_TX_IRQn) ;
	  NVIC_EnableIRQ(USART0_TX_IRQn) ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART0_RX_IRQHandler
* Description  : UART 0 RX interrupt handler
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void USART0_RX_IRQHandler(void)
{

	uint8_t ucmRxByte	;

	ucmRxByte =	USART0->RXDATA;		//copy received byte in temporary Variable

	if(Receive_U0.usmNoOfBytes < UART0_MAX_BUFF_LENGTH)				//To avoid Overflow
	{
		Receive_U0.ucmBuffer[Receive_U0.usmNoOfBytes] = ucmRxByte	;	//Place the received byte into Receive data buffer
		Receive_U0.usmNoOfBytes++									;
	}

	USART_IntClear(USART0, USART_IF_RXDATAV);
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART0_TX_IRQHandler
* Description  : UART 0 TX interrupt handler
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void USART0_TX_IRQHandler(void)
{

		if(Transmit_U0.usmIndex < Transmit_U0.usmNoOfBytes)
		{
			USART0->TXDATA	= Transmit_U0.ucmBuffer[Transmit_U0.usmIndex] ;
			Transmit_U0.usmIndex ++ ;

			if(Transmit_U0.usmIndex >= Transmit_U0.usmNoOfBytes)
			{
				Transmit_U0.usmIndex = ZERO ;
				Transmit_U0.usmNoOfBytes = ZERO ;
				Transmit_U0.ucmStatus = COMPLETED ;
				USART_IntDisable(USART0, USART_IEN_TXBL) ;
				USART0_Start_RX() ;
			}
		}

		USART_IntClear(USART0, USART_IF_TXBL);

}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART0_RX_Update_Timeout
* Description  : Decrements UART 0 RX timer and sets timeout flag -  should be called in 1 ms timer
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void USART0_RX_Update_Timeout(void)
{

	if(Receive_U0.usmTimeOut > ZERO)
	{
		Receive_U0.usmTimeOut-- ;

		if(!Receive_U0.usmTimeOut)
		{
			Receive_U0.ucmStatus = COMPLETED ;
			Transmit_U0.ucmStatus = NOT_STARTED ;
			// Disable receive data valid interrupt
			USART_IntDisable(USART0, USART_IEN_RXDATAV);
		}
	}
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART0_Start_TX
* Description  : Starts UART 0 transfer
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void USART0_Start_TX(void)
{
	Transmit_U0.usmIndex		= 0 		;
	Transmit_U0.ucmStatus = IN_PROGRESS 	;
	Receive_U0.ucmStatus = NOT_STARTED 		;
	USART_IntEnable(USART0, USART_IEN_TXBL) ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART0_Start_RX
* Description  : Starts UART 0 receive
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void USART0_Start_RX(void)
{
	Receive_U0.usmNoOfBytes = ZERO ;
	Receive_U0.usmTimeOut = UART0_DEFAULT_RX_TIMEOUT_ms ;
	Receive_U0.ucmStatus = IN_PROGRESS ;
	// Enable receive data valid interrupt
    USART_IntEnable(USART0, USART_IEN_RXDATAV);

}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART0_RX_SET_TIMEOUT
* Description  : Sets timeout for UART receive
* Arguments    : uint16_t usmTimeout
* Return Value : None
**************************************************************************************************************************************************************/
void USART0_RX_SET_TIMEOUT(uint16_t usmTimeout)
{
	Receive_U0.usmTimeOut = usmTimeout ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART0_TX_ADD_SEND_DATA
* Description  : Adds data to buffer send on UART 0
* Arguments    : uint8_t* ucmBytes , uint8_t ucmNoOfBytes
* Return Value : None
**************************************************************************************************************************************************************/
void USART0_TX_ADD_SEND_DATA(uint8_t* ucmBytes , uint8_t ucmNoOfBytes)
{
	uint8_t ucmAddDataIterator;

	if((Transmit_U0.usmNoOfBytes + ucmNoOfBytes) < UART0_MAX_BUFF_LENGTH)
	{
		for(ucmAddDataIterator = ZERO ; ucmAddDataIterator < ucmNoOfBytes ; ucmAddDataIterator++ )
		{
			Transmit_U0.ucmBuffer[Transmit_U0.usmNoOfBytes] = ucmBytes[ucmAddDataIterator] ;

			Transmit_U0.usmNoOfBytes++ ;
		}
	}

}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART0_RX_GET_DATA
* Description  : Get data from RX buffer
* Arguments    : uint8_t* ucmContainer , uint8_t ucmRXBufferLocation , uint8_t ucmNoOfBytes
* Return Value : None
**************************************************************************************************************************************************************/
void USART0_RX_GET_DATA(uint8_t* ucmContainer , uint8_t ucmRXBufferLocation , uint8_t ucmNoOfBytes)
{
	uint8_t ucmAddDataIterator;

	if((ucmRXBufferLocation + ucmNoOfBytes) < UART0_MAX_BUFF_LENGTH)
	{
		for(ucmAddDataIterator = ZERO ; ucmAddDataIterator < ucmNoOfBytes ; ucmAddDataIterator++ )
		{
			ucmContainer[ucmAddDataIterator] = Receive_U0.ucmBuffer[ucmRXBufferLocation + ucmAddDataIterator] ;

		}
	}

}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART0_RX_GET_BUFFER
* Description  : Returns address of RX buffer
* Arguments    : None
* Return Value : Receive_U0.ucmBuffer
**************************************************************************************************************************************************************/
uint8_t* USART0_RX_GET_BUFFER(void)
{
	return Receive_U0.ucmBuffer ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART0_RX_GET_NO_OF_BYTES
* Description  : Returns no of bytes received
* Arguments    : None
* Return Value : Receive_U0.usmNoOfBytes
**************************************************************************************************************************************************************/
uint16_t USART0_RX_GET_NO_OF_BYTES(void)
{
	return Receive_U0.usmNoOfBytes ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART0_Get_Transmit_Status
* Description  : Return transmit status
* Arguments    : None
* Return Value : Transmit_U0.ucmStatus
**************************************************************************************************************************************************************/
uint8_t USART0_Get_Transmit_Status(void)
{
	return Transmit_U0.ucmStatus ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: USART0_Get_Receive_Status
* Description  : Return receive status
* Arguments    : None
* Return Value : Receive_U0.ucmStatus
**************************************************************************************************************************************************************/
uint8_t USART0_Get_Receive_Status(void)
{
	return Receive_U0.ucmStatus ;
}
