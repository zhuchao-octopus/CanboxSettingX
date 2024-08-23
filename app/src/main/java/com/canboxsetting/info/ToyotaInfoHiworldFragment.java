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

import java.util.Locale;

public class ToyotaInfoHiworldFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    private static final String TAG = "ToyotaInfoHiworldFragment";
    //初始化命令
    private final static int[] INIT_CMDS = {};
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

        addPreferencesFromResource(R.xml.toyota_hiworld_info);
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
        byte[] buf = new byte[]{(byte) 0x90, 0x2, (byte) d0, (byte) d1};
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

    private String getOilString(int index, int unit) {
        String s;

        if (unit == 0) {
            s = index / 10 + "." + index % 10 + "MPG";
        } else if (unit == 1) {
            s = index / 10 + "." + index % 10 + "KM/L";
        } else {
            s = index / 10 + "." + index % 10 + "L/100KM";
        }
        //		setPreference("dynamical_fuel", s);
        return s;
    }

    private void updateView(byte[] buf) {

        int index = 0;
        String s = "";
        switch (buf[0]) {
            case (byte) 0x32: {
                //发动机转速
                index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));
                s = "" + index + "RPM";
                setPreference("engine_speed55", s);

                //顺时车速
                index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));
                s = "" + index + "Km/h";
                //
                setPreference("dynamical", s);
            }
            break;
            case (byte) 0x13: {
                //平均速度
                index = ((buf[11] & 0xff) | ((buf[10] & 0xff) << 8));
                s = "" + index + "Km/h";
                setPreference("averagespeed24", s);

                index = (buf[12] & 0xff);
                switch (index) {
                    case 0x00: {
                        setPreference("unit_of_fuel_consumption", "MPG");
                        break;
                    }
                    case 0x01: {
                        setPreference("unit_of_fuel_consumption", "km/L");
                        break;
                    }
                    case 0x02: {
                        setPreference("unit_of_fuel_consumption", "L/100km");
                        break;
                    }
                }

                //行驶时间
                index = ((buf[8] & 0xff) << 8 | (buf[9] & 0xff));
                s = String.format(Locale.ENGLISH, "%02d:%02d h", index / 60, index % 60);
                setPreference("traveltime", s);

                //续航
                index = ((buf[4] & 0xff) << 8 | (buf[5] & 0xff));
                s = index + "km";
                setPreference("driving_mileage", s);

                //瞬时油耗
                index = ((buf[2] & 0xff) << 8 | (buf[3] & 0xff));
                s = getOilString(index, buf[12]);
                setPreference("dynamical_fuel", s);


            }
            break;
            case (byte) 0x16: {
                //当前(现在)行程油耗
                index = ((buf[3] & 0xff) | ((buf[2] & 0xff) << 8));
                s = getOilString(index, buf[14]);
                setPreference("curfuel", s);

                //Trip1
                index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));

                s = getOilString(index, buf[14]);
                setPreference("trip1", s);

                //Trip2
                index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));

                s = getOilString(index, buf[14]);
                setPreference("trip2", s);

                //Trip3
                index = ((buf[9] & 0xff) | ((buf[8] & 0xff) << 8));

                s = getOilString(index, buf[14]);
                setPreference("trip3", s);

                //Trip4
                index = ((buf[11] & 0xff) | ((buf[10] & 0xff) << 8));

                s = getOilString(index, buf[14]);
                setPreference("trip4", s);

                //Trip5
                index = ((buf[13] & 0xff) | ((buf[12] & 0xff) << 8));

                s = getOilString(index, buf[14]);
                setPreference("trip5", s);


            }
            break;
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
