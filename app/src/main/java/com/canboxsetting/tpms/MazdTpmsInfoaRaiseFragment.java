package com.canboxsetting.tpms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import com.canboxsetting.R;
import com.canboxsetting.R.id;
import com.canboxsetting.R.layout;
import com.canboxsetting.R.string;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.SystemConfig;
import com.common.util.Util;
import com.common.util.shell.ShellUtils;
import com.common.view.MyPreference2;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class MazdTpmsInfoaRaiseFragment extends PreferenceFragment implements
		OnPreferenceClickListener {
	private static final String TAG = "Golf7InfoSimpleFragment";

	PreferenceScreen mTpms;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mTpmsView = inflater.inflate(R.layout.type_info3, container, false);
		return mTpmsView;
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterListener();
	}

	// private void initTpmsView() {
	//
	// MyPreference2 p = (MyPreference2) findPreference("tpms_content");
	// if (p != null) {
	// mTpmsView = p.getMainView();
	// if (mTpmsView != null) {
	// mTpmsReset = mTpmsView.findViewById(R.id.tpms);
	// if (mTpmsReset != null) {
	// mTpmsReset.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// byte[] buf = new byte[] { (byte) 0xc6, 0x02,
	// (byte) 0x22, (byte) 0x01 };
	// BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	// }
	// });
	// }
	// }
	// }
	// }

	@Override
	public void onResume() {
		super.onResume();
		registerListener();

		byte[] buf = new byte[] { (byte) 0x90, 0x01, (byte) 0x26 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);

		// new Handler() {
		// @Override
		// public void handleMessage(Message msg) {
		// initTpmsView();
		// }
		// };
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// mHandler.removeMessages(msg.what);
			// mHandler.sendEmptyMessageDelayed(msg.what, 700);
			sendCanboxInfo(0x90, (msg.what & 0xff00) >> 8, msg.what & 0xff);
		}
	};

	private void sendCanboxInfo(int d0, int d1, int d2) {
		byte[] buf = new byte[] { (byte) d0, 0x02, (byte) d1, (byte) d2 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private View mTpmsView;
	private View mTpmsReset;

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

	private int mUnit = 0;

	private void setTpmsTextValue(int id, int value, int color) {

		String text;

//		switch (mUnit) {
//		case 1:
//			text = value / 2 + "." + (value * 5) % 10 + " psi";
//			break;
//		case 2:
			text = value * 2 + " kPa";
//			break;
//		default:
//			text = value / 10 + "." + value % 10 + " bar";
//			break;
//		}

//		if (color != 0) {
//			color = Color.RED;
//		} else {
			color = Color.WHITE;
//		}

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

	private void updateView(byte[] buf) {

		switch (buf[0]) {
		case 0x26:
			setTpmsTextValue(R.id.type11_info, buf[2], 0);
			setTpmsTextValue(R.id.type12_info, buf[3], 0);
			setTpmsTextValue(R.id.type21_info, buf[4], 0);
			setTpmsTextValue(R.id.type22_info, buf[5], 0);
			break;
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
