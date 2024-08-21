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

import com.canboxsetting.info.Accord2013InfoSimpleFragment;
import com.canboxsetting.info.BMWE90X1UnionFragment;
import com.canboxsetting.info.FiatEGEARaiseFragment;
import com.canboxsetting.info.FiatFragment;
import com.canboxsetting.info.FocusFragment;
import com.canboxsetting.info.FocusRaiseFragment;
import com.canboxsetting.info.GMInfoSimpleFragment;
import com.canboxsetting.info.Golf7InfoSimpleFragment;
import com.canboxsetting.info.HYRaiseFragment;
import com.canboxsetting.info.HondaInfoSimpleFragment;
import com.canboxsetting.info.JeepInfoSimpleFragment;
import com.canboxsetting.info.KadjarRaiseFragment;
import com.canboxsetting.info.LandRoverHaoZhengFragment;
import com.canboxsetting.info.Mazda3SimpleFragment;
import com.canboxsetting.info.MazdaBinarytekInfoFragment;
import com.canboxsetting.info.MondeoDaojunFragment;
import com.canboxsetting.info.NissanRaiseFragment;
import com.canboxsetting.info.OBDBinarytekFragment;
import com.canboxsetting.info.OuShangeInfoRaiseFragment;
import com.canboxsetting.info.PSA206308SimpleFragment;
import com.canboxsetting.info.PSAInfoBagooFragment;
import com.canboxsetting.info.PSAInfoRaiseFragment;
import com.canboxsetting.info.PSAInfoSimpleFragment;
import com.canboxsetting.info.Peugeot206SimpleFragment;
import com.canboxsetting.info.PorscheUnionInfoFragment;
import com.canboxsetting.info.Rx330HZInfoSimpleFragment;
import com.canboxsetting.info.TouaregHiworldFragment;
import com.canboxsetting.info.ToyotaInfoSBinarytekFragment;
import com.canboxsetting.info.ToyotaInfoSimpleFragment;
import com.canboxsetting.info.VWInfoSimpleFragment;
import com.canboxsetting.info.VWMQBInfoRaiseFragment;
import com.canboxsetting.info.ZhongXingFragment;
import com.common.utils.GlobalDef;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * This activity plays a video from a specified URI.
 */
public class CarInfoActivity extends AppCompatActivity {
    private static final String TAG = "CanboxSetting";

    private FragmentManager mFragmentManager;
    private Fragment mSetting;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.main);
        mFragmentManager = getSupportFragmentManager();

        String value = null;// AppConfig.getCanboxSetting();//MachineConfig.getPropertyForce(MachineConfig.KEY_CAN_BOX);

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
                    } else if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_ID)) {
                        String mProId = ss[i].substring(1);
                        if (mProId.length() >= 4) {
                            int start = 0;
                            int end = 0;
                            if (mProId.charAt(1) == '0' && mProId.charAt(2) != 0) {
                                end = 1;
                            } else if (mProId.charAt(2) == '0') {
                                end = 2;
                            }

                            start = end + 1;
                            int mModelId = -1;
                            if (mProId.contains("-")) {
                                String[] sss = mProId.substring(start).split("-");
                                mModelId = Integer.parseInt(sss[1]);
                            } else {
                                if ((mProId.length() - start) == 2) {
                                    mModelId = Integer.parseInt(mProId.substring(start + 1, start + 2));
                                } else if ((mProId.length() - start) == 4) {
                                    mModelId = Integer.parseInt(mProId.substring(start + 2, start + 4));
                                } else if ((mProId.length() - start) == 3) {
                                    mModelId = Integer.parseInt(mProId.substring(start + 2, start + 3));
                                }
                            }
                            GlobalDef.setModelId(mModelId);
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        Log.d(TAG, TAG + " mProVersion = " + mProVersion + ",mProIndex = " + mProIndex);
        if (mProVersion >= 3 && mProIndex != null) {
            Class<?> c = FragmentPro.getFragmentInfoByID(mProIndex);
            if (c != null) {
                try {
                    mSetting = (Fragment) c.newInstance();
                } catch (Exception ignored) {
                }
            }
            if (mSetting == null) {
                finish();
                return;
            }
        } else {
            if (value != null) {
                switch (value) {
                    case MachineConfig.VALUE_CANBOX_GM_SIMPLE:
                    case MachineConfig.VALUE_CANBOX_GM_RAISE:
                        mSetting = new GMInfoSimpleFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_VW:
                        mSetting = new VWInfoSimpleFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_PSA_BAGOO:
                        mSetting = new PSAInfoBagooFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_VW_GOLF_SIMPLE:
                        mSetting = new Golf7InfoSimpleFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_VW_MQB_RAISE:
                        mSetting = new VWMQBInfoRaiseFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_BMW_E90X1_UNION:
                        mSetting = new BMWE90X1UnionFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_FORD_SIMPLE:
                        mSetting = new FocusFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_FIAT:
                        mSetting = new FiatFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_PSA:
                        mSetting = new PSAInfoSimpleFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_KADJAR_RAISE:
                        mSetting = new KadjarRaiseFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_TOYOTA:
                    case MachineConfig.VALUE_CANBOX_TOYOTA_RAISE:
                        mSetting = new ToyotaInfoSimpleFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_HONDA_DA_SIMPLE:
                    case MachineConfig.VALUE_CANBOX_HONDA_RAISE:
                        mSetting = new HondaInfoSimpleFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_JEEP_SIMPLE:
                        mSetting = new JeepInfoSimpleFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_TOYOTA_BINARYTEK:
                        mSetting = new ToyotaInfoSBinarytekFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_PEUGEOT206:
                        mSetting = new Peugeot206SimpleFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_ACCORD2013:
                    case MachineConfig.VALUE_CANBOX_ACCORD_BINARYTEK:
                        mSetting = new Accord2013InfoSimpleFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_PORSCHE_UNION:
                        mSetting = new PorscheUnionInfoFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_MAZDA3_BINARYTEK:
                    case MachineConfig.VALUE_CANBOX_MAZDA_XINBAS:
                    case MachineConfig.VALUE_CANBOX_MAZDA_RAISE:
                        mSetting = new MazdaBinarytekInfoFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_TOUAREG_HIWORLD:
                        mSetting = new TouaregHiworldFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_PETGEO_RAISE:
                    case MachineConfig.VALUE_CANBOX_PETGEO_SCREEN_RAISE:
                        mSetting = new PSAInfoRaiseFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_OBD_BINARUI:
                        mSetting = new OBDBinarytekFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_FORD_RAISE:
                        mSetting = new FocusRaiseFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_MAZDA3_SIMPLE:
                        mSetting = new Mazda3SimpleFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_LANDROVER_HAOZHENG:
                        mSetting = new LandRoverHaoZhengFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_RX330_HAOZHENG:
                        mSetting = new Rx330HZInfoSimpleFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_PSA206_SIMPLE:
                        mSetting = new PSA206308SimpleFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_MONDEO_DAOJUN:
                        mSetting = new MondeoDaojunFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_OUSHANG_RAISE:
                        mSetting = new OuShangeInfoRaiseFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_FIAT_EGEA_RAISE:
                        mSetting = new FiatEGEARaiseFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_HY_RAISE:
                        mSetting = new HYRaiseFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_NISSAN_RAISE:
                        mSetting = new NissanRaiseFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_ZHONGXING_OD:
                        mSetting = new ZhongXingFragment();
                        break;
                }
            }
        }
        // if(){
        //
        // } else {
        //
        // }
        if (mSetting == null) {
            mSetting = new GMInfoSimpleFragment();
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

    private void upateIntent(Intent it) {
        if (it != null) {
            mCmd = it.getIntExtra(MyCmd.EXTRA_COMMON_CMD, 0);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mSetting instanceof MyFragment) {
            MyFragment new_name = (MyFragment) mSetting;
            new_name.onNewIntent(intent);
        }
        setIntent(intent);
        upateIntent(intent);

    }

    public void onClick(View v) {
        if (mSetting instanceof MyFragment) {
            MyFragment new_name = (MyFragment) mSetting;
            new_name.onClick(v);
        }
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
