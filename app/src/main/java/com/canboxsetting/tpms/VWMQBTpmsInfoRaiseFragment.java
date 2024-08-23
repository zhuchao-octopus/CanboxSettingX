package com.canboxsetting.tpms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MachineConfig;
import com.common.utils.MyCmd;

public class VWMQBTpmsInfoRaiseFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    private static final String TAG = "Golf7InfoSimpleFragment";
    // PreferenceScreen mTpms;
    // @Override
    // public void onCreate(Bundle savedInstanceState) {
    // super.onCreate(savedInstanceState);
    //
    // addPreferencesFromResource(R.xml.vw_mqb_tpms_raisse_info);
    // mTpms = (PreferenceScreen) findPreference("tpms");
    // mTpms.setOnPreferenceClickListener(this);

    // }
    private final static int[] INIT_CMDS = {0x6600, 0x6601, 0x6800};
    String mCanboxType;
    String mProIndex = null;
    private boolean mPause = true;
    private View mTpmsView;
    private View mTpmsReset;
    private int mColor = 0;
    private int mUnit = 0;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        mTpmsView = inflater.inflate(R.layout.type_info, container, false);

        mCanboxType = MachineConfig.getPropertyForce(MachineConfig.KEY_CAN_BOX);

        if (mCanboxType != null) {
            String[] ss = mCanboxType.split(",");
            mCanboxType = ss[0];
            try {
                for (int i = 1; i < ss.length; ++i) {
                    if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_PROTOCAL_INDEX)) {
                        mProIndex = ss[i].substring(1);
                    }
                }
            } catch (Exception e) {

            }
        }

        return mTpmsView;
    }

    @Override
    public void onPause() {
        mPause = true;
        stopRequestInitData();
        super.onPause();
        unregisterListener();
    }    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    requestInitData();
                    break;
                case 1:
                    sendCanboxInfo(0x90, (msg.arg1 & 0xff00) >> 8, msg.arg1 & 0xff);
                    break;
            }
        }
    };

    private void initTpmsView() {

        // MyPreference2 p = (MyPreference2) findPreference("tpms_content");
        // if (p != null) {
        // mTpmsView = p.getMainView();
        if (mTpmsView != null) {
            mTpmsReset = mTpmsView.findViewById(R.id.tpms);
            if (mTpmsReset != null) {
                if (MachineConfig.VALUE_CANBOX_VW_MQB_RAISE.equals(mCanboxType) || "46".equals(mProIndex) || "106".equals(mProIndex)) {
                    mTpmsReset.setVisibility(View.GONE);
                } else {
                    mTpmsReset.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            byte[] buf = new byte[]{(byte) 0xc6, 0x02, (byte) 0x22, (byte) 0x01};
                            BroadcastUtil.sendCanboxInfo(getActivity(), buf);
                        }
                    });
                }
            }
        }
        // }
    }

    private void requestInitData() {
        if (mPause) {
            return;
        }
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, 1500);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendMessageDelayed(mHandler.obtainMessage(1, INIT_CMDS[i], 0), (i * 200));
        }
    }

    private void stopRequestInitData() {
        mHandler.removeMessages(0);
        mHandler.removeMessages(1);
        mHandler.removeMessages(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListener();


        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                initTpmsView();
            }
        });
        mPause = false;
        //		requestInitData();
        if ("106".equals(mProIndex)) {

            byte[] buf = new byte[]{(byte) 0x02, 0x0a, (byte) 0x48, (byte) 0x00};
            BroadcastUtil.sendCanboxInfo(getActivity(), buf);
        } else {
            //	byte[] buf = new byte[] { (byte) 0x90, 0x02, (byte) 0x40, (byte) 0x90 };
            //	BroadcastUtil.sendCanboxInfo(getActivity(), buf);
            requestInitData();
        }
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{(byte) d0, 0x02, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    public boolean onPreferenceClick(Preference arg0) {

        try {
            String key = arg0.getKey();
            if ("tpms".equals(key)) {
                mTpmsView = null;
                sendCanboxInfo(0x90, 0x65, 0);
            }
        } catch (Exception e) {

        }

        return false;
    }

    private void setTpmsText(int id, int text, int color) {
        switch (text) {
            case 0:
                text = R.string.am_normal;
                break;
            case 1:
                text = R.string.tpms_low;
                break;
            case 2:
                text = R.string.tpms_hi;
                break;
            default:
                text = 0;
                break;
        }

        if (color != 0) {
            color = Color.RED;
        } else {
            color = Color.WHITE;
        }
        TextView tv = ((TextView) mTpmsView.findViewById(id));

        tv.setTextColor(color);
        if (text != 0) {
            tv.setText(text);
        } else {
            if (tv.getText().length() == 0) {
                tv.setText(R.string.am_normal);
            }
        }
    }

    private void updateViewRaise(byte[] buf) {
        if (mTpmsView == null) {
            initTpmsView();
        }
        switch (buf[0]) {
            case 0x66:
                mUnit = (buf[7] & 0xff);

                if (buf[2] == 0) {
                    setTpmsTextValueSimple(R.id.type11_info, buf[3], mColor & 0x1, buf[2]);
                    setTpmsTextValueSimple(R.id.type12_info, buf[4], mColor & 0x2, buf[2]);
                    setTpmsTextValueSimple(R.id.type21_info, buf[5], mColor & 0x4, buf[2]);
                    setTpmsTextValueSimple(R.id.type22_info, buf[6], mColor & 0x8, buf[2]);
                } else {

                    setTpmsTextValueSimple(R.id.type11_num, buf[3], mColor & 0x1, buf[2]);
                    setTpmsTextValueSimple(R.id.type12_num, buf[4], mColor & 0x2, buf[2]);
                    setTpmsTextValueSimple(R.id.type21_num, buf[5], mColor & 0x4, buf[2]);
                    setTpmsTextValueSimple(R.id.type22_num, buf[6], mColor & 0x8, buf[2]);
                }

                //				setTpmsTextValue(R.id.type11_info, buf[3], mColor & 0x1);
                //				setTpmsTextValue(R.id.type12_info, buf[4], mColor & 0x2);
                //				setTpmsTextValue(R.id.type21_info, buf[5], mColor & 0x4);
                //				setTpmsTextValue(R.id.type22_info, buf[6], mColor & 0x8);
                break;
            case 0x68:
                mColor = buf[2];
                int text = buf[3];
                switch (text) {
                    case 0:
                        text = R.string.vw_raise_warning_info_0;
                        break;
                    case 2:
                        text = R.string.vw_raise_warning_info_1;
                        break;
                    case 3:
                        text = R.string.vw_raise_warning_info_2;
                        break;
                    case 4:
                        text = R.string.vw_raise_warning_info_3;
                        break;
                    default:
                        text = 0;
                        break;
                }

                TextView tv = ((TextView) mTpmsView.findViewById(R.id.type30_info));
                if (text == 0) {
                    tv.setText("");
                } else {
                    tv.setText(text);
                }

                setTpmsText(R.id.type11_info2, -1, mColor & 0x1);
                setTpmsText(R.id.type12_info2, -1, mColor & 0x2);
                setTpmsText(R.id.type21_info2, -1, mColor & 0x4);
                setTpmsText(R.id.type22_info2, -1, mColor & 0x8);
                break;
        }
    }

    private void setTpmsTextValue(int id, int value, int color) {

        String text;

        switch (mUnit) {
            case 1:
                text = value / 2 + "." + (value * 5) % 10 + " psi";
                break;
            case 2:
                text = value * 10 + " kPa";
                break;
            default:
                text = value / 10 + "." + value % 10 + " bar";
                break;
        }

        if (color != 0) {
            color = Color.RED;
        } else {
            color = Color.WHITE;
        }

        TextView tv = ((TextView) mTpmsView.findViewById(id));

        tv.setTextColor(color);
        tv.setText(text);

    }

    private void setTpmsTextValueSimple(int id, int value, int color, int type) {

        String text;

        switch (mUnit) {
            case 1:
                text = value / 2 + "." + (value * 5) % 10 + " psi";
                break;
            case 2:
                text = value * 10 + " kPa";
                break;
            default:
                text = value / 10 + "." + value % 10 + " bar";
                break;
        }

        if (type == 0) {
            text = "(" + text + ")";
        }

        if (color != 0) {
            color = Color.RED;
        } else {
            color = Color.WHITE;
        }

        TextView tv = ((TextView) mTpmsView.findViewById(id));

        tv.setTextColor(color);
        tv.setText(text);

    }

    private void setTpmsTextColor(int id, int color) {

        TextView tv = ((TextView) mTpmsView.findViewById(id));
        // CharSequence s = tv.getText();
        if (color != 0) {
            color = Color.RED;
        } else {
            color = Color.WHITE;
        }
        tv.setTextColor(color);
        // tv.setText(s);

    }

    private void setTpmsTextValueHiworld(int id, int value, int color) {

        String text = value / 10 + "." + value % 10;

        //		switch (mUnit) {
        //		case 1:
        //			text = value / 10 + "." + value % 10 + " psi";
        //			break;
        //		case 2:
        //			text = value / 10 + "." + value % 10 + " kPa";
        //			break;
        //		default:
        //			text = value / 10 + "." + value % 10 + " bar";
        //			break;
        //		}

        if (color != 0) {
            color = Color.RED;
        } else {
            color = Color.WHITE;
        }

        TextView tv = ((TextView) mTpmsView.findViewById(id));

        tv.setTextColor(color);
        tv.setText(text);

    }

    private void setTpmsTextValueHiworld2(int id, int value) {

        String text = "(" + value / 10 + "." + value % 10 + ")";


        TextView tv = ((TextView) mTpmsView.findViewById(id));

        tv.setText(text);

    }

    private void updateView(byte[] buf) {
        if (mTpmsView == null) {
            initTpmsView();
        }
        switch (buf[0]) {
            case 0x66:
                if (buf[2] == 0) {
                    setTpmsTextValueSimple(R.id.type11_info, buf[3], mColor & 0x1, buf[2]);
                    setTpmsTextValueSimple(R.id.type12_info, buf[4], mColor & 0x2, buf[2]);
                    setTpmsTextValueSimple(R.id.type21_info, buf[5], mColor & 0x4, buf[2]);
                    setTpmsTextValueSimple(R.id.type22_info, buf[6], mColor & 0x8, buf[2]);
                } else {

                    setTpmsTextValueSimple(R.id.type11_num, buf[3], mColor & 0x1, buf[2]);
                    setTpmsTextValueSimple(R.id.type12_num, buf[4], mColor & 0x2, buf[2]);
                    setTpmsTextValueSimple(R.id.type21_num, buf[5], mColor & 0x4, buf[2]);
                    setTpmsTextValueSimple(R.id.type22_num, buf[6], mColor & 0x8, buf[2]);
                }
                break;
            case 0x40:
                if (buf[2] == 0x90) {
                    mUnit = (buf[4] & 0xf0) >> 4;
                }
                break;
            case 0x65:
                mColor = buf[2];
                int text = buf[3];
                switch (text) {
                    case 0:
                        text = R.string.vw_raise_warning_info_0;
                        break;
                    case 2:
                        text = R.string.vw_raise_warning_info_1;
                        break;
                    case 3:
                        text = R.string.vw_raise_warning_info_2;
                        break;
                    case 4:
                        text = R.string.vw_raise_warning_info_3;
                        break;
                    default:
                        text = 0;
                        break;
                }

                TextView tv = ((TextView) mTpmsView.findViewById(R.id.type30_info));
                if (text == 0) {
                    tv.setText("");
                } else {
                    tv.setText(text);
                }

                setTpmsTextColor(R.id.type11_info, mColor & 0x1);
                setTpmsTextColor(R.id.type12_info, mColor & 0x2);
                setTpmsTextColor(R.id.type21_info, mColor & 0x4);
                setTpmsTextColor(R.id.type22_info, mColor & 0x8);
                break;
            case 0x48:// this is hidworld
                String s = "";
                //			switch ((buf[2] & 0xff) >> 4) {
                //			case 0:
                //				mUnit = 2;
                //				break;
                //			case 1:
                //				mUnit = 0;
                //				break;
                //			case 2:
                //				mUnit = 1;
                //				break;
                //			}

                switch ((buf[2] & 0xff) >> 6) {
                    case 1:
                        s = "bar";
                        break;
                    case 2:
                        s = "psi";
                        break;
                    default:
                        s = "kPa";
                        break;
                }

                tv = ((TextView) mTpmsView.findViewById(R.id.type30_info));
                tv.setText(s);
                tv.setTextColor(Color.WHITE);


                mColor = ((buf[3] & 0x80) >> 7) | ((buf[3] & 0x40) >> 5) | ((buf[3] & 0x20) >> 3) | ((buf[3] & 0x10) >> 1);

                setTpmsTextColor(R.id.type11_info, mColor & 0x1);
                setTpmsTextColor(R.id.type12_info, mColor & 0x2);
                setTpmsTextColor(R.id.type21_info, mColor & 0x4);
                setTpmsTextColor(R.id.type22_info, mColor & 0x8);

                setTpmsTextValueHiworld(R.id.type11_num, ((buf[4] & 0x0ff) << 8) | (buf[5] & 0x0ff), mColor & 0x1);
                setTpmsTextValueHiworld(R.id.type12_num, ((buf[6] & 0x0ff) << 8) | (buf[7] & 0x0ff), mColor & 0x2);
                setTpmsTextValueHiworld(R.id.type21_num, ((buf[8] & 0x0ff) << 8) | (buf[9] & 0x0ff), mColor & 0x4);
                setTpmsTextValueHiworld(R.id.type22_num, ((buf[10] & 0x0ff) << 8) | (buf[11] & 0x0ff), mColor & 0x8);

                setTpmsTextValueHiworld2(R.id.type11_info, ((buf[12] & 0x0ff) << 8) | (buf[13] & 0x0ff));
                setTpmsTextValueHiworld2(R.id.type12_info, ((buf[14] & 0x0ff) << 8) | (buf[15] & 0x0ff));
                setTpmsTextValueHiworld2(R.id.type21_info, ((buf[16] & 0x0ff) << 8) | (buf[17] & 0x0ff));
                setTpmsTextValueHiworld2(R.id.type22_info, ((buf[18] & 0x0ff) << 8) | (buf[19] & 0x0ff));

                break;
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
                                if (MachineConfig.VALUE_CANBOX_VW_MQB_RAISE.equals(mCanboxType) || "46".equals(mProIndex)) {
                                    updateViewRaise(buf);
                                } else {
                                    updateView(buf);
                                }

                            } catch (Exception e) {

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
