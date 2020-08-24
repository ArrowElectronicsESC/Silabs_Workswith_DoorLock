/*
 * UART0.h
 *
 *  Created on: July 17, 2020
 *      Author: prassanna.sakore
 */

#ifndef UART0_H_
#define UART0_H_

typedef struct
{
	uint8_t 	ucmBuffer[UART0_MAX_BUFF_LENGTH]	;
	uint16_t 	usmNoOfBytes	 					;
	uint16_t 	usmIndex 							;
	uint8_t 	ucmStatus							;
	uint16_t 	usmTimeOut							;
}TagUartTxRx0	;

TagUartTxRx0 Transmit_U0 , Receive_U0 ;

#endif /* UART0_H_ */
