package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.NodePreference;

public class Info298 extends PreferenceFragmentCompat {
    private static final String TAG = "HYSettingsRaiseFragment";

    private static final NodePreference[] NODES = {new NodePreference("remaining_power", 0), new NodePreference("motor_power", 0), new NodePreference("system1", 0), new NodePreference("reference_to_remaining_mileage", 0), new NodePreference("average_energy_consumption", 0),};

    private final static int[] INIT_CMDS = {0x31};

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

    private boolean mPaused = true;

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
            case 0x31:
                String s;
                index = buf[2] & 0xff;

                s = index + "%";
                setPreference(NODES[0].mKey, s);


                index = buf[3] & 0xff;
                s = index + "KW";
                setPreference(NODES[1].mKey, s);


                index = buf[4] & 0xff;
                s = index + "KW";
                setPreference(NODES[2].mKey, s);

                setPreference(NODES[3].mKey, (((buf[5] & 0xff) << 8) | (buf[6] & 0xff)) + "KM");


                index = ((buf[8] & 0xff) | ((buf[7] & 0xff) << 8));
                s = String.format("%d.%d kWh/100km", index / 10, index % 10);
                setPreference(NODES[4].mKey, s);

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
