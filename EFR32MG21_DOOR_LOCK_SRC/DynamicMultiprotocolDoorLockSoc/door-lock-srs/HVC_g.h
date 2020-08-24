/*
 * HVC_g.h
 *
 *  Created on: July 30, 2020
 *      Author: prassanna.sakore
 */

#ifndef HVC_G_H_
#define HVC_G_H_

/*----------------------------------------------------------------------------*/
/* Execution flag */
#define HVC_ACTIV_BODY_DETECTION        0x00000001
#define HVC_ACTIV_HAND_DETECTION        0x00000002
#define HVC_ACTIV_FACE_DETECTION        0x00000004
#define HVC_ACTIV_FACE_DIRECTION        0x00000008
#define HVC_ACTIV_AGE_ESTIMATION        0x00000010
#define HVC_ACTIV_GENDER_ESTIMATION     0x00000020
#define HVC_ACTIV_GAZE_ESTIMATION       0x00000040
#define HVC_ACTIV_BLINK_ESTIMATION      0x00000080
#define HVC_ACTIV_EXPRESSION_ESTIMATION 0x00000100
#define HVC_ACTIV_FACE_RECOGNITION      0x00000200

#define HVC_DEFAULT_BAUDRATE	9600

/*----------------------------------------------------------------------------*/
/* Expression */
typedef enum
{
    EX_NEUTRAL = 1,
    EX_HAPPINESS,
    EX_SURPRISE,
    EX_ANGER,
    EX_SADNESS
}EXPRESSION ;

/*----------------------------------------------------------------------------*/
/* Header for send signal data */
typedef enum
{
    SEND_HEAD_SYNCBYTE = 0,
    SEND_HEAD_COMMANDNO,
    SEND_HEAD_DATALENGTHLSB,
    SEND_HEAD_DATALENGTHMSB,
    SEND_HEAD_NUM
}SEND_HEADER ;

/*----------------------------------------------------------------------------*/
/* Header for receive signal data */
typedef enum
{
    RECEIVE_HEAD_SYNCBYTE = 0,
    RECEIVE_HEAD_STATUS,
    RECEIVE_HEAD_DATALENLL,
    RECEIVE_HEAD_DATALENLM,
    RECEIVE_HEAD_DATALENML,
    RECEIVE_HEAD_DATALENMM,
    RECEIVE_HEAD_NUM
}RECEIVE_HEADER ;

/*----------------------------------------------------------------------------*/
/* Struct                                                                     */
/*----------------------------------------------------------------------------*/
/*----------------------------------------------------------------------------*/
/* Device�fs model and version info                                           */
/*----------------------------------------------------------------------------*/
typedef struct
{
    uint8_t   string[12] ;
    uint8_t   major ;
    uint8_t   minor ;
    uint8_t   relese ;
    uint8_t   revision[4] ;
}HVC_VERSION ;

/*----------------------------------------------------------------------------*/
/* Detection result                                                           */
/*----------------------------------------------------------------------------*/
typedef struct
{
    int32_t   posX ;       /* Center x-coordinate */
    int32_t   posY ;       /* Center y-coordinate */
    int32_t   size ;       /* Size */
    int32_t   confidence ; /* Degree of confidence */
}DETECT_RESULT ;

/*----------------------------------------------------------------------------*/
/* Face direction                                                             */
/*----------------------------------------------------------------------------*/
typedef struct
{
    int32_t   yaw ;        /* Yaw angle */
    int32_t   pitch ;      /* Pitch angle */
    int32_t   roll ;       /* Roll angle */
    int32_t   confidence ; /* Degree of confidence */
}DIR_RESULT ;

/*----------------------------------------------------------------------------*/
/* Age                                                                        */
/*----------------------------------------------------------------------------*/
typedef struct
{
    int32_t   age ;        /* Age */
    int32_t   confidence ; /* Degree of confidence */
}AGE_RESULT ;

/*----------------------------------------------------------------------------*/
/* Gender                                                                     */
/*----------------------------------------------------------------------------*/
typedef struct
{
    int32_t   gender ;     /* Gender */
    int32_t   confidence ; /* Degree of confidence */
}GENDER_RESULT ;

/*----------------------------------------------------------------------------*/
/* Gaze                                                                       */
/*----------------------------------------------------------------------------*/
typedef struct
{
    int32_t   gazeLR ;     /* Yaw angle */
    int32_t   gazeUD ;     /* Pitch angle */
}GAZE_RESULT ;

/*----------------------------------------------------------------------------*/
/* Blink                                                                      */
/*----------------------------------------------------------------------------*/
typedef struct
{
    int32_t   ratioL ;     /* Left eye blink result */
    int32_t   ratioR ;     /* Right eye blink result */
}BLINK_RESULT ;

/*----------------------------------------------------------------------------*/
/* Expression                                                                 */
/*----------------------------------------------------------------------------*/
typedef struct
{
    int32_t   topExpression ;  /* Top expression */
    int32_t   topScore ;       /* Top score */
    int32_t   score[5] ;       /* Score of 5 expression */
    int32_t   degree ;         /* Negative-positive degree */
}EXPRESSION_RESULT ;

/*----------------------------------------------------------------------------*/
/* Face Recognition                                                           */
/*----------------------------------------------------------------------------*/
typedef struct
{
    int32_t   uid ;        /* User ID */
    int32_t   confidence ; /* Degree of confidence */
}RECOGNITION_RESULT ;

/*----------------------------------------------------------------------------*/
/* Face Detection & Estimations result                                        */
/*----------------------------------------------------------------------------*/
typedef struct
{
    DETECT_RESULT       dtResult ;           /* Face detection result */
    DIR_RESULT          dirResult ;          /* Face direction estimation result */
    AGE_RESULT          ageResult ;          /* Age Estimation result */
    GENDER_RESULT       genderResult ;       /* Gender Estimation result */
    GAZE_RESULT         gazeResult ;         /* Gaze Estimation result */
    BLINK_RESULT        blinkResult ;        /* Blink Estimation result */
    EXPRESSION_RESULT   expressionResult ;   /* Expression Estimation result */
    RECOGNITION_RESULT  recognitionResult ;  /* Face Recognition result */
}FACE_RESULT ;

/*----------------------------------------------------------------------------*/
/* Human Body Detection results                                               */
/*----------------------------------------------------------------------------*/
typedef struct
{
    uint8_t         num ;            /* Number of Detection */
    DETECT_RESULT   bdResult[35] ;   /* Detection result */
}BD_RESULT ;

/*----------------------------------------------------------------------------*/
/* Hand Detection results                                                     */
/*----------------------------------------------------------------------------*/
typedef struct
{
    uint8_t         num ;            /* Number of Detection */
    DETECT_RESULT   hdResult[35] ;   /* Detection result */
}HD_RESULT ;

/*----------------------------------------------------------------------------*/
/* Face Detection & Estimations results                                       */
/*----------------------------------------------------------------------------*/
typedef struct
{
    uint8_t         num ;            /* Number of Detection */
    FACE_RESULT     fcResult[35] ;   /* Detection & Estimations result */
}FD_RESULT ;

/*----------------------------------------------------------------------------*/
/* Image data                                                                 */
/*----------------------------------------------------------------------------*/
typedef struct
{
    int32_t   width ;
    int32_t   height ;
    //uint8_t   image[320*240] ;
}HVC_IMAGE ;

/*----------------------------------------------------------------------------*/
/* Result data of Execute command                                             */
/*----------------------------------------------------------------------------*/
typedef struct
{
    int32_t     executedFunc ;   /* Execution flag */
    BD_RESULT   bdResult ;       /* Human Body Detection results */
    HD_RESULT   hdResult ;       /* Hand Detection results */
    FD_RESULT   fdResult ;       /* Face Detection & Estimations results */
    HVC_IMAGE   image ;          /* Image data */
}HVC_RESULT ;

/*----------------------------------------------------------------------------*/
/* Threshold of confidence                                                    */
/*----------------------------------------------------------------------------*/
typedef struct
{
    int32_t   bdThreshold ;        /* Threshold of confidence of Human Body Detection */
    int32_t   hdThreshold ;        /* Threshold of confidence of Hand Detection */
    int32_t   dtThreshold ;        /* Threshold of confidence of Face Detection */
    int32_t   rsThreshold ;        /* Threshold of confidence of Face Recognition */
}HVC_THRESHOLD ;

/*----------------------------------------------------------------------------*/
/* Detection size                                                             */
/*----------------------------------------------------------------------------*/
typedef struct
{
    int32_t   bdMinSize ;          /* Minimum detection size of Human Body Detection */
    int32_t   bdMaxSize ;          /* Maximum detection size of Human Body Detection */
    int32_t   hdMinSize ;          /* Minimum detection size of Hand Detection */
    int32_t   hdMaxSize ;          /* Maximum detection size of Hand Detection */
    int32_t   dtMinSize ;          /* Minimum detection size of Face Detection */
    int32_t   dtMaxSize ;          /* Maximum detection size of Face Detection */
}HVC_SIZERANGE ;


/* HVC_GetVersion                                                             	*/
/* param    : int32_t         	inTimeOutTime   timeout time (ms)              	*/
/*          : HVC_VERSION   	*outVersion     version data                    */
/*          : uint8_t         	*outStatus      response code                   */
int32_t HVC_GetVersion(int32_t inTimeOutTime, HVC_VERSION *outVersion, uint8_t *outStatus);

/* HVC_Execute                                                                	*/
/* param    : int32_t         inTimeOutTime   timeout time (ms)                 */
/*          : int32_t         inExec          executable function               */
/*          : int32_t         inImage         image output number               */
/*          : HVC_RESULT    *outHVCResult   result data                       	*/
/*          : uint8_t         *outStatus      response code                     */
int32_t HVC_Execute(int32_t inTimeOutTime, int32_t inExec, int32_t inImage, HVC_RESULT *outHVCResult, uint8_t *outStatus);

/* HVC_ExecuteEx                                                              	*/
/* param    : int32_t         inTimeOutTime   timeout time (ms)                 */
/*          : int32_t         inExec          executable function               */
/*          : int32_t         inImage         image output number               */
/*          : HVC_RESULT    *outHVCResult   result data                      	*/
/*          : uint8_t         *outStatus      response code                     */
int32_t HVC_ExecuteEx(int32_t inTimeOutTime, int32_t inExec, int32_t inImage, HVC_RESULT *outHVCResult, uint8_t *outStatus);

/* HVC_Registration                                                           	*/
/* param    : int32_t         inTimeOutTime   timeout time (ms)                 */
/*          : int32_t         inUserID        User ID (0-499)                   */
/*          : int32_t         inDataID        Data ID (0-9)                     */
/*          : HVC_IMAGE     *outImage       image info                        	*/
/*          : uint8_t         *outStatus      response code                     */
int32_t HVC_Registration(int32_t inTimeOutTime, int32_t inUserID, int32_t inDataID, HVC_IMAGE *outImage, uint8_t *outStatus);

/* HVC_DeleteData                                                             	*/
/* param    : int32_t         inTimeOutTime   timeout time (ms)                 */
/*          : int32_t         inUserID        User ID (0-499)                   */
/*          : int32_t         inDataID        Data ID (0-9)                     */
/*          : uint8_t         *outStatus      response code                     */
int32_t HVC_DeleteData(int32_t inTimeOutTime, int32_t inUserID, int32_t inDataID, uint8_t *outStatus);

/* HVC_DeleteUser                                                             	*/
/* param    : int32_t         inTimeOutTime   timeout time (ms)                 */
/*          : int32_t         inUserID        User ID (0-499)                   */
/*          : uint8_t         *outStatus      response code                     */
int32_t HVC_DeleteUser(int32_t inTimeOutTime, int32_t inUserID, uint8_t *outStatus);

/* HVC_DeleteAll                                                              	*/
/* param    : int32_t         inTimeOutTime   timeout time (ms)                 */
/*          : uint8_t         *outStatus      response code                     */
int32_t HVC_DeleteAll(int32_t inTimeOutTime, uint8_t *outStatus);

/* HVC_GetUserData                                                           	*/
/* param    : int32_t         inTimeOutTime   timeout time (ms)                 */
/*          : int32_t         inUserID        User ID (0-499)                   */
/*          : int32_t         *outDataNo      Registration Info                 */
/*          : uint8_t         *outStatus      response code                     */
int32_t HVC_GetUserData(int32_t inTimeOutTime, int32_t inUserID, int32_t *outDataNo, uint8_t *outStatus);

/* HVC_SaveAlbum                                                              	*/
/* param    : int32_t         inTimeOutTime       timeout time (ms)             */
/*          : uint8_t         *outAlbumData       Album data                    */
/*          : int32_t         *outAlbumDataSize   Album data size               */
/*          : uint8_t         *outStatus          response code                 */
int32_t HVC_SaveAlbum(int32_t inTimeOutTime, uint8_t *outAlbumData, int32_t *outAlbumDataSize, uint8_t *outStatus);

/* HVC_LoadAlbum                                                              	*/
/* param    : int32_t         inTimeOutTime   timeout time (ms)                 */
/*          : uint8_t         *inAlbumData    Album data                        */
/*          : int32_t         inAlbumDataSize Album data size                   */
/*          : uint8_t         *outStatus      response code                     */
int32_t HVC_LoadAlbum(int32_t inTimeOutTime, uint8_t *inAlbumData, int32_t inAlbumDataSize, uint8_t *outStatus);

/* HVC_WriteAlbum                                                             	*/
/* param    : int32_t         inTimeOutTime   timeout time (ms)                 */
/*          : uint8_t         *outStatus      response code                     */
int32_t HVC_WriteAlbum(int32_t inTimeOutTime, uint8_t *outStatus);



#endif  /* HVC_G_H_ */
