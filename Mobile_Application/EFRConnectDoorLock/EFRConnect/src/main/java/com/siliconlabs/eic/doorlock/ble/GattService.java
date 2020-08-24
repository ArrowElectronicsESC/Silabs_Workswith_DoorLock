package com.siliconlabs.eic.doorlock.ble;

import com.siliconlabs.eic.doorlock.utils.UuidUtils;

import java.util.Locale;
import java.util.UUID;

/**
 * Enumeration of all available Gatt Services.
 * https://developer.bluetooth.org/gatt/services/Pages/ServicesHome.aspx
 */
public enum GattService {
    GenericAccess(0x00001800, "org.bluetooth.service.generic_access",
            GattCharacteristic.DeviceName,
            GattCharacteristic.Appearance),
    GenericAttribute(0x00001801, "org.bluetooth.service.generic_attribute", GattCharacteristic.ServiceChange),
    ImmediateAlert(0x00001802, "org.bluetooth.service.immediate_alert", GattCharacteristic.AlertLevel),
    LinkLoss(0x00001803, "org.bluetooth.service.link_loss", GattCharacteristic.AlertLevel),
    TxPower(0x00001804, "org.bluetooth.service.tx_power", GattCharacteristic.TxPowerLevel),
    HealthThermometer(0x00001809, "org.bluetooth.service.health_thermometer",
            GattCharacteristic.Temperature,
            GattCharacteristic.TemperatureType,
            GattCharacteristic.IntermediateTemperature),
    DeviceInformation(0x0000180a, "org.bluetooth.service.device_information",
            GattCharacteristic.ManufacturerName,
            GattCharacteristic.ModelNumberString,
            GattCharacteristic.SystemId),
    BatteryService(0x0000180f, "org.bluetooth.service.battery_service", GattCharacteristic.BatteryLevel),
    HudDocking(0xdc03900d, "com.sensedriver.service.hud_docking", GattCharacteristic.DockStatus),
    OtaService(0x1d14d6ee, "com.silabs.service.ota",
            GattCharacteristic.OtaControl,
            GattCharacteristic.OtaData,
            GattCharacteristic.FwVersion,
            GattCharacteristic.OtaVersion),
    ThreadLightService("dd1c077d-d306-4b30-846a-4f55cc35767a", "custom.type",
            GattCharacteristic.Light,
            GattCharacteristic.TriggerSource,
            GattCharacteristic.SourceAddress),
    ConnectLightService("62792313-adf2-4fc9-974d-fab9ddf2622c", "custom.type",
            GattCharacteristic.Light,
            GattCharacteristic.TriggerSource,
            GattCharacteristic.SourceAddress),
    ZigbeeLightService("bae55b96-7d19-458d-970c-50613d801bc9", "custom.type",
            GattCharacteristic.Light,
            GattCharacteristic.TriggerSource,
            GattCharacteristic.SourceAddress),
    ProprietaryLightService("63f596e4-b583-4078-bfc3-b04225378713", "custom.type",
            GattCharacteristic.Light,
            GattCharacteristic.TriggerSource,
            GattCharacteristic.SourceAddress),
    RangeTestService("530aa649-17e6-4d62-9f20-9e393b177e63", "custom.type",
            GattCharacteristic.RangeTestDestinationId,
            GattCharacteristic.RangeTestSourceId,
            GattCharacteristic.RangeTestPacketsReceived,
            GattCharacteristic.RangeTestPacketsSend,
            GattCharacteristic.RangeTestPacketsCount,
            GattCharacteristic.RangeTestPacketsRequired,
            GattCharacteristic.RangeTestPER,
            GattCharacteristic.RangeTestMA,
            GattCharacteristic.RangeTestChannel,
            GattCharacteristic.RangeTestRadioMode,
            GattCharacteristic.RangeTestFrequency,
            GattCharacteristic.RangeTestTxPower,
            GattCharacteristic.RangeTestPayload,
            GattCharacteristic.RangeTestMaSize,
            GattCharacteristic.RangeTestLog,
            GattCharacteristic.RangeTestIsRunning),
    DOOR_LOCK("d17fe25a-e3eb-4d6f-84e6-3157fb5e4a60", "custom.type",
            GattCharacteristic.door_lock_state,
            GattCharacteristic.door_lock_req);


    public static final UUID UUID_MASK = UUID.fromString("0000ffff-0000-0000-0000-000000000000");
    private static final String FORMAT_STR = "%08x-0000-1000-8000-00805f9b34fb";
    /**
     * The so-called "Assigned Number" of this service.
     */
    public final UUID number;

    /**
     * The "Type" of this service (fully qualified name).
     */
    public final String type;
    /**
     * Available gatt characteristics for this service.
     */
    public final GattCharacteristic[] availableCharacteristics;

    GattService(int number, String type, GattCharacteristic... availableCharacteristics) {
        this.number = UUID.fromString(String.format(Locale.US, FORMAT_STR, number));
        this.type = type;
        this.availableCharacteristics = availableCharacteristics;

        BluetoothLEGatt.GATT_SERVICE_DESCS.put(number, this);
    }

    GattService(String uuid, String type, GattCharacteristic... availableCharacteristics) {
        this.number = UUID.fromString(uuid);
        this.type = type;
        this.availableCharacteristics = availableCharacteristics;

        int key = UuidUtils.parseIntFromUuidStart(uuid);
        BluetoothLEGatt.GATT_SERVICE_DESCS.put(key, this);
    }

    public static GattService fromUuid(UUID uuid) {
        for (int i = 0; i < values().length; i++) {
            GattService service = values()[i];
            if (service.number.equals(uuid)) {
                return service;
            }
        }
        return null;
    }
}
