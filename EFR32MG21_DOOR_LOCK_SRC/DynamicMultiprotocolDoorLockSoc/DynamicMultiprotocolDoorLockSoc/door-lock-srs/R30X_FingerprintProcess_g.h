/*
 * R30X_FingerprintProcess_g.h
 *
 *  Created on: July 24, 2020
 *      Author: prassanna.sakore
 */

#ifndef R30X_FINGERPRINTPROCESS_G_H_
#define R30X_FINGERPRINTPROCESS_G_H_

#define ENROLL_ENTER_FINGER			0
#define ENROLL_RE_ENTER_FINGER		1
#define ENROLL_SUCCESS_FINGER		2

#define AUTHENTICATE_ENTER_FINGER	0
#define AUTHENTICATE_SUCCESS_FINGER	1

void FingerPrintEnrollment(void) ;
void SetupFingerEnrollment(void) ;
uint8_t GetFingerEnrollmentState(void) ;

void FingerAuthentication(void) ;
void SetupFingerAuthentication(void) ;
int8_t GetFingerAuthenticationUserNo(void) ;
uint8_t GetFingerAuthenticationState(void) ;
void UpadateDelayBetweenFingerEnrollment(void) ;

#endif /* R30X_FINGERPRINTPROCESS_G_H_ */
