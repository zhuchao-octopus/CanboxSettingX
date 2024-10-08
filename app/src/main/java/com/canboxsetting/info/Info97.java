package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;

public class Info97 extends PreferenceFragment {
    private static final String TAG = "Golf7InfoSimpleFragment";
    private final static int[] INIT_CMDS = {0x53};
    boolean mPaused = true;
    private View mMainView;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    };
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            if (id == R.id.mode0) {
                sendCmd(0x0);
            } else if (id == R.id.mode1) {
                sendCmd(0x1);
            } else if (id == R.id.mode2) {
                sendCmd(0x2);
            } else if (id == R.id.mode3) {
                sendCmd(0x3);
            } else {
                return;
            }

            mHandler.removeMessages(0);
            mHandler.sendEmptyMessageDelayed(0, 5000);
        }
    };
    private BroadcastReceiver mReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.drive_mode_mingjue, container, false);

        setOnClick(R.id.mode0);
        setOnClick(R.id.mode1);
        setOnClick(R.id.mode2);
        setOnClick(R.id.mode3);
        return mMainView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
        unregisterListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPaused = false;
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, 5000);
        registerListener();
    }

    private void sendCanboxInfo(int d0) {

        byte[] buf = new byte[]{(byte) 0x90, 0x02, (byte) ((d0 & 0xff00) >> 8), (byte) (d0 & 0xff)};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void updateView(byte[] buf) {
        int index;
        switch (buf[0]) {
            case 0x53:

                index = ((buf[2] & 0xff));
                switch (index) {
                    case 0:
                        index = R.id.mode0;
                        break;
                    case 1:
                        index = R.id.mode1;
                        break;
                    case 2:
                        index = R.id.mode2;
                        break;
                    case 3:
                        index = R.id.mode3;
                        break;
                    default:
                        index = 0;
                        break;
                }

                setSelectedView(R.id.mode0, false);
                setSelectedView(R.id.mode1, false);
                setSelectedView(R.id.mode2, false);
                setSelectedView(R.id.mode3, false);
                setSelectedView(index, true);

                mHandler.removeMessages(0);
                mHandler.sendEmptyMessageDelayed(0, 5000);

                break;

        }
    }

    private void sendCmd(int cmd) {
        byte[] buf = new byte[]{(byte) 0xc6, 0x03, 0xb, (byte) (cmd), 0x0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void setSelectedView(int index, boolean sel) {
        if (index != 0) {
            TextView v = (TextView) mMainView.findViewById(index);
            if (v != null) {
                v.setSelected(sel);
            }
        }
    }

    private void setOnClick(int index) {
        if (index != 0) {
            View v = mMainView.findViewById(index);
            if (v != null) {
                v.setOnClickListener(mOnClickListener);
            }
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
