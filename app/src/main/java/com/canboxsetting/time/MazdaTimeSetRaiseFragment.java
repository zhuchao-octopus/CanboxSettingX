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

public class MazdaTimeSetRaiseFragment extends MyFragment {

    private View mMainView;
    private OnTouchListener mOnTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {
            if (arg1.getAction() == KeyEvent.ACTION_DOWN) {
                //				mHandler.removeMessages(0);
                int id = arg0.getId();
                if (id == R.id.time_clock) {
                    sendCanboxInfo(0x20);
                } else if (id == R.id.time_h) {
                    sendCanboxInfo(0x40);
                } else if (id == R.id.time_m) {
                    sendCanboxInfo(0x80);
                }
            } else if (arg1.getAction() == KeyEvent.ACTION_UP) {
                sendCanboxInfo(0);
            }
            return false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.mazda_timeset_raise, container, false);

        mMainView.findViewById(R.id.time_clock).setOnTouchListener(mOnTouchListener);
        // mMainView.findViewById(R.id.time_set).setOnTouchListener(
        // mOnTouchListener);
        mMainView.findViewById(R.id.time_h).setOnTouchListener(mOnTouchListener);
        mMainView.findViewById(R.id.time_m).setOnTouchListener(mOnTouchListener);

        return mMainView;
    }
    //
    //	public void onPause() {
    //		mHandler.removeMessages(0);
    //	};
    //
    //	private Handler mHandler = new Handler() {
    //		public void handleMessage(Message msg) {
    //			byte[] buf = new byte[] { 0x4, 0x6, (byte) msg.arg1 };
    //			BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    //			mHandler.sendMessageDelayed(mHandler.obtainMessage(0, msg.arg1, 0), 200);
    //		};
    //	};

    private void sendCanboxInfo(int d0) {
        //		if (d0 != 0) {
        //			mHandler.sendMessageDelayed(mHandler.obtainMessage(0, d0, 0), 2000);
        //		} else {
        //			mHandler.removeMessages(0);
        //		}
        byte[] buf = new byte[]{0x4, 0x6, (byte) d0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

}
