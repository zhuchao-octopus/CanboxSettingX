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

public class Info268 extends PreferenceFragmentCompat {
    private static final String TAG = "HYSettingsRaiseFragment";
    private static final NodePreference[] NODES = {new NodePreference("remaining_power", 0x31), new NodePreference("battery_temperature", 0x31), new NodePreference("battery_voltage", 0x31), new NodePreference("battery_current", 0x31), new NodePreference("motor_temperature", 0x31), new NodePreference("control_module_voltage", 0x31), new NodePreference("motor_controller_failure", 0x31),};

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


        switch (buf[0]) {
            case 0x31:
                setPreference(NODES[0].mKey, buf[2] + "%");
                setPreference(NODES[1].mKey, buf[3] + getString(R.string.temp_unic_centigrade));
                setPreference(NODES[2].mKey, (((buf[4] & 0xff) << 8) | (buf[5] & 0xff)) + "V");
                setPreference(NODES[3].mKey, buf[6] + "A");
                setPreference(NODES[4].mKey, buf[7] + getString(R.string.temp_unic_centigrade));
                setPreference(NODES[5].mKey, (((buf[8] & 0xff) << 8) | (buf[9] & 0xff)) + "V");
                setPreference(NODES[6].mKey, (buf[10] == 1) ? getString(R.string.motor_failure) : getString(R.string.ordinary));
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
