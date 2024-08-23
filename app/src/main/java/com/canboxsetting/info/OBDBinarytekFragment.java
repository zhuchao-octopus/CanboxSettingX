package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;

public class OBDBinarytekFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    private static final String TAG = "OBDBinarytekFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.obd_binarytek);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    private final static int[] INIT_CMDS = {0x8905, 0x8a05, 0x8b0a};
    private final static int[] DEINIT_CMDS = {0x8900, 0x8a00, 0x8b00};

    private void requestInitData() {
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 200));
        }

    }

    private void requestDeinitData() {
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < DEINIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(DEINIT_CMDS[i], (i * 100));
        }

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused || ((msg.what & 0xff) == 0)) {
                sendCanboxInfo((msg.what & 0xff00) >> 8, msg.what & 0xff);
            }
        }
    };

    private void sendCanboxInfo(int d0, int d1) {
        byte[] buf = new byte[]{(byte) d0, 0x1, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    public boolean onPreferenceClick(Preference arg0) {

        return false;
    }

    private boolean mPaused = true;

    @Override
    public void onPause() {
        super.onPause();
        requestDeinitData();
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
        String s = "";

        switch (buf[0]) {
            case 0x68: {

                index = ((buf[2] & 0xff) | ((buf[3] & 0xff) << 8));
                s = index + " rpm";
                setPreference("enginespeed", s);

            }
            break;

            case 0x6a: {

                index = ((buf[2] & 0xff) | ((buf[3] & 0xff) << 8));
                s = index + " km/h";
                setPreference("speed", s);

            }
            break;

            case 0x27: {

                index = (buf[2]);
                s = index + " °C";
                setPreference("coolant_temp", s);

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
