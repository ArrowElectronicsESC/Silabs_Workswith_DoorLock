/*
 * HVC.h
 *
 *  Created on: July 30, 2020
 *      Author: prassanna.sakore
 */

#ifndef HVC_H_
#define HVC_H_

/*----------------------------------------------------------------------------*/
/* Command number                                                             */
/*----------------------------------------------------------------------------*/
#define HVC_COM_GET_VERSION             (uint8_t)0x00
#define HVC_COM_SET_CAMERA_ANGLE        (uint8_t)0x01
#define HVC_COM_GET_CAMERA_ANGLE        (uint8_t)0x02
#define HVC_COM_EXECUTE                 (uint8_t)0x03
#define HVC_COM_EXECUTEEX               (uint8_t)0x04
#define HVC_COM_SET_THRESHOLD           (uint8_t)0x05
#define HVC_COM_GET_THRESHOLD           (uint8_t)0x06
#define HVC_COM_SET_SIZE_RANGE          (uint8_t)0x07
#define HVC_COM_GET_SIZE_RANGE          (uint8_t)0x08
#define HVC_COM_SET_DETECTION_ANGLE     (uint8_t)0x09
#define HVC_COM_GET_DETECTION_ANGLE     (uint8_t)0x0A
#define HVC_COM_SET_BAUDRATE            (uint8_t)0x0E
#define HVC_COM_REGISTRATION            (uint8_t)0x10
#define HVC_COM_DELETE_DATA             (uint8_t)0x11
#define HVC_COM_DELETE_USER             (uint8_t)0x12
#define HVC_COM_DELETE_ALL              (uint8_t)0x13
#define HVC_COM_GET_PERSON_DATA         (uint8_t)0x15
#define HVC_COM_SAVE_ALBUM              (uint8_t)0x20
#define HVC_COM_LOAD_ALBUM              (uint8_t)0x21
#define HVC_COM_WRITE_ALBUM             (uint8_t)0x22

/* Image info of Execute command */
#define HVC_EXECUTE_IMAGE_NONE          0x00000000
#define HVC_EXECUTE_IMAGE_QVGA          0x00000001
#define HVC_EXECUTE_IMAGE_QVGA_HALF     0x00000002

/*----------------------------------------------------------------------------*/
/* Error code */

/* Parameter error */
#define HVC_ERROR_PARAMETER             -1

/* Send signal timeout error */
#define HVC_ERROR_SEND_DATA             -10

/* Receive header signal timeout error */
#define HVC_ERROR_HEADER_TIMEOUT        -20

/* Invalid header error */
#define HVC_ERROR_HEADER_INVALID        -21

/* Receive data signal timeout error */
#define HVC_ERROR_DATA_TIMEOUT          -22


/*----------------------------------------------------------------------------*/
/* Album data size */
#define HVC_ALBUM_SIZE_MIN              32
#define HVC_ALBUM_SIZE_MAX              816032


#endif /* HVC_H_ */
