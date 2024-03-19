package com.common.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.android.canboxsetting.R;

public class MyPreferenceSeekBar extends Preference {

	@Override
	protected void onClick() {
		// TODO Auto-generated method stub
		super.onClick();
		showDialog();
	}

	public MyPreferenceSeekBar(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initAttrs(attrs, defStyleAttr);
	}

	public MyPreferenceSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);initAttrs(attrs, 0);
	}

	public MyPreferenceSeekBar(Context context) {
		super(context);
	}
	

	private void initAttrs(AttributeSet attrs, int defStyle) {
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.MyPreferenceSeekBar, defStyle, 0);
		mMin = a.getInteger(R.styleable.MyPreferenceSeekBar_min, 0);
		mMax = a.getInteger(R.styleable.MyPreferenceSeekBar_max, 0);
		mStep = a.getInteger(R.styleable.MyPreferenceSeekBar_step, 0);
		if (mStep <= 0) {
			mStep = 1;
		}
		mUnit = a.getString(R.styleable.MyPreferenceSeekBar_unit);
	}

	private int mMax = 0;
	private int mMin = 0;
	private int mStep = 1;
	private String mUnit = null;
	private SeekBar mSeekBar;
	private TextView mSeekBarValue;

	@Override
	public void setSummary(CharSequence summary) {
		// TODO Auto-generated method stub
		if (mUnit != null) {
			summary = summary.toString() + " " + mUnit;
		}
		super.setSummary(summary);
	}
	
	public void setUnit(String unit) {
		mUnit = unit;
	}
	
	public void updateSeekBar(int min, int max, int step) {
		mMax = max;
		mMin = min;
		if (step > 0){
			mStep = step;
		}
		if (mSeekBar != null){
			mSeekBar.setMax((mMax-mMin)/mStep);
		}
	}

	private void showDialog() {
		mAlertDialog.setTitle(getTitle());
		CharSequence s = getSummary();
		if (s != null) {
			String[] ss = s.toString().split(" ");
			try {
				int i = Integer.parseInt(ss[0]);
				mSeekBarValue.setText(i + "");
				mSeekBar.setProgress((i - mMin) / mStep);
			} catch (Exception e) {

			}
		}
		mAlertDialog.show();
	}

	private void doStep(boolean f) {
		int v = mSeekBar.getProgress();
		v = mMin + v*mStep;
		if (f) {
			v += mStep;
			if (v > mMax) {
				v = mMax;
			}
		} else {
			v -= mStep;
			if (v < mMin) {
				v = mMin;
			}
		}

		mSeekBar.setProgress((v - mMin) / mStep);
	}

	private void doSetValue() {
		if (getOnPreferenceChangeListener() != null) {
			getOnPreferenceChangeListener().onPreferenceChange(this,
					mSeekBarValue.getText());
		}
	}

	AlertDialog mAlertDialog;

	@Override
	protected View onCreateView(ViewGroup parent) {
		// TODO Auto-generated method stub
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getContext());

		alertDialogBuilder.setPositiveButton(R.string.ok,
				new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						doSetValue();
					}
				});

		alertDialogBuilder.setNegativeButton(R.string.cancel,
				new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				});

		View view = LayoutInflater.from(getContext()).inflate(
				R.layout.seekbar_dialog_preferece, parent, false);

		mAlertDialog = alertDialogBuilder.create();
		mAlertDialog.setView(view);

		mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
		mSeekBarValue = (TextView) view.findViewById(R.id.value);
		mSeekBar.setMax((mMax-mMin)/mStep);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mSeekBarValue.setText("" + (progress*mStep + mMin));
			}
		});

		view.findViewById(R.id.btn_minus).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						doStep(false);
					}
				});

		view.findViewById(R.id.btn_add).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						doStep(true);
					}
				});

		return super.onCreateView(parent);
	}
}
