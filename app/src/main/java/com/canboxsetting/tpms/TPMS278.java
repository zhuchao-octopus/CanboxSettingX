package com.canboxsetting.tpms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.canboxsetting.R;
import com.car.ui.GlobalDef;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.Util;
import com.common.view.MyPreference2;

public class TPMS278 extends PreferenceFragment {

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

        byte[] buf = new byte[]{(byte) 0xff, 1, (byte) 0xb};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private View mTpmsView;


    private void setTpmsTextInfo(int id, int value, int color) {

        String text = "";

        if (value != 0xffff) {
            text = String.format("%d kpa", value);
        } else {
            text = "--";
        }

        if (color == 0) {
            color = Color.WHITE;
        } else {
            color = Color.RED;
        }

        TextView tv = ((TextView) mTpmsView.findViewById(id));

        tv.setTextColor(color);
        tv.setText(text);
    }

    private void setTpmsTextInfo2(int id, int value) {

        String text = "";

        if (value != 255) {
            text = String.format("(%d.%d) Bar", value / 10, value % 10);
        } else {
            text = "--";
        }


        TextView tv = ((TextView) mTpmsView.findViewById(id));

        tv.setTextColor(Color.WHITE);
        tv.setText(text);
    }

    private void setTpmsTextValue(int id, int value, int color) {

        String text;

        if (value == 255) {
            text = "--";
        } else {
            value = value - 40;
            text = String.format("%d °C", value);
        }

        color = Color.WHITE;

        TextView tv = ((TextView) mTpmsView.findViewById(id));

        tv.setTextColor(color);
        tv.setText(text);
    }


    private void setTpmsWaring(int text_id, int value) {
        String s = "";
        int id = 0;
        switch ((value & 0xff)) {
            case 1:
                id = R.string.red_warn;
                break;
            case 2:
                id = R.string.str_quick_leakage;
                break;
            case 3:
                id = R.string.yellow_warn;
                break;
        }
        TextView tv = ((TextView) mTpmsView.findViewById(text_id));
        if (id != 0) {
            s = getString(id);
        }
        tv.setText(s);
    }

    int mColor;

    private void updateView(byte[] buf) {
        switch (buf[0]) {
            case (byte) 0xb:

                if (mTpmsView != null) {


                    setTpmsTextInfo(R.id.type11_num, (buf[4] & 0xff) | ((buf[3] & 0xff) << 8), (buf[2] & 0x8));


                    setTpmsTextInfo(R.id.type12_num, (buf[6] & 0xff) | ((buf[5] & 0xff) << 8), (buf[2] & 0x4));

                    setTpmsTextInfo(R.id.type21_num, (buf[8] & 0xff) | ((buf[7] & 0xff) << 8), (buf[2] & 0x2));

                    setTpmsTextInfo(R.id.type22_num, (buf[10] & 0xff) | ((buf[9] & 0xff) << 8), (buf[2] & 0x1));

                }

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
