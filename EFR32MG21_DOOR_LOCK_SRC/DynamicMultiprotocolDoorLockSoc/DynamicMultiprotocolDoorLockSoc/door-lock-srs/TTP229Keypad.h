/*
 * TTP229Keypad.h
 *
 *  Created on: July 15, 2020
 *      Author: prassanna.sakore
 */

#ifndef TTP229KEYPAD_H_
#define TTP229KEYPAD_H_

#define ADDRESS_TTP229 		0xA7
#define NO_OF_BYTES_TTP229 	2

typedef struct
{
    uint8_t  ucmAddress ;
    uint16_t usmKeys ;
    uint16_t usmPrevState ;

}TagTTP229;

TagTTP229 TTP229 ;

uint8_t ReverseBits(uint8_t ucmNumber) ;

#endif /* TTP229KEYPAD_H_ */
