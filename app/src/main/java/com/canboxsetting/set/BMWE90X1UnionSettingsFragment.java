package com.canboxsetting.set;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.Node;
import com.common.utils.Util;

public class BMWE90X1UnionSettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "BMWE90X1UnionSettingsFragment";
    private final static int[] INIT_CMDS = {};
    private static final Node[] NODES = {new Node("range", 0x8200, 0x0401), new Node("lang", 0x8201, 0x0401), new Node("age_full", 0x8202, 0x0401), new Node("temp", 0x8203, 0x0401), new Node("lef_hot", 0x8501, 0x0), new Node("rif_hot", 0x8502, 0x0), new Node("redar", 0x8503, 0x0), new Node("curtain", 0x8504, 0x0),};
    private final Preference[] mPreferences = new Preference[NODES.length];
    private boolean mPaused = true;
    private byte[] mBufUnit = new byte[4];
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.bmw_e90x1_union_settings);

        for (int i = 0; i < NODES.length; ++i) {
            mPreferences[i] = findPreference(NODES[i].mKey);
            // Log.d("aa", mPreferences[i]+":"+NODES[i].mKey);
            if (mPreferences[i] != null) {
                if (mPreferences[i] instanceof PreferenceScreen) {
                    mPreferences[i].setOnPreferenceClickListener(this);
                } else {
                    mPreferences[i].setOnPreferenceChangeListener(this);
                }
            }
        }

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

    }

    // private Handler mHandler = new Handler() {
    // @Override
    // public void handleMessage(Message msg) {
    // if (!mPaused) {
    // sendCanboxInfo(0x90, (msg.what & 0xff00) >> 8, msg.what & 0xff);
    // }
    // }
    // };

    private void requestInitData() {
        // for (int i = 0; i < INIT_CMDS.length; ++i) {
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], i * 100);
        // }
    }

    private void sendCanboxData(int cmd, int value) {
        int index = ((cmd & 0xff) >> 0);
        mBufUnit[index] = (byte) value;

        String strTimeFormat = Settings.System.getString(getActivity().getContentResolver(), android.provider.Settings.System.TIME_12_24);

        byte t24 = 1;
        if ("12".equals(strTimeFormat)) {
            t24 = 0;
        }

        byte[] buf = new byte[]{(byte) ((cmd & 0xff00) >> 8), 0x05, mBufUnit[0], mBufUnit[1], mBufUnit[2], mBufUnit[3], t24};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);

    }

    private void sendCanboxData(int cmd) {

        byte[] buf = new byte[]{(byte) ((cmd & 0xff00) >> 8), 0x2, (byte) ((cmd & 0xff) >> 0), 0x1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);

        Util.doSleep(300);
        buf[3] = 0x0;
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);

    }

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {
                    sendCanboxData(NODES[i].mCmd, Integer.parseInt((String) newValue));
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
        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean onPreferenceClick(Preference arg0) {

        try {
            udpatePreferenceValue(arg0, null);
        } catch (Exception ignored) {
        }
        return false;
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
            }
        }
    }

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }

    private void updateView(byte[] buf) {

        try {
            switch (buf[0]) {
                case 0x4:
                    Util.byteArrayCopy(mBufUnit, buf, 0, 2, mBufUnit.length);

                    setPreference(NODES[0].mKey, buf[2]);
                    setPreference(NODES[1].mKey, buf[3]);
                    setPreference(NODES[2].mKey, buf[4]);
                    setPreference(NODES[3].mKey, buf[5]);

                    break;
                case 0x7:
                    String s = null;
                    int index;

                    index = ((buf[2] & 0xf0) >> 4);
                    if (index == 0) {
                        s = getString(R.string.close);
                    } else {
                        s = "" + index;
                    }
                    setPreference(NODES[4].mKey, s);

                    index = ((buf[2] & 0xf) >> 0);
                    if (index == 0) {
                        s = getString(R.string.close);
                    } else {
                        s = "" + index;
                    }
                    setPreference(NODES[5].mKey, s);

                    index = ((buf[3] & 0x1) >> 0);
                    if (index == 0) {
                        s = getString(R.string.close);
                    } else {
                        s = getString(R.string.open);
                    }
                    setPreference(NODES[6].mKey, s);

                    //				index = ((buf[2] & 0xf0) >> 4);
                    //				if (index == 0) {
                    //					s = getString(R.string.close);
                    //				} else {
                    //					s = "" + index;
                    //				}
                    //				setPreference(NODES[7].mKey, s);

                    break;
            }
        } catch (Exception e) {
            Log.d("allen", "err" + e);
        }
    }

    private void showPreference(String id, int show) {
        Preference preference = null;

        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(id)) {
                preference = mPreferences[i];
                break;
            }
        }

        if (preference != null) {
            if (show != 0) {
                if (findPreference(id) == null) {
                    getPreferenceScreen().addPreference(preference);
                }
            } else {
                if (findPreference(id) != null) {
                    getPreferenceScreen().removePreference(preference);
                }
            }
        }

    }

    private void unregisterListener() {
        if (mReceiver != null) {
            this.getActivity().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
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
