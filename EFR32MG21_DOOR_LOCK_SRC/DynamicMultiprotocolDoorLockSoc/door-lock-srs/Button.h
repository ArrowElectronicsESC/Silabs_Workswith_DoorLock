/*
 * Button.h
 *
 *  Created on: July 10, 2020
 *      Author: prassanna.sakore
 */

#ifndef BUTTON_H_
#define BUTTON_H_


#define     TIME_BETWEEN_SHORT_PRESS_N_LONG_PRESS_ms             			500         //Gap time between short Press and long Press
#define     TIME_BETWEEN_SHORT_PRESS_N_LONG_PRESS_TIME_COUNT     			(uint8_t)(TIME_BETWEEN_SHORT_PRESS_N_LONG_PRESS_ms/BUTTON_READ_TIME_mS)
#define     LONG_PRESS_CHECK_EVENT_TIME_ms                            		200         //Time between two long press
#define     LONG_PRESS_CHECK_EVENT_TIME_COUNT                         		(uint8_t)(LONG_PRESS_CHECK_EVENT_TIME_ms/BUTTON_READ_TIME_mS)

#define		BUTTON_RELEASE_CONFIRM_COUNT				2

#define		NO_OF_BUTTONS								2


//Button Pins with respected Pack Position in Byte					D7		D6		D5		D4		D3		D2		D1		D0
//			Button Name											Respected Pack Position in Byte
//#define 	BUTTON_MODE		(uint8_t)(MODE_BUTTON_PIN  << 1)	//	-		-		-		-		-		-		MODE	-
//#define 	BUTTON_STEP		(uint8_t)(STEP_BUTTON_PIN  << 0)	//	-		-		-		-		-		-		-		STEP

void CalculateButtonForProcess(uint8_t ucvButtonPressed , uint8_t ucvButtonPressedTime_Sec , uint8_t ucvOneTimeButtonLongPress) ;

#endif /* BUTTON_H_ */
