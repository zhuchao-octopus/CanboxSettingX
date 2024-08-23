package com.canboxsetting.set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

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
import com.common.utils.Util;
import com.common.view.MyPreference2;
import com.common.view.MyPreferenceEdit;
import com.common.view.MyPreferenceEdit.IButtonCallBack;

public class PSASettingsSimpleFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "PSASimpleFragment";
    private static final Node[] NODES = {


            //		new Node("eq", 0x8401, 0x17000000, 0x0080, 0, Node.TYPE_BUFF1_INDEX),

            new Node("parking_assist", 0x8001, 0x38000000, 0x0210, 0, Node.TYPE_BUFF1_INDEX), new Node("bwiper", 0x8002, 0x38000000, 0x0180, 0, Node.TYPE_BUFF1_INDEX),


            new Node("atmosphere_lighting", 0x8004, 0x38000000, 0x02e0, 0, Node.TYPE_BUFF1_INDEX), new Node("radar_stop", 0x8005, 0x38000000, 0x0208, 0, Node.TYPE_BUFF1_INDEX), new Node("home_lighting", 0x8006, 0x38000000, 0x03c0, 0, Node.TYPE_BUFF1_INDEX), new Node("welcome_lig", 0x8007, 0x38000000, 0x0330, 0, Node.TYPE_BUFF1_INDEX), new Node("daytime_lights", 0x8008, 0x38000000, 0x0308, 0, Node.TYPE_BUFF1_INDEX),


            new Node("sound", 0x8009, 0x38000000, 0x0306, 0, Node.TYPE_BUFF1_INDEX), new Node("oil_unit", 0x800A, 0x38000000, 0x0301, 0, Node.TYPE_BUFF1_INDEX), new Node("languages", 0x800B, 0x38000000, 0x040f, 0, Node.TYPE_BUFF1_INDEX),


            new Node("blind_detection", 0x800C, 0x38000000, 0x0480, 0, Node.TYPE_BUFF1_INDEX),

            new Node("engine_stop", 0x800D, 0x38000000, 0x0440, 0, Node.TYPE_BUFF1_INDEX), new Node("welcome_cmd", 0x800E, 0x38000000, 0x0420, 0, Node.TYPE_BUFF1_INDEX), new Node("setdoor", 0x800F, 0x38000000, 0x0410, 0, Node.TYPE_BUFF1_INDEX),

            new Node("lamp_no", 0x8012, 0x38000000, 0x0530, 0, Node.TYPE_BUFF1_INDEX), new Node("unlock_trunk_only", 0x8013, 0x38000000, 0x0110, 0, Node.TYPE_BUFF1_INDEX),

            new Node("psa_simple_14", 0x8014, 0x38000000, 0x0108, 0, Node.TYPE_BUFF1_INDEX), new Node("psa_simple_15", 0x8015, 0x38000000, 0x0104, 0, Node.TYPE_BUFF1_INDEX), new Node("psa_simple_16", 0x8016, 0x38000000, 0x0102, 0, Node.TYPE_BUFF1_INDEX), new Node("psa_simple_17", 0x8017, 0x38000000, 0x0000, 0, Node.TYPE_BUFF1_INDEX), new Node("psa_simple_18", 0x8018, 0x38000000, 0x0504, 0, Node.TYPE_BUFF1_INDEX), new Node("psa_simple_19", 0x8019, 0x38000000, 0x0502, 0, Node.TYPE_BUFF1_INDEX), new Node("psa_simple_1a", 0x801a, 0x38000000, 0x0501, 0, Node.TYPE_BUFF1_INDEX),


            new Node("oushang_1d", 0x801b, 0x38000000, 0x0680, 0, Node.TYPE_BUFF1_INDEX), new Node("oushang_1e", 0x801c, 0x38000000, 0x0660, 0, Node.TYPE_BUFF1_INDEX), new Node("speed_recommendation", 0x801d, 0x38000000, 0x0610, 0, Node.TYPE_BUFF1_INDEX), new Node("dynamic_bend", 0x801e, 0x38000000, 0x0608, 0, Node.TYPE_BUFF1_INDEX), new Node("automatic_rearview_mirror_folding", 0x801f, 0x38000000, 0x0604, 0, Node.TYPE_BUFF1_INDEX),

            new Node("tpms_cal", 0x8010, 0, 0), new Node("keyboard", 0x0, 0, 0),


            new Node("memory_speed", 0x0, 0x3B000000, 0x80, 0, Node.TYPE_CUSTOM), new Node("speed1", 0x01, 0x3b000000, 0x01ff), new Node("speed2", 0x02, 0x3b000000, 0x02ff), new Node("speed3", 0x03, 0x3b000000, 0x03ff), new Node("speed4", 0x04, 0x3b000000, 0x04ff), new Node("speed5", 0x05, 0x3b000000, 0x05ff),


    };
    private final static int[] INIT_CMDS = {0x38, 0x3B,
            /*0x4010, 0x4020, 0x4030,
            0x4031, 0x4040, 0x4050, 0x4051,
            0x4060, 0x4070, 0x4080, 0x4090,
            */};
    private final static int[][] BUTTON_ID = {{R.id.mode, 0x2}, {R.id.up, 0x7}, {R.id.menu, 0x4}, {R.id.left, 0x6}, {R.id.right, 0x5}, {R.id.ok, 0x9}, {R.id.dark, 0x1}, {R.id.down, 0x8}, {R.id.esc, 0x3},};
    private int mType = 0;
    private byte[] mData3B = new byte[6];
    private IButtonCallBack mButtonCallBack = new IButtonCallBack() {
        @Override
        public void callback(String key, boolean add) {
            // TODO Auto-generated method stub

            for (int i = 0; i < NODES.length; ++i) {
                if (key.equals(NODES[i].mKey)) {

                    int index = NODES[i].mCmd;
                    if (index > 0 && index < mData3B.length) {
                        byte[] buf = new byte[]{(byte) 0x88, 0x06, 0x40, mData3B[1], mData3B[2], mData3B[3], mData3B[4], mData3B[5]};

                        int value = mData3B[index] & 0xff;
                        if (add) {
                            if (value < 0xff) {
                                value += 5;
                            }
                        } else {
                            if (value > 0) {
                                value -= 5;
                            }
                        }
                        if (value < 0) {
                            value = 0;
                        } else if (value > 0xff) {
                            value = 0xff;
                        }

                        buf[index + 2] = (byte) value;
                        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
                    }

                    break;
                }
            }
        }
    };
    private Preference[] mPreferences = new Preference[NODES.length];
    private boolean mPaused = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                byte[] buf = new byte[]{(byte) 0x90, 0x04, (byte) msg.what, 0, 0, 0};
                BroadcastUtil.sendCanboxInfo(getActivity(), buf);

            }
        }
    };
    private int mKeyId;
    View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, android.view.MotionEvent event) {
            //			Log.d("allen3", "onKey!!");
            mKeyId = getKey(v.getId());
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mHandler.removeMessages(0);
                if (mKeyId != 0) {
                    sendCanboxInfo(0x8c, mKeyId);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mHandler.removeMessages(0);
                sendCanboxInfo(0x8c, 0);
                mKeyId = 0;
            }

            return false;
        }

        ;
    };
    private Handler mHandlerKey = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                initKeyBoard();
            }
        }
    };
    private BroadcastReceiver mReceiver;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.psa_simple_settings);

        for (int i = 0; i < NODES.length; ++i) {
            mPreferences[i] = findPreference(NODES[i].mKey);
            if (mPreferences[i] != null) {
                if (mPreferences[i] instanceof PreferenceScreen) {
                    mPreferences[i].setOnPreferenceClickListener(this);
                }
                if (mPreferences[i] instanceof MyPreferenceEdit) {
                    ((MyPreferenceEdit) mPreferences[i]).setCallback(mButtonCallBack);
                } else {
                    mPreferences[i].setOnPreferenceChangeListener(this);
                }
            }
        }

        //		Preference p = findPreference("eq");
        //		int caneq = SettingProperties.getIntProperty(getActivity(), SettingProperties.KEY_CANBOX_EQ);
        //		if (caneq == 0){
        //			((SwitchPreference)p).setChecked(false);
        //		} else {
        //			((SwitchPreference)p).setChecked(true);
        //		}

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
        //		mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 500));
        }
    }

    private void sendCanboxData(int cmd, int value) {
        sendCanboxInfo(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);

    }

    private void sendCanboxData(int cmd) {
        sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0));

    }

    private void updateEQSet(boolean b) {
        //		SettingProperties.setIntProperty(getActivity(), SettingProperties.KEY_CANBOX_EQ, b?1:0); //why is cannt set?

        BroadcastUtil.sendToCarService(getActivity(), MyCmd.Cmd.SEND_UPDATE_CANBOX_SET, b ? 1 : 0);
    }

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        if ("eq".equals(key)) {
            updateEQSet((Boolean) newValue);
            ((SwitchPreference) preference).setChecked((Boolean) newValue);
        } else {
            for (int i = 0; i < NODES.length; ++i) {
                if (NODES[i].mKey.equals(key)) {
                    if (preference instanceof ListPreference) {
                        if (key.equals("psa_simple_17")) {
                            ((ListPreference) preference).setValue((String) newValue);
                            preference.setSummary("%s");
                        }
                        sendCanboxData(NODES[i].mCmd, Integer.parseInt((String) newValue));
                    } else if (preference instanceof SwitchPreference) {
                        if (NODES[i].mType == Node.TYPE_CUSTOM) {

                            mData3B[0] &= 0xC0;
                            if (((Boolean) newValue)) {
                                mData3B[0] |= 0x40;
                            }

                            byte[] buf = new byte[]{(byte) 0x88, 0x06, mData3B[0], mData3B[1], mData3B[2], mData3B[3], mData3B[4], mData3B[5]};
                            BroadcastUtil.sendCanboxInfo(getActivity(), buf);

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
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        try {
            udpatePreferenceValue(preference, newValue);
        } catch (Exception e) {

        }
        return false;
    }

    public boolean onPreferenceClick(Preference arg0) {
        if (arg0.getKey().equals("tpms_cal")) {
            byte[] buf = new byte[]{(byte) 0x80, 0x2, 0x10, 0x1};
            BroadcastUtil.sendCanboxInfo(getActivity(), buf);
        } else if (arg0.getKey().equals("keyboard")) {
            mHandlerKey.sendEmptyMessageDelayed(0, 1000);
        }
        //		else {
        //			try {
        //				udpatePreferenceValue(arg0, null);
        //			} catch (Exception e) {
        //
        //			}
        //		}
        return false;
    }

    private void initKeyBoard() {
        MyPreference2 p = (MyPreference2) findPreference("keyboard_content");
        if (p != null) {
            View mKeyView = p.getMainView();

            View v;
            for (int i = 0; i < BUTTON_ID.length; ++i) {
                v = mKeyView.findViewById(BUTTON_ID[i][0]);
                if (v != null) {
                    v.setOnTouchListener(mOnTouchListener);
                    //					v.setOnLongClickListener(mOnLongClickListener);
                }
            }
        }
    }

    private int getKey(int id) {
        int ret = 0;
        for (int i = 0; i < BUTTON_ID.length; ++i) {
            if (BUTTON_ID[i][0] == id) {
                ret = BUTTON_ID[i][1];
                break;
            }
        }
        return ret;
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
                if (key.equals("sound")) {
                    ++index;
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
                if (key.equals("memory_speed")) {
                    if (index == 0) {
                        showPreference("speed1", 0);
                        showPreference("speed2", 0);
                        showPreference("speed3", 0);
                        showPreference("speed4", 0);
                        showPreference("speed5", 0);
                        showPreference("speed6", 0);
                    } else {

                        showPreference("speed1", 1);
                        showPreference("speed2", 1);
                        showPreference("speed3", 1);
                        showPreference("speed4", 1);
                        showPreference("speed5", 1);
                        showPreference("speed6", 1);
                    }
                }

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
        if (buf[0] == 0x3b) {
            Util.byteArrayCopy(mData3B, buf, 0, 2, mData3B.length);
            //			return;
        }
        int cmd;
        int param;
        int mask;
        int value;

        for (int i = 0; i < NODES.length; ++i) {
            cmd = (NODES[i].mStatus & 0xff000000) >> 24;
            param = (NODES[i].mStatus & 0xff0000) >> 16;

            if (NODES[i].mType == Node.TYPE_DEF_BUFF) {
                if ((buf[0] & 0xff) == cmd) {
                    mask = (NODES[i].mMask & 0xff);
                    int index = ((NODES[i].mMask & 0xff00) >> 8);
                    value = getStatusValue1(buf[2 + index], mask);

                    Preference p = findPreference(NODES[i].mKey);
                    p.setSummary(value + " KM/H");
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
            } else if (NODES[i].mType == Node.TYPE_CUSTOM) {

                if ((buf[0] & 0xff) == cmd) {
                    mask = (NODES[i].mMask & 0xff);
                    int index = ((NODES[i].mMask & 0xff00) >> 8);
                    value = getStatusValue1(buf[2 + index], mask);
                    setPreference(NODES[i].mKey, value);
                    // break;

                }

            }

        }

    }

    private void updateVisible(byte[] buf) {
        switch (buf[2]) {
            case 0x10:
                showPreference("actionstate", buf[3] & 0x1, null);
                break;
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
        showPreference(id, show, null);

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
