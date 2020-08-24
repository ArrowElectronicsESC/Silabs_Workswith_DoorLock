package com.siliconlabs.eic.doorlock.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.siliconlabs.eic.doorlock.R;
import com.siliconlabs.eic.doorlock.adapters.ConnectionsAdapter;
import com.siliconlabs.eic.doorlock.adapters.DebugModeDeviceAdapter;
import com.siliconlabs.eic.doorlock.adapters.DeviceInfoViewHolder;
import com.siliconlabs.eic.doorlock.adapters.LogAdapter;
import com.siliconlabs.eic.doorlock.ble.BlueToothService;
import com.siliconlabs.eic.doorlock.ble.BluetoothDeviceInfo;
import com.siliconlabs.eic.doorlock.ble.Discovery;
import com.siliconlabs.eic.doorlock.ble.ErrorCodes;
import com.siliconlabs.eic.doorlock.ble.TimeoutGattCallback;
import com.siliconlabs.eic.doorlock.bluetoothdatamodel.parsing.Common;
import com.siliconlabs.eic.doorlock.bluetoothdatamodel.parsing.Device;
import com.siliconlabs.eic.doorlock.bluetoothdatamodel.parsing.Engine;
import com.siliconlabs.eic.doorlock.fragment.LogFragment;
import com.siliconlabs.eic.doorlock.fragment.SearchFragment;
import com.siliconlabs.eic.doorlock.interfaces.DebugModeCallback;
import com.siliconlabs.eic.doorlock.interfaces.ServicesConnectionsCallback;
import com.siliconlabs.eic.doorlock.log.TimeoutLog;
import com.siliconlabs.eic.doorlock.toolbars.ConnectionsFragment;
import com.siliconlabs.eic.doorlock.toolbars.FilterFragment;
import com.siliconlabs.eic.doorlock.toolbars.LoggerFragment;
import com.siliconlabs.eic.doorlock.toolbars.ToolbarCallback;
import com.siliconlabs.eic.doorlock.utils.Constants;
import com.siliconlabs.eic.doorlock.utils.FilterDeviceParams;
import com.siliconlabs.eic.doorlock.utils.SharedPrefUtils;
import com.siliconlabs.eic.doorlock.utils.ToolbarName;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.siliconlabs.eic.doorlock.utils.Constants.REQUEST_APP_PERMISSION_CODE;
import static com.siliconlabs.eic.doorlock.utils.Constants.RESTART_SCAN_TIMEOUT;
import static com.siliconlabs.eic.doorlock.utils.Constants.TIMEOUT_SCAN_STOP;
import static com.siliconlabs.eic.doorlock.utils.Constants.TOOLBAR_CLOSE_PERCENTAGE;
import static com.siliconlabs.eic.doorlock.utils.Constants.TOOLBAR_OPEN_PERCENTAGE;


public class HomeActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, ServicesConnectionsCallback, Discovery.DeviceContainer, Discovery.BluetoothDiscoveryHost, DebugModeCallback {

    private static final String TAG = HomeActivity.class.getSimpleName();
    // bluetooth service related objects
    private final Discovery mBluetoothDiscovery = new Discovery(this, this);
    private final LinkedList<String> mErrorMessageQueue = new LinkedList<>();
    private final List<String> mAppPermissionList = new ArrayList<>();
    /*
    View objects binding
     */
    @InjectView(R.id.bluetooth_enable)
    RelativeLayout mBluetoothEnableBar;
    @InjectView(R.id.bluetooth_enable_msg)
    TextView mTxtBluetoothEnable;
    @InjectView(R.id.bluetooth_enable_btn)
    TextView mBtnEnableBluetooth;
    @InjectView(R.id.toolbar)
    Toolbar mToolBar;
    @InjectView(R.id.no_devices_found)
    LinearLayout mLayoutNoDeviceFound;
    @InjectView(R.id.looking_for_devices_background)
    LinearLayout mLayoutLookingForDevice;
    @InjectView(R.id.scanning_gradient_container)
    RelativeLayout mScanningGradientContainer;
    @InjectView(R.id.swipe_refresh_container)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.connecting_container)
    RelativeLayout mConnectingContainer;
    @InjectView(R.id.connecting_anim_gradient_right_container)
    LinearLayout mConnectingAnimationBar;
    @InjectView(R.id.connecting_bar_container)
    RelativeLayout mConnectingBarContainer;
    @InjectView(R.id.recyclerview_debug_devices)
    RecyclerView mDevicesRecyclerView;
    @InjectView(R.id.frame_layout)
    FrameLayout mFrameLayout;
    @InjectView(R.id.framelayout_container)
    RelativeLayout mTopBarFrameContainerRL;
    @InjectView(R.id.bluetooth_browser_background)
    RelativeLayout mBluetoothBrowserBackgroundRL;
    @InjectView(R.id.button_scanning)
    Button mButtonScan;
    // BluetoothBrowserToolbar view elements
    @InjectView(R.id.linearlayout_log)
    LinearLayout mLayoutLogsLL;
    @InjectView(R.id.imageview_log)
    ImageView mImageLogs;
    @InjectView(R.id.textview_log)
    TextView mTxtLogs;
    @InjectView(R.id.linearlayout_connections)
    LinearLayout mConnectionsBar;
    @InjectView(R.id.imageview_connections)
    ImageView mImageConnections;
    @InjectView(R.id.textview_connections)
    TextView mTxtConnections;
    @InjectView(R.id.linearlayout_filter)
    LinearLayout mLayoutFilterLL;
    @InjectView(R.id.imageview_filter)
    ImageView mImageFilter;
    @InjectView(R.id.textview_filter)
    TextView mTxtFilter;
    @InjectView(R.id.scan_anim_gradient_left)
    RelativeLayout mScanningAnimationLayoutLeft;
    @InjectView(R.id.scan_anim_gradient_right)
    RelativeLayout mScanningAnimationLayoutRight;
    @InjectView(R.id.imgScanning)
    ImageView mImageScanningAnim;
    @InjectView(R.id.scanningAnimationLayout)
    LinearLayout mScanningAnimationContainer;
    private TextView mTxtLog;
    private BlueToothService.Binding mBluetoothServiceBinder;
    private BluetoothAdapter mDefaultBluetoothAdapter;
    private DebugModeDeviceAdapter mDebugModeDeviceAdapter;
    private ConnectionsFragment mFragmentConnections;
    private ConnectionsAdapter mConnectionsAdapter;

    private BlueToothService mBluetoothService;
    private boolean mHasPermission = false;
    private FilterFragment mFragmentFilter;
    private LoggerFragment mFragmentLogs;

    private Context mContext;
    private boolean mIsBluetoothAdapterEnabled = false;
    private float mLastScale = 0.0f;
    private SharedPrefUtils mSharedPref;
    private boolean mScanRunning = false;
    private final Runnable stopScanningCallback = () -> {
        if (mScanRunning) {
            Log.d(TAG, "Scan Timeout");
            onScanningStopped();
        }
    };
    private boolean mAllowUpdating = true;
    private boolean mToolbarIsOpened = false;
    private ToolbarName mToolbarOpenedName = null;
    private Handler mHandler;
    private final Runnable mRestartScanTimeout = new Runnable() {
        @Override
        public void run() {
            mBluetoothDiscovery.clearDevicesCache();
            flushContainer();
            mSharedPref.mergeTmpDevicesToFavorites();
            mAllowUpdating = true;
            // If not scanning before - start scanning
            if (!mScanRunning) {
                startScanning();
            }
        }
    };
    private Toast mToast;
    private final Runnable mDisplayQueuedMessages = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(mDisplayQueuedMessages);
            synchronized (mDisplayQueuedMessages) {
                if (mErrorMessageQueue.size() > 0 && mToast != null && mToast.getView() != null && mToast.getView().isShown()) {
                    mHandler.postDelayed(mDisplayQueuedMessages, 1000);
                } else if (mErrorMessageQueue.size() > 0) {
                    mToast = Toast.makeText(mContext, mErrorMessageQueue.removeFirst(), Toast.LENGTH_LONG);
                    mToast.show();
                    mHandler.postDelayed(mDisplayQueuedMessages, 1000);
                }
            }
        }
    };
    private final BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            BluetoothAdapter defaultBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (action == null) {
                return;
            }

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        showEnableBluetoothAdapterBar();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                    case BluetoothAdapter.STATE_TURNING_ON:
                        mIsBluetoothAdapterEnabled = false;
                        break;
                    case BluetoothAdapter.STATE_ON:
                        if (defaultBluetoothAdapter != null &&
                                defaultBluetoothAdapter.isEnabled()) {
                            if (!mIsBluetoothAdapterEnabled) {
                                mToast = Toast.makeText(mContext,
                                        R.string.toast_bluetooth_enabled,
                                        Toast.LENGTH_SHORT);
                                mToast.show();
                            }

                            mBluetoothEnableBar.setVisibility(View.GONE);

                        }
                        mIsBluetoothAdapterEnabled = true;
                        break;
                }
            }
        }
    };
    private boolean mServiceHasBeenSet = false;
    private String mDeviceAddressToBeConnect = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        initHandler();
        initView();
        initListener();
        initDevicesRecyclerView();
        initFragments(savedInstanceState);
        handleToolbarClickEvents();
        if (mHasPermission) {
            initDiscoveryEngine();
        }
    }

    private void initListener() {
        findViewById(R.id.go_back_button).setOnClickListener(v -> onBackPressed());

        mBluetoothBrowserBackgroundRL.setOnClickListener(v -> {
            if (mToolbarIsOpened) {
                closeToolbar();
                mToolbarIsOpened = !mToolbarIsOpened;
            }
        });

        mBtnEnableBluetooth.setOnClickListener(v -> {
            BluetoothAdapter.getDefaultAdapter().enable();
            changeEnableBluetoothAdapterToConnecting();
        });

        mButtonScan.setOnClickListener(view -> onScanningButtonClicked());

    }

    private void initDiscoveryEngine() {
        mBluetoothDiscovery.connect(mContext);
        // init bluetooth discovery engine, matches to accepted bluetooth gatt profiles
        Engine.getInstance().init(mContext);
        if (mIsBluetoothAdapterEnabled) {
            startScanning();
        }
    }

    private void animateScanningBarFlyIn() {
        mScanningGradientContainer.setBackgroundColor(getColor(R.color.silabs_red_dark));
        mScanningAnimationContainer.setVisibility(View.VISIBLE);

        Animation animScanLeft = AnimationUtils.loadAnimation(mContext,
                R.anim.scan_translate_left);
        mScanningAnimationLayoutLeft.startAnimation(animScanLeft);

        Animation animScanRight = AnimationUtils.loadAnimation(mContext,
                R.anim.scan_translate_right);
        mScanningAnimationLayoutRight.startAnimation(animScanRight);

        mImageScanningAnim.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.scan_anim));

    }

    private void animateScanningBarFlyOut() {
        mScanningGradientContainer.setBackgroundColor(getColor(R.color.silabs_card_background));
        mScanningAnimationContainer.clearAnimation();
        mScanningAnimationContainer.setVisibility(View.GONE);
        mImageScanningAnim.clearAnimation();
    }


    private boolean checkForPermission() {
        mHasPermission = false;
        boolean locationPermission;
        boolean storagePermission;
        boolean isNewApi = false;


        if (!mAppPermissionList.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE) && (!mAppPermissionList.contains(Manifest.permission.ACCESS_FINE_LOCATION))) {
            mAppPermissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            mAppPermissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isNewApi = true;
            if (!mAppPermissionList.contains(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                mAppPermissionList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
            locationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
        } else {
            locationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }

        storagePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        String[] permissionArray = new String[mAppPermissionList.size()];
        for (int permissionListIndex = 0; permissionListIndex < mAppPermissionList.size(); permissionListIndex++) {
            permissionArray[permissionListIndex] = mAppPermissionList.get(permissionListIndex);
        }

        if (storagePermission && locationPermission) {
            mHasPermission = true;
            return true;
        } else if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED
                || (isNewApi && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PermissionChecker.PERMISSION_GRANTED)) {
            requestPermissions(permissionArray, REQUEST_APP_PERMISSION_CODE);
            return false;
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                || (isNewApi && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION))) {
            requestPermissions(permissionArray, REQUEST_APP_PERMISSION_CODE);
            return false;
        } else {
            requestPermissions(permissionArray, REQUEST_APP_PERMISSION_CODE);
            return false;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_APP_PERMISSION_CODE) {
            for (int resultIndex = 0; resultIndex <= grantResults.length - 1; resultIndex++) {
                if (resultIndex == 0) {
                    mHasPermission = grantResults[resultIndex] == PermissionChecker.PERMISSION_GRANTED;
                } else {
                    mHasPermission = mHasPermission && grantResults[resultIndex] == PermissionChecker.PERMISSION_GRANTED;
                }
            }
            if (mHasPermission) {
                initDiscoveryEngine();
            } else {
                Toast.makeText(mContext, "Missing Permissions", Toast.LENGTH_LONG).show();
                mHandler.postDelayed(() -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                    intent.setData(uri);
                    //mContext.startActivity(intent);
                }, 3000);
            }
        }
    }


    private void initView() {
        mContext = this;
        mSharedPref = new SharedPrefUtils(getApplicationContext());
        ButterKnife.inject(this);
        setSupportActionBar(mToolBar);
        Constants.clearLogs();
        setShowSpinnerDialogVisibility(false);
        initSwipeRefreshLayout();
        mScanningGradientContainer.setVisibility(View.GONE);
        mDefaultBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mDefaultBluetoothAdapter != null && !mDefaultBluetoothAdapter.isEnabled()) {
            showEnableBluetoothAdapterBar();
        }
        mHasPermission = checkForPermission();
    }


    private void initHandler() {
        mHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.i("handler", "receiving message");
                String sentString = msg.getData().getString("what");
                mTxtLog = findViewById(R.id.log_view);
                mTxtLog.setMovementMethod(new ScrollingMovementMethod());
                mTxtLog.append(sentString);
                mTxtLog.scrollTo(0, mTxtLog.getScrollY());
            }
        };
    }

    private void initFragments(final Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            LogFragment logFragment = new LogFragment();
            FragmentTransaction logFragmentTransaction = getSupportFragmentManager().beginTransaction();
            logFragmentTransaction.add(R.id.log_body, logFragment);
            logFragmentTransaction.commit();
        }
        if (savedInstanceState == null) {
            SearchFragment searchfragment = new SearchFragment();
            FragmentTransaction filterFragmentTransaction = getSupportFragmentManager().beginTransaction();
            filterFragmentTransaction.add(R.id.filter_body, searchfragment);
            filterFragmentTransaction.commit();
        }


        mFragmentLogs = new LoggerFragment().setCallback(new ToolbarCallback() {
            @Override
            public void close() {
                closeToolbar();
                mToolbarIsOpened = !mToolbarIsOpened;
            }

            @Override
            public void submit(FilterDeviceParams filterDeviceParams, boolean close) {

            }
        });

        mFragmentLogs.setAdapter(new LogAdapter(Constants.LOGS, getApplicationContext()));

        mFragmentConnections = new ConnectionsFragment().setCallback(new ToolbarCallback() {
            @Override
            public void close() {
                closeToolbar();
                mToolbarIsOpened = !mToolbarIsOpened;
                mDebugModeDeviceAdapter.notifyDataSetChanged();
                updateCountOfConnectedDevices();
            }

            @Override
            public void submit(FilterDeviceParams filterDeviceParams, boolean close) {

            }
        });
        mConnectionsAdapter = new ConnectionsAdapter(getConnectedBluetoothDevices(), getApplicationContext());
        mFragmentConnections.setAdapter(mConnectionsAdapter);
        mFragmentConnections.getAdapter().setServicesConnectionsCallback(this);
        mFragmentFilter = new FilterFragment();
    }

    private void initDevicesRecyclerView() {
        mSharedPref.mergeTmpDevicesToFavorites();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.generateLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mDevicesRecyclerView.setLayoutManager(layoutManager);

        mDebugModeDeviceAdapter = new DebugModeDeviceAdapter(mContext,
                HomeActivity.this,
                new DeviceInfoViewHolder.Generator(R.layout.bluetooth_browser_device_item) {
                    @Override
                    public DeviceInfoViewHolder generate(View itemView) {
                        return new DebugModeDeviceAdapter.ViewHolder(
                                mContext,
                                itemView,
                                HomeActivity.this);
                    }
                });
        mDebugModeDeviceAdapter.setDebugMode();
        mDevicesRecyclerView.setAdapter(mDebugModeDeviceAdapter);
        mDevicesRecyclerView.setHasFixedSize(true);
    }


    private void handleToolbarClickEvents() {
        setToolbarItemsNotClicked();
        mLayoutLogsLL.setOnClickListener(v -> {
            if (mToolbarIsOpened && mToolbarOpenedName == ToolbarName.LOGS) {
                mFragmentLogs.stopLogUpdater();
                closeToolbar();
                mToolbarIsOpened = !mToolbarIsOpened;
                return;
            }
            if (!mToolbarIsOpened) {
                mBluetoothBrowserBackgroundRL.setBackgroundColor(Color.parseColor("#99000000"));
                mBluetoothBrowserBackgroundRL.setVisibility(View.VISIBLE);
                ViewCompat.setTranslationZ(mBluetoothBrowserBackgroundRL, 4f);
                animateToolbarOpen(TOOLBAR_OPEN_PERCENTAGE, 300);
                mToolbarIsOpened = !mToolbarIsOpened;
            }
            setToolbarItemsNotClicked();
            setToolbarItemClicked(mImageLogs, mTxtLogs);
            mToolbarOpenedName = ToolbarName.LOGS;
            setToolbarFragment(mFragmentLogs);
            mFragmentLogs.runLogUpdater();
            mFragmentLogs.scrollToEnd();
        });

        mConnectionsBar.setOnClickListener(v -> {
            if (mToolbarIsOpened && mToolbarOpenedName == ToolbarName.CONNECTIONS) {
                closeToolbar();
                mToolbarIsOpened = !mToolbarIsOpened;
                return;
            }
            if (!mToolbarIsOpened) {
                mBluetoothBrowserBackgroundRL.setBackgroundColor(Color.parseColor("#99000000"));
                mBluetoothBrowserBackgroundRL.setVisibility(View.VISIBLE);
                ViewCompat.setTranslationZ(mBluetoothBrowserBackgroundRL, 4f);
                animateToolbarOpen(TOOLBAR_OPEN_PERCENTAGE, 300);
                mToolbarIsOpened = !mToolbarIsOpened;
            }
            setToolbarItemsNotClicked();
            setToolbarItemClicked(mImageConnections, mTxtConnections);
            mToolbarOpenedName = ToolbarName.CONNECTIONS;
            setToolbarFragment(mFragmentConnections);
        });

        mLayoutFilterLL.setOnClickListener(v -> {
            if (mToolbarIsOpened && mToolbarOpenedName == ToolbarName.FILTER) {
                closeToolbar();
                mToolbarIsOpened = !mToolbarIsOpened;
                return;
            }
            if (!mToolbarIsOpened) {
                mBluetoothBrowserBackgroundRL.setBackgroundColor(Color.parseColor("#99000000"));
                mBluetoothBrowserBackgroundRL.setVisibility(View.VISIBLE);
                ViewCompat.setTranslationZ(mBluetoothBrowserBackgroundRL, 4f);
                animateToolbarOpen(TOOLBAR_OPEN_PERCENTAGE, 300);
                mToolbarIsOpened = !mToolbarIsOpened;
            }
            setToolbarItemsNotClicked();
            setToolbarItemClicked(mImageFilter, mTxtFilter);
            mToolbarOpenedName = ToolbarName.FILTER;
            setToolbarFragment(mFragmentFilter.setCallback(new ToolbarCallback() {
                @Override
                public void close() {
                    closeToolbar();
                    mToolbarIsOpened = !mToolbarIsOpened;
                }

                @Override
                public void submit(FilterDeviceParams filterDeviceParams, boolean close) {
                    if (close) {
                        closeToolbar();
                        mToolbarIsOpened = !mToolbarIsOpened;
                    }
                    if (filterDeviceParams.isEmptyFilter()) {
                        mImageFilter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_icon_filter));
                    } else {
                        mImageFilter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_icon_filter_active));
                    }
                    filterDevices(filterDeviceParams);
                }
            }));
        });

    }

    void filterDevices(FilterDeviceParams filterDeviceParams) {
        mDebugModeDeviceAdapter.filterDevices(filterDeviceParams, true);
        mDebugModeDeviceAdapter.setDebugMode();
        mDevicesRecyclerView.setAdapter(mDebugModeDeviceAdapter);
        mDevicesRecyclerView.setHasFixedSize(true);
    }

    private void showConnectingAnimation() {
        runOnUiThread(() -> {
            mScanningGradientContainer.setVisibility(View.GONE);
            Animation connectingGradientAnimation = AnimationUtils.loadAnimation(mContext,
                    R.anim.connection_translate_right);
            mConnectingContainer.setVisibility(View.VISIBLE);
            mConnectingAnimationBar.startAnimation(connectingGradientAnimation);
            Animation connectingBarFlyIn = AnimationUtils.loadAnimation(mContext,
                    R.anim.scanning_bar_fly_in);
            mConnectingBarContainer.startAnimation(connectingBarFlyIn);
        });
    }

    private void hideConnectingAnimation() {
        runOnUiThread(() -> {
            mDebugModeDeviceAdapter.debugModeConnectingDevice = null;
            mDebugModeDeviceAdapter.notifyDataSetChanged();

            mConnectingContainer.setVisibility(View.GONE);
            mConnectingAnimationBar.clearAnimation();

            mScanningGradientContainer.setVisibility(View.VISIBLE);
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        configureFontScale();
        mScanningGradientContainer.setVisibility(View.VISIBLE);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoothStateReceiver, filter);
        if (!mScanRunning) {
            setScanningStatus(false);
            setScanningButtonStart();
        }

        mDefaultBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mDefaultBluetoothAdapter != null) {
            mIsBluetoothAdapterEnabled = mDefaultBluetoothAdapter.isEnabled();
            if (mIsBluetoothAdapterEnabled) {
                mBluetoothEnableBar.setVisibility(View.GONE);
            }
//            if (mBluetoothService != null) {
//                //mBluetoothService.clearGatt();
//                mBluetoothService = null;
//
//            }
//            if (mBluetoothServiceBinder != null) {
//                mBluetoothServiceBinder.unbind();
//                mServiceHasBeenSet = false;
//            }

        } else {
            mIsBluetoothAdapterEnabled = false;
        }

        updateCountOfConnectedDevices();
        mDebugModeDeviceAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onPause() {
        super.onPause();
        onScanningStopped();
        unregisterReceiver(mBluetoothStateReceiver);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "OnDestroy");
        mBluetoothServiceBinder = new BlueToothService.Binding(getApplicationContext()) {
            @Override
            protected void onBound(BlueToothService service) {
                for (BluetoothDevice bd : getConnectedBluetoothDevices()) {
                    service.disconnectGatt(bd.getAddress());
                }
                service.clearGatt();
                mBluetoothServiceBinder.unbind();
                Log.d(TAG, "OnDestroy: Unbinding Service");
            }
        };
        if (!mServiceHasBeenSet) {
            BlueToothService.bind(mBluetoothServiceBinder);
        }
        mBluetoothDiscovery.disconnect();
    }


    private void onScanningButtonClicked() {
        if (checkForPermission()) {
            if (mScanRunning || mButtonScan.getText().equals(getString(R.string.Stop_Scanning))) {
                mHandler.removeCallbacks(mRestartScanTimeout);
                mDevicesRecyclerView.setVisibility(View.VISIBLE);
                onScanningStopped();
            } else {
                startScanning();
            }
        }
    }

    private void startDoorLockActivity(boolean isNewConnection, String... extra) {
        Intent intent = new Intent(mContext, DoorLockActivity.class);
        if (extra != null && extra.length > 0) {
            intent.putExtra("DEVICE_SELECTED_ADDRESS", extra[0]);
        }
        intent.putExtra("IS_NEW_CONNECTION", isNewConnection);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }


    private void setToolbarFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext, android.R.color.holo_red_dark),
                ContextCompat.getColor(mContext, android.R.color.holo_orange_dark),
                ContextCompat.getColor(mContext, android.R.color.holo_orange_light),
                ContextCompat.getColor(mContext, android.R.color.holo_red_light));
    }

    @Override
    public void onRefresh() {
        mHandler.removeCallbacks(mRestartScanTimeout);
        if (checkForPermission()) {
            mAllowUpdating = false;
            mDevicesRecyclerView.setVisibility(View.GONE);
            mLayoutNoDeviceFound.setVisibility(View.GONE);
            mLayoutLookingForDevice.setVisibility(View.VISIBLE);
            setScanningButtonStop();
            mHandler.postDelayed(mRestartScanTimeout, RESTART_SCAN_TIMEOUT);
        }
        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));
    }

    private void showEnableBluetoothAdapterBar() {
        runOnUiThread(() -> {
            mTxtBluetoothEnable.setText(R.string.bluetooth_adapter_bar_disabled);
            mBluetoothEnableBar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.silabs_red_dark));
            mBtnEnableBluetooth.setVisibility(View.VISIBLE);
            mBluetoothEnableBar.setVisibility(View.VISIBLE);
            mBluetoothEnableBar.postInvalidate();
        });
    }

    // Displays scanning status in UI and starts scanning for new BLE devices
    private void startScanning() {
        mHandler.removeCallbacks(mRestartScanTimeout);
        mHandler.postDelayed(stopScanningCallback, TIMEOUT_SCAN_STOP);
        setScanningButtonStop();
        mScanRunning = true;
        setScanningProgress(true);
        setScanningStatus(true);
        mDebugModeDeviceAdapter.setRunUpdater(true);
        // Connected devices are not deleted from list
        reDiscover(false);
    }

    private void onScanningStopped() {
        setScanningButtonStart();
        mScanRunning = false;
        mBluetoothDiscovery.stopDiscovery(false);
        setScanningStatus(mDebugModeDeviceAdapter.getItemCount() > 0);
        setScanningProgress(false);
        mDebugModeDeviceAdapter.setRunUpdater(false);
        int numbDevicesCurrentlyDisplaying = mDebugModeDeviceAdapter.getItemCount();

        if (numbDevicesCurrentlyDisplaying > 0) {
            mLayoutNoDeviceFound.setVisibility(View.GONE);
            mLayoutLookingForDevice.setVisibility(View.GONE);
        }
    }

    private void setScanningStatus(boolean foundDevices) {
        if (!foundDevices) {
            mLayoutNoDeviceFound.setVisibility(View.VISIBLE);
            mLayoutLookingForDevice.setVisibility(View.GONE);
            animateScanningBarFlyOut();
        }
    }

    private void setScanningProgress(boolean isScanning) {
        if (mDebugModeDeviceAdapter.getItemCount() == 0) {
            if (isScanning) {
                mLayoutLookingForDevice.setVisibility(View.VISIBLE);
                mLayoutNoDeviceFound.setVisibility(View.GONE);
                animateScanningBarFlyIn();
            } else {
                mLayoutLookingForDevice.setVisibility(View.GONE);
                mLayoutNoDeviceFound.setVisibility(View.VISIBLE);
                mImageScanningAnim.clearAnimation();
                animateScanningBarFlyOut();
            }
        }
    }

    public void setScanningButtonStart() {
        mButtonScan.setText(getResources().getString(R.string.Start_Scanning));
        mButtonScan.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.silabs_blue)));
        animateScanningBarFlyOut();
        mImageScanningAnim.clearAnimation();
    }

    public void setScanningButtonStop() {
        mButtonScan.setText(getResources().getString(R.string.Stop_Scanning));
        mButtonScan.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.silabs_red)));
        animateScanningBarFlyIn();
        mImageScanningAnim.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.scan_anim));
    }

    private void changeEnableBluetoothAdapterToConnecting() {
        runOnUiThread(() -> {
            BluetoothAdapter.getDefaultAdapter().enable();
            mBtnEnableBluetooth.setVisibility(View.GONE);
            mTxtBluetoothEnable.setText(R.string.bluetooth_adapter_bar_turning_on);
            mBluetoothEnableBar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.silabs_blue));
        });
    }

    private boolean refreshDeviceCache(BluetoothGatt gatt) {
        try {
            Log.d("refreshDevice", "Called");
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh");
            if (localMethod != null) {
                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                Log.d("refreshDevice", "bool: " + bool);
                return bool;
            }
        } catch (Exception localException) {
            Log.e("refreshDevice", "An exception occured while refreshing device");
        }
        return false;
    }


    private List<BluetoothDevice> getConnectedBluetoothDevices() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        return bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
    }

    public void reDiscover(boolean clearCachedDiscoveries) {
        mBluetoothDiscovery.startDiscovery(clearCachedDiscoveries);
    }

    private void closeToolbar() {
        runOnUiThread(() -> {
            animateToolbarClose(TOOLBAR_CLOSE_PERCENTAGE, 300);
            setToolbarItemsNotClicked();
            mBluetoothBrowserBackgroundRL.setVisibility(View.GONE);
            hideKeyboard();
        });

    }

    private void setToolbarItemClicked(ImageView imageView, TextView textView) {
        textView.setTextColor(ContextCompat.getColor(this, R.color.silabs_blue));
        DrawableCompat.setTint(imageView.getDrawable(), ContextCompat.getColor(this, R.color.silabs_blue));
    }

    private void setToolbarItemsNotClicked() {
        mTxtLogs.setTextColor(ContextCompat.getColor(this, R.color.silabs_primary_text));
        DrawableCompat.setTint(mImageLogs.getDrawable(), ContextCompat.getColor(this, R.color.silabs_primary_text));

        mTxtConnections.setTextColor(ContextCompat.getColor(this, R.color.silabs_primary_text));
        DrawableCompat.setTint(mImageConnections.getDrawable(), ContextCompat.getColor(this, R.color.silabs_primary_text));

        mTxtFilter.setTextColor(ContextCompat.getColor(this, R.color.silabs_primary_text));
        DrawableCompat.setTint(mImageFilter.getDrawable(), ContextCompat.getColor(this, R.color.silabs_primary_text));
    }

    private void animateToolbarOpen(int openPercentHeight, int duration) {
        ValueAnimator animator = ValueAnimator.ofInt(0, percentHeightToPx(openPercentHeight)).setDuration(duration);
        animator.addUpdateListener(animation -> {
            mFrameLayout.getLayoutParams().height = (Integer) animation.getAnimatedValue();
            mFrameLayout.requestLayout();
        });

        mFrameLayout.setVisibility(View.VISIBLE);
        ViewCompat.setTranslationZ(mTopBarFrameContainerRL, 5f);
        AnimatorSet set = new AnimatorSet();
        set.play(animator);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }

    private void animateToolbarClose(int openPercentHeight, int duration) {
        ValueAnimator animator = ValueAnimator.ofInt(percentHeightToPx(openPercentHeight), 0).setDuration(duration);


        animator.addUpdateListener(animation -> {
            mFrameLayout.getLayoutParams().height = (Integer) animation.getAnimatedValue();
            mFrameLayout.requestLayout();
        });

        AnimatorSet set = new AnimatorSet();
        mHandler.post(() -> {
            set.play(animator);
            set.setInterpolator(new AccelerateDecelerateInterpolator());
            set.start();
        });

    }

    private int percentHeightToPx(int percent) {
        if (percent < 0 || percent > 100) throw new IllegalArgumentException();
        int height = mDevicesRecyclerView.getHeight() + mScanningGradientContainer.getHeight();
        return (int) (((float) percent / 100.0) * height);
    }


    // Configures number of shown advertisement types
    private void configureFontScale() {
        float scale = getResources().getConfiguration().fontScale;
        if (mLastScale != scale) {
            mLastScale = scale;
            if (mLastScale == Common.FONT_SCALE_LARGE) {
                Device.MAX_EXTRA_DATA = 2;
            } else if (mLastScale == Common.FONT_SCALE_XLARGE) {
                Device.MAX_EXTRA_DATA = 1;
            } else {
                Device.MAX_EXTRA_DATA = 3;
            }
        }
    }


    @Override
    public void onDisconnectClicked(final BluetoothDeviceInfo deviceInfo) {
        Log.d(TAG, "onDisconnectClicked");
        mBluetoothServiceBinder = new BlueToothService.Binding(getApplicationContext()) {
            @Override
            protected void onBound(BlueToothService service) {
                boolean successDisconnected = service.disconnectGatt(deviceInfo.getAddress());
                if (!successDisconnected) {
                    mToast = Toast.makeText(getApplicationContext(), R.string.device_not_from_EFR, Toast.LENGTH_LONG);
                    mToast.show();
                }
                updateCountOfConnectedDevices();
                mDebugModeDeviceAdapter.notifyDataSetChanged();
            }
        };

        BlueToothService.bind(mBluetoothServiceBinder);
    }

    @Override
    public void onDeviceClicked(final BluetoothDeviceInfo device) {
        if (mBluetoothService != null && mBluetoothService.getConnectedGatt() != null) {
            if (mBluetoothService.isGattConnected(device.getAddress())) {
                Log.v(TAG, "On_Device_Clicked" + " Gatt Connected.. Launching Parameter Screen");
                startDoorLockActivity(false, device.getAddress());
            } else {
                Log.v(TAG, "On_Device_Clicked" + " Not Connected.. Connecting");
                connectToDevice(device);
            }
        } else {
            Log.v(TAG, "On_Device_Clicked" + " Service is Null Or Not Connected.. Connecting");
            connectToDevice(device);
        }

    }

    @Override
    public void flushContainer() {
        mDebugModeDeviceAdapter.flushContainer();
    }

    @Override
    public void updateWithDevices(final List devices) {
        if (mAllowUpdating) {
            mDebugModeDeviceAdapter.updateWith(devices);
        } else {
            return;
        }

        if (mDebugModeDeviceAdapter.getItemCount() > 0) {
            mLayoutLookingForDevice.setVisibility(View.GONE);
            mLayoutNoDeviceFound.setVisibility(View.GONE);
            mDevicesRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mLayoutLookingForDevice.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean isReady() {
        return !isFinishing();
    }

    @Override
    public void reDiscover() {
        reDiscover(false);
    }

    @Override
    public void onAdapterDisabled() {

    }

    @Override
    public void onAdapterEnabled() {

    }

    @Override
    public void connectToDevice(final BluetoothDeviceInfo deviceInfo) {
        if (deviceInfo == null) {
            Log.e("deviceInfo", "null");
            return;
        }

        mDeviceAddressToBeConnect = deviceInfo.getAddress();

        if (!mDefaultBluetoothAdapter.isEnabled()) {
            return;
        }

        if (mScanRunning) {
            onScanningStopped();
        }


        if (mBluetoothServiceBinder != null) {
            if (mBluetoothService != null && mBluetoothService.isGattConnected(deviceInfo.getAddress())) {
                Log.d(TAG, "Already Connected");
                startDoorLockActivity(false, deviceInfo.getAddress());
            } else {
                Log.d(TAG, "Removing Binder");
                mBluetoothServiceBinder.unbind();
                mServiceHasBeenSet = false;
            }
        } else {
            Log.e(TAG, "Binder is null");
        }


        if (deviceInfo.isConnectable || !deviceInfo.isConnected()) {
            showConnectingAnimation();
        }


        mBluetoothServiceBinder = new BlueToothService.Binding(mContext) {
            @Override
            protected void onBound(final BlueToothService service) {
                mBluetoothService = service;
                mServiceHasBeenSet = true;
                if (service.isGattConnected(deviceInfo.getAddress())) {
                    mDeviceAddressToBeConnect = "";
                    hideConnectingAnimation();
                    if (mToolbarIsOpened) {
                        closeToolbar();
                        mToolbarIsOpened = !mToolbarIsOpened;
                    }
                    startDoorLockActivity(true, deviceInfo.getAddress());
                    return;
                }

                service.connectGatt(deviceInfo.device, false, new TimeoutGattCallback() {
                    @Override
                    public void onTimeout() {
                        Constants.LOGS.add(new TimeoutLog(deviceInfo.device));
                        mToast = Toast.makeText(mContext,
                                R.string.toast_connection_timed_out,
                                Toast.LENGTH_SHORT);
                        mToast.show();
                        hideConnectingAnimation();
                        mDeviceAddressToBeConnect = "";
                    }

                    @Override
                    public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
                        super.onConnectionStateChange(gatt, status, newState);
                        updateCountOfConnectedDevices();
                        service.gattMap.put(deviceInfo.getAddress(), gatt);
                        hideConnectingAnimation();

                        if (newState == BluetoothGatt.STATE_DISCONNECTED && status != BluetoothGatt.GATT_SUCCESS) {
                            final String deviceName = TextUtils.isEmpty(deviceInfo.getName()) ? getString(R.string.not_advertising_shortcut) : deviceInfo.getName();
                            if (gatt.getDevice().getAddress().equals(mDeviceAddressToBeConnect)) {
                                mDeviceAddressToBeConnect = "";
                                synchronized (mErrorMessageQueue) {
                                    mErrorMessageQueue.add(ErrorCodes.getFailedConnectingToDeviceMessage(deviceName, status));
                                }
                            } else {
                                synchronized (mErrorMessageQueue) {
                                    mErrorMessageQueue.add(ErrorCodes.getDeviceDisconnectedMessage(deviceName, status));
                                }
                            }

//                            handler.removeCallbacks(displayQueuedMessages);
//                            handler.postDelayed(displayQueuedMessages, 500);

                        } else if (newState == BluetoothGatt.STATE_CONNECTED && status == BluetoothGatt.GATT_SUCCESS) {
                            //refreshDeviceCache(gatt);
                            if (service.isGattConnected()) {
                                mDeviceAddressToBeConnect = "";
                                if (mToolbarIsOpened) {
                                    closeToolbar();
                                    mToolbarIsOpened = !mToolbarIsOpened;
                                }
                                startDoorLockActivity(true, deviceInfo.getAddress());
                            }
                        } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                            Log.d("STATE_DISCONNECTED", "Called");
                            gatt.close();
                            service.clearGatt();
                            mBluetoothServiceBinder.unbind();
                        }
                    }
                });
            }
        };

        if (!mServiceHasBeenSet) {
            mServiceHasBeenSet = true;
            BlueToothService.bind(mBluetoothServiceBinder);
        }
    }

    @Override
    public void addToFavorite(String deviceAddress) {
        mSharedPref.addDeviceToFavorites(deviceAddress);
    }

    @Override
    public void removeFromFavorite(String deviceAddress) {
        mSharedPref.removeDeviceFromFavorites(deviceAddress);
        mSharedPref.removeDeviceFromTemporaryFavorites(deviceAddress);
    }

    @Override
    public void addToTemporaryFavorites(String deviceAddress) {
        mSharedPref.addDeviceToTemporaryFavorites(deviceAddress);
    }

    @Override
    public void updateCountOfConnectedDevices() {
        final List<BluetoothDevice> connectedBluetoothDevices = getConnectedBluetoothDevices();
        final int size = connectedBluetoothDevices.size();
        runOnUiThread(() -> {
            mTxtConnections.setText(String.format(Locale.getDefault(), "%d Connections", size));
            mFragmentConnections.getAdapter().setConnectionsList(connectedBluetoothDevices);
            mFragmentConnections.getAdapter().notifyDataSetChanged();
        });
    }

    @Override
    public void gotoDeviceProperty(BluetoothDeviceInfo device) {
        if (mBluetoothService != null && mBluetoothService.getConnectedGatt() != null) {
            if (mBluetoothService.isGattConnected(device.getAddress())) {
                Log.v(TAG, "Goto_Device_Property" + " Gatt Connected.. Launching Parameter Screen");
                startDoorLockActivity(false, device.getAddress());
            } else {
                Log.v(TAG, "Goto_Device_Property" + " Not Connected.. Connecting");
                connectToDevice(device);
            }
        } else {
            Log.v(TAG, "Goto_Device_Property" + "Service is Null or Not Connected.. Connecting");
            connectToDevice(device);
        }

    }

}
