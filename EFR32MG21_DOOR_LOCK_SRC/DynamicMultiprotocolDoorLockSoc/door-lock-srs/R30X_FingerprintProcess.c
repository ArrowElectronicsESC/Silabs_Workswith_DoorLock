/*
 * R30X_FingerprintProcess.c
 *
 *  Created on: July 24, 2020
 *      Author: prassanna.sakore
 */
#include "Global.h"
#include "R30X_FingerprintProcess.h"


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: FingerPrintEnrollment
* Description  : This function enrolls Finger Print
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void FingerPrintEnrollment(void)
{

	switch(FingerEnroll.ucmEnrollmentState)
	{
		case ENROLL_ENTER_FINGER :

			if(FingerEnroll.usmDelayBetweenFingerEnrollments EQ ZERO)
			{
				if(GenerateImage() EQ FPS_RX_OK)
				{
					if(GenerateCharacter(1) EQ FPS_RESP_OK)
					{
						RefreshIdleStateTimer() ;
						SetGreenLedForGreenSuccess() ;
						FingerEnroll.ucmEnrollmentState = ENROLL_RE_ENTER_FINGER ;
						FingerEnroll.usmDelayBetweenFingerEnrollments = SCAN_TIME_BETWEEN_TWO_FINGERS ;
					}
					else
					{
						SetRedLedForFail() ;
					}
				}
			}

			break ;

		case ENROLL_RE_ENTER_FINGER	:

			if(FingerEnroll.usmDelayBetweenFingerEnrollments EQ ZERO)
			{
				if(GenerateImage() EQ FPS_RX_OK)
				{
					if(GenerateCharacter(2) EQ FPS_RESP_OK)
					{
						if(GenerateTemplate() EQ FPS_RESP_OK)
						{
							if(SaveTemplate(1, (Users.usmCurrentEnrollmentNumber + 1)) EQ FPS_RESP_OK)
							{
								RefreshIdleStateTimer() ;
								SetGreenLedForGreenSuccess() ;
								FingerEnroll.ucmEnrollmentState = ENROLL_SUCCESS_FINGER ;
							}
							else
							{
								FingerEnroll.usmDelayBetweenFingerEnrollments = SCAN_TIME_BETWEEN_TWO_FINGERS ;
								FingerEnroll.ucmEnrollmentState = ENROLL_ENTER_FINGER ;
								SetRedLedForFail() ;
							}
						}
						else
						{
							FingerEnroll.usmDelayBetweenFingerEnrollments = SCAN_TIME_BETWEEN_TWO_FINGERS ;
							FingerEnroll.ucmEnrollmentState = ENROLL_ENTER_FINGER ;
							SetRedLedForFail() ;
						}
					}
					else
					{
						FingerEnroll.usmDelayBetweenFingerEnrollments = SCAN_TIME_BETWEEN_TWO_FINGERS ;
						FingerEnroll.ucmEnrollmentState = ENROLL_ENTER_FINGER ;
						SetRedLedForFail() ;
					}
				}

			}

			break ;


		case ENROLL_SUCCESS_FINGER 	:



			break ;

		default :
			   //default
			break ;
	}


}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: SetupFingerEnrollment
* Description  : This function sets up finger process for enrollment
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void SetupFingerEnrollment(void)
{

	FingerEnroll.ucmEnrollmentState = ENROLL_ENTER_FINGER ;
	FingerEnroll.usmDelayBetweenFingerEnrollments = SCAN_TIME_BETWEEN_TWO_FINGERS ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: UpadateDelayBetweenFingerEnrollment
* Description  : This function updates delay between two finger scans
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void UpadateDelayBetweenFingerEnrollment(void)
{
	if(FingerEnroll.usmDelayBetweenFingerEnrollments)
	{
		FingerEnroll.usmDelayBetweenFingerEnrollments -- ;
	}
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: GetFingerEnrollmentState
* Description  : This function returns the enrolled state for finger
* Arguments    : None
* Return Value : FingerEnroll.ucmEnrollmentState
**************************************************************************************************************************************************************/
uint8_t GetFingerEnrollmentState(void)
{
	return FingerEnroll.ucmEnrollmentState ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: FingerAuthentication
* Description  : This function authenticates finger
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void FingerAuthentication(void)
{
	uint8_t ucmResponse = 0 ;

	switch(FingerAuthenticate.ucmAuthenticationState)
	{
		case AUTHENTICATE_ENTER_FINGER :

				if(GenerateImage() EQ FPS_RX_OK)
				{
					if(GenerateCharacter(1) EQ FPS_RESP_OK)
					{
						ucmResponse = SearchLibrary(1, 1, (Users.usmCurrentEnrollmentNumber)) ;

						if(ucmResponse EQ FPS_RESP_OK)
						{
							if(GetMatchScore() >= MIN_FINGER_MATCH_SCORE)
							{
								SetGreenLedForGreenSuccess() ;
								FingerAuthenticate.ucmAuthenticationState = AUTHENTICATE_SUCCESS_FINGER ;
								FingerAuthenticate.ucmUserNo = GetFingerID() ;
							}
							else
							{
								SetRedLedForFail() ;
							}
						}
						else if(ucmResponse NEQ FPS_RX_TIMEOUT)
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

		case AUTHENTICATE_SUCCESS_FINGER :


			break ;

		default :
			   //default
			break ;
	}

}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: SetupFingerAuthentication
* Description  : This function sets up fingerprint for Authentication
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void SetupFingerAuthentication(void)
{
	FingerAuthenticate.ucmUserNo = CLEAR ;
	FingerAuthenticate.ucmAuthenticationState = AUTHENTICATE_ENTER_FINGER ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: GetFingerAuthenticationUserNo
* Description  : This function returns the user number to match the finger
* Arguments    : None
* Return Value : FingerAuthenticate.ucmUserNo or NOT_AUTHENTICATED
**************************************************************************************************************************************************************/
int8_t GetFingerAuthenticationUserNo(void)
{
	if(FingerAuthenticate.ucmAuthenticationState EQ AUTHENTICATE_SUCCESS_FINGER)
	{
		return FingerAuthenticate.ucmUserNo ;
	}
	else
	{
		return NOT_AUTHENTICATED ;
	}
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: GetFingerAuthenticationState
* Description  : This function returns the authentication state for finger print
* Arguments    : None
* Return Value : FingerEnroll.ucmAuthenticationState
**************************************************************************************************************************************************************/
uint8_t GetFingerAuthenticationState(void)
{
	return FingerAuthenticate.ucmAuthenticationState ;
}
