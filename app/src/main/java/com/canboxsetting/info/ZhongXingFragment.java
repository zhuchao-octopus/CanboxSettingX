package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.Util;
import com.zhuchao.android.fbase.MMLog;

import java.util.Objects;

public class ZhongXingFragment extends PreferenceFragmentCompat {
    private static final String TAG = "Golf7InfoSimpleFragment";
    private boolean mPause = false;
    private Handler mHandler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(Message msg) {
            // mHandler.removeMessages(msg.what);
            // mHandler.sendEmptyMessageDelayed(msg.what, 700);
            if (!mPause) {
                sendCanboxInfo(0x90, (msg.what & 0xff00) >> 8, msg.what & 0xff);
            }
        }
    };
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.zhongxing_od);

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    @Override
    public void onPause() {
        mPause = false;
        super.onPause();
        unregisterListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListener();

        // sendCanboxInfo(0x50, 0x1A);

        mHandler.sendEmptyMessageDelayed(0x3800, 0);
        mPause = false;
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{(byte) d0, 0x02, (byte) d1, (byte) d2};
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
            case 0x16:
                index = ((buf[2] & 0xff) | ((buf[3] & 0xff) << 8));
                index /= 16;
                if ((buf[4] & 0x1) == 0) {
                    s = index + " km/h";
                } else {
                    s = index + " mph";
                }
                setPreference("speed", s);
                break;
            case 0x50:
                switch (buf[2]) {
                    case 0x10:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8));
                        if ((buf[3] & 0x1) == 0) {
                            s = index + " KM";
                        } else {
                            s = index + " MI";
                        }
                        setPreference("mileage", s);
                        break;
                    case 0x20:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8) | ((buf[6] & 0xff) << 16) | ((buf[7] & 0xff) << 24)) / 10;
                        if ((buf[3] & 0x1) == 0) {
                            s = index + " KM";
                        } else {
                            s = index + " MI";
                        }
                        setPreference("since_start", s);
                        break;
                    case 0x21:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8) | ((buf[6] & 0xff) << 16) | ((buf[7] & 0xff) << 24)) / 10;
                        if ((buf[3] & 0x1) == 0) {
                            s = index + " KM";
                        } else {
                            s = index + " MI";
                        }
                        setPreference("since_refueling", s);
                        break;
                    case 0x22:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8) | ((buf[6] & 0xff) << 16) | ((buf[7] & 0xff) << 24)) / 10;
                        if ((buf[3] & 0x1) == 0) {
                            s = index + " KM";
                        } else {
                            s = index + " MI";
                        }
                        setPreference("long_term", s);
                        break;

                    case 0x30:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
                        if ((buf[3] & 0x3) == 0) {
                            s = index + " L/100KM";
                        } else if ((buf[3] & 0x3) == 1) {
                            s = index + " KM/L";
                        } else if ((buf[3] & 0x3) == 1) {
                            s = index + " MPG(UK)";
                        } else {
                            s = index + " MPG(US)";
                        }
                        setPreference("avg_start", s);
                        break;
                    case 0x31:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
                        if ((buf[3] & 0x3) == 0) {
                            s = index + " L/100KM";
                        } else if ((buf[3] & 0x3) == 1) {
                            s = index + " KM/L";
                        } else if ((buf[3] & 0x3) == 1) {
                            s = index + " MPG(UK)";
                        } else {
                            s = index + " MPG(US)";
                        }
                        setPreference("avrefueling", s);
                        break;
                    case 0x32:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
                        if ((buf[3] & 0x3) == 0) {
                            s = index + " L/100KM";
                        } else if ((buf[3] & 0x3) == 1) {
                            s = index + " KM/L";
                        } else if ((buf[3] & 0x3) == 1) {
                            s = index + " MPG(UK)";
                        } else {
                            s = index + " MPG(US)";
                        }
                        setPreference("avlong_term", s);
                        break;
                    case 0x40:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
                        if ((buf[3] & 0x1) == 0) {
                            s = index + " KM/H";
                        } else {
                            s = index + " MPH";
                        }
                        setPreference("avspeed_start", s);
                        break;
                    case 0x41:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
                        if ((buf[3] & 0x1) == 0) {
                            s = index + " KM/H";
                        } else {
                            s = index + " MPH";
                        }
                        setPreference("speeds_refueling", s);
                        break;
                    case 0x42:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
                        if ((buf[3] & 0x1) == 0) {
                            s = index + " KM/H";
                        } else {
                            s = index + " MPH";
                        }
                        setPreference("speed_long", s);
                        break;
                    case 0x50:
                        index = ((buf[3] & 0xff) | ((buf[4] & 0xff) << 8) | ((buf[5] & 0xff) << 16));
                        s = index + " MIN";
                        setPreference("travelling_time", s);
                        break;
                    case 0x51:
                        index = ((buf[3] & 0xff) | ((buf[4] & 0xff) << 8) | ((buf[5] & 0xff) << 16));
                        s = index + " MIN";
                        setPreference("ttsr", s);
                        break;
                    case 0x52:
                        index = ((buf[3] & 0xff) | ((buf[4] & 0xff) << 8) | ((buf[5] & 0xff) << 16));
                        s = index + " MIN";
                        setPreference("tt_long_term", s);
                        break;

                    case 0x60:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8));
                        if ((buf[3] & 0x1) == 0) {
                            s = index + " gal/h";
                        } else {
                            s = index + " l/h";
                        }
                        setPreference("conv_consumers", s);
                        break;

                    case 0x61:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
                        if ((buf[3] & 0x3) == 0) {
                            s = index + " L/100KM";
                        } else if ((buf[3] & 0x3) == 1) {
                            s = index + " KM/L";
                        } else if ((buf[3] & 0x3) == 1) {
                            s = index + " MPG(UK)";
                        } else {
                            s = index + " MPG(US)";
                        }
                        setPreference("instant", s);
                        break;
                }

                break;

            case 0x63:

                switch (buf[2]) {
                    case 0x0:
                        byte[] name = new byte[buf.length - 4];
                        Util.byteArrayCopy(name, buf, 0, 3, name.length);
                        try {
                            s = new String(name, "GBK");
                        } catch (Exception e) {

                        }
                        setPreference("vehicle", s);
                        break;
                    case 0x10:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8));
                        if ((buf[3] & 0xf) == 0) {
                            s = "------";
                        } else if ((buf[3] & 0xf) == 1) {
                            s = index + getString(R.string.days);
                        } else {
                            s = getString(R.string.be_overdue) + index + getString(R.string.days);
                        }
                        setPreference("vi_days", s);
                        break;

                    case 0x11:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) * 100;

                        if ((buf[3] & 0xf0) == 0) {
                            s = " KM";
                        } else {
                            s = " MI";
                        }

                        if ((buf[3] & 0xf) == 0) {
                            s = "------";
                        } else if ((buf[3] & 0xf) == 1) {
                            s = index + s;
                        } else {
                            s = getString(R.string.be_overdue) + index + s;
                        }
                        setPreference("vi_distance", s);
                        break;

                    case 0x20:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8));
                        if ((buf[3] & 0xf) == 0) {
                            s = "------";
                        } else if ((buf[3] & 0xf) == 1) {
                            s = index + getString(R.string.days);
                        } else {
                            s = getString(R.string.be_overdue) + index + getString(R.string.days);
                        }
                        setPreference("oil_days", s);
                        break;

                    case 0x21:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) * 100;

                        if ((buf[3] & 0xf0) == 0) {
                            s = " KM";
                        } else {
                            s = " MI";
                        }

                        if ((buf[3] & 0xf) == 0) {
                            s = "------";
                        } else if ((buf[3] & 0xf) == 1) {
                            s = index + s;
                        } else {
                            s = getString(R.string.be_overdue) + index + s;
                        }
                        setPreference("oil_change", s);
                        break;
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
                                MMLog.d("aa", "!!!!!!!!" + buf);
                            }
                        }
                    }
                    if (action.equals("com.android.canbox.test")) {
                        byte[] buf = new byte[]{(byte) 0x81, 0x01, 0x01};
                        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
                        Log.d(TAG, "canbox startconnect");
                    }
                }
            };
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction(MyCmd.BROADCAST_SEND_FROM_CAN);
            iFilter.addAction("com.android.canbox.test");
            this.getActivity().registerReceiver(mReceiver, iFilter);
        }
    }

}
