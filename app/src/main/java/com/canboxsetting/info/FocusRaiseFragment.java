package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.preference.Preference;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;

public class FocusRaiseFragment extends PreferenceFragmentCompat {
    private static final String TAG = "Golf7InfoSimpleFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.focus_raise_info);

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    boolean mPaused = true;

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

    private final static int[] INIT_CMDS = {0x6300};

    private void requestInitData() {
        //		for (int i = 0; i < INIT_CMDS.length; ++i) {
        //			mHandler.sendMessageDelayed(
        //					mHandler.obtainMessage(0, INIT_CMDS[i], 0), i * 100);
        //		}
        sendCanboxInfo(INIT_CMDS[0]);
        mHandler.removeMessages(1);
        mHandler.sendEmptyMessageDelayed(1, 1000);
    }

    private void sendCanboxInfo(int d0) {

        byte[] buf = new byte[]{(byte) 0x90, 0x02, (byte) ((d0 & 0xff00) >> 8), (byte) (d0 & 0xff)};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                switch (msg.what) {
                    case 0:
                        sendCanboxInfo(msg.arg1);
                        break;
                    case 1:
                        requestInitData();
                        break;
                }
            } else {
                mHandler.removeMessages(0);
                mHandler.removeMessages(1);
            }
        }
    };

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }

    private View mTpmsView;

    private void setTpmsTextValue(int id, int value, int color) {

        String text;

        if (value != 255) {
            value = 275 * value / 10;
            text = String.format("%d.%d KPA", value / 10, value % 10);
        } else {
            text = "--";
        }

        if (color != 0) {
            color = Color.RED;
        } else {
            color = Color.WHITE;
        }

        TextView tv = ((TextView) mTpmsView.findViewById(id));

        tv.setTextColor(color);
        tv.setText(text);
    }

    private void setTpmsTextInfo(int id, int value, int color) {

        String text;

        if (value == 255) {
            text = "--";
        } else {
            value = value - 60;
            text = String.format("%d °C", value);
        }

        if (color != 0) {
            color = Color.RED;
        } else {
            color = Color.WHITE;
        }

        TextView tv = ((TextView) mTpmsView.findViewById(id));

        tv.setTextColor(color);
        tv.setText(text);
    }

    private void updateView(byte[] buf) {
        int index;
        String s = "";
        switch (buf[0]) {
            case 0x63:
                //move to tpms activity
                //			if (mTpmsView == null) {
                //				MyPreference2 p = (MyPreference2) findPreference("tpms_content");
                //				if (p != null) {
                //					mTpmsView = p.getMainView();
                //					View v = mTpmsView.findViewById(R.id.tpms);
                //					v.setVisibility(View.GONE);
                //				}
                //			}
                if (mTpmsView != null) {
                    setTpmsTextValue(R.id.type11_num, buf[9] & 0xff, (buf[17] & 0xf0));
                    setTpmsTextValue(R.id.type12_num, buf[10] & 0xff, (buf[17] & 0xf));
                    setTpmsTextValue(R.id.type21_num, buf[11] & 0xff, (buf[18] & 0xf0));
                    setTpmsTextValue(R.id.type22_num, buf[12] & 0xff, (buf[18] & 0xf));

                    setTpmsTextInfo(R.id.type11_info, buf[13] & 0xff, (buf[17] & 0xf0));
                    setTpmsTextInfo(R.id.type12_info, buf[14] & 0xff, (buf[17] & 0xf));
                    setTpmsTextInfo(R.id.type21_info, buf[15] & 0xff, (buf[18] & 0xf0));
                    setTpmsTextInfo(R.id.type22_info, buf[16] & 0xff, (buf[18] & 0xf));
                }

                index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8) | ((buf[2] & 0xff) << 16));

                s = String.format("%d KM", index);
                setPreference("mileage1", s);

                index = ((buf[6] & 0xff) | ((buf[5] & 0xff) << 8));
                if (index > 999) {
                    s = "---";
                } else {
                    s = String.format("%d KM", index);
                }
                setPreference("mileage17", s);

                index = (buf[7] & 0xff) * 2 + 1;

                if (index > 300) {
                    s = "--";
                } else {
                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                }

                setPreference("averagefuel24", s);

                index = (buf[8] & 0xff);

                if (index > 100) {
                    s = "--";
                } else {
                    s = String.format("%d.%d ", index / 2, (index * 10 / 2) % 10);
                    s += "%";
                }

                setPreference("beoilmass", s);
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
