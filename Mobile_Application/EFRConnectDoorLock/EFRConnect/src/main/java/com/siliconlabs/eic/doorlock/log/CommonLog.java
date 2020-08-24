package com.siliconlabs.eic.doorlock.log;

import com.siliconlabs.eic.doorlock.other.LogType;

public class CommonLog extends Log {

    public CommonLog(String value, String deviceAddress) {
        setLogTime(getTime());
        setLogInfo(value);
        setLogType(LogType.INFO); //malo wazne
        setDeviceAddress(deviceAddress);
    }
}
