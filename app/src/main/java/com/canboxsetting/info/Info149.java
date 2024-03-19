package com.canboxsetting.info;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.view.View;
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
import android.graphics.drawable.Drawable;
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
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import com.canboxsetting.R;
import com.canboxsetting.R.string;
import com.canboxsetting.R.xml;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Node;
import com.common.util.SystemConfig;
import com.common.util.Util;
import com.common.util.shell.ShellUtils;
import com.common.view.MyPreference2;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class Info149 extends PreferenceFragment implements
		OnPreferenceClickListener {
	private static final String TAG = "ToyotaInfoHiworldFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.toyota_xinchi_info);
	}

	// 初始化命令
	private final static int[] INIT_CMDS = {};

	private void requestInitData() {
		// mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
		for (int i = 0; i < INIT_CMDS.length; ++i) {
			mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 200));
		}

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!mPaused) {
				sendCanboxInfo0x90((msg.what & 0xff00) >> 8, msg.what & 0xff);

			}
		}
	};

	private void sendCanboxInfo0x90(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0x90, 0x2, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	public boolean onPreferenceClick(Preference arg0) {

		return false;
	}

	private boolean mPaused = true;

	@Override
	public void onPause() {
		super.onPause();
		unregisterListener();
		mPaused = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		registerListener();
		mPaused = false;
		requestInitData();
	}

	private void setPreference(String key, String s) {
		Preference p = findPreference(key);
		if (p != null) {
			p.setSummary(s);
		}
	}

	private String getOilString(int index, int unit) {
		String s;

		if (unit == 0) {
			s = index / 10 + "." + index % 10 + "MPG";
		} else if (unit == 1) {
			s = index / 10 + "." + index % 10 + "KM/L";
		} else {
			s = index / 10 + "." + index % 10 + "L/100KM";
		}
		// setPreference("dynamical_fuel", s);
		return s;
	}

	private boolean mIs0x5a = false;

	private void updateView(byte[] buf) {

		int index = 0;
		String s = "";
		switch (buf[0]) {
		case 0x5a:
			mIs0x5a = true;
			// 瞬时油耗
			index = ((buf[2] & 0xff) << 8 | (buf[3] & 0xff));
			if (index == 0xffff) {
				s = "----";
			} else {
				s = index / 10 + "." + index % 10;
				if ((buf[12] & 0xc0) == 0) {
					s += "l/100km";
				} else {
					s += "km/l";
				}
			}
			setPreference("dynamical_fuel", s);

			// 当前(现在)行程油耗
			index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));
			if (index == 0xffff) {
				s = "----";
			} else {
				s = getOilString(index, (buf[12] & 0x30) >> 4);
			}
			setPreference("curfuel", s);

			// 续航
			index = ((buf[6] & 0xff) << 8 | (buf[7] & 0xff));
			if (index == 0xffff) {
				s = "----";
			} else {
				s = index + "km";
				if ((buf[12] & 0x08) == 0) {
					s = index + "km";
				} else {
					s = index + "mils";
				}
			}
			setPreference("driving_mileage", s);

			// 平均速度
			index = ((buf[9] & 0xff) | ((buf[8] & 0xff) << 8));
			if (index == 0xffff) {
				s = "----";
			} else {
				s = index / 10 + "." + index % 10;
				if ((buf[12] & 0x04) == 0) {
					s = s + "Km/h";
				} else {
					s = s + "MPH";
				}
			}
			setPreference("averagespeed24", s);
			// 行驶里程
			index = ((buf[11] & 0xff) | ((buf[10] & 0xff) << 8));
			if (index == 0xffff) {
				s = "----";
			} else {
				s = index / 10 + "." + index % 10;
				if ((buf[12] & 0x02) == 0) {
					s = s + "km";
				} else {
					s = s + "mils";
				}
			}
			setPreference("distance_travelled", s);

			break;
		case (byte) 0x22: {
			if (mIs0x5a) {
				return;
			}
			// 瞬时油耗
			index = ((buf[3] & 0xff) << 8 | (buf[4] & 0xff));
			s = getOilString(index, buf[2]);
			setPreference("dynamical_fuel", s);
		}
			break;
		case (byte) 0x23: {
			// if (mIs0x5a) {
			// return;
			// }
			// 当前(现在)行程油耗
			index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));
			s = getOilString(index, buf[2]);
			setPreference("curfuel", s);

			// Trip1
			index = ((buf[6] & 0xff) | ((buf[5] & 0xff) << 8));

			s = getOilString(index, buf[2]);
			setPreference("trip1", s);

			// Trip2
			index = ((buf[8] & 0xff) | ((buf[7] & 0xff) << 8));

			s = getOilString(index, buf[2]);
			setPreference("trip2", s);

			// Trip3
			index = ((buf[10] & 0xff) | ((buf[9] & 0xff) << 8));

			s = getOilString(index, buf[2]);
			setPreference("trip3", s);

			// Trip4
			index = ((buf[12] & 0xff) | ((buf[11] & 0xff) << 8));

			s = getOilString(index, buf[2]);
			setPreference("trip4", s);

			// Trip5
			index = ((buf[14] & 0xff) | ((buf[13] & 0xff) << 8));

			s = getOilString(index, buf[2]);
			setPreference("trip5", s);

		}
			break;

		case (byte) 0x21: {
			if (mIs0x5a) {
				return;
			}
			// 平均速度
			index = ((buf[3] & 0xff) | ((buf[2] & 0xff) << 8));
			s = "" + index + "Km/h";
			setPreference("averagespeed24", s);
			index = (buf[12] & 0xff);
			// 行驶时间
			index = ((buf[5] & 0xff) << 8 | (buf[4] & 0xff));
			s = String.format(Locale.ENGLISH, "%02d:%02d h", index / 60,
					index % 60);
			setPreference("traveltime", s);

			// 续航
			index = ((buf[7] & 0xff) << 8 | (buf[6] & 0xff));
			s = index + "km";
			setPreference("driving_mileage", s);
		}
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
