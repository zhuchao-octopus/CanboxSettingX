package com.canboxsetting.compass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.GlobalDef;
import com.common.utils.MyCmd;

public class HondaCompassRaiseFragment extends MyFragment {

    PreferenceScreen mTpms;
    SeekBar mSeekBar;
    private View mMainView;
    private BroadcastReceiver mReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.honda_compass, container, false);
        mSeekBar = ((SeekBar) mMainView.findViewById(R.id.zone));
        mSeekBar.setMax(14);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                if (fromUser) {
                    if (GlobalDef.getProId() == 185) {
                        sendCanboxInfo(0xc9, 0xc2, progress);
                    } else {
                        sendCanboxInfo(0xc6, 0xc1, progress);
                    }

                }
            }
        });
        return mMainView;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v.getId() == R.id.calibration) {
            if (GlobalDef.getProId() == 185) {

                sendCanboxInfo(0xc9, 0x1, 1);
            } else {

                sendCanboxInfo(0xc6, 0xc0, 1);
            }
        }
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

        byte[] buf = new byte[]{(byte) 0x90, 0x01, (byte) 0x26};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);

    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{(byte) d0, 0x02, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void updateView(byte[] buf) {

        switch (buf[0]) {
            case (byte) 0xd2:
                if ((buf[2] & 0x80) != 0) {
                    ((TextView) mMainView.findViewById(R.id.status)).setText(R.string.calibrating);
                } else {
                    ((TextView) mMainView.findViewById(R.id.status)).setText("");
                }
                int zone = buf[2] & 0x7f;
                mSeekBar.setProgress(zone);
                ((TextView) mMainView.findViewById(R.id.zone_value)).setText(zone + "");
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
