package com.siliconlabs.eic.doorlock.toolbars;

import com.siliconlabs.eic.doorlock.utils.FilterDeviceParams;

public interface ToolbarCallback {
    void close();

    void submit(FilterDeviceParams filterDeviceParams, boolean close);

}
