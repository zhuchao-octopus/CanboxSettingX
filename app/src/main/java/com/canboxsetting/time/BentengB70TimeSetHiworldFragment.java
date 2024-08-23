package com.canboxsetting.time;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;

public class BentengB70TimeSetHiworldFragment extends MyFragment {

    private View mMainView;
    private OnTouchListener mOnTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {
            if (arg1.getAction() == KeyEvent.ACTION_DOWN) {
                //				mHandler.removeMessages(0);
                int id = arg0.getId();
                if (id == R.id.hour_a) {
                    sendCanboxInfo(0x6, 0x1);
                } else if (id == R.id.hour_m) {
                    sendCanboxInfo(0x6, 0x2);
                } else if (id == R.id.min_a) {
                    sendCanboxInfo(0x7, 0x1);
                } else if (id == R.id.min_m) {
                    sendCanboxInfo(0x7, 0x2);
                } else if (id == R.id.set24) {
                    sendCanboxInfo(0x8, 0x1);
                } else if (id == R.id.set12) {
                    sendCanboxInfo(0x8, 0x2);
                }
            }
            return false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.bentengb70_timeset_hiworld, container, false);


        mMainView.findViewById(R.id.hour_a).setOnTouchListener(mOnTouchListener);
        mMainView.findViewById(R.id.hour_m).setOnTouchListener(mOnTouchListener);
        mMainView.findViewById(R.id.min_a).setOnTouchListener(mOnTouchListener);
        mMainView.findViewById(R.id.min_m).setOnTouchListener(mOnTouchListener);
        mMainView.findViewById(R.id.set24).setOnTouchListener(mOnTouchListener);
        mMainView.findViewById(R.id.set12).setOnTouchListener(mOnTouchListener);

        return mMainView;
    }

    private void sendCanboxInfo(int d0, int d1) {

        byte[] buf = new byte[]{0x2, (byte) 0x6e, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

}
