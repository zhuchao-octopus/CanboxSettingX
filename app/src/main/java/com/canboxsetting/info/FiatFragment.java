package com.canboxsetting.info;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import com.canboxsetting.CarInfoActivity;
import com.canboxsetting.R;
import com.canboxsetting.R.id;
import com.canboxsetting.R.layout;
import com.common.util.BroadcastUtil;
import com.common.util.Kernel;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.SystemConfig;
import com.common.util.Util;
import com.common.util.shell.ShellUtils;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class FiatFragment extends Fragment {
    private static final String TAG = "FiatFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // addPreferencesFromResource(R.xml.gm_simple_info);

        registerListener();
    }

    private View mMainView;
    private TextView mTVTime;

    private ImageView mPP;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fiat_info, container, false);

        mTVTime = (TextView) mMainView.findViewById(R.id.time);
        mPP = (ImageView) mMainView.findViewById(R.id.pp);

        ((ImageView) mMainView.findViewById(R.id.pp)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mPlayStatus) {
                    mPlayStatus = false;
                    mPP.getDrawable().setLevel(0);
                    sendCanboxData((byte) 0x80);
                } else {
                    mPlayStatus = true;
                    mPP.getDrawable().setLevel(1);
                    sendCanboxData((byte) 0x81);
                }
            }
        });
        ((ImageView) mMainView.findViewById(R.id.next)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                sendCanboxData((byte) 0x3);
            }
        });
        ((ImageView) mMainView.findViewById(R.id.prev)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                sendCanboxData((byte) 0x4);
            }
        });
        return mMainView;
    }

    private boolean mPause = false;

    @Override
    public void onPause() {
        super.onPause();
        mPause = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPause = false;
        byte[] buf = new byte[]{(byte) 0xff};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterListener();
        if (mSetSource) {
            // BroadcastUtil.sendToCarServiceSetSource(getActivity(),
            // MyCmd.SOURCE_MX51);
        }
    }

    ;

    private void sendCanboxData(byte cmd) {
        byte[] buf = new byte[]{(byte) 0x92, 0x1, cmd};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void updateView(byte[] buf) {
        switch (buf[0]) {
            case 0x12:
                if ((buf[2] & 0x8) != 0) {
                    mMainView.findViewById(R.id.mic).setVisibility(View.VISIBLE);
                    mMainView.findViewById(R.id.phone).setVisibility(View.GONE);
                    mMainView.findViewById(R.id.usb).setVisibility(View.GONE);
                } else if ((buf[2] & 0x4) != 0) {
                    mMainView.findViewById(R.id.mic).setVisibility(View.GONE);
                    mMainView.findViewById(R.id.phone).setVisibility(View.VISIBLE);
                    mMainView.findViewById(R.id.usb).setVisibility(View.GONE);
                } else {

                    if ((buf[2] & 0x3) != 0) {
                        if (!mPause && CarInfoActivity.mCmd == 0) {
                            mSetSource = true;
                            BroadcastUtil.sendToCarServiceSetSource(getActivity(), MyCmd.SOURCE_CANBOX_MEDIA);
                        }
                        if ((buf[2] & 0x3) == 0x1) {
                            mPlayStatus = false;
                            mPP.getDrawable().setLevel(0);
                        } else {
                            mPlayStatus = true;
                            mPP.getDrawable().setLevel(1);
                        }
                        // mTVTime.setText("");
                        mMainView.findViewById(R.id.mic).setVisibility(View.GONE);
                        mMainView.findViewById(R.id.phone).setVisibility(View.GONE);
                        mMainView.findViewById(R.id.usb).setVisibility(View.VISIBLE);
                    } else {
                        if (!mPause) {
                            //						getActivity().finish();
                            // Kernel.doKeyEvent(Kernel.KEY_HOMEPAGE);
                        }
                    }

                    if (CarInfoActivity.mCmd == 1) {
                        getActivity().finish();
                    }

                }
                break;
            case 0x17:
                String s = String.format("%02d:%02d", (buf[2] & 0xff), (buf[3] & 0xff));
                mTVTime.setText(s);
                break;
        }
    }

    private boolean mSetSource = false;
    private boolean mPlayStatus = false;

    private BroadcastReceiver mReceiver;

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
