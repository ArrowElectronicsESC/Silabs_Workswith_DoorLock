/*
 * TTP229Keypad_g.h
 *
 *  Created on: July 15, 2020
 *      Author: prassanna.sakore
 */

#ifndef TTP229KEYPAD_G_H_
#define TTP229KEYPAD_G_H_

#define KEYS_NOT_PRESSED ((int16_t)(-1))

uint16_t ReadKeys(void) ;
int16_t GetKey(void) ;
uint8_t IsKeyPressed(uint8_t ucmKeyNo) ;
uint8_t IsKeyDown(uint8_t ucmKeyNo) ;
uint8_t IsKeyUp(uint8_t ucmKeyNo) ;
void InitTTP229Variables(void) ;

#endif /* TTP229KEYPAD_G_H_ */
