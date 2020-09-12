/*
 * TTP229KeypadProcess.h
 *
 *  Created on: July 16, 2020
 *      Author: prassanna.sakore
 */

#ifndef TTP229KEYPADPROCESS_H_
#define TTP229KEYPADPROCESS_H_

#define NO_OF_PASSWORD_DIGITS 		4
#define NO_OF_KEYS_TO_USE 			10
#define KEY_USED_FOR_ZERO			10
#define KEY_USED_FOR_DELETE			16 // should be > 10


typedef struct
{
    uint8_t ucmEnrollmentState ;
    uint8_t ucmPasswordDigitNo ;
    uint16_t usmPassword ;
    uint16_t usmReEnterPassword ;
    uint8_t ucmKeyDeleteUserCount ;
}TagKeypadEnroll;

TagKeypadEnroll KeypadEnroll ;

typedef struct
{
	uint8_t ucmUserNo ;
    uint8_t ucmAuthenticationState ;
    uint8_t ucmPasswordDigitNo ;
    uint16_t usmPassword ;
}TagKeypadAuthenticate;

TagKeypadAuthenticate KeypadAuthenticate ;


#endif /* TTP229KEYPADPROCESS_H_ */
