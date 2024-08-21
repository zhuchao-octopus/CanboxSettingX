package com.canboxsetting.info;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;

public class PSAInfoBagooFragment extends PreferenceFragmentCompat {
    private static final String TAG = "GMInfoSimpleFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // addPreferencesFromResource(R.xml.gm_simple_info);

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    private View mMainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = inflater.getContext();
        mMainView = inflater.inflate(R.layout.psa_bagoo_info, container, false);

        mMainView.findViewById(R.id.fuelclear2).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                byte[] buf = new byte[]{(byte) 0x82, 0x06, 0, 0, 0, 0x41, 0, 0};
                BroadcastUtil.sendCanboxInfo(getActivity(), buf);
            }
        });
        mMainView.findViewById(R.id.fuelclear3).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                byte[] buf = new byte[]{(byte) 0x82, 0x06, 0, 0, 0, 0x22, 0, 0};
                BroadcastUtil.sendCanboxInfo(getActivity(), buf);
            }
        });
        mMainView.findViewById(R.id.booking_mileage).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showBookingMileageDialog();
            }
        });
        // claer warning
        byte[] buf = new byte[24];
        buf[0] = 0x47;
        updateView(buf);
        return mMainView;
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

                        byte[] buf = new byte[]{(byte) 0x82, 0x06, 0, 0, 0, (byte) 0x80, (byte) ((value & 0xff00) >> 8), (byte) ((value & 0xff))};
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
        //		ev.setTransformationMethod(PasswordTransformationMethod.getInstance());

        ad.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                mInputManager.hideSoftInputFromWindow(ev.getWindowToken(), 0);
            }
        });
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
        mHandler.sendEmptyMessageDelayed(0x33, 1);
        mHandler.sendEmptyMessageDelayed(0x34, 150);
        mHandler.sendEmptyMessageDelayed(0x35, 300);
        mHandler.sendEmptyMessageDelayed(0x38, 450);
        mHandler.sendEmptyMessageDelayed(0x47, 600);
    }

    private void sendCanboxInfo(int d0) {
        byte[] buf = new byte[]{(byte) 0xf1, 0x01, (byte) d0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // mHandler.removeMessages(msg.what);
            // mHandler.sendEmptyMessageDelayed(msg.what, 700);
            sendCanboxInfo(msg.what);
        }
    };

    private void updateView(byte[] buf) {

        String temp = "";
        int i;
        try {
            switch (buf[0]) {
                case 0x38:

                    if ((buf[2] & 0x4) == 0) {
                        ((TextView) mMainView.findViewById(R.id.am_safetybelt)).setText(R.string.am_buttoned);
                        ((TextView) mMainView.findViewById(R.id.am_safetybelt)).setTextColor(Color.WHITE);
                    } else {
                        ((TextView) mMainView.findViewById(R.id.am_safetybelt)).setText(R.string.am_nobuttoned);
                        ((TextView) mMainView.findViewById(R.id.am_safetybelt)).setTextColor(Color.RED);
                    }

                    if ((buf[3] & 0x20) == 0) {
                        ((TextView) mMainView.findViewById(R.id.beoilmass)).setText(R.string.am_normal);
                        ((TextView) mMainView.findViewById(R.id.beoilmass)).setTextColor(Color.WHITE);
                    } else {
                        ((TextView) mMainView.findViewById(R.id.beoilmass)).setText(R.string.electriclow);
                        ((TextView) mMainView.findViewById(R.id.beoilmass)).setTextColor(Color.RED);
                    }
                case 0x33:

                    i = ((int) buf[2] & 0xff) * 256 + ((int) buf[3] & 0xff);
                    if (i < 0 || i > 300) {
                        i = 0;
                    }
                    temp = String.format("%d.%d L/100KM", i / 10, i % 10);

                    ((TextView) mMainView.findViewById(R.id.fulecons1)).setText(temp);

                    i = ((int) buf[4] & 0xff) * 256 + ((int) buf[5] & 0xff);
                    if (i < 0 || i > 2000) {
                        i = 0;
                    }
                    temp = String.format("%d KM", i);

                    ((TextView) mMainView.findViewById(R.id.driving_mileage)).setText(temp);

                    i = ((int) buf[6] & 0xff) * 256 + ((int) buf[7] & 0xff);
                    if (i < 0 || i > 6000) {
                        i = 0;
                    }
                    temp = String.format("%d KM", i);

                    ((TextView) mMainView.findViewById(R.id.destination)).setText(temp);

                    break;

                case 0x34:

                    i = ((int) buf[2] & 0xff) * 256 + ((int) buf[3] & 0xff);
                    if (i < 0 || i > 300) {
                        i = 0;
                    }
                    temp = String.format("%d.%d L/100KM", i / 10, i % 10);

                    ((TextView) mMainView.findViewById(R.id.fulecons2)).setText(temp);

                    i = ((int) buf[4] & 0xff) * 256 + ((int) buf[5] & 0xff);
                    if (i < 0 || i > 250) {
                        i = 0;
                    }
                    temp = String.format("%d KM/H", i);

                    ((TextView) mMainView.findViewById(R.id.averageapeed1)).setText(temp);

                    i = ((int) buf[6] & 0xff) * 256 + ((int) buf[7] & 0xff);
                    if (i < 0 || i > 9999) {
                        i = 0;
                    }
                    temp = String.format("%d KM", i);

                    ((TextView) mMainView.findViewById(R.id.mileage1)).setText(temp);
                    break;

                case 0x35:

                    i = ((int) buf[2] & 0xff) * 256 + ((int) buf[3] & 0xff);
                    if (i < 0 || i > 300) {
                        i = 0;
                    }
                    temp = String.format("%d.%d L/100KM", i / 10, i % 10);

                    ((TextView) mMainView.findViewById(R.id.fulecons3)).setText(temp);

                    i = ((int) buf[4] & 0xff) * 256 + ((int) buf[5] & 0xff);
                    if (i < 0 || i > 250) {
                        i = 0;
                    }
                    temp = String.format("%d KM/H", i);

                    ((TextView) mMainView.findViewById(R.id.averageapeed2)).setText(temp);

                    i = ((int) buf[6] & 0xff) * 256 + ((int) buf[7] & 0xff);
                    if (i < 0 || i > 9999) {
                        i = 0;
                    }
                    temp = String.format("%d KM", i);

                    ((TextView) mMainView.findViewById(R.id.mileage2)).setText(temp);
                    break;
                case 0x61:
                    ((TextView) mMainView.findViewById(R.id.speed)).setText(((int) buf[2] & 0xff) + "KM/H");
                    break;
                case 0x47:
                    mMainView.findViewById(R.id.warning_layout).setVisibility(View.VISIBLE);

                    setWarning(R.id.bagoo_warning_info_00, buf[2] & 0x1);
                    setWarning(R.id.bagoo_warning_info_01, buf[2] & 0x2);
                    setWarning(R.id.bagoo_warning_info_02, buf[2] & 0x4);
                    setWarning(R.id.bagoo_warning_info_03, buf[2] & 0x8);
                    setWarning(R.id.bagoo_warning_info_04, buf[2] & 0x10);

                    setWarning(R.id.bagoo_warning_info_10, buf[3] & 0x1);
                    setWarning(R.id.bagoo_warning_info_11, buf[3] & 0x2);
                    setWarning(R.id.bagoo_warning_info_12, buf[3] & 0x4);

                    setWarning(R.id.bagoo_warning_info_20, buf[4] & 0x1);
                    setWarning(R.id.bagoo_warning_info_21, buf[4] & 0x2);
                    setWarning(R.id.bagoo_warning_info_22, buf[4] & 0x4);

                    setWarning(R.id.bagoo_warning_info_30, buf[5] & 0x1);
                    setWarning(R.id.bagoo_warning_info_31, buf[5] & 0x2);
                    setWarning(R.id.bagoo_warning_info_40, buf[6] & 0x1);
                    setWarning(R.id.bagoo_warning_info_41, buf[6] & 0x2);
                    setWarning(R.id.bagoo_warning_info_42, buf[6] & 0x4);

                    setWarning(R.id.bagoo_warning_info_50, buf[7] & 0x1);
                    setWarning(R.id.bagoo_warning_info_51, buf[7] & 0x2);
                    setWarning(R.id.bagoo_warning_info_52, buf[7] & 0x4);

                    setWarning(R.id.bagoo_warning_info_60, buf[8] & 0x1);
                    setWarning(R.id.bagoo_warning_info_61, buf[8] & 0x2);
                    setWarning(R.id.bagoo_warning_info_62, buf[8] & 0x4);
                    setWarning(R.id.bagoo_warning_info_63, buf[8] & 0x8);
                    setWarning(R.id.bagoo_warning_info_64, buf[8] & 0x10);
                    setWarning(R.id.bagoo_warning_info_65, buf[8] & 0x20);
                    setWarning(R.id.bagoo_warning_info_66, buf[8] & 0x40);
                    setWarning(R.id.bagoo_warning_info_67, buf[8] & 0x80);

                    setWarning(R.id.bagoo_warning_info_70, buf[9] & 0x1);
                    setWarning(R.id.bagoo_warning_info_71, buf[9] & 0x2);
                    setWarning(R.id.bagoo_warning_info_72, buf[9] & 0x4);
                    setWarning(R.id.bagoo_warning_info_73, buf[9] & 0x8);
                    setWarning(R.id.bagoo_warning_info_74, buf[9] & 0x10);

                    setWarning(R.id.bagoo_warning_info_80, buf[10] & 0x1);
                    setWarning(R.id.bagoo_warning_info_81, buf[10] & 0x2);
                    setWarning(R.id.bagoo_warning_info_82, buf[10] & 0x4);

                    setWarning(R.id.bagoo_warning_info_90, buf[11] & 0x1);

                    setWarning(R.id.bagoo_warning_info_A0, buf[11] & 0x1);
                    setWarning(R.id.bagoo_warning_info_A1, buf[11] & 0x2);

                    setWarning(R.id.bagoo_warning_info_B0, buf[12] & 0x1);
                    setWarning(R.id.bagoo_warning_info_B1, buf[12] & 0x2);
                    setWarning(R.id.bagoo_warning_info_B2, buf[12] & 0x4);
                    setWarning(R.id.bagoo_warning_info_B3, buf[12] & 0x8);
                    setWarning(R.id.bagoo_warning_info_B4, buf[12] & 0x10);
                    setWarning(R.id.bagoo_warning_info_B5, buf[12] & 0x20);
                    setWarning(R.id.bagoo_warning_info_B6, buf[12] & 0x40);

                    setWarning(R.id.bagoo_warning_info_C0, buf[13] & 0x1);
                    setWarning(R.id.bagoo_warning_info_C1, buf[13] & 0x2);
                    setWarning(R.id.bagoo_warning_info_C2, buf[13] & 0x4);

                    setWarning(R.id.bagoo_warning_info_D0, buf[14] & 0x1);
                    setWarning(R.id.bagoo_warning_info_D1, buf[14] & 0x2);
                    setWarning(R.id.bagoo_warning_info_D2, buf[14] & 0x4);
                    setWarning(R.id.bagoo_warning_info_D3, buf[14] & 0x8);
                    setWarning(R.id.bagoo_warning_info_D4, buf[14] & 0x10);
                    setWarning(R.id.bagoo_warning_info_D5, buf[14] & 0x20);
                    setWarning(R.id.bagoo_warning_info_D6, buf[14] & 0x40);

                    setWarning(R.id.bagoo_warning_info_E0, buf[15] & 0x1);
                    setWarning(R.id.bagoo_warning_info_E1, buf[15] & 0x2);
                    setWarning(R.id.bagoo_warning_info_E2, buf[15] & 0x4);
                    setWarning(R.id.bagoo_warning_info_E3, buf[15] & 0x8);
                    setWarning(R.id.bagoo_warning_info_E4, buf[15] & 0x10);
                    setWarning(R.id.bagoo_warning_info_E5, buf[15] & 0x20);
                    setWarning(R.id.bagoo_warning_info_E6, buf[15] & 0x40);

                    setWarning(R.id.bagoo_warning_info_F0, buf[16] & 0x1);
                    setWarning(R.id.bagoo_warning_info_F1, buf[16] & 0x2);
                    setWarning(R.id.bagoo_warning_info_F2, buf[16] & 0x4);

                    setWarning(R.id.bagoo_warning_info_G0, buf[17] & 0x1);
                    setWarning(R.id.bagoo_warning_info_G1, buf[17] & 0x2);

                    setWarning(R.id.bagoo_warning_info_H0, buf[18] & 0x1);
                    setWarning(R.id.bagoo_warning_info_H1, buf[18] & 0x2);
                    setWarning(R.id.bagoo_warning_info_H2, buf[18] & 0x4);

                    setWarning(R.id.bagoo_warning_info_I0, buf[19] & 0x1);
                    setWarning(R.id.bagoo_warning_info_I1, buf[19] & 0x2);
                    break;
                case 0x49:
                    setFunction(R.id.automatic_door_locking, buf[2] & 0x3);
                    setFunction(R.id.automatic_headlamp_lighting, (buf[2] & 0xc) >> 2);
                    setFunction(R.id.passenger_airbag, (buf[2] & 0x30) >> 4);
                    setFunction(R.id.parking_assistance, (buf[2] & 0xc0) >> 6);
                    int index = (buf[3] & 0x3) >> 0;
                    if (index == 1) {
                        index = 3;
                    } else if (index == 2) {
                        index = 1;
                    }

                    setFunction(R.id.sport_suspension_Bit1_mode, index);
                    setFunction(R.id.automatic_screen_wipe, (buf[3] & 0xc) >> 2);
                    setFunction(R.id.esp_system, (buf[3] & 0x30) >> 4);
                    setFunction2(R.id.door, (buf[3] & 0xc0) >> 6);
                    setFunction(R.id.stop_start_system, (buf[4] & 0x30) >> 4);
                    setFunction(R.id.child_safety, (buf[4] & 0xc0) >> 6);
                    // if ((buf[2] & 0x3) == 0x1) {
                    // ((TextView) mMainView.findViewById(R.id.car_door_lock))
                    // .setText(R.string.off);
                    // } else {
                    // ((TextView) mMainView.findViewById(R.id.car_door_lock))
                    // .setText(R.string.on);
                    // }

                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "updateView" + e);
        }
    }

    private void setWarning(int id, int visible) {
        TextView tv = (TextView) mMainView.findViewById(id);
        if (visible != 0) {
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.GONE);
        }
    }

    private void setFunction2(int id, int visible) {
        TextView tv = (TextView) mMainView.findViewById(id);
        int text_id;
        if (visible == 0x3) {
            text_id = R.string.lock;
        } else if (visible == 0x1) {
            text_id = R.string.unlock2;
        } else {
            text_id = R.string.none;
        }
        tv.setText(text_id);
    }

    private void setFunction(int id, int visible) {
        TextView tv = (TextView) mMainView.findViewById(id);
        int text_id;
        if (visible == 0x3) {
            text_id = R.string.on;
        } else if (visible == 0x1) {
            text_id = R.string.off;
        } else {
            text_id = R.string.none;
        }
        tv.setText(text_id);
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

}
