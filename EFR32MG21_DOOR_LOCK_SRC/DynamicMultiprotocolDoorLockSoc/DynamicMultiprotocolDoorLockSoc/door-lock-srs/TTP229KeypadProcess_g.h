/*
 * TTP229KeypadProcess_g.h
 *
 *  Created on: July 16, 2020
 *      Author: prassanna.sakore
 */

#ifndef TTP229KEYPADPROCESS_G_H_
#define TTP229KEYPADPROCESS_G_H_

#define ENROLL_ENTER_PASSWORD		0
#define ENROLL_RE_ENTER_PASSWORD	1
#define ENROLL_MATCH_PASSWORD		2
#define ENROLL_SUCCESS_PASSWORD		3

#define AUTHENTICATE_ENTER_PASSWORD		0
#define AUTHENTICATE_MATCH_PASSWORD		1
#define AUTHENTICATE_SUCCESS_PASSWORD	2

void KeypadEnrollment(void) ;
void SetupKeypadEnrollment(void) ;
int16_t GetEnrollmentPassword(void) ;
uint8_t GetEnrollmentState(void) ;

void KeypadAuthentication(void) ;
void SetupKeypadAuthentication(void) ;
int8_t GetAuthenticationUserNo(void) ;
uint8_t GetAuthenticationState(void) ;

#endif /* TTP229KEYPADPROCESS_G_H_ */
