package com.common.view;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.canboxsetting.R;


public class MyPreferenceEdit extends Preference {

	public int title;

	public MyPreferenceEdit(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		init(context, attrs);
	}

	public MyPreferenceEdit(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context, attrs);

	}

	public MyPreferenceEdit(Context context) {
		super(context);
		init(context, null);
	}

	private void init(Context context, AttributeSet attrs) {
		setLayoutResource(R.layout.preference_edit);
	}

	protected View onCreateView(ViewGroup parent) {
		View v = super.onCreateView(parent);
		// Log.d("bb", ""+v.findViewById(R.id.prefrence_button1));
		// getKey()
		((Button) v.findViewById(R.id.prefrence_a))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(mButtonCallBack!=null){
							mButtonCallBack.callback(getKey(), true);
						}
					}
				});
		((Button) v.findViewById(R.id.prefrence_m))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(mButtonCallBack!=null){
							mButtonCallBack.callback(getKey(), false);
						}
					}
				});
		
		return v;
	}

	public void setCallback(IButtonCallBack cb){
		mButtonCallBack = cb;
	}
	private IButtonCallBack mButtonCallBack;
	public static interface IButtonCallBack {
		public void callback(String key, boolean add);
	};
}
