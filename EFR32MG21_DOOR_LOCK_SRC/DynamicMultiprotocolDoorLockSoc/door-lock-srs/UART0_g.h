/*
 * UART0_g.h
 *
 *  Created on: July 17, 2020
 *      Author: prassanna.sakore
 */

#ifndef UART0_G_H_
#define UART0_G_H_

#define UART0_MAX_BUFF_LENGTH  	300

#define UART0_DEFAULT_RX_TIMEOUT_ms  	2000

void InitUSART0(void) ;
void USART0_RX_Update_Timeout(void) ;
void USART0_Start_TX(void) ;
void USART0_RX_GET_DATA(uint8_t* ucmContainer , uint8_t ucmRXBufferLocation , uint8_t ucmNoOfBytes) ;
void USART0_TX_ADD_SEND_DATA(uint8_t* ucmBytes , uint8_t ucmNoOfBytes) ;
uint8_t* USART0_RX_GET_BUFFER(void) ;
uint16_t USART0_RX_GET_NO_OF_BYTES(void) ;
void USART0_RX_SET_TIMEOUT(uint16_t usmTimeout) ;
void USART0_Change_Baudrate(uint32_t ulmBaudrate) ;
void USART0_Start_RX(void) ;
uint8_t USART0_Get_Transmit_Status(void) ;
uint8_t USART0_Get_Receive_Status(void) ;

#endif /* UART0_G_H_ */
