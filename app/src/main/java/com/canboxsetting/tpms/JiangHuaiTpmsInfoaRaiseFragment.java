package com.canboxsetting.tpms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.Util;

public class JiangHuaiTpmsInfoaRaiseFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {

    byte mColor1;
    byte mColor2;
    byte mColor3;
    byte mColor4;
    private View mTpmsView;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mTpmsView = inflater.inflate(R.layout.type_info4, container, false);
        return mTpmsView;
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListener();

        byte[] buf = new byte[]{(byte) 0x90, 0x02, (byte) 0x38, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
        Util.doSleep(20);
        buf[2] = 0x39;
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{(byte) d0, 0x02, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    public boolean onPreferenceClick(Preference arg0) {

        try {
            String key = arg0.getKey();
            if ("tpms".equals(key)) {
                mTpmsView = null;
                sendCanboxInfo(0x90, 0x65, 0);
            }
        } catch (Exception e) {

        }

        return false;
    }

    private void setTpmsTextValue(int id, int value, int color) {

        String text;

        if (value != 255) {
            value = 2745 * value / 1000;
            text = String.format("%d.%d bar", value / 100, value % 100);
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
            value = value - 40;
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

    private void setTpmsTextInfo2(int id, byte value) {

        String s = getTpmsInfoText(value);
        int color = getTpmsInfoColor(value);
        TextView tv = ((TextView) mTpmsView.findViewById(id));

        tv.setTextColor(color);
        tv.setText(s);
    }

    private String getTpmsInfoText(byte data) {
        int text = data & 0xff;
        switch (text) {
            case 1:
                text = R.string.vw_raise_warning_info_1;
                break;
            case 2:
                text = R.string.str_quick_leakage;
                break;
            case 3:
                text = R.string.vw_raise_warning_info_1;
                break;
            default:
                text = 0;
                break;
        }
        String s = "";
        if (text != 0) {
            s = getString(text);
        }
        return s;
    }

    private int getTpmsInfoColor(byte data) {
        int text = data & 0xff;
        switch (text) {
            case 1:
            case 2:
                text = Color.RED;
                break;
            case 3:
                text = Color.YELLOW;
                break;
            default:
                text = Color.WHITE;
                break;
        }
        return text;
    }

    private void updateView(byte[] buf) {
        switch (buf[0]) {
            case 0x38:


                if (mTpmsView != null) {

                    setTpmsTextValue(R.id.type11_num, buf[6] & 0xff, mColor1);
                    setTpmsTextValue(R.id.type12_num, buf[7] & 0xff, mColor2);
                    setTpmsTextValue(R.id.type21_num, buf[8] & 0xff, mColor3);
                    setTpmsTextValue(R.id.type22_num, buf[9] & 0xff, mColor4);

                    setTpmsTextInfo(R.id.type11_info, buf[2] & 0xff, mColor1);
                    setTpmsTextInfo(R.id.type12_info, buf[3] & 0xff, mColor2);
                    setTpmsTextInfo(R.id.type21_info, buf[4] & 0xff, mColor3);
                    setTpmsTextInfo(R.id.type22_info, buf[5] & 0xff, mColor4);

                }

                break;

            case 0x39:
                //			int text;
                //			int id;

                setTpmsTextInfo2(R.id.type11_info2, buf[2]);
                setTpmsTextInfo2(R.id.type12_info2, buf[3]);
                setTpmsTextInfo2(R.id.type21_info2, buf[4]);
                setTpmsTextInfo2(R.id.type22_info2, buf[5]);
                //			text = getTpmsInfoText(buf[2]);
                //			if (text == 0) {
                //				text = getTpmsInfoText(buf[3]);
                //			}
                //			if (text == 0) {
                //				text = getTpmsInfoText(buf[4]);
                //			}
                //			if (text == 0) {
                //				text = getTpmsInfoText(buf[5]);
                //			}
                //			mColor1 = buf[2];
                //			mColor2 = buf[3];
                //			mColor3 = buf[4];
                //			mColor4 = buf[5];
                //			TextView tv = ((TextView) mTpmsView.findViewById(R.id.type30_info));
                //			if (text == 0) {
                //				tv.setText("");
                //			} else {
                //				tv.setText(text);
                //			}
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
