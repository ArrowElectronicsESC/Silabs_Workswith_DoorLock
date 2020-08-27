/*
 * R30X_Fingerprint.c
 *
 *  Created on: July 20, 2020
 *      Author: prassanna.sakore
 */
#include "Global.h"
#include "R30X_Fingerprint.h"


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: InitR30XFingerprintVariables
* Description  : Initializes R30X fingerprint variables
* Arguments    : uint32_t ulmPassword, uint32_t ulmAddress
* Return Value : None
**************************************************************************************************************************************************************/
void InitR30XFingerprintVariables (uint32_t ulmPassword , uint32_t ulmAddress)
{

  //storing 32-bit values as 8-bit values in arrays can make many operations easier later
  R30X_Fingerprint.uccDevicePassword[0] = ulmPassword & 0xFFU ; //these can be altered later
  R30X_Fingerprint.uccDevicePassword[1] = (ulmPassword >> 8) & 0xFFU ;
  R30X_Fingerprint.uccDevicePassword[2] = (ulmPassword >> 16) & 0xFFU ;
  R30X_Fingerprint.uccDevicePassword[3] = (ulmPassword >> 24) & 0xFFU ;
  R30X_Fingerprint.ulmDevicePasswordL 	= ulmPassword ;

  R30X_Fingerprint.uccDeviceAddress[0] 	= ulmAddress & 0xFFU ;
  R30X_Fingerprint.uccDeviceAddress[1] 	= (ulmAddress >> 8) & 0xFFU ;
  R30X_Fingerprint.uccDeviceAddress[2] 	= (ulmAddress >> 16) & 0xFFU ;
  R30X_Fingerprint.uccDeviceAddress[3] 	= (ulmAddress >> 24) & 0xFFU ;
  R30X_Fingerprint.ulmDeviceAddressL 	= ulmAddress ;

  R30X_Fingerprint.ucmStartCode[0] = FPS_ID_STARTCODE & 0xFFU ; //packet start marker
  R30X_Fingerprint.ucmStartCode[1] = (FPS_ID_STARTCODE >> 8) & 0xFFU ;

  ResetParameters() ;  //initialize and reset and all parameters
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: ResetParameters
* Description  : Initializes R30X fingerprint parameters
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void ResetParameters (void)
{
  R30X_Fingerprint.ulmDeviceBaudrate 	= FPS_DEFAULT_BAUDRATE ;  //UART speed
  R30X_Fingerprint.ucmSecurityLevel 	= FPS_DEFAULT_SECURITY_LEVEL ;  //threshold level for fingerprint matching
  R30X_Fingerprint.usmDataPacketLength 	= FPS_DEFAULT_RX_DATA_LENGTH ;

  R30X_Fingerprint.ucmTxPacketType 			= FPS_ID_COMMANDPACKET ; //type of packet
  R30X_Fingerprint.ucmTxInstructionCode 	= FPS_CMD_VERIFYPASSWORD ; //
  R30X_Fingerprint.uccTxPacketLength[0] 	= CLEAR ;
  R30X_Fingerprint.uccTxPacketLength[1] 	= CLEAR ;
  R30X_Fingerprint.usmTxPacketLengthL 		= CLEAR ;
  R30X_Fingerprint.ucpTxDataBuffer 			= NULL ; //packet data buffer
  R30X_Fingerprint.usmTxDataBufferLength 	= CLEAR ;
  R30X_Fingerprint.uccTxPacketChecksum[0] 	= CLEAR ;
  R30X_Fingerprint.uccTxPacketChecksum[1] 	= CLEAR ;
  R30X_Fingerprint.usmTxPacketChecksumL 	= CLEAR ;

  R30X_Fingerprint.ucmRxPacketType 			= FPS_ID_COMMANDPACKET ; //type of packet
  R30X_Fingerprint.ucmRxConfirmationCode 	= FPS_CMD_VERIFYPASSWORD ; //
  R30X_Fingerprint.uccRxPacketLength[0] 	= CLEAR ;
  R30X_Fingerprint.uccRxPacketLength[1] 	= CLEAR;
  R30X_Fingerprint.usmRxPacketLengthL 		= CLEAR ;
  R30X_Fingerprint.ucpRxDataBuffer		 	= NULL ; //packet data buffer
  R30X_Fingerprint.ulmRxDataBufferLength 	= CLEAR ;
  R30X_Fingerprint.uccRxPacketChecksum[0] 	= CLEAR ;
  R30X_Fingerprint.uccRxPacketChecksum[1] 	= CLEAR ;
  R30X_Fingerprint.usmRxPacketChecksumL 	= CLEAR ;

  R30X_Fingerprint.usmFingerId 		= CLEAR ; //initialize them
  R30X_Fingerprint.usmMatchScore 	= CLEAR ;
  R30X_Fingerprint.usmTemplateCount = CLEAR ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: SendPacket
* Description  : Send a data packet to the FPS (fingerprint scanner)
* Arguments    : uint8_t ucmType, uint8_t ucmCommand, uint8_t* ucpData, uint16_t usmDataLength
* Return Value : FPS_RX_OK
**************************************************************************************************************************************************************/
uint8_t SendPacket (uint8_t ucmType, uint8_t ucmCommand, uint8_t* ucpData, uint16_t usmDataLength)
{
	int16_t ssmDataIterator ;

	if(ucpData NEQ NULL)
	{  //sometimes there's no additional data except the command
		R30X_Fingerprint.ucpTxDataBuffer = ucpData ;
		R30X_Fingerprint.usmTxDataBufferLength = usmDataLength ;
	}
	else
	{
		R30X_Fingerprint.ucpTxDataBuffer = NULL ;
		R30X_Fingerprint.usmTxDataBufferLength = 0 ;
	}

	R30X_Fingerprint.ucmTxPacketType = ucmType ; //type of packet - 1 byte
	R30X_Fingerprint.ucmTxInstructionCode = ucmCommand ; //instruction code - 1 byte
	R30X_Fingerprint.usmTxPacketLengthL = R30X_Fingerprint.usmTxDataBufferLength + 3 ; //1 byte for command, 2 bytes for checksum
	R30X_Fingerprint.uccTxPacketLength[0] = R30X_Fingerprint.usmTxPacketLengthL & 0xFFU ; //get lower byte
	R30X_Fingerprint.uccTxPacketLength[1] = (R30X_Fingerprint.usmTxPacketLengthL >> 8) & 0xFFU ; //get high byte

	R30X_Fingerprint.usmTxPacketChecksumL = R30X_Fingerprint.ucmTxPacketType + R30X_Fingerprint.uccTxPacketLength[0] + R30X_Fingerprint.uccTxPacketLength[1] + R30X_Fingerprint.ucmTxInstructionCode ; //sum of packet ID and packet length bytes

	for(int i=0 ; i<R30X_Fingerprint.usmTxDataBufferLength ; i++)
	{
		R30X_Fingerprint.usmTxPacketChecksumL += R30X_Fingerprint.ucpTxDataBuffer[i] ; //add rest of the data bytes
	}

	R30X_Fingerprint.uccTxPacketChecksum[0] = R30X_Fingerprint.usmTxPacketChecksumL & 0xFFU ; //get low byte
	R30X_Fingerprint.uccTxPacketChecksum[1] = (R30X_Fingerprint.usmTxPacketChecksumL >> 8) & 0xFFU ; //get high byte

	USART0_TX_ADD_SEND_DATA(&R30X_Fingerprint.ucmStartCode[1] , 1) ;
	USART0_TX_ADD_SEND_DATA(&R30X_Fingerprint.ucmStartCode[0] , 1) ;
	USART0_TX_ADD_SEND_DATA(&R30X_Fingerprint.uccDeviceAddress[3] , 1) ;
	USART0_TX_ADD_SEND_DATA(&R30X_Fingerprint.uccDeviceAddress[2] , 1) ;
	USART0_TX_ADD_SEND_DATA(&R30X_Fingerprint.uccDeviceAddress[1] , 1) ;
	USART0_TX_ADD_SEND_DATA(&R30X_Fingerprint.uccDeviceAddress[0] , 1) ;
	USART0_TX_ADD_SEND_DATA(&R30X_Fingerprint.ucmTxPacketType , 1) ;
	USART0_TX_ADD_SEND_DATA(&R30X_Fingerprint.uccTxPacketLength[1] , 1) ;
	USART0_TX_ADD_SEND_DATA(&R30X_Fingerprint.uccTxPacketLength[0] , 1) ;
	USART0_TX_ADD_SEND_DATA(&R30X_Fingerprint.ucmTxInstructionCode , 1) ;

	for(ssmDataIterator=(R30X_Fingerprint.usmTxDataBufferLength-1) ; ssmDataIterator>=0 ; ssmDataIterator--)
	{
		USART0_TX_ADD_SEND_DATA(&R30X_Fingerprint.ucpTxDataBuffer[ssmDataIterator] , 1) ;
	}


	USART0_TX_ADD_SEND_DATA(&R30X_Fingerprint.uccTxPacketChecksum[1] , 1) ;
	USART0_TX_ADD_SEND_DATA(&R30X_Fingerprint.uccTxPacketChecksum[0] , 1) ;

	USART0_Start_TX() ;

	while(USART0_Get_Transmit_Status() EQ IN_PROGRESS) ;

	return FPS_RX_OK ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: ReceivePacket
* Description  : Receive a data packet from the FPS and extract values
* Arguments    : uint_16t usmTimeout
* Return Value : Error codes
**************************************************************************************************************************************************************/
uint8_t ReceivePacket (uint16_t usmTimeout)
{
  uint16_t usmToken = 0 ; //a position counter/indicator
  uint16_t usmTempSum = 0 ; //temp checksum
  uint8_t* ucpSerialBuffer = USART0_RX_GET_BUFFER() ;

  USART0_RX_SET_TIMEOUT(usmTimeout) ;

  while(USART0_Get_Receive_Status() EQ IN_PROGRESS) ;

  memset(R30X_Fingerprint.uccDataBuffer , CLEAR , FPS_DEFAULT_RX_DATA_LENGTH) ;

  R30X_Fingerprint.ucpRxDataBuffer = R30X_Fingerprint.uccDataBuffer ;


  if(USART0_RX_GET_NO_OF_BYTES() EQ 0)
  {
	  return FPS_RX_TIMEOUT ;
  }

  if(USART0_RX_GET_NO_OF_BYTES() < FPS_RX_MIN_NO_OF_BYTES)
  {
	  return FPS_RX_BADPACKET ;
  }

  //the following loop checks each segments of the data packet for errors, and retrieve the correct ones
  while(true)
  {
    switch (usmToken)
    {
      case 0: //test packet start codes
        if(ucpSerialBuffer[usmToken] EQ R30X_Fingerprint.ucmStartCode[1])
        {
        	break ;
        }
        else
        {
        	return FPS_RX_BADPACKET ;
        }

      case 1:
        if(ucpSerialBuffer[usmToken] EQ R30X_Fingerprint.ucmStartCode[0])
        {
        	break ;
        }
        else
        {
        	return FPS_RX_BADPACKET ;
        }

      case 2: //test device address
        if(ucpSerialBuffer[usmToken] EQ R30X_Fingerprint.uccDeviceAddress[3])
        {
        	break ;
        }
        else
        {
        	return FPS_RX_BADPACKET ;
        }
      
      case 3:
        if(ucpSerialBuffer[usmToken] EQ R30X_Fingerprint.uccDeviceAddress[2])
        {
        	break ;
        }
        else
        {
        	return FPS_RX_BADPACKET ;
        }

      case 4:
        if(ucpSerialBuffer[usmToken] EQ R30X_Fingerprint.uccDeviceAddress[1])
        {
        	break ;
        }
        else
        {
        	return FPS_RX_BADPACKET ;
        }
      
      case 5:
        if(ucpSerialBuffer[usmToken] EQ R30X_Fingerprint.uccDeviceAddress[0])
        {
        	break ;
        }
        else
        {
        	return FPS_RX_BADPACKET ;
        }

      case 6: //test for valid packet type
        if((ucpSerialBuffer[usmToken] EQ FPS_ID_COMMANDPACKET) OR (ucpSerialBuffer[usmToken] EQ FPS_ID_DATAPACKET) OR (ucpSerialBuffer[usmToken] EQ FPS_ID_ACKPACKET) OR (ucpSerialBuffer[usmToken] EQ FPS_ID_ENDDATAPACKET))
        {
        	R30X_Fingerprint.ucmRxPacketType = ucpSerialBuffer[usmToken] ; //store the packet ID to class variable
        	break ;
        }
        else
        {
        	return FPS_RX_WRONG_RESPONSE ;
        }

      case 7: //read packet data length
        if((ucpSerialBuffer[usmToken] > 0) OR (ucpSerialBuffer[usmToken + 1] > 0))
        {
        	R30X_Fingerprint.uccRxPacketLength[0] = ucpSerialBuffer[usmToken + 1] ;  //lower byte
        	R30X_Fingerprint.uccRxPacketLength[1] = ucpSerialBuffer[usmToken] ;  //higher byte
        	R30X_Fingerprint.usmRxPacketLengthL = (uint16_t)(R30X_Fingerprint.uccRxPacketLength[1] << 8) + R30X_Fingerprint.uccRxPacketLength[0] ; //calculate the full length value
        	R30X_Fingerprint.ulmRxDataBufferLength = R30X_Fingerprint.usmRxPacketLengthL - 3 ; //subtract 2 for checksum and 1 for command
        	usmToken++ ; //because we read one additional bytes here
        	break ;
        }

        else
        {
        	return FPS_RX_WRONG_RESPONSE ;
        }

      case 9: //read confirmation or instruction code
    	R30X_Fingerprint.ucmRxConfirmationCode = ucpSerialBuffer[usmToken] ; //the first byte of data will be either instruction or confirmation code
        break ;

      case 10: //read data
        for(int i=0 ; i < R30X_Fingerprint.ulmRxDataBufferLength ; i++)
        {
        	R30X_Fingerprint.ucpRxDataBuffer[(R30X_Fingerprint.ulmRxDataBufferLength - 1) - i] = ucpSerialBuffer[usmToken + i] ; //store low values at start of the rxDataBuffer array
        }
        break ;
      
      case 11: //read checksum
        if(R30X_Fingerprint.ulmRxDataBufferLength EQ 0)
        { //sometimes there's no data other than the confirmation code
        	R30X_Fingerprint.uccRxPacketChecksum[0] = ucpSerialBuffer[usmToken] ; //lower byte
        	R30X_Fingerprint.uccRxPacketChecksum[1] = ucpSerialBuffer[usmToken - 1] ; //high byte
        	R30X_Fingerprint.usmRxPacketChecksumL = (uint16_t)(R30X_Fingerprint.uccRxPacketChecksum[1] << 8) + R30X_Fingerprint.uccRxPacketChecksum[0] ; //calculate L value

        	usmTempSum = R30X_Fingerprint.ucmRxPacketType + R30X_Fingerprint.uccRxPacketLength[0] + R30X_Fingerprint.uccRxPacketLength[1] + R30X_Fingerprint.ucmRxConfirmationCode ;

        	if(R30X_Fingerprint.usmRxPacketChecksumL EQ usmTempSum)
        	{ //check if the calculated checksum matches the received one
        		return FPS_RX_OK ; //packet read success
        	}
        	else
        	{ //if the checksums do not match
        		return FPS_RX_BADPACKET ;  //then that's an error
        	}

          break;
        }
        else if((ucpSerialBuffer[usmToken + (R30X_Fingerprint.ulmRxDataBufferLength-1)] > 0) OR ((ucpSerialBuffer[usmToken + 1 + (R30X_Fingerprint.ulmRxDataBufferLength-1)] > 0)))
        {
        	R30X_Fingerprint.uccRxPacketChecksum[0] = ucpSerialBuffer[usmToken + 1 + (R30X_Fingerprint.ulmRxDataBufferLength-1)] ; //lower byte
        	R30X_Fingerprint.uccRxPacketChecksum[1] = ucpSerialBuffer[usmToken + (R30X_Fingerprint.ulmRxDataBufferLength-1)] ; //high byte
        	R30X_Fingerprint.usmRxPacketChecksumL = (uint16_t)(R30X_Fingerprint.uccRxPacketChecksum[1] << 8) + R30X_Fingerprint.uccRxPacketChecksum[0] ; //calculate L value

        	usmTempSum = R30X_Fingerprint.ucmRxPacketType + R30X_Fingerprint.uccRxPacketLength[0] + R30X_Fingerprint.uccRxPacketLength[1] + R30X_Fingerprint.ucmRxConfirmationCode ;

        	for(int i=0 ; i < R30X_Fingerprint.ulmRxDataBufferLength ; i++)
        	{
        		usmTempSum += R30X_Fingerprint.ucpRxDataBuffer[i] ; //calculate data checksum
        	}

        	if(R30X_Fingerprint.usmRxPacketChecksumL EQ usmTempSum)
        	{ //check if the calculated checksum matches the received one
        		return FPS_RX_OK ; //packet read success
        	}
        	else
        	{ //if the checksums do not match
        		return FPS_RX_BADPACKET ;  //then that's an error
        	}

          break ;
        }
        else
        { //if the checksum received is 0
        	return FPS_RX_BADPACKET ;  //that too an error
        }

        break ;
    
      default :

        break ;
    }
    usmToken++ ; //increment to progressively scan the packet
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: VerifyPassword
* Description  : Verify if the password set by user is correct
* Arguments    : uint32_t ulmPassword
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t VerifyPassword (uint32_t ulmPassword)
{
  uint8_t uccPasswordArray[4] = {0} ;
  uint8_t ucmResponse = 0 ;

  uccPasswordArray[0] = ulmPassword & 0xFFU ;
  uccPasswordArray[1] = (ulmPassword >> 8) & 0xFFU ;
  uccPasswordArray[2] = (ulmPassword >> 16) & 0xFFU ;
  uccPasswordArray[3] = (ulmPassword >> 24) & 0xFFU ;

  SendPacket(FPS_ID_COMMANDPACKET , FPS_CMD_VERIFYPASSWORD , uccPasswordArray , 4) ; //send the command and data

  ucmResponse = ReceivePacket(FPS_DEFAULT_TIMEOUT) ; //read response

  if(ucmResponse EQ FPS_RX_OK)
  { //if the response packet is valid
	  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
	  {
		  R30X_Fingerprint.ulmDevicePasswordL = ulmPassword ;
		  R30X_Fingerprint.uccDevicePassword[0] = uccPasswordArray[0] ; //save the new password as array
		  R30X_Fingerprint.uccDevicePassword[1] = uccPasswordArray[1] ;
		  R30X_Fingerprint.uccDevicePassword[2] = uccPasswordArray[2] ;
		  R30X_Fingerprint.uccDevicePassword[3] = uccPasswordArray[3] ;

		  return FPS_RESP_OK ; //password is correct
	  }
	  else
	  {
		  return R30X_Fingerprint.ucmRxConfirmationCode ;  //password is not correct and so send confirmation code
	  }
  }
  else
  {
	  return ucmResponse ; //return packet receive error code
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: SetPassword
* Description  : Set a new 4 byte password
* Arguments    : uint32_t ulmPassword
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t SetPassword (uint32_t ulmPassword)
{
  uint8_t uccPasswordArray[4] = {0} ;
  uint8_t ucmResponse = 0 ;

  uccPasswordArray[0] = ulmPassword & 0xFFU ;
  uccPasswordArray[1] = (ulmPassword >> 8) & 0xFFU ;
  uccPasswordArray[2] = (ulmPassword >> 16) & 0xFFU ;
  uccPasswordArray[3] = (ulmPassword >> 24) & 0xFFU ;

  SendPacket(FPS_ID_COMMANDPACKET , FPS_CMD_SETPASSWORD , uccPasswordArray , 4) ; //send the command and data

  ucmResponse = ReceivePacket(FPS_DEFAULT_TIMEOUT) ; //read response

  if(ucmResponse EQ FPS_RX_OK)
  { //if the response packet is valid
	  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
	  { //the confrim code will be saved when the response is received
		  R30X_Fingerprint.ulmDevicePasswordL = ulmPassword ; //save the new password (Long)
		  R30X_Fingerprint.uccDevicePassword[0] = uccPasswordArray[0] ; //save the new password as array
		  R30X_Fingerprint.uccDevicePassword[1] = uccPasswordArray[1] ;
		  R30X_Fingerprint.uccDevicePassword[2] = uccPasswordArray[2] ;
		  R30X_Fingerprint.uccDevicePassword[3] = uccPasswordArray[3] ;

		  return FPS_RESP_OK ; //password setting complete
	  }
	  else
	  {
		  return R30X_Fingerprint.ucmRxConfirmationCode ;  //setting was unsuccessful and so send confirmation code
	  }
  }
  else
  {
	  return ucmResponse ; //return packet receive error code
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: SetAddress
* Description  : Set a new 4 byte device address. if the operation is successful, the new address will be saved
* Arguments    : uint32_t ulmAddress
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t SetAddress (uint32_t ulmAddress)
{
  uint8_t uccAddressArray[4] = {0} ; //just so that we do not need to alter the existing address before successfully changing it
  uint8_t ucmResponse = 0 ;

  uccAddressArray[0] = ulmAddress & 0xFF ;
  uccAddressArray[1] = (ulmAddress >> 8) & 0xFF ;
  uccAddressArray[2] = (ulmAddress >> 16) & 0xFF ;
  uccAddressArray[3] = (ulmAddress >> 24) & 0xFF ;

  SendPacket(FPS_ID_COMMANDPACKET , FPS_CMD_SETDEVICEADDRESS , uccAddressArray , 4) ; //send the command and data

  R30X_Fingerprint.ulmDeviceAddressL = ulmAddress ; //save the new address (Long)
  R30X_Fingerprint.uccDeviceAddress[0] = uccAddressArray[0] ; //save the new address as array
  R30X_Fingerprint.uccDeviceAddress[1] = uccAddressArray[1] ;
  R30X_Fingerprint.uccDeviceAddress[2] = uccAddressArray[2] ;
  R30X_Fingerprint.uccDeviceAddress[3] = uccAddressArray[3] ;

  ucmResponse = ReceivePacket(FPS_DEFAULT_TIMEOUT) ; //read response

  if(ucmResponse EQ FPS_RX_OK)
  { //if the response packet is valid
	  if((R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK) OR (R30X_Fingerprint.ucmRxConfirmationCode EQ 0x20U))
	  { //the confrim code will be saved when the response is received
		  return FPS_RESP_OK ; //address setting complete
	  }
	  else
	  {
		  return R30X_Fingerprint.ucmRxConfirmationCode ;  //setting was unsuccessful and so send confirmation code
	  }
  }
  else
  {
	  return ucmResponse ; //return packet receive error code
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: SetBaudrate
* Description  : Change the baudrate and reinitialize the port. The new baudrate will be saved after successful execution
* Arguments    : uint32_t ulmBaud
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t SetBaudrate (uint32_t ulmBaud)
{
  uint8_t ucmBaudNumber = ulmBaud / 9600 ; //check is the baudrate is a multiple of 9600
  uint8_t uccDataArray[2] = {0} ;
  uint8_t ucmResponse = 0 ;

  if((ucmBaudNumber > 0) AND (ucmBaudNumber < 13))
  { //should be between 1 (9600bps) and 12 (115200bps)
	  uccDataArray[0] = ucmBaudNumber ;  //low byte
	  uccDataArray[1] = 4 ; //the code for the system parameter number, 4 means baudrate

	  SendPacket(FPS_ID_COMMANDPACKET, FPS_CMD_SETSYSPARA, uccDataArray, 2) ; //send the command and data

	  ucmResponse = ReceivePacket(FPS_DEFAULT_TIMEOUT) ; //read response

	  if(ucmResponse EQ FPS_RX_OK)
	  { //if the response packet is valid
		  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
		  { //the confirm code will be saved when the response is received
			  R30X_Fingerprint.ulmDeviceBaudrate = ulmBaud ;

			  USART0_Change_Baudrate(R30X_Fingerprint.ulmDeviceBaudrate) ;

			  return FPS_RESP_OK ; //baudrate setting complete
		  }
		  else
		  {
			  return R30X_Fingerprint.ucmRxConfirmationCode ;  //setting was unsuccessful and so send confirmation code
		  }
	  }
	  else
	  {
		  return ucmResponse ; //return packet receive error code
	  }
  }
  else
  {
	  return FPS_BAD_VALUE ;
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: SetSecurityLevel
* Description  : Change the security level - or the threshold for matching two fingerprint templates
* Arguments    : uint8_t ucmLevel
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t SetSecurityLevel (uint8_t ucmLevel)
{
  uint8_t uccDataArray[2] = {0} ;
  uint8_t ucmResponse = 0 ;

  if((ucmLevel > 0) AND (ucmLevel < 6))
  { //should be between 1 and 5
	  uccDataArray[0] = ucmLevel ;  //low byte
	  uccDataArray[1] = 5 ; //the code for the system parameter number, 5 means the security level

	  SendPacket(FPS_ID_COMMANDPACKET , FPS_CMD_SETSYSPARA , uccDataArray , 2) ; //send the command and data

	  ucmResponse = ReceivePacket(FPS_DEFAULT_TIMEOUT) ; //read response

	  if(ucmResponse EQ FPS_RX_OK)
	  { //if the response packet is valid
		  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
		  { //the confirm code will be saved when the response is received
			  R30X_Fingerprint.ucmSecurityLevel = ucmLevel ;  //save new value
			  return FPS_RESP_OK ; //security level setting complete
		  }
		  else
		  {
			  return R30X_Fingerprint.ucmRxConfirmationCode ;  //setting was unsuccessful and so send confirmation code
		  }
	  }
	  else
	  {
		  return ucmResponse ; //return packet receive error code
	  }
  }
  else
  {
	  return FPS_BAD_VALUE ; //the received parameter is invalid
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: SetDataLength
* Description  : Set the max length of data bytes that can be received from the module
* Arguments    : uint16_t usmLength
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t SetDataLength (uint16_t usmLength)
{
  uint8_t uccDataArray[2] = {0} ;
  uint8_t ucmResponse = 0 ;

  if((usmLength EQ 32) OR (usmLength EQ 64) OR (usmLength EQ 128) OR (usmLength EQ 256))
  { //should be 32, 64, 128 or 256 bytes
	  if(usmLength EQ 32)
	  {
		  uccDataArray[0] = 0 ;  //low byte
	  }
	  else if(usmLength EQ 64)
	  {
		  uccDataArray[0] = 1 ;  //low byte
	  }
	  else if(usmLength EQ 128)
	  {
		  uccDataArray[0] = 2 ;  //low byte
	  }
	  else if(usmLength EQ 256)
	  {
		  uccDataArray[0] = 3 ;  //low byte
	  }

	  uccDataArray[1] = 6 ; //the code for the system parameter number

	  SendPacket(FPS_ID_COMMANDPACKET, FPS_CMD_SETSYSPARA, uccDataArray, 2) ; //send the command and data

	  ucmResponse = ReceivePacket(FPS_DEFAULT_TIMEOUT) ; //read response

	  if(ucmResponse EQ FPS_RX_OK)
	  { //if the response packet is valid
		  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
		  { //the confirm code will be saved when the response is received
			  R30X_Fingerprint.usmDataPacketLength = usmLength ;  //save the new data length

			  return FPS_RESP_OK ; //length setting complete
		  }
		  else
		  {
			  return R30X_Fingerprint.ucmRxConfirmationCode ;  //setting was unsuccessful and so send confirmation code
		  }
	  }
	  else
	  {
		  return ucmResponse ; //return packet receive error code
	  }
  }
  else
  {
    return FPS_BAD_VALUE ; //the received parameter is invalid
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: PortControl
* Description  : Turns on/off the communication port
* Arguments    : uint8_t ucmValue
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t PortControl (uint8_t ucmValue)
{
  uint8_t uccDataArray[1] = {0} ;
  uint8_t ucmResponse = 0 ;

  if((ucmValue EQ 0) OR (ucmValue EQ 1))
  { //should be either 1 or 0
	  uccDataArray[0] = ucmValue ;

	  SendPacket(FPS_ID_COMMANDPACKET , FPS_CMD_PORTCONTROL , uccDataArray , 1) ; //send the command and data

	  ucmResponse = ReceivePacket(FPS_DEFAULT_TIMEOUT) ; //read response

	  if(ucmResponse EQ FPS_RX_OK)
	  { //if the response packet is valid
		  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
		  { //the confirm code will be saved when the response is received
			  return FPS_RESP_OK ; //port setting complete
		  }
		  else
		  {
			  return R30X_Fingerprint.ucmRxConfirmationCode ;  //setting was unsuccessful and so send confirmation code
		  }
	  }
	  else
	  {
		  return ucmResponse ; //return packet receive error code
	  }
  }
  else
  {
	  return FPS_BAD_VALUE ; //the received parameter is invalid
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: ReadSysPara
* Description  : Read system configuration
* Arguments    : None
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t ReadSysPara()
{
  uint8_t ucmResponse = 0 ;

  SendPacket(FPS_ID_COMMANDPACKET, FPS_CMD_READSYSPARA , NULL , ZERO) ; //send the command, there's no additional data

  ucmResponse = ReceivePacket(FPS_DEFAULT_TIMEOUT) ; //read response

  if(ucmResponse EQ FPS_RX_OK)
  { //if the response packet is valid
	  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
	  { //the confirm code will be saved when the response is received
		  R30X_Fingerprint.usmStatusRegister = (uint16_t)(R30X_Fingerprint.ucpRxDataBuffer[15] << 8) + R30X_Fingerprint.ucpRxDataBuffer[14] ;  //high byte + low byte
		  R30X_Fingerprint.ucmSecurityLevel = R30X_Fingerprint.ucpRxDataBuffer[8] ;

		  if(R30X_Fingerprint.ucpRxDataBuffer[2] EQ 0)
		  {
			  R30X_Fingerprint.usmDataPacketLength = 32 ;
		  }
		  else if(R30X_Fingerprint.ucpRxDataBuffer[2] EQ 1)
		  {
			  R30X_Fingerprint.usmDataPacketLength = 64 ;
		  }
		  else if(R30X_Fingerprint.ucpRxDataBuffer[2] EQ 2)
		  {
			  R30X_Fingerprint.usmDataPacketLength = 128 ;
		  }
		  else if(R30X_Fingerprint.ucpRxDataBuffer[2] EQ 3)
		  {
			  R30X_Fingerprint.usmDataPacketLength = 256 ;
		  }

		  R30X_Fingerprint.ulmDeviceBaudrate = R30X_Fingerprint.ucpRxDataBuffer[0] * 9600 ;  //baudrate is retrieved as a multiplier

		  return FPS_RESP_OK ;
	  }
	  else
	  {
		  return R30X_Fingerprint.ucmRxConfirmationCode ;  //setting was unsuccessful and so send confirmation code
	  }
  }
  else
  {
	  return ucmResponse ; //return packet receive error code
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: GetTemplateCount
* Description  : Returns the total template count in the flash memory
* Arguments    : None
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t GetTemplateCount()
{
  uint8_t ucmResponse = 0 ;

  SendPacket(FPS_ID_COMMANDPACKET , FPS_CMD_TEMPLATECOUNT , NULL , ZERO) ; //send the command, there's no additional data

  ucmResponse = ReceivePacket(FPS_DEFAULT_TIMEOUT) ; //read response

  if(ucmResponse EQ FPS_RX_OK)
  { //if the response packet is valid
	  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
	  { //the confirm code will be saved when the response is received
		  R30X_Fingerprint.usmTemplateCount = (uint16_t)(R30X_Fingerprint.ucpRxDataBuffer[1] << 8) + R30X_Fingerprint.ucpRxDataBuffer[0] ;  //high byte + low byte

		  return FPS_RESP_OK ;
	  }
	  else
	  {
		  return R30X_Fingerprint.ucmRxConfirmationCode ;  //setting was unsuccessful and so send confirmation code
	  }
  }
  else
  {
	  return ucmResponse ; //return packet receive error code
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: CaptureAndRangeSearch
* Description  : scans the fingerprint and finds a match within specified range
				 timeout = 100-25500 milliseconds
				 startId = #1-#1000
				 range = 1-1000
* Arguments    : uint16_t usmCaptureTimeout, uint16_t usmStartLocation, uint16_t usmCount
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t CaptureAndRangeSearch (uint16_t usmCaptureTimeout , uint16_t usmStartLocation , uint16_t usmCount)
{
  uint8_t uccDataArray[5] = {0} ; //need 5 bytes here
  uint8_t ucmResponse = 0 ;

  if(usmCaptureTimeout > 25500)
  { //25500 is the max timeout the device supports
	  return FPS_BAD_VALUE ;
  }

  if((usmStartLocation > 1000) OR (usmStartLocation < 1))
  { //if not in range (0-999)
	  return FPS_BAD_VALUE ;
  }

  if((usmStartLocation + usmCount) > 1001)
  { //if range overflows
	  return FPS_BAD_VALUE ;
  }

  //generate the data array
  uccDataArray[4] = (uint8_t)(usmCaptureTimeout / 140) ;  //this byte is sent first
  uccDataArray[3] = ((usmStartLocation-1) >> 8) & 0xFFU ;  //high byte
  uccDataArray[2] = (uint8_t)((usmStartLocation-1) & 0xFFU) ;  //low byte
  uccDataArray[1] = (usmCount >> 8) & 0xFFU ; //high byte
  uccDataArray[0] = (uint8_t)(usmCount & 0xFFU) ; //low byte

  SendPacket(FPS_ID_COMMANDPACKET , FPS_CMD_SCANANDRANGESEARCH , uccDataArray , 5) ; //send the command, there's no additional data

  ucmResponse = ReceivePacket(usmCaptureTimeout + 100) ; //read response

  if(ucmResponse EQ FPS_RX_OK)
  { //if the response packet is valid
	  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
	  { //the confirm code will be saved when the response is received
		  R30X_Fingerprint.usmFingerId = (uint16_t)(R30X_Fingerprint.ucpRxDataBuffer[3] << 8) + R30X_Fingerprint.ucpRxDataBuffer[2] ;  //high byte + low byte
		  R30X_Fingerprint.usmFingerId += 1 ;  //because IDs start from #1
		  R30X_Fingerprint.usmMatchScore = (uint16_t)(R30X_Fingerprint.ucpRxDataBuffer[1] << 8) + R30X_Fingerprint.ucpRxDataBuffer[0] ;  //data length will be 4 here

		  return FPS_RESP_OK ;
	  }
	  else
	  {
    	R30X_Fingerprint.usmFingerId = 0 ;
    	R30X_Fingerprint.usmMatchScore = 0 ;

    	return R30X_Fingerprint.ucmRxConfirmationCode ;  //setting was unsuccessful and so send confirmation code
	  }
  }
  else
  {
	  return ucmResponse ; //return packet receive error code
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: CaptureAndFullSearch
* Description  : Scans the fingerprint and finds a match within the full range of library a timeout can not be specified here
* Arguments    : None
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t CaptureAndFullSearch ()
{
  uint8_t ucmResponse = 0 ;

  SendPacket(FPS_ID_COMMANDPACKET , FPS_CMD_SCANANDFULLSEARCH, NULL , ZERO) ; //send the command, there's no additional data

  ucmResponse = ReceivePacket(FPS_FULL_SEARCH_TIMEOUT) ; //read response

  if(ucmResponse EQ FPS_RX_OK)
  { //if the response packet is valid
	  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
	  { //the confirm code will be saved when the response is received
		  R30X_Fingerprint.usmFingerId = (uint16_t)(R30X_Fingerprint.ucpRxDataBuffer[3] << 8) + R30X_Fingerprint.ucpRxDataBuffer[2];  //high byte + low byte
		  R30X_Fingerprint.usmFingerId += 1;  //because IDs start from #1
		  R30X_Fingerprint.usmMatchScore = (uint16_t)(R30X_Fingerprint.ucpRxDataBuffer[1] << 8) + R30X_Fingerprint.ucpRxDataBuffer[0];  //data length will be 4 here

		  return FPS_RESP_OK;
    }
    else
    {
    	R30X_Fingerprint.usmFingerId = 0 ;
    	R30X_Fingerprint.usmMatchScore = 0 ;

    	return R30X_Fingerprint.ucmRxConfirmationCode ;  //setting was unsuccessful and so send confirmation code
    }
  }
  else
  {
	  return ucmResponse ; //return packet receive error code
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: GenerateImage
* Description  : Scan the fingerprint, generate an image and store it in the image buffer
* Arguments    : None
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t GenerateImage ()
{
  uint8_t ucmResponse = 0 ;

  SendPacket(FPS_ID_COMMANDPACKET , FPS_CMD_SCANFINGER , NULL , ZERO); //send the command, there's no additional data

  ucmResponse = ReceivePacket(FPS_DEFAULT_TIMEOUT) ; //read response

  if(ucmResponse EQ FPS_RX_OK)
  { //if the response packet is valid
	  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
	  { //the confirm code will be saved when the response is received
		  return FPS_RESP_OK ; //just the confirmation code only
	  }
	  else
	  {
		  return R30X_Fingerprint.ucmRxConfirmationCode ;  //setting was unsuccessful and so send confirmation code
	  }
  }
  else
  {
	  return ucmResponse ; //return packet receive error code
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: GenerateCharacter
* Description  : Generate a character file from image stored in image buffer and store it in one of the two character buffers
* Arguments    : uint8_t ucmBufferId
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t GenerateCharacter (uint8_t ucmBufferId)
{
  uint8_t uccDataBuffer[1] = {ucmBufferId} ; //create data array
  uint8_t ucmResponse = 0 ;

  if(!((ucmBufferId > 0) AND (ucmBufferId < 3)))
  { //if the value is not 1 or 2
	  return FPS_BAD_VALUE ;
  }
  
  SendPacket(FPS_ID_COMMANDPACKET , FPS_CMD_IMAGETOCHARACTER , uccDataBuffer , 1);

  ucmResponse = ReceivePacket(FPS_DEFAULT_TIMEOUT) ; //read response

  if(ucmResponse EQ FPS_RX_OK)
  { //if the response packet is valid
	  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
	  { //the confirm code will be saved when the response is received
		  return FPS_RESP_OK ; //just the confirmation code only
	  }
	  else
	  {
		  return R30X_Fingerprint.ucmRxConfirmationCode ;  //setting was unsuccessful and so send confirmation code
	  }
  }
  else
  {
	  return ucmResponse ; //return packet receive error code
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: GenerateTemplate
* Description  : Combine the two character files from buffers and generate a template the template will be saved to the both the buffers
* Arguments    : None
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t GenerateTemplate ()
{
  uint8_t ucmResponse = 0 ;

  SendPacket(FPS_ID_COMMANDPACKET , FPS_CMD_GENERATETEMPLATE , NULL , ZERO); //send the command, there's no additional data

  ucmResponse = ReceivePacket(FPS_DEFAULT_TIMEOUT) ; //read response

  if(ucmResponse EQ FPS_RX_OK)
  { //if the response packet is valid
	  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
	  { //the confirm code will be saved when the response is received
		  return FPS_RESP_OK ; //just the confirmation code only
	  }
	  else
	  {
		  return R30X_Fingerprint.ucmRxConfirmationCode ;  //setting was unsuccessful and so send confirmation code
	  }
  }
  else
  {
	  return ucmResponse ; //return packet receive error code
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: SaveTemplate
* Description  : Store the contents of one of the two template (character) buffers to a location on the fingerprint library
* Arguments    : uint8_t ucmBufferId, uint16_t usmLocation
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t SaveTemplate (uint8_t ucmBufferId , uint16_t usmLocation)
{
  uint8_t uccDataArray[3] = {0} ; //create data array
  uint8_t ucmResponse = 0 ;

  if(!((ucmBufferId > 0) AND (ucmBufferId < 3)))
  { //if the value is not 1 or 2
	  return FPS_BAD_VALUE ;
  }

  if((usmLocation > 1000) OR (usmLocation < 1))
  { //if the value is not in range
	  return FPS_BAD_VALUE ;
  }

  uccDataArray[2] = ucmBufferId ;  //highest byte
  uccDataArray[1] = ((usmLocation-1) >> 8) & 0xFFU ; //high byte of location
  uccDataArray[0] = ((usmLocation-1) & 0xFFU) ; //low byte of location

  SendPacket(FPS_ID_COMMANDPACKET , FPS_CMD_STORETEMPLATE , uccDataArray , 3); //send the command and data

  ucmResponse = ReceivePacket(FPS_DEFAULT_TIMEOUT) ; //read response

  if(ucmResponse EQ FPS_RX_OK)
  { //if the response packet is valid
	  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
	  { //the confirm code will be saved when the response is received
		  return FPS_RESP_OK ; //just the confirmation code only
	  }
	  else
	  {
		  return R30X_Fingerprint.ucmRxConfirmationCode ;  //setting was unsuccessful and so send confirmation code
	  }
  }
  else
  {
	  return ucmResponse ; //return packet receive error code
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: LoadTemplate
* Description  : Load the contents from a location on the library to one of the two template/character buffers
* Arguments    : uint8_t ucmBufferId, uint16_t usmLocation
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t LoadTemplate (uint8_t ucmBufferId , uint16_t usmLocation)
{
  uint8_t uccDataArray[3] = {0} ; //create data array
  uint8_t ucmResponse = 0 ;

  if(!((ucmBufferId > 0) AND (ucmBufferId < 3)))
  { //if the value is not 1 or 2
	  return FPS_BAD_VALUE ;
  }

  if((usmLocation > 1000) OR (usmLocation < 1))
  { //if the value is not in range
	  return FPS_BAD_VALUE ;
  }

  uccDataArray[2] = ucmBufferId ;  //highest byte
  uccDataArray[1] = ((usmLocation-1) >> 8) & 0xFFU ; //high byte of location
  uccDataArray[0] = ((usmLocation-1) & 0xFFU) ; //low byte of location

  SendPacket(FPS_ID_COMMANDPACKET , FPS_CMD_LOADTEMPLATE , uccDataArray , 3) ; //send the command and data

  ucmResponse = ReceivePacket(FPS_DEFAULT_TIMEOUT) ; //read response

  if(ucmResponse EQ FPS_RX_OK)
  { //if the response packet is valid
	  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
	  { //the confirm code will be saved when the response is received
		  return FPS_RESP_OK ; //just the confirmation code only
	  }
	  else
	  {
		  return R30X_Fingerprint.ucmRxConfirmationCode ;  //setting was unsuccessful and so send confirmation code
	  }
  }
  else
  {
	  return ucmResponse ; //return packet receive error code
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: DeleteTemplate
* Description  : Delete a set of templates saved in the flash memory
* Arguments    : uint16_t usmStartLocation, uint16_t usmCount
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t DeleteTemplate (uint16_t usmStartLocation , uint16_t usmCount)
{
  uint8_t uccDataArray[4] = {0} ; //create data array
  uint8_t ucmResponse = 0 ;

  if((usmStartLocation > 1000) OR (usmStartLocation < 1))
  { //if the value is not 1 or 2
	  return FPS_BAD_VALUE ;
  }

  if((usmCount + usmStartLocation) > 1001)
  { //if the value is not in range
	  return FPS_BAD_VALUE ;
  }

  uccDataArray[3] = ((usmStartLocation-1) >> 8) & 0xFFU ; //high byte of location
  uccDataArray[2] = ((usmStartLocation-1) & 0xFFU) ; //low byte of location
  uccDataArray[1] = (usmCount >> 8) & 0xFFU ; //high byte of total no. of templates to delete
  uccDataArray[0] = (usmCount & 0xFFU) ; //low byte of count

  SendPacket(FPS_ID_COMMANDPACKET , FPS_CMD_DELETETEMPLATE , uccDataArray , 4) ; //send the command and data

  ucmResponse = ReceivePacket(FPS_DEFAULT_TIMEOUT) ; //read response

  if(ucmResponse EQ FPS_RX_OK)
  { //if the response packet is valid
	  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
	  { //the confirm code will be saved when the response is received
		  return FPS_RESP_OK ; //just the confirmation code only
	  }
	  else
	  {
		  return R30X_Fingerprint.ucmRxConfirmationCode ;  //setting was unsuccessful and so send confirmation code
	  }
  }
  else
  {
	  return ucmResponse ; //return packet receive error code
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: ClearLibrary
* Description  : Deletes all the templates stored in the library
* Arguments    : None
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t ClearLibrary ()
{
  uint8_t ucmResponse = 0 ;

  SendPacket(FPS_ID_COMMANDPACKET , FPS_CMD_CLEARLIBRARY , NULL , ZERO) ; //send the command

  ucmResponse = ReceivePacket(FPS_DEFAULT_TIMEOUT) ; //read response

  if(ucmResponse EQ FPS_RX_OK)
  { //if the response packet is valid
	  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
	  { //the confirm code will be saved when the response is received
		  return FPS_RESP_OK ; //just the confirmation code only
	  }
	  else
	  {
		  return R30X_Fingerprint.ucmRxConfirmationCode ;  //setting was unsuccessful and so send confirmation code
	  }
  }
  else
  {
	  return ucmResponse ; //return packet receive error code
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: MatchTemplates
* Description  : Match the templates stored in the buffers and calculate a match score
* Arguments    : None
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t MatchTemplates ()
{
  uint8_t ucmResponse = 0 ;

  SendPacket(FPS_ID_COMMANDPACKET , FPS_CMD_MATCHTEMPLATES , NULL , ZERO) ; //send the command

  ucmResponse = ReceivePacket(FPS_DEFAULT_TIMEOUT) ; //read response

  if(ucmResponse EQ FPS_RX_OK)
  { //if the response packet is valid
	  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
	  { //the confirm code will be saved when the response is received
		  R30X_Fingerprint.usmMatchScore = (uint16_t)(R30X_Fingerprint.ucpRxDataBuffer[1] << 8) + R30X_Fingerprint.ucpRxDataBuffer[0] ;

		  return FPS_RESP_OK ; //just the confirmation code only
	  }
	  else
	  {
		  return R30X_Fingerprint.ucmRxConfirmationCode ;  //setting was unsuccessful and so send confirmation code
	  }
  }
  else
  {
	  return ucmResponse ; //return packet receive error code
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: SearchLibrary
* Description  : Searches the contents of one of the two char buffers for a match on the fingerprint library throughout a range
* Arguments    : uint8_t ucmBufferId, uint16_t usmStartLocation, uint16_t usmCount
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t SearchLibrary (uint8_t ucmBufferId , uint16_t usmStartLocation , uint16_t usmCount)
{
  uint8_t uccDataArray[5] = {0} ;
  uint8_t ucmResponse = 0 ;

  if(!((ucmBufferId > 0) AND (ucmBufferId < 3)))
  { //if the value is not 1 or 2
	  return FPS_BAD_VALUE ;
  }

  if((usmStartLocation > 1000) OR (usmStartLocation < 1))
  { //if not in range (0-999)
	  return FPS_BAD_VALUE ;
  }

  if((usmStartLocation + usmCount) > 1001)
  { //if range overflows
	  return FPS_BAD_VALUE ;
  }

  uccDataArray[4] = ucmBufferId ;
  uccDataArray[3] = (usmStartLocation >> 8) & 0xFFU ;  //high byte
  uccDataArray[2] = (usmStartLocation & 0xFFU) ; //low byte
  uccDataArray[1] = (usmCount >> 8) & 0xFFU ; //high byte
  uccDataArray[0] = (usmCount & 0xFFU) ; //low byte


  SendPacket(FPS_ID_COMMANDPACKET , FPS_CMD_SEARCHLIBRARY , uccDataArray , 5); //send the command

  ucmResponse = ReceivePacket(FPS_DEFAULT_TIMEOUT) ; //read response

  if(ucmResponse EQ FPS_RX_OK)
  { //if the response packet is valid
	  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
	  { //the confirm code will be saved when the response is received
		  R30X_Fingerprint.usmFingerId = (uint16_t)(R30X_Fingerprint.ucpRxDataBuffer[3] << 8) + R30X_Fingerprint.ucpRxDataBuffer[2] ;  //add high byte and low byte
		  R30X_Fingerprint.usmFingerId += 1 ;  //because IDs start from #1
		  R30X_Fingerprint.usmMatchScore = (uint16_t)(R30X_Fingerprint.ucpRxDataBuffer[1] << 8) + R30X_Fingerprint.ucpRxDataBuffer[0] ;  //add high byte and low byte
      
		  return FPS_RESP_OK ; //just the confirmation code only
	  }
	  else
	  {
      //fingerId = 0 doesn't mean the match was found at location 0
      //instead it means an error. check the confirmation code to determine the problem
		  R30X_Fingerprint.usmFingerId = 0 ;
		  R30X_Fingerprint.usmMatchScore = 0 ;

		  return R30X_Fingerprint.ucmRxConfirmationCode ;
	  }
  }
  else
  {
	  return ucmResponse ; //return packet receive error code
  }
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: ReadNotepad
* Description  : Read Notepad
* Arguments    : uint8_t ucmPageNumber , uint8_t *ucmData32Bytes
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t ReadNotepad (uint8_t ucmPageNumber , uint8_t *ucmData32Bytes)
{
  uint8_t ucmResponse = 0 ;
  uint8_t ucmIterator ;

  if(ucmPageNumber > 15)
  { //if range overflows
	  return FPS_BAD_VALUE ;
  }

  SendPacket(FPS_ID_COMMANDPACKET , FPS_CMD_READNOTEPAD , &ucmPageNumber , 1); //send the command

  ucmResponse = ReceivePacket(FPS_NOTEPAD_TIMEOUT) ; //read response

  if(ucmResponse EQ FPS_RX_OK)
  { //if the response packet is valid
	  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
	  { //the confirm code will be saved when the response is received

		  for(ucmIterator = 0 ; ucmIterator < 32 ; ucmIterator++)
		  {
			  ucmData32Bytes[ucmIterator] = R30X_Fingerprint.ucpRxDataBuffer[ucmIterator] ;
		  }

		  return FPS_RESP_OK ; //just the confirmation code only
	  }
	  else
	  {
		  return R30X_Fingerprint.ucmRxConfirmationCode ;
	  }
  }
  else
  {
	  return ucmResponse ; //return packet receive error code
  }
}

/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: WriteNotepad
* Description  : Write Notepad
* Arguments    : uint8_t ucmPageNumber , uint8_t *ucmData32Bytes
* Return Value : uint8_t ucmResponse
**************************************************************************************************************************************************************/
uint8_t WriteNotepad (uint8_t ucmPageNumber , uint8_t *ucmData32Bytes)
{
  uint8_t uccDataArray[33] = {0} ;
  uint8_t ucmIterator ;
  uint8_t ucmResponse = 0 ;

  if(ucmPageNumber > 15)
  { //if range overflows
	  return FPS_BAD_VALUE ;
  }

  uccDataArray[32] = ucmPageNumber ;

  for(ucmIterator = 0 ; ucmIterator < 32 ; ucmIterator++)
  {
	  uccDataArray[ucmIterator] = ucmData32Bytes[ucmIterator] ;
  }

  SendPacket(FPS_ID_COMMANDPACKET , FPS_CMD_WRITENOTEPAD , uccDataArray , 33); //send the command

  ucmResponse = ReceivePacket(FPS_NOTEPAD_TIMEOUT) ; //read response

  if(ucmResponse EQ FPS_RX_OK)
  { //if the response packet is valid
	  if(R30X_Fingerprint.ucmRxConfirmationCode EQ FPS_RESP_OK)
	  { //the confirm code will be saved when the response is received

		  return FPS_RESP_OK ; //just the confirmation code only
	  }
	  else
	  {
  		  return R30X_Fingerprint.ucmRxConfirmationCode ;
	  }
  }
  else
  {
	  return ucmResponse ; //return packet receive error code
  }
}

/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: GetFingerID
* Description  : Returns finger ID
* Arguments    : None
* Return Value : R30X_Fingerprint.usmFingerId
**************************************************************************************************************************************************************/
uint16_t GetFingerID(void)
{
	return R30X_Fingerprint.usmFingerId ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: GetMatchScore
* Description  : Returns Match Score
* Arguments    : None
* Return Value : R30X_Fingerprint.usmMatchScore
**************************************************************************************************************************************************************/
uint16_t GetMatchScore(void)
{
	return R30X_Fingerprint.usmMatchScore ;
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: ReadFlash
* Description  : Reads Flash Data
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void ReadFlash(void)
{
	uint8_t uccData[32] = {0} ;
	uint8_t ucmResponse , ucmIterator;
	uint8_t ucmPasswordNumber = 0 ;

	ucmResponse = ReadNotepad(0 , uccData) ;

	if(ucmResponse EQ FPS_RESP_OK)
	{
		Users.usmCurrentEnrollmentNumber = (uint16_t)((uccData[0] << 8) | (uccData[1])) ;

		for(ucmIterator = 2 ; ucmIterator < 16 ; ucmIterator+= 2)
		{
			Users.usmKeypadPassword[(ucmIterator - 2) / 2] = (uint16_t)((uccData[ucmIterator] << 8) | uccData[ucmIterator + 1]) ;
		}
	}
	ucmPasswordNumber += 15 ;

	ucmResponse = ReadNotepad(1 , uccData) ;

	if(ucmResponse EQ FPS_RESP_OK)
	{
		for(ucmIterator = 0 ; ucmIterator < 16 ; ucmIterator+= 2)
		{
			Users.usmKeypadPassword[((ucmIterator) / 2) + ucmPasswordNumber] = (uint16_t)((uccData[ucmIterator] << 8) | uccData[ucmIterator + 1]) ;
		}
	}
	ucmPasswordNumber += 16 ;

	ucmResponse = ReadNotepad(2 , uccData) ;

	if(ucmResponse EQ FPS_RESP_OK)
	{
		for(ucmIterator = 0 ; ucmIterator < 16 ; ucmIterator+= 2)
		{
			Users.usmKeypadPassword[((ucmIterator) / 2) + ucmPasswordNumber] = (uint16_t)((uccData[ucmIterator] << 8) | uccData[ucmIterator + 1]) ;
		}
	}
	ucmPasswordNumber += 16 ;

	ucmResponse = ReadNotepad(3 , uccData) ;

	if(ucmResponse EQ FPS_RESP_OK)
	{
		for(ucmIterator = 0 ; ucmIterator < 16 ; ucmIterator+= 2)
		{
			Users.usmKeypadPassword[((ucmIterator) / 2) + ucmPasswordNumber] = (uint16_t)((uccData[ucmIterator] << 8) | uccData[ucmIterator + 1]) ;
		}
	}
	ucmPasswordNumber += 16 ;

	ucmResponse = ReadNotepad(4 , uccData) ;

	if(ucmResponse EQ FPS_RESP_OK)
	{
		for(ucmIterator = 0 ; ucmIterator < 16 ; ucmIterator+= 2)
		{
			Users.usmKeypadPassword[((ucmIterator) / 2) + ucmPasswordNumber] = (uint16_t)((uccData[ucmIterator] << 8) | uccData[ucmIterator + 1]) ;
		}
	}
	ucmPasswordNumber += 16 ;

	ucmResponse = ReadNotepad(5 , uccData) ;

	if(ucmResponse EQ FPS_RESP_OK)
	{
		for(ucmIterator = 0 ; ucmIterator < 16 ; ucmIterator+= 2)
		{
			Users.usmKeypadPassword[((ucmIterator) / 2) + ucmPasswordNumber] = (uint16_t)((uccData[ucmIterator] << 8) | uccData[ucmIterator + 1]) ;
		}
	}
	ucmPasswordNumber += 16 ;

	ucmResponse = ReadNotepad(6 , uccData) ;

	if(ucmResponse EQ FPS_RESP_OK)
	{
		for(ucmIterator = 0 ; ucmIterator < 5 ; ucmIterator+= 2)
		{
			Users.usmKeypadPassword[((ucmIterator) / 2) + ucmPasswordNumber] = (uint16_t)((uccData[ucmIterator] << 8) | uccData[ucmIterator + 1]) ;
		}
	}
}


/*************************************************************************************************************************************************************
* Author 	   : Prassanna Sakore
* Function Name: WriteFlash
* Description  : Write Flash Data
* Arguments    : None
* Return Value : None
**************************************************************************************************************************************************************/
void WriteFlash(void)
{
	uint8_t uccData[32] = {0};
	uint8_t ucmResponse , ucmIterator;
	uint8_t ucmPasswordNumber = 0 ;

	uccData[0] = (uint8_t)((Users.usmCurrentEnrollmentNumber >> 8) & 0xFF) ;
	uccData[1] = (uint8_t)(Users.usmCurrentEnrollmentNumber & 0xFF) ;

	for(ucmIterator = 2 ; ucmIterator < 16 ; ucmIterator+= 2)
	{
		uccData[ucmIterator] = (uint8_t)((Users.usmKeypadPassword[(ucmIterator - 2) / 2] >> 8) & 0xFF) ;
		uccData[ucmIterator + 1] =	(uint8_t)(Users.usmKeypadPassword[(ucmIterator - 2) / 2] & 0xFF) ;
	}

	ucmResponse = WriteNotepad(0 , uccData) ;

	if(ucmResponse EQ FPS_RESP_OK)
	{
	}

	ucmPasswordNumber += 15 ;


	for(ucmIterator = 0 ; ucmIterator < 16 ; ucmIterator+= 2)
	{
		uccData[ucmIterator] = (uint8_t)((Users.usmKeypadPassword[((ucmIterator) / 2) + ucmPasswordNumber] >> 8) & 0xFF) ;
		uccData[ucmIterator + 1] =	(uint8_t)(Users.usmKeypadPassword[((ucmIterator) / 2) + ucmPasswordNumber] & 0xFF) ;
	}

	ucmResponse = WriteNotepad(1 , uccData) ;

	if(ucmResponse EQ FPS_RESP_OK)
	{
	}


	ucmPasswordNumber += 16 ;

	for(ucmIterator = 0 ; ucmIterator < 16 ; ucmIterator+= 2)
	{
		uccData[ucmIterator] = (uint8_t)((Users.usmKeypadPassword[((ucmIterator) / 2) + ucmPasswordNumber] >> 8) & 0xFF) ;
		uccData[ucmIterator + 1] =	(uint8_t)(Users.usmKeypadPassword[((ucmIterator) / 2) + ucmPasswordNumber] & 0xFF) ;
	}

	ucmResponse = WriteNotepad(2 , uccData) ;

	if(ucmResponse EQ FPS_RESP_OK)
	{
	}

	ucmPasswordNumber += 16 ;

	for(ucmIterator = 0 ; ucmIterator < 16 ; ucmIterator+= 2)
	{
		uccData[ucmIterator] = (uint8_t)((Users.usmKeypadPassword[((ucmIterator) / 2) + ucmPasswordNumber] >> 8) & 0xFF) ;
		uccData[ucmIterator + 1] =	(uint8_t)(Users.usmKeypadPassword[((ucmIterator) / 2) + ucmPasswordNumber] & 0xFF) ;
	}

	ucmResponse = WriteNotepad(3 , uccData) ;

	if(ucmResponse EQ FPS_RESP_OK)
	{
	}

	ucmPasswordNumber += 16 ;

	for(ucmIterator = 0 ; ucmIterator < 16 ; ucmIterator+= 2)
	{
		uccData[ucmIterator] = (uint8_t)((Users.usmKeypadPassword[((ucmIterator) / 2) + ucmPasswordNumber] >> 8) & 0xFF) ;
		uccData[ucmIterator + 1] =	(uint8_t)(Users.usmKeypadPassword[((ucmIterator) / 2) + ucmPasswordNumber] & 0xFF) ;
	}

	ucmResponse = WriteNotepad(4 , uccData) ;

	if(ucmResponse EQ FPS_RESP_OK)
	{
	}

	ucmPasswordNumber += 16 ;

	for(ucmIterator = 0 ; ucmIterator < 16 ; ucmIterator+= 2)
	{
		uccData[ucmIterator] = (uint8_t)((Users.usmKeypadPassword[((ucmIterator) / 2) + ucmPasswordNumber] >> 8) & 0xFF) ;
		uccData[ucmIterator + 1] =	(uint8_t)(Users.usmKeypadPassword[((ucmIterator) / 2) + ucmPasswordNumber] & 0xFF) ;
	}

	ucmResponse = WriteNotepad(5 , uccData) ;

	if(ucmResponse EQ FPS_RESP_OK)
	{
	}

	ucmPasswordNumber += 16 ;

	for(ucmIterator = 0 ; ucmIterator < 5 ; ucmIterator+= 2)
	{
		uccData[ucmIterator] = (uint8_t)((Users.usmKeypadPassword[((ucmIterator) / 2) + ucmPasswordNumber] >> 8) & 0xFF) ;
		uccData[ucmIterator + 1] =	(uint8_t)(Users.usmKeypadPassword[((ucmIterator) / 2) + ucmPasswordNumber] & 0xFF) ;
	}

	ucmResponse = WriteNotepad(6 , uccData) ;

	if(ucmResponse EQ FPS_RESP_OK)
	{
	}
}
