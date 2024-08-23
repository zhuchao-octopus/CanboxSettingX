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

import com.car.ui.GlobalDef;
import com.common.util.MachineConfig;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;


public class CarCompassActivity extends AppCompatActivity {
    private static final String TAG = "CanAirControlActivity";

    private FragmentManager mFragmentManager;
    private MyFragment mSetting;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.main);
        mFragmentManager = getSupportFragmentManager();

        String value = null;
        String mCanboxType = MachineConfig.getPropertyForce(MachineConfig.KEY_CAN_BOX);
        int mProVersion = 0;
        String mProIndex = null;
        if (mCanboxType != null) {
            String[] ss = mCanboxType.split(",");
            value = ss[0];
            try {
                for (int i = 1; i < ss.length; ++i) {
                    if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_PROTOCAL_VERSION)) {
                        mProVersion = Integer.parseInt(ss[i].substring(1));
                    } else if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_PROTOCAL_INDEX)) {
                        mProIndex = ss[i].substring(1);
                        try {
                            GlobalDef.setProId(Integer.parseInt(mProIndex));
                        } catch (Exception ignored) {
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        if (mProVersion >= 3 && mProIndex != null) {
            Class<?> c = FragmentPro.getFragmentCompassByID(mProIndex);
            if (c != null) {
                try {
                    mSetting = (MyFragment) c.newInstance();
                } catch (Exception ignored) {
                }
            }
        }

        if (mSetting == null) {
            finish();
            return;
        }

        replaceFragment(R.id.main, mSetting, false);
    }

    public void onClick(View v) {
        mSetting.onClick(v);
    }

    private void replaceFragment(int layoutId, Fragment fragment, boolean isAddStack) {
        if (fragment != null) {
            FragmentTransaction transation = mFragmentManager.beginTransaction();
            transation.replace(layoutId, fragment);
            if (isAddStack) {
                transation.addToBackStack(null);
            }
            transation.commit();
        }
    }

}
