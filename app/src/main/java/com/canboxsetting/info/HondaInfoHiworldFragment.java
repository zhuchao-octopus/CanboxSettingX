package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceFragmentCompat;

import android.util.Log;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;

public class HondaInfoHiworldFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    private static final String TAG = "ToyotaInfoHiworldFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.honda_hiworld_info);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    //初始化命令
    private final static int[] INIT_CMDS = {0x16, 0x17, 0x32};

    private void requestInitData() {
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 100));
        }

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                sendCanboxInfo(msg.what & 0xff);
            }
        }
    };

    private void sendCanboxInfo(int d0) {
        byte[] buf = new byte[]{0x3, (byte) 0x6a, 0x5, 0x1, (byte) d0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    public boolean onPreferenceClick(Preference arg0) {

        return false;
    }

    private boolean mPaused = true;

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
        int uint = 0;    //瞬时油耗的单位
        String str;
        java.text.DecimalFormat myformat;
        String s = "";
        switch (buf[0]) {
            case (byte) 0x16: {
                //瞬时油耗
                index = (buf[2] & 0xff);
                s = "" + index + "KML";
                //瞬时油耗单位
                uint = buf[14] & 0x03;
                if (uint == 0) {
                    s = "" + index + "mpg";

                } else if (uint == 1) {
                    s = "" + index + "km/L";
                } else {
                    s = "" + index + "L/100Km";
                }
                setPreference("dynamical_fuel", s);

                uint = buf[14] & 0x0C;
                //当前平均耗油
                index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));
                findex = index * 0.1;
                myformat = new java.text.DecimalFormat("0.0");
                str = myformat.format(findex);
                if (uint == 0x00) {
                    s = "" + str + "mpg";

                } else if (uint == 4) {
                    s = "" + str + "km/L";
                } else {
                    s = "" + str + "L/100Km";
                }
                setPreference("current_consumption", s);

                //历史平均耗油
                index = ((buf[6] & 0xff) | ((buf[5] & 0xff) << 8));
                findex = index * 0.1;
                myformat = new java.text.DecimalFormat("0.0");
                str = myformat.format(findex);
                if (uint == 0x00) {
                    s = "" + str + "mpg";

                } else if (uint == 0x10) {
                    s = "" + str + "km/L";
                } else {
                    s = "" + str + "L/100Km";
                }
                setPreference("history", s);

                //平均油耗
                uint = buf[14] & 0x30;

                index = ((buf[8] & 0xff) | ((buf[7] & 0xff) << 8));
                findex = index * 0.1;
                myformat = new java.text.DecimalFormat("0.0");
                str = myformat.format(findex);

                if (uint == 0x00) {
                    s = "" + str + "mpg";

                } else if (uint == 0x10) {
                    s = "" + str + "km/L";
                } else {
                    s = "" + str + "L/100Km";
                }
                setPreference("i_stop_average_fuel_consumption", s);

                //续航里程
                uint = buf[14] & 0x80;
                index = ((buf[13] & 0xff) | ((buf[12] & 0xff) << 8));
                if (uint == 0x00) {
                    s = "" + index + "km";
                } else {
                    s = "" + index + "mile";
                }
                setPreference("camry_distance_1", s);

                //发动机转速
                index = (buf[16] & 0xff);
                s = "" + index + "RPM";
                setPreference("engine_speed_default", s);

                //驾驶状况履历
                //平均耗油
                index = ((buf[8] & 0xff) | ((buf[7] & 0xff) << 8));
                findex = index * 0.1;
                myformat = new java.text.DecimalFormat("0.0");
                str = myformat.format(findex);
                s = "" + str + "km";
                setPreference("tripA_average_fuel_consumption", s);

                //累计里程
                index = ((buf[11] & 0xff) | ((buf[10] & 0xff) << 8) | ((buf[9] & 0xff) << 16));
                findex = index * 0.1;
                myformat = new java.text.DecimalFormat("0.0");
                str = myformat.format(findex);
                s = "" + str + "km/L";
                setPreference("tripA_cumulative_mileage", s);
            }
            break;
            case (byte) 0x17: {
                //第一次
                index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8) | ((buf[2] & 0xff) << 16));
                findex = index * 0.1;
                myformat = new java.text.DecimalFormat("0.0");
                str = myformat.format(findex);
                s = "" + str + "km";
                setPreference("cumulative_mileage_1", s);

                index = ((buf[6] & 0xff) | ((buf[5] & 0xff) << 8));
                findex = index * 0.1;
                myformat = new java.text.DecimalFormat("0.0");
                str = myformat.format(findex);
                s = "" + str + "km";
                setPreference("i_stop_average_fuel_consumption_1", s);
                //第二次
                index = ((buf[9] & 0xff) | ((buf[8] & 0xff) << 8) | ((buf[7] & 0xff) << 16));
                findex = index * 0.1;
                myformat = new java.text.DecimalFormat("0.0");
                str = myformat.format(findex);
                s = "" + str + "km";
                setPreference("cumulative_mileage_2", s);

                index = ((buf[11] & 0xff) | ((buf[10] & 0xff) << 8));
                findex = index * 0.1;
                myformat = new java.text.DecimalFormat("0.0");
                str = myformat.format(findex);
                s = "" + str + "km/L";
                setPreference("i_stop_average_fuel_consumption_2", s);
                //第三次
                index = ((buf[14] & 0xff) | ((buf[13] & 0xff) << 8) | ((buf[12] & 0xff) << 16));
                findex = index * 0.1;
                myformat = new java.text.DecimalFormat("0.0");
                str = myformat.format(findex);
                s = "" + str + "km/L";
                setPreference("cumulative_mileage_3", s);

                index = ((buf[16] & 0xff) | ((buf[15] & 0xff) << 8));
                findex = index * 0.1;
                myformat = new java.text.DecimalFormat("0.0");
                str = myformat.format(findex);
                s = "" + str + "km/L";
                setPreference("i_stop_average_fuel_consumption_3", s);
            }
            break;
            case (byte) 0x32: {
                //发动机转速
                index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));
                s = "" + index;
                setPreference("instantaneous_speed", s);

                index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));
                s = "" + index;
                setPreference("engine_speed55", s);
            }
        }
    }

    private BroadcastReceiver mReceiver;

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
