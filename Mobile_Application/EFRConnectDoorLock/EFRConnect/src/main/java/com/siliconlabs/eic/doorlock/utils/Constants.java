package com.siliconlabs.eic.doorlock.utils;

import android.view.MenuItem;

import com.siliconlabs.eic.doorlock.log.Log;

import java.util.LinkedList;
import java.util.List;

public class Constants {

    public static final String OTA_SERVICE = "OTA Service";
    public static final String NA = "N/A";
    public static final String ABOUT_DIALOG_HTML_ASSET_FILE_PATH = "file:///android_asset/about.html";
    public static final int TOOLBAR_OPEN_PERCENTAGE = 95;
    public static final int TOOLBAR_CLOSE_PERCENTAGE = 95;
    public static final int RESTART_SCAN_TIMEOUT = 1000;
    public static final int TIMEOUT_SCAN_STOP = 1000 * 30;
    public static final int REQUEST_APP_PERMISSION_CODE = 101;


    public static List<Log> LOGS = new LinkedList<>();

    public static MenuItem ota_button;

    public static void clearLogs() {
        LOGS.clear();
    }
}
