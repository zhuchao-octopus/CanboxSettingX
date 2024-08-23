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
import com.common.utils.MyCmd;

public class KadjarRaiseFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    private static final String TAG = "KadjarRaiseFragment";
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.kadjar_raise_info);

        findPreference("vehicle_reset").setOnPreferenceClickListener(this);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

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

    public boolean onPreferenceClick(Preference arg0) {
        String key = arg0.getKey();
        if ("vehicle_reset".equals(key)) {
            byte[] buf = new byte[]{(byte) 0x83, 0x02, (byte) 0x80, (byte) 0x01};
            BroadcastUtil.sendCanboxInfo(getActivity(), buf);
        }
        return false;
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
                index = ((buf[3] & 0xff) | ((buf[2] & 0xff) << 8));
                s = index / 10 + "." + index % 10 + " L/100KM";
                setPreference("averagefuel", s);


                index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));

                s = index / 10 + "." + index % 10 + " KM/H";
                setPreference("averageapeed", s);

                index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8) | ((buf[8] & 0x01) << 16));

                s = index / 10 + "." + index % 10 + " KM";
                setPreference("running_distance", s);

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
