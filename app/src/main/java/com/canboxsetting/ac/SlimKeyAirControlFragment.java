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

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.R.id;
import com.canboxsetting.R.layout;
import com.canboxsetting.R.string;

import com.canboxsetting.view.ACSpeedLevelSelect;
import com.canboxsetting.view.ACTempLevelSelect;
import com.common.utils.BroadcastUtil;
import com.common.utils.GlobalDef;
import com.common.utils.MachineConfig;
import com.common.utils.MyCmd;
import com.zhuchao.android.fbase.ByteUtils;
import com.zhuchao.android.fbase.MMLog;

import java.util.Locale;
import java.util.Objects;

/**
 * This activity plays a video from a specified URI.
 */
public class SlimKeyAirControlFragment extends MyFragment {
    private static final String TAG = "SlimKeyAirControlFragment";

    private ACTempLevelSelect acTempLevelSelect;
    private  ACSpeedLevelSelect acSpeedLevelSelect;
    private ImageView acSpeedUp,acSpeedMiddle,acSpeedDown;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
//        getActivity().getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//        );
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    private View mMainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(layout.ac_peugeot_slim, container, false);
        if ((GlobalDef.getProId() == 186)) {
            mMainView.findViewById(id.wheel).setVisibility(View.GONE);
        }

        MMLog.d(TAG, "SlimKeyAirControlFragment.onCreateView!");
        initSlimKeyACData();
        return mMainView;
    }

    private void sendCanboxSlim(byte d0, byte d1) {
        byte[] buf = new byte[]{
                0x00, (byte) 0xA4, 0x01, 0x00, 0x02, d0, d1, (byte) (0xA7 + d0 + d1)
        };
        MMLog.d(TAG, "sendCanboxInfo: buf = " + ByteUtils.BuffToHexStr(buf));
        BroadcastUtil.sendCanboxInfo(requireActivity(), buf);
    }

    private void updateSelect(int id, int s) {
        View v = mMainView.findViewById(id);
        if (v != null) {
            v.setSelected(s != 0);
        }
    }

    private void setSpeed(int speed) {
        currAirWind = (byte) speed;
        ACSpeedLevelSelect acSpeedLevelSelect = mMainView.findViewById(id.ac_speed_level);
        acSpeedLevelSelect.setLevel(speed);
    }

    private void setLoop(int loop) {
        ImageView v = (ImageView) mMainView.findViewById(id.air_title_ce_inner_loop);
        if (v != null) {
            if (loop == 0) {
                v.getDrawable().setLevel(0);
            } else {
                v.getDrawable().setLevel(1);
                ///MMLog.d(TAG,"!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }
    }

    private byte currTemp = 1;
    private void setSlimTemp(byte temp) {
        Log.d(TAG, "setSlimTemp: temp " + temp);
        if (temp > 0 && temp <= 16) {
            sendCanboxSlim((byte) 0x83, temp);
//            updateTempView(temp);

        } else if (temp < 1) {
            setSlimTemp((byte) 1);
        } else if (temp > 16) {
            setSlimTemp((byte) 16);
        }
    }

    private void updateTempView(byte temp) {
        currTemp = temp;
        ACTempLevelSelect acTempLevelSelect = mMainView.findViewById(id.ac_temp_level);
        if (acTempLevelSelect != null) {
            acTempLevelSelect.setLevel(temp);
        }
    }

    private final Handler mHandler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            sendCanboxSlim((byte) 0x00, (byte) msg.arg1);
        }
    };

    @Override
    public void onPause() {
        unregisterListener();
        sendCanboxSlim((byte) 0x80, (byte) 0x00);
        super.onPause();
    }

    @Override
    public void onResume() {
        registerListener();
        //sendCanboxInfo0x90(0x21);
//        mHandler.sendEmptyMessageDelayed(0, 500);
//        mHandler.sendEmptyMessageDelayed(0, 1000);
        updateACInfo();
        super.onResume();
    }

    private void updateACInfo() {
        sendCanboxSlim((byte) 0x80, (byte) 0x01);
        Message message0 = mHandler.obtainMessage();
        message0.arg1 = 0;
        mHandler.sendMessageDelayed(message0,1000);
//        Message message1 = mHandler.obtainMessage();
//        message1.arg1 = 1;
//        mHandler.sendMessageDelayed(message1,3000);
//        Message message2 = mHandler.obtainMessage();
//        message2.arg1 = 2;
//        mHandler.sendMessageDelayed(message2,5000);
//        Message message3 = mHandler.obtainMessage();
//        message3.arg1 = 3;
//        mHandler.sendMessageDelayed(message3,7000);
//        Message message4 = mHandler.obtainMessage();
//        message4.arg1 = 4;
//        mHandler.sendMessageDelayed(message4,9000);
//        Message message5 = mHandler.obtainMessage();
//        message5.arg1 = 5;
//        mHandler.sendMessageDelayed(message5,11000);
//        Message message6 = mHandler.obtainMessage();
//        message6.arg1 = 6;
//        mHandler.sendMessageDelayed(message6,13000);
    }

    private BroadcastReceiver mReceiver;

    private void unregisterListener() {
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerListener() {
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action == null) return;
                    if (action.equals(MyCmd.BROADCAST_SEND_FROM_CAN)) {
                        byte[] buf = intent.getByteArrayExtra("buf");
                        if (buf != null) {
                            try {
                                //                                updateView(buf);
                                updateSlimView(buf);
                            } catch (Exception e) {
                                Log.d("aa", "!!!!!!!!" + e);
                            }
                        }
                    }
                }
            };

            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction(MyCmd.BROADCAST_SEND_FROM_CAN);
            getActivity().registerReceiver(mReceiver, iFilter);
        }
    }

    private void updateSlimView(byte[] buf) {
        if (buf != null && buf.length == 6 && buf[0] == 0x00 && buf[1] == 0x00 && buf[2] == 0x02) {
            if (buf[3] == 0x05 && buf[4] == 0x01) {
                MMLog.d(TAG, "updateSlimView: 打开AC");
                updateSelect(id.air_title_ce_ac_1, 1);
            } else if (buf[3] == 0x05 && buf[4] == 0x00) {
                MMLog.d(TAG, "updateSlimView: 关闭AC");
                updateSelect(id.air_title_ce_ac_1, 0);
            } else if (buf[3] == 0x06 && (buf[4] == 0x00 || buf[4] == 0x01)) {
                MMLog.d(TAG, "updateSlimView: AC MAX = " + buf[4]);
                updateSelect(id.air_title_ce_ac_max, buf[4]);
            }else if (buf[3] == 0x00 && buf[4] == 0x02) {
                MMLog.d(TAG, "updateSlimView: 打开外循环");
                setLoop(0);
            } else if (buf[3] == 0x00 && buf[4] == 0x00) {
                MMLog.d(TAG, "updateSlimView: 关闭View");
                getActivity().finish();
            } else if (buf[3] == 0x00 && buf[4] == 0x03) {
                MMLog.d(TAG, "updateSlimView: 打开内循环");
                setLoop(1);
            } else if (buf[3] == 0x00 && buf[4] == 0x04) {
                MMLog.d(TAG, "updateSlimView: 后除霜器已关闭");
                updateSelect(id.air_title_ce_rear, 0);
            } else if (buf[3] == 0x00 && buf[4] == 0x05) {
                MMLog.d(TAG, "updateSlimView: 后除霜器已打开");
                updateSelect(id.air_title_ce_rear, 1);
            } else if (buf[3] == 0x01 && buf[4] >= 0x00 && buf[4] <= 4) {
                MMLog.d(TAG, "updateSlimView: 空气方向 = " + buf[4]);
                updateAirDirection(buf[4]);
            } else if (buf[3] == 0x02 && buf[4] > 0 && buf[4] <= 7) {
                MMLog.d(TAG, "updateSlimView: 风速调节 = " + buf[4]);
                setSpeed(buf[4]);
            } else if (buf[3] == 0x03 && buf[4] > 0 && buf[4] <= 16) {
                MMLog.d(TAG, "updateSlimView: 温度调节 = " + buf[4]);
                updateTempView(buf[4]);
            } else if (buf[3] == 0x04 && (buf[4] == 0x00 || buf[4] == 0x01)) {
                MMLog.d(TAG, "updateSlimView: 空调开关 = " + buf[4]);
                updatePower(buf[4] == 0x01);
            }
        }
        super.callBack(0);
    }

    private void updatePower(boolean isPower) {
        byte[] slimKeyACData = new byte[]{0x00,0,0,0,0,0,1,0,0};
        if (isPower) {
            String acData = MachineConfig.getProperty("AC_UPDATE_DATA");
            if (!TextUtils.isEmpty(acData)) {
                slimKeyACData = HexStr2Bytes(acData.replace(" ", ""));
                updateACAllView(slimKeyACData);
                //updateSelect(id.icon_power, 0x01);
            }
        } else {
               slimKeyACData = new byte[]{0x00,0,0,0,0,0,0,0,0};
               //updateSelect(id.icon_power, 0x00);
        }
        updateACAllView(slimKeyACData);
    }

    private void controlAirDirection(byte cmd) {
        sendCanboxSlim((byte) 0x81, cmd);
        //updateAirDirection(cmd);
    }
    private void updateAirDirection(byte cmd) {
        updateSelect(id.canbus21_mode1, cmd == 0x00 ? 1 : 0);
        updateSelect(id.canbus21_mode2, cmd == 0x02 ? 1 : 0);
        updateSelect(id.canbus21_mode3, cmd == 0x01 ? 1 : 0);
        updateSelect(id.canbus21_mode4, cmd == 0x03 ? 1 : 0);
        //updateSelect(id.canbus21_mode5, cmd == 0x04 ? 1 : 0);
        updateSelect(id.air_title_ce_max, cmd == 0x04 ? 1 : 0);
        if (cmd == 0x03 || cmd == 0x04) {
            acSpeedUp.setImageResource(R.mipmap.slim_speed_up_on);
        } else {
            acSpeedUp.setImageResource(R.mipmap.slim_speed_up_off);
        }

        if (cmd == 0x00 || cmd == 0x01) {
            acSpeedMiddle.setImageResource(R.mipmap.slim_speed_middle_on);
        } else {
            acSpeedMiddle.setImageResource(R.mipmap.slim_speed_middle_off);
        }

        if (cmd == 0x02 || cmd == 0x01 || cmd == 0x03) {
            acSpeedDown.setImageResource(R.mipmap.slim_speed_down_on);
        } else {
            acSpeedDown.setImageResource(R.mipmap.slim_speed_down_off);
        }
    }

    public void onClick(View v) {
        ///sendCmd(v.getId());
        if (v.getId() == id.air_title_ce_auto_large) {

        } else if (v.getId() == id.air_title_ce_ac_1) {
            switchStatus(v, (byte) 0x85);
        } else if (v.getId() == id.air_title_ce_ac_max) {
            switchStatus(v, (byte) 0x86);
        } else if (v.getId() == id.con_left_temp_up) {
            setSlimTemp(++currTemp);
        } else if (v.getId() == id.con_left_temp_down) {
            setSlimTemp(--currTemp);
        } else if (v.getId() == id.con_right_temp_up) {
            setSlimTemp(++currTemp);
        } else if (v.getId() == id.con_right_temp_down) {
            setSlimTemp(--currTemp);
        } else if (v.getId() == id.canbus21_mode1) {
            controlAirDirection((byte) 0x00);
        } else if (v.getId() == id.canbus21_mode2) {
            controlAirDirection((byte) 0x02);
        } else if (v.getId() == id.canbus21_mode3) {
            controlAirDirection((byte) 0x01);
        } else if (v.getId() == id.canbus21_mode4) {
            controlAirDirection((byte) 0x03);
        } else if (v.getId() == id.canbus21_mode5) {
            controlAirDirection((byte) 0x04);
        } else if (v.getId() == id.wind_minus) {
            airWindControl(--currAirWind);
        } else if (v.getId() == id.wind_add) {
            airWindControl(++currAirWind);
        } else if (v.getId() == id.air_title_sync) {

        } else if (v.getId() == id.icon_power) {
            switchStatus(v, (byte) 0x84);
        } else if (v.getId() == id.air_title_ce_max) {//前窗
//            updateAirDirection((byte) 0x04);
            if (v.isSelected()) {
                controlAirDirection((byte) 0x00);
            } else {
                controlAirDirection((byte) 0x04);
            }
        } else if (v.getId() == id.air_title_ce_rear) {//后窗
            if (v.isSelected()) {
//                updateSelect(id.air_title_ce_rear, 0);
                sendCanboxSlim((byte) 0x80, (byte) 0x04);
            } else {
//                updateSelect(id.air_title_ce_rear, 1);
                sendCanboxSlim((byte) 0x80, (byte) 0x05);
            }
        } else if (v.getId() == id.air_title_ce_inner_loop) {
            ImageView vv = (ImageView) v;
            int level = vv.getDrawable().getLevel();
            if (level == 0) {
                sendCanboxSlim((byte) 0x80, (byte) 0x03);
//                vv.getDrawable().setLevel(1);
            } else {
                sendCanboxSlim((byte) 0x80, (byte) 0x02);
//                vv.getDrawable().setLevel(0);
            }
        } else if (v.getId() == id.close_ac) {
//            getActivity().finish();
            getActivity().onBackPressed();  // 发送返回事件
        }
    }

    private byte currAirWind = 1;

    private void airWindControl(byte windValue) {
        MMLog.d(TAG, "airWindControl: windValue = " + windValue);
        if (windValue > 0 && windValue <= 7) {
            sendCanboxSlim((byte) 0x82, windValue);
//            setSpeed(windValue);

        } else if (windValue < 1){
            airWindControl((byte) 1);
        } else if (windValue > 7) {
            airWindControl((byte) 7);
        }
    }


    private void switchStatus(View v, byte id) {
        if (v.isSelected()) {
            sendCanboxSlim(id, (byte) 0x00);
//            v.setSelected(false);
        } else {
            sendCanboxSlim(id, (byte) 0x01);
//            v.setSelected(true);
        }
    }


    private void initSlimKeyACData() {
        String acData = MachineConfig.getProperty("AC_UPDATE_DATA");
        Log.d(TAG, "initSlimKeyACData: acData = " + acData);
        acSpeedUp = mMainView.findViewById(id.ac_speed_up);
        acSpeedMiddle = mMainView.findViewById(id.ac_speed_middle);
        acSpeedDown = mMainView.findViewById(id.ac_speed_down);
        if (!TextUtils.isEmpty(acData)) {
            byte[] slimKeyACData = HexStr2Bytes(acData.replace(" ", ""));
            updateACAllView(slimKeyACData);
        }
        acTempLevelSelect = mMainView.findViewById(id.ac_temp_level);
        acTempLevelSelect.setListener(new ACSpeedLevelSelect.OnACLevelClickListener() {
            @Override
            public void onACLevelClickListener(int level) {
                setSlimTemp((byte) level);
            }
        });
        acSpeedLevelSelect = mMainView.findViewById(id.ac_speed_level);
        acSpeedLevelSelect.setListener(new ACSpeedLevelSelect.OnACLevelClickListener() {
            @Override
            public void onACLevelClickListener(int level) {
                airWindControl((byte) level);
            }
        });
    }

    private void updateACAllView(byte[] slimKeyACData) {
        ImageView vv = (ImageView) mMainView.findViewById(id.air_title_ce_inner_loop);
        vv.getDrawable().setLevel(slimKeyACData[2] & 0x02);
        updateSelect(id.air_title_ce_rear, slimKeyACData[2] & 0x04);//后窗加热
        updateAirDirection(slimKeyACData[3]);
        setSpeed(slimKeyACData[4]);
        updateTempView(slimKeyACData[5]);
        updateSelect(id.icon_power, slimKeyACData[6]);
        updateSelect(id.air_title_ce_ac_1, slimKeyACData[7]);
        updateSelect(id.air_title_ce_ac_max, slimKeyACData[8]);
    }

    public static byte[] HexStr2Bytes(String src) {
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = (byte) Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }
}
