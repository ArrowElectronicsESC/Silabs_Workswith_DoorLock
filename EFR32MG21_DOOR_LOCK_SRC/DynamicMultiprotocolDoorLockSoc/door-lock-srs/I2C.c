/*
 * I2C.c
 *
 *  Created on: July 15, 2020
 *      Author: prassanna.sakore
 */
#include "Global.h"
#include "I2C.h"

#ifdef KEYPAD_I2C
/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: InitI2C0
* Description  : This function initializes the I2C 0
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void InitI2C0(void)
{
	// Using default settings
	I2C_Init_TypeDef I2CInit = I2C_INIT_DEFAULT ;

	// Using PC4 (SDA) and PC5 (SCL)
	GPIO_PinModeSet(I2C0_SDA_PIN_PORT, I2C0_SDA_PIN, gpioModeWiredAndPullUpFilter, 1) ;
	GPIO_PinModeSet(I2C0_SCL_PIN_PORT, I2C0_SCL_PIN, gpioModeWiredAndPullUpFilter, 1) ;

	// Route GPIO pins to I2C module
	GPIO->I2CROUTE[0].SDAROUTE = (GPIO->I2CROUTE[0].SDAROUTE & ~_GPIO_I2C_SDAROUTE_MASK)
						| (I2C0_SDA_PIN_PORT << _GPIO_I2C_SDAROUTE_PORT_SHIFT
						| (I2C0_SDA_PIN << _GPIO_I2C_SDAROUTE_PIN_SHIFT)) ;

	GPIO->I2CROUTE[0].SCLROUTE = (GPIO->I2CROUTE[0].SCLROUTE & ~_GPIO_I2C_SCLROUTE_MASK)
						| (I2C0_SCL_PIN_PORT << _GPIO_I2C_SCLROUTE_PORT_SHIFT
						| (I2C0_SCL_PIN << _GPIO_I2C_SCLROUTE_PIN_SHIFT)) ;

	GPIO->I2CROUTE[0].ROUTEEN = GPIO_I2C_ROUTEEN_SDAPEN | GPIO_I2C_ROUTEEN_SCLPEN ;

	// Initializing the I2C
	I2C_Init(I2C0, &I2CInit);

	I2C0->CTRL = I2C_CTRL_AUTOSN;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: I2C0_Read
* Description  : This function reads data from slave
* Arguments    : uint16_t usmSlaveAddress, uint8_t ucmTargetAddress, uint8_t *ucpRxBuff, uint8_t ucmNumBytes
* Return Value : None
**************************************************************************************************************************************************************/
void I2C0_Read(uint16_t usmSlaveAddress, uint8_t ucmTargetAddress, uint8_t *ucpRxBuff, uint8_t ucmNumBytes)
{
  // Transfer structure
  I2C_TransferSeq_TypeDef I2CTransfer;
  I2C_TransferReturn_TypeDef Result;

  // Initializing I2C transfer
  I2CTransfer.addr          = usmSlaveAddress;
  I2CTransfer.flags         = I2C_FLAG_WRITE_READ; // must write target address before reading
  I2CTransfer.buf[0].data   = &ucmTargetAddress;
  I2CTransfer.buf[0].len    = 1;
  I2CTransfer.buf[1].data   = ucpRxBuff;
  I2CTransfer.buf[1].len    = ucmNumBytes;

  Result = I2C_TransferInit(I2C0, &I2CTransfer);

  // Sending data
  while (Result == i2cTransferInProgress)
  {
    Result = I2C_Transfer(I2C0);
  }

  if(Result != i2cTransferDone)
  {
	// Transmission Problem
    while(1);
  }
}
#endif
