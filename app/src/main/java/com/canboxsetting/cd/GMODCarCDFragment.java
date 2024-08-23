/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use getActivity() file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.canboxsetting.cd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.R.id;
import com.common.utils.AuxInUI;
import com.common.utils.BroadcastUtil;
import com.common.utils.MachineConfig;
import com.common.utils.MyCmd;
import com.common.utils.Util;

/**
 * This activity plays a video from a specified URI.
 */
public class GMODCarCDFragment extends MyFragment {
    private static final String TAG = "JeepCarCDFragment";
    private static final int[] BUTTON_ON_CLICK = new int[]{R.id.home, R.id.navi, R.id.radio, R.id.back, R.id.bt, R.id.power, R.id.prev, R.id.next, R.id.key_vol_a, R.id.key_vol_m, R.id.key_sel_a, R.id.key_sel_m, R.id.ok, R.id.music};
    int mCarType = 0;
    private View mMainView;
    private GestureDetector mGestureDetector;
    private OnGestureListener mOnGestureListener = new OnGestureListener() {
        public void onLongPress(MotionEvent e) {
        }

        ;

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        ;

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        ;

        public void onShowPress(MotionEvent e) {
        }

        ;

        public boolean onDown(MotionEvent e) {
            return false;
        }

        ;

        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        ;
    };
    private OnTouchListener mCameraOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if (mGestureDetector != null) {
                return mGestureDetector.onTouchEvent(event);
            }
            return false;
        }
    };
    private boolean mFull = false;
    private OnDoubleTapListener mOnDoubleTapListener = new OnDoubleTapListener() {
        public boolean onDoubleTap(MotionEvent e) {
            toggleFullScreen();
            return false;
        }

        ;

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            sendXY((int) e.getX(), (int) e.getY());
            return false;
        }

        ;

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            // TODO Auto-generated method stub
            return false;
        }
    };
    private int m6DiskStatus = 0;
    private int mPauseUI = 0;
    private BroadcastReceiver mReceiver;
    private AuxInUI mAuxInUI;
    private int mSource = MyCmd.SOURCE_NONE;    private OnTouchListener mOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    int d0 = Integer.valueOf((String) v.getTag());

                    mHandler.removeMessages(0);
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(0, d0, 0), 2000);
                    sendCanboxInfo0x74(d0, 1);
                }
                break;
                case MotionEvent.ACTION_UP: {
                    mHandler.removeMessages(0);
                    int d0 = Integer.valueOf((String) v.getTag());
                    sendCanboxInfo0x74(d0, 0);
                }
                break;
            }
            return false;
        }
    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // setContentView(R.layout.jeep_car_cd_player);

    }    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mHandler.removeMessages(0);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(0, msg.arg1, 0), 100);
            sendCanboxInfo0x74(msg.arg1, 2);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        mMainView = inflater.inflate(R.layout.gm_od_player, container, false);


        showCamera();
        initPresentationUI();
        return mMainView;
    }

    private void initCarType() {
        String mCanboxType = MachineConfig.getPropertyForce(MachineConfig.KEY_CAN_BOX);

        String[] ss = mCanboxType.split(",");
        String value = ss[0];
        for (int i = 1; i < ss.length; ++i) {
            if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_CAR_TYPE)) {
                try {
                    mCarType = Integer.valueOf(ss[i].substring(1));
                } catch (Exception e) {

                }
            }
        }

        if (mCarType == 1) {

            mMainView.findViewById(R.id.dvd_menu).setVisibility(View.GONE);
        }
    }

    private void initPresentationUI() {
        for (int i : BUTTON_ON_CLICK) {
            View v = mMainView.findViewById(i);
            if (v != null) {
                v.setOnTouchListener(mOnTouchListener);
            }
        }

        mGestureDetector = new GestureDetector(mOnGestureListener);
        mGestureDetector.setOnDoubleTapListener(mOnDoubleTapListener);

        mMainView.findViewById(R.id.aux_main).setOnTouchListener(mCameraOnTouchListener);
    }

    private void sendXY(int x, int y) {
        int w = mMainView.findViewById(R.id.aux_main).getWidth();
        int h = mMainView.findViewById(R.id.aux_main).getHeight();
        if (w != 0 && h != 0) {
            x = x * 0x3fff / w;
            y = y * 0x3fff / h;
        }
        Log.d("ccf", x + ":" + y);
        byte[] buf = new byte[]{(byte) 0x75, 0x5, 0x1, (byte) (x & 0xff), (byte) ((x & 0xff00) >> 8), (byte) (y & 0xff), (byte) ((y & 0xff00) >> 8)};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x74(String tag, int d1) {

        int d0 = Integer.valueOf(tag);
        sendCanboxInfo0x74(d0, d1);
    }

    private void sendCanboxInfo0x74(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0x74, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x90(int d0) {
        byte[] buf = new byte[]{(byte) 0x90, 0x2, (byte) d0, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    public void onClick(View v) {
        if (v.getId() == id.pp) {
        }
    }

    private void setFullScreen() {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mMainView.findViewById(R.id.dvd_menu).setVisibility(View.GONE);

        mFull = true;
    }

    private void quitFullScreen() {
        final WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().setAttributes(attrs);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (mCarType != 1) {
            mMainView.findViewById(R.id.dvd_menu).setVisibility(View.VISIBLE);
        }

        mFull = false;
    }

    private void toggleFullScreen() {
        if (!mFull) {
            setFullScreen();
        } else {
            quitFullScreen();
        }
    }

    private void hideCamera() {
        if (mAuxInUI != null) {
            if (!mAuxInUI.mPause) {
                mAuxInUI.onPause();
            }
        }
    }

    private void showCamera() {
        if (mAuxInUI == null) {
            mAuxInUI = AuxInUI.getInstanse(getActivity(), mMainView.findViewById(R.id.aux_main), 0);
            mAuxInUI.onCreate();
        }
        if (mAuxInUI.mPause) {
            mAuxInUI.onResume();
        }
    }

    private void showDVD() {
        showCamera();
        mMainView.findViewById(R.id.cd).setVisibility(View.GONE);
        mMainView.findViewById(R.id.dvd).setVisibility(View.VISIBLE);
        mMainView.findViewById(R.id.dvd_menu).setVisibility(View.VISIBLE);
        mMainView.findViewById(R.id.disc_status).setVisibility(View.VISIBLE);
        mMainView.findViewById(R.id.dvd_lang_main).setVisibility(View.GONE);
    }

    private void setVisible(int id, int value) {
        View v = mMainView.findViewById(id);
        if (v != null) {
            v.setVisibility(value != 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onPause() {
        unregisterListener();
        hideCamera();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mPauseUI = 0;
    }

    @Override
    public void onResume() {
        registerListener();

        BroadcastUtil.sendToCarServiceSetSource(getActivity(), MyCmd.SOURCE_AUX);
        sendCanboxInfo0x90(0x62);
        Util.doSleep(30);
        sendCanboxInfo0x90(0x61);
        initCarType();
        super.onResume();
    }

    private void unregisterListener() {
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
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

                            } catch (Exception e) {
                                Log.d("aa", "!!!!!!!!" + e);
                            }
                        }
                    } else if (action.equals(MyCmd.BROADCAST_CAR_SERVICE_SEND)) {

                        int cmd = intent.getIntExtra(MyCmd.EXTRA_COMMON_CMD, 0);
                        switch (cmd) {
                            case MyCmd.Cmd.SOURCE_CHANGE:
                            case MyCmd.Cmd.RETURN_CURRENT_SOURCE:
                                int source = intent.getIntExtra(MyCmd.EXTRA_COMMON_DATA, 0);
                                if (mSource == MyCmd.SOURCE_AUX && source != MyCmd.SOURCE_AUX) {
                                    // sendCanboxInfo0xc7(0xE);
                                    // } else {
                                    // sendCanboxInfo0xc7(0x0);
                                }
                                mSource = source;
                                break;
                        }
                    }
                }
            };
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction(MyCmd.BROADCAST_SEND_FROM_CAN);
            iFilter.addAction(MyCmd.BROADCAST_CAR_SERVICE_SEND);

            getActivity().registerReceiver(mReceiver, iFilter);
        }
    }

    public boolean isCurrentSource() {
        return (mSource == MyCmd.SOURCE_AUX);
    }





}
