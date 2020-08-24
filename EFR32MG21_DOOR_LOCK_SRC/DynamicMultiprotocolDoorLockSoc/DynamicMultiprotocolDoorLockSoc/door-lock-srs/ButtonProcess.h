/*
 * ButtonProcess.h
 *
 *  Created on: July 10, 2020
 *      Author: prassanna.sakore
 */

#ifndef BUTTONPROCESS_H_
#define BUTTONPROCESS_H_

void ButtonProcess(uint8_t ucvButtonPressedType , uint16_t usvFinalProcessingButton);

void StepButton(uint8_t ucvButtonPressedType);
void ModeButton(uint8_t ucvButtonPressedType);
void ModeStepButton(uint8_t ucvButtonPressedType);



#endif /* BUTTONPROCESS_H_ */
