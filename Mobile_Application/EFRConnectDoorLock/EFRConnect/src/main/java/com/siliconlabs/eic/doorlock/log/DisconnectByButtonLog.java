package com.siliconlabs.eic.doorlock.log;

import com.siliconlabs.eic.doorlock.other.LogType;

public class DisconnectByButtonLog extends Log {

    public DisconnectByButtonLog(String deviceAddress) {
        setLogTime(getTime());
        setLogInfo(deviceAddress + " Disconnected on UI");
        setLogType(LogType.INFO); //malo wazne
        setDeviceAddress(deviceAddress);
    }

}