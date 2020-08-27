/*
 * I2C_g.h
 *
 *  Created on: July 15, 2020
 *      Author: prassanna.sakore
 */

#ifndef I2C_G_H_
#define I2C_G_H_

#ifdef KEYPAD_I2C
void InitI2C0(void) ;
void I2C0_Read(uint16_t usmSlaveAddress, uint8_t ucmTargetAddress, uint8_t *ucpRxBuff, uint8_t ucmNumBytes) ;
#endif

#endif /* I2C_G_H_ */
