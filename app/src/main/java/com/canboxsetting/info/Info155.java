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

public class Info155 extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    private static final String TAG = "KadjarRaiseFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.accord8_info);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    private final static int[] INIT_CMDS = {0xe, 0x3302, 0xd300};

    private void requestInitData() {
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 200));
        }

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                sendCanboxInfo0x90((msg.what & 0xff00) >> 8, msg.what & 0xff);

            }
        }
    };

    private void sendCanboxInfo0x90(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0xff, 0x1, (byte) d0};
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

    private int getFuelrange(byte b) {
        int ret = 0;
        switch (b) {
            case 0:
                ret = 60;
                break;
            case 1:
                ret = 10;
                break;
            case 2:
                ret = 12;
                break;
            case 3:
                ret = 20;
                break;
            case 4:
                ret = 30;
                break;
            case 5:
                ret = 40;
                break;
            case 6:
                ret = 50;
                break;
            case 7:
                ret = 60;
                break;
            case 8:
                ret = 70;
                break;
            case 9:
                ret = 80;
                break;
            case 10:
                ret = 90;
                break;
            case 11:
                ret = 100;
                break;
        }
        return ret;
    }

    private void updateView(byte[] buf) {

        int index = 0;
        String s = "";

        switch (buf[0]) {
            case 0xe: {
                index = (buf[2] & 0xff);
                s = "l/l00km";
                s = index + " " + s;
                setPreference("instant", s);

                index = ((buf[3] & 0xff) | ((buf[4] & 0xff) << 8));
                s = "l/l00km";
                if (index != 0xffff) {
                    s = String.format("%d.%d", index / 10, index % 10) + " " + s;
                } else {
                    s = "-- " + s;
                }
                setPreference("tripA_average_fuel_consumption", s);

                index = ((buf[5] & 0xff) | ((buf[6] & 0xff) << 8));
                s = "l/l00km";
                if (index != 0xffff) {
                    s = String.format("%d.%d", index / 10, index % 10) + " " + s;
                } else {
                    s = "-- " + s;
                }
                setPreference("tripB_average_fuel_consumption", s);

                s = (buf[7] & 0xff) + ":" + (buf[8] & 0xff);
                setPreference("driving_time", s);

                index = (buf[9] & 0xff);

                s = "km/h";
                if (index == 0xff) {
                    s = " -- " + s;
                } else {
                    s = index + " " + s;
                }
                setPreference("avg_speed_1", s);

                index = ((buf[10] & 0xff) | ((buf[11] & 0xff) << 8));
                s = "km";
                if (index != 0xffff) {
                    s = index + " " + s;
                } else {
                    s = "-- " + s;
                }
                setPreference("mileage17", s);

                index = ((buf[12] & 0xff) | ((buf[13] & 0xff) << 8));
                s = "km";
                if (index != 0xffff) {
                    s = index + " " + s;
                } else {
                    s = "-- " + s;
                }
                setPreference("mileage_sum", s);

            }

            break;

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
