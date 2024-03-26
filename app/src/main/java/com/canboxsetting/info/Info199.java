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
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
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
import android.text.InputType;
import android.text.format.DateFormat;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
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

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.R.id;
import com.canboxsetting.R.layout;
import com.canboxsetting.R.string;
import com.common.util.AppConfig;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.SystemConfig;
import com.common.util.Util;
import com.common.util.shell.ShellUtils;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class Info199 extends MyFragment {
    private static final String TAG = "GMInfoSimpleFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // addPreferencesFromResource(R.xml.gm_simple_info);

    }

    private View mMainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.opel_haozheng_info, container, false);

        mMainView.findViewById(R.id.driving_page1).setOnClickListener(mOnClickListener);
        mMainView.findViewById(R.id.driving_page2).setOnClickListener(mOnClickListener);
        mMainView.findViewById(R.id.driving_page3).setOnClickListener(mOnClickListener);

        mMainView.findViewById(R.id.fuelclear1).setOnClickListener(mOnClickClearListener);
        mMainView.findViewById(R.id.fuelclear2).setOnClickListener(mOnClickClearListener);
        // mMainView.findViewById(R.id.booking_mileage).setOnClickListener(
        // mOnClickClearListener);

        showUI(R.id.driving_page1);

        return mMainView;
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListener();

        sendCanboxInfo90(0x1);
        Util.doSleep(10);
        sendCanboxInfo90(0x7);
        Util.doSleep(10);
        sendCanboxInfo90(0xb);
        Util.doSleep(10);
        sendCanboxInfo90(0x12);

    }

    private void showBookingMileageDialog() {
        final InputMethodManager mInputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        final EditText ev = new EditText(getActivity());
        final Toast t = Toast.makeText(getActivity(), getResources().getString(R.string.booking_mileage_rang), Toast.LENGTH_SHORT);
        AlertDialog ad = new AlertDialog.Builder(getActivity()).setTitle(R.string.booking_mileage_rang).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String str = ev.getText().toString().toLowerCase();
                try {
                    int value = Integer.valueOf(str);
                    if (value >= 0 && value <= 3000) {

                        byte[] buf = new byte[]{
                                (byte) 0x82, 0x06, 0, 0, 0, (byte) 0x80, (byte) ((value & 0xff00) >> 8), (byte) ((value & 0xff))
                        };
                        BroadcastUtil.sendCanboxInfo(getActivity(), buf);

                    } else {
                        t.show();
                    }
                } catch (Exception e) {

                }

            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).create();
        ;
        // ad.setOnCancelListener(new OnCancelListener() {
        // public void onCancel(DialogInterface dialog) {
        // finish();
        // }
        // });

        ad.setView(ev);
        ad.show();

        ev.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        ev.setInputType(InputType.TYPE_CLASS_NUMBER);
        // ev.setTransformationMethod(PasswordTransformationMethod.getInstance());

        ad.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                mInputManager.hideSoftInputFromWindow(ev.getWindowToken(), 0);
            }
        });
    }

    OnClickListener mOnClickClearListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            int id = v.getId();
            if (id == R.id.fuelclear1) {// cmd82 |= 0x40;
                // cmd82 &= ~0x20;
                cmd82 = 3;
            } else if (id == R.id.fuelclear2) {
                cmd82 = 4;
                // cmd82 |= 0x20;
                // cmd82 &= ~0x40;
            } else if (id == R.id.booking_mileage) {
                showBookingMileageDialog();

                return;
            }
            sendCanboxInfo82((byte) cmd82);
        }
    };
    OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            showUI(v.getId());
        }
    };

    private int cmd82 = 0;
    private int mCurUI = 0;

    private void showUI(int id) {
        // int cmd82 = 0;
        int cmd90 = 0;
        mCurUI = id;
        if (id == R.id.driving_page1) {
            mMainView.findViewById(R.id.driving_page1_layout).setVisibility(View.VISIBLE);
            mMainView.findViewById(R.id.driving_page2_layout).setVisibility(View.GONE);
            mMainView.findViewById(R.id.driving_page3_layout).setVisibility(View.GONE);
            cmd82 = 0;
            cmd90 = 0x12;
        } else if (id == R.id.driving_page2) {
            mMainView.findViewById(R.id.driving_page2_layout).setVisibility(View.VISIBLE);
            mMainView.findViewById(R.id.driving_page1_layout).setVisibility(View.GONE);
            mMainView.findViewById(R.id.driving_page3_layout).setVisibility(View.GONE);
            cmd82 = 1;
            cmd90 = 0x13;
        } else if (id == R.id.driving_page3) {
            mMainView.findViewById(R.id.driving_page3_layout).setVisibility(View.VISIBLE);
            mMainView.findViewById(R.id.driving_page2_layout).setVisibility(View.GONE);
            mMainView.findViewById(R.id.driving_page1_layout).setVisibility(View.GONE);
            cmd82 = 2;
            cmd90 = 0x14;
        }

        // sendCanboxInfo82((byte) cmd82);
        mHandler.sendEmptyMessageDelayed(cmd90, 150);
    }

    private void sendCanboxInfo82(byte d0) {

        // byte[] buf = new byte[] { 0x4, (byte)0x8f, d0 };
        // BroadcastUtil.sendCanboxInfo(getActivity(), buf);
        byte[] buf = new byte[]{(byte) 0x82, 0x1, (byte) d0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo90(int d0) {
        byte[] buf = new byte[]{(byte) 0x90, 1, (byte) d0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
        // byte[] buf = new byte[] { (byte) 0x90, 0x4, (byte) d0, 0, 0, 0 };
        // BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // mHandler.removeMessages(msg.what);
            // mHandler.sendEmptyMessageDelayed(msg.what, 700);
            sendCanboxInfo90(msg.what);
        }
    };

    private final static String TITLE[] = {
            " ", "BC:", "Settings:", "Sound:", "stations:", "store:", "Tracks:", "Extras:",
    };

    private String mTitle0 = "";

    private String getBufString(byte[] buf, int s, int len) {
        byte b[] = new byte[len];
        Util.byteArrayCopy(b, buf, 0, s, len);

        String str = "";
        try {
            str = (new String(b, "UNICODE"));
        } catch (Exception e) {

        }
        return str;

    }

    private void updateView(byte[] buf) {

        String temp = "";
        int i;
        try {
            switch (buf[0]) {
                case 1:
                    temp = "POWER:";
                    if ((buf[2] & 0x80) == 0) {
                        temp += "  OFF";
                    } else {
                        temp += "  ON";
                    }
                    ((TextView) mMainView.findViewById(R.id.title0)).setText(temp);

                    mTitle0 = TITLE[((buf[2] & 0x70) >> 4)];
                    break;
                case 7:
                    temp = getBufString(buf, 2, buf[1]);
                    ((TextView) mMainView.findViewById(R.id.title1)).setText(mTitle0 + "  " + temp);
                    break;
                case 0xb:
                    temp = TITLE[((buf[3] & 0xf)) - 0xa + 2];
                    temp = temp + "  " + getBufString(buf, 4, buf[1] - 2);
                    ((TextView) mMainView.findViewById(R.id.title2)).setText(temp);
                    break;
                case 0x12:

                    i = ((int) buf[4] & 0xff) * 256 + ((int) buf[5] & 0xff);
                    if (i == 0xffff) {
                        temp = "--";
                    } else {
                        temp = String.format("%d.%d L/100KM", i / 10, i % 10);
                    }

                    ((TextView) mMainView.findViewById(R.id.fulecons1)).setText(temp);

                    i = ((int) buf[2] & 0xff) * 256 + ((int) buf[3] & 0xff);
                    if (i == 0xffff) {
                        temp = "--";
                    } else {
                        temp = String.format("%d.%d KM", i / 10, i % 10);
                    }

                    ((TextView) mMainView.findViewById(R.id.driving_mileage)).setText(temp);

                    break;

                case 0x13:
                    i = (int) (((buf[2] & 0xff) << 24) | ((buf[3] & 0xff) << 16) | ((buf[4] & 0xff) << 8) | ((buf[5] & 0xff) << 0));
                    temp = String.format("%d.%d KM", i / 10, i % 10);

                    ((TextView) mMainView.findViewById(R.id.mileage1)).setText(temp);

                    i = (int) (((buf[7] & 0xff) << 24) | ((buf[8] & 0xff) << 16) | ((buf[9] & 0xff) << 8) | ((buf[10] & 0xff) << 0));
                    temp = String.format("%d.%d L/100KM", i / 10, i % 10);

                    ((TextView) mMainView.findViewById(R.id.fulecons2)).setText(temp);

                    i = ((int) buf[11] & 0xff) * 256 + ((int) buf[12] & 0xff);
                    temp = String.format("%d.%d L/100KM", i / 10, i % 10);

                    ((TextView) mMainView.findViewById(R.id.averagefuel)).setText(temp);

                    i = (int) (buf[6] & 0xff);
                    temp = String.format("%d KM/H", i);

                    ((TextView) mMainView.findViewById(R.id.averageapeed1)).setText(temp);

                    break;

                case 0x14:
                    i = (int) (((buf[2] & 0xff) << 24) | ((buf[3] & 0xff) << 16) | ((buf[4] & 0xff) << 8) | ((buf[5] & 0xff) << 0));
                    temp = String.format("%d.%d KM", i / 10, i % 10);

                    ((TextView) mMainView.findViewById(R.id.mileage2)).setText(temp);

                    i = (int) (((buf[7] & 0xff) << 24) | ((buf[8] & 0xff) << 16) | ((buf[9] & 0xff) << 8) | ((buf[10] & 0xff) << 0));
                    temp = String.format("%d.%d L/100KM", i / 10, i % 10);

                    ((TextView) mMainView.findViewById(R.id.fulecons3)).setText(temp);

                    i = ((int) buf[11] & 0xff) * 256 + ((int) buf[12] & 0xff);
                    temp = String.format("%d.%d L/100KM", i / 10, i % 10);

                    ((TextView) mMainView.findViewById(R.id.averagefuel3)).setText(temp);

                    i = (int) (buf[6] & 0xff);
                    temp = String.format("%d KM/H", i);

                    ((TextView) mMainView.findViewById(R.id.averageapeed2)).setText(temp);

                    break;
                case 0x61:
                    ((TextView) mMainView.findViewById(R.id.speed)).setText(((int) buf[2] & 0xff) + "KM/H");
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "updateView" + e);
        }
    }

    // private boolean isEmtpy(byte buf, int s, int e){
    // if (){
    //
    // }
    // }
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

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        int id = intent.getIntExtra(MyCmd.EXTRA_COMMON_ID, 0);
        if (id != 0) {
            if (mCurUI == R.id.driving_page1) {
                id = R.id.driving_page2;
            } else if (mCurUI == R.id.driving_page2) {
                id = R.id.driving_page3;
            } else {
                id = R.id.driving_page1;
            }
            showUI(id);
        }
    }

}
