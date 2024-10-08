package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;

public class GmInfoHiworldFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    private static final String TAG = "ToyotaInfoHiworldFragment";
    //初始化命令
    private final static int[] INIT_CMDS = {0x34, 0x32, 0x12,};
    static int uint_index = 0;
    static int value_index = 0;
    private boolean mPaused = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                sendCanboxInfo0x90((msg.what & 0xff00) >> 8, msg.what & 0xff);

            }
        }
    };
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.gm_hiworld_info);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    private void requestInitData() {
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 200));
        }

    }

    private void sendCanboxInfo0x90(int d0, int d1) {
        byte[] buf = new byte[]{0x3, (byte) 0x60, 0x5, 1, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    public boolean onPreferenceClick(Preference arg0) {

        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterListener();
        mPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListener();
        mPaused = false;
        requestInitData();
    }

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }

    private void updateView(byte[] buf) {

        int index = 0;
        double findex = 0.0;
        String str;
        java.text.DecimalFormat myformat;
        String s = "";
        switch (buf[0]) {
            case (byte) 0x32: {
                //速度


                //发动机转速
                index = ((buf[5] & 0xff) | (buf[4] & 0xff) << 8);
                s = "" + index + "RPM";
                setPreference("engine_speed55", s);

                //车速
                index = ((buf[7] & 0xff) | (buf[6] & 0xff) << 8);
                s = "" + index + "km/h";
                setPreference("commander_speed", s);
            }
            break;
            case (byte) 0x34: {
                //相关单元
                index = (buf[24] & 0x03);
                uint_index = index;

                //瞬时油耗
                index = ((buf[3] & 0xff) | (buf[2] & 0xff) << 8);
                value_index = index;
                switch (uint_index) {
                    case (byte) 0x00: {
                        findex = value_index * 0.1;
                        myformat = new java.text.DecimalFormat("0.0");
                        str = myformat.format(findex);
                        s = "" + str + "mpg(UK)";
                        setPreference("fuel", s);
                        break;
                    }
                    case (byte) 0x01: {
                        findex = value_index * 0.1;
                        myformat = new java.text.DecimalFormat("0.0");
                        str = myformat.format(findex);
                        s = "" + str + "km/L";
                        setPreference("fuel", s);
                        break;
                    }
                    case (byte) 0x02: {
                        findex = value_index * 0.1;
                        myformat = new java.text.DecimalFormat("0.0");
                        str = myformat.format(findex);
                        s = "" + str + "L/100km";
                        setPreference("fuel", s);
                        break;
                    }
                    case (byte) 0x03: {
                        findex = value_index * 0.1;
                        myformat = new java.text.DecimalFormat("0.0");
                        str = myformat.format(findex);
                        s = "" + str + "L/H";
                        setPreference("fuel", s);
                        break;
                    }
                }
                break;
                //里程
            }
            case (byte) 0x12: {
                //电池电压
                index = (buf[9] & 0xff);
                findex = index * 0.1;
                myformat = new java.text.DecimalFormat("0.0");
                str = myformat.format(findex);
                s = "" + str + "v";
                setPreference("battery_voltage", s);

                //手刹信息
                index = buf[10];
                if (index == 0) {
                    s = getActivity().getString(R.string.brake_down);
                    setPreference("brake_state", s);
                } else {
                    s = getActivity().getString(R.string.brake_up);
                    setPreference("brake_state", s);
                }

                //左前窗
                index = buf[8] & 0x01;
                if (index == 0) {
                    s = getActivity().getString(R.string.gac_close);
                    setPreference("left_front_windows", s);
                } else {
                    s = getActivity().getString(R.string.opend);
                    setPreference("left_front_windows", s);
                }

                //右前窗
                index = buf[8] & 0x02;
                if (index == 0) {
                    s = getActivity().getString(R.string.gac_close);
                    setPreference("right_front_windows", s);
                } else {
                    s = getActivity().getString(R.string.opend);
                    setPreference("right_front_windows", s);
                }

                //左后窗
                index = buf[8] & 0x04;
                if (index == 0) {
                    s = getActivity().getString(R.string.gac_close);
                    setPreference("left_rear_windows", s);
                } else {
                    s = getActivity().getString(R.string.opend);
                    setPreference("left_rear_windows", s);
                }

                //右后窗
                index = buf[8] & 0x08;
                if (index == 0) {
                    s = getActivity().getString(R.string.gac_close);
                    setPreference("right_rear_windows", s);
                } else {
                    s = getActivity().getString(R.string.opend);
                    setPreference("right_rear_windows", s);
                }

                //当前档位
                switch (buf[3]) {
                    case (byte) 0x01: {
                        s = getActivity().getString(R.string.p_d);
                        setPreference("curent_gear", s);
                        break;
                    }
                    case (byte) 0x02: {
                        s = getActivity().getString(R.string.n_d);
                        setPreference("curent_gear", s);
                        break;
                    }
                    case (byte) 0x03: {
                        s = getActivity().getString(R.string.r_d);
                        setPreference("curent_gear", s);
                        break;
                    }
                    case (byte) 0x04: {
                        s = getActivity().getString(R.string.d_d);
                        setPreference("curent_gear", s);
                        break;
                    }
                    case (byte) 0x00: {
                        s = getActivity().getString(R.string.nothing);
                        setPreference("curent_gear", s);
                        break;
                    }

                }
            }
            case (byte) 0x19: {

            }
        }
    }

    private void unregisterListener() {
        if (mReceiver != null) {
            this.getActivity().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private void registerListener() {
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(MyCmd.BROADCAST_SEND_FROM_CAN)) {

                        byte[] buf = intent.getByteArrayExtra("buf");
                        if (buf != null) {

                            try {
                                Log.d("hhhxg", "!!!!" + buf);
                                updateView(buf);
                            } catch (Exception e) {
                                Log.d("aa", "!!!!!!!!" + buf);
                            }
                        }
                    }
                }
            };
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction(MyCmd.BROADCAST_SEND_FROM_CAN);

            this.getActivity().registerReceiver(mReceiver, iFilter);
        }
    }

}
