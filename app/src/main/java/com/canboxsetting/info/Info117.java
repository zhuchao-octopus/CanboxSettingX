package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;

import java.util.Locale;

public class Info117 extends PreferenceFragment {
    private static final String TAG = "Golf7InfoSimpleFragment";
    private final static int[] INIT_CMDS = {0x2902};
    boolean mPaused = true;
    private byte[] mBufUnit = new byte[4];
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ford_hiworld_info);

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

    private void requestInitData() {
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendMessageDelayed(mHandler.obtainMessage(0, INIT_CMDS[i], 0), i * 100);
        }
        mHandler.sendEmptyMessageDelayed(1, 800);
    }    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                switch (msg.what) {
                    case 0:
                        sendCanboxInfo(msg.arg1);
                        break;
                    case 1:
                        requestInitData();
                        break;
                }
            } else {
                mHandler.removeMessages(0);
                mHandler.removeMessages(1);
            }
        }
    };

    private void sendCanboxInfo(int d0) {

        byte[] buf = new byte[]{(byte) 0x90, 0x02, (byte) ((d0 & 0xff00) >> 8), (byte) (d0 & 0xff)};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }

    private void updateView(byte[] buf) {
        int index;
        String s = "";
        switch (buf[0]) {
            case 0x34:

                index = ((buf[8] & 0xff) | ((buf[7] & 0xff) << 8) | ((buf[6] & 0xff) << 8));

                s = String.format("%d.%d", index / 10, index % 10, Locale.ENGLISH);
                setPreference("total_mileage", s);
                break;
            case 0x32:
                index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));

                s = String.format("%d km/h", index, Locale.ENGLISH);
                setPreference("speed", s);

                index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));

                s = index + " RPM";
                setPreference("enginespeed", s);

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
