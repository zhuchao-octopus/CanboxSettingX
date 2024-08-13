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

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import com.canboxsetting.MyFragment.MsgInterface;
import com.canboxsetting.ac.GMAirODFragment;
import com.canboxsetting.ac.Golf7SimpleAirControlFragment;
import com.canboxsetting.ac.HondaSimpleACFragment;
import com.canboxsetting.ac.JeepAirControlFragment;
import com.canboxsetting.ac.JeepAirControlXinbasFragment;
import com.canboxsetting.ac.RX330HZAirControlFragment;
import com.canboxsetting.ac.RaiseAirControlFragment;
import com.canboxsetting.ac.SlimKeyAirControlFragment;
import com.canboxsetting.ac.TouaregHiworldACFragment;
import com.canboxsetting.ac.ToyotaRaiseAirControlFragment;
import com.canboxsetting.ac.VWMQBAirControlFragment;
import com.canboxsetting.set.X30RaiseAirControlFragment;
import com.car.ui.GlobalDef;
import com.common.util.AppConfig;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Util;
import com.zhuchao.android.fbase.MMLog;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * This activity plays a video from a specified URI.
 */
public class CanAirControlActivity extends Activity {
    private static final String TAG = "CanAirControlActivity";
    private FragmentManager mFragmentManager;
    private MyFragment mSetting;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        GlobalDef.init(this);
        setContentView(R.layout.main);
        mFragmentManager = getFragmentManager();

        String value = null;// = AppConfig.getCanboxSetting();//
        String mCanboxType = MachineConfig.getPropertyForce(MachineConfig.KEY_CAN_BOX);
        int mProVersion = 0;
        int mModelId = 0;
        String mProIndex = null;
        MMLog.d(TAG,"mCanboxType="+mCanboxType);

        if (mCanboxType != null)
        {
            String[] ss = mCanboxType.split(",");
            value = ss[0];
            try {
                for (int i = 1; i < ss.length; ++i)
                {
                    if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_PROTOCAL_VERSION)) {
                        mProVersion = Integer.parseInt(ss[i].substring(1));
                    }
                    else if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_PROTOCAL_INDEX)) {
                        mProIndex = ss[i].substring(1);
                        try {
                            GlobalDef.setProId(Integer.parseInt(mProIndex));
                        } catch (Exception ignored) {
                        }
                    }
                    else if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_ID))
                    {
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
                    } else if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_CAR_CONFIG)) {
                        int mCarConfig = Integer.parseInt(ss[i].substring(1));
                        GlobalDef.setCarConfig(mCarConfig);
                    }
                }
            } catch (Exception ignored) {
            }
        }

        MMLog.d(TAG,"mProVersion="+mProVersion + " mProIndex="+mProIndex + " value="+value);
        if (mProVersion >= 3 && mProIndex != null) {
            Class<?> c = FragmentPro.getFragmentACByID(mProIndex);
            if (c != null) {
                try {
                    mSetting = (MyFragment) c.newInstance();
                    mSetting.mCarType = mModelId;
                } catch (Exception ignored) {
                }
            }
            if (mSetting == null) {
                finish();
                return;
            }
        }
        else
        {
            if (value != null) {
                switch (value) {
                    case MachineConfig.VALUE_CANBOX_VW_MQB_RAISE:
                        mSetting = new VWMQBAirControlFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_VW_GOLF_SIMPLE:
                        mSetting = new Golf7SimpleAirControlFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_TOUAREG_HIWORLD:
                        mSetting = new TouaregHiworldACFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_RX330_HAOZHENG:
                        mSetting = new RX330HZAirControlFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_X30_RAISE:
                        mSetting = new X30RaiseAirControlFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_JEEP_XINBAS:
                        mSetting = new JeepAirControlXinbasFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_GM_OD:
                        mSetting = new GMAirODFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_TOYOTA_RAISE:
                        mSetting = new ToyotaRaiseAirControlFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_HONDA_DA_SIMPLE:
                        mSetting = new HondaSimpleACFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_PETGEO_RAISE:
                        mSetting = new RaiseAirControlFragment();
                        break;
                    case MachineConfig.VALUE_CANBOX_SLIMKEY2:
                        mSetting = new SlimKeyAirControlFragment();
                        break;
                }
            }
            if (mSetting == null) {
                mSetting = new JeepAirControlFragment();
            }
        }

        mSetting.setCallback(mMsgInterface);
        replaceFragment(R.id.main, mSetting, false);
    }

    public void onClick(View v) {
        prepareFinish();
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

    public final static int CMD_GROUP_AC = 0x200;

    public final static int AC_CMD_REQUEST_INFO = 1;

    private void requestACInfo() {
        Intent i = new Intent(MyCmd.BROADCAST_SEND_TO_CAN);
        i.putExtra(MyCmd.EXTRA_COMMON_CMD, AC_CMD_REQUEST_INFO | CMD_GROUP_AC);
        BroadcastUtil.sendToCarService(this, i);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        GlobalDef.updateTempUnit(this);
        prepareFinish();
        requestACInfo();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mHandler.removeMessages(0);
    }

    private final Handler mHandler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            finish();
        }
    };

    private int mFinishDelayTime = 6000;

    private void prepareFinish() {
        mHandler.removeMessages(0);
        if (mFinishDelayTime != 0) {
            mHandler.sendEmptyMessageDelayed(0, mFinishDelayTime);
        }
    }

    public static final int AC_ISNOT_FINISH = 0x7fffff00;
    MsgInterface mMsgInterface = new MsgInterface() {
        @Override
        public void callBack(int msg) {
            // TODO Auto-generated method stub
            if ((msg & 0xffffff00) == 0x7fffff00) {
                mFinishDelayTime = (msg & 0xff) * 100;
            }
            prepareFinish();
        }
    };

}
