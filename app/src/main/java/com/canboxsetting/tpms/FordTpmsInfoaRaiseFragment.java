package com.canboxsetting.tpms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.view.MyPreference2;

public class FordTpmsInfoaRaiseFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {

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

        byte[] buf = new byte[]{(byte) 0x90, 0x01, (byte) 0x4a};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);

    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{(byte) d0, 0x02, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private View mTpmsView;

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
        switch (buf[0]) {
            case 0x63:


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
