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

public class MingjueRongweiTpmsInfoaRaiseFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {

    private View mTpmsView;
    private byte mUnit = 0;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mTpmsView = inflater.inflate(R.layout.type_info3, container, false);
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

        byte[] buf = new byte[]{(byte) 0x90, 0x02, (byte) 0x25, 0};
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

        String text = "";

        if (value != 255) {
            switch (mUnit) {
                case 0:
                    value = 100 * value;
                    text = String.format("%d.%d KPA", value / 10, value % 10);
                    break;
                case 1:
                    text = String.format("%d.%d Bar", value / 10, value % 10);
                    break;
                case 2:
                    text = String.format("%d psi", value);
                    break;
            }
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
        switch (buf[0]) {
            case 0x52:
                mUnit = buf[12];

                if (mTpmsView != null) {
                    setTpmsTextValue(R.id.type11_num, buf[4] & 0xff, (buf[2] & 0xf0));
                    setTpmsTextValue(R.id.type12_num, buf[5] & 0xff, (buf[2] & 0xf));
                    setTpmsTextValue(R.id.type21_num, buf[6] & 0xff, (buf[3] & 0xf0));
                    setTpmsTextValue(R.id.type22_num, buf[7] & 0xff, (buf[3] & 0xf));

                    setTpmsTextInfo(R.id.type11_info, buf[8] & 0xff, (buf[2] & 0xf0));
                    setTpmsTextInfo(R.id.type12_info, buf[9] & 0xff, (buf[2] & 0xf));
                    setTpmsTextInfo(R.id.type21_info, buf[10] & 0xff, (buf[3] & 0xf0));
                    setTpmsTextInfo(R.id.type22_info, buf[11] & 0xff, (buf[3] & 0xf));
                }


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
