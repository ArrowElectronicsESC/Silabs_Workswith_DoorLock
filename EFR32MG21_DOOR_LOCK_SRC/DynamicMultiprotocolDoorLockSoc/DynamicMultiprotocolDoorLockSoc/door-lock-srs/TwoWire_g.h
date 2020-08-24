/*
 * TwoWire_g.h
 *
 *  Created on: July 25, 2020
 *      Author: prassanna.sakore
 */

#ifndef TWO_WIRE_G_H_
#define TWO_WIRE_G_H_

#ifndef KEYPAD_I2C


uint16_t ReadKeys16(void) ;
void EnableKeyCheck(void) ;
void InitTwoWire(void) ;
void InitTwoWireVariables(void) ;

#endif

#endif /* TWO_WIRE_G_H_ */
