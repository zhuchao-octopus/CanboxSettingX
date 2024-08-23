package com.canboxsetting.set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.Node;
import com.common.util.SystemConfig;

public class JeepSettingsSimpleFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "HondaSettingsSimpleFragment";

    private int mType = 0;

    public void setType(int t) {
        mType = t;
    }

    private static final Node[] NODES = {


            new Node("parksense", 0xC6A0, 0x40A00000, 0x1, 0x0), new Node("f_parksense", 0xC6A1, 0x40A00000, 0x300, 0x0), new Node("b_parksense", 0xC6A2, 0x40A00000, 0xc00, 0x0),


            new Node("backview", 0xC6AD, 0x40A00000, 0x8000, 0x0), new Node("parkView", 0xC6A4, 0x40A00000, 0x4, 0x0),

            new Node("wipers_induction", 0xC6Af, 0x40A00000, 0x40000, 0x0), new Node("ramp", 0xC6A6, 0x40A00000, 0x10, 0x0), new Node("image_parkView", 0xC6A5, 0x40A00000, 0x8, 0x0),


            new Node("radar_parking", 0xC6f0, 0x40A00000, 0x80000, 0x0), new Node("parking_brake", 0xC6f1, 0x40A00000, 0x100000, 0x0),


            new Node("lane_warning", 0xC6A9, 0x40A00000, 0xc00000, 0x0), new Node("deviation_correction", 0xC6Ae, 0x40A00000, 0x30000, 0x0),


            new Node("busy_warning", 0xC6Ac, 0x40A00000, 0xc0, 0x0), new Node("for_outo_warning", 0xC6Ab, 0x40A00000, 0x2000, 0x0),


            new Node("for_warning", 0xC6Aa, 0x40A00000, 0x1000, 0x0), new Node("rear_parkSense", 0xC6A3, 0x40A00000, 0x2, 0x0),


            //		new Node("image_parkView", 0xC6A5, 0x40A00000, 0x8, 0x0),

            new Node("headlights_off", 0xC620, 0x40200000, 0x7f, 0x0), new Node("bright_headlights", 0xC621, 0x40200000, 0x7f00, 0x0),

            new Node("wipers_start", 0xC626, 0x40200000, 0x40000, 0x0), new Node("running_lights", 0xC624, 0x40200000, 0x10000, 0x0), new Node("lights_flash", 0xC623, 0x40200000, 0x8000, 0x0), new Node("outhigh_beam", 0xC628, 0x40200000, 0x100000, 0x0),

            new Node("welcome_light", 0xC62a, 0x40200000, 0x800000, 0x0), new Node("turn_lights_set", 0xC625, 0x40200000, 0x20000, 0x0), new Node("rearview_dimming", 0xC627, 0x40200000, 0x80000, 0x0), new Node("rearview_auto_folding", 0xC69d, 0x40a00000, 0x200000, 0x0), new Node("high_beam_control", 0xC62c, 0x40200000, 0x80, 0x0), new Node("front_light", 0xC629, 0x40200000, 0x600000, 0x0),

            //		new Node("headlights_off", 0xC620, 0x40200000, 0x7f, 0x0),

            new Node("driving_auto", 0xC630, 0x40300000, 0x1, 0x0), new Node("unlock_driving", 0xC631, 0x40300000, 0x2, 0x0), new Node("door_lights_flash", 0xC632, 0x40300000, 0x4, 0x0), new Node("beep_lock", 0xC637, 0x40300000, 0x18, 0x0),


            new Node("key_unlock", 0xC634, 0x40300000, 0x100, 0x0), new Node("keyless_entry", 0xC636, 0x40300000, 0x400, 0x0),

            new Node("personalise", 0xC638, 0x40300000, 0x20, 0x0), new Node("door_alarm", 0xC63a, 0x40300000, 0x80, 0x0), new Node("remote_lock", 0xC63b, 0x40300000, 0x10000, 0x0),


            new Node("remote_unlock", 0xC63c, 0x40300000, 0x20000, 0x0),


            //		new Node("remote_unlock", 0xC63c, 0x40200000, 0x20, 0x0),


            new Node("tail_headlights_off", 0xC640, 0x40400000, 0xff, 0x0), new Node("seat", 0xC642, 0x40400000, 0x10000, 0x0), new Node("power_off", 0xC641, 0x40400000, 0xff00, 0x0),


            //		new Node("power_off", 0xC641, 0x40400000, 0x10000, 0x0),


            new Node("auto_adjustment", 0xC6d0, 0x40d00000, 0x1, 0x0), new Node("tire_mode", 0xC6d1, 0x40d00000, 0x2, 0x0), new Node("transport_mode", 0xC6d2, 0x40d00000, 0x4, 0x0), new Node("wheel_mode", 0xC6d3, 0x40d00000, 0x8, 0x0), new Node("dis_suspension", 0xC6d4, 0x40d00000, 0x10, 0x0),


            new Node("unit_set", 0xC601, 0x40010000, 0x1, 0x0),

            new Node("outseat_heating", 0xC690, 0x40900000, 0x3, 0x0), new Node("fulecons", 0xC605, 0x40010000, 0x6, 0x0), new Node("tireunit", 0xC607, 0x40010000, 0x60, 0x0), new Node("range", 0xC603, 0x40010000, 0x8, 0x0),
            //		new Node("temperature", 0xC604, 0x40010000, 0x10, 0x0),

            new Node("auto_parking", 0xC6c1, 0x40c00000, 0x2, 0x0),


            new Node("car_type", 0xca01, 0x0, 0x0, 0x0),
            //		new Node("wheel_mode", 0xC6c0, 0x40c00000, 0x10000, 0x0)


            new Node("langauage5", 0xC600, 0x40000000, 0xff, 0x0),


    };

    private final static int[] INIT_CMDS = {0x40ff,};

    private Preference[] mPreferences = new Preference[NODES.length];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.jeep_simple_setting);

        for (int i = 0; i < NODES.length; ++i) {
            mPreferences[i] = findPreference(NODES[i].mKey);
            if (mPreferences[i] != null) {
                if (mPreferences[i] instanceof PreferenceScreen) {
                    mPreferences[i].setOnPreferenceClickListener(this);
                } else {
                    mPreferences[i].setOnPreferenceChangeListener(this);
                }
            }
        }

        mTempDis = (ListPreference) findPreference("temperature");
        mTempDis.setOnPreferenceChangeListener(this);

        // findPreference("speeddata").setOnPreferenceClickListener(this);
        // for (Node node : NODES) {
        // String s = node.mKey;
        // if (findPreference(s) != null) {
        // findPreference(s).setOnPreferenceChangeListener(this);
        // }
        // }

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

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

        if (mType == 1) {
            PreferenceScreen p = (PreferenceScreen) findPreference("driving_mode");
            if (p != null) {
                setPreferenceScreen(p);
            }
        }
        updateTempUnit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mType = 0;
    }

    private void requestInitData() {
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 500));
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                sendCanboxInfo(0x90, (msg.what & 0xff00) >> 8, msg.what & 0xff);
            }
        }
    };

    private void sendCanboxData(int cmd, int value) {
        sendCanboxInfo(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);

    }

    private void sendCanboxData(int cmd) {
        sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0));

    }

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {


                    if (key.equals("car_type")) {
                        ((ListPreference) preference).setValue((String) newValue);
                        ((ListPreference) preference).setSummary(((ListPreference) preference).getEntry());

                        sendCanboxInfo(0xca, Integer.parseInt((String) newValue));
                    } else {
                        sendCanboxData(NODES[i].mCmd, Integer.parseInt((String) newValue));
                    }
                } else if (preference instanceof SwitchPreference) {
                    if (NODES[i].mType == Node.TYPE_CUSTOM) {
                        sendCanboxData(NODES[i].mCmd, NODES[i].mStatus);
                    } else {
                        sendCanboxData(NODES[i].mCmd, ((Boolean) newValue) ? 0x1 : 0x0);
                    }

                    if (key.equals("ctm_system")) {
                        mSetCTM = (((Boolean) newValue) ? 0x1 : 0x0);
                    }


                } else if (preference instanceof PreferenceScreen) {
                    sendCanboxData(NODES[i].mCmd);
                }
                break;
            }
        }
    }

    private int mSetCTM = -1;

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        try {
            if ("temperature".equals(preference.getKey())) {
                setTempUnit((String) newValue);
            } else {
                udpatePreferenceValue(preference, newValue);
            }
        } catch (Exception e) {

        }
        return false;
    }

    public boolean onPreferenceClick(Preference arg0) {
        if (arg0.getKey().equals("individual_reset")) {
            sendCanboxInfo(0xc6, 0xd4, 0x01);
        } else if (arg0.getKey().equals("speeddata")) {
            // sendCanboxInfo(0xc6, 0xd4, 0x01);
        } else {
            try {
                udpatePreferenceValue(arg0, null);
            } catch (Exception e) {

            }
        }
        return false;
    }

    private void sendCanboxInfo(int d0, int d1) {
        byte[] buf = new byte[]{(byte) d0, 0x01, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{(byte) d0, 0x02, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void setPreference(String key, int index) {
        Preference p = findPreference(key);
        if (p != null) {
            if (p instanceof ListPreference) {
                ListPreference lp = (ListPreference) p;
                if (key.equals("front_light")) {
                    index++;
                }

                CharSequence[] ss = lp.getEntries();
                if (ss != null && (ss.length > index)) {
                    lp.setValue(String.valueOf(index));
                }
                lp.setSummary("%s");

                // Log.d("aa", key+":"+((ListPreference)
                // findPreference(key)).getEntry());
            } else if (p instanceof SwitchPreference) {
                SwitchPreference sp = (SwitchPreference) p;
                sp.setChecked(index == 0 ? false : true);
            }
        }
    }

    private int getStatusValue(byte[] buf, int mask) {

        int value = 0;
        int start = 0;
        int i;
        for (i = 0; i < 32; i++) {
            if ((mask & (0x1 << i)) != 0) {
                start = i;
                break;
            }
        }

        value = 0;
        if (buf.length > 3) {
            value = ((buf[3] & 0xff) << 0);
        }
        if (buf.length > 4) {
            value |= ((buf[4] & 0xff) << 8);
        }
        if (buf.length > 5) {
            value |= ((buf[5] & 0xff) << 16);
        }


        return ((value & mask) >> start);
    }

    private int getStatusValue1(int value, int mask) {

        int start = 0;
        int i;
        for (i = 0; i < 32; i++) {
            if ((mask & (0x1 << i)) != 0) {
                start = i;
                break;
            }
        }

        // } catch (Exception e) {
        // value = 0;
        // }

        return ((value & mask) >> start);
    }

    private void updateView(byte[] buf) {

        //		if ((buf[0] & 0xff) == 0xd0) {
        //		} else

        if ((buf[0] & 0xff) == 0x40) {

            int cmd;
            int mask;
            int value;
            int param;

            for (int i = 0; i < NODES.length; ++i) {
                cmd = (NODES[i].mStatus & 0xff000000) >> 24;
                param = (NODES[i].mStatus & 0xff0000) >> 16;

                if (NODES[i].mType == Node.TYPE_DEF_BUFF) {
                    if ((buf[0] & 0xff) == cmd && (buf[2] & 0xff) == param) {
                        mask = (NODES[i].mMask);
                        value = getStatusValue(buf, mask);
                        setPreference(NODES[i].mKey, value);
                        // break;
                    }
                } else if (NODES[i].mType == Node.TYPE_BUFF1) {
                    if ((buf[0] & 0xff) == cmd) {
                        mask = (NODES[i].mMask);
                        value = getStatusValue1(buf[6], mask);
                        setPreference(NODES[i].mKey, value);
                        // break;
                    }
                } else if (NODES[i].mType == Node.TYPE_BUFF1_INDEX) {
                    if ((buf[0] & 0xff) == cmd) {
                        mask = (NODES[i].mMask & 0xff);
                        int index = ((NODES[i].mMask & 0xff00) >> 8);
                        value = getStatusValue1(buf[2 + index], mask);
                        setPreference(NODES[i].mKey, value);
                        // break;
                    }
                } else if (NODES[i].mType == Node.TYPE_DEFINE1) {

                }

            }
        }

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

    }

    private void showPreference(String id, int show) {
        showPreference(id, show, "driving_mode");

    }


    private ListPreference mTempDis;

    private void setTempUnit(String value) {

        if (mTempDis == null) {
            return;
        }

        Log.d("fkc", "setTempUnit:" + value);
        SystemConfig.setProperty(getActivity(), SystemConfig.CANBOX_TEMP_UNIT, value);

        Intent it = new Intent(MyCmd.BROADCAST_MACHINECONFIG_UPDATE);
        it.putExtra(MyCmd.EXTRA_COMMON_CMD, SystemConfig.CANBOX_TEMP_UNIT);
        it.putExtra(MyCmd.EXTRA_COMMON_DATA, value);
        getActivity().sendBroadcast(it);

        mTempDis.setValue(value);
        mTempDis.setSummary(mTempDis.getEntry());

    }

    private void updateTempUnit() {
        if (mTempDis == null) {
            return;
        }
        String value = SystemConfig.getProperty(getActivity(), SystemConfig.CANBOX_TEMP_UNIT);

        if (value == null) {
            value = "0";
        }
        mTempDis.setValue(value);
        mTempDis.setSummary(mTempDis.getEntry());
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
                                Log.d("aa", "!!!!!!!!" + e);
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
