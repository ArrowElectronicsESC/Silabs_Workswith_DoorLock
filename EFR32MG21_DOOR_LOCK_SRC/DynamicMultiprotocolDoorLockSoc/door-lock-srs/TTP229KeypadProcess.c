/*
 * TTP229KeypadProcess.c
 *
 *  Created on: July 16, 2020
 *      Author: prassanna.sakore
 */
#include "Global.h"
#include "TTP229KeypadProcess.h"


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: KeypadEnrollment
* Description  : This function enrolls keypad password
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void KeypadEnrollment(void)
{
	int16_t ssmKeyPressed = 0;

	if((KeypadEnroll.ucmEnrollmentState EQ ENROLL_ENTER_PASSWORD) OR
	   (KeypadEnroll.ucmEnrollmentState EQ ENROLL_RE_ENTER_PASSWORD))
	{
		ReadKeys();
		ssmKeyPressed = GetKey() ;
	}

	if((ssmKeyPressed EQ KEYS_NOT_PRESSED) OR (ssmKeyPressed > NO_OF_KEYS_TO_USE))
	{
		return ;
	}

	if(ssmKeyPressed EQ KEY_USED_FOR_ZERO)
	{
		ssmKeyPressed = ZERO ;
	}

	RefreshIdleStateTimer() ;

	switch(KeypadEnroll.ucmEnrollmentState)
	{
		case ENROLL_ENTER_PASSWORD :

				SetGreenLedForUserAction() ;

				KeypadEnroll.usmPassword += ssmKeyPressed ;

				if(KeypadEnroll.ucmPasswordDigitNo < (NO_OF_PASSWORD_DIGITS - 1))
				{
					KeypadEnroll.usmPassword *= 10 ;
				}

				KeypadEnroll.ucmPasswordDigitNo++ ;

				if(KeypadEnroll.ucmPasswordDigitNo >= NO_OF_PASSWORD_DIGITS)
				{
					KeypadEnroll.ucmEnrollmentState = ENROLL_RE_ENTER_PASSWORD ;
					KeypadEnroll.ucmPasswordDigitNo = CLEAR ;
				}


			break ;

		case ENROLL_RE_ENTER_PASSWORD	:

				SetGreenLedForUserAction() ;

				KeypadEnroll.usmReEnterPassword += ssmKeyPressed ;

				if(KeypadEnroll.ucmPasswordDigitNo < (NO_OF_PASSWORD_DIGITS - 1))
				{
					KeypadEnroll.usmReEnterPassword *= 10 ;
				}

				KeypadEnroll.ucmPasswordDigitNo++ ;

				if(KeypadEnroll.ucmPasswordDigitNo >= NO_OF_PASSWORD_DIGITS)
				{
					KeypadEnroll.ucmEnrollmentState = ENROLL_MATCH_PASSWORD ;
					KeypadEnroll.ucmPasswordDigitNo = CLEAR ;
				}

			break ;


		case ENROLL_MATCH_PASSWORD 	:

			if(KeypadEnroll.usmPassword EQ KeypadEnroll.usmReEnterPassword)
			{
				KeypadEnroll.ucmEnrollmentState = ENROLL_SUCCESS_PASSWORD ;
				SetGreenLedForGreenSuccess() ;
			}
			else
			{
				SetRedLedForFail() ;
				KeypadEnroll.usmPassword = CLEAR ;
				KeypadEnroll.usmReEnterPassword = CLEAR ;
				KeypadEnroll.ucmEnrollmentState = ENROLL_ENTER_PASSWORD ;
			}

			break ;

		case ENROLL_SUCCESS_PASSWORD 	:


			break ;

		default :
			   //default
			break ;
	}


}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: SetupKeypadEnrollment
* Description  : This function sets up keypad for enrollment
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void SetupKeypadEnrollment(void)
{
	KeypadEnroll.usmPassword = CLEAR ;
	KeypadEnroll.usmReEnterPassword = CLEAR ;
	KeypadEnroll.ucmEnrollmentState = ENROLL_ENTER_PASSWORD ;
	KeypadEnroll.ucmPasswordDigitNo = CLEAR ;

}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: GetEnrollmentPassword
* Description  : This function returns the enrolled password for keypad
* Arguments    : None
* Return Value : KeypadEnroll.usmPassword
**************************************************************************************************************************************************************/
int16_t GetEnrollmentPassword(void)
{
	if(KeypadEnroll.ucmEnrollmentState EQ ENROLL_SUCCESS_PASSWORD)
	{
		return KeypadEnroll.usmPassword ;
	}
	else
	{
		return NOT_ENROLLED ;
	}

}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: GetEnrollmentState
* Description  : This function returns the enrolled state for keypad
* Arguments    : None
* Return Value : KeypadEnroll.ucmEnrollmentState
**************************************************************************************************************************************************************/
uint8_t GetEnrollmentState(void)
{
	return KeypadEnroll.ucmEnrollmentState ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: KeypadAuthentication
* Description  : This function authenticates keypad password
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void KeypadAuthentication(void)
{
	int16_t ssmKeyPressed = 0;
	uint8_t ucmUser ;

	if(KeypadAuthenticate.ucmAuthenticationState EQ AUTHENTICATE_ENTER_PASSWORD)
	{
		ReadKeys();
		ssmKeyPressed = GetKey() ;
	}

	if((ssmKeyPressed EQ KEYS_NOT_PRESSED) OR (ssmKeyPressed > NO_OF_KEYS_TO_USE))
	{
		return ;
	}

	if(ssmKeyPressed EQ KEY_USED_FOR_ZERO)
	{
		ssmKeyPressed = ZERO ;
	}

	RefreshIdleStateTimer() ;

	switch(KeypadAuthenticate.ucmAuthenticationState)
	{
		case AUTHENTICATE_ENTER_PASSWORD :

			SetGreenLedForUserAction() ;

			KeypadAuthenticate.usmPassword += ssmKeyPressed ;

			if(KeypadAuthenticate.ucmPasswordDigitNo < (NO_OF_PASSWORD_DIGITS - 1))
			{
				KeypadAuthenticate.usmPassword *= 10 ;
			}

			KeypadAuthenticate.ucmPasswordDigitNo++ ;

			if(KeypadAuthenticate.ucmPasswordDigitNo >= NO_OF_PASSWORD_DIGITS)
			{
				KeypadAuthenticate.ucmAuthenticationState = AUTHENTICATE_MATCH_PASSWORD ;
				KeypadAuthenticate.ucmPasswordDigitNo = CLEAR ;
			}

			break ;

		case AUTHENTICATE_MATCH_PASSWORD :

			if(Users.usmCurrentEnrollmentNumber > 0)
			{
				for(ucmUser = 0 ; ucmUser < Users.usmCurrentEnrollmentNumber ; ucmUser++ )
				{
					if(Users.usmKeypadPassword[ucmUser] EQ KeypadAuthenticate.usmPassword)
					{
						KeypadAuthenticate.usmPassword = CLEAR ;
						KeypadAuthenticate.ucmUserNo = (ucmUser + 1) ;
						KeypadAuthenticate.ucmAuthenticationState = AUTHENTICATE_SUCCESS_PASSWORD ;
						SetGreenLedForGreenSuccess() ;
						break ;
					}

					if(ucmUser EQ (Users.usmCurrentEnrollmentNumber - 1))
					{
						KeypadAuthenticate.usmPassword = CLEAR ;
						KeypadAuthenticate.ucmAuthenticationState = AUTHENTICATE_ENTER_PASSWORD ;
						SetRedLedForFail() ;
					}

				}
			}
			else
			{
				KeypadAuthenticate.usmPassword = CLEAR ;
				KeypadAuthenticate.ucmAuthenticationState = AUTHENTICATE_ENTER_PASSWORD ;
				SetRedLedForFail() ;
			}

			break ;

		case AUTHENTICATE_SUCCESS_PASSWORD :


			break ;

		default :
			   //default
			break ;
	}

}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: SetupKeypadAuthentication
* Description  : This function sets up keypad for Authentication
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void SetupKeypadAuthentication(void)
{
	KeypadAuthenticate.usmPassword = CLEAR ;
	KeypadAuthenticate.ucmUserNo = CLEAR ;
	KeypadAuthenticate.ucmAuthenticationState = AUTHENTICATE_ENTER_PASSWORD ;
	KeypadAuthenticate.ucmPasswordDigitNo = CLEAR ;

}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: GetAuthenticationUserNo
* Description  : This function returns the first user number to match the password
* Arguments    : None
* Return Value : KeypadAuthenticate.ucmUserNo or NOT_AUTHENTICATED
**************************************************************************************************************************************************************/
int8_t GetAuthenticationUserNo(void)
{
	if(KeypadAuthenticate.ucmAuthenticationState EQ AUTHENTICATE_SUCCESS_PASSWORD)
	{
		return KeypadAuthenticate.ucmUserNo ;
	}
	else
	{
		return NOT_AUTHENTICATED ;
	}
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: GetAuthenticationState
* Description  : This function returns the authentication state for keypad
* Arguments    : None
* Return Value : KeypadEnroll.ucmAuthenticationState
**************************************************************************************************************************************************************/
uint8_t GetAuthenticationState(void)
{
	return KeypadAuthenticate.ucmAuthenticationState ;
}
