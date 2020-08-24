/*
 * R30X_Fingerprint.h
 *
 *  Created on: July 20, 2020
 *      Author: prassanna.sakore
 */

#ifndef R30X_FINGERPRINT_H
#define R30X_FINGERPRINT_H

typedef struct
{
  //common parameters
  uint32_t ulmDevicePasswordL; //32-bit single value version of password (L = long)
  uint32_t ulmDeviceAddressL;  //module's address
  uint16_t usmStartCodeL; //packet start marker
  uint8_t uccDevicePassword[4]; //array version of password
  uint8_t uccDeviceAddress[4]; //device address as an array
  uint8_t ucmStartCode[2]; //packet start marker
  uint32_t ulmDeviceBaudrate;  //UART speed
  uint8_t ucmSecurityLevel;  //threshold level for finger print matching
  uint16_t usmDataPacketLength; //the max length of data in packet. can be 32, 64, 128 or 256

  //transmit packet parameters
  uint8_t ucmTxPacketType; //type of packet
  uint16_t usmTxPacketLengthL; //length of packet (Data + Checksum)
  uint8_t ucmTxInstructionCode;  //instruction to be sent to FPS
  uint16_t usmTxPacketChecksumL; //checksum long value
  uint8_t uccTxPacketLength[2];  //packet length as an array
  uint8_t* ucpTxDataBuffer; //packet data buffer
  uint16_t usmTxDataBufferLength; //length of actual data in a packet
  uint8_t uccTxPacketChecksum[2];  //packet checksum as an array

  //receive packet parameters
  uint8_t ucmRxPacketType; //type of packet
  uint16_t usmRxPacketLengthL; //packet length long
  uint8_t ucmRxConfirmationCode; //the return codes from the FPS
  uint16_t usmRxPacketChecksumL; //packet checksum long
  uint8_t uccRxPacketLength[2];  //packet length as an array
  uint8_t* ucpRxDataBuffer; //packet data buffer
  uint32_t ulmRxDataBufferLength;  //the length of the data only. this doesn't include instruction or confirmation code
  uint8_t uccRxPacketChecksum[2];  //packet checksum as array

  uint16_t usmFingerId; //location of finger print in the library
  uint16_t usmMatchScore;  //the match score of comparison of two finger prints
  uint16_t usmTemplateCount; //total number of finger print templates in the library
  uint16_t usmStatusRegister;  //contents of the FPS status register

  uint8_t uccDataBuffer[FPS_DEFAULT_RX_DATA_LENGTH] ;

}TagR30X_Fingerprint ;

TagR30X_Fingerprint R30X_Fingerprint ;

#endif /* R30X_FINGERPRINT_H */

