/*
 * ButtonProcess.c
 *
 *  Created on: July 10, 2020
 *      Author: prassanna.sakore
 */

#include "Global.h"
#include "ButtonProcess.h"

/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: CheckButton
* Description  : This function is used to check the Button press is detected or not.
* 				 If Button press is detected then button process is executed according to the pressed Button.
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void CheckButton(void)
{
	TagButton ButtonData ;
	ButtonData	= Button ;
	
	Button.ucmProcessed	= TRUE ;		//To make ready to take new data for Button press.

	if(ButtonData.ucmProcessed NEQ TRUE)  	//If Button press is detected but not processed
	{
		ButtonProcess(ButtonData.ucmButtonPressedType , ButtonData.usmFinalProcessingButton);			//To process on the pressed button...
		
		Button.usmButtonPressTimeout		= BUTTON_PRESS_TIMEOUT_SEC ;   //Reload the button press Timeout
	}
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: ButtonProcess
* Description  : This function is used to call the Button processing function to process on the pressed Button
* Arguments    : uint8_t ucvButtonPressedType , uint16_t usvFinalProcessingButton
* Return Value : None
**************************************************************************************************************************************************************/
void ButtonProcess(uint8_t ucvButtonPressedType , uint16_t usvFinalProcessingButton)
{
	
	switch(usvFinalProcessingButton) //To process according to the Pressed Button
	{
		case STEP_BUTTON :
			StepButton(ucvButtonPressedType);
			break ;
				
		case MODE_BUTTON	:
			ModeButton(ucvButtonPressedType);
			break ;
			
		case MODE_STEP_BUTTON	:
			ModeStepButton(ucvButtonPressedType);
			break ;
				
        default :
               //default
            break ;     
	}
	
    
}

/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: StepButton
* Description  : This function is used to process the Step Button
* Arguments    : uint8_t ucvButtonPressedType
* Return Value : None
**************************************************************************************************************************************************************/
void StepButton(uint8_t ucvButtonPressedType)
{
    if(ucvButtonPressedType NEQ LONG_PRESS)
    {
    	ToggleStepOrStartEnrollment(FALSE);
    }

	if((Button.ucmOneTimeButtonLongPress EQ TRUE) AND (Button.ucmButtonPressedTime >= ONE_TIME_LONG_PRESS_TIME_Sec))
	{
		ToggleStepOrStartEnrollment(TRUE);
	}


}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: ModeButton
* Description  : This function is used to process the Mode Button
* Arguments    : uint8_t ucvButtonPressedType
* Return Value : None
**************************************************************************************************************************************************************/
void ModeButton(uint8_t ucvButtonPressedType)
{
    if(ucvButtonPressedType EQ LONG_PRESS)
    {
        return ;
    }

	IncrementMode();
}




/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: ModeStepButton
* Description  : This function is used to process the Mode and Step Simultaneous press
* Arguments    : uint8_t ucvButtonPressedType
* Return Value : None
**************************************************************************************************************************************************************/
void ModeStepButton(uint8_t ucvButtonPressedType)
{

	if((Button.ucmOneTimeButtonLongPress EQ TRUE) AND (Button.ucmButtonPressedTime >= ONE_TIME_LONG_PRESS_TIME_Sec))
	{
		ToggleEnrollmentAndStep();
	}

}




