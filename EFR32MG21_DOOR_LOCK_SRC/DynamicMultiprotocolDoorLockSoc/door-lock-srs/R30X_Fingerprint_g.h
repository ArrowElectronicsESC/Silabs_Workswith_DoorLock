/*
 * R30X_Fingerprint_g.h
 *
 *  Created on: July 20, 2020
 *      Author: prassanna.sakore
 */

#ifndef R30X_FINGERPRINT_G_H
#define R30X_FINGERPRINT_G_H


//=========================================================================//
//Response codes from FPS to the commands sent to it
//FPS = Fingerprint Scanner

#define FPS_RESP_OK                      0x00U   //command executed successfully
#define FPS_RESP_RECIEVEERR              0x01U   //packet receive error
#define FPS_RESP_NOFINGER                0x02U   //no finger detected
#define FPS_RESP_ENROLLFAIL              0x03U   //failed to enroll the finger
#define FPS_RESP_OVERDISORDERFAIL        0x04U   //failed to generate character file due to over-disorderly fingerprint image
#define FPS_RESP_OVERWETFAIL             0x05U   //failed to generate character file due to over-wet fingerprint image
#define FPS_RESP_OVERDISORDERFAIL2       0x06U   //failed to generate character file due to over-disorderly fingerprint image
#define FPS_RESP_FEATUREFAIL             0x07U   //failed to generate character file due to over-wet fingerprint image
#define FPS_RESP_DONOTMATCH              0x08U   //fingers do not match
#define FPS_RESP_NOTFOUND                0x09U   //no valid match found
#define FPS_RESP_ENROLLMISMATCH          0x0AU   //failed to combine character files (two character files (images) are used to create a template)
#define FPS_RESP_BADLOCATION             0x0BU   //addressing PageID is beyond the finger library
#define FPS_RESP_INVALIDTEMPLATE         0x0CU   //error when reading template from library or the template is invalid
#define FPS_RESP_TEMPLATEUPLOADFAIL      0x0DU   //error when uploading template
#define FPS_RESP_PACKETACCEPTFAIL        0x0EU   //module can not accept more packets
#define FPS_RESP_IMAGEUPLOADFAIL         0x0FU   //error when uploading image
#define FPS_RESP_TEMPLATEDELETEFAIL      0x10U   //error when deleting template
#define FPS_RESP_DBCLEARFAIL             0x11U   //failed to clear fingerprint library
#define FPS_RESP_WRONGPASSOWRD           0x13U   //wrong password
#define FPS_RESP_IMAGEGENERATEFAIL       0x15U   //fail to generate the image due to lackness of valid primary image
#define FPS_RESP_FLASHWRITEERR           0x18U   //error when writing flash
#define FPS_RESP_NODEFINITIONERR         0x19U   //no definition error
#define FPS_RESP_INVALIDREG              0x1AU   //invalid register number
#define FPS_RESP_INCORRECTCONFIG         0x1BU   //incorrect configuration of register
#define FPS_RESP_WRONGNOTEPADPAGE        0x1CU   //wrong notepad page number
#define FPS_RESP_COMPORTERR              0x1DU   //failed to operate the communication port
#define FPS_RESP_INVALIDREG              0x1AU   //invalid register number
#define FPS_RESP_SECONDSCANNOFINGER      0x41U   //secondary fingerprint scan failed due to no finger
#define FPS_RESP_SECONDENROLLFAIL        0x42U   //failed to enroll second fingerprint
#define FPS_RESP_SECONDFEATUREFAIL       0x43U   //failed to generate character file due to lack of enough features
#define FPS_RESP_SECONDOVERDISORDERFAIL  0x44U   //failed to generate character file due to over-disorderliness
#define FPS_RESP_DUPLICATEFINGERPRINT    0x45U   //duplicate fingerprint

//-------------------------------------------------------------------------//
//Received packet verification status codes from host device

#define FPS_RX_OK                        0x00U  //when the response is correct
#define FPS_RX_BADPACKET                 0x01U  //if the packet received from FPS is badly formatted
#define FPS_RX_WRONG_RESPONSE            0x02U  //unexpected response
#define FPS_RX_TIMEOUT                   0x03U  //when no response was received

//-------------------------------------------------------------------------//
//Packet IDs

#define FPS_ID_STARTCODE              0xEF01U
#define FPS_ID_STARTCODEHIGH          0xEFU
#define FPS_ID_STARTCODELOW           0x01U
#define FPS_ID_COMMANDPACKET          0x01U
#define FPS_ID_DATAPACKET             0x02U
#define FPS_ID_ACKPACKET              0x07U
#define FPS_ID_ENDDATAPACKET          0x08U

//-------------------------------------------------------------------------//
//Command codes

#define FPS_CMD_SCANFINGER            0x01U    //scans the finger and collect finger image
#define FPS_CMD_IMAGETOCHARACTER      0x02U    //generate char file from a single image and store it to one of the buffers
#define FPS_CMD_MATCHTEMPLATES        0x03U    //match two fingerprints precisely
#define FPS_CMD_SEARCHLIBRARY         0x04U    //search the fingerprint library
#define FPS_CMD_GENERATETEMPLATE      0x05U    //combine both character buffers and generate a template
#define FPS_CMD_STORETEMPLATE         0x06U    //store the template on one of the buffers to flash memory
#define FPS_CMD_LOADTEMPLATE          0x07U    //load a template from flash memory to one of the buffers
#define FPS_CMD_EXPORTTEMPLATE        0x08U    //export a template file from buffer to computer
#define FPS_CMD_IMPORTTEMPLATE        0x09U    //import a template file from computer to sensor buffer
#define FPS_CMD_EXPORTIMAGE           0x0AU    //export fingerprint image from buffer to computer
#define FPS_CMD_IMPORTIMAGE           0x0BU    //import an image from computer to sensor buffer
#define FPS_CMD_DELETETEMPLATE        0x0CU    //delete a template from flash memory
#define FPS_CMD_CLEARLIBRARY          0x0DU    //clear fingerprint library
#define FPS_CMD_SETSYSPARA            0x0EU    //set system configuration register
#define FPS_CMD_READSYSPARA           0x0FU    //read system configuration register
#define FPS_CMD_SETPASSWORD           0x12U    //set device password
#define FPS_CMD_VERIFYPASSWORD        0x13U    //verify device password
#define FPS_CMD_GETRANDOMCODE         0x14U    //get random code from device
#define FPS_CMD_SETDEVICEADDRESS      0x15U    //set 4 byte device address
#define FPS_CMD_PORTCONTROL           0x17U    //enable or disable comm port
#define FPS_CMD_WRITENOTEPAD          0x18U    //write to device notepad
#define FPS_CMD_READNOTEPAD           0x19U    //read from device notepad
#define FPS_CMD_HISPEEDSEARCH         0x1BU    //highspeed search of fingerprint
#define FPS_CMD_TEMPLATECOUNT         0x1DU    //read total template count
#define FPS_CMD_SCANANDRANGESEARCH    0x32U    //read total template count
#define FPS_CMD_SCANANDFULLSEARCH     0x34U    //read total template count

#define FPS_NOTEPAD_TIMEOUT            		200  //UART reading timeout in milliseconds
#define FPS_DEFAULT_TIMEOUT                 2000  //UART reading timeout in milliseconds
#define FPS_FULL_SEARCH_TIMEOUT				3000
#define FPS_DEFAULT_BAUDRATE                57600 //9600*6
#define FPS_DEFAULT_RX_DATA_LENGTH          64    //the max length of data in a received packet
#define FPS_DEFAULT_SECURITY_LEVEL          3     //the threshold at which the fingerprints will be matched
#define FPS_DEFAULT_PASSWORD                0xFFFFFFFF
#define FPS_DEFAULT_ADDRESS                 0xFFFFFFFF
#define FPS_BAD_VALUE                       0x1FU //some bad value or paramter was delivered
#define FPS_RX_MIN_NO_OF_BYTES				10
//=========================================================================//

void InitR30XFingerprintVariables (uint32_t password , uint32_t address);
void ResetParameters (void); //initialize and reset and all parameters
uint8_t VerifyPassword (uint32_t password ); //verify the user supplied password
uint8_t SetPassword (uint32_t password);  //set FPS password
uint8_t SetAddress (uint32_t address );  //set FPS address
uint8_t SetBaudrate (uint32_t baud);  //set UART baudrate, default is 57000
uint8_t SetSecurityLevel (uint8_t level); //set the threshold for fingerprint matching
uint8_t SetDataLength (uint16_t length); //set the max length of data in a packet
uint8_t PortControl (uint8_t value);  //turn the comm port on or off
uint8_t SendPacket (uint8_t type, uint8_t command, uint8_t* data , uint16_t dataLength); //assemble and send packets to FPS
uint8_t ReceivePacket (uint16_t usmTimeout) ; //receive packet from FPS
uint8_t ReadSysPara (void); //read FPS system configuration
uint8_t CaptureAndRangeSearch (uint16_t captureTimeout, uint16_t startId, uint16_t count); //scan a finger and search a range of locations
uint8_t CaptureAndFullSearch (void);  //scan a finger and search the entire library
uint8_t GenerateImage (void); //scan a finger, generate an image and store it in the buffer
uint8_t GenerateCharacter (uint8_t bufferId); //generate character file from image
uint8_t GenerateTemplate (void);  //combine the two character files and generate a single template
uint8_t SaveTemplate (uint8_t bufferId, uint16_t location);  //store the template in the buffer to a location in the library
uint8_t LoadTemplate (uint8_t bufferId, uint16_t location); //load a template from library to one of the buffers
uint8_t DeleteTemplate (uint16_t startLocation, uint16_t count);  //delete a set of templates from library
uint8_t ClearLibrary (void);  //delete all templates from library
uint8_t MatchTemplates (void);  //match the templates stored in the two character buffers
uint8_t SearchLibrary (uint8_t bufferId, uint16_t startLocation, uint16_t count); //search the library for a template stored in the buffer
uint8_t GetTemplateCount (void);  //get the total no. of templates in the library
uint16_t GetFingerID(void) ;
uint16_t GetMatchScore(void) ;
uint8_t ReadNotepad (uint8_t ucmPageNumber , uint8_t *ucmData32Bytes) ;
uint8_t WriteNotepad (uint8_t ucmPageNumber , uint8_t *ucmData32Bytes) ;
void ReadFlash(void) ;
void WriteFlash(void) ;
//=========================================================================//


#endif /* R30X_FINGERPRINT_G_H */
