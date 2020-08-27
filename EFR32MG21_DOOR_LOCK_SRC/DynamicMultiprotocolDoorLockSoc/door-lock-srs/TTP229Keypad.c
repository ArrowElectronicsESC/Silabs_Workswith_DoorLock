/*
 * TTP229Keypad.c
 *
 *  Created on: July 15, 2020
 *      Author: prassanna.sakore
 */
#include "Global.h"
#include "TTP229Keypad.h"


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: ReverseBits
* Description  : This function reverses the bits of a byte
* Arguments    : uint8_t ucmNumber
* Return Value : reversed uint8_t ucmNumber
**************************************************************************************************************************************************************/
uint8_t ReverseBits(uint8_t ucmNumber)
{
    ucmNumber = (ucmNumber & 0x55) << 1 | (ucmNumber & 0xAA) >> 1;
    ucmNumber = (ucmNumber & 0x33) << 2 | (ucmNumber & 0xCC) >> 2;
    ucmNumber = (ucmNumber & 0x0F) << 4 | (ucmNumber & 0xF0) >> 4;

    return ucmNumber;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: ReadKeys
* Description  : This function reads keys from slave device
* Arguments    : None
* Return Value : TTP229.usmKeys keys read from slave
**************************************************************************************************************************************************************/
uint16_t ReadKeys(void)
{
#ifdef KEYPAD_I2C
	uint8_t ucmReverseValue[2] ;
#endif

	TTP229.usmPrevState = TTP229.usmKeys;
	TTP229.usmKeys = 0;

#ifdef KEYPAD_I2C
    I2C0_Read(TTP229.ucmAddress , 0 , (uint8_t*)&TTP229.usmKeys , NO_OF_BYTES_TTP229) ;

    *(uint16_t*)ucmReverseValue = TTP229.usmKeys ;

    ucmReverseValue[0] = ReverseBits(ucmReverseValue[0]) ;
    ucmReverseValue[1] = ReverseBits(ucmReverseValue[1]) ;

    TTP229.usmKeys = *(uint16_t*)ucmReverseValue ;

#else

    TTP229.usmKeys = ReadKeys16() ;

#endif

    return TTP229.usmKeys;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: GetKey
* Description  : This function gets the key pressed
* Arguments    : None
* Return Value : int16_t usmKeyPressed or KEYS_NOT_PRESSED
**************************************************************************************************************************************************************/
int16_t GetKey(void)
{
    uint16_t usmKeyPressed = 1;
    uint16_t usmMask = 0 ;

    for (usmMask = 0x0001; usmMask; usmMask <<= 1)
    {
        if (usmMask & TTP229.usmKeys)
        {
            return usmKeyPressed;
        }

        ++usmKeyPressed;
    }
    return KEYS_NOT_PRESSED;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: IsKeyPressed
* Description  : This function returns whether passed key is pressed
* Arguments    : uint8_t ucmKeyNo
* Return Value : uint8_t - True if key no passed is pressed else False
**************************************************************************************************************************************************************/
uint8_t IsKeyPressed(uint8_t ucmKeyNo)
{
	return ((TTP229.usmKeys >> (ucmKeyNo - 1)) & 0x01) ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: IsKeyDown
* Description  : This function returns whether the key is down
* Arguments    : uint8_t ucmKeyNo
* Return Value : uint8_t - True if key no passed is down else False
**************************************************************************************************************************************************************/
uint8_t IsKeyDown(uint8_t ucmKeyNo)
{
	return (!((TTP229.usmPrevState >> (ucmKeyNo - 1)) & 0x01)) && ((TTP229.usmKeys >> (ucmKeyNo - 1)) & 0x01) ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: IsKeyUp
* Description  : This function returns whether the key is up
* Arguments    : uint8_t ucmKeyNo
* Return Value : uint8_t - True if key no passed is up else False
**************************************************************************************************************************************************************/
uint8_t IsKeyUp(uint8_t ucmKeyNo)
{
	return ((TTP229.usmPrevState >> (ucmKeyNo - 1)) & 0x01) && (!((TTP229.usmKeys >> (ucmKeyNo - 1)) & 0x01)) ;
}

/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: InitTTP229Variables
* Description  : This function initializes TTP229 variables
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void InitTTP229Variables(void)
{
	TTP229.ucmAddress = ADDRESS_TTP229 ;
	TTP229.usmKeys = CLEAR ;
	TTP229.usmPrevState = CLEAR ;
}

