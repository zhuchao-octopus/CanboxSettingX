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

package com.canboxsetting;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.canboxsetting.radio.MazdaRaiseRaidoFragment;
import com.common.utils.BroadcastUtil;
import com.common.utils.MachineConfig;
import com.common.utils.MyCmd;

/**
 * This activity plays a video from a specified URI.
 */
public class RadioActivity extends AppCompatActivity {
    private static final String TAG = "HondaAirControl";
    private FragmentManager mFragmentManager;
    private MyFragment mSetting;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.main);
        mFragmentManager = getSupportFragmentManager();

        String value = null;// AppConfig.getCanboxSetting();//
        // MachineConfig.getPropertyForce(MachineConfig.KEY_CAN_BOX);

        String mCanboxType = MachineConfig.getPropertyForce(MachineConfig.KEY_CAN_BOX);
        int mProVersion = 0;
        String mProIndex = null;
        if (mCanboxType != null) {
            String[] ss = mCanboxType.split(",");
            value = ss[0];
            try {
                for (int i = 1; i < ss.length; ++i) {
                    if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_PROTOCAL_VERSION)) {
                        mProVersion = Integer.parseInt(ss[i].substring(1));
                    } else if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_PROTOCAL_INDEX)) {
                        mProIndex = ss[i].substring(1);
                    }
                }
            } catch (Exception ignored) {
            }
        }

        if (mProVersion >= 3 && mProIndex != null) {
            Class<?> c = FragmentPro.getFragmentRadioByID(mProIndex);
            if (c != null) {
                try {
                    mSetting = (MyFragment) c.newInstance();
                } catch (Exception ignored) {
                }
            }
            if (mSetting == null) {
                finish();
                return;
            }
        } else {
            if (value != null) {
                if (value.equals(MachineConfig.VALUE_CANBOX_MAZDA_RAISE)) {
                    mSetting = new MazdaRaiseRaidoFragment();
                }
            }
        }
        // if(){
        //
        // } else {
        //
        // }
        if (mSetting == null) {
            finish();
            return;
        }

        replaceFragment(R.id.main, mSetting, false);
        registerListener();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        BroadcastUtil.sendToCarServiceSetSource(this, MyCmd.SOURCE_AUX);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterListener();
        BroadcastUtil.sendToCarServiceSetSource(this, MyCmd.SOURCE_MX51);
    }

    public void onClick(View v) {
        mSetting.onClick(v);
    }

    private void replaceFragment(int layoutId, Fragment fragment, boolean isAddStack) {
        if (fragment != null) {
            FragmentTransaction transation = mFragmentManager.beginTransaction();
            transation.replace(layoutId, fragment);
            if (isAddStack) {
                transation.addToBackStack(null);
            }
            transation.commit();
        }
    }

    public void doKeyControl(int code) {
        int id = -1;
        switch (code) {
            case MyCmd.Keycode.CH_UP:
            case MyCmd.Keycode.KEY_SEEK_NEXT:
            case MyCmd.Keycode.KEY_TURN_A:
            case MyCmd.Keycode.NEXT:
            case MyCmd.Keycode.KEY_DVD_UP:
            case MyCmd.Keycode.KEY_DVD_RIGHT:
                id = R.id.next;
                break;

            case MyCmd.Keycode.CH_DOWN:
            case MyCmd.Keycode.KEY_SEEK_PREV:
            case MyCmd.Keycode.KEY_TURN_D:
            case MyCmd.Keycode.PREVIOUS:
            case MyCmd.Keycode.KEY_DVD_DOWN:
            case MyCmd.Keycode.KEY_DVD_LEFT:
                id = R.id.prev;
                break;
            case MyCmd.Keycode.FAST_R:
                id = R.id.fr;
                break;
            case MyCmd.Keycode.FAST_F:
                id = R.id.ff;
                break;
            case MyCmd.Keycode.STOP:
                id = R.id.next;
                break;
            case MyCmd.Keycode.KEY_REPEAT:
                id = R.id.repeat;
                break;
            case MyCmd.Keycode.KEY_SHUFFLE:
                id = R.id.shuffle;
                break;
            case MyCmd.Keycode.KEY_REPEAT_ONE:

                break;

            case MyCmd.Keycode.PLAY_PAUSE:
                id = R.id.pp;
                break;

        }
        if (id != -1) {
            View v = findViewById(id);
            if (v != null) {
                onClick(v);
            }
        }
    }

    private void unregisterListener() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerListener() {
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(MyCmd.ACTION_KEY_PRESS)) {
                        if (mSetting.isCurrentSource()) {
                            int code = intent.getIntExtra(MyCmd.EXTRAS_KEY_CODE, 0);
                            doKeyControl(code);
                        }
                    }
                }
            };
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction(MyCmd.ACTION_KEY_PRESS);

            registerReceiver(mReceiver, iFilter);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mSetting.onBackKey()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
