/*
 * HVC_Process.h
 *
 *  Created on: August 7, 2020
 *      Author: prassanna.sakore
 */

#ifndef HVC_PROCESS_H_
#define HVC_PROCESS_H_

#define TIMEOUT_WRITE_ALBUM			5000
#define TIMEOUT_ENROLL_FACE 		2000
#define TIMEOUT_AUTHENTICATE_FACE 	2000


#define SCAN_TIME_BETWEEN_TWO_FACES 2000

#define MIN_FACE_MATCH_SCORE 500

typedef struct
{
    uint8_t ucmEnrollmentState ;
    uint16_t usmDelayBetweenFaceEnrollments ;

}TagFaceEnroll ;

TagFaceEnroll FaceEnroll ;

typedef struct
{
	uint8_t ucmUserNo ;
    uint8_t ucmAuthenticationState ;

}TagFaceAuthenticate ;

TagFaceAuthenticate FaceAuthenticate ;


#endif /* HVC_PROCESS_H_ */
