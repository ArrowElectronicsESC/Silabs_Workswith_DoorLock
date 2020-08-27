/*
 * TwoWire.h
 *
 *  Created on: July 25, 2020
 *      Author: prassanna.sakore
 */

#ifndef TWO_WIRE_H_
#define TWO_WIRE_H_

#ifndef KEYPAD_I2C

typedef struct
{
	uint8_t ucmKey16;
	uint16_t usmKeys16;
	uint8_t ucmKeyPressInterrupt ;
}TagTWTTP229 ;

TagTWTTP229 TWTTP229 ;

void Keys16(void) ;
void WaitForTouch(void) ;
uint8_t	GetBit(void) ;

#endif

#endif /* TWO_WIRE_H_ */
