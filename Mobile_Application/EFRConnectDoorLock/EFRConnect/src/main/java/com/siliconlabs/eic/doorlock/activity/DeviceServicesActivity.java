/*
 * Bluegiga’s Bluetooth Smart Android SW for Bluegiga BLE modules
 * Contact: support@bluegiga.com.
 *
 * This is free software distributed under the terms of the MIT license reproduced below.
 *
 * Copyright (c) 2013, Bluegiga Technologies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files ("Software")
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF
 * ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A  PARTICULAR PURPOSE.
 */
package com.siliconlabs.eic.doorlock.activity;


import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.siliconlabs.eic.doorlock.OtaFileType;
import com.siliconlabs.eic.doorlock.dialogs.ErrorDialog;
import com.siliconlabs.eic.doorlock.mappings.Mapping;
import com.siliconlabs.eic.doorlock.mappings.MappingType;
import com.siliconlabs.eic.doorlock.mappings.MappingsEditDialog;
import com.siliconlabs.eic.doorlock.R;
import com.siliconlabs.eic.doorlock.adapters.ConnectionsAdapter;
import com.siliconlabs.eic.doorlock.adapters.LogAdapter;
import com.siliconlabs.eic.doorlock.ble.BlueToothService;
import com.siliconlabs.eic.doorlock.ble.BluetoothDeviceInfo;
import com.siliconlabs.eic.doorlock.ble.TimeoutGattCallback;
import com.siliconlabs.eic.doorlock.bluetoothdatamodel.datatypes.Characteristic;
import com.siliconlabs.eic.doorlock.bluetoothdatamodel.datatypes.Descriptor;
import com.siliconlabs.eic.doorlock.bluetoothdatamodel.datatypes.Service;
import com.siliconlabs.eic.doorlock.bluetoothdatamodel.parsing.Common;
import com.siliconlabs.eic.doorlock.bluetoothdatamodel.parsing.Converters;
import com.siliconlabs.eic.doorlock.bluetoothdatamodel.parsing.Engine;
import com.siliconlabs.eic.doorlock.fragment.FragmentCharacteristicDetail;
import com.siliconlabs.eic.doorlock.fragment.LogFragmentConnected;
import com.siliconlabs.eic.doorlock.interfaces.MappingCallback;
import com.siliconlabs.eic.doorlock.interfaces.ServicesConnectionsCallback;
import com.siliconlabs.eic.doorlock.log.TimeoutLog;
import com.siliconlabs.eic.doorlock.toolbars.ConnectionsFragment;
import com.siliconlabs.eic.doorlock.toolbars.LoggerFragment;
import com.siliconlabs.eic.doorlock.toolbars.ToolbarCallback;
import com.siliconlabs.eic.doorlock.utils.BLEUtils;
import com.siliconlabs.eic.doorlock.utils.BLEUtils.Notifications;
import com.siliconlabs.eic.doorlock.utils.Constants;
import com.siliconlabs.eic.doorlock.utils.FilterDeviceParams;
import com.siliconlabs.eic.doorlock.utils.SharedPrefUtils;
import com.siliconlabs.eic.doorlock.utils.ToolbarName;
import com.siliconlabs.eic.doorlock.views.ServiceItemContainer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DeviceServicesActivity extends AppCompatActivity implements ServicesConnectionsCallback {
    private static final String TAG = "DeviceServicesActivity";
    private static final String ABOUT_DIALOG_HTML_ASSET_FILE_PATH = "file:///android_asset/about.html";
    private static final String CHARACTERISTIC_ADD_FRAGMENT_TRANSACTION_ID = "characteristicdetail";
    private static final int UI_CREATION_DELAY = 0;
    private static final int GATT_FETCH_ON_SERVICE_DISCOVERED_DELAY = 875;
    private static final String PROPERTY_ICON_TAG = "characteristicpropertyicon";
    private static final String PROPERTY_NAME_TAG = "characteristispropertyname";
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_PERMISSION = 300;
    private static final int FILE_CHOOSER_REQUEST_CODE = 9999;
    private static final int TOOLBAR_OPEN_PERCENTAGE = 95;
    private static final int TOOLBAR_CLOSE_PERCENTACE = 95;
    /**
     * Services UUIDs
     */
    public static UUID ota_service = UUID.fromString("1d14d6ee-fd63-4fa1-bfa4-8f47b42119f0");
    /**
     * BLUETOOTH ADAPTER RESPONSES
     **************************************************/
    private final BroadcastReceiver bluetoothAdapterStateChangeListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        //Log.d("BTAdapter","STATE_ON");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        //Log.d("BTAdapter","STATE_OFF");
                        break;
                }
            }
        }
    };
    @InjectView(R.id.loading_container)
    public RelativeLayout loadingContainer;
    @InjectView(R.id.loading_anim_gradient_right_container)
    public LinearLayout loadingGradientContainer;
    @InjectView(R.id.loading_bar_container)
    public RelativeLayout loadingBarContainer;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.services_container)
    LinearLayout servicesContainer;
    @InjectView(R.id.servicesWrapper)
    RelativeLayout servicesWrapper;
    @InjectView(R.id.scrollViewWrapper)
    RelativeLayout scrollViewWrapper;
    @InjectView(R.id.framelayout_container)
    RelativeLayout frameLayoutContainerRL;
    @InjectView(R.id.frame_layout)
    FrameLayout frameLayout;
    @InjectView(R.id.linearlayout_connections)
    LinearLayout connectionsLL;
    @InjectView(R.id.bluetooth_browser_background)
    RelativeLayout bluetoothBrowserBackgroundRL;
    @InjectView(R.id.textview_connections)
    TextView connectionsTV;
    @InjectView(R.id.imageview_connections)
    ImageView connectionsIV;
    @InjectView(R.id.linearlayout_log)
    LinearLayout logLL;
    @InjectView(R.id.textview_log)
    TextView logTV;
    @InjectView(R.id.imageview_log)
    ImageView logIV;
    @InjectView(R.id.rssi_text_view)
    TextView rssiTV;
    @InjectView(R.id.rssi_image_view)
    ImageView rssiIV;
    // This is used to refresh from FragmentCharacteristicDetail after a write
    // refactor into a callback / a more comprehensive mechanism needed
    Button btnCaretPressed = null;
    private Handler handler;
    private UUID ota_control = UUID.fromString("f7bf3564-fb6d-4e53-88a4-5e37e0326063");

    /**
     * OTA MenuButton
     */
//    MenuItem ota_button;
    private UUID ota_data = UUID.fromString("984227f3-34fc-4045-a5d0-2c581f81a153");
    private UUID fw_version = UUID.fromString("4f4a2368-8cca-451e-bfff-cf0e2ee23e9f");
    private UUID ota_version = UUID.fromString("4cc07bcf-0868-4b32-9dad-ba4cc41e5316");
    private UUID homekit_descriptor = UUID.fromString("dc46f0fe-81d2-4616-b5d9-6abdd796939a");
    private UUID homekit_service = UUID.fromString("0000003e-0000-1000-8000-0026bb765291");
    private BluetoothDevice bluetoothDevice = null;
    private int generatedId = 10000;
    private boolean serviceHasBeenSet;
    private BlueToothService service;
    private BlueToothService.Binding bluetoothBinding;
    private BluetoothGatt bluetoothGatt;
    private final Runnable WRITE_OTA_CONTROL_ZERO = new Runnable() {
        @Override
        public void run() {
            writeOtaControl((byte) 0x00);
        }
    };
    private Dialog dialogLicense;
    private Dialog newPriority;
    private Dialog newMTU;
    private ErrorDialog errorDialog;
    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothManager bluetoothManager = null;
    private BluetoothLeScanner bluetoothLeScanner = null;
    private HashMap<String, Mapping> characteristicNamesMap;
    private HashMap<String, Mapping> serviceNamesMap;
    private SharedPrefUtils sharedPrefUtils;
    private HashMap<Integer, FragmentCharacteristicDetail> characteristicFragments = new HashMap<>();
    /**
     * LOG
     */
    private Thread logupdate;
    private volatile boolean running = false;
    private StringBuilder substraction = new StringBuilder();
    private TextView tv;
    //Not used
    public Handler logHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Log.i("handler","receiving message");
            String sentString = msg.getData().getString("whatisconnected");
            try {
                tv = findViewById(R.id.log_view);
                tv.setMovementMethod(new ScrollingMovementMethod());
                tv.append(sentString);
                tv.scrollTo(0, tv.getScrollY());
            } catch (Exception e) {
                Log.e("mHandle", "error: " + e);
            }

        }
    };
    /**
     * OTA Progress
     */
    private Dialog otaProgress;
    private ProgressBar progressBar;
    private Chronometer chrono;
    private TextView dataRate;
    private TextView datasize;
    private TextView filename;
    private TextView steps;
    private ProgressBar uploadimage;
    private Button OTAStart;
    /**
     * OTA Setup
     */
    private Dialog otaSetup;
    private RadioButton reliabilityRB;
    private RadioButton speedRB;
    private Button partialOTA;
    private Button fullOTA;
    private Button OTA_OK;
    private SeekBar requestMTU;
    private SeekBar delaySeekBar;
    private int delayNoResponse = 1;
    private TextView sizename;
    private TextView mtuname;
    private CheckBox reliableWrite;
    private TextView delayText;
    private Button appFileButton;
    private Button appLoaderFileButton;
    private OtaFileType currentOtaFileType;
    private int priority = 2;
    private int requestMTUValue;
    /**
     * File Selection
     */
    private String appPath = "";
    private String stackPath = "";
    /**
     * Loading Dialog
     */
    private Dialog loadingdialog;
    private TextView loadingLog;
    private TextView loadingHeader;
    private ProgressBar loadingimage;
    /**
     * Global Variables
     */
    private int MTU = 247;
    private int MTU_divisible = 0;
    private long otatime = 0;
    private int pack = 0;
    private byte[] otafile;
    private String reconnectaddress;
    private long delayToConnect = 0;
    private int onScanCallback = 0;
    /**
     * Global Booleans
     */
    private boolean reliable = true;
    private boolean boolFullOTA = false;
    private boolean boolOTAbegin = false;
    private boolean connected = false;
    private boolean boolOTAdata = false;
    private boolean UICreated = false;
    private boolean discoverTimeout = true;
    private boolean ota_mode = false;
    private boolean boolrequest_mtu = false;
    private boolean ota_process = false;
    private boolean boolrefresh_services = false;
    private boolean disconnect_gatt = false;
    private boolean disconnectionTimeout = false;
    private boolean homekit = false;
    private boolean doubleStepUpload = false;
    private boolean otaMode = false;
    private BluetoothGattDescriptor kit_descriptor;
    private final Runnable DFU_OTA_UPLOAD = new Runnable() {
        @Override
        public void run() {
            DFUMode("OTAUPLOAD");
        }
    };
    private FragmentCharacteristicDetail currentWriteReadFragment;
    private Map<String, ServiceItemContainer> serviceItemContainers;
    private ConnectionsFragment connectionsFragment;
    private ConnectionsAdapter connectionsAdapter;
    private LoggerFragment loggerFragment;
    private boolean btToolbarOpened = false;
    /************************************************************************************/
    private ToolbarName btToolbarOpenedName = null;
    /*************************************************************************************/
    private String deviceAddress;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    /**
     * (RUNNABLE) CHECKS OTA BEGIN BOX AND STARTS
     **********************************/
    private Runnable checkbeginrunnable = new Runnable() {
        @Override
        public void run() {
            chrono.setBase(SystemClock.elapsedRealtime());
            chrono.start();
        }
    };
    /**
     * BLUETOOTH GATT CALLBACKS
     *********************************************************/
    private TimeoutGattCallback gattCallback = new TimeoutGattCallback() {
        @Override
        public void onReadRemoteRssi(final BluetoothGatt gatt, final int rssi, int status) {
            if (!otaMode) {
                super.onReadRemoteRssi(gatt, rssi, status);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("onReadRemoteRssi", "RSSI: " + rssi);
                        rssiTV.setText(getResources().getString(R.string.n_dBm, rssi));
                    }
                });
            }
        }

        @Override
        public void onTimeout() {
            Constants.LOGS.add(new TimeoutLog());
            super.onTimeout();
            Log.d("gattCallback", "onTimeout");
        }

        @Override //CALLBACK TO REQUEST MTU
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            Log.d("onMtuChanged", "MTU: " + mtu + " - status: " + status);

            if (status == 0) { //NO ERRORS
                MTU = mtu;

                bluetoothGatt.requestConnectionPriority(priority);

                if (boolrequest_mtu) { //Request MTU From rounded_button_red menu
                    MTUonButtonMenu();
                } else if (ota_process && !boolrequest_mtu) {
                    if (ota_mode && newMTU.isShowing()) { //Reopen OTA Setup
                        reopenOTASetup();
                    }
                    if (ota_mode) { //Reset OTA Progress
                        resetOTAProgress();
                    }
                }
            } else { //ERROR HANDLING
                final int error = status;
                Log.d("RequestMTU", "Error: " + error);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "ERROR REQUESTING MTU: " + error, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        disconnectGatt(bluetoothGatt);
                    }
                }, 2000);
            }
        }

        @Override //CALLBACK ON CONNECTION STATUS CHANGES
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
            updateCountOfConnectedDevices();
            if (bluetoothGatt != null) {
                if (!bluetoothGatt.getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                    return;
                }
            }
            super.onConnectionStateChange(gatt, status, newState);
            Log.d("onConnectionStateChange", "status = " + status + " - newState = " + newState);
            switch (newState) {
                case BluetoothGatt.STATE_CONNECTED: //Handling Connections
                    connected = true;
                    Log.d("onConnectionStateChange", "CONNECTED");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!loadingdialog.isShowing()) {
                                Toast.makeText(DeviceServicesActivity.this, "DEVICE CONNECTED", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    if (ota_process) { //After OTA process started
                        Log.d("Address", "" + gatt.getDevice());
                        Log.d("Name", "" + gatt.getDevice().getName());

                        if (gatt.getServices().isEmpty()) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    bluetoothGatt = null; //It's going to be equal gatt in Discover Services Callback...
                                    Log.d("onConnected", "Start Services Discovery: " + gatt.discoverServices());
                                }
                            }, 250);
                            discoverTimeout = true;
                            Runnable timeout = new Runnable() { //Discover Services Timeout
                                @Override
                                public void run() {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (discoverTimeout) {
                                                disconnectGatt(gatt);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getBaseContext(), "DISCOVER SERVICES TIMEOUT", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        }
                                    }, 25000);
                                }
                            };
                            new Thread(timeout).start();
                        }
                    }
                    break;
                case BluetoothGatt.STATE_DISCONNECTED://Handling Disonnections
                    connected = false;
                    discoverTimeout = false;
                    final int error = status;
                    disconnectionTimeout = false;

                    if (status != 0 && otaMode && errorDialog == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorDialog = new ErrorDialog(error, new ErrorDialog.OtaErrorCallback() {
                                    @Override
                                    public void onDismiss() {
                                        exit(bluetoothGatt);
                                    }
                                });
                                errorDialog.show(getSupportFragmentManager(), "ota_error_dialog");
                            }
                        });
                    } else {
                        if (disconnect_gatt) {
                            exit(gatt);
                        }

                        if (ota_process || boolOTAbegin || boolFullOTA) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (loadingdialog.isShowing()) {
                                        loadingLog.setText("Rebooting...");
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        loadingLog.setText("Waiting...");
                                                    }
                                                });
                                            }
                                        }, 1500);
                                    }
                                }
                            });
                        }

                        if (otaSetup != null) if (otaSetup.isShowing()) {
                            exit(gatt);
                        }

                        if (gatt != null && gatt.getServices().isEmpty()) {
                            exit(gatt);

                        }
                        if (gatt != null && !boolFullOTA && !boolOTAbegin && !ota_process) {
                            exit(gatt);
                        }
                    }
                    break;
                case BluetoothGatt.STATE_CONNECTING:
                    Log.d("onConnectionStateChange", "Connecting...");
                    break;
            }
        }

        @Override //CALLBACK ON CHARACTERISTIC READ
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (currentWriteReadFragment != null) {
                currentWriteReadFragment.onActionDataAvailable(characteristic.getUuid().toString());
            }

            Log.i("Callback", "OnCharacteristicRead: " + Converters.getHexValue(characteristic.getValue()) + " Status: " + status);

            if (characteristic == (bluetoothGatt.getService(ota_service).getCharacteristic(ota_control))) {
                byte[] value = characteristic.getValue();
                if (value[2] == (byte) 0x05) {
                    Log.d("homekit_descriptor", "Insecure Connection");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "Error: Not a Homekit Secure Connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (value[2] == (byte) 0x04) {
                    Log.d("homekit_descriptor", "Wrong Address");
                } else if (value[2] == (byte) 0x00) {
                    Log.d("homekit_descriptor", "Entering in DFU_Mode...");
                    if (ota_mode && ota_process) {
                        Log.d("OTAUPLOAD", "Sent");
                        runOnUiThread(checkbeginrunnable);
                        handler.removeCallbacks(DFU_OTA_UPLOAD);
                        handler.postDelayed(DFU_OTA_UPLOAD, 500);
                    } else if (!ota_mode && ota_process) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingLog.setText("Resetting...");
                                showLoading();
                                animaloading();
                                Constants.ota_button.setVisible(true);
                            }
                        });
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                reconnect(4000);
                            }
                        }, 200);
                    }
                }
            }
        }

        @Override //CALLBACK ON CHARACTERISTIC WRITE (PROPERTY: WHITE)
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            if (currentWriteReadFragment != null) {
                currentWriteReadFragment.onActionDataWrite(characteristic.getUuid().toString(), status);
            }

            if (characteristic.getValue().length < 10)
                Log.d("OnCharacteristicRead", "Char: " + characteristic.getUuid().toString() + " Value: " + Converters.getHexValue(characteristic.getValue()) + " Status: " + status);

            if (status != 0) { // Error Handling
                Log.d("onCharWrite", "status: " + Integer.toHexString(status));
                final int error = status;
                if (errorDialog == null) {
                    runOnUiThread(new Runnable() { //Display error on Toast
                        @Override
                        public void run() {
                            errorDialog = new ErrorDialog(error, new ErrorDialog.OtaErrorCallback() {
                                @Override
                                public void onDismiss() {
                                    exit(bluetoothGatt);
                                }
                            });
                            errorDialog.show(getSupportFragmentManager(), "ota_error_dialog");
                        }
                    });
                }
            } else {

                if (characteristic.getUuid().equals(ota_control)) { //OTA Control Callback Handling
                    if (characteristic.getValue().length == 1) {
                        if (characteristic.getValue()[0] == (byte) 0x00) {
                            Log.d("Callback", "Control " + Converters.getHexValue(characteristic.getValue()) + "status: " + status);
                            if (ota_mode && ota_process) {
                                Log.d("OTAUPLOAD", "Sent");
                                runOnUiThread(checkbeginrunnable);
                                handler.removeCallbacks(DFU_OTA_UPLOAD);
                                handler.postDelayed(DFU_OTA_UPLOAD, 500);
                            } else if (!ota_mode && ota_process) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadingLog.setText("Resetting...");
                                        showLoading();
                                        animaloading();
                                        Constants.ota_button.setVisible(true);
                                    }
                                });
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        reconnect(4000);
                                    }
                                });
                            }
                        }
                        if (characteristic.getValue()[0] == (byte) 0x03) {
                            if (ota_process) {
                                Log.d("Callback", "Control " + Converters.getHexValue(characteristic.getValue()) + "status: " + status);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        OTAStart.setBackgroundColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_red));
                                        OTAStart.setClickable(true);

                                    }
                                });
                                boolOTAbegin = false;
                                if (boolFullOTA) {
                                    stackPath = "";
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            otaProgress.dismiss();
                                            loadingLog.setText("Loading");
                                            showLoading();
                                            animaloading();
                                        }
                                    });

                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            reconnect(4000);
                                        }
                                    }, 500);
                                }
                            }
                        }
                    } else {
                        Log.i("OTA_Control", "Received: " + Converters.getHexValue(characteristic.getValue()));
                        if (characteristic.getValue()[0] == 0x00 && characteristic.getValue()[1] == 0x02) {
                            Log.i("HomeKit", "Reading OTA_Control...");
                            bluetoothGatt.readCharacteristic(characteristic);
                        }
                    }
                }

                if (characteristic.getUuid().equals(ota_data)) {   //OTA Data Callback Handling
                    if (reliable) {
                        if (otaProgress.isShowing()) {
                            pack += MTU_divisible;
                            if (pack <= otafile.length - 1) {
                                //Log.d("callback", "pack: " + (pack - MTUheader) + " / " + pack + " : " + Converters.getHexValue(characteristic.getValue()));
                                //Log.d("callback", "" + status);
                                otaWriteDataReliable();
                            } else if (pack > otafile.length - 1) {
                                //Log.d("callback", "last: " + pack + " / " + otafile.length + " : " + Converters.getHexValue(characteristic.getValue()));
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                chrono.stop();
                                                uploadimage.clearAnimation();
                                                uploadimage.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                    }
                                });

                                boolOTAdata = false;
                                DFUMode("OTAEND");

                            }
                        }
                    }
                }
            }
            bluetoothGatt.readCharacteristic(characteristic);
        }

        @Override //CALLBACK ON DESCRIPTOR WRITE
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (currentWriteReadFragment != null) {
                currentWriteReadFragment.onDescriptorWrite(descriptor.getUuid());
            }
        }

        @Override //CALLBACK ON DESCRIPTOR READ
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

            if (descriptor.getUuid().toString().equals(homekit_descriptor.toString())) {

                byte[] value = new byte[2];
                value[0] = (byte) 0xF2;
                value[1] = (byte) 0xFF;

                if (descriptor.getValue()[0] == value[0] && descriptor.getValue()[1] == value[1]) {

                    Log.i("descriptor", "getValue " + Converters.getHexValue(descriptor.getValue()));
                    homeKitOTAControl(descriptor.getValue());

                }
            }
        }

        @Override //CALLBACK ON CHARACTERISTIC CHANGED VALUE (READ - CHARACTERISTIC NOTIFICATION)
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            for (int key : characteristicFragments.keySet()) {
                FragmentCharacteristicDetail fragment = characteristicFragments.get(key);
                if (fragment != null && fragment.getmCharact().getUuid().equals(characteristic.getUuid())) {
                    fragment.onActionDataAvailable(characteristic.getUuid().toString());
                    break;
                }
            }

        }

        @Override //CALLBACK ON SERVICES DISCOVERED
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
//            bluetoothGatt = gatt;

            if (bluetoothGatt != gatt) {
                bluetoothGatt = gatt;
                refreshServices();
            } else {

                discoverTimeout = false;
                /**ERROR IN SERVICE DISCOVERY*/
                if (status != 0) {
                    Log.d("Error status", "" + Integer.toHexString(status));
                    final int error = status;

                    if (errorDialog == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorDialog = new ErrorDialog(error, new ErrorDialog.OtaErrorCallback() {
                                    @Override
                                    public void onDismiss() {
                                        exit(bluetoothGatt);
                                    }
                                });
                                errorDialog.show(getSupportFragmentManager(), "ota_error_dialog");
                            }
                        });
                    }
                } else {
                    /**ON SERVICE DISCOVERY WITHOUT ERROR*/

                    getServicesInfo(gatt); //SHOW SERVICES IN LOG

                    final BluetoothGatt btGatt = gatt;

                    //REFRESH SERVICES UI <- REFRESH SERVICES MENU BUTTON
                    if (boolrefresh_services) {
                        boolrefresh_services = false;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        onGattFetched();
                                        hideCharacteristicLoadingAnimation();
                                    }
                                });
                            }
                        }, GATT_FETCH_ON_SERVICE_DISCOVERED_DELAY);
                    } else {
                        //DEFINE IF DEVICE SUPPORT OTA & MODE (NORMAL/DFU)

                        final boolean ota_service_check = btGatt.getService(ota_service) != null;
                        if (ota_service_check) {
                            //Log.d("ota_service_check", "" + ota_service_check);
                            final boolean ota_data_check = btGatt.getService(ota_service).getCharacteristic(ota_data) != null;
                            if (ota_data_check) {
                                //Log.d("ota_data_check", "" + ota_data_check);
                                final boolean homekit_check = btGatt.getService(homekit_service) != null;
                                if (!homekit_check) {
                                    ota_mode = true;
                                    int ota_data_property = btGatt.getService(ota_service).getCharacteristic(ota_data).getProperties();
                                    if (ota_data_property == 12 || ota_data_property == 8 || ota_data_property == 10) {
                                        //reliable = true;
                                    } else if (ota_mode && ota_data_property == 4) {
                                        //reliable = false;
                                    }
                                }
                            } else {
                                if (boolOTAbegin) onceAgain();
                            }
                        } //else Log.d("ota_service_check", "" + ota_service_check);


                        //REQUEST MTU
                        if (UICreated && loadingdialog.isShowing()) {
                            bluetoothGatt.requestMtu(MTU);
                        }

                        //LAUNCH SERVICES UI
                        if (!boolFullOTA) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            onGattFetched();
                                            hideCharacteristicLoadingAnimation();
                                        }
                                    });
                                }
                            }, GATT_FETCH_ON_SERVICE_DISCOVERED_DELAY);
                        }

                        //IF DFU_MODE, LAUNCH OTA SETUP AUTOMATICALLY
                        if (ota_mode && boolOTAbegin) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadingimage.setVisibility(View.GONE);
                                            loadingdialog.dismiss();
                                            showOtaProgress();
                                        }
                                    });
                                }
                            }, (int) (2.5 * UI_CREATION_DELAY));
                        }
                    }
                }
            }
        }
    };
    //Not Used - resetconnection
    BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            Log.d("Scanning", "");
            if (device.getAddress().equals(reconnectaddress)) {
                bluetoothGatt = device.connectGatt(getBaseContext(), false, gattCallback);
                Log.d("onLeScan", "Device is found" + device.getAddress());
            }
        }
    };
    //Not Used - resetconnection
    private ScanCallback reScanCallback;

    /**
     * ACTIVITY STATES MACHINE
     ***********************************************************/

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_services);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);

        findViewById(R.id.go_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        if (!getResources().getBoolean(R.bool.isTablet)) {
            rssiTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            int size = (int) (getResources().getDisplayMetrics().density * 16);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            rssiIV.setLayoutParams(params);
        }

        sharedPrefUtils = new SharedPrefUtils(DeviceServicesActivity.this);
        characteristicNamesMap = sharedPrefUtils.getCharacteristicNamesMap();
        serviceNamesMap = sharedPrefUtils.getServiceNamesMap();


        if (savedInstanceState == null) {
            LogFragmentConnected logFragment = new LogFragmentConnected();
            androidx.fragment.app.FragmentTransaction logtransaction = getSupportFragmentManager().beginTransaction();
            logtransaction.add(R.id.log_body_connected, logFragment);
            logtransaction.commit();
        }

        reScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                final BluetoothDevice btDevice = result.getDevice();
                onScanCallback++;
                loadingLog.setText(getResources().getText(R.string.Waiting_to_connect));
                reconnectGatt(btDevice);
                onScanCallback = 0;
            }
        };

        initDevice(getDeviceAddress(savedInstanceState));

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothAdapterStateChangeListener, filter);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        fragmentsInit();

        setToolbarItemsNotClicked();

        connectionsLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btToolbarOpened && btToolbarOpenedName == ToolbarName.CONNECTIONS) {
                    closeToolbar();
                    btToolbarOpened = !btToolbarOpened;
                    return;
                }
                if (!btToolbarOpened) {
                    bluetoothBrowserBackgroundRL.setBackgroundColor(Color.parseColor("#99000000"));
                    bluetoothBrowserBackgroundRL.setVisibility(View.VISIBLE);
                    ViewCompat.setTranslationZ(bluetoothBrowserBackgroundRL, 4f);
                    animateToolbarOpen(TOOLBAR_OPEN_PERCENTAGE, 300);
                    btToolbarOpened = !btToolbarOpened;
                }
                setToolbarItemsNotClicked();
                setToolbarItemClicked(connectionsIV, connectionsTV);
                btToolbarOpenedName = ToolbarName.CONNECTIONS;
                setToolbarFragment(connectionsFragment);
            }
        });

        logLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btToolbarOpened && btToolbarOpenedName == ToolbarName.LOGS) {
                    loggerFragment.stopLogUpdater();
                    closeToolbar();
                    btToolbarOpened = !btToolbarOpened;
                    return;
                }
                if (!btToolbarOpened) {
                    bluetoothBrowserBackgroundRL.setBackgroundColor(Color.parseColor("#99000000"));
                    bluetoothBrowserBackgroundRL.setVisibility(View.VISIBLE);
                    ViewCompat.setTranslationZ(bluetoothBrowserBackgroundRL, 4f);
                    animateToolbarOpen(TOOLBAR_OPEN_PERCENTAGE, 300);
                    btToolbarOpened = !btToolbarOpened;
                }
                setToolbarItemsNotClicked();
                setToolbarItemClicked(logIV, logTV);
                btToolbarOpenedName = ToolbarName.LOGS;
                setToolbarFragment(loggerFragment);
                loggerFragment.scrollToEnd();
                loggerFragment.runLogUpdater();
            }
        });
    }

    private String getDeviceAddress(final Bundle savedInstanceState) {
        String deviceAddress;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                deviceAddress = null;
            } else {
                deviceAddress = extras.getString("DEVICE_SELECTED_ADDRESS");
            }
        } else {
            deviceAddress = savedInstanceState.getString("DEVICE_SELECTED_ADDRESS");
        }
        this.deviceAddress = deviceAddress;
        return deviceAddress;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("DEVICE_SELECTED_ADDRESS", deviceAddress);
        super.onSaveInstanceState(outState);
    }


    /*****************************************************************************************/

    @Override
    protected void onResume() {
        super.onResume();
        if ((serviceHasBeenSet && service == null) || (service != null && !service.isGattConnected())) {
            Toast.makeText(DeviceServicesActivity.this, R.string.toast_debug_connection_failed, Toast.LENGTH_LONG).show();
            if (bluetoothGatt != null) if (service != null) {
                service.clearGatt();
            }
            bluetoothBinding.unbind();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPrefUtils.saveCharacteristicNamesMap(characteristicNamesMap);
        sharedPrefUtils.saveServiceNamesMap(serviceNamesMap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (otaProgress != null) otaProgress.dismiss();
        if (loadingdialog != null) loadingdialog.dismiss();

        try {
            unregisterReceiver(bluetoothAdapterStateChangeListener);
        } catch (Exception b) {
            Log.e("onDestroy", "unregisterReceiver Err" + b);
        }

        // zakomentowane, aby nie rozłączać
//        if (service != null) {
//            //Log.d("onDestroy","called");
//            service.clearGatt();
//        }

        try {
            bluetoothBinding.unbind();
        } catch (Exception e) {
            Log.e("onDestroy", "bluetoothBinding Err" + e);
        }

//        if (bluetoothGatt != null) bluetoothGatt = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device_services_bluegiga, menu);
        Constants.ota_button = menu.findItem(R.id.OTA_button);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_license:
                showAboutDialog();
                break;
            case R.id.menu_log: //LOG MENU BUTTON
                adjustLayout();
                break;
            case R.id.OTA_button: //OTA MENU BUTTON
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_PERMISSION);
                } else if (UICreated) {
                    otaMode = true;
                    OTAonClick();
                }
                break;
            case R.id.refresh_services: //REFRESH SERVICES MENU BUTTON
                boolrefresh_services = true;
                refreshServices();
                break;
            case R.id.request_mtu: //REQUEST MTU MENU BUTTON
                if (UICreated) {
                    boolrequest_mtu = true;
                    showRequestMTU();
                }
                break;
            case R.id.request_priority://REQUEST PRIORITY MENU BUTTON
                if (bluetoothGatt != null && newPriority != null) {
                    showRequestPriority();
                }
                break;
            case android.R.id.home: //BACK MENU BUTTON
                onBackPressed();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * FUNCTIONS
     *****************************************************************************/


    public void onceAgain() {
        writeOtaControl((byte) 0x00);
    }

    /**
     * START OTA BUTTON (UI, Bools)
     */
    public void OTAonClick() {

        if (ota_mode) {
            ota_process = true;
            boolOTAbegin = false;
        } else {
            ota_process = true;
            boolOTAbegin = true;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingimage.setVisibility(View.GONE);
                loadingdialog.dismiss();
                showOtaSetup();
                Constants.ota_button.setVisible(true);
            }
        });
    }

    /**
     * ACTION WHEN MTU MENU BUTTON IS PRESSED
     ************************************************/
    public void MTUonButtonMenu() {
        boolrequest_mtu = false;
        if (newMTU.isShowing()) {
            newMTU.dismiss();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.MTU_colon_n, MTU), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * CLOSES THE MTU DIALOG AND SHOW OTA SETUP DIALOG
     *****************************************/
    public void reopenOTASetup() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newMTU.dismiss();
                otaSetup.show();
                requestMTU.setProgress(MTU);
            }
        });

    }

    /**
     * SETS ALL THE INFO IN THE OTA PROGRESS DIALOG TO "" OR 0
     ********************************/
    public void resetOTAProgress() {
        boolFullOTA = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                datasize.setText("");
                filename.setText("");
                loadingimage.setVisibility(View.GONE);
                loadingdialog.dismiss();
                progressBar.setProgress(0);
                datasize.setText(getResources().getString(R.string.zero_percent));
                dataRate.setText("");
                OTAStart.setClickable(false);
                OTAStart.setBackgroundColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_button_inactive));
                showOtaProgress();
            }
        });
    }

    /**
     * FUTURE IMPLEMENTATION OF LOG VIEW
     **************************************************/
    //Not used
    private void adjustLayout() {
        float scale = getResources().getDisplayMetrics().density;
        RelativeLayout logbody = findViewById(R.id.log_body_connected);
        ViewGroup.MarginLayoutParams svw = (ViewGroup.MarginLayoutParams) scrollViewWrapper.getLayoutParams();
        if (logbody.getVisibility() == View.GONE) {
            Log.i("adjustLayout", "Creating View");
            running = true;
            svw.setMargins(0, 0, 0, (int) ((scale * 300) + 0.5f));
            scrollViewWrapper.setLayoutParams(svw);
            logbody.setVisibility(View.VISIBLE);
            startlog();
        } else {
            Log.i("adjustLayout", "Hiding View");
            running = false;
            svw.setMargins(0, 0, 0, 0);
            scrollViewWrapper.setLayoutParams(svw);
            logbody.setVisibility(View.GONE);
        }
    }

    //Not used
    private void startlog() {
        if (logupdate == null) {
            logupdate = new Thread(new Runnable() {
                public void run() {
                    synchronized (this) {
                        try {
                            while (running) {
                                log();
                                Thread.sleep(250);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            });
            logupdate.start();
        }
    }

    //Not used
    private void log() {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(" I ") && !line.contains("ViewRoot") && !line.contains("readRssi()")) {
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                }
            }
            if (substraction.toString().length() != stringBuilder.toString().length()) {
                String result;
                if (substraction.toString().length() > stringBuilder.toString().length())
                    result = stringBuilder.toString();
                else
                    result = stringBuilder.substring(substraction.length(), stringBuilder.length());
                Message m = new Message();
                Bundle b = new Bundle();
                b.putString("whatisconnected", result);
                m.setData(b);
                logHandler.sendMessage(m);
                substraction = stringBuilder;
            }
        } catch (IOException e) {
            Log.e("log()", "couldn't create log");
        }
    }

    /**************************************************************************************/

    //Not used
    public String getdate(String string) {
        String currentDateTimeString = null;
        switch (string) {
            case "normal":
                currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                break;
            case "compact":
                SimpleDateFormat LOG_FILE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ssZ");
                currentDateTimeString = LOG_FILE_FORMAT.format(new Date());
                break;
        }
        return currentDateTimeString;
    }

    //Not used
    public File save_log() {
        final File path = new File(Environment.getExternalStorageDirectory(), "SiliconLabs_EFRConnect");
        final File file = new File(path + File.separator + "SiliconLabs." + getdate("compact") + ".txt");
        Thread save_log = new Thread(new Runnable() {
            public void run() {
                String savedata = tv.getText().toString();
                if (!path.exists()) path.mkdirs();
                BufferedWriter bw = null;
                try {
                    file.createNewFile();
                    bw = new BufferedWriter(new FileWriter(file), 1024);
                    bw.write(savedata);
                } catch (IOException e) {
                    Log.e("save_log()", "error saving log", e);
                } finally {
                    if (bw != null) {
                        try {
                            bw.close();
                        } catch (IOException e) {
                            Log.e("save_log()", "error closing save_log()", e);
                        }
                    }
                }
            }
        });
        save_log.start();
        return file;
    }

    //Not used
    public void share_log() {
        Thread share_log = new Thread(new Runnable() {
            public void run() {
                String logdata = tv.getText().toString();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "SiliconLabs BGApp Log: " + getdate("normal"));
                shareIntent.putExtra(Intent.EXTRA_TEXT, logdata);
                startActivity(Intent.createChooser(shareIntent, "Share SiliconLabs BGApp Log ..."));
            }
        });
        share_log.start();
    }

    //Not used
    public void clear_log() {
        tv = findViewById(R.id.log_view);
        tv.setText("");
        try {
            Runtime.getRuntime().exec(new String[]{"logcat", "-c"});
            Log.i("clear_log()", "log cleaned");
            //restartlog();
        } catch (IOException e) {
            Log.e("alogcat", "error clearing log", e);
        }
    }

    /**
     * USED TO CLEAN CACHE AND REDISCOVER SERVICES
     ****************************************/
    private void refreshServices() {
        if (bluetoothGatt != null && bluetoothGatt.getDevice() != null) {
            refreshDeviceCache(bluetoothGatt);
            bluetoothGatt.discoverServices();
        } else if (service != null && service.getConnectedGatt() != null) {
            refreshDeviceCache(service.getConnectedGatt());
            service.getConnectedGatt().discoverServices();
        }
    }

    /**
     * INITIATES SERVICES VIEWS
     ************************************************************/
    private void initServicesViews() {
        serviceItemContainers = new HashMap<>();
        // iterate through all of the services for the device, inflate and add views to the scrollview
        ArrayList<BluetoothGattService> services = (ArrayList<BluetoothGattService>) bluetoothGatt.getServices(); //service.getConnectedGatt().getServices();
        for (int position = 0; position < services.size(); position++) {
            final ServiceItemContainer serviceItemContainer = new ServiceItemContainer(DeviceServicesActivity.this);

            // get information about service at index 'position'
            UUID uuid = services.get(position).getUuid();
            Service service = Engine.getInstance().getService(uuid);
            String serviceName = Common.getServiceName(uuid, getApplicationContext());
            String serviceUuid = Common.getUuidText(uuid);

            serviceName = Common.checkOTAService(serviceUuid, serviceName);

            // initialize information about services in service item container
            initServiceItemContainer(serviceItemContainer, position, serviceName, serviceUuid);

            // initialize views for each characteristic of the service, put into characteristics expansion for service's list item
            final BluetoothGattService blueToothGattService = service == null ? services.get(position) : bluetoothGatt.getService(service.getUuid());
            List<BluetoothGattCharacteristic> characteristics = blueToothGattService.getCharacteristics();
            if (characteristics.size() == 0) {
                serviceItemContainer.serviceInfoCardView.setBackgroundColor(Color.LTGRAY);
                continue;
            }
            // iterate through the characteristics of this service
            for (final BluetoothGattCharacteristic bluetoothGattCharacteristic : characteristics) {
                // retrieve relevant bluetooth data for characteristic of service
                final BluetoothGattCharacteristic thisCharacteristic = bluetoothGattCharacteristic;
                // the engine parses through the data of the btgattcharac and returns a wrapper characteristic
                // the wrapper characteristic is matched with accepted bt gatt profiles, provides field types/values/units
                Characteristic charact = Engine.getInstance().getCharacteristic(bluetoothGattCharacteristic.getUuid());
                String characteristicName;

                if (charact != null) {
                    characteristicName = charact.getName().trim();
                } else {
                    characteristicName = getOtaSpecificCharacteristicName(bluetoothGattCharacteristic.getUuid().toString());
                }


                final String characteristicUuid = (charact != null ? Common.getUuidText(charact.getUuid()) : Common.getUuidText(bluetoothGattCharacteristic.getUuid()));

                //TODO: They are in GattCharacteristic, but their names are not appearing
                if (characteristicUuid.equals(ota_control.toString()))
                    characteristicName = "OTA Control";
                if (characteristicUuid.equals(ota_data.toString())) characteristicName = "OTA Data";
                if (characteristicUuid.equals(fw_version.toString()))
                    characteristicName = "FW Version";
                if (characteristicUuid.equals(ota_version.toString()))
                    characteristicName = "OTA Version";

                // inflate/create ui elements
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                final LinearLayout characteristicContainer = (LinearLayout) layoutInflater.inflate(R.layout.list_item_debug_mode_characteristic_of_service, null);
                final LinearLayout characteristicExpansion = characteristicContainer.findViewById(R.id.characteristic_expansion);
                final LinearLayout propsContainer = characteristicContainer.findViewById(R.id.characteristic_props_container);
                final TextView characteristicNameTextView = characteristicContainer.findViewById(R.id.characteristic_title);
                final TextView characteristicUuidTextView = characteristicContainer.findViewById(R.id.characteristic_uuid);
                final TextView descriptorsLabelTextView = characteristicContainer.findViewById(R.id.text_view_descriptors_label);
                final LinearLayout descriptorLinearLayout = characteristicContainer.findViewById(R.id.linear_layout_descriptor);
                final ImageView characteristicEditNameImageView = characteristicContainer.findViewById(R.id.image_view_edit_charac_name);
                final LinearLayout characEditNameLinearLayout = characteristicContainer.findViewById(R.id.linear_layout_edit_charac_name);
                final LinearLayout showCharacDetailsLinearLayout = characteristicContainer.findViewById(R.id.linear_layout_charac_details);
                View characteristicSeparator = characteristicContainer.findViewById(R.id.characteristics_separator);
                final int id = generateNextId();
                characteristicExpansion.setId(id);

                loadCharacteristicDescriptors(bluetoothGattCharacteristic, descriptorsLabelTextView, descriptorLinearLayout);

                // init/populate ui elements with info from bluetooth data for characteristic of service
                characteristicNameTextView.setText(characteristicName);

                if (characteristicName.equals(getString(R.string.unknown_characteristic_label))) {
                    characteristicEditNameImageView.setVisibility(View.VISIBLE);

                    characEditNameLinearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialog dialog = new MappingsEditDialog(DeviceServicesActivity.this,
                                    characteristicNameTextView.getText().toString(),
                                    characteristicUuid,
                                    new MappingCallback() {
                                        @Override
                                        public void onNameChanged(Mapping nameMapping) {
                                            characteristicNameTextView.setText(nameMapping.getName());
                                            characteristicNamesMap.put(nameMapping.getUuid(), nameMapping);
                                        }
                                    }, MappingType.CHARACTERISTIC);
                            dialog.show();
                        }
                    });

                    if (characteristicNamesMap.containsKey(characteristicUuid)) {
                        characteristicNameTextView.setText(characteristicNamesMap.get(characteristicUuid).getName());
                    }


                }


                characteristicUuidTextView.setText(characteristicUuid);

                // hide divider between characteristics if last characteristic of service
                if (serviceItemContainer.groupOfCharacteristicsForService.getChildCount() == characteristics.size() - 1) {
                    characteristicSeparator.setVisibility(View.GONE);
                    serviceItemContainer.lastItemDivider.setVisibility(View.VISIBLE);
                }
                serviceItemContainer.groupOfCharacteristicsForService.addView(characteristicContainer);

                final String finalServiceName = serviceName;

                // add properties to characteristic list item in expansion
                addPropertiesToCharacteristic(bluetoothGattCharacteristic, propsContainer);
                setPropertyClickListeners(propsContainer, bluetoothGattCharacteristic, blueToothGattService, finalServiceName, characteristicExpansion);
                serviceItemContainer.setCharacteristicNotificationState(characteristicUuid, Notifications.DISABLED);

                characteristicContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (characteristicExpansion.getVisibility() == View.VISIBLE) {
                            characteristicExpansion.setVisibility(View.GONE);
                        } else {
                            characteristicExpansion.setVisibility(View.VISIBLE);

                            if (characteristicFragments.containsKey(id)) {
                                currentWriteReadFragment = characteristicFragments.get(id);
                            } else {
                                currentWriteReadFragment = initFragmentCharacteristicDetail(bluetoothGattCharacteristic, id, blueToothGattService, characteristicExpansion, false);
                                characteristicFragments.put(id, currentWriteReadFragment);
                            }
                        }
                    }
                });


            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            int margin16Dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
            int margin10Dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());

            if (position == 0) {
                params.setMargins(margin16Dp, margin16Dp, margin16Dp, margin10Dp);
            } else if (position == services.size() - 1) {
                params.setMargins(margin16Dp, margin10Dp, margin16Dp, margin16Dp);
            } else {
                params.setMargins(margin16Dp, margin10Dp, margin16Dp, margin10Dp);
            }

            serviceItemContainer.serviceInfoCardView.setLayoutParams(params);
            servicesContainer.addView(serviceItemContainer, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            serviceItemContainers.put(serviceName, serviceItemContainer);
        }
    }

    private void loadCharacteristicDescriptors(BluetoothGattCharacteristic bluetoothGattCharacteristic, TextView descriptorsLabelTextView, LinearLayout descriptorLinearLayout) {
        if (bluetoothGattCharacteristic.getDescriptors().size() <= 0) {
            descriptorsLabelTextView.setVisibility(View.GONE);
        } else {
            for (BluetoothGattDescriptor d : bluetoothGattCharacteristic.getDescriptors()) {
                Descriptor descriptor = Engine.getInstance().getDescriptorByUUID(d.getUuid());

                TextView descriptorNameTV = new TextView(this);
                descriptorNameTV.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (descriptor == null) {
                    descriptorNameTV.setText(getResources().getString(R.string.unknown));
                } else {
                    descriptorNameTV.setText(descriptor.getName());
                }
                descriptorNameTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                descriptorNameTV.setTextColor(ContextCompat.getColor(this, R.color.silabs_primary_text));
                descriptorLinearLayout.addView(descriptorNameTV);


                LinearLayout uuidLL = new LinearLayout(this);
                uuidLL.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                uuidLL.setOrientation(LinearLayout.HORIZONTAL);

                TextView uuidLabelTV = new TextView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMarginEnd((int) (getResources().getDisplayMetrics().density * 4));
                uuidLabelTV.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                uuidLabelTV.setText(getResources().getText(R.string.UUID_colon));
                uuidLabelTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                uuidLabelTV.setLayoutParams(params);
                uuidLL.addView(uuidLabelTV);

                TextView uuidTV = new TextView(this);
                uuidTV.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (descriptor != null) {
                    uuidTV.setText(Common.getUuidText(descriptor.getUuid()));
                } else {
                    uuidTV.setText(getResources().getString(R.string.unknown));
                }

                uuidTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                uuidTV.setTextColor(ContextCompat.getColor(this, R.color.silabs_primary_text));
                uuidLL.addView(uuidTV);

                descriptorLinearLayout.addView(uuidLL);
            }
        }

    }

    public void refreshCharacteristicExpansion() {
        if (btnCaretPressed != null) {
            btnCaretPressed.performClick();
            btnCaretPressed.performClick();
        }
    }

    /**
     * INITIATES SERVICES ITENS
     ************************************************************/
    private void initServiceItemContainer(final ServiceItemContainer serviceItemContainer, int position, String serviceName, String serviceUuid) {

        if (position == 0) {
            UICreated = true;
            if (bluetoothGatt.getServices().contains(bluetoothGatt.getService(ota_service))) {
                Constants.ota_button.setVisible(true);
            } else {
                Constants.ota_button.setVisible(false);
            }
        }
        serviceItemContainer.groupOfCharacteristicsForService.setVisibility(View.GONE);
        serviceItemContainer.groupOfCharacteristicsForService.removeAllViews();
        serviceItemContainer.serviceTitleTextView.setText(serviceName);
        serviceItemContainer.serviceUuidTextView.setText(serviceUuid);


        if (serviceName.equals(getString(R.string.unknown_service))) {
            serviceItemContainer.serviceEditNameImageView.setVisibility(View.VISIBLE);
            serviceItemContainer.serviceEditNameLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new MappingsEditDialog(DeviceServicesActivity.this,
                            serviceItemContainer.serviceTitleTextView.getText().toString(),
                            serviceItemContainer.serviceUuidTextView.getText().toString(),
                            new MappingCallback() {
                                @Override
                                public void onNameChanged(Mapping nameMapping) {
                                    serviceItemContainer.serviceTitleTextView.setText(nameMapping.getName());
                                    serviceNamesMap.put(nameMapping.getUuid(), nameMapping);
                                }
                            }, MappingType.SERVICE);
                    dialog.show();
                }
            });

            if (serviceNamesMap.containsKey(serviceUuid)) {
                serviceItemContainer.serviceTitleTextView.setText(serviceNamesMap.get(serviceUuid).getName());
            }
        }

    }

    /**
     * SHOW CHARACTERISTIC PROPERTIES IN UI: READ, WRITE
     ************************************/
    private void addPropertiesToCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic,
                                               LinearLayout propsContainer) {
        String propertiesString = Common.getProperties(DeviceServicesActivity.this, bluetoothGattCharacteristic.getProperties());
        String[] propsExploded = propertiesString.split(",");


        if (Arrays.toString(propsExploded).toLowerCase().contains("write no response")) {
            ArrayList<String> temp = new ArrayList<>();
            boolean writeAdded = false;

            for (String s : propsExploded) {
                if (s.toLowerCase().contains("write no response") && !writeAdded) {
                    temp.add("Write");
                    writeAdded = true;
                } else if (!s.toLowerCase().contains("write")) {
                    temp.add(s);
                }
            }

            propsExploded = new String[temp.size()];

            for (int i = 0; i < temp.size(); i++) {
                propsExploded[i] = temp.get(i);
            }
        }

        for (String propertyValue : propsExploded) {
            TextView propertyView = new TextView(this);

            String propertyValueTrimmed = propertyValue.trim();
            propertyValueTrimmed = propertyValue.length() > 13 ? propertyValue.substring(0, 13) : propertyValueTrimmed;
            propertyView.setText(propertyValueTrimmed);
            propertyView.setBackgroundColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_white));
            propertyView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.characteristic_property_text_size));
            propertyView.setTextColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_inactive));
            propertyView.setTypeface(Typeface.DEFAULT_BOLD);
            propertyView.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout propertyContainer = new LinearLayout(DeviceServicesActivity.this);
            propertyContainer.setOrientation(LinearLayout.HORIZONTAL);

            ImageView propertyIcon = new ImageView(DeviceServicesActivity.this);
            int iconId;
            if (propertyValue.trim().toUpperCase().equals(Common.PROPERTY_VALUE_BROADCAST)) {
                iconId = R.drawable.debug_prop_broadcast;
            } else if (propertyValue.trim().toUpperCase().equals(Common.PROPERTY_VALUE_READ)) {
                iconId = R.drawable.ic_icon_read_off;
            } else if (propertyValue.trim().toUpperCase().equals(Common.PROPERTY_VALUE_WRITE)) {
                iconId = R.drawable.ic_icon_edit_off;
            } else if (propertyValue.trim().toUpperCase().equals(Common.PROPERTY_VALUE_NOTIFY)) {
                iconId = R.drawable.ic_icon_notify_off;
            } else if (propertyValue.trim().toUpperCase().equals(Common.PROPERTY_VALUE_INDICATE)) {
                iconId = R.drawable.ic_icon_indicate_off;
            } else if (propertyValue.trim().toUpperCase().equals(Common.PROPERTY_VALUE_SIGNED_WRITE)) {
                iconId = R.drawable.debug_prop_signed_write;
            } else if (propertyValue.trim().toUpperCase().equals(Common.PROPERTY_VALUE_EXTENDED_PROPS)) {
                iconId = R.drawable.debug_prop_ext;
            } else {
                iconId = R.drawable.debug_prop_ext;
            }
            propertyIcon.setBackgroundResource(iconId);
            propertyIcon.setTag(PROPERTY_ICON_TAG);
            propertyView.setTag(PROPERTY_NAME_TAG);

            LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramsText.gravity = Gravity.CENTER_VERTICAL;

            //int propIconEdgeLength = getResources().getDimensionPixelSize(R.dimen.prop_icon_edge_length);
            LinearLayout.LayoutParams paramsIcon = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            float d = getResources().getDisplayMetrics().density;
            paramsIcon.setMarginEnd((int) (8 * d));
            paramsIcon.gravity = Gravity.CENTER_VERTICAL;

            propertyContainer.addView(propertyIcon, paramsIcon);
            propertyContainer.addView(propertyView, paramsText);

            propertyContainer.setTag(propertyValue);

            LinearLayout.LayoutParams paramsTextAndIconContainer = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            paramsTextAndIconContainer.setMargins(0, (int) (4 * d), (int) (10 * d), 0);
            propertyContainer.setPadding((int) (2 * d), (int) (8 * d), (int) (6 * d), (int) (6 * d));
            propsContainer.addView(propertyContainer, paramsTextAndIconContainer);
        }
    }

    private void setPropertyClickListeners(LinearLayout propsContainer, final BluetoothGattCharacteristic bluetoothGattCharacteristic, final BluetoothGattService service, final String serviceName, final LinearLayout characteristicExpansion) {
        final ImageView notificationIcon = getIconWithValue(propsContainer, Common.PROPERTY_VALUE_NOTIFY);
        final TextView notificationText = getTextViewWithValue(propsContainer, Common.PROPERTY_VALUE_NOTIFY);
        final ImageView indicationIcon = getIconWithValue(propsContainer, Common.PROPERTY_VALUE_INDICATE);
        final TextView indicationText = getTextViewWithValue(propsContainer, Common.PROPERTY_VALUE_INDICATE);
        final ImageView readIcon = getIconWithValue(propsContainer, Common.PROPERTY_VALUE_READ);
        final TextView readText = getTextViewWithValue(propsContainer, Common.PROPERTY_VALUE_READ);
        final ImageView writeIcon = getIconWithValue(propsContainer, Common.PROPERTY_VALUE_WRITE);
        final TextView writeText = getTextViewWithValue(propsContainer, Common.PROPERTY_VALUE_WRITE);
        final int id = characteristicExpansion.getId();

        for (int i = 0; i < propsContainer.getChildCount(); i++) {
            if (propsContainer.getChildAt(i).getTag() == null) {
                continue;
            }
            final LinearLayout propertyContainer = (LinearLayout) propsContainer.getChildAt(i);
            String propertyValueId = ((String) propertyContainer.getTag()).trim().toUpperCase();
            switch (propertyValueId) {
                case Common.PROPERTY_VALUE_READ:
                    propertyContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            readIcon.startAnimation(AnimationUtils.loadAnimation(DeviceServicesActivity.this, R.anim.property_image_click));

                            if (characteristicFragments.containsKey(id)) {
                                currentWriteReadFragment = characteristicFragments.get(id);
                            } else {
                                currentWriteReadFragment = initFragmentCharacteristicDetail(bluetoothGattCharacteristic, id, service, characteristicExpansion, false);
                                characteristicFragments.put(id, currentWriteReadFragment);
                            }

                            characteristicExpansion.setVisibility(View.VISIBLE);

                            bluetoothGatt.readCharacteristic(bluetoothGattCharacteristic);

                        }
                    });

                    break;
                case Common.PROPERTY_VALUE_WRITE:
                    propertyContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            writeIcon.startAnimation(AnimationUtils.loadAnimation(DeviceServicesActivity.this, R.anim.property_image_click));

                            if (characteristicFragments.containsKey(id)) {
                                currentWriteReadFragment = characteristicFragments.get(id);
                                characteristicFragments.get(id).showCharacteristicWriteDialog();
                            } else {
                                currentWriteReadFragment = initFragmentCharacteristicDetail(bluetoothGattCharacteristic, id, service, characteristicExpansion, true);
                                characteristicFragments.put(id, currentWriteReadFragment);
                            }

                            characteristicExpansion.setVisibility(View.VISIBLE);
                        }
                    });

                    break;
                case Common.PROPERTY_VALUE_NOTIFY:
                    propertyContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notificationIcon.startAnimation(AnimationUtils.loadAnimation(DeviceServicesActivity.this, R.anim.property_image_click));

                            if (characteristicFragments.containsKey(id)) {
                                currentWriteReadFragment = characteristicFragments.get(id);

                                if (characteristicExpansion.getVisibility() == View.GONE && notificationText.getCurrentTextColor() == ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_inactive)) {
                                    characteristicExpansion.setVisibility(View.VISIBLE);
                                }
                            } else {
                                currentWriteReadFragment = initFragmentCharacteristicDetail(bluetoothGattCharacteristic, id, service, characteristicExpansion, false);
                                characteristicFragments.put(id, currentWriteReadFragment);
                            }
                            setNotifyProperty(bluetoothGattCharacteristic, serviceName, notificationIcon, notificationText, indicationIcon, indicationText);
                        }
                    });

                    break;
                case Common.PROPERTY_VALUE_INDICATE:
                    propertyContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            indicationIcon.startAnimation(AnimationUtils.loadAnimation(DeviceServicesActivity.this, R.anim.property_image_click));

                            if (characteristicFragments.containsKey(id)) {
                                currentWriteReadFragment = characteristicFragments.get(id);

                                if (characteristicExpansion.getVisibility() == View.GONE && indicationText.getCurrentTextColor() == ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_inactive)) {
                                    characteristicExpansion.setVisibility(View.VISIBLE);
                                }
                            } else {
                                currentWriteReadFragment = initFragmentCharacteristicDetail(bluetoothGattCharacteristic, id, service, characteristicExpansion, false);
                                characteristicFragments.put(id, currentWriteReadFragment);
                            }
                            setIndicateProperty(bluetoothGattCharacteristic, serviceName, indicationIcon, indicationText, notificationIcon, notificationText);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    private FragmentCharacteristicDetail initFragmentCharacteristicDetail(BluetoothGattCharacteristic bluetoothGattCharacteristic, int expansionId, BluetoothGattService service, LinearLayout characteristicExpansion, boolean displayWriteDialog) {
        FragmentManager fragmentManager = getFragmentManager();

        FragmentCharacteristicDetail characteristicDetail = new FragmentCharacteristicDetail();
        characteristicDetail.address = bluetoothGatt.getDevice().getAddress();
        characteristicDetail.setmService(service);
        characteristicDetail.setmBluetoothCharact(bluetoothGattCharacteristic);
        characteristicDetail.displayWriteDialog = displayWriteDialog;

        characteristicExpansion.setVisibility(View.VISIBLE);

        // show characteristic's expansion and add the fragment to view/edit characteristic detail
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(expansionId, characteristicDetail, CHARACTERISTIC_ADD_FRAGMENT_TRANSACTION_ID);
        fragmentTransaction.commit();

        return characteristicDetail;
    }

    private void setIndicateProperty(BluetoothGattCharacteristic bluetoothGattCharacteristic, String serviceName, ImageView indicatePropertyIcon, TextView indicatePropertyName, ImageView notificationIcon, TextView notificationText) {
        boolean indicationsEnabled = currentWriteReadFragment.getIndicationsEnabled(); // Indication not enabled
        boolean submitted = BLEUtils.SetNotificationForCharacteristic(bluetoothGatt, bluetoothGattCharacteristic, indicationsEnabled ? Notifications.DISABLED : Notifications.INDICATE); // If indication not enabled -> enable
        if (submitted) {
            indicationsEnabled = !indicationsEnabled;
        }

        currentWriteReadFragment.setIndicationsEnabled(indicationsEnabled);
        indicatePropertyIcon.setBackgroundResource(indicationsEnabled ? R.drawable.ic_icon_indicate_on : R.drawable.ic_icon_indicate_off); // enable -> blue, disable -> grey
        indicatePropertyName.setTextColor(ContextCompat.getColor(DeviceServicesActivity.this, indicationsEnabled ? R.color.silabs_blue : R.color.silabs_inactive)); // enable -> blue, disable -> grey

        String characteristicUuid = getUuidFromBluetoothGattCharacteristic(bluetoothGattCharacteristic);
        serviceItemContainers.get(serviceName).setCharacteristicNotificationState(characteristicUuid, indicationsEnabled ? Notifications.INDICATE : Notifications.DISABLED);

        currentWriteReadFragment.setNotificationsEnabled(false);
        if (notificationIcon != null) {
            notificationIcon.setBackgroundResource(R.drawable.ic_icon_notify_off);
            notificationText.setTextColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_inactive));
        }
    }

    private void setNotifyProperty(BluetoothGattCharacteristic bluetoothGattCharacteristic, String serviceName, ImageView notifyPropertyIcon, TextView notifyPropertyName, ImageView indicationIcon, TextView indicationText) {
        boolean notificationsEnabled = currentWriteReadFragment.getNotificationsEnabled();
        boolean submitted = BLEUtils.SetNotificationForCharacteristic(bluetoothGatt, bluetoothGattCharacteristic, notificationsEnabled ? Notifications.DISABLED : Notifications.NOTIFY);
        if (submitted) {
            notificationsEnabled = !notificationsEnabled;
        }
        currentWriteReadFragment.setNotificationsEnabled(notificationsEnabled);
        notifyPropertyIcon.setBackgroundResource(notificationsEnabled ? R.drawable.ic_icon_notify_on : R.drawable.ic_icon_notify_off);
        notifyPropertyName.setTextColor(ContextCompat.getColor(DeviceServicesActivity.this, notificationsEnabled ? R.color.silabs_blue : R.color.silabs_inactive));

        String characteristicUuid = getUuidFromBluetoothGattCharacteristic(bluetoothGattCharacteristic);
        serviceItemContainers.get(serviceName).setCharacteristicNotificationState(characteristicUuid, notificationsEnabled ? Notifications.NOTIFY : Notifications.DISABLED);

        currentWriteReadFragment.setIndicationsEnabled(false);
        if (indicationIcon != null) {
            indicationIcon.setBackgroundResource(R.drawable.ic_icon_indicate_off);
            indicationText.setTextColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_inactive));
        }
    }

    private TextView getTextViewWithValue(LinearLayout propsContainer, String value) {
        for (int i = 0; i < propsContainer.getChildCount(); i++) {
            if (propsContainer.getChildAt(i).getTag() == null) {
                continue;
            }
            LinearLayout propertyContainer = (LinearLayout) propsContainer.getChildAt(i);
            for (int j = 0; j < propertyContainer.getChildCount(); j++) {
                View view = propertyContainer.getChildAt(j);
                if (view.getTag() != null && view.getTag().equals(PROPERTY_NAME_TAG)) {
                    String propertyValue = ((String) propertyContainer.getTag()).trim().toUpperCase();
                    if (propertyValue.equals(value)) {
                        return (TextView) view;
                    }
                }
            }
        }
        return null;
    }

    private ImageView getIconWithValue(LinearLayout propsContainer, String value) {
        for (int i = 0; i < propsContainer.getChildCount(); i++) {
            if (propsContainer.getChildAt(i).getTag() == null) {
                continue;
            }
            LinearLayout propertyContainer = (LinearLayout) propsContainer.getChildAt(i);
            for (int j = 0; j < propertyContainer.getChildCount(); j++) {
                View view = propertyContainer.getChildAt(j);
                if (view.getTag() != null && view.getTag().equals(PROPERTY_ICON_TAG)) {
                    String propertyValue = ((String) propertyContainer.getTag()).trim().toUpperCase();
                    if (propertyValue.equals(value)) {
                        return (ImageView) view;
                    }
                }
            }
        }
        return null;
    }

    /*
        If characteristic uuid is OTA-specific return its name
        else return unknown_characteristic
     */
    private String getOtaSpecificCharacteristicName(String uuid) {
        uuid = uuid.toUpperCase();
        switch (uuid) {
            case "F7BF3564-FB6D-4E53-88A4-5E37E0326063":
                return "OTA Control Attribute";
            case "984227F3-34FC-4045-A5D0-2C581F81A153":
                return "OTA Data Attribute";
            case "4F4A2368-8CCA-451E-BFFF-CF0E2EE23E9F":
                return "AppLoader version";
            case "4CC07BCF-0868-4B32-9DAD-BA4CC41E5316":
                return "OTA version";
            case "25F05C0A-E917-46E9-B2A5-AA2BE1245AFE":
                return "Gecko Bootloader version";
            case "0D77CC11-4AC1-49F2-BFA9-CD96AC7A92F8":
                return "Application version";
            default:
                return getString(R.string.unknown_characteristic_label);
        }

    }

    private String getUuidFromBluetoothGattCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        Characteristic characteristic = Engine.getInstance().getCharacteristic(bluetoothGattCharacteristic.getUuid());
        return (characteristic != null ? Common.getUuidText(characteristic.getUuid()) : Common.getUuidText(bluetoothGattCharacteristic.getUuid()));
    }

    /**
     * INITIALIZES ABOUT DIALOG
     *******************************************************/
    private void initAboutDialog() {
        dialogLicense = new Dialog(this);
        dialogLicense.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLicense.setContentView(R.layout.dialog_about_silicon_labs_blue_gecko);
        WebView webView = dialogLicense.findViewById(R.id.menu_item_license);
        Button closeButton = dialogLicense.findViewById(R.id.close_about_btn);
        webView.loadUrl(ABOUT_DIALOG_HTML_ASSET_FILE_PATH);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLicense.dismiss();
            }
        });
    }

    /**
     * INITIALIZES OTA PROGRESS DIALOG
     *******************************************************/
    private void initOtaProgress() {
        otaProgress = new Dialog(this);
        otaProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        otaProgress.setContentView(R.layout.ota_progress);
        TextView address = otaProgress.findViewById(R.id.device_address);
        address.setText(bluetoothGatt.getDevice().getAddress());
        progressBar = otaProgress.findViewById(R.id.otaprogress);
        dataRate = otaProgress.findViewById(R.id.datarate);
        datasize = otaProgress.findViewById(R.id.datasize);
        filename = otaProgress.findViewById(R.id.filename);
        steps = otaProgress.findViewById(R.id.otasteps);
        chrono = otaProgress.findViewById(R.id.chrono);
        OTAStart = otaProgress.findViewById(R.id.otabutton);
        sizename = otaProgress.findViewById(R.id.sizename);
        mtuname = otaProgress.findViewById(R.id.mtuname);
        uploadimage = otaProgress.findViewById(R.id.connecting_spinner);
        OTAStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otaProgress.dismiss();
                DFUMode("DISCONNECTION");
            }
        });
    }

    /**
     * INITIALIZES OTA SETUP DIALOG
     *******************************************************/
    private void initOtaSetup() {
        otaSetup = new Dialog(this);
        otaSetup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        otaSetup.setContentView(R.layout.ota_config);
        otaSetup.setCancelable(false);
        partialOTA = otaSetup.findViewById(R.id.radio_ota);
        TextView address = otaSetup.findViewById(R.id.device_address);
        address.setText(bluetoothGatt.getDevice().getAddress());
        fullOTA = otaSetup.findViewById(R.id.radio_ota_full);
        final LinearLayout stacklayout = otaSetup.findViewById(R.id.stacklayout);
        OTA_OK = otaSetup.findViewById(R.id.ota_proceed);
        Button OTA_CANCEL = otaSetup.findViewById(R.id.ota_cancel);
        reliableWrite = otaSetup.findViewById(R.id.check_reliable);
        delaySeekBar = otaSetup.findViewById(R.id.delay_seekBar);
        delayText = otaSetup.findViewById(R.id.delay_text);
        delayText.setVisibility(View.INVISIBLE);
        delaySeekBar.setVisibility(View.GONE);
        requestMTU = otaSetup.findViewById(R.id.mtu_seekBar);
        reliabilityRB = otaSetup.findViewById(R.id.reliability_radio_button);
        speedRB = otaSetup.findViewById(R.id.speed_radio_button);

        final EditText mtu_value = otaSetup.findViewById(R.id.mtu_value);

        mtu_value.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (mtu_value.getText() != null) {
                    int test = Integer.parseInt(mtu_value.getText().toString());
                    if (test < 23) test = 23;
                    else if (test > 250) test = 250;
                    requestMTU.setProgress(test - 23);
                    MTU = test;
                }
                return false;

            }
        });


        appLoaderFileButton = otaSetup.findViewById(R.id.select_apploader_file_btn);
        appFileButton = otaSetup.findViewById(R.id.select_app_file_btn);

        appLoaderFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("*/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                currentOtaFileType = OtaFileType.APPLOADER;
                startActivityForResult(Intent.createChooser(i, "Choose directory"), FILE_CHOOSER_REQUEST_CODE);
            }
        });

        appFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("*/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                currentOtaFileType = OtaFileType.APPLICATION;
                startActivityForResult(Intent.createChooser(i, "Choose directory"), FILE_CHOOSER_REQUEST_CODE);
            }
        });

        requestMTU.setMax(250 - 23);
        requestMTU.setProgress(250 - 23);
        requestMTU.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mtu_value.setText("" + (progress + 23));
                MTU = progress + 23;
            }
        });

        SeekBar requestPriority = otaSetup.findViewById(R.id.connection_seekBar);
        requestPriority.setMax(2);
        requestPriority.setProgress(2);
        priority = 1;
        requestPriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("onProgressChanged", "" + progress);
                if (progress == 1) priority = 0;//BALANCE
                else if (progress == 2) priority = 1;//HIGH
                else if (progress == 0) priority = 2;//LOW

            }
        });

        delaySeekBar.setMax(100);
        delaySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                delayText.setText("" + progress + " ms");
                delayNoResponse = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        OTA_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otaMode = false;
                otaSetup.dismiss();
                boolOTAbegin = false;
                ota_process = false;
            }
        });
        OTA_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OTA_OK.setClickable(false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        otaSetup.dismiss();
                        if (ota_mode) {
                            bluetoothGatt.requestMtu(Integer.parseInt(mtu_value.getText().toString()));
                        } else DFUMode("OTABEGIN");
                    }
                });
            }
        });
        fullOTA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stacklayout.setVisibility(View.VISIBLE);
                partialOTA.setBackgroundColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_red));
                fullOTA.setBackgroundColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_red_selected));
                doubleStepUpload = true;

                if (areFullOTAFilesCorrect()) {
                    OTA_OK.setClickable(true);
                    OTA_OK.setBackgroundColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_red));
                } else {
                    OTA_OK.setClickable(false);
                    OTA_OK.setBackgroundColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_button_inactive));
                }

            }
        });
        partialOTA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stacklayout.setVisibility(View.GONE);
                partialOTA.setBackgroundColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_red_selected));
                fullOTA.setBackgroundColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_red));
                doubleStepUpload = false;

                if (arePartialOTAFilesCorrect()) {
                    OTA_OK.setClickable(true);
                    OTA_OK.setBackgroundColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_red));
                } else {
                    OTA_OK.setClickable(false);
                    OTA_OK.setBackgroundColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_button_inactive));
                }

            }
        });

        reliableWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!reliableWrite.isChecked()) {
                    reliable = false;
                    delayText.setVisibility(View.VISIBLE);
                    delaySeekBar.setVisibility(View.VISIBLE);
                } else {
                    delayText.setVisibility(View.INVISIBLE);
                    delaySeekBar.setVisibility(View.GONE);
                    reliable = true;
                }
            }
        });

        reliabilityRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reliable = true;
            }
        });

        speedRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reliable = false;
            }
        });


    }

    /**
     * INITIALIZES MTU DIALOG
     *******************************************************/
    private void initNewMTU() {
        newMTU = new Dialog(this);
        newMTU.requestWindowFeature(Window.FEATURE_NO_TITLE);
        newMTU.setContentView(R.layout.newmtu);
        final TextView mtu_value = newMTU.findViewById(R.id.request_mtu_value);
        SeekBar requestMTU = newMTU.findViewById(R.id.request_mtu_seekBar);
        requestMTU.setMax(250 - 23);
        requestMTU.setProgress(250 - 23);
        requestMTUValue = requestMTU.getProgress() + 23;
        requestMTU.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mtu_value.setText("" + (progress + 23));
                requestMTUValue = progress + 23;
            }
        });

//        newMTU.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//            }
//        });
        Button requestBtn = newMTU.findViewById(R.id.request);
        Button cancelrequest = newMTU.findViewById(R.id.cancel_request);
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bluetoothGatt.requestMtu(requestMTUValue);

                newMTU.dismiss();
                if (!boolrequest_mtu) otaSetup.show();
            }
        });
        cancelrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newMTU.dismiss();
                if (!boolrequest_mtu) otaSetup.show();
            }
        });


    }

    /**
     * INITIALIZES CONNECTION INTERVAL DIALOG
     *******************************************************/
    private void initNewPriority() {
        newPriority = new Dialog(this);
        newPriority.requestWindowFeature(Window.FEATURE_NO_TITLE);
        newPriority.setContentView(R.layout.newpriority);
        Button request = newPriority.findViewById(R.id.request);
        Button cancelrequest = newPriority.findViewById(R.id.cancel_request);
        final CheckBox lowPriority = newPriority.findViewById(R.id.low_priority);
        final CheckBox balancedPriority = newPriority.findViewById(R.id.balanced_priority);
        final CheckBox highPriority = newPriority.findViewById(R.id.high_priority);
        lowPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lowPriority.isChecked()) {
                    if (highPriority.isChecked()) highPriority.setChecked(false);
                    if (balancedPriority.isChecked()) balancedPriority.setChecked(false);
                }
            }
        });
        balancedPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (balancedPriority.isChecked()) {
                    if (lowPriority.isChecked()) lowPriority.setChecked(false);
                    if (highPriority.isChecked()) highPriority.setChecked(false);
                }
            }
        });
        highPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (highPriority.isChecked()) {
                    if (lowPriority.isChecked()) lowPriority.setChecked(false);
                    if (balancedPriority.isChecked()) balancedPriority.setChecked(false);
                }
            }
        });
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (highPriority.isChecked() || balancedPriority.isChecked() || lowPriority.isChecked()) {
                    if (highPriority.isChecked()) {
                        bluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.CONNECTION_PRIORITY_HIGH), Toast.LENGTH_SHORT).show();
                    } else if (balancedPriority.isChecked()) {
                        bluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_BALANCED);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.CONNECTION_PRIORITY_BALANCED), Toast.LENGTH_SHORT).show();
                    } else if (lowPriority.isChecked()) {
                        bluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_LOW_POWER);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.CONNECTION_PRIORITY_LOW), Toast.LENGTH_SHORT).show();
                    }
                }
                newPriority.dismiss();
            }
        });
        cancelrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPriority.dismiss();
            }
        });
    }

    /**
     * INITIALIZES LOADING DIALOG
     *******************************************************/
    private void initLoading() {
        loadingdialog = new Dialog(this);
        loadingdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingdialog.setContentView(R.layout.loadingdialog);
        loadingimage = loadingdialog.findViewById(R.id.connecting_spinner);
        loadingLog = loadingdialog.findViewById(R.id.loadingLog);
        loadingHeader = loadingdialog.findViewById(R.id.loading_header);
    }

    /**
     * SHOWS OTA PROGRESS DIALOG IN UI
     *******************************************************/
    private void showOtaProgress() {
        otaProgress.show();
        OTAStart.setClickable(false);
        otaProgress.setCanceledOnTouchOutside(false);
        DFUMode("OTABEGIN"); //OTAProgress
    }

    /**
     * SHOWS OTA SETUP DIALOG IN UI
     *******************************************************/
    private void showOtaSetup() {
        if (otaSetup != null && !otaSetup.isShowing()) {
            otaSetup.show();
            otaSetup.setCanceledOnTouchOutside(false);

            if (areFullOTAFilesCorrect() && doubleStepUpload) {
                OTA_OK.setClickable(true);
                OTA_OK.setBackgroundColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_red));
            } else if (arePartialOTAFilesCorrect() && !doubleStepUpload) {
                OTA_OK.setClickable(true);
                OTA_OK.setBackgroundColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_red));
            } else {
                OTA_OK.setClickable(false);
                OTA_OK.setBackgroundColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_button_inactive));
            }

            if (reliable) {
                reliableWrite.setChecked(true);
            } else {
                delaySeekBar.setVisibility(View.VISIBLE);
                delayText.setVisibility(View.VISIBLE);
            }

        }
    }

    /**
     * SHOWS OTA SETUP DIALOG IN UI
     *******************************************************/
    private void showLoading() {
        if (loadingdialog != null) {
            loadingdialog.show();
            loadingdialog.setCanceledOnTouchOutside(false);
            animaloading();
        }
    }

    /**
     * SHOWS OTA REQUEST MTU DIALOG IN UI
     *******************************************************/
    private void showRequestMTU() {
        if (newMTU != null) {
            newMTU.show();
            newMTU.setCanceledOnTouchOutside(false);
        }
    }

    /**
     * SHOWS OTA CONNECTION INTERVAL DIALOG IN UI
     *******************************************************/
    private void showRequestPriority() {
        if (newPriority != null) {
            newPriority.show();
            newPriority.setCanceledOnTouchOutside(false);
        }
    }

    /**
     * SHOWS OTA ABOUT DIALOG IN UI
     *******************************************************/
    private void showAboutDialog() {
        dialogLicense.show();
    }

    public int generateNextId() {
        generatedId += 1;
        return generatedId;
    }

    public BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    /**
     * INITILIAZES ALL NECESSARY DIALOGS AND VIEW IN UI - ONCREATE
     ***********************/
    private void onGattFetched() {
        connectionsAdapter.setSelectedDevice(bluetoothGatt.getDevice().getAddress());
        connectionsAdapter.notifyDataSetChanged();
        String deviceName = bluetoothGatt.getDevice().getName();
        deviceName = TextUtils.isEmpty(deviceName) ? getString(R.string.not_advertising_shortcut) : deviceName;
        getSupportActionBar().setTitle(deviceName);
        servicesContainer.removeAllViews();
        initServicesViews();
        initAboutDialog();
        if (!boolOTAbegin) {
            initOtaSetup();
            initOtaProgress();
            initLoading();
            initNewMTU();
            initNewPriority();
        }
        if (btToolbarOpened) {
            closeToolbar();
            btToolbarOpened = !btToolbarOpened;
        }
    }

    /**
     * READ ALL THE SERVICES, PRINT IT ON LOG AND RECOGNIZES HOMEKIT ACCESSORIES
     *****************/
    public void getServicesInfo(BluetoothGatt gatt) {

        List<BluetoothGattService> gattServices = gatt.getServices();
        Log.i("onServicesDiscovered", "Services count: " + gattServices.size());

        for (BluetoothGattService gattService : gattServices) {
            String serviceUUID = gattService.getUuid().toString();
            Log.i("onServicesDiscovered", "Service UUID " + serviceUUID + " - Char count: " + gattService.getCharacteristics().size());
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {

                String CharacteristicUUID = gattCharacteristic.getUuid().toString();
                Log.i("onServicesDiscovered", "Characteristic UUID " + CharacteristicUUID + " - Properties: " + gattCharacteristic.getProperties());

                if (gattCharacteristic.getUuid().toString().equals(ota_control.toString())) {
                    if (gattCharacteristics.contains(bluetoothGatt.getService(ota_service).getCharacteristic(ota_data))) {
                        if (!gattServices.contains(bluetoothGatt.getService(homekit_service))) {
                            Log.i("onServicesDiscovered", "Device in DFU Mode");
                        } else {
                            Log.i("onServicesDiscovered", "OTA_Control found");
                            List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic.getDescriptors();

                            for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) {
                                String descriptor = gattDescriptor.getUuid().toString();

                                if (gattDescriptor.getUuid().toString().equals(homekit_descriptor.toString())) {
                                    kit_descriptor = gattDescriptor;
                                    Log.i("descriptor", "UUID: " + descriptor);
                                    //bluetoothGatt.readDescriptor(gattDescriptor);
                                    byte[] stable = {(byte) 0x00, (byte) 0x00};
                                    homeKitOTAControl(stable);
                                    homekit = true;

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * WRITES OTA CONTROL FOR HOMEKIT DEVICES
     *****************************************/
    public void homeKitOTAControl(byte[] instanceID) {

        //WRITE CHARACTERISTIC FOR HOMEKIT
        byte[] value = {0x00, 0x02, (byte) 0xee, instanceID[0], instanceID[1], 0x03, 0x00, 0x01, 0x01, 0x01};
        writeGenericCharacteristic(ota_service, ota_control, value);
        Log.d("characteristic", "writting: " + Converters.getHexValue(value));

    }

    /**
     * WRITES BYTE TO OTA CONTROL CHARACTERISTIC
     *****************************************/
    public boolean writeOtaControl(byte ctrl) {
        Log.d("writeOtaControl", "Called");

        if (bluetoothGatt.getService(ota_service) != null) {
            BluetoothGattCharacteristic charac = bluetoothGatt.getService(ota_service).getCharacteristic(ota_control);
            if (charac != null) {
                Log.d("Instance ID", "" + charac.getInstanceId());
                charac.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                Log.d("charac_properties", "" + charac.getProperties());
                byte[] control = new byte[1];
                control[0] = ctrl;
                charac.setValue(control);
                bluetoothGatt.writeCharacteristic(charac);
                return true;
            } else {
                Log.d("characteristic", "null");
            }
        } else {
            Log.d("service", "null");
        }
        return false;
    }

    /**
     * WRITES BYTE ARRAY TO A GENERIC CHARACTERISTIC
     *****************************************/
    public boolean writeGenericCharacteristic(UUID service, UUID characteristic, byte[] value) {

        if (bluetoothGatt != null) {

            BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGatt.getService(service).getCharacteristic(characteristic);
            Log.d("characteristic", "exists");

            if (bluetoothGattCharacteristic != null) {

                bluetoothGattCharacteristic.setValue(value);
                bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
                Log.d("characteristic", "written");

            } else {

                Log.d("characteristic", "null");
                return false;
            }

        } else {

            Log.d("bluetoothGatt", "null");
            return false;

        }

        return true;

    }

    //Not used - White with NO RESPONSE*************************************************/
    public synchronized void whiteOtaData(final byte[] datathread) {
        try {
            boolOTAdata = true;
            byte[] value = new byte[MTU - 3];
            long start = System.nanoTime();
            long current = System.currentTimeMillis();
            int j = 0;
            for (int i = 0; i < datathread.length; i++) {
                value[j] = datathread[i];
                j++;
                if (j >= MTU - 3 || i >= (datathread.length - 1)) {
                    long wait = System.nanoTime();

                    final BluetoothGattCharacteristic charac = bluetoothGatt.getService(ota_service).getCharacteristic(ota_data);
                    charac.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                    final float progress = ((float) (i + 1) / datathread.length) * 100;
                    final float bitrate = ((float) ((i + 1) * (8.0)) / ((float) ((wait - start) / 1000000.0)));
                    if (j < MTU - 3) {
                        byte[] end = new byte[j];
                        System.arraycopy(value, 0, end, 0, j);
                        Log.d("Progress", "sent " + (i + 1) + " / " + datathread.length + " - " + String.format("%.1f", progress) + " % - " + String.format("%.2fkbit/s", bitrate) + " - " + Converters.getHexValue(end));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                datasize.setText((int) progress + " %");
                                progressBar.setProgress((int) progress);
                            }
                        });

                        charac.setValue(end);
                    } else {
                        j = 0;
                        Log.d("Progress", "sent " + (i + 1) + " / " + datathread.length + " - " + String.format("%.1f", progress) + " % - " + String.format("%.2fkbit/s", bitrate) + " - " + Converters.getHexValue(value));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                datasize.setText((int) progress + " %");
                                progressBar.setProgress((int) progress);
                            }
                        });

                        charac.setValue(value);
                    }

                    if (bluetoothGatt.writeCharacteristic(charac)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String datarate = String.format(Locale.US, "%.2fkbit/s", bitrate);
                                dataRate.setText(datarate);
                                //String dataSize = String.format("%.2fkbit/s", (float) datathread.length/1000);
                            }
                        });

                        while ((System.nanoTime() - wait) / 1000000.0 < delayNoResponse) ;
                    } else {
                        do {
                            while ((System.nanoTime() - wait) / 1000000.0 < delayNoResponse) ;
                            wait = System.nanoTime();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String datarate = String.format(Locale.US, "%.2fkbit/s", bitrate);
                                    dataRate.setText(datarate);
                                    //String dataSize = String.format("%.2fkbit/s", (float) datathread.length/1000);
                                }
                            });

                        } while (!bluetoothGatt.writeCharacteristic(charac));
                    }
                }
            }
            long end = System.currentTimeMillis();
            float time = (end - start) / 1000L;
            Log.d("OTA Time - ", "" + time + "s");
            boolOTAdata = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chrono.stop();
                    uploadimage.clearAnimation();
                    uploadimage.setVisibility(View.INVISIBLE);
                }
            });
            DFUMode("OTAEND");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * WRITES EBL/GBL FILES TO OTA_DATA CHARACTERISTIC
     *****************************************/
    public synchronized void otaWriteDataReliable() {

        boolOTAdata = true;
        if (pack == 0) {
            /**SET MTU_divisible by 4*/
            int minus = 0;
            do {
                MTU_divisible = MTU - 3 - minus;
                minus++;
            } while (!(MTU_divisible % 4 == 0));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mtuname.setText(MTU_divisible + " bytes");
                }
            });
        }

        byte[] writearray;
        final float pgss;

        if (pack + MTU_divisible > otafile.length - 1) {
            /**SET last by 4*/
            int plus = 0;
            int last = otafile.length - pack;
            do {
                last = last + plus;
                plus++;
            } while (!(last % 4 == 0));
            writearray = new byte[last];
            int j = 0;
            for (int i = pack; i < pack + last; i++) {
                if (otafile.length - 1 < i) {
                    writearray[j] = (byte) 0xFF;
                } else writearray[j] = otafile[i];
                j++;

            }
            pgss = ((float) (pack + last) / (otafile.length - 1)) * 100;
            Log.d("characte", "last: " + pack + " / " + (pack + last) + " : " + Converters.getHexValue(writearray));
        } else {
            int j = 0;
            writearray = new byte[MTU_divisible];
            for (int i = pack; i < pack + MTU_divisible; i++) {
                writearray[j] = otafile[i];
                j++;
            }
            pgss = ((float) (pack + MTU_divisible) / (otafile.length - 1)) * 100;
            Log.d("characte", "pack: " + pack + " / " + (pack + MTU_divisible) + " : " + Converters.getHexValue(writearray));
        }

        BluetoothGattCharacteristic charac = bluetoothGatt.getService(ota_service).getCharacteristic(ota_data);
        charac.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        charac.setValue(writearray);
        bluetoothGatt.writeCharacteristic(charac);

        final long waiting_time = (System.currentTimeMillis() - otatime);
        final float bitrate = 8 * (float) pack / waiting_time;

        if (pack > 0) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress((int) pgss);
                            String datarate = String.format(Locale.US, "%.2fkbit/s", bitrate);
                            dataRate.setText(datarate);
                            datasize.setText((int) pgss + " %");

                        }
                    });
                }
            });
        } else {
            otatime = System.currentTimeMillis();
        }
    }

    /**
     * CREATES BAR PROGRESS ANIMATION IN LOADING AND OTA PROGRESS DIALOG
     **********************************/
    private void animaloading() {
        if (uploadimage != null && loadingimage != null && otaProgress != null) {
            uploadimage.setVisibility(View.GONE);
            loadingimage.setVisibility(View.GONE);
            if (loadingdialog.isShowing()) {
                loadingimage.setVisibility(View.VISIBLE);
            }
            if (otaProgress.isShowing()) {
                uploadimage.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * OTA STATE MACHINE
     */
    public synchronized void DFUMode(String step) {

        switch (step) {

            case "INIT":
                DFUMode("OTABEGIN");
                break;

            /**WRITES 0x00 TO OTA_CONTROL CHARACTERISTIC*/
            case "OTABEGIN":
                if (ota_mode) {
                    //START OTA PROCESS -> gattCallback -> OnCharacteristicWrite
                    Log.d("OTA_BEGIN", "true");
                    handler.postDelayed(WRITE_OTA_CONTROL_ZERO, 200);
                } else {
                    //PUT DEVICE IN DFUMODE -> gattCallback -> OnCharacteristicWrite
                    if (homekit) {
                        bluetoothGatt.readDescriptor(kit_descriptor);
                    } else {
                        Log.d("DFU_MODE", "true");
                        handler.postDelayed(WRITE_OTA_CONTROL_ZERO, 200);
                    }
                }
                break;

            /**SET THE FILES TO BE UPLOADED TO OTA_DATA CHARACTERISTIC*/
            case "OTAUPLOAD":
                Log.d("OTAUPLOAD", "Called");
                /**Check Services*/

                BluetoothGattService mBluetoothGattService = bluetoothGatt.getService(ota_service);
                if (mBluetoothGattService != null) {
                    BluetoothGattCharacteristic charac = bluetoothGatt.getService(ota_service).getCharacteristic(ota_data);
                    if (charac != null) {
                        charac.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                        Log.d("Instance ID", "" + charac.getInstanceId());

                        /**Check Files*/

                        byte[] ebl = null;
                        try {
                            Log.d("stackPath", "" + stackPath);
                            Log.d("appPath", "" + appPath);
                            File file;

                            if (!stackPath.equals("") && doubleStepUpload) {
                                file = new File(stackPath);
                                boolFullOTA = true;
                            } else {
                                file = new File(appPath);
                                boolFullOTA = false;
                            }
                            FileInputStream fileInputStream = new FileInputStream(file);
                            int size = fileInputStream.available();
                            Log.d("size", "" + size);
                            byte[] temp = new byte[size];
                            fileInputStream.read(temp);
                            fileInputStream.close();
                            ebl = temp;
                        } catch (Exception e) {
                            Log.e("InputStream", "Couldn't open file" + e);
                        }
                        final byte[] datathread = ebl;
                        otafile = ebl;

                        /**Check if it is partial of full OTA*/

                        final String fn;
                        if (!stackPath.equals("") && doubleStepUpload) {
                            int last = stackPath.lastIndexOf(File.separator);
                            fn = stackPath.substring(last);
                            Log.d("CurrentlyUpdating", "apploader");
                        } else {
                            int last = appPath.lastIndexOf(File.separator);
                            fn = appPath.substring(last);
                            Log.d("CurrentlyUpdating", "appliaction");
                        }
                        pack = 0;

                        /**Prepare information about current upload step*/

                        final String stepInfo;
                        if (doubleStepUpload) {
                            if (!stackPath.equals("")) {
                                stepInfo = "1 OF 2";
                            } else {
                                stepInfo = "2 OF 2";
                            }
                        } else {
                            stepInfo = "1 OF 1";
                        }

                        /**Set info into UI OTA Progress*/
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                filename.setText(fn);
                                steps.setText(stepInfo);
                                sizename.setText(datathread.length + " bytes");
                                mtuname.setText(Integer.toString(MTU));
                                uploadimage.setVisibility(View.VISIBLE);
                                animaloading();
                            }
                        });

                        /**Start OTA_data Upload in another thread*/
                        Thread otaUpload = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (reliable) {
                                    otaWriteDataReliable();
                                } else whiteOtaData(datathread);
                            }
                        });
                        otaUpload.start();
                    }
                }
                break;

            /**WRITES 0x03 TO OTA_CONTROL CHARACTERISTIC*/
            case "OTAEND":
                Log.d("OTAEND", "Called");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        writeOtaControl((byte) 0x03);
                    }
                }, 500);
                break;

            /**ENDS THE OTA PROCESS*/
            case "DISCONNECTION":
                ota_process = false;
                boolFullOTA = false;
                boolOTAbegin = false;
                disconnectGatt(bluetoothGatt);
                break;
            default:
                break;
        }

    }

    //Not used - Using reconnect() instead
    private void resetconnection() {
        Timer scanTimer = new Timer();

        if (bluetoothGatt != null) {

            reconnectaddress = bluetoothGatt.getDevice().getAddress();

            bluetoothDevice = bluetoothGatt.getDevice();

            bluetoothGatt.disconnect();
            service.clearCache();
            refreshDeviceCache(bluetoothGatt);


            scanTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    bluetoothBinding.unbind();
                    service.clearGatt();
                    bluetoothGatt.close();
                }
            }, 400);

            scanTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d("fetchUUIDs", "" + bluetoothDevice.fetchUuidsWithSdp());
                }
            }, 500);

            scanTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    bluetoothGatt = null;
                    bluetoothDevice = null;
                }
            }, 600);

        }

        scanTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            }
        }, 1500);

        scanTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startScanLeDevice();
                        loadingLog.setText("Scanning...");
                    }
                });
            }
        }, 3000);

    }

    //Not used - Reconnect with device after scanner
    private void reconnectGatt(final BluetoothDevice btDevice) {
        bluetoothDevice = btDevice;
        stopScan();

        Timer reconnectTimer = new Timer();
        reconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                bluetoothBinding = new BlueToothService.Binding(getApplicationContext()) {
                    @Override
                    protected void onBound(final BlueToothService service) {
                        service.connectGatt(bluetoothDevice, false, gattCallback);
                        bluetoothGatt = service.getConnectedGatt();
                    }
                };
                BlueToothService.bind(bluetoothBinding);
            }
        }, delayToConnect);
    }

    //Not Used - resetconnection
    private void startScanLeDevice() {
        ScanFilter macaddress = new ScanFilter.Builder().setDeviceAddress(reconnectaddress).build();
        ArrayList<ScanFilter> filters = new ArrayList<>();
        filters.add(macaddress);

        ScanSettings settings = new ScanSettings.Builder().build();
        bluetoothLeScanner.startScan(filters, settings, reScanCallback);

        Log.d("startScanLeDevice", "Scan Started");
    }

    //Not Used - resetconnection
    private void stopScan() {
        bluetoothLeScanner.stopScan(reScanCallback);
        Log.d("stopScan", "Called");
    }


    /**
     * CALLS A METHOD TO CLEAN DEVICE SERVICES
     ********************************************************/
    private boolean refreshDeviceCache(final BluetoothGatt gatt) {
        try {
            Log.d("refreshDevice", "Called");
            Method localMethod = bluetoothGatt.getClass().getMethod("refresh");
            if (localMethod != null) {
                boolean bool = ((Boolean) localMethod.invoke(bluetoothGatt, new Object[0])).booleanValue();
                Log.d("refreshDevice", "bool: " + bool);
                return bool;
            }
        } catch (Exception localException) {
            Log.e("refreshDevice", "An exception occured while refreshing device");
        }
        return false;
    }

    /**
     * DISCONNECT GATT GENTLY AND CLEAN GLOBAL VARIABLES
     ***************************************************/
    public void disconnectGatt(BluetoothGatt gatt) {
        Timer disconnectTimer = new Timer();
        boolFullOTA = false;
        boolOTAbegin = false;
        running = false;
        ota_process = false;
        disconnect_gatt = true;
        UICreated = false;
        if (gatt != null && gatt.getDevice() != null) {

            if (loadingdialog == null) {
                initLoading();
            }

            final BluetoothGatt btGatt = gatt;
            disconnectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    /**Getting bluetoothDevice to FetchUUID*/
                    if (btGatt.getDevice() != null) bluetoothDevice = btGatt.getDevice();
                    /**Disconnect gatt*/
                    btGatt.disconnect();
                    service.clearGatt();
                    Log.d("disconnectGatt", "gatt disconnect");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showLoading();
                            loadingLog.setText("Disconnecting...");
                            loadingHeader.setText("GATT Connection");
                        }
                    });
                }
            }, 200);

            disconnectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    bluetoothDevice.fetchUuidsWithSdp();
                }
            }, 300);


            disconnectionTimeout = true;

            Runnable timeout = new Runnable() {
                @Override
                public void run() {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (disconnectionTimeout) {
                                finish();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getBaseContext(), "DISCONNECTION PROBLEM", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }, 5000);
                }
            };
            new Thread(timeout).start();


        } else {
            finish();
        }
    }

    /**
     * CLEANS USER INTERFACE AND FINISH ACTIVITY
     *********************************************************/
    public void exit(BluetoothGatt gatt) {
        gatt.close();
        if (service.getConnectedGatt() != null) {
            service.getConnectedGatt().close();
        }
        service.clearCache();
        bluetoothBinding.unbind();
        disconnect_gatt = false;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bluetoothGatt = null;
                service = null;
                bluetoothBinding = null;
                if (loadingdialog != null && loadingdialog.isShowing()) loadingdialog.dismiss();
                if (otaProgress != null && otaProgress.isShowing()) otaProgress.dismiss();
                if (otaSetup != null && otaSetup.isShowing()) otaSetup.dismiss();
                finish();
            }
        }, 1000);
    }

    /**
     * DISCONNECTS AND CONNECTS WITH THE SELECTED DELAY
     *******************************************************/
    public void reconnect(long delaytoconnect) {

        Timer reconnectTimer = new Timer();
        bluetoothDevice = bluetoothGatt.getDevice();

        if (service.isGattConnected()) {
            service.clearGatt();
            service.clearCache();
        }

        bluetoothGatt.disconnect();

        reconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                bluetoothGatt.close();
                bluetoothBinding.unbind();
            }
        }, 400);


        reconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingdialog.isShowing()) {
                            loadingLog.setText("Attempting connection...");
                        }
                    }
                });
                bluetoothBinding = new BlueToothService.Binding(getApplicationContext()) {
                    @Override
                    protected void onBound(final BlueToothService service) {
                        bluetoothGatt = bluetoothDevice.connectGatt(getApplicationContext(), false, gattCallback);
                    }
                };
                BlueToothService.bind(bluetoothBinding);
            }
        }, delaytoconnect);
    }


    /**
     * ANIMATIONS CONTROLLERS
     ******************************************************************************/
    public void showCharacteristicLoadingAnimation() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // this onclicklistener prevents services and characteristics from user interaction before ui is loaded
                    }
                });
                Animation loadingGradientAnimation = AnimationUtils.loadAnimation(DeviceServicesActivity.this, R.anim.connection_translate_right);
                loadingContainer.setVisibility(View.VISIBLE);
                loadingGradientContainer.startAnimation(loadingGradientAnimation);
                Animation loadingBarFlyIn = AnimationUtils.loadAnimation(DeviceServicesActivity.this, R.anim.scanning_bar_fly_in);
                loadingBarContainer.startAnimation(loadingBarFlyIn);
            }
        });
    }

    public void hideCharacteristicLoadingAnimation() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingGradientContainer.clearAnimation();
                Animation loadingBarFlyIn = AnimationUtils.loadAnimation(DeviceServicesActivity.this, R.anim.scanning_bar_fly_out);
                loadingBarContainer.startAnimation(loadingBarFlyIn);

                loadingBarFlyIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        loadingContainer.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });
    }

    /********************************************************************************************************/

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("DeviceServices Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private void animateToolbarOpen(int openPercentHeight, int duration) {
        ValueAnimator animator = ValueAnimator.ofInt(0, percentHeightToPx(openPercentHeight)).setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                frameLayout.getLayoutParams().height = value;
                frameLayout.requestLayout();
            }
        });

        frameLayout.setVisibility(View.VISIBLE);
        ViewCompat.setTranslationZ(frameLayoutContainerRL, 5f);
        AnimatorSet set = new AnimatorSet();
        set.play(animator);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }

    private void closeToolbar() {
        animateToolbarClose(TOOLBAR_CLOSE_PERCENTACE, 300);
        setToolbarItemsNotClicked();
        bluetoothBrowserBackgroundRL.setVisibility(View.GONE);
    }

    private void setToolbarItemClicked(ImageView imageView, TextView textView) {
        textView.setTextColor(ContextCompat.getColor(this, R.color.silabs_blue));
        DrawableCompat.setTint(imageView.getDrawable(), ContextCompat.getColor(this, R.color.silabs_blue));
    }

    private void setToolbarItemsNotClicked() {
        connectionsTV.setTextColor(ContextCompat.getColor(this, R.color.silabs_primary_text));
        DrawableCompat.setTint(connectionsIV.getDrawable(), ContextCompat.getColor(this, R.color.silabs_primary_text));

        logTV.setTextColor(ContextCompat.getColor(this, R.color.silabs_primary_text));
        DrawableCompat.setTint(logIV.getDrawable(), ContextCompat.getColor(this, R.color.silabs_primary_text));
    }

    private void animateToolbarClose(int openPercentHeight, int duration) {
        ValueAnimator animator = ValueAnimator.ofInt(percentHeightToPx(openPercentHeight), 0).setDuration(duration);


        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                frameLayout.getLayoutParams().height = value;
                frameLayout.requestLayout();
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.play(animator);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }

    private int percentHeightToPx(int percent) {
        if (percent < 0 || percent > 100) throw new IllegalArgumentException();
        int height = servicesWrapper.getHeight();
        return (int) (((float) percent / 100.0) * height);
    }

    private void setToolbarFragment(Fragment fragment) {
        androidx.fragment.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void fragmentsInit() {
        loggerFragment = new LoggerFragment().setCallback(new ToolbarCallback() {
            @Override
            public void close() {
                closeToolbar();
                btToolbarOpened = !btToolbarOpened;
            }

            @Override
            public void submit(FilterDeviceParams filterDeviceParams, boolean close) {

            }
        });
        loggerFragment.setAdapter(new LogAdapter(Constants.LOGS, getApplicationContext()));


        connectionsFragment = new ConnectionsFragment().setCallback(new ToolbarCallback() {
            @Override
            public void close() {
                closeToolbar();
                btToolbarOpened = !btToolbarOpened;
            }

            @Override
            public void submit(FilterDeviceParams filterDeviceParams, boolean close) {

            }
        });
        connectionsAdapter = new ConnectionsAdapter(getConnectedBluetoothDevices(), getApplicationContext());
        connectionsFragment.setAdapter(connectionsAdapter);
        connectionsFragment.getAdapter().setServicesConnectionsCallback(this);
        connectionsTV.setText(getConnectedBluetoothDevices().size() + " Connections");
    }

    private List<BluetoothDevice> getConnectedBluetoothDevices() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        return bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(DeviceServicesActivity.this, getResources().getString(R.string.Permissions_granted_succesfully), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DeviceServicesActivity.this, R.string.permissions_not_granted, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDisconnectClicked(final BluetoothDeviceInfo deviceInfo) {
        final String currenctDeviceAddr = bluetoothGatt.getDevice().getAddress();

        bluetoothBinding = new BlueToothService.Binding(DeviceServicesActivity.this) {
            @Override
            protected void onBound(BlueToothService service) {
                boolean success = service.disconnectGatt(deviceInfo.getAddress());
                if (!success) {
                    Toast.makeText(DeviceServicesActivity.this, R.string.device_not_from_EFR, Toast.LENGTH_LONG).show();
                }
                updateCountOfConnectedDevices();
                connectionsFragment.getAdapter().notifyDataSetChanged();

                if (currenctDeviceAddr.equals(deviceInfo.getAddress())) {

                    if (getConnectedBluetoothDevices().size() <= 0) finish();
                    else {
                        BluetoothDevice device = getConnectedBluetoothDevices().get(0);
                        changeDevice(device.getAddress());
                    }
                }
            }
        };
        BlueToothService.bind(bluetoothBinding);

    }

    @Override
    public void onDeviceClicked(final BluetoothDeviceInfo device) {
        boolOTAbegin = false;
        Log.i(TAG, "On_Device_Clicked" + " Change Device Address");
        changeDevice(device.getAddress());
    }

    private void initDevice(final String deviceAddress) {
        handler = new Handler();
        bluetoothBinding = new BlueToothService.Binding(this) {
            @Override
            protected void onBound(BlueToothService service) {//todo dubel
                serviceHasBeenSet = true;
                DeviceServicesActivity.this.service = service;
                if (!service.isGattConnected(deviceAddress)) {
                    Toast.makeText(DeviceServicesActivity.this, R.string.toast_debug_connection_failed, Toast.LENGTH_LONG).show();
                    disconnectGatt(bluetoothGatt);
                } else {
                    BluetoothGatt bG = service.getConnectedGatt(deviceAddress);
                    if (bG == null) {
                        Toast.makeText(DeviceServicesActivity.this, R.string.device_not_from_EFR, Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                    service.registerGattCallback(true, gattCallback);
                    if (bG.getServices() != null && !bG.getServices().isEmpty()) {
                        bluetoothGatt = bG;
                        onGattFetched();
                    } else {
                        showCharacteristicLoadingAnimation();
                        bG.discoverServices();
                    }
                }
            }
        };

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BlueToothService.bind(bluetoothBinding);
                    }
                });
            }
        }, UI_CREATION_DELAY);
    }


    public void changeDevice(final String address) {
        servicesContainer.removeAllViews();
        loggerFragment.getAdapter().logByDeviceAddress(address);

        initDevice(address);
    }


    public void updateCountOfConnectedDevices() {
        final List<BluetoothDevice> connectedBluetoothDevices = getConnectedBluetoothDevices();
        final int size = connectedBluetoothDevices.size();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectionsTV.setText(getResources().getString(R.string.n_Connections, size));
                connectionsFragment.getAdapter().setConnectionsList(connectedBluetoothDevices);
                connectionsFragment.getAdapter().notifyDataSetChanged();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FILE_CHOOSER_REQUEST_CODE:
                    OtaFileType type = currentOtaFileType;
                    Uri uri = data.getData();
                    String filename;

                    try {
                        filename = getFileName(uri);
                    } catch (Exception e) {
                        filename = "";
                    }

                    if (!hasOtaFileCorrectExtension(filename)) {
                        Toast.makeText(DeviceServicesActivity.this, getResources().getString(R.string.Incorrect_file), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // APPLICATION
                    if (type.equals(OtaFileType.APPLICATION)) {
                        prepareOtaFile(uri, OtaFileType.APPLICATION, filename);
                        // APPLOADER
                    } else {
                        prepareOtaFile(uri, OtaFileType.APPLOADER, filename);
                    }
                    break;
            }
        }

        if (areFullOTAFilesCorrect() && doubleStepUpload) {
            OTA_OK.setClickable(true);
            OTA_OK.setBackgroundColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_red));
        } else if (arePartialOTAFilesCorrect() && !doubleStepUpload) {
            OTA_OK.setClickable(true);
            OTA_OK.setBackgroundColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_red));
        } else {
            OTA_OK.setClickable(false);
            OTA_OK.setBackgroundColor(ContextCompat.getColor(DeviceServicesActivity.this, R.color.silabs_button_inactive));
        }

    }


    private boolean areFullOTAFilesCorrect() {
        return !appFileButton.getText().equals(getString(R.string.Select_Application_gbl_file)) && !appLoaderFileButton.getText().equals(getString(R.string.Select_Apploader_gbl_file));
    }

    private boolean arePartialOTAFilesCorrect() {
        return !appFileButton.getText().equals(getString(R.string.Select_Application_gbl_file));
    }

    /*
    public String getFileName(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) return ""; //If cursor is null return empty string

        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        String name = cursor.getString(nameIndex);
        cursor.close();

        return name;
    }
*/
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public boolean hasOtaFileCorrectExtension(String filename) {
        if (filename.toUpperCase().contains(".GBL")) return true;
        return false;
    }

    public void prepareOtaFile(Uri uri, OtaFileType type, String filename) {

        try {
            InputStream is = getContentResolver().openInputStream(uri);

            if (is == null) {
                Toast.makeText(DeviceServicesActivity.this, getResources().getString(R.string.There_was_a_problem_while_preparing_the_file), Toast.LENGTH_SHORT).show();
                return;
            }

            File file = new File(getCacheDir(), filename);

            OutputStream output = new FileOutputStream(file);
            byte[] buffer = new byte[4 * 1024];
            int read;

            while ((read = is.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }

            if (type.equals(OtaFileType.APPLICATION)) {
                appPath = file.getAbsolutePath();
                appFileButton.setText(filename);
            } else {
                stackPath = file.getAbsolutePath();
                appLoaderFileButton.setText(filename);
            }

            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(DeviceServicesActivity.this, getResources().getString(R.string.Incorrect_file), Toast.LENGTH_SHORT).show();
        }

    }

}
