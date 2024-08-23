package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.Util;

public class GMInfoSimpleFragment extends PreferenceFragmentCompat {
    private static final String TAG = "GMInfoSimpleFragment";
    private View mMainView;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // addPreferencesFromResource(R.xml.gm_simple_info);

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = inflater.getContext();
        mMainView = inflater.inflate(R.layout.gm_simple_info, container, false);
        View v = mMainView.findViewById(R.id.tpms);
        if (v != null) {
            v.setVisibility(View.GONE);
        }
        return mMainView;
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
        sendCanboxInfo(0x31);
        Util.doSleep(50);
        sendCanboxInfo(0x32);
        Util.doSleep(50);
        sendCanboxInfo(0x33);
    }

    private void sendCanboxInfo(int d0) {

        byte[] buf = new byte[]{(byte) 0x90, 0x01, (byte) d0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void updateView(byte[] buf) {

        String temp = "";
        int i;
        switch (buf[0]) {
            case 0xb:
                ((TextView) mMainView.findViewById(R.id.speed)).setText((((((int) buf[2] & 0xff) * 256) + ((int) buf[3] & 0xff)) / 16) + "KM/H");
                break;
            case 0x31:

                temp = String.format("%d.%d V", ((int) buf[3] & 0xff) / 10, ((int) buf[3] & 0xff) % 10);

                ((TextView) mMainView.findViewById(R.id.cell_V)).setText(temp);

                // if ((buf[4] & 0x80) != 0) {
                // temp = "-";
                // }
                temp = "" + buf[4];
                //			if (mMainView.findViewById(R.id.temp_info) != null) {
                //				((TextView) mMainView.findViewById(R.id.temp_info))
                //						.setText(temp + "°C");
                //			}

                temp = ((((int) buf[6] & 0xff) << 24) + (((int) buf[7] & 0xff) << 16) + (((int) buf[8] & 0xff) << 8) + ((int) buf[9] & 0xff)) + "KM";
                ((TextView) mMainView.findViewById(R.id.mileage_sum)).setText(temp);

                break;
            case 0x32:
                temp = (((int) buf[2] & 0xff) * 256 + ((int) buf[3] & 0xff)) + "RPM";
                ((TextView) mMainView.findViewById(R.id.am_enginespeed)).setText(temp);

                i = ((int) buf[4] & 0xff) * 256 + ((int) buf[5] & 0xff);
                temp = String.format("%d.%d L", i / 10, i % 10);

                ((TextView) mMainView.findViewById(R.id.instant)).setText(temp);

                ((TextView) mMainView.findViewById(R.id.foot)).setText(((int) buf[6] & 0xff) + "%");

                break;
            case 0x33:

                temp = ((int) buf[2] & 0xff) * 256 + ((int) buf[3] & 0xff) + "";
                ((TextView) mMainView.findViewById(R.id.type11_num)).setText(temp);

                temp = ((int) buf[4] & 0xff) * 256 + ((int) buf[5] & 0xff) + "";
                ((TextView) mMainView.findViewById(R.id.type12_num)).setText(temp);

                temp = ((int) buf[6] & 0xff) * 256 + ((int) buf[7] & 0xff) + "";
                ((TextView) mMainView.findViewById(R.id.type21_num)).setText(temp);

                temp = ((int) buf[8] & 0xff) * 256 + ((int) buf[9] & 0xff) + "";
                ((TextView) mMainView.findViewById(R.id.type22_num)).setText(temp);

                temp = "";
                if ((buf[12] & 0x1) != 0) {
                    temp = getString(R.string.tpms_hi);
                } else if ((buf[12] & 0x2) != 0) {
                    temp = getString(R.string.tpms_low);
                } else if ((buf[12] & 0x4) != 0) {
                    temp = getString(R.string.check_tpms);
                }

                ((TextView) mMainView.findViewById(R.id.type11_info)).setText(temp);

                temp = "";
                if ((buf[13] & 0x1) != 0) {
                    temp = getString(R.string.tpms_hi);
                } else if ((buf[13] & 0x2) != 0) {
                    temp = getString(R.string.tpms_low);
                } else if ((buf[13] & 0x4) != 0) {
                    temp = getString(R.string.check_tpms);
                }
                ((TextView) mMainView.findViewById(R.id.type12_info)).setText(temp);

                temp = "";
                if ((buf[14] & 0x1) != 0) {
                    temp = getString(R.string.tpms_hi);
                } else if ((buf[14] & 0x2) != 0) {
                    temp = getString(R.string.tpms_low);
                } else if ((buf[14] & 0x4) != 0) {
                    temp = getString(R.string.check_tpms);
                }
                ((TextView) mMainView.findViewById(R.id.type21_info)).setText(temp);

                temp = "";
                if ((buf[15] & 0x1) != 0) {
                    temp = getString(R.string.tpms_hi);
                } else if ((buf[15] & 0x2) != 0) {
                    temp = getString(R.string.tpms_low);
                } else if ((buf[15] & 0x4) != 0) {
                    temp = getString(R.string.check_tpms);
                }
                ((TextView) mMainView.findViewById(R.id.type22_info)).setText(temp);

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
