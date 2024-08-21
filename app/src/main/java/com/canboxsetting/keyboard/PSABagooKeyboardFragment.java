package com.canboxsetting.keyboard;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;

public class PSABagooKeyboardFragment extends PreferenceFragmentCompat {
    private static final String TAG = "OpelSettingsSimpleFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    private View mMainView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.psa_keyboard, container, false);

        View v;
        for (int i = 0; i < BUTTON_ID.length; ++i) {
            v = mMainView.findViewById(BUTTON_ID[i][0]);
            if (v != null) {
                v.setOnTouchListener(mOnTouchListener);
                v.setOnLongClickListener(mOnLongClickListener);
            }
        }

        return mMainView;
    }

    private void sendCanboxInfo(int d0) {
        byte[] buf = new byte[]{(byte) 0x91, 0x1, (byte) d0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }


    private int mKeyId;
    View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, android.view.MotionEvent event) {
            //			Log.d("allen3", "onKey!!");
            mKeyId = getKey(v.getId());
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mHandler.removeMessages(0);
                if (mKeyId != 0) {
                    sendCanboxInfo(mKeyId);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mHandler.removeMessages(0);
                sendCanboxInfo(0);
                mKeyId = 0;
            }

            return false;
        }

        ;
    };

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(0);
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mKeyId != 0) {
                sendCanboxInfo(mKeyId);
            }
            mHandler.sendEmptyMessageDelayed(0, 300);
        }
    };
    View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            // TODO Auto-generated method stub
            if (mKeyId != 0) {
                //				sendCanboxInfo(mKeyId, );
            }
            mHandler.sendEmptyMessageDelayed(0, 300);
            return false;
        }
    };

    private int getKey(int id) {
        int ret = 0;
        for (int i = 0; i < BUTTON_ID.length; ++i) {
            if (BUTTON_ID[i][0] == id) {
                ret = BUTTON_ID[i][1];
                break;
            }
        }
        return ret;
    }

    private final static int[][] BUTTON_ID = {{R.id.mode, 0x1}, {R.id.up, 0x5}, {R.id.menu, 0x3}, {R.id.left, 0x6}, {R.id.right, 0x8}, {R.id.ok, 0x7}, {R.id.dark, 0x2}, {R.id.down, 0x9}, {R.id.esc, 0x4},};


}
