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
import com.common.utils.Node;

public class HondaInfoSimpleFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    private static final String TAG = "HondaInfoSimpleFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.honda_simple_info);

        for (int i = 0; i < NODES.length; ++i) {
            mPreferences[i] = findPreference(NODES[i].mKey);
            if (mPreferences[i] != null) {
                mPreferences[i].setOnPreferenceClickListener(this);
            }
        }

        // findPreference("tpms").setOnPreferenceClickListener(this);

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    private final static int[] INIT_CMDS = {0x3301, 0x3302};

    private void requestInitData() {
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 200));
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

    private void sendCanboxInfo0xff(int d1) {// no canbox cmd.
        byte[] buf = new byte[]{(byte) 0xff, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x90(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0x90, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    public boolean onPreferenceClick(Preference arg0) {

        try {
            String key = arg0.getKey();
            if ("delrecords".equals(key)) {
                sendCanboxInfo0x90(0x33, 0x3);
            }
        } catch (Exception e) {

        }

        return false;
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

    private static final Node[] NODES = {

            new Node("delrecords", 0x0)

    };
    private Preference[] mPreferences = new Preference[NODES.length];

    private void updateView(byte[] buf) {

        int index = 0;
        String s = "";
        String temp;

        switch (buf[0]) {
            case 0x33: {
                switch (buf[2]) {
                    case 0x1:
                        index = ((buf[3] & 0xff));
                        if (((buf[15] & 0x3) >> 0) == 0x1) {
                            s = index + "km/l";
                        } else if (((buf[15] & 0x3) >> 0) == 0x2) {
                            s = index + "l/100km";
                        } else {
                            s = index + "mpg";
                        }
                        setPreference("instant", s);

                        index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));

                        s = String.format("%d.%d ", index / 10, (index % 10));

                        if (((buf[15] & 0x3) >> 2) == 0x1) {
                            s = s + "km/l";
                        } else if (((buf[15] & 0x3) >> 0) == 0x2) {
                            s = s + "l/100km";
                        } else {
                            s = s + "mpg";
                        }
                        setPreference("curfuel", s);

                        index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));
                        s = String.format("%d.%d ", index / 10, (index % 10));
                        if (((buf[15] & 0xc) >> 2) == 0x1) {
                            s = s + "km/l";
                        } else if (((buf[15] & 0x3) >> 0) == 0x2) {
                            s = s + "l/100km";
                        } else {
                            s = s + "mpg";
                        }
                        setPreference("historyfuel", s);

                        index = ((buf[9] & 0xff) | ((buf[8] & 0xff) << 8));
                        s = String.format("%d.%d ", index / 10, (index % 10));
                        if (((buf[15] & 0x30) >> 4) == 0x1) {
                            s = s + "km/l";
                        } else if (((buf[15] & 0x3) >> 0) == 0x2) {
                            s = s + "l/100km";
                        } else {
                            s = s + "mpg";
                        }
                        setPreference("averagefuel24", s);

                        index = ((buf[12] & 0xff) | ((buf[11] & 0xff) << 8) | ((buf[10] & 0xff) << 16));
                        s = String.format("%d.%d ", index / 10, (index % 10));
                        if (((buf[15] & 0x40)) != 0) {
                            s = s + "mil";
                        } else {
                            s = s + "km";
                        }
                        setPreference("trip_a1", s);

                        index = ((buf[14] & 0xff) | ((buf[13] & 0xff) << 8));
                        if (((buf[15] & 0x80)) != 0) {
                            s = index + " mil";
                        } else {
                            s = index + " km";
                        }
                        setPreference("driving_mileage", s);
                        break;
                    case 0x2:
                        String unitTrip;
                        String unitFuel;

                        if (((buf[18] & 0x40)) != 0) {
                            unitTrip = " mil";
                        } else {
                            unitTrip = " km";
                        }

                        if (((buf[18] & 0x30) >> 4) == 0x1) {
                            unitFuel = "km/l";
                        } else if (((buf[18] & 0x30) >> 4) == 0x2) {
                            unitFuel = "l/100km";
                        } else {
                            unitFuel = "mpg";
                        }

                        index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8) | ((buf[3] & 0xff) << 16));
                        s = String.format("%d.%d ", index / 10, (index % 10));
                        s += unitTrip;
                        setPreference("the_historical_journey1", s);

                        index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));
                        s = String.format("%d.%d ", index / 10, (index % 10));
                        s += unitFuel;
                        setPreference("the_historical_journey_wasted1", s);

                        index = ((buf[10] & 0xff) | ((buf[9] & 0xff) << 8) | ((buf[8] & 0xff) << 16));
                        s = String.format("%d.%d ", index / 10, (index % 10));
                        s += unitTrip;
                        setPreference("the_historical_journey2", s);

                        index = ((buf[12] & 0xff) | ((buf[11] & 0xff) << 8));
                        s = String.format("%d.%d ", index / 10, (index % 10));
                        s += unitFuel;
                        setPreference("the_historical_journey_wasted2", s);

                        index = ((buf[15] & 0xff) | ((buf[14] & 0xff) << 8) | ((buf[13] & 0xff) << 16));
                        s = String.format("%d.%d ", index / 10, (index % 10));
                        s += unitTrip;
                        setPreference("the_historical_journey3", s);

                        index = ((buf[17] & 0xff) | ((buf[16] & 0xff) << 8));
                        s = String.format("%d.%d ", index / 10, (index % 10));
                        s += unitFuel;
                        setPreference("the_historical_journey_wasted3", s);

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
