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

public class RAMFiatSettingSimpleFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "Golf7SettingsSimpleFragment";

    private int mType = 0;

    public void setType(int t) {
        mType = t;
        // if (mType == 1) {
        // PreferenceScreen p = (PreferenceScreen)
        // findPreference("driving_mode");
        // if (p != null) {
        // setPreferenceScreen(p);
        // }
        // }
    }

    private static final Node[] NODES = {

            new Node("langauage", 0xc600, 0x40000000, 0xff),

            new Node("unit", 0xc601, 0x40010000, 0x01), new Node("oil_unit", 0xc605, 0x40010000, 0x06), new Node("mileage_unit", 0xc603, 0x40010000, 0x08), new Node("temp", 0xc604, 0x40010000, 0x10),


            new Node("fuel_saver", 0xc602, 0x40020000, 0x01), new Node("dis_trip_b", 0xc606, 0x40030000, 0x01), new Node("hill_start", 0xc610, 0x40100000, 0x01),


            new Node("lights_wipers", 0xc622, 0x40200000, 0x80), new Node("flash_lights", 0xc623, 0x40200000, 0x8000), new Node("headlight_off", 0xc620, 0x40200000, 0x7f), new Node("illuminated", 0xc621, 0x40200000, 0x7f00), new Node("daytime", 0xc624, 0x40200000, 0x10000),


            new Node("auto_door", 0xc630, 0x40300000, 0x1), new Node("auto_unlock_on", 0xc631, 0x40300000, 0x2), new Node("door_flash_lights", 0xc632, 0x40300000, 0x4), new Node("horn_lock", 0xc633, 0x40300000, 0x8), new Node("remote_door", 0xc634, 0x40300000, 0x100), new Node("autoclose", 0xc635, 0x40300000, 0x200),


            new Node("headlight_off1", 0xc640, 0x40400000, 0xff), new Node("engine_power", 0xc641, 0x40400000, 0xff00),


            new Node("show_time", 0xc670, 0x40700000, 0x1), new Node("sync_time", 0xc671, 0x40700000, 0x2),


            new Node("trailer_m", 0xc650, 0x40500000, 0xf0), new Node("trailer_type", 0xc651, 0x40500000, 0xf),};

    private final static int[] INIT_CMDS = {0x4000, 0x4001, 0x4002, 0x4003, 0x4010, 0x4020, 0x4030, 0x4040, 0x4050, 0x4070,

    };

    private Preference[] mPreferences = new Preference[NODES.length];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ram_fiat_settings);

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
        //		mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 100));
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
                } else if (preference instanceof PreferenceScreen) {
                    sendCanboxData(NODES[i].mCmd);
                }
                break;
            }
        }
    }

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
        if (buf.length > 3) {
            value = ((buf[3] & 0xff) << 0);
        }
        if (buf.length > 4) {
            value |= ((buf[4] & 0xff) << 8);
        }
        if (buf.length > 5) {
            value |= ((buf[5] & 0xff) << 16);
        }
        if (buf.length > 6) {
            value |= ((buf[6] & 0xff) << 24);
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


        int cmd;
        int param;
        int mask;
        int value;

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
                    value = getStatusValue1(buf[2], mask);
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
                        //						Log.d("dd", "" + b);
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
