################################################################################
# Automatically-generated file. Do not edit!
################################################################################

export WINEDEBUG=-all

-include ../makefile.init

RM := rm -rf

# All of the sources participating in the build are defined here
-include sources.mk
-include zll-utility-server/subdir.mk
-include zll-scenes-server/subdir.mk
-include zll-on-off-server/subdir.mk
-include zll-identify-server/subdir.mk
-include zll-commissioning-server/subdir.mk
-include zll-commissioning-common/subdir.mk
-include zcl-framework-core/subdir.mk
-include strong-random/subdir.mk
-include serial/EFR32/subdir.mk
-include serial/subdir.mk
-include scenes/subdir.mk
-include scan-dispatch/subdir.mk
-include reporting/subdir.mk
-include on-off/subdir.mk
-include nvm3/EFR32/subdir.mk
-include nvm3/subdir.mk
-include network-creator-security/subdir.mk
-include network-creator/subdir.mk
-include micrium-rtos/EFR32/subdir.mk
-include micrium-rtos/subdir.mk
-include mbedtls/EFR32/subdir.mk
-include mbedtls-multiprotocol/subdir.mk
-include interpan/subdir.mk
-include idle-sleep/subdir.mk
-include identify-feedback/subdir.mk
-include identify/subdir.mk
-include hal-library/EFR32/subdir.mk
-include groups-server/subdir.mk
-include green-power-common/subdir.mk
-include green-power-client/subdir.mk
-include form-and-join/subdir.mk
-include find-and-bind-target/subdir.mk
-include external-generated-files/subdir.mk
-include emlib/subdir.mk
-include emdrv/subdir.mk
-include ember-minimal-printf/subdir.mk
-include efr32/subdir.mk
-include eeprom/subdir.mk
-include door-lock-srs/subdir.mk
-include dmp-ui-demo-stub/subdir.mk
-include dmp-tuning/subdir.mk
-include debug-jtag/EFR32/subdir.mk
-include debug-basic-library/EFR32/subdir.mk
-include counters/subdir.mk
-include concentrator/subdir.mk
-include coexistence-stub/EFR32/subdir.mk
-include ccm-encryption/subdir.mk
-include ble/subdir.mk
-include antenna-stub/subdir.mk
-include address-table/subdir.mk
-include adc/EFR32/subdir.mk
-include subdir.mk
-include objects.mk

ifneq ($(MAKECMDGOALS),clean)
ifneq ($(strip $(ASM_UPPER_DEPS)),)
-include $(ASM_UPPER_DEPS)
endif
ifneq ($(strip $(MSA_UPPER_DEPS)),)
-include $(MSA_UPPER_DEPS)
endif
ifneq ($(strip $(ASM_DEPS)),)
-include $(ASM_DEPS)
endif
ifneq ($(strip $(S79_DEPS)),)
-include $(S79_DEPS)
endif
ifneq ($(strip $(S_DEPS)),)
-include $(S_DEPS)
endif
ifneq ($(strip $(S_UPPER_DEPS)),)
-include $(S_UPPER_DEPS)
endif
ifneq ($(strip $(MSA_DEPS)),)
-include $(MSA_DEPS)
endif
ifneq ($(strip $(C_DEPS)),)
-include $(C_DEPS)
endif
ifneq ($(strip $(S79_UPPER_DEPS)),)
-include $(S79_UPPER_DEPS)
endif
endif

-include ../makefile.defs

# Add inputs and outputs from these tool invocations to the build variables 

# All Target
all: pre-build main-build

# Main-build Target
main-build: DynamicMultiprotocolDoorLockSoc.out

# Tool invocations
DynamicMultiprotocolDoorLockSoc.out: $(OBJS) $(USER_OBJS)
	@echo 'Building target: $@'
	@echo 'Invoking: IAR Linker for ARM'
	@echo 'Adding linker inputs to ilinkCommandLine_DynamicMultiprotocolDoorLockSoc.out.xcl'
	echo. $(foreach file,$(OBJS), $(file)) >> ilinkCommandLine_DynamicMultiprotocolDoorLockSoc.out.xcl
	echo. $(foreach file,$(USER_OBJS), $(file)) >> ilinkCommandLine_DynamicMultiprotocolDoorLockSoc.out.xcl
	@echo 'Invoking linker reading args from file: ilinkCommandLine_DynamicMultiprotocolDoorLockSoc.out.xcl'
	ilinkarm -f "C:\Users\hardik.patel\SimplicityStudio\v4_workspace\DynamicMultiprotocolDoorLockSoc\IAR ARM - Default\ilinkCommandLine_DynamicMultiprotocolDoorLockSoc.out.xcl" 
	ielftool --srec --verbose "DynamicMultiprotocolDoorLockSoc.out" "DynamicMultiprotocolDoorLockSoc.s37"  
	ielftool --ihex --verbose "DynamicMultiprotocolDoorLockSoc.out" "DynamicMultiprotocolDoorLockSoc.hex"  
	ielftool --bin --verbose "DynamicMultiprotocolDoorLockSoc.out" "DynamicMultiprotocolDoorLockSoc.bin"
	@echo 'Finished building target: $@'
	@echo ' '
	$(MAKE) --no-print-directory post-build

# Other Targets
clean:
	-$(RM) $(ASM_UPPER_DEPS)$(MSA_UPPER_DEPS)$(EXECUTABLES)$(OBJS)$(ASM_DEPS)$(S79_DEPS)$(S_DEPS)$(S_UPPER_DEPS)$(MSA_DEPS)$(C_DEPS)$(S79_UPPER_DEPS) DynamicMultiprotocolDoorLockSoc.out
	-@echo ' '

pre-build:
	-cmd //c 'C:\Users\HARDIK~1.PAT\SIMPLI~1\V4_WOR~1\DYNAMI~2/DynamicMultiprotocolDoorLockSoc-prebuild.bat' 'DynamicMultiprotocolDoorLockSoc' 'C:/Users/hardik.patel/SimplicityStudio/v4_workspace/DynamicMultiprotocolDoorLockSoc' 'C:/Program Files (x86)/IAR Systems/Embedded Workbench 8.4/arm' 'C:\SiliconLabs\SimplicityStudio\v4'
	-@echo ' '

post-build:
	-cmd //c 'C:\Users\HARDIK~1.PAT\SIMPLI~1\V4_WOR~1\DYNAMI~2/DynamicMultiprotocolDoorLockSoc-postbuild.bat' 'DynamicMultiprotocolDoorLockSoc' 'C:/Users/hardik.patel/SimplicityStudio/v4_workspace/DynamicMultiprotocolDoorLockSoc' 'C:/Program Files (x86)/IAR Systems/Embedded Workbench 8.4/arm' 'C:\SiliconLabs\SimplicityStudio\v4'
	-@echo ' '

.PHONY: all clean dependents
.SECONDARY: main-build pre-build post-build

-include ../makefile.targets
