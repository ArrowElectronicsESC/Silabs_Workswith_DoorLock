/*
 * HVC_Process.c
 *
 *  Created on: August 7, 2020
 *      Author: prassanna.sakore
 */
#include "Global.h"
#include "HVC_Process.h"

HVC_IMAGE pImage ;
HVC_RESULT Result ;
/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: FacePrintEnrollment
* Description  : This function enrolls Face Print
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void FacePrintEnrollment(void)
{

	//HVC_IMAGE *pImage = NULL ;
	uint8_t ucmOutStatus = ZERO ;
	int32_t slmRet = ZERO ;

	//pImage = (HVC_IMAGE *)calloc(1,sizeof(HVC_IMAGE));

	memset(&pImage , 0 ,sizeof(pImage)) ;

	switch(FaceEnroll.ucmEnrollmentState)
	{
		case ENROLL_ENTER_FACE :

			if(FaceEnroll.usmDelayBetweenFaceEnrollments EQ ZERO)
			{
				slmRet = HVC_Registration(TIMEOUT_ENROLL_FACE,(Users.usmCurrentEnrollmentNumber + 1),1,&pImage,&ucmOutStatus) ;

				if(slmRet EQ ZERO)
				{
					if(ucmOutStatus EQ ZERO)
					{
						RefreshIdleStateTimer() ;
						SetGreenLedForGreenSuccess() ;
						FaceEnroll.ucmEnrollmentState = ENROLL_RE_ENTER_FACE ;
						FaceEnroll.usmDelayBetweenFaceEnrollments = SCAN_TIME_BETWEEN_TWO_FACES ;
					}
					else
					{
						SetRedLedForFail() ;
					}
				}
				else
				{
					SetRedLedForFail() ;
				}
			}

			break ;

		case ENROLL_RE_ENTER_FACE	:

			if(FaceEnroll.usmDelayBetweenFaceEnrollments EQ ZERO)
			{
				slmRet = HVC_Registration(TIMEOUT_ENROLL_FACE,(Users.usmCurrentEnrollmentNumber + 1),2,&pImage,&ucmOutStatus) ;
				if(slmRet EQ ZERO)
				{
					if(ucmOutStatus EQ ZERO)
					{
						RefreshIdleStateTimer() ;
						HVC_WriteAlbum(TIMEOUT_WRITE_ALBUM, &ucmOutStatus) ;
						SetGreenLedForGreenSuccess() ;
						FaceEnroll.ucmEnrollmentState = ENROLL_SUCCESS_FACE ;
					}
					else
					{
						SetRedLedForFail() ;
					}
				}
				else
				{
					SetRedLedForFail() ;
				}
			}
			break ;


		case ENROLL_SUCCESS_FACE 	:



			break ;

		default :
			   //default
			break ;
	}

	//free(pImage);
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: SetupFaceEnrollment
* Description  : This function sets up face process for enrollment
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void SetupFaceEnrollment(void)
{

	FaceEnroll.ucmEnrollmentState = ENROLL_ENTER_FACE ;
	FaceEnroll.usmDelayBetweenFaceEnrollments = SCAN_TIME_BETWEEN_TWO_FACES ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: UpadateDelayBetweenFaceEnrollment
* Description  : This function updates delay between two face scans
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void UpadateDelayBetweenFaceEnrollment(void)
{
	if(FaceEnroll.usmDelayBetweenFaceEnrollments)
	{
		FaceEnroll.usmDelayBetweenFaceEnrollments -- ;
	}
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: GetFaceEnrollmentState
* Description  : This function returns the enrolled state for face
* Arguments    : None
* Return Value : FaceEnroll.ucmEnrollmentState
**************************************************************************************************************************************************************/
uint8_t GetFaceEnrollmentState(void)
{
	return FaceEnroll.ucmEnrollmentState ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: FaceAuthentication
* Description  : This function authenticates face
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void FaceAuthentication(void)
{
	//HVC_RESULT *Result = NULL ;
	uint8_t ucmOutStatus ;
	int32_t slmRet ;
	//Result = (HVC_RESULT *)calloc(1,sizeof(HVC_RESULT));

	memset(&Result , 0 ,sizeof(Result)) ;

	switch(FaceAuthenticate.ucmAuthenticationState)
	{
		case AUTHENTICATE_ENTER_FACE :

			slmRet = HVC_ExecuteEx(TIMEOUT_AUTHENTICATE_FACE ,HVC_ACTIV_FACE_RECOGNITION , 0 ,&Result , &ucmOutStatus ) ;
			if(slmRet EQ ZERO)
			{
				if(Result.fdResult.fcResult[0].recognitionResult.uid > ZERO)
				{
					if(Result.fdResult.fcResult[0].recognitionResult.confidence >= MIN_FACE_MATCH_SCORE)
					{
						SetGreenLedForGreenSuccess() ;
						FaceAuthenticate.ucmAuthenticationState = AUTHENTICATE_SUCCESS_FACE ;
						FaceAuthenticate.ucmUserNo = Result.fdResult.fcResult[0].recognitionResult.uid ;
					}
					else
					{
						SetRedLedForFail() ;
					}
				}
				else
				{
					SetRedLedForFail() ;
				}
			}
			break ;

		case AUTHENTICATE_SUCCESS_FACE :


			break ;

		default :
			   //default
			break ;
	}

	//free(Result) ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: SetupFaceAuthentication
* Description  : This function sets up faceprint for Authentication
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void SetupFaceAuthentication(void)
{
	FaceAuthenticate.ucmUserNo = CLEAR ;
	FaceAuthenticate.ucmAuthenticationState = AUTHENTICATE_ENTER_FACE ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: GetFaceAuthenticationUserNo
* Description  : This function returns the user number to match the face
* Arguments    : None
* Return Value : FaceAuthenticate.ucmUserNo or NOT_AUTHENTICATED
**************************************************************************************************************************************************************/
int8_t GetFaceAuthenticationUserNo(void)
{
	if(FaceAuthenticate.ucmAuthenticationState EQ AUTHENTICATE_SUCCESS_FACE)
	{
		return FaceAuthenticate.ucmUserNo ;
	}
	else
	{
		return NOT_AUTHENTICATED ;
	}
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: GetFaceAuthenticationState
* Description  : This function returns the authentication state for face print
* Arguments    : None
* Return Value : FaceEnroll.ucmAuthenticationState
**************************************************************************************************************************************************************/
uint8_t GetFaceAuthenticationState(void)
{
	return FaceAuthenticate.ucmAuthenticationState ;
}
