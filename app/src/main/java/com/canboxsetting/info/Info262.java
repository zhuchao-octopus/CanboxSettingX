package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;

public class Info262 extends PreferenceFragment {
    private static final String TAG = "Golf7InfoSimpleFragment";
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.renault_baogu_info);


        getPreferenceScreen().removePreference(findPreference("fuelinfo"));
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

        byte[] buf = new byte[]{(byte) 0x90, 0x2, (byte) 0x81, (byte) 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);

    }

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }

    private void updateView(byte[] buf) {
        int index;
        String s = "";
        switch (buf[0]) {
            case (byte) 0x81:
                index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8) | ((buf[8] & 0x01) << 16));
                if (index == 0x1ffff) {
                    s = "------ km";
                } else {
                    s = index / 10 + "." + index % 10 + " km";
                }
                setPreference("mileage", s);

                index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));
                s = index / 10 + "." + index % 10 + " km/h";
                setPreference("averagespeed", s);

                index = ((buf[3] & 0xff) | ((buf[2] & 0xff) << 8));

                if (index == 0xffff) {
                    s = " ---- l/100km";
                } else {
                    s = index / 10 + "." + index % 10 + " l/100km";
                }
                setPreference("averagefuel", s);

                // index = ((buf[9] & 0xff) | ((buf[8] & 0xff) << 8));
                // s = index / 10 + "." + index % 10 + " L";
                // setPreference("fuelinfo", s);
                //
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
