/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.canboxsetting;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.canboxsetting.anstar.GMOnStarFragment;
import com.canboxsetting.anstar.GMOnStarHiworldFragment;
import com.common.utils.MachineConfig;

/**
 * This activity plays a video from a specified URI.
 */
public class AnStartActivity extends AppCompatActivity {
    private static final String TAG = "CanAirControlActivity";
    private FragmentManager mFragmentManager;
    private MyFragment mSetting;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.main);
        mFragmentManager = getSupportFragmentManager();

        String mCanboxType = MachineConfig.getPropertyForce(MachineConfig.KEY_CAN_BOX);
        String mProIndex = null;
        if (mCanboxType != null) {
            String[] ss = mCanboxType.split(",");
            mCanboxType = ss[0];
            try {
                for (int i = 1; i < ss.length; ++i) {
                    if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_PROTOCAL_INDEX)) {
                        mProIndex = ss[i].substring(1);
                    }
                }
            } catch (Exception ignored) {
            }
        }

        if ("111".equals(mProIndex)) {
            mSetting = new GMOnStarHiworldFragment();
        } else {
            mSetting = new GMOnStarFragment();
        }

        replaceFragment(R.id.main, mSetting, false);

    }

    public void onClick(View v) {
        mSetting.onClick(v);
    }

    private void replaceFragment(int layoutId, Fragment fragment, boolean isAddStack) {
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(layoutId, fragment);
            if (isAddStack) {
                fragmentTransaction.addToBackStack(null);
            }
            fragmentTransaction.commit();
        }
    }

}
