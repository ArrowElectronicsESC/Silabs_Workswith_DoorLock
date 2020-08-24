/*
 * HVC.c
 *
 *  Created on: July 31, 2020
 *      Author: prassanna.sakore
 */

#include "Global.h"
#include "HVC.h"

/*--------------------------------------------------------------------------------*/
/* Send command signal                                                            */
/* param    : uint8_t         inCommandNo     	command number                    */
/*          : int32_t         inDataSize      	sending signal data size          */
/*          : uint8_t         *inData         	sending signal data               */
/* return   : int32_t                         	execution result error code       */
/*          :                               	0...normal                        */
/*          :                               	-10...timeout error               */
/*--------------------------------------------------------------------------------*/
static int32_t HVC_SendCommand(uint8_t inCommandNo, int32_t inDataSize, uint8_t *inData)
{
    int32_t i;
    uint8_t sendData[32];

    /* Create header */
    sendData[SEND_HEAD_SYNCBYTE]        = (uint8_t)0xFE;
    sendData[SEND_HEAD_COMMANDNO]       = (uint8_t)inCommandNo;
    sendData[SEND_HEAD_DATALENGTHLSB]   = (uint8_t)(inDataSize&0xff);
    sendData[SEND_HEAD_DATALENGTHMSB]   = (uint8_t)((inDataSize>>8)&0xff);

    for(i = 0; i < inDataSize; i++)
    {
        sendData[SEND_HEAD_NUM + i] = inData[i];
    }

	USART1_TX_ADD_SEND_DATA(sendData, (SEND_HEAD_NUM+inDataSize)) ;

    /* Send command signal */
	USART1_Start_TX() ;

	while(USART1_Get_Transmit_Status() EQ IN_PROGRESS) ;

    return 0;
}

/*--------------------------------------------------------------------------------*/
/* Send command signal of LoadAlbum                                           	  */
/* param    : uint8_t         inCommandNo     	command number                    */
/*          : int32_t         inDataSize      	sending signal data size          */
/*          : uint8_t         *inData         	sending signal data               */
/* return   : int32_t                         	execution result error code       */
/*          :                               	0...normal                        */
/*          :                               	-10...timeout error               */
/*--------------------------------------------------------------------------------*/
static int32_t HVC_SendCommandOfLoadAlbum(uint8_t inCommandNo, int32_t inDataSize, uint8_t *inData)
{   
    int32_t i;
    uint8_t *pSendData = NULL;
    
    pSendData = (uint8_t*)malloc(SEND_HEAD_NUM + 4 + inDataSize);

    /* Create header */
    pSendData[SEND_HEAD_SYNCBYTE]       = (uint8_t)0xFE;
    pSendData[SEND_HEAD_COMMANDNO]      = (uint8_t)inCommandNo;
    pSendData[SEND_HEAD_DATALENGTHLSB]  = (uint8_t)4;
    pSendData[SEND_HEAD_DATALENGTHMSB]  = (uint8_t)0;

    pSendData[SEND_HEAD_NUM + 0]        = (uint8_t)(inDataSize & 0x000000ff);
    pSendData[SEND_HEAD_NUM + 1]        = (uint8_t)((inDataSize >> 8) & 0x000000ff);
    pSendData[SEND_HEAD_NUM + 2]        = (uint8_t)((inDataSize >> 16) & 0x000000ff);
    pSendData[SEND_HEAD_NUM + 3]        = (uint8_t)((inDataSize >> 24) & 0x000000ff);

    for(i = 0; i < inDataSize; i++)
    {
    	pSendData[SEND_HEAD_NUM + 4 + i] = inData[i];
    }
     
    USART1_TX_ADD_SEND_DATA(pSendData, (SEND_HEAD_NUM+4+inDataSize)) ;

	/* Send command signal */
	USART1_Start_TX() ;

	while(USART1_Get_Transmit_Status() EQ IN_PROGRESS) ;

    free(pSendData);

    return 0;
}

/*--------------------------------------------------------------------------------*/
/* Receive header                                                             	  */
/* param    : int32_t         inTimeOutTime   	timeout time                      */
/*          : int32_t         *outDataSize    	receive signal data length        */
/*          : uint8_t         *outStatus      	status                            */
/* return   : int32_t                         	execution result error code       */
/*          :                               	0...normal                        */
/*          :                               	-20...timeout error               */
/*          :                               	-21...invalid header error        */
/*--------------------------------------------------------------------------------*/
static int32_t HVC_ReceiveHeader(int32_t inTimeOutTime, int32_t *outDataSize, uint8_t *outStatus)
{
    uint8_t* headerData = USART1_RX_GET_BUFFER() ;

    USART1_RX_SET_TIMEOUT((uint16_t)inTimeOutTime) ;

    while(USART1_Get_Receive_Status() EQ IN_PROGRESS) ;

    if(USART1_RX_GET_NO_OF_BYTES() < RECEIVE_HEAD_NUM)
    {
        return HVC_ERROR_HEADER_TIMEOUT;
    }
    else if((uint8_t)0xFE != headerData[RECEIVE_HEAD_SYNCBYTE])
    {
        /* Different value indicates an invalid result */
        return HVC_ERROR_HEADER_INVALID;
    }

    /* Get data length */
    *outDataSize = headerData[RECEIVE_HEAD_DATALENLL] +
                    (headerData[RECEIVE_HEAD_DATALENLM]<<8) +
                    (headerData[RECEIVE_HEAD_DATALENML]<<16) +
                    (headerData[RECEIVE_HEAD_DATALENMM]<<24);

    /* Get command execution result */
    *outStatus  = headerData[RECEIVE_HEAD_STATUS];
    return 0;
}

/*--------------------------------------------------------------------------------*/
/* Receive data                                                               	  */
/* param    : int32_t         inTimeOutTime   	timeout time                      */
/*          : int32_t         inDataSize      	receive signal data size          */
/*          : uint8_t         *outResult      	receive signal data               */
/* return   : int32_t                         	execution result error code       */
/*          :                               	0...normal                        */
/*          :                               	-20...timeout error               */
/*--------------------------------------------------------------------------------*/
static int32_t HVC_ReceiveData(int32_t inTimeOutTime,int32_t startLocation, int32_t inDataSize, uint8_t *outResult)
{
    int32_t ret = 0;
    uint8_t* Data = USART1_RX_GET_BUFFER() ;

    if ( inDataSize <= 0 ) return 0;

    if((USART1_RX_GET_NO_OF_BYTES() - startLocation) < inDataSize)
    {
        return HVC_ERROR_DATA_TIMEOUT;
    }

    for(ret = startLocation ; ret < (startLocation + inDataSize); ret++)
    {
    	outResult[(ret - startLocation)] = Data[ret] ;
    }

    return 0;
}

/*--------------------------------------------------------------------------------*/
/* HVC_GetVersion                                                             	  */
/* param    : int32_t         inTimeOutTime   	timeout time (ms)                 */
/*          : HVC_VERSION   *outVersion     	version data                      */
/*          : uint8_t         *outStatus      	response code                     */
/* return   : int32_t                         	execution result error code       */
/*          :                               	0...normal                        */
/*          :                               	-1...parameter error              */
/*          :                               	other...signal error              */
/*--------------------------------------------------------------------------------*/
int32_t HVC_GetVersion(int32_t inTimeOutTime, HVC_VERSION *outVersion, uint8_t *outStatus)
{
    int32_t ret = 0;
    int32_t size = 0;

    if((NULL == outVersion) || (NULL == outStatus))
    {
        return HVC_ERROR_PARAMETER;
    }

    /* Send GetVersion command signal */
    ret = HVC_SendCommand(HVC_COM_GET_VERSION, 0, NULL);
    if ( ret != 0 ) return ret;

    /* Receive header */
    ret = HVC_ReceiveHeader(inTimeOutTime, &size, outStatus);
    if ( ret != 0 ) return ret;

    if ( size > (int32_t)sizeof(HVC_VERSION) )
    {
        size = sizeof(HVC_VERSION);
    }

    /* Receive data */
    return HVC_ReceiveData(inTimeOutTime, RECEIVE_HEAD_NUM ,size, (uint8_t*)outVersion);
}

/*--------------------------------------------------------------------------------*/
/* HVC_Execute                                                                    */
/* param    : int32_t         inTimeOutTime   	timeout time (ms)                 */
/*          : int32_t         inExec          	executable function               */
/*          : int32_t         inImage         	image info                        */
/*          : HVC_RESULT    *outHVCResult   	result data                       */
/*          : uint8_t         *outStatus      	response code                     */
/* return   : int32_t                         	execution result error code       */
/*          :                               	0...normal                        */
/*          :                               	-1...parameter error              */
/*          :                               	other...signal error              */
/*--------------------------------------------------------------------------------*/
int32_t HVC_Execute(int32_t inTimeOutTime, int32_t inExec, int32_t inImage, HVC_RESULT *outHVCResult, uint8_t *outStatus)
{
    int i;
    int32_t ret = 0;
    int32_t size = 0;
    int32_t startLocation = RECEIVE_HEAD_NUM;
    uint8_t sendData[32];
    uint8_t recvData[32];

    if((NULL == outHVCResult) || (NULL == outStatus))
    {
        return HVC_ERROR_PARAMETER;
    }

    /* Send Execute command signal */
    sendData[0] = (uint8_t)(inExec&0xff);
    sendData[1] = (uint8_t)((inExec>>8)&0xff);
    sendData[2] = (uint8_t)(inImage&0xff);
    ret = HVC_SendCommand(HVC_COM_EXECUTE, sizeof(uint8_t)*3, sendData);
    if ( ret != 0 ) return ret;

    /* Receive header */
    ret = HVC_ReceiveHeader(inTimeOutTime, &size, outStatus);
    if ( ret != 0 ) return ret;

    /* Receive result data */
    if ( size >= (int32_t)sizeof(uint8_t)*4 )
    {
        outHVCResult->executedFunc = inExec;
        ret = HVC_ReceiveData(inTimeOutTime, startLocation , sizeof(uint8_t)*4, recvData);
        outHVCResult->bdResult.num = recvData[0];
        outHVCResult->hdResult.num = recvData[1];
        outHVCResult->fdResult.num = recvData[2];
        if ( ret != 0 ) return ret;
        size -= sizeof(uint8_t)*4;
        startLocation += sizeof(uint8_t)*4;
    }

    /* Get Human Body Detection result */
    for(i = 0; i < outHVCResult->bdResult.num; i++)
    {
        if ( size >= (int32_t)sizeof(uint8_t)*8 ) {
            ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*8, recvData);
            outHVCResult->bdResult.bdResult[i].posX = (short)(recvData[0] + (recvData[1]<<8));
            outHVCResult->bdResult.bdResult[i].posY = (short)(recvData[2] + (recvData[3]<<8));
            outHVCResult->bdResult.bdResult[i].size = (short)(recvData[4] + (recvData[5]<<8));
            outHVCResult->bdResult.bdResult[i].confidence = (short)(recvData[6] + (recvData[7]<<8));
            if ( ret != 0 ) return ret;
            size -= sizeof(uint8_t)*8;
            startLocation += sizeof(uint8_t)*8;
        }
    }

    /* Get Hand Detection result */
    for(i = 0; i < outHVCResult->hdResult.num; i++)
    {
        if ( size >= (int32_t)sizeof(uint8_t)*8 )
        {
            ret = HVC_ReceiveData(inTimeOutTime,startLocation, sizeof(uint8_t)*8, recvData);
            outHVCResult->hdResult.hdResult[i].posX = (short)(recvData[0] + (recvData[1]<<8));
            outHVCResult->hdResult.hdResult[i].posY = (short)(recvData[2] + (recvData[3]<<8));
            outHVCResult->hdResult.hdResult[i].size = (short)(recvData[4] + (recvData[5]<<8));
            outHVCResult->hdResult.hdResult[i].confidence = (short)(recvData[6] + (recvData[7]<<8));
            if ( ret != 0 ) return ret;
            size -= sizeof(uint8_t)*8;
            startLocation += sizeof(uint8_t)*8;
        }
    }

    /* Face-related results */
    for(i = 0; i < outHVCResult->fdResult.num; i++)
    {
        /* Face Detection result */
        if(0 != (outHVCResult->executedFunc & HVC_ACTIV_FACE_DETECTION))
        {
            if ( size >= (int32_t)sizeof(uint8_t)*8 )
            {
                ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*8, recvData);
                outHVCResult->fdResult.fcResult[i].dtResult.posX = (short)(recvData[0] + (recvData[1]<<8));
                outHVCResult->fdResult.fcResult[i].dtResult.posY = (short)(recvData[2] + (recvData[3]<<8));
                outHVCResult->fdResult.fcResult[i].dtResult.size = (short)(recvData[4] + (recvData[5]<<8));
                outHVCResult->fdResult.fcResult[i].dtResult.confidence = (short)(recvData[6] + (recvData[7]<<8));
                if ( ret != 0 ) return ret;
                size -= sizeof(uint8_t)*8;
                startLocation += sizeof(uint8_t)*8;
            }
        }

        /* Face direction */
        if(0 != (outHVCResult->executedFunc & HVC_ACTIV_FACE_DIRECTION))
        {
            if ( size >= (int32_t)sizeof(uint8_t)*8 )
            {
                ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*8, recvData);
                outHVCResult->fdResult.fcResult[i].dirResult.yaw = (short)(recvData[0] + (recvData[1]<<8));
                outHVCResult->fdResult.fcResult[i].dirResult.pitch = (short)(recvData[2] + (recvData[3]<<8));
                outHVCResult->fdResult.fcResult[i].dirResult.roll = (short)(recvData[4] + (recvData[5]<<8));
                outHVCResult->fdResult.fcResult[i].dirResult.confidence = (short)(recvData[6] + (recvData[7]<<8));
                if ( ret != 0 ) return ret;
                size -= sizeof(uint8_t)*8;
                startLocation += sizeof(uint8_t)*8;
            }
        }

        /* Age */
        if(0 != (outHVCResult->executedFunc & HVC_ACTIV_AGE_ESTIMATION))
        {
            if ( size >= (int32_t)sizeof(uint8_t)*3 )
            {
                ret = HVC_ReceiveData(inTimeOutTime, startLocation , sizeof(uint8_t)*3, recvData);
                outHVCResult->fdResult.fcResult[i].ageResult.age = (char)(recvData[0]);
                outHVCResult->fdResult.fcResult[i].ageResult.confidence = (short)(recvData[1] + (recvData[2]<<8));
                if ( ret != 0 ) return ret;
                size -= sizeof(uint8_t)*3;
                startLocation += sizeof(uint8_t)*3;
            }
        }

        /* Gender */
        if(0 != (outHVCResult->executedFunc & HVC_ACTIV_GENDER_ESTIMATION))
        {
            if ( size >= (int32_t)sizeof(uint8_t)*3 )
            {
                ret = HVC_ReceiveData(inTimeOutTime, startLocation , sizeof(uint8_t)*3, recvData);
                outHVCResult->fdResult.fcResult[i].genderResult.gender = (char)(recvData[0]);
                outHVCResult->fdResult.fcResult[i].genderResult.confidence = (short)(recvData[1] + (recvData[2]<<8));
                if ( ret != 0 ) return ret;
                size -= sizeof(uint8_t)*3;
                startLocation += sizeof(uint8_t)*3;
            }
        }

        /* Gaze */
        if(0 != (outHVCResult->executedFunc & HVC_ACTIV_GAZE_ESTIMATION))
        {
            if ( size >= (int32_t)sizeof(uint8_t)*2 )
            {
                ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*2, recvData);
                outHVCResult->fdResult.fcResult[i].gazeResult.gazeLR = (char)(recvData[0]);
                outHVCResult->fdResult.fcResult[i].gazeResult.gazeUD = (char)(recvData[1]);
                if ( ret != 0 ) return ret;
                size -= sizeof(uint8_t)*2;
                startLocation += sizeof(uint8_t)*2;
            }
        }

        /* Blink */
        if(0 != (outHVCResult->executedFunc & HVC_ACTIV_BLINK_ESTIMATION))
        {
            if ( size >= (int32_t)sizeof(uint8_t)*4 )
            {
                ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*4, recvData);
                outHVCResult->fdResult.fcResult[i].blinkResult.ratioL = (short)(recvData[0] + (recvData[1]<<8));
                outHVCResult->fdResult.fcResult[i].blinkResult.ratioR = (short)(recvData[2] + (recvData[3]<<8));
                if ( ret != 0 ) return ret;
                size -= sizeof(uint8_t)*4;
                startLocation += sizeof(uint8_t)*4;
            }
        }

        /* Expression */
        if(0 != (outHVCResult->executedFunc & HVC_ACTIV_EXPRESSION_ESTIMATION))
        {
            if ( size >= (int32_t)sizeof(uint8_t)*3 )
            {
                ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*3, recvData);
                outHVCResult->fdResult.fcResult[i].expressionResult.topExpression = (char)(recvData[0]);
                outHVCResult->fdResult.fcResult[i].expressionResult.topScore = (char)(recvData[1]);
                outHVCResult->fdResult.fcResult[i].expressionResult.degree = (char)(recvData[2]);
                if ( ret != 0 ) return ret;
                size -= sizeof(uint8_t)*3;
                startLocation += sizeof(uint8_t)*3;
            }
        }

        /* Face Recognition */
        if(0 != (outHVCResult->executedFunc & HVC_ACTIV_FACE_RECOGNITION))
        {
            if ( size >= (int32_t)sizeof(uint8_t)*4 )
            {
                ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*4, recvData);
                outHVCResult->fdResult.fcResult[i].recognitionResult.uid = (short)(recvData[0] + (recvData[1]<<8));
                outHVCResult->fdResult.fcResult[i].recognitionResult.confidence = (short)(recvData[2] + (recvData[3]<<8));
                if ( ret != 0 ) return ret;
                size -= sizeof(uint8_t)*4;
                startLocation += sizeof(uint8_t)*4;
            }
        }
    }

    /*if(HVC_EXECUTE_IMAGE_NONE != inImage)
    {
        // Image data
        if ( size >= (int32_t)sizeof(uint8_t)*4 )
        {
            ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*4, recvData);
            outHVCResult->image.width = (short)(recvData[0] + (recvData[1]<<8));
            outHVCResult->image.height = (short)(recvData[2] + (recvData[3]<<8));
            if ( ret != 0 ) return ret;
            size -= sizeof(uint8_t)*4;
            startLocation += sizeof(uint8_t)*4;
        }

        if ( size >= (int32_t)sizeof(uint8_t)*outHVCResult->image.width*outHVCResult->image.height )
        {
            ret = HVC_ReceiveData(inTimeOutTime, startLocation , sizeof(uint8_t)*outHVCResult->image.width*outHVCResult->image.height, outHVCResult->image.image);
            if ( ret != 0 ) return ret;
            size -= sizeof(uint8_t)*outHVCResult->image.width*outHVCResult->image.height;
        }
    }*/
    return 0;
}

/*--------------------------------------------------------------------------------*/
/* HVC_ExecuteEx                                                                  */
/* param    : int32_t         inTimeOutTime   	timeout time (ms)                 */
/*          : int32_t         inExec          	executable function               */
/*          : int32_t         inImage         	image info                        */
/*          : HVC_RESULT    *outHVCResult   	result data                       */
/*          : uint8_t         *outStatus      	response code                     */
/* return   : int32_t                         	execution result error code       */
/*          :                               	0...normal                        */
/*          :                               	-1...parameter error              */
/*          :                               	other...signal error              */
/*--------------------------------------------------------------------------------*/
int32_t HVC_ExecuteEx(int32_t inTimeOutTime, int32_t inExec, int32_t inImage, HVC_RESULT *outHVCResult, uint8_t *outStatus)
{
    int i, j;
    int32_t ret = 0;
    int32_t size = 0;
    int32_t startLocation = RECEIVE_HEAD_NUM;
    uint8_t sendData[32];
    uint8_t recvData[32];

    if((NULL == outHVCResult) || (NULL == outStatus))
    {
        return HVC_ERROR_PARAMETER;
    }

    /* Send Execute command signal */
    sendData[0] = (uint8_t)(inExec&0xff);
    sendData[1] = (uint8_t)((inExec>>8)&0xff);
    sendData[2] = (uint8_t)(inImage&0xff);
    ret = HVC_SendCommand(HVC_COM_EXECUTEEX, sizeof(uint8_t)*3, sendData);
    if ( ret != 0 ) return ret;

    /* Receive header */
    ret = HVC_ReceiveHeader(inTimeOutTime, &size, outStatus);
    if ( ret != 0 ) return ret;

    /* Receive result data */
    if ( size >= (int32_t)sizeof(uint8_t)*4 )
    {
        outHVCResult->executedFunc = inExec;
        ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*4, recvData);
        outHVCResult->bdResult.num = recvData[0];
        outHVCResult->hdResult.num = recvData[1];
        outHVCResult->fdResult.num = recvData[2];
        if ( ret != 0 ) return ret;
        size -= sizeof(uint8_t)*4;
        startLocation += sizeof(uint8_t)*4;
    }

    /* Get Human Body Detection result */
    for(i = 0; i < outHVCResult->bdResult.num; i++)
    {
        if ( size >= (int32_t)sizeof(uint8_t)*8 )
        {
            ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*8, recvData);
            outHVCResult->bdResult.bdResult[i].posX = (short)(recvData[0] + (recvData[1]<<8));
            outHVCResult->bdResult.bdResult[i].posY = (short)(recvData[2] + (recvData[3]<<8));
            outHVCResult->bdResult.bdResult[i].size = (short)(recvData[4] + (recvData[5]<<8));
            outHVCResult->bdResult.bdResult[i].confidence = (short)(recvData[6] + (recvData[7]<<8));
            if ( ret != 0 ) return ret;
            size -= sizeof(uint8_t)*8;
            startLocation += sizeof(uint8_t)*8;
        }
    }

    /* Get Hand Detection result */
    for(i = 0; i < outHVCResult->hdResult.num; i++)
    {
        if ( size >= (int32_t)sizeof(uint8_t)*8 )
        {
            ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*8, recvData);
            outHVCResult->hdResult.hdResult[i].posX = (short)(recvData[0] + (recvData[1]<<8));
            outHVCResult->hdResult.hdResult[i].posY = (short)(recvData[2] + (recvData[3]<<8));
            outHVCResult->hdResult.hdResult[i].size = (short)(recvData[4] + (recvData[5]<<8));
            outHVCResult->hdResult.hdResult[i].confidence = (short)(recvData[6] + (recvData[7]<<8));
            if ( ret != 0 ) return ret;
            size -= sizeof(uint8_t)*8;
            startLocation += sizeof(uint8_t)*8;
        }
    }

    /* Face-related results */
    for(i = 0; i < outHVCResult->fdResult.num; i++)
    {
        /* Face Detection result */
        if(0 != (outHVCResult->executedFunc & HVC_ACTIV_FACE_DETECTION))
        {
            if ( size >= (int32_t)sizeof(uint8_t)*8 )
            {
                ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*8, recvData);
                outHVCResult->fdResult.fcResult[i].dtResult.posX = (short)(recvData[0] + (recvData[1]<<8));
                outHVCResult->fdResult.fcResult[i].dtResult.posY = (short)(recvData[2] + (recvData[3]<<8));
                outHVCResult->fdResult.fcResult[i].dtResult.size = (short)(recvData[4] + (recvData[5]<<8));
                outHVCResult->fdResult.fcResult[i].dtResult.confidence = (short)(recvData[6] + (recvData[7]<<8));
                if ( ret != 0 ) return ret;
                size -= sizeof(uint8_t)*8;
                startLocation += sizeof(uint8_t)*8;
            }
        }

        /* Face direction */
        if(0 != (outHVCResult->executedFunc & HVC_ACTIV_FACE_DIRECTION))
        {
            if ( size >= (int32_t)sizeof(uint8_t)*8 )
            {
                ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*8, recvData);
                outHVCResult->fdResult.fcResult[i].dirResult.yaw = (short)(recvData[0] + (recvData[1]<<8));
                outHVCResult->fdResult.fcResult[i].dirResult.pitch = (short)(recvData[2] + (recvData[3]<<8));
                outHVCResult->fdResult.fcResult[i].dirResult.roll = (short)(recvData[4] + (recvData[5]<<8));
                outHVCResult->fdResult.fcResult[i].dirResult.confidence = (short)(recvData[6] + (recvData[7]<<8));
                if ( ret != 0 ) return ret;
                size -= sizeof(uint8_t)*8;
                startLocation += sizeof(uint8_t)*8;
            }
        }

        /* Age */
        if(0 != (outHVCResult->executedFunc & HVC_ACTIV_AGE_ESTIMATION))
        {
            if ( size >= (int32_t)sizeof(uint8_t)*3 )
            {
                ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*3, recvData);
                outHVCResult->fdResult.fcResult[i].ageResult.age = (char)(recvData[0]);
                outHVCResult->fdResult.fcResult[i].ageResult.confidence = (short)(recvData[1] + (recvData[2]<<8));
                if ( ret != 0 ) return ret;
                size -= sizeof(uint8_t)*3;
                startLocation += sizeof(uint8_t)*3;
            }
        }

        /* Gender */
        if(0 != (outHVCResult->executedFunc & HVC_ACTIV_GENDER_ESTIMATION))
        {
            if ( size >= (int32_t)sizeof(uint8_t)*3 )
            {
                ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*3, recvData);
                outHVCResult->fdResult.fcResult[i].genderResult.gender = (char)(recvData[0]);
                outHVCResult->fdResult.fcResult[i].genderResult.confidence = (short)(recvData[1] + (recvData[2]<<8));
                if ( ret != 0 ) return ret;
                size -= sizeof(uint8_t)*3;
                startLocation += sizeof(uint8_t)*3;
            }
        }

        /* Gaze */
        if(0 != (outHVCResult->executedFunc & HVC_ACTIV_GAZE_ESTIMATION))
        {
            if ( size >= (int32_t)sizeof(uint8_t)*2 )
            {
                ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*2, recvData);
                outHVCResult->fdResult.fcResult[i].gazeResult.gazeLR = (char)(recvData[0]);
                outHVCResult->fdResult.fcResult[i].gazeResult.gazeUD = (char)(recvData[1]);
                if ( ret != 0 ) return ret;
                size -= sizeof(uint8_t)*2;
                startLocation += sizeof(uint8_t)*2;
            }
        }

        /* Blink */
        if(0 != (outHVCResult->executedFunc & HVC_ACTIV_BLINK_ESTIMATION))
        {
            if ( size >= (int32_t)sizeof(uint8_t)*4 )
            {
                ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*4, recvData);
                outHVCResult->fdResult.fcResult[i].blinkResult.ratioL = (short)(recvData[0] + (recvData[1]<<8));
                outHVCResult->fdResult.fcResult[i].blinkResult.ratioR = (short)(recvData[2] + (recvData[3]<<8));
                if ( ret != 0 ) return ret;
                size -= sizeof(uint8_t)*4;
                startLocation += sizeof(uint8_t)*4;
            }
        }

        /* Expression */
        if(0 != (outHVCResult->executedFunc & HVC_ACTIV_EXPRESSION_ESTIMATION))
        {
            if ( size >= (int32_t)sizeof(uint8_t)*6 )
            {
                ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*6, recvData);
                outHVCResult->fdResult.fcResult[i].expressionResult.topExpression = -128;
                outHVCResult->fdResult.fcResult[i].expressionResult.topScore = -128;
                for(j = 0; j < 5; j++)
                {
                    outHVCResult->fdResult.fcResult[i].expressionResult.score[j] = (char)(recvData[j]);
                    if(outHVCResult->fdResult.fcResult[i].expressionResult.topScore < outHVCResult->fdResult.fcResult[i].expressionResult.score[j])
                    {
                        outHVCResult->fdResult.fcResult[i].expressionResult.topScore = outHVCResult->fdResult.fcResult[i].expressionResult.score[j];
                        outHVCResult->fdResult.fcResult[i].expressionResult.topExpression = j + 1;
                    }
                }
                outHVCResult->fdResult.fcResult[i].expressionResult.degree = (char)(recvData[5]);
                if ( ret != 0 ) return ret;
                size -= sizeof(uint8_t)*6;
                startLocation += sizeof(uint8_t)*6;
            }
        }

        /* Face Recognition */
        if(0 != (outHVCResult->executedFunc & HVC_ACTIV_FACE_RECOGNITION))
        {
            if ( size >= (int32_t)sizeof(uint8_t)*4 )
            {
                ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*4, recvData);
                outHVCResult->fdResult.fcResult[i].recognitionResult.uid = (short)(recvData[0] + (recvData[1]<<8));
                outHVCResult->fdResult.fcResult[i].recognitionResult.confidence = (short)(recvData[2] + (recvData[3]<<8));
                if ( ret != 0 ) return ret;
                size -= sizeof(uint8_t)*4;
                startLocation += sizeof(uint8_t)*4;
            }
        }
    }

    /*if(HVC_EXECUTE_IMAGE_NONE != inImage)
    {
        // Image data
        if ( size >= (int32_t)sizeof(uint8_t)*4 )
        {
            ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*4, recvData);
            outHVCResult->image.width = (short)(recvData[0] + (recvData[1]<<8));
            outHVCResult->image.height = (short)(recvData[2] + (recvData[3]<<8));
            if ( ret != 0 ) return ret;
            size -= sizeof(uint8_t)*4;
            startLocation += sizeof(uint8_t)*4;
        }

        if ( size >= (int32_t)sizeof(uint8_t)*outHVCResult->image.width*outHVCResult->image.height )
        {
            ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*outHVCResult->image.width*outHVCResult->image.height, outHVCResult->image.image);
            if ( ret != 0 ) return ret;
            size -= sizeof(uint8_t)*outHVCResult->image.width*outHVCResult->image.height;
        }
    }*/
    return 0;
}

/*--------------------------------------------------------------------------------*/
/* HVC_Registration                                                               */
/* param    : int32_t         inTimeOutTime   	timeout time (ms)                 */
/*          : int32_t         inUserID        	User ID (0-499)                   */
/*          : int32_t         inDataID        	Data ID (0-9)                     */
/*          : HVC_IMAGE     *outImage       	image info                        */
/*          : uint8_t         *outStatus      	response code                     */
/* return   : int32_t                         	execution result error code       */
/*          :                               	0...normal                        */
/*          :                               	-1...parameter error              */
/*          :                               	other...signal error              */
/*--------------------------------------------------------------------------------*/
int32_t HVC_Registration(int32_t inTimeOutTime, int32_t inUserID, int32_t inDataID, HVC_IMAGE *outImage, uint8_t *outStatus)
{
    int32_t ret = 0;
    int32_t size = 0;
    int32_t startLocation = RECEIVE_HEAD_NUM;
    uint8_t sendData[32];
    uint8_t recvData[32];

    if((NULL == outImage) || (NULL == outStatus))
    {
        return HVC_ERROR_PARAMETER;
    }

    /* Send Registration signal command */
    sendData[0] = (uint8_t)(inUserID&0xff);
    sendData[1] = (uint8_t)((inUserID>>8)&0xff);
    sendData[2] = (uint8_t)(inDataID&0xff);
    ret = HVC_SendCommand(HVC_COM_REGISTRATION, sizeof(uint8_t)*3, sendData);
    if ( ret != 0 ) return ret;

    /* Receive header */
    ret = HVC_ReceiveHeader(inTimeOutTime, &size, outStatus);
    if ( ret != 0 ) return ret;

    /* Receive data */
    if ( size >= (int32_t)sizeof(uint8_t)*4 )
    {
        ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*4, recvData);
        outImage->width = recvData[0] + (recvData[1]<<8);
        outImage->height = recvData[2] + (recvData[3]<<8);
        if ( ret != 0 ) return ret;
        size -= sizeof(uint8_t)*4;
        startLocation += sizeof(uint8_t)*4;
    }

    /* Image data */
    /*if ( size >= (int32_t)sizeof(uint8_t)*64*64 )
    {
        ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*64*64, outImage->image);
        if ( ret != 0 ) return ret;
        size -= sizeof(uint8_t)*64*64;
    }*/
    return 0;
}

/*--------------------------------------------------------------------------------*/
/* HVC_DeleteData                                                             	  */
/* param    : int32_t         inTimeOutTime   	timeout time (ms)                 */
/*          : int32_t         inUserID        	User ID (0-499)                   */
/*          : int32_t         inDataID        	Data ID (0-9)                     */
/*          : uint8_t         *outStatus      	response code                     */
/* return   : int32_t                         	execution result error code       */
/*          :                               	0...normal                        */
/*          :                               	-1...parameter error              */
/*          :                               	other...signal error              */
/*--------------------------------------------------------------------------------*/
int32_t HVC_DeleteData(int32_t inTimeOutTime, int32_t inUserID, int32_t inDataID, uint8_t *outStatus)
{
    int32_t ret = 0;
    int32_t size = 0;
    uint8_t sendData[32];

    if(NULL == outStatus)
    {
        return HVC_ERROR_PARAMETER;
    }

    /* Send Delete Data signal command */
    sendData[0] = (uint8_t)(inUserID&0xff);
    sendData[1] = (uint8_t)((inUserID>>8)&0xff);
    sendData[2] = (uint8_t)(inDataID&0xff);
    ret = HVC_SendCommand(HVC_COM_DELETE_DATA, sizeof(uint8_t)*3, sendData);
    if ( ret != 0 ) return ret;

    /* Receive header */
    ret = HVC_ReceiveHeader(inTimeOutTime, &size, outStatus);
    if ( ret != 0 ) return ret;
    return 0;
}

/*--------------------------------------------------------------------------------*/
/* HVC_DeleteUser                                                             	  */
/* param    : int32_t         inTimeOutTime   	timeout time (ms)                 */
/*          : int32_t         inUserID        	User ID (0-499)                   */
/*          : uint8_t         *outStatus      	response code                     */
/* return   : int32_t                         	execution result error code       */
/*          :                               	0...normal                        */
/*          :                               	-1...parameter error              */
/*          :                               	other...signal error              */
/*--------------------------------------------------------------------------------*/
int32_t HVC_DeleteUser(int32_t inTimeOutTime, int32_t inUserID, uint8_t *outStatus)
{
    int32_t ret = 0;
    int32_t size = 0;
    uint8_t sendData[32];

    if(NULL == outStatus)
    {
        return HVC_ERROR_PARAMETER;
    }

    /* Send Delete User signal command */
    sendData[0] = (uint8_t)(inUserID&0xff);
    sendData[1] = (uint8_t)((inUserID>>8)&0xff);
    ret = HVC_SendCommand(HVC_COM_DELETE_USER, sizeof(uint8_t)*2, sendData);
    if ( ret != 0 ) return ret;

    /* Receive header */
    ret = HVC_ReceiveHeader(inTimeOutTime, &size, outStatus);
    if ( ret != 0 ) return ret;
    return 0;
}

/*--------------------------------------------------------------------------------*/
/* HVC_DeleteAll                                                                  */
/* param    : int32_t         inTimeOutTime   	timeout time (ms)                 */
/*          : uint8_t         *outStatus      	response code                     */
/* return   : int32_t                         	execution result error code       */
/*          :                               	0...normal                        */
/*          :                               	-1...parameter error              */
/*          :                               	other...signal error              */
/*--------------------------------------------------------------------------------*/
int32_t HVC_DeleteAll(int32_t inTimeOutTime, uint8_t *outStatus)
{
    int32_t ret = 0;
    int32_t size = 0;

    if(NULL == outStatus)
    {
        return HVC_ERROR_PARAMETER;
    }

    /* Send Delete All signal command */
    ret = HVC_SendCommand(HVC_COM_DELETE_ALL, 0, NULL);
    if ( ret != 0 ) return ret;

    /* Receive header */
    ret = HVC_ReceiveHeader(inTimeOutTime, &size, outStatus);
    if ( ret != 0 ) return ret;
    return 0;
}

/*--------------------------------------------------------------------------------*/
/* HVC_GetUserData                                                                */
/* param    : int32_t         inTimeOutTime   	timeout time (ms)                 */
/*          : int32_t         inUserID        	User ID (0-499)                   */
/*          : int32_t         *outDataNo      	Registration Info                 */
/*          : uint8_t         *outStatus      	response code                     */
/* return   : int32_t                         	execution result error code       */
/*          :                               	0...normal                        */
/*          :                               	-1...parameter error              */
/*          :                               	other...signal error              */
/*--------------------------------------------------------------------------------*/
int32_t HVC_GetUserData(int32_t inTimeOutTime, int32_t inUserID, int32_t *outDataNo, uint8_t *outStatus)
{
    int32_t ret = 0;
    int32_t size = 0;
    int32_t startLocation = RECEIVE_HEAD_NUM;
    uint8_t sendData[8];
    uint8_t recvData[8];

    if((NULL == outDataNo) || (NULL == outStatus))
    {
        return HVC_ERROR_PARAMETER;
    }

    /* Send Get Registration Info signal command */
    sendData[0] = (uint8_t)(inUserID&0xff);
    sendData[1] = (uint8_t)((inUserID>>8)&0xff);
    ret = HVC_SendCommand(HVC_COM_GET_PERSON_DATA, sizeof(uint8_t)*2, sendData);
    if ( ret != 0 ) return ret;

    /* Receive header */
    ret = HVC_ReceiveHeader(inTimeOutTime, &size, outStatus);
    if ( ret != 0 ) return ret;

    if ( size > (int32_t)sizeof(uint8_t)*2 )
    {
        size = sizeof(uint8_t)*2;
    }

    /* Receive data */
    ret = HVC_ReceiveData(inTimeOutTime, startLocation, size, recvData);
    *outDataNo = recvData[0] + (recvData[1]<<8);
    return ret;
}

/*--------------------------------------------------------------------------------*/
/* HVC_SaveAlbum                                                                  */
/* param    : int32_t         inTimeOutTime       	timeout time (ms)             */
/*          : uint8_t         *outAlbumData       	Album data                    */
/*          : int32_t         *outAlbumDataSize   	Album data size               */
/*          : uint8_t         *outStatus          	response code                 */
/* return   : int32_t                             	execution result error code   */
/*          :                                   	0...normal                    */
/*          :                                   	-1...parameter error          */
/*          :                                   	other...signal error          */
/*--------------------------------------------------------------------------------*/
int32_t HVC_SaveAlbum(int32_t inTimeOutTime, uint8_t *outAlbumData, int32_t *outAlbumDataSize, uint8_t *outStatus)
{
    int32_t ret = 0;
    int32_t size = 0;
    int32_t startLocation = RECEIVE_HEAD_NUM;
    uint8_t *tmpAlbumData = NULL;;
    
    if((NULL == outAlbumData) || (NULL == outAlbumDataSize) || (NULL == outStatus))
    {
        return HVC_ERROR_PARAMETER;
    }
        
    /* Send Save Album signal command */
    ret = HVC_SendCommand(HVC_COM_SAVE_ALBUM, 0, NULL);
    if ( ret != 0 ) return ret;

    ret = HVC_ReceiveHeader(inTimeOutTime, &size, outStatus);
    if ( ret != 0 ) return ret;

    if ( size >= (int32_t)sizeof(uint8_t)*8 + HVC_ALBUM_SIZE_MIN )
    {
        *outAlbumDataSize = size;
        tmpAlbumData = outAlbumData;

        do{
            ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*4, tmpAlbumData);
            if ( ret != 0 ) return ret;
            tmpAlbumData += sizeof(uint8_t)*4;
            startLocation += sizeof(uint8_t)*4;

            ret = HVC_ReceiveData(inTimeOutTime, startLocation, sizeof(uint8_t)*4, tmpAlbumData);
            if ( ret != 0 ) return ret;
            tmpAlbumData += sizeof(uint8_t)*4;
            startLocation += sizeof(uint8_t)*4;

            ret = HVC_ReceiveData(inTimeOutTime, startLocation, size - sizeof(uint8_t)*8, tmpAlbumData);
            if ( ret != 0 ) return ret;

          }while(0);
    }
    return ret;
}


/*--------------------------------------------------------------------------------*/
/* HVC_LoadAlbum                                                                  */
/* param    : int32_t         inTimeOutTime   	timeout time (ms)                 */
/*          : uint8_t         *inAlbumData    	Album data                        */
/*          : int32_t         inAlbumDataSize 	Album data size                   */
/*          : uint8_t         *outStatus      	response code                     */
/* return   : int32_t                         	execution result error code       */
/*          :                               	0...normal                        */
/*          :                               	-1...parameter error              */
/*          :                               	other...signal error              */
/*--------------------------------------------------------------------------------*/
int32_t HVC_LoadAlbum(int32_t inTimeOutTime, uint8_t *inAlbumData, int32_t inAlbumDataSize, uint8_t *outStatus)
{
    int32_t ret = 0;
    int32_t size = 0;
        
    if((NULL == inAlbumData) || (NULL == outStatus))
    {
        return HVC_ERROR_PARAMETER;
    }
        
    /* Send Save Album signal command */
    ret = HVC_SendCommandOfLoadAlbum(HVC_COM_LOAD_ALBUM, inAlbumDataSize, inAlbumData);
    if ( ret != 0 ) return ret;
    
    /* Receive header */
    ret = HVC_ReceiveHeader(inTimeOutTime, &size, outStatus);
    if ( ret != 0 ) return ret;

    return ret;
}

/*--------------------------------------------------------------------------------*/
/* HVC_WriteAlbum                                                             	  */
/* param    : int32_t         inTimeOutTime   	timeout time (ms)                 */
/*          : uint8_t         *outStatus      	response code                     */
/* return   : int32_t                         	execution result error code       */
/*          :                               	0...normal                        */
/*          :                               	-1...parameter error              */
/*          :                               	other...signal error              */
/*--------------------------------------------------------------------------------*/
int32_t HVC_WriteAlbum(int32_t inTimeOutTime, uint8_t *outStatus)
{
    int32_t ret = 0;
    int32_t size = 0;

    if(NULL == outStatus)
    {
        return HVC_ERROR_PARAMETER;
    }

    /* Send Write Album signal command */
    ret = HVC_SendCommand(HVC_COM_WRITE_ALBUM, 0, NULL);
    if ( ret != 0 ) return ret;

    /* Receive header */
    ret = HVC_ReceiveHeader(inTimeOutTime, &size, outStatus);
    if ( ret != 0 ) return ret;

    return ret;
}

