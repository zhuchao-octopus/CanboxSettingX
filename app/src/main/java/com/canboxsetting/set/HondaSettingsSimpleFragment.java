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
import com.common.utils.Node;

public class HondaSettingsSimpleFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "HondaSettingsSimpleFragment";

    private int mType = 0;

    public void setType(int t) {
        mType = t;
    }

    private static final Node[] NODES = {

            new Node("ctm_system", 0xC660, 0, 0),

            new Node("adjust_outside", 0xC600, 0x32000000, 0x000f, 0x0, Node.TYPE_BUFF1_INDEX), new Node("trip_a", 0xC602, 0x32000000, 0x0030, 0x0, Node.TYPE_BUFF1_INDEX), new Node("trip_b", 0xC603, 0x32000000, 0x00c0, 0x0, Node.TYPE_BUFF1_INDEX),

            new Node("interior", 0xC604, 0x32000000, 0x0103, 0x0, Node.TYPE_BUFF1_INDEX), new Node("headlight", 0xC605, 0x32000000, 0x010c, 0x0, Node.TYPE_BUFF1_INDEX), new Node("auto_light", 0xC606, 0x32000000, 0x0170, 0x0, Node.TYPE_BUFF1_INDEX),

            new Node("lock_with", 0xC607, 0x32000000, 0x0203, 0x0, Node.TYPE_BUFF1_INDEX), new Node("unlock_with", 0xC608, 0x32000000, 0x020c, 0x0, Node.TYPE_BUFF1_INDEX), new Node("key_mode", 0xC609, 0x32000000, 0x0240, 0x0, Node.TYPE_BUFF1_INDEX), new Node("keyless", 0xC60a, 0x32000000, 0x0280, 0x0, Node.TYPE_BUFF1_INDEX), new Node("security", 0xC60b, 0x32000000, 0x0230, 0x0, Node.TYPE_BUFF1_INDEX),

            new Node("access_beep", 0xC60d, 0x32000000, 0x0340, 0x0, Node.TYPE_BUFF1_INDEX),

            new Node("tpms_check", 0xC61100), new Node("default_all", 0xC60f00),

            new Node("alarm_vol", 0xC612, 0x32000000, 0x04c0, 0x0, Node.TYPE_BUFF1_INDEX), new Node("backlight", 0xC613, 0x32000000, 0x0420, 0x0, Node.TYPE_BUFF1_INDEX), new Node("notifications", 0xC614, 0x32000000, 0x0410, 0x0410, Node.TYPE_BUFF1_INDEX), new Node("speed_distance_units", 0xC615, 0x32000000, 0x0408, 0x0, Node.TYPE_BUFF1_INDEX), new Node("tachometer", 0xC616, 0x32000000, 0x0404, 0x0, Node.TYPE_BUFF1_INDEX), new Node("walk_away_auto_lock", 0xC617, 0x32000000, 0x0402, 0x0, Node.TYPE_BUFF1_INDEX),

            new Node("remote_startoff", 0xC618, 0x32000000, 0x0320, 0x0, Node.TYPE_BUFF1_INDEX), new Node("door_unlock_mode", 0xC619, 0x32000000, 0x0310, 0x0, Node.TYPE_BUFF1_INDEX), new Node("keyless_access_light", 0xC61a, 0x32000000, 0x0308, 0x0, Node.TYPE_BUFF1_INDEX), new Node("illumination", 0xC61b, 0x32000000, 0x0307, 0x0, Node.TYPE_BUFF1_INDEX),

            new Node("headlight_wiper", 0xC61c, 0x32000000, 0x0401, 0x0, Node.TYPE_BUFF1_INDEX),

            new Node("start_stop_dis", 0xC61d, 0x32000000, 0x0540, 0x0, Node.TYPE_BUFF1_INDEX), new Node("voice_alarm", 0xC61e, 0x32000000, 0x0580, 0x0, Node.TYPE_BUFF1_INDEX), new Node("danger_ahead", 0xC61f, 0x32000000, 0x050c, 0x0, Node.TYPE_BUFF1_INDEX), new Node("acc_car", 0xC620, 0x32000000, 0x0520, 0x0, Node.TYPE_BUFF1_INDEX), new Node("stop_lkas", 0xC621, 0x32000000, 0x0510, 0x0, Node.TYPE_BUFF1_INDEX), new Node("deviate", 0xC622, 0x32000000, 0x0503, 0x0, Node.TYPE_BUFF1_INDEX),

            new Node("tachometer_settings", 0xC623, 0x32000000, 0x0680, 0x0, Node.TYPE_BUFF1_INDEX),

            new Node("driver_attention_monitor", 0xC624, 0x32000000, 0x090c, 0x0, Node.TYPE_BUFF1_INDEX), new Node("the_handle_electrically", 0xC626, 0x32000000, 0x0902, 0x0, Node.TYPE_BUFF1_INDEX), new Node("remote_control_open_condition", 0xC625, 0x32000000, 0x0901, 0x0, Node.TYPE_BUFF1_INDEX),

            new Node("reset_info", 0xC60e00),

    };

    private final static int[] INIT_CMDS = {0x3200,};

    private Preference[] mPreferences = new Preference[NODES.length];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.honda_simple_setting);

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
                    sendCanboxData(NODES[i].mCmd, Integer.parseInt((String) newValue));
                } else if (preference instanceof SwitchPreference) {
                    if (NODES[i].mType == Node.TYPE_CUSTOM) {
                        sendCanboxData(NODES[i].mCmd, NODES[i].mStatus);
                    } else {
                        sendCanboxData(NODES[i].mCmd, ((Boolean) newValue) ? 0x1 : 0x0);
                    }

                    if (key.equals("ctm_system")) {
                        mSetCTM = (((Boolean) newValue) ? 0x1 : 0x0);
                        ((SwitchPreference) preference).setChecked((Boolean) newValue);
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
            udpatePreferenceValue(preference, newValue);
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
        if (buf.length > 2) {
            value = ((buf[2] & 0xff) << 0);
        }
        if (buf.length > 3) {
            value |= ((buf[3] & 0xff) << 8);
        }
        if (buf.length > 4) {
            value |= ((buf[4] & 0xff) << 16);
        }
        if (buf.length > 5) {
            value |= ((buf[5] & 0xff) << 24);
        }

        // } catch (Exception e) {
        // value = 0;
        // }

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

        if ((buf[0] & 0xff) == 0xd0) {
            if (buf[2] == 0x60/* && buf[3] == 0 */) {// bad...
                if (mSetCTM == 1) {
                    ((SwitchPreference) findPreference("ctm_system")).setChecked(true);
                } else {
                    ((SwitchPreference) findPreference("ctm_system")).setChecked(false);
                }
            }
            return;
        } else if ((buf[0] & 0xff) == 0x32) {

            int cmd;
            int mask;
            int value;

            for (int i = 0; i < NODES.length; ++i) {
                cmd = (NODES[i].mStatus & 0xff000000) >> 24;
                // param = (NODES[i].mStatus & 0xff0000) >> 16;

                if (NODES[i].mType == Node.TYPE_DEF_BUFF) {
                    if ((buf[0] & 0xff) == cmd) {
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

                    // new Node("speedunits1", 0x0, 0x40200000, 0x2,
                    // 0x0,Node.TYPE_DEFINE1),
                    // new Node("speeddata", 0x0, 0x40200000, 0xff00,
                    // 0x0,Node.TYPE_DEFINE1),

                    if (NODES[i].mKey.equals("speedunits1")) {
                        if (findPreference("speedunits1") != null) {
                            if ((buf[3] & 0x2) == 0) {
                                findPreference("speedunits1").setSummary("km/h");
                            } else {
                                findPreference("speedunits1").setSummary("mph");
                            }
                        }
                        // mSpeedUnit = (byte)(buf[3]&0x2);
                    } else if (NODES[i].mKey.equals("speeddata")) {
                        String s;
                        if ((buf[3] & 0x2) == 0) {
                            s = " km/h";
                        } else {
                            s = " mph";
                        }
                        if (findPreference("speeddata") != null) {
                            findPreference("speeddata").setSummary(buf[4] + s);
                        }
                    }

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
