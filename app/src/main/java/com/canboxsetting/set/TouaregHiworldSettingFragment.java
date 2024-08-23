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
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.Node;
import com.common.view.MyPreferenceEdit;
import com.common.view.MyPreferenceEdit.IButtonCallBack;

public class TouaregHiworldSettingFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "TouaregHiworldSettingFragment";
    private static final Node[] NODES = {

            new Node("wiper", 0xcc02, 0xc3, 10, Node.TYPE_BUFF1), new Node("reset", 0xcc01, 0, 0, 0),

            new Node("fvol", 0x7a01, 0x71, 0x3, Node.TYPE_BUFF1), new Node("fvol_on", 0x7a02, 0x71, 0x4, Node.TYPE_BUFF1), new Node("bvol", 0x7a03, 0x71, 0x5, Node.TYPE_BUFF1), new Node("bvol_on", 0x7a04, 0x71, 0x6, Node.TYPE_BUFF1),

            new Node("fwindow", 0x6f01, 0x64, 0x800000, 0x0), new Node("bwindow", 0x6f02, 0x64, 0x400000, 0x0), new Node("hwindow", 0x6f03, 0x64, 0x200000, 0x0),

            new Node("autolockcmd", 0x6f05, 0x64, 0x80000000, 0x0),

            new Node("rearview_mirror", 0x6f06, 0x64, 0x40000000, 0x0), new Node("trunklock", 0x6f08, 0x64, 0x10000000, 0x0), new Node("doorlock", 0x6f07, 0x64, 0x20000000, 0x0),

            new Node("auto_active", 0x7a05, 0x71, 0x8000, 0x0),

            new Node("speed_warning", 0x4b02, 0x46, 0x8000, 0x0), new Node("warning_at", 0x4b03, 0x46, 0xff0000, 0x0),

            new Node("raining", 0x4d01, 0x48, 0x8000, 0x0),

            new Node("incar_light", 0x6d11, 0x68, 0x6, Node.TYPE_BUFF1),

            new Node("gohome", 0x6d0d, 0x68, 0xc000000, 0), new Node("leavehome", 0x6d0e, 0x68, 0x3000000, 0),

            new Node("foot", 0x6d0e, 0x68, 0x5, Node.TYPE_BUFF1),

            new Node("car_light", 0x6d10, 0x68, 0x10000000, 0), new Node("intelligent_driving", 0x6d12, 0x68, 0x20000000, 0), new Node("left_drive", 0x6d13, 0x68, 0x40000000, 0),

            new Node("range", 0xca01, 0xc1, 0x8000, Node.TYPE_BUFF2), new Node("speed", 0xca02, 0xc1, 0x4000, Node.TYPE_BUFF2), new Node("temp", 0xca03, 0xc1, 0x2000, Node.TYPE_BUFF2), new Node("volume", 0xca04, 0xc1, 0x1800, Node.TYPE_BUFF2), new Node("energy", 0xca05, 0xc1, 0x0600, Node.TYPE_BUFF2), new Node("tpms", 0xca06, 0xc1, 0xc00000, Node.TYPE_BUFF2),

            new Node("air_switch", 0x3a06, 0x31, 0x40, 0),

            new Node("auto_air", 0x3a01, 0x31, 0x800, 0),

            new Node("blow_window", 0x3a03, 0x31, 5, Node.TYPE_BUFF3), new Node("blowing", 0x3a04, 0x31, 5, Node.TYPE_BUFF3), new Node("feet", 0x3a05, 0x31, 5, Node.TYPE_BUFF3),

            new Node("auto_cmd", 0x3a02, 0x31, 0x3, 0),

            new Node("wind_speed", 0x3a07, 0x31, 0x6, Node.TYPE_BUFF1),

            new Node("left_tempe", 0x3a07, 0x31, 0x7, Node.TYPE_BUFF4),//
            new Node("right_tempe", 0x3a07, 0x31, 0x8, Node.TYPE_BUFF4),

    };
    private final static int[] INIT_CMDS = {0x46, 0x48, 0x64, 0x68, 0x71, 0xc1, 0x31};
    private int mType = 0;
    private Preference[] mPreferences = new Preference[NODES.length];
    private int mWarningAt = 0;
    private int mLightInCar = 0;
    private int mFoot = 0;
    private int mLeftT = 34;
    private int mRightT = 34;
    private IButtonCallBack mButtonCallBack = new IButtonCallBack() {
        @Override
        public void callback(String key, boolean add) {
            // TODO Auto-generated method stub

            if ("warning_at".equals(key)) {
                if (add) {
                    mWarningAt++;
                } else {
                    mWarningAt--;
                }
                sendCanboxInfo(0x4b, 0x3, mWarningAt);
            } else if ("incar_light".equals(key)) {
                if (add) {
                    mLightInCar++;
                } else {
                    mLightInCar--;
                }
                if (mLightInCar < 0) {
                    mLightInCar = 0;
                }
                if (mLightInCar > 100) {
                    mLightInCar = 100;
                }
                sendCanboxInfo(0x6d, 0x11, mLightInCar);
            } else if ("foot".equals(key)) {
                if (add) {
                    mFoot++;
                } else {
                    mFoot--;
                }
                if (mFoot < 0) {
                    mFoot = 0;
                }
                if (mFoot > 100) {
                    mFoot = 100;
                }
                sendCanboxInfo(0x6d, 0xF, mFoot);
            } else if ("right_tempe".equals(key)) {
                if (add) {
                    mRightT++;
                } else {
                    mRightT--;
                }
                if (mRightT < 0) {
                    mRightT = 0;
                }
                if (mRightT > 100) {
                    mRightT = 100;
                }
                sendCanboxInfo(0x3a, 0x9, mRightT);
            } else if ("left_tempe".equals(key)) {
                if (add) {
                    mLeftT++;
                } else {
                    mLeftT--;
                }
                if (mLeftT < 0) {
                    mLeftT = 0;
                }
                if (mLeftT > 100) {
                    mLeftT = 100;
                }
                sendCanboxInfo(0x3a, 0x8, mLeftT);
            }
        }
    };
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

    public void setType(int t) {
        mType = t;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.touareghiworld_setting);

        for (int i = 0; i < NODES.length; ++i) {
            mPreferences[i] = findPreference(NODES[i].mKey);
            if (mPreferences[i] != null) {
                if (mPreferences[i] instanceof PreferenceScreen) {
                    mPreferences[i].setOnPreferenceClickListener(this);
                } else if (mPreferences[i] instanceof MyPreferenceEdit) {
                    ((MyPreferenceEdit) mPreferences[i]).setCallback(mButtonCallBack);
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
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 200));
        }
    }

    private void sendCanboxInfo0x90(int d0, int d1) {
        byte[] buf = new byte[]{0x3, (byte) 0x6a, 0x5, 0x1, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

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

                    if (key.equals("screen1")) {
                        ((ListPreference) preference).setValue((String) newValue);
                        preference.setSummary("%s");
                    }
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
        if (arg0.getKey().equals("reset")) {
            sendCanboxInfo(0xcc, 0x1, 0x01);
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
        byte[] buf = new byte[]{0x02, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{0x02, (byte) d0, (byte) d1, (byte) d2};
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
            } else if (p instanceof MyPreferenceEdit) {
                MyPreferenceEdit lp = (MyPreferenceEdit) p;
                if ("warning_at".equals(key)) {
                    mWarningAt = index;
                    lp.setSummary(index + " km/h");
                } else if ("incar_light".equals(key)) {
                    mLightInCar = index;
                    lp.setSummary(index + " %");
                } else if ("foot".equals(key)) {
                    mFoot = index;
                    lp.setSummary(index + " %");
                }
                // else if ("right_tempe".equals(key)) {
                // mRightT = index;
                // lp.setSummary(index + getString(R.string.temp_unic));
                // } else if ("left_tempe".equals(key)) {
                // mLeftT = index;
                // lp.setSummary(index + getString(R.string.temp_unic));
                // }
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

    private void updateView(byte[] buf) {

        int mask;
        int value;

        for (int i = 0; i < NODES.length; ++i) {

            mask = (NODES[i].mMask);

            if (NODES[i].mStatus == (buf[2] & 0xff)) {
                if (NODES[i].mShow == Node.TYPE_BUFF1) {
                    value = buf[mask + 2];
                    if (NODES[i].mKey.equals("wiper")) {
                        if ((value & 0x80) != 0) {
                            value = 1;
                        }
                    }
                    setPreference(NODES[i].mKey, value);
                } else if (NODES[i].mShow == Node.TYPE_BUFF2) {
                    value = getStatusValue(buf, mask);
                    value++;
                    setPreference(NODES[i].mKey, value);
                } else if (NODES[i].mShow == Node.TYPE_BUFF3) {
                    value = buf[mask + 2];
                    if (NODES[i].mKey.equals("blow_window")) {
                        if (value == 0xb || value == 0xc || value == 0xd || value == 0xe) {
                            value = 1;
                        } else {
                            value = 0;
                        }
                    } else if (NODES[i].mKey.equals("blowing")) {
                        if (value == 0x5 || value == 0x6 || value == 0xd || value == 0xe) {
                            value = 1;
                        } else {
                            value = 0;
                        }
                    } else if (NODES[i].mKey.equals("feet")) {
                        if (value == 0x3 || value == 0x5 || value == 0xc || value == 0xe) {
                            value = 1;
                        } else {
                            value = 0;
                        }
                    }
                    setPreference(NODES[i].mKey, value);
                } else if (NODES[i].mShow == Node.TYPE_BUFF4) {
                    value = buf[mask + 2];

                    String s;

                    String key = NODES[i].mKey;
                    Preference lp = findPreference(key);

                    s = String.format("%d.%d ", value / 2, ((value % 2) == 0) ? 0 : 5);
                    if ("right_tempe".equals(key)) {
                        mRightT = value;
                        lp.setSummary(s + getString(R.string.temp_unic));
                    } else if ("left_tempe".equals(key)) {
                        mLeftT = value;
                        lp.setSummary(s + getString(R.string.temp_unic));
                    }

                } else {
                    value = getStatusValue(buf, mask);
                    setPreference(NODES[i].mKey, value);
                }
            }
            // break;

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

    private void updateVisible(byte[] buf) {
        switch (buf[2]) {
            case 0x46:
                showPreference("speed_warning", buf[3] & 0x40, null);
                break;
            case 0x48:
                showPreference("raining", buf[3] & 0x80, null);
                break;


            case 0x68:
                showPreference("left_drive", buf[3] & 0x20, null);

                showPreference("intelligent_driving", buf[3] & 0x10, null);


                showPreference("car_light", buf[3] & 0x8, null);


                showPreference("foot", buf[3] & 0x4, null);
                showPreference("gohome", buf[3] & 0x2, null);
                showPreference("leavehome", buf[3] & 0x1, null);
                break;

            case 0x71:
                showPreference("auto_active", buf[3] & 0x80, null);
                break;

            case 0x64:
                showPreference("fwindow", buf[3] & 0x80, null);
                showPreference("bwindow", buf[3] & 0x40, null);
                showPreference("hwindow", buf[3] & 0x20, null);

                showPreference("autolockcmd", buf[4] & 0x80, null);
                showPreference("rearview_mirror", buf[4] & 0x40, null);
                showPreference("trunklock", buf[4] & 0x10, null);
                showPreference("doorlock", buf[4] & 0x20, null);
                break;

            case (byte) 0xc1:
                showPreference("range", buf[3] & 0x80, null);
                showPreference("speed", buf[3] & 0x40, null);
                showPreference("temp", buf[3] & 0x20, null);
                showPreference("volume", buf[3] & 0x10, null);
                showPreference("energy", buf[3] & 0x8, null);
                showPreference("tpms", buf[3] & 0x4, null);
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
                                updateVisible(buf);
                            } catch (Exception e) {
                                Log.d("aa", "!!!!!!!!" + e);
                            }

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
