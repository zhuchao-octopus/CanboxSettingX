package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.preference.Preference;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.utils.MyCmd;
import com.common.utils.Util;

public class BMWE90X1UnionFragment extends PreferenceFragmentCompat {
    private static final String TAG = "Golf7InfoSimpleFragment";
    private byte[] mBufUnit = new byte[4];
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.bmw_e90x1_union_info);

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
            case 0x3:
                index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));
                if ((mBufUnit[0]) == 0) {
                    s = index + " km";
                } else {
                    s = index + " mls";
                }
                setPreference("mileage", s);


                index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));

                //			if ((mBufUnit[0]) == 0) {
                //				s = index + " KM";
                //			} else {
                //				s = index + " MLS";
                //			}
                s = index / 10 + "." + index % 10 + " km/h";
                setPreference("averagespeed", s);
                index = ((buf[9] & 0xff) | ((buf[8] & 0xff) << 8));

                if ((mBufUnit[2]) == 0) {
                    s = index / 10 + "." + index % 10 + " l/100km";
                } else if ((mBufUnit[2]) == 1) {
                    s = index / 10 + "." + index % 10 + " mpg(US)";
                } else if ((mBufUnit[2]) == 2) {
                    s = index / 10 + "." + index % 10 + " mpg(UK)";
                } else if ((mBufUnit[2]) == 3) {
                    s = index / 10 + "." + index % 10 + " km/l";
                }
                setPreference("averagefuel", s);

                break;
            case 0x4:
                Util.byteArrayCopy(mBufUnit, buf, 0, 2, mBufUnit.length);
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
