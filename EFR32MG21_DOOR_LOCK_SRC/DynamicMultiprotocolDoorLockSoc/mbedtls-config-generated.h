/***************************************************************************//**
 * @file mbedtls-config-generated.h
 * @brief mbed TLS configuration file. This file is generated do not modify it directly. Please use the mbed TLS setup instead.
 *
 *******************************************************************************
 * # License
 * <b>Copyright 2018 Silicon Laboratories Inc. www.silabs.com</b>
 *******************************************************************************
 *
 * The licensor of this software is Silicon Laboratories Inc. Your use of this
 * software is governed by the terms of Silicon Labs Master Software License
 * Agreement (MSLA) available at
 * www.silabs.com/about-us/legal/master-software-license-agreement. This
 * software is distributed to you in Source Code format and is governed by the
 * sections of the MSLA applicable to Source Code.
 *
 ******************************************************************************/

#ifndef MBEDTLS_CONFIG_GENERATED_H
#define MBEDTLS_CONFIG_GENERATED_H

#if !defined(EMBER_TEST)
#define MBEDTLS_NO_PLATFORM_ENTROPY

#else
// mbedtls/library/entropy_poll.c needs this,
// implicit declaration of function 'syscall' otherwise
#define _GNU_SOURCE
#endif

// Generated content that is coming from contributor plugins

#include <stddef.h>
#include <stdint.h>
// bg_calloc and bg_free are implemented in bgcommon
void bg_free(void *pv);
void *bg_calloc(size_t num, size_t size);
#undef MBEDTLS_FS_IO


#define MBEDTLS_AES_C
#define MBEDTLS_BIGNUM_C
#define MBEDTLS_CCM_C
#define MBEDTLS_CIPHER_C
#define MBEDTLS_CIPHER_MODE_CTR
#define MBEDTLS_CMAC_C
#define MBEDTLS_CTR_DRBG_C
#define CRYPTO_DEVICE_PREEMPTION
#define MBEDTLS_ECP_C
#define MBEDTLS_ECP_DP_SECP256R1_ENABLED
#define MBEDTLS_ECP_NO_FALLBACK
#define MBEDTLS_ECDH_C
#define MBEDTLS_ENTROPY_C
#define MBEDTLS_MICRIUMOS
#define MBEDTLS_PLATFORM_C
#define MBEDTLS_PLATFORM_MEMORY
#define MBEDTLS_SHA256_C
#define MBEDTLS_THREADING_C
#define MBEDTLS_THREADING_ALT

#define MBEDTLS_ENTROPY_MAX_SOURCES 2
#define MBEDTLS_PLATFORM_CALLOC_MACRO bg_calloc
#define MBEDTLS_PLATFORM_FREE_MACRO bg_free

#if defined(MBEDTLS_ENTROPY_C)
// Enable RAIL radio as entropy source by default
// This is going to help on devices that does not have TRNG support by software
// (FR32XG13 (SDID_89) and EFR32XG14 (SDID_95))
#define MBEDTLS_ENTROPY_RAIL_C
#endif

// Inclusion of the Silabs specific device acceleration configuration file.
#if defined(MBEDTLS_DEVICE_ACCELERATION_CONFIG_FILE)
#include MBEDTLS_DEVICE_ACCELERATION_CONFIG_FILE
#endif

// Inclusion of the app specific device acceleration configuration file.
#if defined(MBEDTLS_DEVICE_ACCELERATION_CONFIG_APP_FILE)
#include MBEDTLS_DEVICE_ACCELERATION_CONFIG_APP_FILE
#endif

// Inclusion of the mbed TLS config_check.h header file.
#include "mbedtls/check_config.h"

#endif /* MBEDTLS_CONFIG_GENERATED_H */
