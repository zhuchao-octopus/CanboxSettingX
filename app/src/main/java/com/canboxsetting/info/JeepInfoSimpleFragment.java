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

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;

public class JeepInfoSimpleFragment extends PreferenceFragmentCompat {
    private static final String TAG = "JeepInfoSimpleFragment";
    private final static int[] INIT_CMDS = {0x4001, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0xa};
    private final static int MSG_REQUEST_INIT = 1000;
    private boolean mPaused = true;
    private byte mUnit = 0;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.jeep_simple_info);

        findPreference("tripa1").setTitle(getString(R.string.trip_a1) + " " + getString(R.string.averagefuel));
        findPreference("tripa2").setTitle(getString(R.string.trip_a1) + " " + getString(R.string.averageapeed));
        findPreference("tripa3").setTitle(getString(R.string.trip_a1) + " " + getString(R.string.mileage1));
        findPreference("tripa4").setTitle(getString(R.string.trip_a1) + " " + getString(R.string.traveltime1));

        findPreference("tripb1").setTitle(getString(R.string.trip_b1) + " " + getString(R.string.averagefuel));
        findPreference("tripb2").setTitle(getString(R.string.trip_b1) + " " + getString(R.string.averageapeed));
        findPreference("tripb3").setTitle(getString(R.string.trip_b1) + " " + getString(R.string.mileage1));
        findPreference("tripb4").setTitle(getString(R.string.trip_b1) + " " + getString(R.string.traveltime1));
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
        mHandler.removeMessages(MSG_REQUEST_INIT);
        unregisterListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPaused = false;
        requestInitData();

        registerListener();
    }    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                if (msg.what == MSG_REQUEST_INIT) {
                    requestInitData();
                } else {
                    if (msg.what <= 0xff) {
                        sendCanboxInfo(msg.what);
                    } else {
                        sendCanboxInfo2(msg.what);
                    }
                }
            }
        }
    };

    private void requestInitData() {
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.removeMessages(INIT_CMDS[i]);
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 100));
        }

        mHandler.removeMessages(MSG_REQUEST_INIT);
        mHandler.sendEmptyMessageDelayed(MSG_REQUEST_INIT, 15000);
    }

    private void sendCanboxInfo2(int cmd) {
        byte[] buf = new byte[]{(byte) 0x90, 0x02, (byte) ((cmd & 0xff00) >> 8), (byte) (cmd & 0xff)};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int cmd) {
        byte[] buf = new byte[]{(byte) 0x90, 0x02, 0x07, (byte) cmd};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }

    public String bcd2Str(byte b) {
        String c = "";
        if (b >= 0 && b < 10) {
            c = "" + b;
        } else if (b >= 0xa && b <= 0xf) {
            switch (b) {
                case 0xa:
                    c = "A";
                    break;
                case 0xb:
                    c = "B";
                    break;
                case 0xc:
                    c = "C";
                    break;
                case 0xd:
                    c = "D";
                    break;
                case 0xe:
                    c = "E";
                    break;
                case 0xf:
                    c = "F";
                    break;
            }
        }
        return c;
    }

    private String getFuelUnit() {
        String s;
        switch ((mUnit & 0x6) >> 1) {
            case 1:
                s = "KM/L";
                break;
            case 2:
                s = "MPG(US)";
                break;
            case 3:
                s = "MPG(UK)";
                break;
            case 0:
            default:
                s = "L/100KM";
                break;
        }
        return s;
    }

    private String getDistanceUnit() {
        String s;
        switch ((mUnit & 0x8) >> 3) {
            case 1:
                s = "MI";
                break;
            default:
                s = "KM";
                break;
        }
        return s;
    }

    private String getSpeedUnit() {
        String s;
        switch ((mUnit & 0x80) >> 7) {
            case 1:
                s = "MPH";
                break;
            default:
                s = "KM/h";
                break;
        }
        return s;
    }

    private void updateView(byte[] buf) {

        int index = 0;
        String s = "";

        switch (buf[0]) {
            case 0x28: {
                index = ((buf[2] & 0xff) | ((buf[3] & 0xff) << 8));

                s = index + getSpeedUnit();

                setPreference("speed", s);

                index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8));

                s = index + "RPM";

                setPreference("enginespeed", s);

            }
            break;
            case 0x7: {
                switch (buf[2]) {
                    case 0x1:

                        index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));

                        s = index + getDistanceUnit();

                        setPreference("mileage", s);
                        break;
                    case 0x2:
                        index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));
                        if (index == 0xffff) {
                            s = "----";
                        } else {
                            s = bcd2Str((byte) ((buf[3] & 0xf0) >> 4)) + bcd2Str((byte) (buf[3] & 0x0f)) + "." + bcd2Str((byte) ((buf[4] & 0xf0) >> 4)) + bcd2Str((byte) (buf[4] & 0x0f));

                            s += getFuelUnit();
                        }

                        setPreference("curfuel", s);
                        break;
                    case 0x3:
                        index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));
                        if (index == 0xffff) {
                            s = "----";
                        } else {

                            if (!"0".equals(bcd2Str((byte) ((buf[3] & 0xf0) >> 4)))) {
                                s = bcd2Str((byte) ((buf[3] & 0xf0) >> 4));
                            } else {
                                s = "";
                            }
                            s += bcd2Str((byte) (buf[3] & 0x0f)) + ".";
                            if (!"0".equals(bcd2Str((byte) ((buf[4] & 0xf0) >> 4)))) {
                                s += bcd2Str((byte) ((buf[4] & 0xf0) >> 4));
                            }
                            s += bcd2Str((byte) (buf[4] & 0x0f));

                            s += getFuelUnit();

                        }

                        setPreference("tripa1", s);
                        break;

                    case 0x4:

                        index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));
                        if (index == 0xffff) {
                            s = "----";
                        } else {
                            s = index + getSpeedUnit();
                        }

                        setPreference("tripa2", s);
                        break;
                    case 0x5:

                        index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8) | ((buf[3] & 0xff) << 16));
                        if (index == 0xffffff) {
                            s = "----";
                        } else {
                            s = String.format("%d.%d ", index / 10, (index % 10));
                            s += getDistanceUnit();
                        }
                        setPreference("tripa3", s);
                        break;
                    case 0x6:
                        index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));
                        s = String.format("%d h:%d m ", index, (buf[5] & 0xff));

                        setPreference("tripa4", s);
                        break;
                    case 0x7:
                        index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));
                        if (index == 0xffff) {
                            s = "----";
                        } else {
                            if (!"0".equals(bcd2Str((byte) ((buf[3] & 0xf0) >> 4)))) {
                                s = bcd2Str((byte) ((buf[3] & 0xf0) >> 4));
                            } else {
                                s = "";
                            }
                            s += bcd2Str((byte) (buf[3] & 0x0f)) + ".";
                            if (!"0".equals(bcd2Str((byte) ((buf[4] & 0xf0) >> 4)))) {
                                s += bcd2Str((byte) ((buf[4] & 0xf0) >> 4));
                            }
                            s += bcd2Str((byte) (buf[4] & 0x0f));

                            s += getFuelUnit();
                        }

                        setPreference("tripb1", s);
                        break;
                    case 0x8:

                        index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));
                        if (index == 0xffff) {
                            s = "----";
                        } else {
                            s = index + getSpeedUnit();
                        }

                        setPreference("tripb2", s);
                        break;
                    case 0x9:

                        index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8) | ((buf[3] & 0xff) << 16));
                        if (index == 0xffffff) {
                            s = "----";
                        } else {
                            s = String.format("%d.%d ", index / 10, (index % 10));
                            s += getDistanceUnit();
                        }
                        setPreference("tripb3", s);
                        break;
                    case 0xa:
                        index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));
                        s = String.format("%d h:%d m ", index, (buf[5] & 0xff));

                        setPreference("tripb4", s);
                        break;
                }

            }
            break;
            case 0x40:
                if (mUnit != buf[3]) {
                    mUnit = buf[3];
                    requestInitData();
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
