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

public class Rx330HZInfoSimpleFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    private static final String TAG = "Rx330HZInfoSimpleFragment";
    private final static int[] INIT_CMDS = {0x3500};
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
        addPreferencesFromResource(R.xml.rx330_hz_info);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    private void requestInitData() {
        sendCanboxInfo(0x89, 0x0);
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], ((i + 1) * 200));
        }

    }

    private void sendCanboxInfo0x90(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0x90, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0xd0, 0x1, (byte) d1};
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
        sendCanboxInfo(0x89, 0x0);
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
            case 0x50: {
                index = (buf[2] & 0xff) | ((buf[3] & 0xff) << 8);

                s = String.format(Locale.ENGLISH, "%d RPM", index);
                setPreference("enginespeed", s);
            }
            break;
            case 0x16: {
                index = (buf[2] & 0xff) | ((buf[3] & 0xff) << 8);
                index = (index * 100) / 16;
                s = String.format(Locale.ENGLISH, "%d.%d l/l00km", index / 100, index % 100);
                setPreference("speed", s);
            }
            break;
            case 0x35: {
                if (buf.length >= 12) {

                    index = (buf[3] & 0xff) | ((buf[2] & 0xff) << 8);

                    s = String.format(Locale.ENGLISH, "%d.%d l/l00km", index / 10, index % 10);
                    setPreference("instant", s);


                    index = (buf[5] & 0xff) | ((buf[4] & 0xff) << 8);
                    s = String.format(Locale.ENGLISH, "%d.%d l/l00km", index / 10, index % 10);
                    setPreference("averagefuel", s);

                    index = (buf[7] & 0xff) | ((buf[6] & 0xff) << 8);
                    s = String.format(Locale.ENGLISH, "%d km", index);
                    setPreference("mileage", s);

                    index = (buf[9] & 0xff) | ((buf[8] & 0xff) << 8);
                    s = String.format(Locale.ENGLISH, "%d km/h", index);
                    setPreference("averageapeed", s);

                    index = (buf[11] & 0xff) | ((buf[10] & 0xff) << 8);
                    s = String.format(Locale.ENGLISH, "%d km", index);
                    setPreference("mileage1", s);


                }
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
