package com.canboxsetting.tpms;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.Util;
import com.common.view.MyPreference2;

public class OuShangeTpmsRaiseFragment extends PreferenceFragment implements
		OnPreferenceClickListener {
	private static final String TAG = "VWMQBInfoRaiseFragment";

	PreferenceScreen mTpms;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mTpmsView = inflater.inflate(R.layout.type_info_oushang, container, false);
		return mTpmsView;
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterListener();

		mHandler.removeMessages(0x1);
	}

	@Override
	public void onResume() {
		super.onResume();
		registerListener();

		sendCanboxInfo(0x90, 0x38, 0);
		Util.doSleep(10);
		sendCanboxInfo(0x90, 0x39, 0);
		
		mHandler.removeMessages(0x1);
		mHandler.sendEmptyMessageDelayed(0x1, 500);

	}

	private void initTpmsView() {
	
		if (mTpmsView != null) {
			View v = mTpmsView.findViewById(R.id.tpms);
			v.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					sendCanboxInfo(0x83, 0xc, 1);
				}
			});
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				initTpmsView();
				if (mTpmsView == null) {
					mHandler.sendEmptyMessageDelayed(0x1, 500);
				}
				break;
			}
			// sendCanboxInfo(0x90, (msg.what & 0xff00) >> 8, msg.what & 0xff);
		}
	};

	private void sendCanboxInfo(int d0, int d1, int d2) {
		byte[] buf = new byte[] { (byte) d0, 0x02, (byte) d1, (byte) d2 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void setPreference(String key, String s) {
		Preference p = findPreference(key);
		if (p != null) {
			p.setSummary(s);
		}
	}

	private View mTpmsView;

	public boolean onPreferenceClick(Preference arg0) {

		return false;
	}

	private void setTpmsBarText(int id, int text) {
		String s;
		if (text == 0xff) {
			s = "-.-";
		} else {
			text = (text * 100 / 7) - 100;

			s = String.format("%d.%1d", text / 1000, (text % 1000)/100);

		}
		TextView tv = ((TextView) mTpmsView.findViewById(id));
		tv.setText(s + "bar");
	}

	private byte[] mTpmsWarning = new byte[4];

	private void setTpmsWarningText(int id, int text) {
		String s = null;

		if ((text & 0xff) != 0) {
			String[] ss = getResources()
					.getStringArray(R.array.tpms_waring_msg);
			int i = 0;
			s = "";
			for (i = 0; i < 8; i++) {
				if ((text & (0x1 << i)) != 0) {
					if (s.length() > 0) {
						s += ",";
					}
					s += ss[i];
				}
			}

		}
		TextView tv = ((TextView) mTpmsView.findViewById(id));
		if (s != null) {

			tv.setText(s);
			tv.setVisibility(View.VISIBLE);
		} else {
			tv.setVisibility(View.GONE);
		}
	}

	private void setTpmsText(int id, int text) {
		String s;
		if (text == 0xff) {
			s = "-";
		} else {
			text = -40 + text;

			s = text + " ";

		}
		TextView tv = ((TextView) mTpmsView.findViewById(id));
		tv.setText(s + getActivity().getString(R.string.temp_unic));
	}

	private void updateView(byte[] buf) {
		int index;
		String s = "";
		switch (buf[0]) {

		case 0x3a:
			index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));
			if (index >= 0 && index <= 0x1fff) {
				s = String.format("%d km", index);
			} else {
				s = "";
			}
			setPreference("oil_change", s);

			break;

		case 0x38:

			if (mTpmsView == null) {
				MyPreference2 p = (MyPreference2) findPreference("tpms_content");
				if (p != null) {
					mTpmsView = p.getMainView();
				}
			}

			setTpmsText(R.id.type11_info, buf[2]);
			setTpmsText(R.id.type12_info, buf[3]);
			setTpmsText(R.id.type21_info, buf[4]);
			setTpmsText(R.id.type22_info, buf[5]);

			setTpmsBarText(R.id.type11_num, buf[6] & 0xff);
			setTpmsBarText(R.id.type12_num, buf[7] & 0xff);
			setTpmsBarText(R.id.type21_num, buf[8] & 0xff);
			setTpmsBarText(R.id.type22_num, buf[9] & 0xff);

			break;

		case 0x39:
			setTpmsWarningText(R.id.type11_warning, buf[2]);
			setTpmsWarningText(R.id.type12_warning, buf[3]);
			setTpmsWarningText(R.id.type21_warning, buf[4]);
			setTpmsWarningText(R.id.type22_warning, buf[5]);

			break;
		case 0x52:
			if (buf[2] == 0xc) {
				showTpmsResetDialog(buf[3] & 0xff);
			}
			break;
		}
	}

	private void showTpmsResetDialog(int id) {
		if (id <= 4 && id >= 1) {

			String[] ss = getResources()
					.getStringArray(R.array.tpms_set_msg);

			AlertDialog ad = new AlertDialog.Builder(getActivity())
					.setTitle(ss[id])
					.setNegativeButton(R.string.cancel,
							new OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).create();
			ad.show();
		}
	}

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
								Log.d("cce", "" + e);
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
