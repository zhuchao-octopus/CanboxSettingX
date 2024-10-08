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

package com.canboxsetting.radio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.common.adapter.MyListViewAdapterRadio;
import com.common.utils.AuxInUI;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.Util;

/**
 * This activity plays a video from a specified URI.
 */
public class Radio154 extends MyFragment {
    private static final String TAG = "Radio153";
    private final static int[] INIT_CMDS = {0x22, 0x21};
    private View mMainView;
    private ListView mListViewCD;
    private MyListViewAdapterRadio mMyListViewAdapter;
    private ListView mListViewPreset;
    private MyListViewAdapterRadio mMyListViewAdapterPreset;
    private int mFlashList = 0;
    private int mListIndex;
    private int mStatus;
    private boolean mPaused = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                byte[] buf = new byte[]{(byte) 0x1f, 0x1, (byte) (msg.what & 0xff)};
                BroadcastUtil.sendCanboxInfo(getActivity(), buf);
            }
        }
    };
    private BroadcastReceiver mReceiver;
    private AuxInUI mAuxInUI;
    private int mSource = MyCmd.SOURCE_NONE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.honda_radio, container, false);

        mListViewCD = (ListView) mMainView.findViewById(R.id.liststations);
        mListViewPreset = (ListView) mMainView.findViewById(R.id.listpreset);


        mMainView.findViewById(R.id.liststations).setVisibility(View.GONE);
        mMainView.findViewById(R.id.listpreset).setVisibility(View.VISIBLE);

        mListViewPreset.setVisibility(View.VISIBLE);
        initUI();
        return mMainView;
    }

    private void initUI() {
        if (mCarType == 0) {
            showUI(0);
        } else {
            showUI(1);
        }
    }

    private void showUI(int type) {
        //		if (mCarType == 0) {
        //			type = 0;
        //		}
        mMainView.findViewById(R.id.radio_function_button_scan).setVisibility(View.GONE);
        mMainView.findViewById(R.id.radio_fr).setVisibility(View.GONE);
        mMainView.findViewById(R.id.radio_ff).setVisibility(View.GONE);

        mMainView.findViewById(R.id.stations).setVisibility(View.GONE);
        mMainView.findViewById(R.id.preset).setVisibility(View.GONE);
        mMainView.findViewById(R.id.update_stations_list).setVisibility(View.GONE);
        mMainView.findViewById(R.id.line_am).setVisibility(View.VISIBLE);
        mMainView.findViewById(R.id.am).setVisibility(View.VISIBLE);
        mMainView.findViewById(R.id.fm).setVisibility(View.VISIBLE);
        mMainView.findViewById(R.id.fmam).setVisibility(View.GONE);
        mMainView.findViewById(R.id.radio_function_button_scan).setVisibility(View.GONE);
        if (type == 0) {
            mMainView.findViewById(R.id.other).setVisibility(View.GONE);
            mMainView.findViewById(R.id.radio).setVisibility(View.VISIBLE);
            //			mMainView.findViewById(R.id.fmam).setVisibility(View.VISIBLE);
            mMainView.findViewById(R.id.line1).setVisibility(View.VISIBLE);
            mMainView.findViewById(R.id.line2).setVisibility(View.VISIBLE);

        } else {
            mMainView.findViewById(R.id.other).setVisibility(View.VISIBLE);
            mMainView.findViewById(R.id.radio).setVisibility(View.GONE);
            mMainView.findViewById(R.id.fmam).setVisibility(View.GONE);
            mMainView.findViewById(R.id.line1).setVisibility(View.GONE);
            mMainView.findViewById(R.id.line2).setVisibility(View.GONE);
            //			mMainView.findViewById(R.id.radio_function_button_scan)
            //					.setVisibility(View.GONE);
        }
    }

    private void updateSaveList(int index, String name, int freq) {

        if (mMyListViewAdapter == null) {
            mMyListViewAdapter = new MyListViewAdapterRadio(getActivity(), R.layout.tl_list);
            mListViewCD.setAdapter(mMyListViewAdapter);

            mListViewCD.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View view, int postion, long id) {
                    int index = mMyListViewAdapter.getSelectIndex(postion);
                    if (index != -1) {

                        sendCanboxInfo0x83(0xb, index);

                    }
                }
            });

        }

        if (mMyListViewAdapter != null) {
            mMyListViewAdapter.addList(index, name, freq);
        }

    }

    private void updatePresetList(int index, String name, int freq) {

        if (mMyListViewAdapterPreset == null) {
            mMyListViewAdapterPreset = new MyListViewAdapterRadio(getActivity(), R.layout.tl_list);
            mListViewPreset.setAdapter(mMyListViewAdapterPreset);

            mListViewPreset.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View view, int postion, long id) {
                    int index = mMyListViewAdapterPreset.getSelectIndex(postion);
                    if (index != -1) {

                        sendCanboxInfo0x83(0xc, index + 1);

                    }
                }
            });

        }

        if (mMyListViewAdapterPreset != null) {
            mMyListViewAdapterPreset.addList(index, name, freq);
        }

    }

    private void sendCanboxInfo0x83(int d0) {
        sendCanboxInfo0x83(d0, 1);
    }

    private void sendCanboxInfo0xC5(int d0) {
        //		byte[] buf = new byte[] { (byte) 0xc5, 0x2, (byte) d0, 0 };
        //		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x83(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0xa1, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x90(int d0) {
        byte[] buf = new byte[]{(byte) 0x8f, 0x2, (byte) d0, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fmam) {
            if ((mStatus & 0x7) >= 3) {
                sendCanboxInfo0x83(5, 1);
            } else {
                sendCanboxInfo0x83(5, 2);
            }
        } else if (id == R.id.fm) {
            sendCanboxInfo0x83(5, 1);
        } else if (id == R.id.am) {
            sendCanboxInfo0x83(5, 2);
        } else if (id == R.id.radio_prev) {
            sendCanboxInfo0x83(2);
        } else if (id == R.id.radio_next) {
            sendCanboxInfo0x83(1);
        } else if (id == R.id.radio_ff) {
            sendCanboxInfo0x83(4);
        } else if (id == R.id.radio_fr) {
            sendCanboxInfo0x83(5);
        } else if (id == R.id.radio_function_button_scan) {
            if ((mStatus & 0x20) != 0) {
                sendCanboxInfo0x83(0xa, 1);
            } else {
                sendCanboxInfo0x83(0xa, 0);
            }
        } else if (id == R.id.radio_step_up_button) {
            sendCanboxInfo0x83(4);
        } else if (id == R.id.radio_step_down_button) {
            sendCanboxInfo0x83(3);
        } else if (id == R.id.update_stations_list) {
            if (mFlashList == 1) {
                mFlashList = 0;
            } else {
                mFlashList = 1;
            }
            sendCanboxInfo0x83(0xc, mFlashList);
        } else if (id == R.id.stations) {
            mMainView.findViewById(R.id.liststations).setVisibility(View.VISIBLE);
            mMainView.findViewById(R.id.listpreset).setVisibility(View.GONE);
        } else if (id == R.id.preset) {
            mMainView.findViewById(R.id.liststations).setVisibility(View.GONE);
            mMainView.findViewById(R.id.listpreset).setVisibility(View.VISIBLE);
        }
    }

    private void updateStatus(byte b) {
        mStatus = (b & 0xff);
        String s = "";
        if ((b & 0x80) != 0) {
            s = getActivity().getString(R.string.scaning);
        }

        ((TextView) mMainView.findViewById(R.id.status)).setText(s);

    }

    private void updateView(byte[] buf) {
        String s = "";
        switch (buf[0]) {
            case 0x21: {

                updateStatus(buf[2]);

                int freq = ((buf[3] & 0xff) << 8) | (buf[4] & 0xff);
                String s2;
                String s3;
                if ((buf[2] & 0x7) >= 3) {
                    s = "FM" + ((buf[2] & 0x7) - 2);
                    s3 = "MHz";
                    s2 = String.format("%d.%d", freq / 100, freq % 100);
                } else {
                    s = "AM" + ((buf[2] & 0x7));
                    s3 = "KHz";
                    s2 = freq + "";
                }
                ((TextView) mMainView.findViewById(R.id.freq_baud)).setText(s);
                ((TextView) mMainView.findViewById(R.id.freq_text)).setText(s2);
                ((TextView) mMainView.findViewById(R.id.freq_unit)).setText(s3);
            }
            break;
            case 0x22: {
                int num;
                //			if ((buf[2] & 0x80) == 0) {
                //				num = 12;
                //			} else {
                num = 6;
                //			}
                for (int i = 0; i < num; ++i) {
                    int freq = ((buf[3 + i * 2] & 0xff) << 8) | (buf[4 + i * 2] & 0xff);
                    if ((buf[2] & 0x80) == 0) {
                        s = String.format("%d.%d", freq / 100, freq % 100);
                    } else {
                        s = freq + "";
                    }


                    Log.d("ccfk", i + ":" + freq);

                    updatePresetList(i, s, freq);
                }

                break;
            }
            case 0x7b: {
                switch (buf[2]) {
                    case 1:
                    case 2:
                    case 3:
                        showUI(0);
                        break;
                    case 4:
                        s = "CD";
                        showUI(1);

                        String s1 = "";
                        String s2 = "";

                        s2 += (buf[3] & 0xff);
                        s1 += (buf[4] & 0xff);

                        mMainView.findViewById(R.id.common_repeat1_tag).setVisibility(View.GONE);
                        mMainView.findViewById(R.id.shuffle_tag).setVisibility(View.GONE);
                        if (buf[5] == 1) {

                            mMainView.findViewById(R.id.common_repeat1_tag).setVisibility(View.VISIBLE);
                        } else if (buf[5] == 2) {
                            mMainView.findViewById(R.id.shuffle_tag).setVisibility(View.VISIBLE);
                        } else if (buf[5] == 3) {
                            s1 += " " + getString(R.string.scaning);
                        }

                        ((TextView) mMainView.findViewById(R.id.num)).setText(s2 + "/" + s1);

                        String s3 = String.format("%02d:%02d", (buf[7] & 0xff), (buf[8] & 0xff));

                        ((TextView) mMainView.findViewById(R.id.time)).setText(s3);

                        break;
                    case 5:
                        s = "AUX";
                        showUI(1);
                        ((TextView) mMainView.findViewById(R.id.num)).setText("");
                        ((TextView) mMainView.findViewById(R.id.time)).setText("");
                        ((TextView) mMainView.findViewById(R.id.status)).setText("");
                        break;
                }
                ((TextView) mMainView.findViewById(R.id.title)).setText(s);
            }
            break;
        }
    }

    @Override
    public void onPause() {
        unregisterListener();
        mPaused = true;
        super.onPause();
        byte[] buf = new byte[]{(byte) 0x90, 0x2, 0x4, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    @Override
    public void onResume() {
        registerListener();
        mPaused = false;
        BroadcastUtil.sendToCarServiceSetSource(getActivity(), MyCmd.SOURCE_AUX);
        requestInitData();

        byte[] buf = new byte[]{(byte) 0x90, 0x2, 0x1, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
        super.onResume();
    }

    private void requestInitData() {
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 500));
        }
    }

    private void sendCanboxInfo(int cmd, int d0, int d1) {
        byte[] buf = new byte[]{(byte) cmd, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
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
                                Log.d("ffck", "" + Util.byte2HexStr(buf));
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

    public boolean isCurrentSource() {
        return (mSource == MyCmd.SOURCE_AUX);
    }

}
