/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package com.canboxsetting.ac;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;

/**
 * This activity plays a video from a specified URI.
 */
public class HondaAirControl extends Activity {
    private static final String TAG = "HondaAirControl";
    private boolean mShow = false;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.honda_air_control);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        // System.exit(0);
    }

    private void sendCanboxInfo0xC6(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0xC6, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(this, buf);
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.on) {
            sendCanboxInfo0xC6(0xac, 0x1);
        } else if (id == R.id.off) {
            sendCanboxInfo0xC6(0xac, 0x2);
        } else if (id == R.id.canbus21_mode1) {
            sendCanboxInfo0xC6(0xac, 0x3);
        } else if (id == R.id.canbus21_mode3) {
            sendCanboxInfo0xC6(0xac, 0x4);
        } else if (id == R.id.canbus21_mode2) {
            sendCanboxInfo0xC6(0xac, 0x5);
        } else if (id == R.id.canbus21_mode4) {
            sendCanboxInfo0xC6(0xac, 0x6);
        } else if (id == R.id.point0) {
            sendCanboxInfo0xC6(0xad, 0x1);
        } else if (id == R.id.point1) {
            sendCanboxInfo0xC6(0xad, 0x2);
        } else if (id == R.id.point2) {
            sendCanboxInfo0xC6(0xad, 0x3);
        } else if (id == R.id.point3) {
            sendCanboxInfo0xC6(0xad, 0x4);
        } else if (id == R.id.point4) {
            sendCanboxInfo0xC6(0xad, 0x5);
        } else if (id == R.id.point5) {
            sendCanboxInfo0xC6(0xad, 0x6);
        } else if (id == R.id.point6) {
            sendCanboxInfo0xC6(0xad, 0x7);
        }
    }

    private void updateView(byte[] buf) {

        if ((buf[0] & 0xff) == 0x21) {
            if ((buf[6] & 0x40) != 0) {
                mShow = true;
            } else if (mShow) {
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        unregisterListener();
        mShow = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerListener();
        super.onResume();
    }

    private void unregisterListener() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
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
                    }
                }
            };
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction(MyCmd.BROADCAST_SEND_FROM_CAN);

            registerReceiver(mReceiver, iFilter);
        }
    }
}
