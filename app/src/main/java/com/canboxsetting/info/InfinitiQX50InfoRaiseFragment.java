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

public class InfinitiQX50InfoRaiseFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    private static final String TAG = "InfinitiQX50InfoRaiseFragment";
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

        addPreferencesFromResource(R.xml.infinitiqx50_info_raise);
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

    private void updateView(byte[] buf) {

        int index = 0;
        int uint = 0;
        String s = "";
        double findex = 0.0;
        String str;
        java.text.DecimalFormat myformat;
        switch (buf[0]) {
            case (byte) 0x27: {
                setPreference("consumption_hundredkilometer", s);
                //总里程
                index = ((buf[3] & 0xff) | ((buf[4] & 0xff) << 8) | ((buf[5] & 0xff) << 16) | ((buf[6] & 0xff) << 24));
                uint = buf[2] & 0x01;
                if (uint == 0) {
                    s = "" + index + "Km";
                } else {
                    s = "" + index + "mile";
                }
                //总里程
                setPreference("total_mileage", s);


                //续航里程
                index = ((buf[7] & 0xff) | ((buf[8] & 0xff) << 8));
                findex = index * 0.1;
                myformat = new java.text.DecimalFormat("0.0");
                str = myformat.format(findex);
                uint = buf[2] & 0x02;
                if (uint == 0) {
                    s = "" + str + "Km";
                } else {
                    s = "" + str + "mile";
                }
                setPreference("estimate_range", s);
            }
            break;
            case (byte) 0x29: {
                //瞬间油耗
                index = ((buf[5] & 0xff) | ((buf[6] & 0xff) << 8));
                findex = index * 0.1;
                myformat = new java.text.DecimalFormat("0.0");
                str = myformat.format(findex);
                uint = buf[2] & 0x03;
                if (uint == 0) {
                    s = "" + str + "mpg";
                } else if (uint == 1) {
                    s = "" + str + "km/l";
                } else {
                    s = "" + str + "l/100km";
                }
                setPreference("instant_fuel", s);
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
