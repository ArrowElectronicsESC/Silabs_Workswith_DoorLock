/*
 * Button_g.h
 *
 *  Created on: July 10, 2020
 *      Author: prassanna.sakore
 */

#ifndef BUTTON_G_H_
#define BUTTON_G_H_



/*
MODE	   STEP	    	Button COMBINATIONS

 -			*		//1		STEP_BUTTON
 *			-		//2		MODE_BUTTON
 *			*		//3		MODE_STEP_BUTTON

*/

//Final processing button code for Pressed Button Single/combinations button press
//			Button Name			Button Code					  	//Final processing button code
#define		STEP_BUTTON 		(uint16_t)(1)					// 0000 0000 0000 0001b
#define		MODE_BUTTON			(uint16_t)(STEP_BUTTON << 1)	// 0000 0000 0000 0010b
#define		MODE_STEP_BUTTON	(uint16_t)(MODE_BUTTON << 1)	// 0000 0000 0000 0100b


#define 	BUTTON_READ_TIME_mS		    	50          //Button Read Interval
#define 	BUTTON_CHECK_TIME_mS			100         //Button Check Interval

#define     BUTTON_SHORT_PRESSED          	0
#define     BUTTON_LONG_PRESSED      		0x80        //To set button data "D7" bit as Continuous Press Detected

#define		BUTTON_PRESS_TIMEOUT_SEC		30000

//Button Press Type
#define     SHORT_PRESS                	0
#define     LONG_PRESS            		1

#define 	ONE_TIME_LONG_PRESS_TIME_Sec	3


typedef struct
{
	uint8_t 	ucmProcessed				;
	uint8_t 	ucmButtonPressedType        ;
	uint16_t 	usmFinalProcessingButton	;
	uint16_t 	usmButtonPressTimeout		;
	uint8_t 	ucmButtonPressedTime		;
	uint8_t		ucmOneTimeButtonLongPress	;
	uint8_t		ucmButtonReadFlag			;
	uint8_t 	ucmButtonCheckFlag			;
}TagButton ;

extern TagButton Button ;

void ButtonDetect(void);
void UpdateButtonRead(void) ;
void UpdateButtonCheck(void) ;
void UpdateButtonIdle(void) ;
void InitButtonVariables(void) ;

#endif /* BUTTON_G_H_ */
