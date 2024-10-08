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
import com.common.utils.Node;

public class PSA206308SimpleFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    private static final String TAG = "Peugeot206SimpleFragment";
    private static final Node[] NODES = {

            new Node("fuelclear", 0x0),

    };
    private boolean mPaused = true;
    private Preference[] mPreferences = new Preference[NODES.length];
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.psa206_308__info);

        for (int i = 0; i < NODES.length; ++i) {
            mPreferences[i] = findPreference(NODES[i].mKey);
            if (mPreferences[i] != null) {
                mPreferences[i].setOnPreferenceClickListener(this);
            }
        }

        // findPreference("tpms").setOnPreferenceClickListener(this);

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    private void requestInitData() {
        sendCanboxInfo0x90(0x33, 0);

    }

    private void sendCanboxInfo0x90(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0x90, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    public boolean onPreferenceClick(Preference arg0) {

        try {
            String key = arg0.getKey();
            if ("fuelclear".equals(key)) {
                byte[] buf = new byte[]{(byte) 0xc6, 0x2, 1, 1};
                BroadcastUtil.sendCanboxInfo(getActivity(), buf);
            }
        } catch (Exception e) {

        }

        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterListener();
        mPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListener();
        mPaused = false;
        requestInitData();
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
            case 0x1: {
                if (buf[2] == -1) {
                    s = "--";
                } else {
                    index = ((buf[2] & 0xff));

                    s = String.format("%d km/h", index);
                }
                setPreference("averagespeed", s);

                if (buf[3] == -1 && buf[4] == -1) {
                    s = "----km";
                } else {
                    index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));
                    s = String.format("%d km", index);
                }

                setPreference("mileage", s);

                if (buf[5] == -1 && buf[6] == -1) {
                    s = "----km";
                } else {
                    index = ((buf[6] & 0xff) | ((buf[5] & 0xff) << 8));
                    s = String.format("%d km", index);
                }

                setPreference("mileage1", s);


                if (buf[7] == -1 && buf[8] == -1) {
                    s = "--:--";
                } else {
                    index = ((buf[8] & 0xff) | ((buf[7] & 0xff) << 8));
                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                }

                setPreference("curfuel", s);

                if (buf[9] == -1 && buf[10] == -1) {
                    s = "--:--";
                } else {
                    index = ((buf[10] & 0xff) | ((buf[9] & 0xff) << 8));
                    s = String.format("%d.%d L/100KM", index / 10, index % 10);
                }

                setPreference("averagefuel", s);

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
