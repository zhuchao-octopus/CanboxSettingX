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
import com.canboxsetting.R.array;
import com.canboxsetting.R.id;
import com.canboxsetting.R.string;
import com.canboxsetting.R.xml;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.SystemConfig;
import com.common.util.Util;
import com.common.util.shell.ShellUtils;
import com.common.view.MyPreference2;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class ZongTaiTpmsInfoaRaiseFragment extends PreferenceFragment implements
		OnPreferenceClickListener {
	private static final String TAG = "VWMQBInfoRaiseFragment";

	PreferenceScreen mTpms;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mTpmsView = inflater.inflate(R.layout.type_info4, container, false);
		return mTpmsView;
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

		sendCanboxInfo(0x90, 0x40, 0);
		Util.doSleep(20);
		sendCanboxInfo(0x90, 0x39, 0);
	}

	private void sendCanboxInfo(int d0, int d1, int d2) {
		byte[] buf = new byte[] { (byte) d0, 0x02, (byte) d1, (byte) d2 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
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
			s = String.format("%d kpa", text);
		}
		TextView tv = ((TextView) mTpmsView.findViewById(id));
		tv.setText(s);
	}

	private byte[] mTpmsWarning = new byte[4];




	private void setTpmsWarningText(int id, int text, int text2) {
		String s = "";

		if ((text & 0x80) != 0) {
			s += " " + getString(R.string.jac_front_left_1);
		}
		if ((text & 0x40) != 0) {
			s += " " + getString(R.string.jac_front_left_3);
		}
		if ((text & 0x20) != 0) {
			s += " " + getString(R.string.jac_front_left_4);
		}
		if ((text & 0x10) != 0) {
			s += " " + getString(R.string.jac_front_right_1);
		}
		if ((text & 0x8) != 0) {
			s += " " + getString(R.string.jac_front_right_3);
		}
		if ((text & 0x4) != 0) {
			s += " " + getString(R.string.jac_front_right_4);
		}
		
		if ((text2 & 0x80) != 0) {
			s += " " + getString(R.string.jac_rear_right_1);
		}
		if ((text2 & 0x40) != 0) {
			s += " " + getString(R.string.jac_rear_right_3);
		}
		if ((text2 & 0x20) != 0) {
			s += " " + getString(R.string.jac_rear_right_4);
		}
		if ((text2 & 0x10) != 0) {
			s += " " + getString(R.string.jac_rear_left_1);
		}
		if ((text2 & 0x8) != 0) {
			s += " " + getString(R.string.jac_rear_left_3);
		}
		if ((text2 & 0x4) != 0) {
			s += " " + getString(R.string.jac_rear_left_4);
		}
//		if ((text2 & 0x2) != 0) {
//			s += " " + getString(R.string.str_quick_leakage);
//		}
		if ((text2 & 0x1) != 0) {
			s += " " + getString(R.string.str_warning_system_failure);
		}
		TextView tv = ((TextView) mTpmsView.findViewById(id));

		tv.setText(s);
	}

	private void setTpmsWarningText2(int id, int text, int text2) {
		String s = "";

		if ((text & 0x80) != 0) {
			s += " " + getString(R.string.system_failure);
		}
		if ((text & 0x40) != 0) {
			s += " " + getString(R.string.direction_no_signal);
		}
		if ((text & 0x20) != 0) {
			s += " " + getString(R.string.self_check);
		}
		if ((text & 0x10) != 0) {
			s += " " + getString(R.string.tire_pressure_failure);
		}
		if ((text2 & 0x80) != 0) {
			s += " " + getString(R.string.str_sensor_low_battery_alarm);
		}
		if ((text2 & 0x40) != 0) {
			s += " " + getString(R.string.str_temp_warning);
		}
		if ((text2 & 0x20) != 0) {
			s += " " + getString(R.string.temp_high_more);
		}
		if ((text2 & 0x10) != 0) {
			s += " " + getString(R.string.str_low_pressure);
		}
		if ((text2 & 0x8) != 0) {
			s += " " + getString(R.string.str_high_pressure);
		}
		if ((text2 & 0x4) != 0) {
			s += " " + getString(R.string.str_slow_leakage);
		}
		if ((text2 & 0x2) != 0) {
			s += " " + getString(R.string.str_quick_leakage);
		}
		TextView tv = ((TextView) mTpmsView.findViewById(id));

		tv.setText(s);
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

		case 0x40:

			switch (buf[2]) {
			case 0:
				setTpmsText(R.id.type11_info, buf[4] & 0xff);
				setTpmsBarText(R.id.type11_num, buf[3] & 0xff);
				setTpmsWarningText2(R.id.type11_info2, buf[5] & 0xff,
						buf[6] & 0xff);
				break;
			case 1:
				setTpmsText(R.id.type12_info, buf[4] & 0xff);
				setTpmsBarText(R.id.type12_num, buf[3] & 0xff);
				setTpmsWarningText2(R.id.type12_info2, buf[5] & 0xff,
						buf[6] & 0xff);
				break;
			case 2:
				setTpmsText(R.id.type21_info, buf[4] & 0xff);
				setTpmsBarText(R.id.type21_num, buf[3] & 0xff);
				setTpmsWarningText2(R.id.type21_info2, buf[5] & 0xff,
						buf[6] & 0xff);
				break;
			case 3:
				setTpmsText(R.id.type22_info, buf[4] & 0xff);
				setTpmsBarText(R.id.type22_num, buf[3] & 0xff);
				setTpmsWarningText2(R.id.type22_info2, buf[5] & 0xff,
						buf[6] & 0xff);
				break;
			}
			break;
		// case 0x38:
		//
		//
		//
		// setTpmsText(R.id.type11_info, buf[2]);
		// setTpmsText(R.id.type12_info, buf[3]);
		// setTpmsText(R.id.type21_info, buf[4]);
		// setTpmsText(R.id.type22_info, buf[5]);
		//
		// setTpmsBarText(R.id.type11_num, buf[6] & 0xff);
		// setTpmsBarText(R.id.type12_num, buf[7] & 0xff);
		// setTpmsBarText(R.id.type21_num, buf[8] & 0xff);
		// setTpmsBarText(R.id.type22_num, buf[9] & 0xff);
		//
		// break;
		case 0x39:

			setTpmsWarningText(R.id.type30_info, buf[2] & 0xff,
					buf[3] & 0xff);
			break;
		}
	}

	private void showTpmsResetDialog(int id) {
		if (id <= 4 && id >= 1) {

			String[] ss = getResources().getStringArray(R.array.tpms_set_msg);

			AlertDialog ad = new AlertDialog.Builder(getActivity())
					.setTitle(ss[id])
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
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
