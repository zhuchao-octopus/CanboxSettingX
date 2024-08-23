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

package com.canboxsetting.anstar;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.R.array;
import com.canboxsetting.R.drawable;
import com.canboxsetting.R.id;
import com.canboxsetting.R.layout;
import com.canboxsetting.R.string;
import com.common.adapter.MyListViewAdapterCD;
import com.common.adapter.MyListViewAdapterRadio;
import com.common.util.AuxInUI;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * This activity plays a video from a specified URI.
 */
public class GMOnStarHiworldFragment extends MyFragment {

    private View mMainView;

    public TextView mDigit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.onstar, container, false);

        for (int i : BUTTON_ON_CLICK_DIAL) {
            View v = mMainView.findViewById(i);
            if (v != null) {
                v.setOnClickListener(mOnClickDial);
            }
        }

        for (int i : BUTTON_ON_CLICK) {
            View v = mMainView.findViewById(i);
            if (v != null) {
                v.setOnClickListener(mOnClick);
            }
        }

        mDigit = (TextView) mMainView.findViewById(R.id.tel_text);
        (mMainView.findViewById(R.id.del_onstar)).setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mDigit.setText("");
                return true;
            }
        });
        return mMainView;
    }

    private View.OnClickListener mOnClickDial = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            if (mStatus == 4) {
                byte b = 0;
                String s = ((TextView) v).getText().toString();

                b = (byte) s.charAt(0);

                sendCanboxInfo(0xba, 0x4, b);
            }

            mDigit.setText("" + mDigit.getText() + ((TextView) v).getText());

        }
    };

    private View.OnClickListener mOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            if (id == R.id.del_onstar) {
                String str = mDigit.getText().toString();
                if (str.length() > 0) {
                    mDigit.setText(str.substring(0, str.length() - 1));
                }
            } else if (id == R.id.dial_dialout) {
                onDail();
            } else if (id == R.id.dial_handup) {
                if (mStatus == 4) {
                    sendCanboxInfo(0xba, 0x2, 0);
                } else {
                    sendCanboxInfo(0xba, 0x3, 0);
                }
            }
        }
    };

    private void onDail() {
        //		if (mStatus == 0) {
        //			sendCanboxInfo(0x85, 0x10);
        //		}

        if (mStatus == 0 || mStatus == 1) {
            String num = mDigit.getText().toString();
            if (num != null && num.length() > 0) {
                byte[] buf = new byte[0x22];
                buf[0] = (byte) 0x20;
                buf[1] = (byte) 0xbb;
                int len = num.length();
                if (len > 0x20) {
                    len = 0x20;
                }

                int i = 0;
                for (; i < len; i++) {
                    buf[i + 2] = (byte) num.charAt(i);
                }

                BroadcastUtil.sendCanboxInfo(getActivity(), buf);
            }
        } else if (mStatus == 2) {
            sendCanboxInfo(0xba, 0x1, 0x0);
        }
    }

    private void sendCanboxInfo(int d0, int d1) {
        byte[] buf = new byte[]{(byte) d0, 0x01, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int cmd, int d0, int d1) {
        byte[] buf = new byte[]{0x2, (byte) cmd, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private static final int[] BUTTON_ON_CLICK_DIAL = new int[]{R.id.number_1, R.id.number_2, R.id.number_3, R.id.number_4, R.id.number_5, R.id.number_6, R.id.number_7, R.id.number_8, R.id.number_9, R.id.number_0, R.id.number_star, R.id.number_pound};
    private static final int[] BUTTON_ON_CLICK = new int[]{R.id.del_onstar, R.id.dial_dialout, R.id.dial_handup,};

    private int mStatus = -1;

    private void updateStatus(int status) {
        if (mStatus != status) {
            switch (status) {
                case 0:
                    getActivity().finish();
                    break;
                case 1:
                case 2:
                case 4:
                    BroadcastUtil.sendToCarServiceSetSource(getActivity(), MyCmd.SOURCE_AUX);
                    break;
            }

            mStatus = status;
        }
        String[] ss = getResources().getStringArray(R.array.onstar_states);
        if (status >= 0 && status < ss.length) {
            ((TextView) mMainView.findViewById(R.id.state_text)).setText(ss[status]);
        }

    }

    private void updateView(byte[] buf) {

        switch (buf[0]) {
            case (byte) 0xc3: {
                byte[] data = new byte[0x32];
                Util.byteArrayCopy(data, buf, 0, 2, data.length);
                String s = new String(data);
                ((TextView) mMainView.findViewById(R.id.wireless_port)).setText(getString(R.string.onstar_wireless_port, s));
                mMainView.findViewById(R.id.ll_wireless_model).setVisibility(View.VISIBLE);
            }
            break;
            case (byte) 0xc2: {
                byte[] data = new byte[0x6];
                Util.byteArrayCopy(data, buf, 0, 2, data.length);
                String s = new String(data);
                ((TextView) mMainView.findViewById(R.id.wireless_psd)).setText(getString(R.string.onstar_wireless_psd, s));
                mMainView.findViewById(R.id.ll_wireless_model).setVisibility(View.VISIBLE);
            }
            break;
            case (byte) 0xb1:
                //			switch (buf[2] & 0xff) {
                //			case 1:
                //			case 2:
                //			case 3:
                //				buf[2]++;
                //				break;
                //			case 4:
                //				buf[2] = 1;
                //				break;
                //			}
                updateStatus(buf[2] & 0xff);
                break;
            case (byte) 0xb4:
                String num = "";
                byte[] b = new byte[0x20];
                for (int i = 0; i < buf.length && i < b.length && (buf[2 + i] != 0); ++i) {
                    b[i] = buf[2 + i];
                }
                num = new String(b);
                mDigit.setText(num);
                break;
        }
    }

    private String getBCD(int c) {
        String s = null;
        if ((c & 0xf) == 0xF) {
            s = null;
        } else if ((c & 0xf) == 0xa) {
            s = "*";
        } else if ((c & 0xf) == 0xb) {
            s = "#";
        } else {
            s = "" + (c & 0xf);
        }
        return s;
    }

    private byte getBCD(char c) {
        byte b = 0;
        if (c >= '0' && c <= '9') {
            b = (byte) (c - '0');
        } else if (c == '*') {
            b = 0xA;
        } else if (c == '#') {
            b = 0xb;
        }
        return b;
    }

    @Override
    public void onPause() {
        unregisterListener();
        super.onPause();
    }

    @Override
    public void onResume() {
        registerListener();
        sendCanboxInfo(0x90, 0x09);

        sendCanboxInfo(0x90, 0x08);
        super.onResume();
    }

    private BroadcastReceiver mReceiver;

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
                                updateView(buf);
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

    private AuxInUI mAuxInUI;

    private int mSource = MyCmd.SOURCE_NONE;

    public boolean isCurrentSource() {
        return (mSource == MyCmd.SOURCE_AUX);
    }

}
