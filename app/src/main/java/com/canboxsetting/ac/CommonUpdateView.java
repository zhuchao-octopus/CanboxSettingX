/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use getActivity() file except in compliance with the License.
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

package com.canboxsetting.ac;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.MyFragment.MsgInterface;
import com.canboxsetting.R.drawable;
import com.canboxsetting.R.id;
import com.canboxsetting.R.layout;
import com.canboxsetting.R.string;
import com.car.ui.GlobalDef;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * This activity plays a video from a specified URI.
 */
public class CommonUpdateView extends Handler {
    private static final String TAG = "CommonUpdateView";

    public static final int MESSAGE_AIR_CONDITION = 0x01;
    // public static final int MESSAGE_AIR_OUTDOOR_TEMP = 0x02;
    // public static final int MESSAGE_AIR_HIDE = 0x03;

    private byte[] mAirData = new byte[15]; // 同步CarService
    /*
     * 参考欣朴大众协议v2.61.002,考虑兼容性，不完全一致 data[0]
     *
     *
     * Bit7: 空调开关指示 Bit6: A/C指示 Bit5: 内外循环指示 0:外循环 Bit4 AUTO 大风灯指示 Bit3:AUTO
     * 小风灯指示 Bit2: DUAL 指示 Bit 1: 前窗除雾 MAX FRONT灯指示 Bit 0: REAR灯指示 后窗
     *
     * data[1] 0:OFF 1:ON
     *
     * Bit7 向上送风指示 Bit6 水平送风指示 Bit5 向下送风指示 Bit4 空调显示请求 Bit3~Bit0 风速 0x0~07 风速等级
     * 指示 0-7级
     *
     * data[2] 左边设定温度
     *
     * 0x00: LO 0xff: HI 0xfa: hide 0xfb: no update 0xf0~0xf9: show step
     * 0x01~0xef: 温度, 0.5 步进
     *
     * data[3] 右边设定温度
     *
     * 0x00: LO 0xff: HI 0xfa: hide 0x01~0xff: 温度
     *
     * data[4] 座椅加热 Bit7 1==AQS内循环 0==非 Bit5~4 左座椅 00:不显示 01~11:1~3级温度 Bit3 rear
     * lock 1==LOCK 0==非 Bit2 1==AC MAX 0==非 Bit1~0 右座椅 00:不显示 01~11:1~3级温度
     *
     *
     *
     *
     * data[5] bit0: 0-> C 1-> F 温度单位 bit1: 1: hide left temp bit2: 1: hide
     * right temp bit3~bit4: 强制显示温度单位, 0:auto 1.C 2.F
     *
     *
     *
     * data[6]
     *
     * 同 data[2]，右边座椅吹风。 有些车分左右吹风。
     *
     * data[7]
     *
     * Bit 0: eco Bit 1~2: 0:off 1:soft 2:fast 3:Normal ( 0:soft 1:off 2:fast
     * (bagoo GM) ) Bit3 AUTO REAR SWITCH Bit4 AUTO 超大风灯指示 Bit5 前窗除雾 （有些车有前窗除雾
     * MAX FRONT灯指示，又另外有一个前窗除雾） Bit6 0：手动空调, 1:自动空调 Bit7 sync 指示
     *
     * data[8] Bit7 rest 指示 Bit6 temp show level 指示 Bit5~4 左座椅 00:不显示
     * 01~11:1~3级冷风 Bit3~2 右座椅 00:不显示 01~11:1~3级冷风 Bit1 左座椅 >3级冷风高位 Bit0 右座椅
     * >3级冷风高位
     *
     * data[9] Bit8 后座空调开关 Bit6 左座椅 >3级加热高位 Bit5 负离子 或者 森林 Bit4 SWING吹风(出风口摆动)
     * 或者 上出风口 Bit3 花粉 Bit2 右座椅 >3级加热高位
     *
     * Bit1 后座Ac Auto开头 Bit0 Auto 吹风模式
     *
     * data[10] 后座温度。 同前面左右温度
     *
     * data[11] 0:OFF 1:ON 后座风速及模式
     *
     * Bit7 向上送风指示 Bit6 水平送风指示 Bit5 向下送风指示 Bit4 后排AUTO状态 Bit3~Bit0 风速 0x0~07
     * 风速等级 指示 0-7级 data[12] 一些特殊车型的特殊信息 Bit2~Bit0 0~3 方向盘加热级别 Bit4~Bit3 风量等级
     * 0:低 1：中 2：高 Bit5 单区空调(Mono) 0:关1：开 Bit6 前窗除冰
     *
     * data[13] 一些特殊车型的特殊信息 Bit3~2 后区右座椅 00:不显示 01~11:1~3级温度 Bit1~0 后区左座椅 00:不显示
     * 01~11:1~3级温度
     */
    // private Toast mToast = null;

    private final static int TYPE_AIRCONDITON_CHANGE_TEMP = 2;
    private final static int TYPE_AIRCONDITON_SINGLE_TEMP = 2;
    private int mAirConditionType = 0;
    private boolean mRudder = false;

    public void postChanged(int type, Object obj) {
        // if (hasMessages(type))
        // return;
        obtainMessage(type, obj).sendToTarget();
    }

    public void postChanged(int type, int arg1, int arg2, Object obj) {
        // if (hasMessages(type))
        // return;
        obtainMessage(type, arg1, arg2, obj).sendToTarget();
    }

    public void postChanged(int type, int arg1, int arg2) {
        // if (hasMessages(type))
        // return;
        obtainMessage(type, arg1, arg2).sendToTarget();
    }

    private boolean isExtShow() {
        // if
        // (MachineConfig.VALUE_CANBOX_TOYOTA_BINARYTEK.equals(CarUtil.getCanboxType()))
        // {
        // return true;
        // }
        return false;
    }

    private MsgInterface mMsgInterface;

    public void setCallback(MsgInterface i) {
        mMsgInterface = i;
    }

    public void callBack(int msg) {
        if (mMsgInterface != null) {
            mMsgInterface.callBack(msg);
        }
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_AIR_CONDITION:
                try {
                    byte[] airData = (byte[]) msg.obj;

                    // if (((mAirData[0] & 0x80) != 0) && (airData[0] & 0x80) == 0
                    // && !isExtShow()) {
                    // // mToast.cancel();
                    //
                    // mAirData[0] &= ~0x80;
                    // return;
                    // }

                    boolean eaqual = true;
                    for (int i = 0; i < airData.length; ++i) {
                        if (mAirData[i] != airData[i]) {
                            eaqual = false;
                            break;
                        }
                    }

                    mAirData[0] = airData[0];
                    mAirData[1] = airData[1];
                    mAirData[2] = airData[2];
                    mAirData[3] = airData[3];
                    mAirData[4] = airData[4];
                    mAirData[5] = airData[5];
                    mAirData[6] = airData[6];
                    mAirData[7] = airData[7];
                    // if (msg.arg1 == 0) {
                    // mAirData[6] = mAirData[1];
                    // }

                    if (airData.length > 8) {
                        mAirData[8] = airData[8];
                    }
                    if (airData.length > 9) {
                        mAirData[9] = airData[9];
                    }

                    if (airData.length >= 12) {
                        mAirData[10] = airData[10];
                        mAirData[11] = airData[11];
                    }
                    if (airData.length >= 13) {
                        mAirData[12] = airData[12];
                    }
                    if (airData.length >= 14) {
                        mAirData[13] = airData[13];
                    }
                    if (airData.length >= 15) {
                        mAirData[14] = airData[14];
                    }

                    // mTempUnit = ((airData[5] & 0x18) >> 3);
                    if (mAirConditionType == 2) {// change temp
                        byte temp = mAirData[2];
                        mAirData[2] = mAirData[3];
                        mAirData[3] = temp;
                        // return;
                    } else if (mAirConditionType == 3) {// single temp
                        mAirData[3] = (byte) 0xfa;
                    }

                    if (mRudder) {// change temp
                        byte temp = mAirData[2];
                        mAirData[2] = mAirData[3];
                        mAirData[3] = temp;
                        // return;
                    }

                    if (!isExtShow()) {
                        // View mMainView = mToast.getView();
                        setAirCondtionTitle(mMainView);
                        setAirCondtionWind(mMainView);
                        if (mAirData[2] != (byte) 0xfb) {
                            setAirCondtionTemperature(mMainView);
                        }

                        // setAirCondtionAction(mMainView);

                    }

                    if (!eaqual) {
                        callBack(0);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "" + e);
                }
                break;
        }
    }

    View mMainView = null;

    public CommonUpdateView(View view) {

        mMainView = view;

    }

    public CommonUpdateView(View view, MsgInterface i) {

        mMainView = view;
        mMsgInterface = i;
    }

    void setAirCondtionTitle(View view) {
        updateSelect(R.id.ac, mAirData[0] & 0x40);
        updateSelect(R.id.ac_max, mAirData[4] & 0x04);
        updateSelect(R.id.dual, mAirData[0] & 0x04);
        updateSelect(R.id.max, mAirData[0] & 0x02);
        updateSelect(R.id.rear, mAirData[0] & 0x01);
        updateSelect(R.id.rear_lock, mAirData[4] & 0x08);
        updateSelect(R.id.eco, mAirData[7] & 0x01);
        updateSelect(R.id.rear_switch, mAirData[7] & 0x08);
        updateSelect(R.id.front, mAirData[7] & 0x20);
        updateSelect(R.id.sync, mAirData[7] & 0x80);
        updateSelect(R.id.zone, mAirData[7] & 0x80);
        updateSelect(R.id.rest, mAirData[8] & 0x80);
        updateSelect(R.id.air_fwindow_heat, mAirData[5] & 0x10);

        // Log.d("ffck", ""+Integer.toHexString(mAirData[9] & 0xFF));

        updateSelect(R.id.pollen_clear, mAirData[9] & 0x08);
        updateSelect(R.id.swing, mAirData[9] & 0x10);
        updateSelect(R.id.windshield_deicing, mAirData[9] & 0x20);

        updateSelect(R.id.air_control_automatic_fog_removal_normal, mAirData[9] & 0x10);
        updateSelect(R.id.air_control_forest_forest_normal, mAirData[9] & 0x20);

        updateSelect(R.id.ac_windshield_deicing, mAirData[12] & 0x40);

        if (view.findViewById(R.id.inner_loop) != null) {

            if ((mAirData[4] & 0x80) != 0 && view.findViewById(R.id.inner_loop_auto) == null) {
                if (((ImageView) view.findViewById(R.id.inner_loop)) != null) {
                    ((ImageView) view.findViewById(R.id.inner_loop)).getDrawable().setLevel(2);
                }
            } else {
                if ((mAirData[0] & 0x20) != 0) {// inner loop,loop a
                    if (((ImageView) view.findViewById(R.id.inner_loop)) != null) {
                        ((ImageView) view.findViewById(R.id.inner_loop)).getDrawable().setLevel(1);
                    }
                } else {
                    if (((ImageView) view.findViewById(R.id.inner_loop)) != null) {
                        ((ImageView) view.findViewById(R.id.inner_loop)).getDrawable().setLevel(0);
                    }
                }
            }
        }

        updateSelect(R.id.inner_loop_in, (mAirData[0] & 0x20));
        updateSelect(R.id.inner_loop_out, (mAirData[0] & 0x20) == 0 ? 1 : 0);
        updateSelect(R.id.inner_loop_auto, (mAirData[4] & 0x80) != 0 ? 1 : 0);

        updateSelect(R.id.ac_auto_rear, (mAirData[9] & 0x02));
        updateSelect(R.id.power_rear, (mAirData[9] & 0x80));
        updateSelect(R.id.off_rear, (mAirData[9] & 0x80));

        updateSelect(R.id.wind_low, ((mAirData[12] & 0x18) >> 3) == 0 ? 1 : 0);
        updateSelect(R.id.wind_mid, ((mAirData[12] & 0x18) >> 3) == 1 ? 1 : 0);
        updateSelect(R.id.wind_high, ((mAirData[12] & 0x18) >> 3) == 2 ? 1 : 0);

        updateSelect(R.id.mono, (mAirData[12] & 0x20));

        // if ((mAirData[7] & 0x06) == 0) {
        // view.findViewById(R.id.fast).setVisibility(View.GONE);
        // view.findViewById(R.id.soft).setVisibility(View.GONE);
        // } else {
        //
        // if ((mAirData[7] & 0x06) == 0x4) {
        // view.findViewById(R.id.fast).setVisibility(View.VISIBLE);
        // view.findViewById(R.id.soft).setVisibility(View.GONE);
        // } else {
        // view.findViewById(R.id.fast).setVisibility(View.GONE);
        // view.findViewById(R.id.soft).setVisibility(View.VISIBLE);
        // }
        // }
        //
        ImageView v = (ImageView) view.findViewById(R.id.ac_auto);
        if (v != null) {
            updateSelect(R.id.ac_auto, 1);
            if ((mAirData[7] & 0x10) != 0) {
                v.setImageResource(R.drawable.air_title_ce_auto_large);
            } else if ((mAirData[0] & 0x10) != 0) {
                v.setImageResource(R.drawable.air_title_ce_auto_mid);
            } else if ((mAirData[0] & 0x08) != 0) {
                v.setImageResource(R.drawable.air_title_ce_auto_small);
            } else {
                updateSelect(R.id.ac_auto, 0);
            }
        }

    }

    void setAirCondtionWind(View view) {

        updateSelect(R.id.power, (mAirData[0] & 0x80));

        updateSelect(R.id.wind_up1, (mAirData[1] & 0x80));
        updateSelect(R.id.wind_horizontal1, (mAirData[1] & 0x40));
        updateSelect(R.id.wind_down1, (mAirData[1] & 0x20));

        updateSelect(R.id.wind_horizontal_down, (mAirData[1] & 0x60) == 0x60 ? 1 : 0);
        updateSelect(R.id.wind_up_down, (mAirData[1] & 0xA0) == 0xa0 ? 1 : 0);

        if ((mAirData[1] & 0x60) == 0x60) {
            View v = mMainView.findViewById(R.id.wind_horizontal_down);
            if (v != null) {
                updateSelect(R.id.wind_horizontal1, 0);
                updateSelect(R.id.wind_down1, 0);
            }
        }

        if ((mAirData[1] & 0xA0) == 0xA0) {
            View v = mMainView.findViewById(R.id.wind_up_down);
            if (v != null) {
                updateSelect(R.id.wind_up1, 0);
                updateSelect(R.id.wind_down1, 0);
            }
        }

        // updateSelect(R.id.wind_up1_rear, (mAirData[1] & 0x80));
        updateSelect(R.id.wind_horizontal1_rear, (mAirData[11] & 0x40));
        updateSelect(R.id.wind_down1_rear, (mAirData[11] & 0x20));

        if (mMainView.findViewById(R.id.wind_horizontal_down_rear) != null) {
            if ((mAirData[11] & 0x60) == 0x60) {
                updateSelect(R.id.wind_horizontal_down_rear, 1);
                updateSelect(R.id.wind_horizontal1_rear, 0);
                updateSelect(R.id.wind_down1_rear, 0);
            } else {

                updateSelect(R.id.wind_horizontal_down_rear, 0);
                updateSelect(R.id.wind_horizontal1_rear, (mAirData[11] & 0x40));
                updateSelect(R.id.wind_down1_rear, (mAirData[11] & 0x20));
            }
        }

        updateSelect(R.id.aq, (mAirData[5] & 0x20));

        if ((mAirData[9] & 0x01) != 0) {
            updateSelect(R.id.wind_auto, 1);
            updateSelect(R.id.wind_up1, 0);
            updateSelect(R.id.wind_horizontal1, 0);
            updateSelect(R.id.wind_down1, 0);
            updateSelect(R.id.wind_horizontal_down, 0);
            updateSelect(R.id.wind_up_down, 0);
        } else {

            updateSelect(R.id.wind_auto, 0);
        }

        if ((mAirData[11] & 0x10) != 0) {
            updateSelect(R.id.wind_auto_rear, 1);
            // updateSelect(R.id.wind_up1_rear, 0);
            updateSelect(R.id.wind_horizontal1_rear, 0);
            updateSelect(R.id.wind_down1_rear, 0);
            updateSelect(R.id.wind_horizontal_down_rear, 0);
            // updateSelect(R.id.wind_up_down_rear, 0);
        } else {

            updateSelect(R.id.wind_auto_rear, 0);
        }

        updateSelect(R.id.wheel, mAirData[12] & 0x01);

        // 右模式
        if ((mAirData[5] & 0x80) != 0) {
            View v = mMainView.findViewById(R.id.wind_mode_right);
            if (v != null) {
                int id = R.drawable.air_control_wind_blow_off_normal;
                switch ((mAirData[6] & 0xe0) >> 4) {
                    case 0xc:
                        id = R.drawable.canbus21_mode4;
                        break;
                    case 0xa:
                        id = R.drawable.canbus21_mode4;
                        break;
                    case 0x6:
                        id = R.drawable.canbus21_mode2;
                        break;
                    case 0x8:
                        id = R.drawable.canbus21_mode5;
                        break;
                    case 0x4:
                        id = R.drawable.canbus21_mode1;
                        break;
                    case 0x2:
                        id = R.drawable.canbus21_mode3;
                        break;
                }

                ((ImageView) v).setImageResource(id);

            }
        }
        //
        // if ((mAirData[1] & 0x40) != 0) {
        // view.findViewById(R.id.wind_horizontal1)
        // .setVisibility(View.VISIBLE);
        // //
        // view.findViewById(R.id.wind_horizontal2).setVisibility(View.VISIBLE);
        // } else {
        // view.findViewById(R.id.wind_horizontal1).setVisibility(
        // View.INVISIBLE);
        // //
        // view.findViewById(R.id.wind_horizontal2).setVisibility(View.INVISIBLE);
        // }
        //
        // if ((mAirData[1] & 0x20) != 0) {
        // view.findViewById(R.id.wind_down1).setVisibility(View.VISIBLE);
        // // view.findViewById(R.id.wind_down2).setVisibility(View.VISIBLE);
        // } else {
        // view.findViewById(R.id.wind_down1).setVisibility(View.INVISIBLE);
        // // view.findViewById(R.id.wind_down2).setVisibility(View.INVISIBLE);
        // }
        //
        // if ((mAirData[6] & 0x80) != 0) {
        // // view.findViewById(R.id.wind_up1).setVisibility(View.VISIBLE);
        // view.findViewById(R.id.wind_up2).setVisibility(View.VISIBLE);
        // } else {
        // // view.findViewById(R.id.wind_up1).setVisibility(View.INVISIBLE);
        // view.findViewById(R.id.wind_up2).setVisibility(View.INVISIBLE);
        // }
        //
        // if ((mAirData[6] & 0x40) != 0) {
        // //
        // view.findViewById(R.id.wind_horizontal1).setVisibility(View.VISIBLE);
        // view.findViewById(R.id.wind_horizontal2)
        // .setVisibility(View.VISIBLE);
        // } else {
        // //
        // view.findViewById(R.id.wind_horizontal1).setVisibility(View.INVISIBLE);
        // view.findViewById(R.id.wind_horizontal2).setVisibility(
        // View.INVISIBLE);
        // }
        //
        // if ((mAirData[6] & 0x20) != 0) {
        // // view.findViewById(R.id.wind_down1).setVisibility(View.VISIBLE);
        // view.findViewById(R.id.wind_down2).setVisibility(View.VISIBLE);
        // } else {
        // // view.findViewById(R.id.wind_down1).setVisibility(View.INVISIBLE);
        // view.findViewById(R.id.wind_down2).setVisibility(View.INVISIBLE);
        // }

        for (int i = 0; i < 0xf; i++) {
            if (view.findViewById(R.id.wind_rate_1 + i) != null) {
                if (i < (mAirData[1] & 0x0F)) {
                    if (((ImageView) view.findViewById(R.id.wind_rate_1 + i)) != null) {
                        ((ImageView) view.findViewById(R.id.wind_rate_1 + i)).getDrawable().setLevel(1);
                    }
                    if ((mAirData[1] & 0x0F) >= 7 && (mAirData[1] & 0x0F) <= 0xf) {
                        if (((ImageView) view.findViewById(R.id.wind_rate_1 + i)) != null) {
                            ((ImageView) view.findViewById(R.id.wind_rate_1 + i)).setVisibility(View.VISIBLE);
                        }

                    }

                } else {
                    if (((ImageView) view.findViewById(R.id.wind_rate_1 + i)) != null) {
                        ((ImageView) view.findViewById(R.id.wind_rate_1 + i)).getDrawable().setLevel(0);
                    }
                }
            } else {
                break;
            }
        }

        for (int i = 0; i < 0xc; i++) {
            if (view.findViewById(R.id.wind_rate_rear_1 + i) != null) {
                if (i < (mAirData[11] & 0x0F)) {
                    if (((ImageView) view.findViewById(R.id.wind_rate_rear_1 + i)) != null) {
                        ((ImageView) view.findViewById(R.id.wind_rate_rear_1 + i)).getDrawable().setLevel(1);
                    }

                } else {
                    if (((ImageView) view.findViewById(R.id.wind_rate_rear_1 + i)) != null) {
                        ((ImageView) view.findViewById(R.id.wind_rate_rear_1 + i)).getDrawable().setLevel(0);
                    }
                }
            } else {
                break;
            }
        }

        if ((mAirData[11] & 0x0F) >= 7 && (mAirData[11] & 0x0F) <= 0xc) {
            if (((ImageView) view.findViewById(R.id.wind_rate_rear_1 + (mAirData[11] & 0x0F) - 1)) != null) {
                ((ImageView) view.findViewById(R.id.wind_rate_rear_1 + (mAirData[11] & 0x0F) - 1)).setVisibility(View.VISIBLE);
            }
        }
    }

    String getOurDoorTemperature(View view, double temperature) {
        String temp = null;

        boolean isCentigradeUnit = ((mAirData[5] & 0x1) == 0) ? true : false;
        if (isCentigradeUnit == true) {
            temp = String.format("%.1f%s", temperature, view.getResources().getString(R.string.temp_unic_centigrade));
        } else {
            return String.format("%d%s", (int) temperature, view.getResources().getString(R.string.temp_unic_fahrenheit));

        }

        return temp;
    }

    String getAirTemperature(View view, float temperature) {
        if ((mAirData[7] & 0x40) != 0) {
            if (temperature != 0) {
                return "" + ((int) temperature);
            } else {
                return "";
            }
        }

        // if (temperature <= 14) {
        // return "" + ((int)temperature);
        // }

        boolean isCentigradeUnit = ((mAirData[5] & 0x1) == 0) ? true : false;

        if (GlobalDef.mTempUnit == 1) {
            if (!isCentigradeUnit) {
                temperature = ((temperature) - 32) / 1.8f;
                isCentigradeUnit = !isCentigradeUnit;
            }
        } else if (GlobalDef.mTempUnit == 2) {
            if (isCentigradeUnit) {
                temperature = ((temperature / 2) * 1.8f + 32);
                isCentigradeUnit = !isCentigradeUnit;
            }
        }

        if (isCentigradeUnit == true) {
            return String.format(Locale.ENGLISH, "%.1f%s", temperature / 2, view.getResources().getString(R.string.temp_unic_centigrade));
        } else {
            return String.format(Locale.ENGLISH, "%d%s", (int) temperature, view.getResources().getString(R.string.temp_unic_fahrenheit));
        }

    }

    private int outDoorTemp = 0xff;

    void setAirCondtionTemperature(View view) {
        int leftTemp = (int) (mAirData[2] & 0xff);
        int rightTemp = (int) (mAirData[3] & 0xff);
        int backSeatTemp = (int) (mAirData[10] & 0xff);

        if (view.findViewById(R.id.left_temp) != null) {
            if ((mAirData[5] & 0x2) == 0) {
                if (leftTemp == 0x00) {
                    ((TextView) view.findViewById(R.id.left_temp)).setText(R.string.LO);
                } else if (leftTemp == 0xff) {
                    ((TextView) view.findViewById(R.id.left_temp)).setText(R.string.HI);
                } else if (leftTemp == 0xfa) {
                    ((TextView) view.findViewById(R.id.left_temp)).setText("");
                } else if (leftTemp >= 0xf0 && leftTemp <= 0xf9) {
                    ((TextView) view.findViewById(R.id.left_temp)).setText((leftTemp - 0xf0) + "");
                } else {
                    ((TextView) view.findViewById(R.id.left_temp)).setText(getAirTemperature(view, leftTemp));
                }
                view.findViewById(R.id.left_temp).setVisibility(View.VISIBLE);
            } else {

                view.findViewById(R.id.left_temp).setVisibility(View.INVISIBLE);
            }
        }

        if (view.findViewById(R.id.right_temp) != null) {
            if ((mAirData[5] & 0x4) == 0) {
                if (rightTemp == 0x00) {
                    ((TextView) view.findViewById(R.id.right_temp)).setText(R.string.LO);
                } else if (rightTemp == 0xff) {
                    ((TextView) view.findViewById(R.id.right_temp)).setText(R.string.HI);
                } else if (rightTemp == 0xfa) {
                    ((TextView) view.findViewById(R.id.right_temp)).setText("");
                } else if (rightTemp >= 0xf0 && rightTemp <= 0xf9) {
                    ((TextView) view.findViewById(R.id.right_temp)).setText((rightTemp - 0xf0) + "");
                } else {
                    ((TextView) view.findViewById(R.id.right_temp)).setText(getAirTemperature(view, rightTemp));
                }
                view.findViewById(R.id.right_temp).setVisibility(View.VISIBLE);
            } else {

                view.findViewById(R.id.right_temp).setVisibility(View.INVISIBLE);
            }
        }

        if (view.findViewById(R.id.reartemp) != null) {
            if (backSeatTemp == 0x00) {
                ((TextView) view.findViewById(R.id.reartemp)).setText(R.string.LO);
            } else if (backSeatTemp == 0xff) {
                ((TextView) view.findViewById(R.id.reartemp)).setText(R.string.HI);
            } else if (backSeatTemp == 0xfa) {
                ((TextView) view.findViewById(R.id.reartemp)).setText("");
            } else if (backSeatTemp >= 0xf0 && backSeatTemp <= 0xf9) {
                ((TextView) view.findViewById(R.id.reartemp)).setText((backSeatTemp - 0xf0) + "");
            } else {
                ((TextView) view.findViewById(R.id.reartemp)).setText(getAirTemperature(view, backSeatTemp));
            }
        }

        if (view.findViewById(R.id.reartemp_right) != null) {
            backSeatTemp = (int) (mAirData[14] & 0xff);
            if (backSeatTemp == 0x00) {
                ((TextView) view.findViewById(R.id.reartemp_right)).setText(R.string.LO);
            } else if (backSeatTemp == 0xff) {
                ((TextView) view.findViewById(R.id.reartemp_right)).setText(R.string.HI);
            } else if (backSeatTemp == 0xfa) {
                ((TextView) view.findViewById(R.id.reartemp_right)).setText("");
            } else if (backSeatTemp >= 0xf0 && backSeatTemp <= 0xf9) {
                ((TextView) view.findViewById(R.id.reartemp_right)).setText((backSeatTemp - 0xf0) + "");
            } else {
                ((TextView) view.findViewById(R.id.reartemp_right)).setText(getAirTemperature(view, backSeatTemp));
            }
        }

        if ((mAirData[8] & 0x40) != 0) {
            if (view.findViewById(R.id.left_temp) != null) {
                ((TextView) view.findViewById(R.id.left_temp)).setText(leftTemp + "");
            }
            if (view.findViewById(R.id.right_temp) != null) {
                ((TextView) view.findViewById(R.id.right_temp)).setText(rightTemp + "");
            }
        }

        int seatHeat = (mAirData[4] & 0x33);
        seatHeat |= (mAirData[9] & 0x44);
        int seatCold = (mAirData[8] & 0x3f);

        int left = ((seatHeat >> 4) & 0x07);
        int right = seatHeat & 0x7;
        setSeatheat(R.id.con_seathotright, left);
        setSeatheat(R.id.con_seathotleft, right);
        setSeatheat2(R.id.left_seat_heat, left);
        setSeatheat2(R.id.right_seat_heat, right);

        left = ((mAirData[8] & 0x30) >> 4) | ((mAirData[8] & 0x02) << 1);
        right = ((mAirData[8] & 0xc) >> 2) | ((mAirData[8] & 0x01) << 2);
        setWindheat2(R.id.left_seat_refrigeration, left);
        setWindheat2(R.id.right_seat_refrigeration, right);

        if ((seatHeat != mSeatHeat) || (seatCold != mSeatCold)) {

            // mSeatHeat = (mAirData[4] & 0x33);
            // view.findViewById(R.id.air_action_seat).setVisibility(View.VISIBLE);
            // if ((seatHeat & 0x44)!=0){
            // ((ImageView)
            // view.findViewById(R.id.seat_heat_right_4)).setVisibility(View.VISIBLE);
            // ((ImageView)
            // view.findViewById(R.id.seat_heat_left_4)).setVisibility(View.VISIBLE);
            // }
            //
            // for (int i = 0; i < 4; i++) {
            // if (i < ((seatHeat >> 4) & 0x07)) {
            // ((ImageView) view.findViewById(getLeftHeat(1 + i)))
            // .getDrawable().setLevel(1);
            // } else {
            // ((ImageView) view.findViewById(getLeftHeat(1 + i)))
            // .getDrawable().setLevel(0);
            // }
            //
            // if (i < (seatHeat & 0x07)) {
            // ((ImageView) view.findViewById(getRightHeat(1 + i)))
            // .getDrawable().setLevel(1);
            // } else {
            // ((ImageView) view.findViewById(getRightHeat(1 + i)))
            // .getDrawable().setLevel(0);
            // }
            // }
            // if ((mAirData[8] & 0x3c) != mSeatCold){
            // mSeatCold = (mAirData[8] & 0x3f);
            // mSeatColdExit = true;
            //
            // int left = ((mAirData[8] & 0x30)>>4) | ((mAirData[8] & 0x02)<<1);
            // int right = ((mAirData[8] & 0xc)>>2) | ((mAirData[8] & 0x01)<<2);
            //
            // if (left >= 4 || right >= 4) {
            // ((ImageView) view.findViewById(R.id.seat_cold_right_4))
            // .setVisibility(View.VISIBLE);
            // ((ImageView) view.findViewById(R.id.seat_cold_left_4))
            // .setVisibility(View.VISIBLE);
            // }
            //
            // view.findViewById(R.id.seat_cold_layout).setVisibility(View.VISIBLE);
            // for (int i = 0; i < 4; i++) {
            // if (i < (left)) {
            // ((ImageView) view.findViewById(getLeftCold(1 + i)))
            // .getDrawable().setLevel(1);
            // } else {
            // ((ImageView) view.findViewById(getLeftCold(1 + i)))
            // .getDrawable().setLevel(0);
            // }
            //
            // if (i < (right)) {
            // ((ImageView) view.findViewById(getRightCold(1 + i)))
            // .getDrawable().setLevel(1);
            // } else {
            // ((ImageView) view.findViewById(getRightCold(1 + i)))
            // .getDrawable().setLevel(0);
            // }
            // }
            // } else {
            // if (!mSeatColdExit){
            // view.findViewById(R.id.seat_cold_layout).setVisibility(View.GONE);
            // }
            // }
        } else {
            // view.findViewById(R.id.air_action_seat).setVisibility(View.GONE);
        }
    }

    private void setSeatheat(int id, int level) {

        ImageButton v = (ImageButton) mMainView.findViewById(id);
        int drawable;
        if (v != null) {
            if (id == R.id.con_seathotleft) {
                drawable = R.drawable.img_air_seathotleft0;
                switch (level) {
                    case 1:
                        drawable = R.drawable.img_air_seathotleft1;
                        break;
                    case 2:
                        drawable = R.drawable.img_air_seathotleft2;
                        break;
                    case 3:
                        drawable = R.drawable.img_air_seathotleft3;
                        break;
                }
            } else {
                drawable = R.drawable.img_air_seathotright0;
                switch (level) {
                    case 1:
                        drawable = R.drawable.img_air_seathotright1;
                        break;
                    case 2:
                        drawable = R.drawable.img_air_seathotright2;
                        break;
                    case 3:
                        drawable = R.drawable.img_air_seathotright3;
                        break;
                }
            }

            v.setImageResource(drawable);
        }
    }

    private void setSeatheat2(int id, int level) {

        ImageButton v = (ImageButton) mMainView.findViewById(id);
        int drawable;
        if (v != null) {
            if (id == R.id.left_seat_heat) {
                drawable = R.drawable.air_control_left_seat_heat_0_normal;
                switch (level) {
                    case 1:
                        drawable = R.drawable.air_control_left_seat_heat_1_press;
                        break;
                    case 2:
                        drawable = R.drawable.air_control_left_seat_heat_2_press;
                        break;
                    case 3:
                        drawable = R.drawable.air_control_left_seat_heat_3_press;
                        break;
                    case 4:
                        drawable = R.drawable.air_control_left_seat_heat_4_press;
                        break;
                }
            } else {
                drawable = R.drawable.air_control_right_seat_heat_0_normal;
                switch (level) {
                    case 1:
                        drawable = R.drawable.air_control_right_seat_heat_1_press;
                        break;
                    case 2:
                        drawable = R.drawable.air_control_right_seat_heat_2_press;
                        break;
                    case 3:
                        drawable = R.drawable.air_control_right_seat_heat_3_press;
                        break;
                    case 4:
                        drawable = R.drawable.air_control_right_seat_heat_4_press;
                        break;
                }
            }

            v.setImageResource(drawable);
        }
    }

    private void setWindheat2(int id, int level) {

        ImageButton v = (ImageButton) mMainView.findViewById(id);
        int drawable;
        if (v != null) {
            if (id == R.id.left_seat_refrigeration) {
                drawable = R.drawable.air_control_left_refrigeration_normal_0;
                switch (level) {
                    case 1:
                        drawable = R.drawable.air_control_left_refrigeration_press_1;
                        break;
                    case 2:
                        drawable = R.drawable.air_control_left_refrigeration_press_2;
                        break;
                    case 3:
                        drawable = R.drawable.air_control_left_refrigeration_press_3;
                        break;
                    case 4:
                        drawable = R.drawable.air_control_left_refrigeration_press_4;
                        break;
                }
            } else {
                drawable = R.drawable.air_control_right_refrigeration_normal_0;
                switch (level) {
                    case 1:
                        drawable = R.drawable.air_control_right_refrigeration_press_1;
                        break;
                    case 2:
                        drawable = R.drawable.air_control_right_refrigeration_press_2;
                        break;
                    case 3:
                        drawable = R.drawable.air_control_right_refrigeration_press_3;
                        break;
                    case 4:
                        drawable = R.drawable.air_control_right_refrigeration_press_4;
                        break;
                }
            }

            v.setImageResource(drawable);
        }
    }

    // private int getLeftHeat(int i){
    // switch(i){
    // case 1:
    // return R.id.seat_heat_left_1;
    // case 2:
    // return R.id.seat_heat_left_2;
    // case 3:
    // return R.id.seat_heat_left_3;
    // case 4:
    // return R.id.seat_heat_left_4;
    // }
    // return 0;
    // }
    // private int getRightHeat(int i){
    // switch(i){
    // case 1:
    // return R.id.seat_heat_right_1;
    // case 2:
    // return R.id.seat_heat_right_2;
    // case 3:
    // return R.id.seat_heat_right_3;
    // case 4:
    // return R.id.seat_heat_right_4;
    // }
    // return 0;
    // }
    //
    // private int getLeftCold(int i){
    // switch(i){
    // case 1:
    // return R.id.seat_cold_left_1;
    // case 2:
    // return R.id.seat_cold_left_2;
    // case 3:
    // return R.id.seat_cold_left_3;
    // case 4:
    // return R.id.seat_cold_left_4;
    // }
    // return 0;
    // }
    // private int getRightCold(int i){
    // switch(i){
    // case 1:
    // return R.id.seat_cold_right_1;
    // case 2:
    // return R.id.seat_cold_right_2;
    // case 3:
    // return R.id.seat_cold_right_3;
    // case 4:
    // return R.id.seat_cold_right_4;
    // }
    // return 0;
    // }

    private int mSeatHeat = 0;
    private int mSeatCold = 0;
    private boolean mSeatColdExit = false;

    void setAirCondtionAction(View view) {

    }

    private void updateSelect(int id, int s) {
        View v = mMainView.findViewById(id);
        if (v != null) {
            v.setSelected(s != 0);
        }
    }

    public int getWind() {
        return mAirData[1] & 0xf;
    }

    public int getWindRear() {
        return mAirData[11] & 0xf;
    }

    public int getTempLeft() {
        return mAirData[2] & 0xff;
    }

    public int getTempRight() {
        return mAirData[3] & 0xff;
    }

    public int getTempRear() {
        return mAirData[10] & 0xff;
    }

    public int getTempUnit() {
        return mAirData[5] & 0x1;
    }

    public int getHeatLeft() {
        return (mAirData[4] & 0x30) >> 4;
    }

    public int getHeatRight() {
        return (mAirData[4] & 0x3);
    }

    public int getRefrigerationLeft() {
        return ((mAirData[8] & 0x30) >> 4) | ((mAirData[8] & 0x02) << 1);
    }

    public int getRefrigerationRight() {
        return ((mAirData[8] & 0x0c) >> 2) | ((mAirData[8] & 0x01) << 2);
    }

    public int getLoopInner() {
        return mAirData[0] & 0x20;
    }

    public int getAirData(int index) {
        if (index >= 0 && index < mAirData.length) {
            return mAirData[index] & 0xff;
        }
        return 0;
    }
}
