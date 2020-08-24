package com.siliconlabs.eic.doorlock.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.siliconlabs.eic.doorlock.R;
import com.siliconlabs.eic.doorlock.models.DoorStatusReading;

import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.siliconlabs.eic.doorlock.constants.DoorLockEvents.UI_EVENT_PASSKEY_COMPLETE;

/**
 * Fragment for 3x4 keypad matrix screen for entering the secret key
 *
 * @author Vipul Kole (vipul.kole@einfochips.com).
 * Created on: 15-07-2020.
 * Company:  e-Infochips Pvt Ltd,Pune,India.
 * Copyright (c) 2020 Silicon Labs, All rights reserved.
 */
public class FragmentKeypad extends Fragment implements View.OnClickListener {

    /*
     * View object binding
     */
    @InjectView(R.id.btn_key_0)
    Button mButtonZero;
    @InjectView(R.id.btn_key_1)
    Button mButtonOne;
    @InjectView(R.id.btn_key_2)
    Button mButtonTwo;
    @InjectView(R.id.btn_key_3)
    Button mButtonThree;
    @InjectView(R.id.btn_key_4)
    Button mButtonFour;
    @InjectView(R.id.btn_key_5)
    Button mButtonFive;
    @InjectView(R.id.btn_key_6)
    Button mButtonSix;
    @InjectView(R.id.btn_key_7)
    Button mButtonSeven;
    @InjectView(R.id.btn_key_8)
    Button mButtonEight;
    @InjectView(R.id.btn_key_9)
    Button mButtonNine;
    @InjectView(R.id.btn_key_clear)
    Button mButtonClear;
    @InjectView(R.id.btn_key_backspace)
    Button mButtonBackspace;
    @InjectView(R.id.passkey_indicator_level1)
    TextView mTxtLevel1;
    @InjectView(R.id.passkey_indicator_level2)
    TextView mTxtLevel2;
    @InjectView(R.id.passkey_indicator_level3)
    TextView mTxtLevel3;
    @InjectView(R.id.passkey_indicator_level4)
    TextView mTxtLevel4;
    @InjectView(R.id.loading_spinner)
    ProgressBar mProgressBar;
    @InjectView(R.id.go_back_button)
    View mGoBackButton;
    private String mSecretKeyValue = "";
    private boolean mEditable = true;


    public FragmentKeypad() {
        // Required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_keypad, container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePasskey();
    }

    /**
     * Initializes the UI component and registers the event listeners
     */
    private void initView() {
        mEditable = true;
        mSecretKeyValue = "";
        mProgressBar.setVisibility(View.GONE);

        updatePasskey();
        mButtonZero.setOnClickListener(this);
        mButtonOne.setOnClickListener(this);
        mButtonTwo.setOnClickListener(this);
        mButtonThree.setOnClickListener(this);
        mButtonFour.setOnClickListener(this);
        mButtonFive.setOnClickListener(this);
        mButtonSix.setOnClickListener(this);
        mButtonSeven.setOnClickListener(this);
        mButtonEight.setOnClickListener(this);
        mButtonNine.setOnClickListener(this);
        mButtonClear.setOnClickListener(this);
        mButtonBackspace.setOnClickListener(this);
        mGoBackButton.setOnClickListener(view -> Objects.requireNonNull(getActivity()).onBackPressed());
    }

    @Override
    public void onClick(View view) {
        if (!mEditable) {
            return;
        }
        switch (view.getTag().toString().toLowerCase()) {
            case "number_button":
                if (mSecretKeyValue.length() == 4) {
                    mEditable = false;
                }
                if (mSecretKeyValue.length() < 4) {
                    mSecretKeyValue = mSecretKeyValue + ((Button) view).getText().toString().trim();
                }
                updatePasskey();
                triggerEventAndExit();
                break;
            case "clear":
                if (!mEditable) return;
                mSecretKeyValue = "";
                updatePasskey();
                break;
            case "backspace":
                if (!mEditable) return;
                if (mSecretKeyValue.length() > 0) {
                    mSecretKeyValue = mSecretKeyValue.substring(0, mSecretKeyValue.length() - 1);
                    updatePasskey();
                }
                break;
            default:
                break;
        }

    }

    /**
     * It wil send an event when user completes the 4 digit secret key
     */
    private void triggerEventAndExit() {
        if (mSecretKeyValue.length() == 4) {
            mEditable = false;
            mProgressBar.setVisibility(View.VISIBLE);
            DoorStatusReading.getInstance().onEvent(UI_EVENT_PASSKEY_COMPLETE, mSecretKeyValue);
        }

    }


    /**
     * updates the passkey level indicator on UI
     */
    private void updatePasskey() {
        if (!TextUtils.isEmpty(mSecretKeyValue)) {
            if (mSecretKeyValue.length() == 1) {
                mTxtLevel1.setTextColor(Color.GREEN);
                mTxtLevel2.setTextColor(Color.WHITE);
                mTxtLevel3.setTextColor(Color.WHITE);
                mTxtLevel4.setTextColor(Color.WHITE);
            } else if (mSecretKeyValue.length() == 2) {
                mTxtLevel2.setTextColor(Color.GREEN);
                mTxtLevel3.setTextColor(Color.WHITE);
                mTxtLevel4.setTextColor(Color.WHITE);
            } else if (mSecretKeyValue.length() == 3) {
                mTxtLevel3.setTextColor(Color.GREEN);
                mTxtLevel4.setTextColor(Color.WHITE);
            } else if (mSecretKeyValue.length() == 4) {
                mTxtLevel4.setTextColor(Color.GREEN);
            }
        } else {
            mTxtLevel1.setTextColor(Color.WHITE);
            mTxtLevel2.setTextColor(Color.WHITE);
            mTxtLevel3.setTextColor(Color.WHITE);
            mTxtLevel4.setTextColor(Color.WHITE);
        }
    }

}