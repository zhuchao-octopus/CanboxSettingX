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

public class Info213 extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    private static final String TAG = "Golf7InfoSimpleFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.changcheng_bnr_info);

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    public boolean onPreferenceClick(Preference arg0) {
        String key = arg0.getKey();
        if ("fuelclear".equals(key)) {
            byte[] buf = new byte[]{0x4, (byte) 0x1b, 0x02, 0x02, 0x1, (byte) 0xff};
            BroadcastUtil.sendCanboxInfo(getActivity(), buf);
        }
        return false;
    }

    boolean mPaused = true;

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
        unregisterListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPaused = false;
        registerListener();

        requestInitData();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private final static int[] INIT_CMDS = {0x10, 0x11};

    private void requestInitData() {
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 500));
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

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }


    private void sendCanboxInfo(int d0) {
        byte[] buf = new byte[]{(byte) 0x83, 2, (byte) d0, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void updateView(byte[] buf) {
        int index;
        String s = "";
        switch (buf[0]) {
            case 0x11:
                index = ((buf[2] & 0xff));
                if (index >= 1 && index <= 0x7f) {

                    s = getString(R.string.left_leaning) + index;
                } else if (index >= 0x80 && index <= 0xff) {
                    index = index - 0x80;
                    s = getString(R.string.right_deviation) + index;
                } else {
                    s = "";
                }
                setPreference("left_and_right_inclination", s);


                index = ((buf[4] & 0xff));
                if (index >= 1 && index <= 0x7fe) {
                    s = getString(R.string.anteversion) + index;
                } else if (index >= 0x80 && index <= 0xff) {
                    index = index - 0x80;
                    s = getString(R.string.backward_tilt) + index;
                } else {
                    s = "";
                }
                setPreference("back_and_forth_tilt", s);

                break;
            case 0x10:

                index = ((buf[3] & 0xff) | ((buf[2] & 0xff) << 8));
                s = index + " hpa";
                setPreference("atmosphere_pressure", s);

                index = ((buf[4] & 0xff));
                if (index >= 10 && index <= 150) {
                    s = index + getString(R.string.temp_unic);
                } else {
                    s = "--";
                }
                setPreference("engine_temp", s);


                index = ((buf[5] & 0xff));
                if (index >= 10 && index <= 150) {
                    s = index + getString(R.string.temp_unic);
                } else {
                    s = "--";
                }
                setPreference("transmission_oil_temperature", s);

                index = ((buf[6] & 0xff));

                s = index / 10 + "." + index % 10 + " V";
                setPreference("battery_voltage", s);

                index = ((buf[7] & 0xff));

                s = index + "%";
                setPreference("front_wheel_torque_ratio", s);


                index = ((buf[8] & 0xff));
                if (index == 1) {
                    s = getString(R.string.mount);
                } else {
                    s = getString(R.string.unmounted);
                }
                setPreference("trailer_status", s);


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
