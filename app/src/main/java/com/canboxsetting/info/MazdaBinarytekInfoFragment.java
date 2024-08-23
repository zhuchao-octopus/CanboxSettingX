package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.canboxsetting.R;
import com.common.utils.AppConfig;
import com.common.utils.BroadcastUtil;
import com.common.utils.MachineConfig;
import com.common.utils.MyCmd;
import com.common.utils.UtilSystem;
import com.common.view.MyPreference2;

public class MazdaBinarytekInfoFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    private static final String TAG = "KadjarRaiseFragment";
    private final static int[] INIT_CMDS = {0x0b00, 0x0c00, 0x1000, 0x1100};
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.mazda3_binarytek_info);

        String value = AppConfig.getCanboxSetting();
        if (value != null) {
            if (value.equals(MachineConfig.VALUE_CANBOX_MAZDA_XINBAS) || value.equals(MachineConfig.VALUE_CANBOX_MAZDA_RAISE)) {

                getPreferenceScreen().removePreference(findPreference("miunit"));
                getPreferenceScreen().removePreference(findPreference("the_temperature_value"));
            }

            if (!value.equals(MachineConfig.VALUE_CANBOX_MAZDA_RAISE)) {
                getPreferenceScreen().removePreference(findPreference("tpms"));
                getPreferenceScreen().removePreference(findPreference("warning_msg"));
            } else {
                findPreference("tpms").setOnPreferenceClickListener(this);
                findPreference("warning_msg").setOnPreferenceClickListener(this);
            }
        }
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

    private void sendCanboxInfo0x90(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0x83, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    public boolean onPreferenceClick(Preference arg0) {
        if (arg0.getKey().equals("tpms")) {
            UtilSystem.doRunActivity(getActivity(), "com.canboxsetting", "com.canboxsetting.TPMSActivity");
            return true;
        } else if (arg0.getKey().equals("warning_msg")) {
            //			new Handler().postDelayed(new Runnable() {
            //
            //				@Override
            //				public void run() {
            //					// TODO Auto-generated method stub
            //					updateWarningView(mWarningMsg);
            //				}
            //			}, 5);
            byte[] buf = new byte[]{(byte) 0x90, 0x1, (byte) 0x28};
            BroadcastUtil.sendCanboxInfo(getActivity(), buf);
            return true;
        }
        return false;
    }

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

    private int getFuelrange(byte b) {
        int ret = 0;
        switch (b) {
            case 0:
                ret = 60;
                break;
            case 1:
                ret = 10;
                break;
            case 2:
                ret = 12;
                break;
            case 3:
                ret = 20;
                break;
            case 4:
                ret = 30;
                break;
            case 5:
                ret = 40;
                break;
            case 6:
                ret = 50;
                break;
            case 7:
                ret = 60;
                break;
            case 8:
                ret = 70;
                break;
            case 9:
                ret = 80;
                break;
            case 10:
                ret = 90;
                break;
            case 11:
                ret = 100;
                break;
        }
        return ret;
    }

    //	private byte mWarningMsg = 0;

    private void updateView(byte[] buf) {

        int index = 0;
        String s = "";

        switch (buf[0]) {
            case 0x50:
            case 0x0b: {
                if (buf.length >= 16) {
                    index = ((buf[3] & 0xff) | ((buf[2] & 0xff) << 8));

                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("time1", s);

                    index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));

                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("time2", s);
                    index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));

                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("time3", s);
                    index = ((buf[9] & 0xff) | ((buf[8] & 0xff) << 8));

                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("time4", s);
                    index = ((buf[11] & 0xff) | ((buf[10] & 0xff) << 8));

                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("time5", s);
                    index = ((buf[13] & 0xff) | ((buf[12] & 0xff) << 8));

                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("time6", s);
                    index = ((buf[15] & 0xff) | ((buf[14] & 0xff) << 8));

                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("time_i_eloop", s);
                    index = ((buf[17] & 0xff) | ((buf[16] & 0xff) << 8));

                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("time_cur_drive", s);

                }
                break;
            }
            case 0x51:
            case 0x0c: {
                if (buf.length >= 16) {
                    index = ((buf[3] & 0xff) | ((buf[2] & 0xff) << 8));

                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("min15", s);

                    index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));

                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("min14", s);
                    index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));

                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("min13", s);
                    index = ((buf[9] & 0xff) | ((buf[8] & 0xff) << 8));

                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("min12", s);
                    index = ((buf[11] & 0xff) | ((buf[10] & 0xff) << 8));

                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("min11", s);
                    index = ((buf[13] & 0xff) | ((buf[12] & 0xff) << 8));

                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("min10", s);
                    index = ((buf[15] & 0xff) | ((buf[14] & 0xff) << 8));

                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("min9", s);
                    index = ((buf[17] & 0xff) | ((buf[16] & 0xff) << 8));

                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("min8", s);
                    index = ((buf[19] & 0xff) | ((buf[18] & 0xff) << 8));
                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("min7", s);
                    index = ((buf[21] & 0xff) | ((buf[20] & 0xff) << 8));
                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("min6", s);
                    index = ((buf[23] & 0xff) | ((buf[22] & 0xff) << 8));
                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("min5", s);
                    index = ((buf[25] & 0xff) | ((buf[24] & 0xff) << 8));
                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("min4", s);
                    index = ((buf[27] & 0xff) | ((buf[26] & 0xff) << 8));
                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("min3", s);
                    index = ((buf[29] & 0xff) | ((buf[28] & 0xff) << 8));
                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("min2", s);
                    index = ((buf[31] & 0xff) | ((buf[30] & 0xff) << 8));
                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                    setPreference("min1", s);
                }
                break;
            }
            case 0x10:
                s = "--";
                if (buf[2] == 1) {
                    index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8) | ((buf[3] & 0xff) << 16));
                    // s = String.format("%d.%d KM", index / 10, index % 10);
                    s = index + " KM";
                }
                setPreference("miunit", s);
                break;
            case 0x11:
                s = "--";
                index = ((buf[2] & 0xff)) - 40;

                s = index + " °C";

                setPreference("the_temperature_value", s);
                break;
            case 0x28:
                updateWarningView(buf[3]);
                break;
        }
    }

    private void updateWarningView(byte b) {
        //		mWarningMsg = b;
        String s = "";
        if ((b & 0x80) != 0) {
            if (s.length() != 0) {
                s += "\n\n";
            }
            s += getActivity().getString(R.string.door_unlock_msg);
        }
        if ((b & 0x40) != 0) {
            if (s.length() != 0) {
                s += "\n\n";
            }
            s += getActivity().getString(R.string.safetybelt);
        }
        if ((b & 0x20) != 0) {
            if (s.length() != 0) {
                s += "\n\n";
            }
            s += getActivity().getString(R.string.electriclow);
        }

        MyPreference2 p = (MyPreference2) findPreference("warning_msg_content");
        if (p != null) {
            View v = p.getMainView();
            if (v != null) {
                TextView tv = (TextView) v.findViewById(R.id.warning_msg);
                tv.setText(s);
            }
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
