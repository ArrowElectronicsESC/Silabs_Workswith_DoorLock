/*
 * HVC_Process_g.h
 *
 *  Created on: August 7, 2020
 *      Author: prassanna.sakore
 */

#ifndef HVC_PROCESS_G_H_
#define HVC_PROCESS_G_H_

#define ENROLL_ENTER_FACE			0
#define ENROLL_RE_ENTER_FACE		1
#define ENROLL_SUCCESS_FACE			2

#define AUTHENTICATE_ENTER_FACE		0
#define AUTHENTICATE_SUCCESS_FACE	1

#define FACE_DELETE_USER_TIMEOUT	2000

void FacePrintEnrollment(void) ;
void SetupFaceEnrollment(void) ;
uint8_t GetFaceEnrollmentState(void) ;

void FaceAuthentication(void) ;
void SetupFaceAuthentication(void) ;
int8_t GetFaceAuthenticationUserNo(void) ;
uint8_t GetFaceAuthenticationState(void) ;
void UpadateDelayBetweenFaceEnrollment(void) ;

#endif /* HVC_PROCESS_G_H_ */
