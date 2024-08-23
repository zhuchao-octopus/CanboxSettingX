package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.GlobalDef;
import com.common.utils.MyCmd;

public class Info125 extends PreferenceFragmentCompat implements OnPreferenceClickListener {

    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.obd_binarytek);

        if (GlobalDef.getProId() == 126) {
            getPreferenceScreen().removePreference(findPreference("speed"));
            getPreferenceScreen().removePreference(findPreference("enginespeed"));
        } else if (GlobalDef.getProId() == 134) {
            getPreferenceScreen().removePreference(findPreference("battery_voltage"));
        } else if (GlobalDef.getProId() == 127 || GlobalDef.getProId() == 128 || GlobalDef.getProId() == 132) {
            getPreferenceScreen().removePreference(findPreference("transmission_oil_temperature"));
            getPreferenceScreen().removePreference(findPreference("coolant_temp"));
        }
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    public boolean onPreferenceClick(Preference arg0) {

        return false;
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


        byte[] buf = new byte[]{0x3, (byte) 0x6a, 0x5, 0x1, 0x30};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }

    private void updateView(byte[] buf) {

        int index = 0;
        String s = "";

        switch (buf[0]) {
            case 0x32: {

                index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));
                s = index + " rpm";
                setPreference("enginespeed", s);

                index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));
                s = index + " km/h";
                setPreference("speed", s);

                index = (buf[11] & 0xff) - 40;
                s = index + " °C";
                setPreference("coolant_temp", s);

                index = (buf[8] & 0xff);
                s = String.format("%d.%d V", index / 10, index % 10);
                setPreference("battery_voltage", s);

                //
                if (GlobalDef.getProId() == 126) {
                    index = (buf[14] & 0xff) - 40;
                } else {
                    index = (buf[14]);
                }
                s = index + " °C";
                setPreference("transmission_oil_temperature", s);

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
