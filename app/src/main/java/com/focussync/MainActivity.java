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

package com.focussync;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MachineConfig;
import com.common.utils.MyCmd;
import com.common.utils.Util;

/**
 * This activity plays a video from a specified URI.
 */
public class MainActivity extends Activity {
    private static final String TAG = "FocusSyncMainActivity";
    WakeLock mWakeLock;
    private int mCarType = 0;
    private AuxInUI mUI;
    private boolean mSyncVisible = false;
    private int mKeyboardIndex = 0;
    private int[] mIcon = new int[256];
    private BroadcastReceiver mReceiver;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterListener();
        sendCanboxControl(0x82);
    }

    private void updateCarType() {
        String value = MachineConfig.getPropertyForce(MachineConfig.KEY_CAN_BOX);


        if (value != null) {
            String[] ss = value.split(",");
            value = ss[0];

            for (int i = 1; i < ss.length; ++i) {
                if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_CAR_TYPE)) {
                    try {
                        mCarType = Integer.valueOf(ss[i].substring(1));
                    } catch (Exception e) {

                    }
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
        updateCarType();
        if (mCarType == 2 || mCarType == 3) {
            setContentView(R.layout.main_sync3);
            mUI = AuxInUI.getInstanse(this, findViewById(R.id.main), 0);
            mUI.onCreate();
        } else {
            setContentView(R.layout.sync_main);
        }


        registerListener();

        sendCanboxInfo(0x78, 0x0);
        // int v = getIntent().getIntExtr ("value", 0);
        // if (v != 0) {
        // syncVisisble(true);
        // }

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        // 获取当前壁纸
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        findViewById(R.id.main).setBackground(wallpaperDrawable);
        initIcon();
    }

    @Override
    public void onResume() {
        if (mSyncVisible) {
            BroadcastUtil.sendToCarServiceSetSource(this, MyCmd.SOURCE_AUX);
        }
        super.onResume();

        if (mUI != null) {
            mUI.onResume();
            PowerManager pManager = ((PowerManager) getSystemService(POWER_SERVICE));
            mWakeLock = pManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, this.getPackageName());
            mWakeLock.acquire();
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (mUI != null) mUI.onPause();

        if (null != mWakeLock) {
            mWakeLock.release();
        }
    }

    private void syncVisisble(boolean b) {
        if (b) {

            BroadcastUtil.sendToCarServiceSetSource(this, MyCmd.SOURCE_AUX);

            findViewById(R.id.mask).setVisibility(View.GONE);
            sendCanboxControl(0x83);
            Util.doSleep(5);
            sendCanboxInfo(0x51, 0x0);
            Util.doSleep(5);
            sendCanboxInfo(0x50, 0x0);

        } else {
            findViewById(R.id.mask).setVisibility(View.VISIBLE);
        }
        mSyncVisible = b;
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.menu) {
            sendCanboxControl(0x2);
        } else if (id == R.id.phone) {
            sendCanboxControl(0x3);
        } else if (id == R.id.mic) {
            sendCanboxControl(0x1);
        } else if (id == R.id.music) {
            sendCanboxControl(0x1b);
        } else if (id == R.id.info) {
            sendCanboxControl(0x6);
        } else if (id == R.id.button1) {
            sendCanboxControl(0x1c);
        } else if (id == R.id.button2) {
            sendCanboxControl(0x12);
        } else if (id == R.id.button3) {
            sendCanboxControl(0x1e);
        } else if (id == R.id.button4) {
            sendCanboxControl(0x1f);
        } else if (id == R.id.up) {
            sendCanboxControl(0x0a);
        } else if (id == R.id.down) {
            sendCanboxControl(0x0b);
        } else if (id == R.id.left) {
            sendCanboxControl(0x19);
        } else if (id == R.id.right) {
            sendCanboxControl(0x1a);
        } else if (id == R.id.ok) {
            sendCanboxControl(0x0c);
        } else if (id == R.id.prev) {
            sendCanboxControl(0x08);
        } else if (id == R.id.next) {
            sendCanboxControl(0x09);
        } else if (id == R.id.num0) {
            sendCanboxControl(0x0d);
        } else if (id == R.id.num1) {
            sendCanboxControl(0x0e);
        } else if (id == R.id.num2) {
            sendCanboxControl(0x0f);
        } else if (id == R.id.num3) {
            sendCanboxControl(0x10);
        } else if (id == R.id.num4) {
            sendCanboxControl(0x11);
        } else if (id == R.id.num5) {
            sendCanboxControl(0x12);
        } else if (id == R.id.num6) {
            sendCanboxControl(0x13);
        } else if (id == R.id.num7) {
            sendCanboxControl(0x14);
        } else if (id == R.id.num8) {
            sendCanboxControl(0x15);
        } else if (id == R.id.num9) {
            sendCanboxControl(0x16);
        } else if (id == R.id.num_x) {
            sendCanboxControl(0x17);
        } else if (id == R.id.num_j) {
            sendCanboxControl(0x18);
        } else if (id == R.id.dial) {
            sendCanboxControl(0x5);
        } else if (id == R.id.hang) {
            sendCanboxControl(0x4);
        } else if (id == R.id.list1) {
            sendCanboxControl(0x91);
        } else if (id == R.id.list2) {
            sendCanboxControl(0x92);
        } else if (id == R.id.list3) {
            sendCanboxControl(0x93);
        } else if (id == R.id.list4) {
            sendCanboxControl(0x94);
        } else if (id == R.id.list5) {
            sendCanboxControl(0x95);
        } else if (id == R.id.speech) {
            sendCanboxControl(0x1);
        } else if (id == R.id.app) {
            sendCanboxControl(0x20);
        } else if (id == R.id.pp) {
            sendCanboxControl(0x22);
        } else if (id == R.id.repeat) {
            sendCanboxControl(0x21);
        } else if (id == R.id.media) {
            sendCanboxControl(0x81);
        } else if (id == R.id.random) {
            sendCanboxControl(0x7);
        } else if (id == R.id.keyboard) {
            toggleKeyboard();
        }
    }

    private void toggleKeyboard() {
        if (mKeyboardIndex == 0) {
            mKeyboardIndex = 1;

            findViewById(R.id.keyboard1).setVisibility(View.VISIBLE);
            findViewById(R.id.keyboard2).setVisibility(View.GONE);
        } else {
            mKeyboardIndex = 0;

            findViewById(R.id.keyboard1).setVisibility(View.GONE);
            findViewById(R.id.keyboard2).setVisibility(View.VISIBLE);
        }
    }

    // private void sendCanboxInfo(int d0, int d1, int d2) {
    //
    // byte[] buf = new byte[] { (byte) 0xc6, 0x02, (byte) d0, (byte) d1,
    // (byte) d2 };
    // BroadcastUtil.sendCanboxInfo(this, buf);
    // }

    private void sendCanboxControl(int d1) {

        byte[] buf = new byte[]{(byte) 0xc6, 0x02, (byte) 0xa1, (byte) d1};
        BroadcastUtil.sendCanboxInfo(this, buf);
    }

    private void sendCanboxInfo(int d0, int d1) {

        byte[] buf = new byte[]{(byte) 0x90, 0x02, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(this, buf);
    }

    private void doData(byte[] buf) {
        switch (buf[0]) {
            case 0x78:
                showIcon(buf);
                break;
            case 0x50:
                setListHilight(buf);
                break;
            case 0x51:

                switch (buf[2]) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        setListText(buf[2] - 1, buf);
                        break;
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                        setListText(buf[2] - 6, buf);
                        break;

                    case 11:
                    case 12:
                    case 13:
                    case 14:
                        setButtonText(buf[2] - 11, buf);
                        break;
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                        setButtonText(buf[2] - 15, buf);
                        break;
                }
                break;
            case 0x79:
                if (buf[2] > 0 && buf[2] < 5) {

                    syncVisisble(true);
                }
                //			else {
                //				syncVisisble(false);
                //			}
                //			if (buf[2] == 5) {
                //
                //				// syncVisisble(false);
                //				sendCanboxControl(0x82);
                //				finish();
                //			}
                break;
        }
    }

    private void showIcon(byte[] buf) {
        if ((buf[2] & 0xff) == 0) {
            syncVisisble(false);
        } else {
            syncVisisble(true);
        }

        if ((buf[3] & 0x1) != 0) {
            findViewById(R.id.sync).setVisibility(View.VISIBLE);
        } else {

            findViewById(R.id.sync).setVisibility(View.GONE);
        }

        if ((buf[3] & 0x2) != 0) {
            findViewById(R.id.bt).setVisibility(View.VISIBLE);
        } else {

            findViewById(R.id.bt).setVisibility(View.GONE);
        }
        if ((buf[3] & 0x8) != 0) {
            findViewById(R.id.msg).setVisibility(View.VISIBLE);
        } else {

            findViewById(R.id.msg).setVisibility(View.GONE);
        }

        if ((buf[3] & 0x10) != 0) {
            findViewById(R.id.mic_icon).setVisibility(View.VISIBLE);
        } else {

            findViewById(R.id.mic_icon).setVisibility(View.GONE);
        }

        if ((buf[3] & 0x20) != 0) {
            findViewById(R.id.phone_icon).setVisibility(View.VISIBLE);
        } else {

            findViewById(R.id.phone_icon).setVisibility(View.GONE);
        }
        if ((buf[3] & 0x40) != 0) {
            findViewById(R.id.info_icon).setVisibility(View.VISIBLE);
        } else {

            findViewById(R.id.info_icon).setVisibility(View.GONE);
        }

        int icon = 0;
        switch (buf[4] & 0xf) {
            case 0:
                icon = R.drawable.s0;
                break;
            case 1:
                icon = R.drawable.s1;
                break;
            case 2:
                icon = R.drawable.s2;
                break;
            case 3:
                icon = R.drawable.s3;
                break;
            case 4:
                icon = R.drawable.s4;
                break;
        }
        if (icon == 0) {
            findViewById(R.id.signal).setVisibility(View.GONE);
        } else {
            findViewById(R.id.signal).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.signal)).setImageDrawable(this.getResources().getDrawable(icon));
        }

        icon = 0;
        switch ((buf[4] & 0xf0) >> 4) {
            case 0:
                icon = R.drawable.b0;
                break;
            case 1:
                icon = R.drawable.b1;
                break;
            case 2:
                icon = R.drawable.b2;
                break;
            case 3:
                icon = R.drawable.b3;
                break;
            case 4:
                icon = R.drawable.b4;
                break;
        }
        if (icon == 0) {
            findViewById(R.id.battery).setVisibility(View.GONE);
        } else {
            findViewById(R.id.battery).setVisibility(View.VISIBLE);

            ((ImageView) findViewById(R.id.battery)).setImageDrawable(this.getResources().getDrawable(icon));
        }

    }

    private void setButtonText(int index, byte[] buf) {
        int id = R.id.button1 + index;
        Button tv = (Button) findViewById(id);
        if (tv == null) {
            return;
        }

        String s = "";

        int level = (buf[3] & 0xf);
        int drawable_id = R.drawable.list_button0;
        int text_color = 0;
        int icon = 0;
        switch (level) {
            case 0:
                text_color = Color.WHITE;
                drawable_id = R.drawable.list_button0;
                s = "--";
                break;
            case 2:
                text_color = Color.WHITE;
                drawable_id = R.drawable.list_button0;
                s = "";
                break;
            case 3:
                text_color = Color.WHITE;
                drawable_id = R.drawable.list_button1;

                s = "";
                break;
            case 0xa:
                text_color = Color.WHITE;
                drawable_id = R.drawable.list_button0;
                break;
            case 0xb:
                text_color = Color.GRAY;
                drawable_id = R.drawable.list_button0;
                break;
        }

        tv.setBackground(this.getResources().getDrawable(drawable_id));
        tv.invalidate();

        if (text_color != 0) {
            tv.setTextColor(text_color);
        }

        // if (level == 0xa || level == 0xb) {
        try {
            if (buf.length > 7) {
                byte[] text = new byte[buf.length - 6];

                Util.byteArrayCopy(text, buf, 0, 6, text.length);
                s = new String(text, "UNICODE");

            }
        } catch (Exception e) {

        }

        if (s.length() > 1) {

            char c = s.charAt(s.length() - 1);
            if (c == 65533) {
                s = s.substring(0, s.length() - 1);
            }

        } else if (level == 0x2 || level == 0x3) {
            switch (buf[4] & 0xff) {
                // case 18:
                // s = "MUTE";
                // break;
                // case 207:
                // case 208:
                // s = "PLAY/PAUSE";
                // break;
                default:
                    s = "*";
                    break;
            }
        }
        // Log.d("sync", "icon" + (buf[4] & 0xff) + ":" + (buf[5] & 0xff));
        // for test
        // }

        tv.setText(s);

    }

    private void setListHilight(byte[] buf) {
        int index = 0;
        if (buf[3] >= 1 && buf[3] <= 5) {
            index = buf[3];
        } else if (buf[5] >= 1 && buf[5] <= 5) {

            index = buf[5];
        }

        if (index != 0) {

            // int id = R.id.list1 + index - 1;
            // TextView tv = (TextView) findViewById(id);
            // if (tv != null) {
            // tv.setBackground(this.getResources().getDrawable(
            // R.drawable.list_button1));
            // }

            for (int i = 0; i < 5; ++i) {
                int id = R.id.list1 + i;
                TextView tv = (TextView) findViewById(id);
                if (tv != null) {
                    if (i == (index - 1)) {
                        tv.setBackground(this.getResources().getDrawable(R.drawable.list_button1));
                    } else {

                        tv.setBackground(this.getResources().getDrawable(R.drawable.list_button0));
                    }
                }
            }
        }
    }

    private void setListText(int index, byte[] buf) {
        int id = R.id.list1 + index;
        TextView tv = (TextView) findViewById(id);
        if (tv == null) {
            return;
        }

        String s = "";

        if ((buf[3] & 0x10) == 0) {

            tv.setClickable(false);
        } else {
            tv.setClickable(true);

        }

        try {
            if (buf.length > 6) {
                byte[] text = new byte[buf.length - 6];

                Util.byteArrayCopy(text, buf, 0, 6, text.length);
                s = new String(text, "UNICODE");

            }
        } catch (Exception e) {

        }

        if (s.length() > 1) {

            char c = s.charAt(s.length() - 1);
            if (c == 65533) {
                s = s.substring(0, s.length() - 1);
            }

        }
        tv.setText(s);

        int level = (buf[3] & 0xf);
        int drawable_id = R.drawable.list_button0;
        int text_color = 0;
        switch (level) {
            case 0:
                text_color = Color.WHITE;
                drawable_id = R.drawable.list_button0;
                break;
            case 1:
                text_color = Color.GRAY;
                drawable_id = R.drawable.list_button1;
                break;
            case 2:
                text_color = Color.GRAY;
                drawable_id = R.drawable.list_button0;
                break;
            case 3:
                text_color = Color.GRAY;
                drawable_id = R.drawable.list_button2;
                break;
            case 4:
                text_color = Color.GRAY;
                drawable_id = R.drawable.list_button0;
                break;
        }

        tv.setBackground(this.getResources().getDrawable(drawable_id));

        if (text_color != 0) {
            tv.setTextColor(text_color);
        }

        // id = R.id.list_left1 + index;
        int icon = 0;
        Drawable d = null;
        // ImageView iv = (ImageView) findViewById(id);

        if ((buf[4] & 0xff) > 0) {
            icon = mIcon[(buf[4] & 0xff)];
        }
        if (icon != 0) {
            d = this.getResources().getDrawable(icon);
            d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
        }

        // if (d != null) {
        // iv.setImageDrawable(d);
        // iv.setVisibility(View.VISIBLE);
        // } else {
        // iv.setVisibility(View.INVISIBLE);
        // }

        // id = R.id.list_right1 + index;
        icon = 0;
        Drawable d2 = null;
        // iv = (ImageView) findViewById(id);

        if ((buf[5] & 0xff) > 0) {
            icon = mIcon[(buf[5] & 0xff)];// R.drawable.music;
        }
        if (icon != 0) {
            d2 = this.getResources().getDrawable(icon);
            d2.setBounds(0, 0, d2.getMinimumWidth(), d2.getMinimumHeight());
        }

        // if (d2 != null) {
        // iv.setImageDrawable(d);
        // iv.setVisibility(View.VISIBLE);
        // } else {
        // iv.setVisibility(View.INVISIBLE);
        // }

        tv.setCompoundDrawables(d, null, d2, null);

        Log.d("sync", index + " icon " + (buf[4] & 0xff) + ":" + (buf[5] & 0xff));
    }

    private void initIcon() {

        mIcon[40] = R.drawable.icon_01;

        mIcon[39] = R.drawable.icon_02;
        mIcon[145] = R.drawable.icon_03;
        mIcon[147] = R.drawable.icon_04;

        mIcon[214] = R.drawable.icon_05;
        mIcon[213] = R.drawable.icon_06;

        mIcon[17] = R.drawable.icon_07;

        mIcon[118] = R.drawable.icon_08;
        mIcon[93] = R.drawable.icon_09;
        mIcon[94] = R.drawable.icon_09;

        mIcon[76] = R.drawable.icon_10;
        mIcon[130] = R.drawable.icon_11;
        mIcon[37] = R.drawable.icon_12;

        mIcon[78] = R.drawable.icon_13;
        mIcon[79] = R.drawable.icon_14;
        mIcon[80] = R.drawable.icon_15;
        mIcon[81] = R.drawable.icon_16;

        mIcon[83] = R.drawable.icon_17;

        mIcon[84] = R.drawable.icon_18;

        mIcon[87] = R.drawable.icon_19;
        mIcon[88] = R.drawable.icon_20;
        mIcon[89] = R.drawable.icon_21;

        mIcon[90] = R.drawable.icon_22;

        mIcon[92] = R.drawable.icon_23;

        mIcon[3] = R.drawable.icon_24;

        mIcon[41] = R.drawable.icon_24;

        mIcon[121] = R.drawable.icon_25;

        mIcon[21] = R.drawable.icon_26;

        mIcon[129] = R.drawable.icon_27;

        mIcon[119] = R.drawable.icon_28;

        mIcon[9] = R.drawable.icon_29;

        mIcon[204] = R.drawable.icon_30;

        mIcon[205] = R.drawable.icon_30;

        mIcon[31] = R.drawable.icon_31;

        mIcon[11] = R.drawable.icon_32;
        mIcon[28] = R.drawable.icon_33;
        mIcon[73] = R.drawable.icon_34;
        mIcon[229] = R.drawable.icon_35;

        mIcon[38] = R.drawable.icon_36;
        mIcon[37] = R.drawable.icon_37;

        mIcon[103] = R.drawable.icon_38;
        mIcon[104] = R.drawable.icon_39;
        mIcon[105] = R.drawable.icon_40;
        mIcon[106] = R.drawable.icon_41;
        mIcon[107] = R.drawable.icon_42;

        mIcon[109] = R.drawable.icon_38;
        mIcon[110] = R.drawable.icon_39;
        mIcon[111] = R.drawable.icon_40;
        mIcon[112] = R.drawable.icon_41;
        mIcon[113] = R.drawable.icon_42;

        mIcon[247] = R.drawable.icon_43;
        mIcon[248] = R.drawable.icon_44;
        mIcon[249] = R.drawable.icon_45;
        mIcon[250] = R.drawable.icon_46;
        mIcon[251] = R.drawable.icon_48;

        mIcon[215] = R.drawable.icon_49;
        mIcon[216] = R.drawable.icon_50;
        mIcon[217] = R.drawable.icon_51;
        mIcon[218] = R.drawable.icon_52;
        mIcon[219] = R.drawable.icon_53;
        mIcon[220] = R.drawable.icon_54;
        mIcon[221] = R.drawable.icon_55;
        mIcon[222] = R.drawable.icon_56;
        mIcon[223] = R.drawable.icon_57;
        mIcon[224] = R.drawable.icon_58;
        // mIcon[64] = R.drawable.icon_24;
        // mIcon[65] = R.drawable.icon_24;
        // mIcon[67] = R.drawable.icon_24;
        // mIcon[68] = R.drawable.icon_24;
        // mIcon[69] = R.drawable.icon_24;

        mIcon[75] = R.drawable.icon_59;
        // mIcon[77] = R.drawable.icon_24;

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
                                doData(buf);
                            } catch (Exception e) {
                                Log.d(TAG, "doData Exception:" + buf);
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


    //focus 3

}
