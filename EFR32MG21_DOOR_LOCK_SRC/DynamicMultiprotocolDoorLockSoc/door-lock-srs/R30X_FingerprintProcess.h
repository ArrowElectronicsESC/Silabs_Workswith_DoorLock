/*
 * R30X_FingerprintProcess.h
 *
 *  Created on: July 24, 2020
 *      Author: prassanna.sakore
 */

#ifndef R30X_FINGERPRINTPROCESS_H_
#define R30X_FINGERPRINTPROCESS_H_


#define SCAN_TIME_BETWEEN_TWO_FINGERS 2000

#define MIN_FINGER_MATCH_SCORE 50

typedef struct
{
    uint8_t ucmEnrollmentState ;
    uint16_t usmDelayBetweenFingerEnrollments ;

}TagFingerEnroll ;

TagFingerEnroll FingerEnroll ;

typedef struct
{
	uint8_t ucmUserNo ;
    uint8_t ucmAuthenticationState ;

}TagFingerAuthenticate ;

TagFingerAuthenticate FingerAuthenticate ;


#endif /* R30X_FINGERPRINTPROCESS_H_ */
