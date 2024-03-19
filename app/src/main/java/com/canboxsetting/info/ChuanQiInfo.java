package com.canboxsetting.info;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import com.canboxsetting.CarInfoActivity;
import com.canboxsetting.MyFragment;
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

public class ChuanQiInfo extends MyFragment {
	private static final String TAG = "FiatFragment";

	View mMainView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mMainView = inflater.inflate(R.layout.chuanqi_electric_car_infos,
				container, false);
		initView();
		return mMainView;
	}

	private void initView() {

		showPage(R.id.page1);
	}

	@Override
	public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.charging_settings) {
            showPage(R.id.page1);
        } else if (id == R.id.energy_information) {
            showPage(R.id.page2);
        } else if (id == R.id.charging_mode2) {
            showPage(R.id.page11);

            v.setSelected(false);
        } else if (id == R.id.gs4_energy_recovery1) {
            sendCanboxData(0x3, 0);
        } else if (id == R.id.gs4_energy_recovery2) {
            sendCanboxData(0x3, 1);
        } else if (id == R.id.gs4_energy_recovery3) {
            sendCanboxData(0x3, 2);
        } else if (id == R.id.energy_recovery_i_pedal1) {
            sendCanboxData(0x4, 1);
        } else if (id == R.id.energy_recovery_i_pedal2) {
            sendCanboxData(0x4, 2);
        } else if (id == R.id.cancel) {
            showPage(R.id.page1);
        } else if (id == R.id.time_of_appointment1) {
            (new TimePickerDialog(getActivity(), onTimeSetListener, 0, 0, true))
                    .show();
        } else if (id == R.id.time_of_appointment2) {
            (new TimePickerDialog(getActivity(), onTimeSetListenerEnd, 0, 0,
                    true)).show();
        } else if (id == R.id.ok) {
            setReserveTime();
        } else if (id == R.id.charging_mode1) {
            sendCanboxData(0x1, 1);
            v.setSelected(true);
        }
	}

	private void setReserveTime() {
		if (hStart >= hEnd || (hStart == hEnd && mStart >= mEnd)) {
			new AlertDialog.Builder(getActivity())
					.setTitle(R.string.set_charging_time_conflict)
					.setNegativeButton(android.R.string.cancel, null).show();
		} else {

			byte[] buf = new byte[] { (byte) 0xa9, 0x6, 2, 0, 0, 0, 0, 0 };

			buf[3] = (byte) ((hStart & 0x3f) | ((mStart & 0x03) << 6));
			buf[4] = (byte) ((mStart & 0xfc) >> 2);
			buf[5] = (byte) ((hEnd & 0x3f) | ((mEnd & 0x03) << 6));
			buf[6] = (byte) ((mEnd & 0xfc) >> 2);

			if (((RadioButton) mMainView.findViewById(R.id.cycle_mode1))
					.isChecked()) {
				buf[7] |= 0x1;
			}
			if (((CheckBox) mMainView.findViewById(R.id.cycle_date1))
					.isChecked()) {
				buf[7] |= 0x2;
			}
			if (((CheckBox) mMainView.findViewById(R.id.cycle_date2))
					.isChecked()) {
				buf[7] |= 0x4;
			}
			if (((CheckBox) mMainView.findViewById(R.id.cycle_date3))
					.isChecked()) {
				buf[7] |= 0x8;
			}
			if (((CheckBox) mMainView.findViewById(R.id.cycle_date4))
					.isChecked()) {
				buf[7] |= 0x10;
			}
			if (((CheckBox) mMainView.findViewById(R.id.cycle_date5))
					.isChecked()) {
				buf[7] |= 0x20;
			}
			if (((CheckBox) mMainView.findViewById(R.id.cycle_date6))
					.isChecked()) {
				buf[7] |= 0x40;
			}
			if (((CheckBox) mMainView.findViewById(R.id.cycle_date7))
					.isChecked()) {
				buf[7] |= 0x80;
			}
			BroadcastUtil.sendCanboxInfo(getActivity(), buf);

			showPage(R.id.page1);
			setViewSel(R.id.charging_mode1, false);
		}
	}

	TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			hStart = hourOfDay;
			mStart = minute;
			String s = String.format("%02d:%02d", hStart,
					mStart, Locale.ENGLISH);

			((TextView) mMainView.findViewById(R.id.time_of_appointment1))
					.setText(s);
		}

	};

	TimePickerDialog.OnTimeSetListener onTimeSetListenerEnd = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			hEnd = hourOfDay;
			mEnd = minute;

			String s = String.format("%02d:%02d", hEnd,
					mEnd, Locale.ENGLISH);

			((TextView) mMainView.findViewById(R.id.time_of_appointment2))
					.setText(s);
		}

	};

	private void showPage(int page) {

		setViewVisible(R.id.page1, false);
		setViewVisible(R.id.page11, false);
		setViewVisible(R.id.page2, false);

		setViewVisible(page, true);

		setViewSel(R.id.charging_settings, false);
		setViewSel(R.id.energy_information, false);
		if (page == R.id.page2) {
			setViewSel(R.id.energy_information, true);
		} else {
			setViewSel(R.id.charging_settings, true);

		}
	}

	private void setViewVisible(int id, boolean visible) {
		View v = mMainView.findViewById(id);
		if (v != null) {
			v.setVisibility(visible ? View.VISIBLE : View.GONE);
		}
	}

	private void setViewSel(int id, boolean s) {
		View v = mMainView.findViewById(id);
		if (v != null) {
			v.setSelected(s);
		}
	}

	int hStart;
	int mStart;
	int hEnd;
	int mEnd;
	private boolean mPause = false;

	@Override
	public void onPause() {
		super.onPause();
		mPause = true;
		unregisterListener();
	}

	@Override
	public void onResume() {
		super.onResume();
		mPause = false;

		registerListener();

		byte[] buf = new byte[] { (byte) 0x90, 0x2, 0x54, 0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
		Util.doSleep(10);
		buf[2] = 0x53;
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxData(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0xa9, 0x2, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void updateView(byte[] buf) {
		String s;
		int index;
		switch (buf[0]) {
		case 0x53: {

			if (buf[7] == 0 && buf[8] == 0) {
				s = getString(R.string.distance_charging_time) + "\n" + "--"
						+ getString(R.string.days) + "--"
						+ getString(R.string.gs4_hour) + "--"
						+ getString(R.string.gs4_minute);
			} else {
				s = getString(R.string.distance_charging_time) + "\n"
						+ ((buf[7] & 0x7) >> 0) + getString(R.string.days)
						+ ((buf[7] & 0xf8) >> 3) + getString(R.string.gs4_hour)
						+ ((buf[8] & 0xfc) >> 2)
						+ getString(R.string.gs4_minute);
			}
			((TextView) mMainView.findViewById(R.id.distance_charging_time))
					.setText(s);

			hStart = buf[2] & 0x3f;
			mStart = ((buf[2] & 0xc0) >> 6) | ((buf[3] & 0xff) >> 2);
			hEnd = buf[4] & 0x3f;
			mEnd = ((buf[4] & 0xc0) >> 6)
					| ((buf[5] & 0xff) >> 2);

			s = String.format("%02d:%02d", hStart,
					mStart, Locale.ENGLISH);

			((TextView) mMainView.findViewById(R.id.time_of_appointment1))
					.setText(s);
			
			s = String.format("%02d:%02d", hEnd,
					mEnd, Locale.ENGLISH);

			((TextView) mMainView.findViewById(R.id.time_of_appointment2))
					.setText(s);
			
			
			s = String.format("%02d:%02d~%02d:%02d", buf[2] & 0x3f,
					((buf[2] & 0xc0) >> 6) | ((buf[3] & 0xff) >> 2),
					buf[4] & 0x3f, ((buf[4] & 0xc0) >> 6)
							| ((buf[5] & 0xff) >> 2), Locale.ENGLISH);

			
//			((RadioButton) mMainView.findViewById(R.id.cycle_mode1))
//					.setChecked(false);
//			((RadioButton) mMainView.findViewById(R.id.cycle_mode2))
//					.setChecked(false);
			((CheckBox) mMainView.findViewById(R.id.cycle_date1))
					.setChecked(false);
			((CheckBox) mMainView.findViewById(R.id.cycle_date2))
					.setChecked(false);
			((CheckBox) mMainView.findViewById(R.id.cycle_date3))
					.setChecked(false);
			((CheckBox) mMainView.findViewById(R.id.cycle_date4))
					.setChecked(false);
			((CheckBox) mMainView.findViewById(R.id.cycle_date5))
					.setChecked(false);
			((CheckBox) mMainView.findViewById(R.id.cycle_date6))
					.setChecked(false);
			((CheckBox) mMainView.findViewById(R.id.cycle_date7))
					.setChecked(false);

			if ((buf[6] & 0xff) != 0) {

				s += " ";
				if ((buf[6] & 0x01) != 0) {
					s += getString(R.string.loop);
					((RadioButton) mMainView.findViewById(R.id.cycle_mode1))
							.setChecked(true);
				} else {
					s += getString(R.string.single_time);
					((RadioButton) mMainView.findViewById(R.id.cycle_mode2))
							.setChecked(true);
				}

				if ((buf[6] & 0x02) != 0) {
					s += getString(R.string.monday);

					((CheckBox) mMainView.findViewById(R.id.cycle_date1))
							.setChecked(true);
				}
				if ((buf[6] & 0x04) != 0) {
					s += getString(R.string.tuesday);
					((CheckBox) mMainView.findViewById(R.id.cycle_date2))
							.setChecked(true);
				}
				if ((buf[6] & 0x08) != 0) {
					s += getString(R.string.wednesday);
					((CheckBox) mMainView.findViewById(R.id.cycle_date3))
							.setChecked(true);
				}
				if ((buf[6] & 0x10) != 0) {
					s += getString(R.string.thursday);
					((CheckBox) mMainView.findViewById(R.id.cycle_date4))
							.setChecked(true);
				}
				if ((buf[6] & 0x20) != 0) {
					s += getString(R.string.friday);
					((CheckBox) mMainView.findViewById(R.id.cycle_date5))
							.setChecked(true);
				}
				if ((buf[6] & 0x40) != 0) {
					s += getString(R.string.saturday);
					((CheckBox) mMainView.findViewById(R.id.cycle_date6))
							.setChecked(true);
				}
				if ((buf[6] & 0x80) != 0) {
					s += getString(R.string.sunday);
					((CheckBox) mMainView.findViewById(R.id.cycle_date7))
							.setChecked(true);
				}
			}

			((TextView) mMainView.findViewById(R.id.current_appointment))
					.setText(s);

			s = (buf[10] & 0xff) + " " + getString(R.string.level);
			//
			((TextView) mMainView.findViewById(R.id.energy_flow_state))
					.setText(s);

			setViewSel(R.id.gs4_energy_recovery1, false);
			setViewSel(R.id.gs4_energy_recovery2, false);
			setViewSel(R.id.gs4_energy_recovery3, false);

			switch ((buf[11] & 0x3)) {
			case 0:
				setViewSel(R.id.gs4_energy_recovery1, true);
				break;
			case 1:
				setViewSel(R.id.gs4_energy_recovery2, true);
				break;
			case 2:
				setViewSel(R.id.gs4_energy_recovery3, true);
				break;
			}
			setViewSel(R.id.energy_recovery_i_pedal1, false);
			setViewSel(R.id.energy_recovery_i_pedal2, false);
			if ((buf[11] & 0x80) != 0) {
				setViewSel(R.id.energy_recovery_i_pedal1, true);
				// s = getString(R.string.open);
			} else {
				// s = getString(R.string.close);
				setViewSel(R.id.energy_recovery_i_pedal2, true);
			}
			 ((SeekBar) mMainView.findViewById(R.id.enag)).setProgress(buf[9]&0xff);

		}
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
