package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;

public class AudiInfoRaiseFragment extends PreferenceFragment {
    private static final String TAG = "KadjarRaiseFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.audi_raise_info);
    }

    private final static int[] INIT_CMDS = {0x4101, 0x4102, 0x4103,};

    private void requestInitData() {
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 100));
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
        byte[] buf = new byte[]{(byte) 0x90, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
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
            case 0x41: {
                switch (buf[2]) {
                    case 1:
                        if ((buf[3] & 0x80) != 0) {
                            s = getString(R.string.tips_17);
                        } else {
                            s = getString(R.string.regular);
                        }

                        setPreference("whether_wear_seat_belt", s);
                        if ((buf[3] & 0x40) != 0) {
                            s = getString(R.string.cleaning_fluid_too_low);
                        } else {
                            s = getString(R.string.regular);
                        }

                        setPreference("washwater_status", s);

                        break;
                    case 2:

                        index = ((buf[6] & 0xff) | ((buf[5] & 0xff) << 8));
                        s = index / 100 + " km/h";
                        setPreference("speed_instance", s);

                        index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));
                        s = String.format("%d RPM", index);
                        setPreference("engine_speed", s);


                        index = ((buf[8] & 0xff) | ((buf[7] & 0xff) << 8));
                        s = String.format("%d.%d V", index / 100, index % 100);
                        setPreference("battery_voltage", s);

                        index = ((buf[13] & 0xff) | ((buf[12] & 0xff) << 8) | ((buf[11] & 0xff) << 16));
                        s = "km";

                        s = String.format("%d", index) + " " + s;

                        setPreference("mileage", s);


                        index = ((buf[14] & 0xff));
                        s = String.format("%d L", index);
                        setPreference("remain_oid", s);

                        break;

                    case 3:
                        if ((buf[3] & 0x80) != 0) {
                            s = getString(R.string.remaining_oil_too_low);
                        } else {
                            s = getString(R.string.regular);
                        }

                        setPreference("fuel_warning_sign", s);
                        if ((buf[3] & 0x40) != 0) {
                            s = getString(R.string.battery_voltage_too_low);
                        } else {
                            s = getString(R.string.regular);
                        }
                        setPreference("battery_voltage_warning", s);
                        break;
                }

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
