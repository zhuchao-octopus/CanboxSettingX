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
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.Util;
import com.common.view.MyPreference2;

public class TPMS192 extends PreferenceFragment {

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

    }


    private View mTpmsView;


    private void setTpmsTextInfoOD(int id, int value, int color) {

        String text = "";

        if (value != 255) {
            //			value = 1373 * value;
            text = String.format("%d  kpa", value);
        } else {
            text = "--";
        }

        // if (color != 0) {
        // color = Color.RED;
        // } else {
        color = Color.WHITE;
        // }

        TextView tv = ((TextView) mTpmsView.findViewById(id));

        tv.setTextColor(color);
        tv.setText(text);
    }

    final static int[] WARNING = {0, R.string.system_self_test, R.string.str_quick_leakage, R.string.str_slow_leakage,

            R.string.landwind_Tire_pressure_high, R.string.landwind_Tire_pressure_low,

            R.string.temp_high_more, R.string.str_temp_warning, R.string.str_low_battery,};

    private void setTpmsTextWarning(int id, int value) {

        String text = "";
        if (value > 0 && value < WARNING.length) {
            text = getString(WARNING[value]);
        }

        TextView tv = ((TextView) mTpmsView.findViewById(id));

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

        // if (color != 0) {
        // color = Color.RED;
        // } else {
        color = Color.WHITE;
        // }

        TextView tv = ((TextView) mTpmsView.findViewById(id));

        tv.setTextColor(color);
        tv.setText(text);
    }


    private void updateView(byte[] buf) {
        switch (buf[0]) {
            case (byte) 0x37:

                if (mTpmsView != null) {

                    switch (buf[3]) {
                        case 0:
                            switch (buf[2]) {
                                case 0:
                                    setTpmsTextInfoOD(R.id.type11_num, (buf[5] & 0xff) | ((buf[4] & 0xff) << 8), 0);
                                    break;
                                case 1:
                                    setTpmsTextInfoOD(R.id.type12_num, (buf[5] & 0xff) | ((buf[4] & 0xff) << 8), 0);
                                    break;
                                case 2:
                                    setTpmsTextInfoOD(R.id.type21_num, (buf[5] & 0xff) | ((buf[4] & 0xff) << 8), 0);
                                    break;
                                case 3:
                                    setTpmsTextInfoOD(R.id.type22_num, (buf[5] & 0xff) | ((buf[4] & 0xff) << 8), 0);
                                    break;
                            }
                            break;

                        case 1:
                            switch (buf[2]) {
                                case 0:
                                    setTpmsTextValue(R.id.type11_info, (buf[5] & 0xff) | ((buf[4] & 0xff) << 8), 0);
                                    break;
                                case 1:
                                    setTpmsTextValue(R.id.type12_info, (buf[5] & 0xff) | ((buf[4] & 0xff) << 8), 0);
                                    break;
                                case 2:
                                    setTpmsTextValue(R.id.type21_info, (buf[5] & 0xff) | ((buf[4] & 0xff) << 8), 0);
                                    break;
                                case 3:
                                    setTpmsTextValue(R.id.type22_info, (buf[5] & 0xff) | ((buf[4] & 0xff) << 8), 0);
                                    break;
                            }
                            break;
                        case 2:
                            switch (buf[2]) {
                                case 0:
                                    setTpmsTextWarning(R.id.type11_info2, (buf[5] & 0xff));
                                    break;
                                case 1:
                                    setTpmsTextWarning(R.id.type12_info2, (buf[5] & 0xff));
                                    break;
                                case 2:
                                    setTpmsTextWarning(R.id.type21_info2, (buf[5] & 0xff));
                                    break;
                                case 3:
                                    setTpmsTextWarning(R.id.type22_info2, (buf[5] & 0xff));
                                    break;
                            }
                            break;
                    }

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
