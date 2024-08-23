package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.Node;
import com.common.view.MyPreference2;

public class ToyotaInfoSBinarytekFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    private static final String TAG = "ToyotaInfoSBinarytekFragment";
    private final static int[] INIT_CMDS = {0xff25, 0x1f00, 0x4101, 0x4102, 0x4103, 0xff21, 0xff22, 0xff23,
            /*
             * 0x4010, 0x4020, 0x4030, 0x4031, 0x4040, 0x4050, 0x4051, 0x4060, 0x4070,
             * 0x4080, 0x4090,
             */};
    private static final Node[] NODES = {

            new Node("tpms", 0x0), new Node("hybrid", 0x0),

    };
    boolean mShowHybird = false;
    private boolean mPaused = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                if ((msg.what & 0xff00) == 0xff00) {
                    sendCanboxInfo0xff(msg.what & 0xff);
                } else {

                    sendCanboxInfo0x90((msg.what & 0xff00) >> 8, msg.what & 0xff);
                }
            }
        }
    };
    private Preference[] mPreferences = new Preference[NODES.length];
    private byte mHybrid;
    private View mTpmsView;
    private View mBatteryView;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.toyota_info_binarytek);

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

    private void requestInitData() {
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 200));
        }

    }

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
            if ("tpms".equals(key)) {
                mTpmsView = null;
                sendCanboxInfo0xff(0x25);
            } else if ("hybrid".equals(key)) {
                mBatteryView = null;
                sendCanboxInfo0x90(0x1f, 0);
                // sendCanboxInfo(0xff,0x1f);
            }
        } catch (Exception e) {

        }

        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterListener();
        mPaused = true;
        mHandlerHybird.removeMessages(0);
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

    private void updateVisible(byte[] buf) {

        showPreference("cardoorspeed", buf[2] & 0x1, null);
        showPreference("cardoorautomatic", buf[2] & 0x2, null);
        showPreference("plinkage", buf[2] & 0x4, null);
        showPreference("linkagedoorlock", buf[2] & 0x8, null);
        showPreference("twokeyunlock", buf[2] & 0x10, null);
        showPreference("remoteunlock", buf[2] & 0x20, null);
        showPreference("opendoorflash", buf[2] & 0x40, null);
        showPreference("timeswitchlight", buf[2] & 0x80, null);

        // showPreference("cardoorspeed", buf[3] & 0x1, null);
        showPreference("autolight", buf[3] & 0x2, null);
        showPreference("smartdoorlock", buf[3] & 0x4, null);
        showPreference("lockakey", buf[3] & 0x8, null);

        showPreference("airautokey", buf[3] & 0x10, null);
        showPreference("airswitchautokey", buf[3] & 0x20, null);
        showPreference("back_camera_path", buf[3] & 0x40, null);
        showPreference("cell_back_door", buf[3] & 0x80, null);

    }

    private void showPreference(String id, int show, String parant) {
        Preference preference = null;

        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(id)) {
                preference = mPreferences[i];
                break;
            }
        }

        if (preference != null) {
            PreferenceScreen ps;
            if (parant != null) {
                ps = (PreferenceScreen) findPreference(parant);
            } else {
                ps = getPreferenceScreen();
            }
            if (ps != null) {
                if (show != 0) {
                    if (ps.findPreference(id) == null) {
                        ps.addPreference(preference);
                    }
                } else {
                    if (findPreference(id) != null) {
                        boolean b = ps.removePreference(preference);
                        // Log.d("dd", "" + b);
                    }
                }
            }
        }

    }    private Handler mHandlerHybird = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            mHandlerHybird.removeMessages(0);
            if (mShowHybird) {
                clearHybird();
                mShowHybird = false;
                mHandlerHybird.sendEmptyMessageDelayed(0, 1000);
            } else {
                showHyBird(mHybrid);
            }
        }
    };

    private void clearHybird() {
        mBatteryView.findViewById(R.id.canbus8_wheel_motor).setVisibility(View.GONE);

        mBatteryView.findViewById(R.id.canbus8_motor_wheel).setVisibility(View.GONE);
        mBatteryView.findViewById(R.id.canbus8_battery_motor).setVisibility(View.GONE);
        mBatteryView.findViewById(R.id.canbus8_motor_battery).setVisibility(View.GONE);
        mBatteryView.findViewById(R.id.canbus8_arrow_set).setVisibility(View.GONE);
        mBatteryView.findViewById(R.id.canbus8_img_lf2_1).setVisibility(View.GONE);
    }

    private void showHyBird(byte b) {
        mHybrid = b;

        mHandlerHybird.removeMessages(0);
        if (mHybrid != 0) {
            mShowHybird = true;
            mHandlerHybird.sendEmptyMessageDelayed(0, 1000);
        }
        if ((mHybrid & 0x20) == 0) {
            mBatteryView.findViewById(R.id.canbus8_wheel_motor).setVisibility(View.GONE);

            if ((mHybrid & 0x2) == 0) {
                mBatteryView.findViewById(R.id.canbus8_motor_wheel).setVisibility(View.GONE);
            } else {

                mBatteryView.findViewById(R.id.canbus8_motor_wheel).setVisibility(View.VISIBLE);
            }

        } else {

            mBatteryView.findViewById(R.id.canbus8_wheel_motor).setVisibility(View.VISIBLE);

            mBatteryView.findViewById(R.id.canbus8_motor_wheel).setVisibility(View.GONE);
        }

        if ((mHybrid & 0x10) == 0) {
            mBatteryView.findViewById(R.id.canbus8_battery_motor).setVisibility(View.GONE);

            if ((mHybrid & 0x1) == 0) {
                mBatteryView.findViewById(R.id.canbus8_motor_battery).setVisibility(View.GONE);
            } else {

                mBatteryView.findViewById(R.id.canbus8_motor_battery).setVisibility(View.VISIBLE);
            }

        } else {

            mBatteryView.findViewById(R.id.canbus8_battery_motor).setVisibility(View.VISIBLE);

            mBatteryView.findViewById(R.id.canbus8_motor_battery).setVisibility(View.GONE);
        }

        if ((mHybrid & 0x4) == 0) {
            mBatteryView.findViewById(R.id.canbus8_arrow_set).setVisibility(View.GONE);
        } else {

            mBatteryView.findViewById(R.id.canbus8_arrow_set).setVisibility(View.VISIBLE);
        }

        if ((mHybrid & 0x8) == 0) {
            mBatteryView.findViewById(R.id.canbus8_img_lf2_1).setVisibility(View.GONE);
        } else {

            mBatteryView.findViewById(R.id.canbus8_img_lf2_1).setVisibility(View.VISIBLE);
        }
    }

    private void updateView(byte[] buf) {

        int index = 0;
        String s = "";
        String temp;

        switch (buf[0]) {
            case 0x16: {
                index = (((buf[2] & 0xff) | ((buf[3] & 0xff) << 8)) / 16);
                s = String.format("%d km/h", index);
                setPreference("am_runningspeed", s);
            }
            break;
            case 0x50: {

                index = ((buf[2] & 0xff) | ((buf[3] & 0xff) << 8));
                s = String.format("%d RPM", index);
                setPreference("engine_speed55", s);
            }
            break;

            case 0x23: {
                String unit = "";
                if (buf[2] == 0x1) {
                    unit = "KM/L";
                } else if (buf[2] == 0x2) {
                    unit = "L/100KM";
                } else {
                    unit = "MPG";
                }

                index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));
                s = String.format("%d.%d %s", index / 10, index % 10, unit);
                setPreference("historyfuel", s);
            }
            break;
            case 0x22: {
                String unit = "";
                if (buf[2] == 0x1) {
                    unit = "KM/L";
                } else if (buf[2] == 0x2) {
                    unit = "L/100KM";
                } else {
                    unit = "MPG";
                }

                index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));
                s = String.format("%d.%d %s", index / 10, index % 10, unit);
                setPreference("instant", s);
            }
            break;
            case 0x21:
                if (buf[8] == 0x0) {
                    s = "--";

                    setPreference("averagespeed", s);
                    setPreference("traveltime", s);
                    setPreference("mileage", s);
                } else {
                    String unit = "";
                    if (buf[8] == 0x1) {
                        unit = "MILE";
                    } else if (buf[8] == 0x2) {
                        unit = "KM";
                    }

                    index = ((buf[3] & 0xff) | ((buf[2] & 0xff) << 8));
                    s = String.format("%d.%d %s/H", index / 10, index % 10, unit);
                    setPreference("averagespeed", s);

                    index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));
                    s = String.format("%02d:%02d", index / 60, index % 60);
                    setPreference("traveltime", s);

                    index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));
                    s = String.format("%d %s", index, unit);
                    setPreference("mileage", s);
                }

                break;
            case 0x1f:
                if ((buf[2] & 0x80) == 0) {
                    showPreference("hybrid", 0, null);
                    break;
                } else {
                    showPreference("hybrid", 1, null);
                }

                if (mBatteryView == null) {
                    MyPreference2 p = (MyPreference2) findPreference("hybrid_content");
                    if (p != null) {
                        mBatteryView = p.getMainView();
                    }
                }

                if (mBatteryView == null) {
                    break;
                }

                showHyBird(buf[3]);

                int level = (buf[2] & 0xf);
                Drawable d;

                d = mBatteryView.findViewById(R.id.battery).getBackground();
                d.setLevel(level);

                break;
            case 0x25:

                if ((buf[2] & 0x80) == 0) {
                    showPreference("tpms", 0, null);
                    break;
                } else {
                    showPreference("tpms", 1, null);
                }

                if (mTpmsView == null) {
                    MyPreference2 p = (MyPreference2) findPreference("tpms_content");
                    if (p != null) {
                        mTpmsView = p.getMainView();
                    }
                }

                if (mTpmsView == null) {
                    break;
                }

                if ((buf[2] & 0x4) == 0) {
                    mTpmsView.findViewById(R.id.type1).setVisibility(View.GONE);
                    mTpmsView.findViewById(R.id.type2).setVisibility(View.VISIBLE);
                } else {

                    mTpmsView.findViewById(R.id.type1).setVisibility(View.VISIBLE);
                    mTpmsView.findViewById(R.id.type2).setVisibility(View.GONE);
                }

                String unit;
                String t1;
                String t2;
                String t3;
                String t4;
                String t5;
                int tpms;
                if ((buf[2] & 0x3) == 1) {
                    unit = " PSI";

                    t1 = (buf[3] & 0xff) + unit;
                    t2 = (buf[4] & 0xff) + unit;
                    t3 = (buf[5] & 0xff) + unit;
                    t4 = (buf[6] & 0xff) + unit;
                    t5 = (buf[7] & 0xff) + unit;

                } else if ((buf[2] & 0x3) == 2) {
                    unit = " KPA";

                    tpms = 25 * (buf[3] & 0xff);
                    t1 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));

                    tpms = 25 * (buf[4] & 0xff);
                    t2 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));
                    tpms = 25 * (buf[5] & 0xff);
                    t3 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));
                    tpms = 25 * (buf[6] & 0xff);
                    t4 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));
                    tpms = 25 * (buf[7] & 0xff);
                    t5 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));

                } else {
                    unit = " BAR";

                    tpms = (buf[3] & 0xff);
                    t1 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));
                    tpms = (buf[4] & 0xff);
                    t2 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));
                    tpms = (buf[5] & 0xff);
                    t3 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));
                    tpms = (buf[6] & 0xff);
                    t4 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));
                    tpms = (buf[7] & 0xff);
                    t5 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));

                }

                if (buf[3] == 0xff) {
                    t1 = "--";
                }
                if (buf[4] == 0xff) {
                    t2 = "--";
                }
                if (buf[5] == 0xff) {
                    t3 = "--";
                }
                if (buf[6] == 0xff) {
                    t4 = "--";
                }

                if (buf[7] == 0xff || ((buf[2] & 0x20) == 0)) {
                    t5 = "--";
                }

                ((TextView) mTpmsView.findViewById(R.id.type11_info)).setText(t1);
                ((TextView) mTpmsView.findViewById(R.id.type12_info)).setText(t2);
                ((TextView) mTpmsView.findViewById(R.id.type21_info)).setText(t3);
                ((TextView) mTpmsView.findViewById(R.id.type22_info)).setText(t4);

                ((TextView) mTpmsView.findViewById(R.id.type30_info)).setText(t5);

                ((TextView) mTpmsView.findViewById(R.id.type91_info)).setText(t1);
                ((TextView) mTpmsView.findViewById(R.id.type92_info)).setText(t2);
                ((TextView) mTpmsView.findViewById(R.id.type93_info)).setText(t3);
                ((TextView) mTpmsView.findViewById(R.id.type94_info)).setText(t4);

                ((TextView) mTpmsView.findViewById(R.id.type95_info)).setText(t5);

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
