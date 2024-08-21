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

import com.canboxsetting.tpms.MazdTpmsInfoaRaiseFragment;
import com.canboxsetting.tpms.VWMQBTpmsInfoRaiseFragment;
import com.canboxsetting.tpms.VWMQBTpmsInfoSimpleFragment;
import com.canboxsetting.tpms.ZhongXingFragment;
import com.car.ui.GlobalDef;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

/**
 * This activity plays a video from a specified URI.
 */
public class TPMSActivity extends AppCompatActivity {
    private static final String TAG = "CanboxSetting";
    private FragmentManager mFragmentManager;
    private Fragment mSetting;
    //	public static String mCanboxType = null;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.main);
        mFragmentManager = getSupportFragmentManager();

        String mCanboxType = MachineConfig.getPropertyForce(MachineConfig.KEY_CAN_BOX);
        int mProVersion = 0;
        String mProIndex = null;
        if (mCanboxType != null) {
            String[] ss = mCanboxType.split(",");
            mCanboxType = ss[0];
            try {
                for (int i = 1; i < ss.length; ++i) {
                    if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_PROTOCAL_VERSION)) {
                        mProVersion = Integer.parseInt(ss[i].substring(1));
                    } else if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_PROTOCAL_INDEX)) {
                        mProIndex = ss[i].substring(1);
                        GlobalDef.setProId(Integer.parseInt(mProIndex));
                    }
                }
            } catch (Exception ignored) {
            }
        }

        if (mProVersion >= 3 && mProIndex != null) {
            Class<?> c = FragmentPro.getFragmentTpmsByID(mProIndex);
            if (c != null) {
                try {
                    mSetting = (Fragment) c.newInstance();
                    Bundle b = new Bundle();
                    b.putString(MachineConfig.KEY_SUB_CANBOX_PROTOCAL_INDEX, mProIndex);
                    mSetting.setArguments(b);
                } catch (Exception ignored) {
                }

            }
            if (mSetting == null) {
                finish();
                return;
            }
        } else {
            // mCanboxType =
            // AppConfig.getCanboxSetting();//MachineConfig.getPropertyForce(MachineConfig.KEY_CAN_BOX);
            if (mCanboxType != null) {
                switch (mCanboxType) {
                    case MachineConfig.VALUE_CANBOX_VW_MQB_RAISE:
                        mSetting = new VWMQBTpmsInfoRaiseFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_VW_GOLF_SIMPLE:
                        mSetting = new VWMQBTpmsInfoSimpleFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_MAZDA_RAISE:
                        mSetting = new MazdTpmsInfoaRaiseFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_ZHONGXING_OD:
                        mSetting = new ZhongXingFragment();
                        break;
                }
            }

            if (mSetting == null) {
                mSetting = new VWMQBTpmsInfoRaiseFragment();
            }
        }

        replaceFragment(R.id.main, mSetting, false);

        mCmd = 0;
        upateIntent(getIntent());
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

    public static int mCmd = 0;
    public static byte[] mBuf;

    private void upateIntent(Intent it) {
        if (it != null) {
            mBuf = it.getByteArrayExtra(MyCmd.EXTRA_COMMON_CMD);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        upateIntent(intent);
    }

    @Override
    protected void onPause() {

        super.onPause();
        // finish();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        // System.exit(0);
    }
}
