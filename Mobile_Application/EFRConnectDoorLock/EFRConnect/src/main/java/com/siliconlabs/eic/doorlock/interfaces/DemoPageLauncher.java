package com.siliconlabs.eic.doorlock.interfaces;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.view.View;

public interface DemoPageLauncher {
    DialogFragment launchPage(View view, FragmentManager fragmentManager);
}
