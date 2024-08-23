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
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.NodePreference;

public class Info292 extends PreferenceFragmentCompat {
    private static final String TAG = "HYSettingsRaiseFragment";

    private static final NodePreference[] NODES = {new NodePreference("camry_distance_1", 0), new NodePreference("mileage_sum", 0), new NodePreference("water_temp", 0), new NodePreference("engine_speed", 0), new NodePreference("am_runningspeed", 0),};

    private final static int[] INIT_CMDS = {0x40};
    private boolean mPaused = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                sendCanboxInfo(msg.what & 0xff);
            }
        }
    };
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.empty_setting);
        init();

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    private void init() {

        for (int i = 0; i < NODES.length; ++i) {
            Preference p = NODES[i].createPreference(getActivity());
            if (p != null) {

                Preference ps = getPreferenceScreen();
                if (ps instanceof PreferenceScreen) {
                    ((PreferenceScreen) ps).addPreference(p);
                }

            }
        }
    }

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

    private void requestInitData() {
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 500));
        }
    }

    private void sendCanboxInfo(int d0) {
        byte[] buf = new byte[]{(byte) 0x90, 1, (byte) d0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void setPreference(String key, String s) {
        Preference ps = getPreferenceScreen();

        Preference p = ((PreferenceScreen) ps).findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }

    private void updateView(byte[] buf) {

        int index;
        switch (buf[0]) {
            case 0x40:
                String s;
                index = buf[4];
                if (index < 0) {
                    index++;
                }
                s = index + getResources().getString(R.string.temp_unic);

                setPreference(NODES[2].mKey, s);


                setPreference(NODES[0].mKey, (((buf[8] & 0xff) << 8) | (buf[9] & 0xff)) + "KM");

                setPreference(NODES[1].mKey, (((buf[10] & 0xff) << 16) | ((buf[11] & 0xff) << 8) | (buf[12] & 0xff)) + "KM");


                index = ((buf[5] & 0xff));
                s = index + " km/h";
                setPreference(NODES[4].mKey, s);

                index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));
                s = String.format("%d RPM", index);
                setPreference(NODES[3].mKey, s);

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
                                Log.d(TAG, "updateView:Exception " + e);
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
