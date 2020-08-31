/*
 * UART1_g.h
 *
 *  Created on: July 30, 2020
 *      Author: prassanna.sakore
 */

#ifndef UART1_G_H_
#define UART1_G_H_

#define UART1_MAX_BUFF_LENGTH  	300

#define UART1_DEFAULT_RX_TIMEOUT_ms  	2000

void InitUSART1(void) ;
void USART1_RX_Update_Timeout(void) ;
void USART1_Start_TX(void) ;
void USART1_RX_GET_DATA(uint8_t* ucmContainer , uint8_t ucmRXBufferLocation , uint8_t ucmNoOfBytes) ;
void USART1_TX_ADD_SEND_DATA(uint8_t* ucmBytes , uint8_t ucmNoOfBytes) ;
uint8_t* USART1_RX_GET_BUFFER(void) ;
uint16_t USART1_RX_GET_NO_OF_BYTES(void) ;
void USART1_RX_SET_TIMEOUT(uint16_t usmTimeout) ;
void USART1_Change_Baudrate(uint32_t ulmBaudrate) ;
void USART1_Start_RX(void) ;
uint8_t USART1_Get_Transmit_Status(void) ;
uint8_t USART1_Get_Receive_Status(void) ;

#endif /* UART1_G_H_ */
