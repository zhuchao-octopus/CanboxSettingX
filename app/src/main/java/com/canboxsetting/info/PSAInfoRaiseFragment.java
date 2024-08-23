package com.canboxsetting.info;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;

public class PSAInfoRaiseFragment extends MyFragment {
    private static final String TAG = "GMInfoSimpleFragment";
    private View mMainView;
    private int cmd82 = 0;
    OnClickListener mOnClickClearListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            int id = v.getId();
            if (id == R.id.fuelclear1) {//				cmd82 |= 0x40;
                //				cmd82 &= ~0x20;
                cmd82 = 0x41;
            } else if (id == R.id.fuelclear2) {
                cmd82 = 0x22;
                //				cmd82 |= 0x20;
                //				cmd82 &= ~0x40;
            } else if (id == R.id.booking_mileage) {
                showBookingMileageDialog();

                return;
            }
            sendCanboxInfo82((byte) cmd82);
        }
    };
    private int mCurUI = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // mHandler.removeMessages(msg.what);
            // mHandler.sendEmptyMessageDelayed(msg.what, 700);
            sendCanboxInfo90(msg.what);
        }
    };
    OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            showUI(v.getId());
        }
    };
    // private boolean isEmtpy(byte buf, int s, int e){
    // if (){
    //
    // }
    // }
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // addPreferencesFromResource(R.xml.gm_simple_info);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.psa_raise_info, container, false);

        mMainView.findViewById(R.id.driving_page1).setOnClickListener(mOnClickListener);
        mMainView.findViewById(R.id.driving_page2).setOnClickListener(mOnClickListener);
        mMainView.findViewById(R.id.driving_page3).setOnClickListener(mOnClickListener);

        mMainView.findViewById(R.id.fuelclear1).setOnClickListener(mOnClickClearListener);
        mMainView.findViewById(R.id.fuelclear2).setOnClickListener(mOnClickClearListener);
        //		mMainView.findViewById(R.id.booking_mileage).setOnClickListener(
        //				mOnClickClearListener);

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
        // ev.setTransformationMethod(PasswordTransformationMethod.getInstance());

        ad.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                mInputManager.hideSoftInputFromWindow(ev.getWindowToken(), 0);
            }
        });
    }

    private void showUI(int id) {
        // int cmd82 = 0;
        int cmd90 = 0;
        mCurUI = id;
        if (id == R.id.driving_page1) {
            mMainView.findViewById(R.id.driving_page1_layout).setVisibility(View.VISIBLE);
            mMainView.findViewById(R.id.driving_page2_layout).setVisibility(View.GONE);
            mMainView.findViewById(R.id.driving_page3_layout).setVisibility(View.GONE);
            cmd82 = 0;
            cmd90 = 0x33;
        } else if (id == R.id.driving_page2) {
            mMainView.findViewById(R.id.driving_page2_layout).setVisibility(View.VISIBLE);
            mMainView.findViewById(R.id.driving_page1_layout).setVisibility(View.GONE);
            mMainView.findViewById(R.id.driving_page3_layout).setVisibility(View.GONE);
            cmd82 = 1;
            cmd90 = 0x34;
        } else if (id == R.id.driving_page3) {
            mMainView.findViewById(R.id.driving_page3_layout).setVisibility(View.VISIBLE);
            mMainView.findViewById(R.id.driving_page2_layout).setVisibility(View.GONE);
            mMainView.findViewById(R.id.driving_page1_layout).setVisibility(View.GONE);
            cmd82 = 2;
            cmd90 = 0x35;
        }

        sendCanboxInfo82((byte) cmd82);
        mHandler.sendEmptyMessageDelayed(cmd90, 150);
    }

    private void sendCanboxInfo82(byte d0) {

        //		byte[] buf = new byte[] { 0x4, (byte)0x8f, d0 };
        //		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
        byte[] buf = new byte[]{0x6, (byte) 0x82, (byte) d0, 0x0, 0x0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo90(int d0) {
        byte[] buf = new byte[]{0x4, (byte) 0x8f, (byte) d0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
        //		byte[] buf = new byte[] { (byte) 0x90, 0x4, (byte) d0, 0, 0, 0 };
        //		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void updateView(byte[] buf) {

        String temp = "";
        int i;
        try {
            switch (buf[1]) {
                case 0x33:

                    i = ((int) buf[2] & 0xff) * 256 + ((int) buf[3] & 0xff);
                    if (i == 0xffff) {
                        temp = "--";
                    } else {
                        temp = String.format("%d.%d L/100KM", i / 10, i % 10);
                    }

                    ((TextView) mMainView.findViewById(R.id.fulecons1)).setText(temp);

                    i = ((int) buf[4] & 0xff) * 256 + ((int) buf[5] & 0xff);
                    if (i == 0xffff) {
                        temp = "--";
                    } else {
                        temp = String.format("%d KM", i);
                    }

                    ((TextView) mMainView.findViewById(R.id.driving_mileage)).setText(temp);

                    i = ((int) buf[6] & 0xff) * 256 + ((int) buf[7] & 0xff);
                    if (i == 0xffff) {
                        temp = "--";
                    } else {
                        temp = String.format("%d KM", i);
                    }

                    ((TextView) mMainView.findViewById(R.id.destination)).setText(temp);

                    if (i == 0xffff) {
                        temp = "--";
                    } else {
                        temp = String.format("%02d:%02d:%02d", ((int) buf[8] & 0xff), ((int) buf[9] & 0xff), ((int) buf[10] & 0xff));
                    }
                    ((TextView) mMainView.findViewById(R.id.start_stop_time)).setText(temp);
                    break;

                case 0x34:

                    i = ((int) buf[2] & 0xff) * 256 + ((int) buf[3] & 0xff);
                    temp = String.format("%d.%d L/100KM", i / 10, i % 10);

                    ((TextView) mMainView.findViewById(R.id.fulecons2)).setText(temp);

                    i = ((int) buf[4] & 0xff) * 256 + ((int) buf[5] & 0xff);
                    temp = String.format("%d KM/H", i);

                    ((TextView) mMainView.findViewById(R.id.averageapeed1)).setText(temp);

                    i = ((int) buf[6] & 0xff) * 256 + ((int) buf[7] & 0xff);
                    temp = String.format("%d KM", i);

                    ((TextView) mMainView.findViewById(R.id.mileage1)).setText(temp);

                    break;

                case 0x35:

                    i = ((int) buf[2] & 0xff) * 256 + ((int) buf[3] & 0xff);
                    temp = String.format("%d.%d L/100KM", i / 10, i % 10);

                    ((TextView) mMainView.findViewById(R.id.fulecons3)).setText(temp);

                    i = ((int) buf[4] & 0xff) * 256 + ((int) buf[5] & 0xff);
                    temp = String.format("%d KM/H", i);

                    ((TextView) mMainView.findViewById(R.id.averageapeed2)).setText(temp);

                    i = ((int) buf[6] & 0xff) * 256 + ((int) buf[7] & 0xff);
                    temp = String.format("%d KM", i);

                    ((TextView) mMainView.findViewById(R.id.mileage2)).setText(temp);

                    break;
                case 0x61:
                    ((TextView) mMainView.findViewById(R.id.speed)).setText(((int) buf[2] & 0xff) + "KM/H");
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "updateView" + e);
        }
    }

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
