/*
 * UART1.h
 *
 *  Created on: July 30, 2020
 *      Author: prassanna.sakore
 */

#ifndef UART1_H_
#define UART1_H_

typedef struct
{
	uint8_t 	ucmBuffer[UART1_MAX_BUFF_LENGTH]	;
	uint16_t 	usmNoOfBytes	 					;
	uint16_t 	usmIndex 							;
	uint8_t 	ucmStatus							;
	uint16_t 	usmTimeOut							;
}TagUartTxRx1	;

TagUartTxRx1 Transmit_U1 , Receive_U1 ;

#endif /* UART1_H_ */
