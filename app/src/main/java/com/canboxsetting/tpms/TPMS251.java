package com.canboxsetting.tpms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;

public class TPMS251 extends PreferenceFragment {

    int mColor;
    private View mTpmsView;
    private BroadcastReceiver mReceiver;

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

        byte[] buf = new byte[]{3, (byte) 0x6a, 0x05, 1, (byte) 0x48};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{(byte) d0, 0x02, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void setTpmsTextInfo(int id, int value, int color) {

        String text = "";
        value = (int) (value * 2.745f);

        if (value != 255) {
            text = String.format("%d.%02d Bar", value / 100, value % 100);
        } else {
            text = "--";
        }

        if (color == 0) {
            color = Color.WHITE;
        } else if (color == 3) {
            color = Color.YELLOW;
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

    private void updateView(byte[] buf) {
        switch (buf[0]) {
            case (byte) 0x38:

                if (mTpmsView != null) {


                    setTpmsTextInfo(R.id.type11_num, buf[6] & 0xff, mColor & 0xff);
                    setTpmsTextInfo(R.id.type12_num, buf[7] & 0xff, (mColor & 0xff00) >> 8);
                    setTpmsTextInfo(R.id.type21_num, buf[8] & 0xff, (mColor & 0xff0000) >> 16);
                    setTpmsTextInfo(R.id.type22_num, buf[9] & 0xff, (mColor & 0xff000000) >> 24);


                    setTpmsTextValue(R.id.type11_info, buf[2] & 0xff, 0);
                    setTpmsTextValue(R.id.type12_info, buf[3] & 0xff, 0);
                    setTpmsTextValue(R.id.type21_info, buf[4] & 0xff, 0);
                    setTpmsTextValue(R.id.type22_info, buf[5] & 0xff, 0);

                }

                break;
            case 0x39:
                mColor = (((buf[2] & 0xff) << 0) | ((buf[3] & 0xff) << 8) | ((buf[4] & 0xff) << 16) | ((buf[5] & 0xff) << 24));

                setTpmsWaring(R.id.type11_info2, buf[2] & 0xff);
                setTpmsWaring(R.id.type12_info2, buf[3] & 0xff);
                setTpmsWaring(R.id.type21_info2, buf[4] & 0xff);
                setTpmsWaring(R.id.type22_info2, buf[5] & 0xff);


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
