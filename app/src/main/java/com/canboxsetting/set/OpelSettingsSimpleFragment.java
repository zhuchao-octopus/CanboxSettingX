package com.canboxsetting.set;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.SystemConfig;

public class OpelSettingsSimpleFragment extends Fragment {
	private static final String TAG = "OpelSettingsSimpleFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private View mMainView;
	

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mMainView = inflater.inflate(R.layout.mazda_open_simple_settings,
				container, false);

		View v;
		for(int i = 0; i<BUTTON_ID.length;++i){
			v = mMainView.findViewById(BUTTON_ID[i][0]);
			if(v!=null){
				v.setOnTouchListener(mOnTouchListener);
				v.setOnLongClickListener(mOnLongClickListener);
			}
		}
		
		v = mMainView.findViewById(R.id.key_type);
		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showSingleChoiceDialog();
			}
		});
		
		mSelectItem = SystemConfig.getIntProperty(getActivity(),
				MachineConfig.VALUE_CANBOX_OPEL);
		((TextView) v).setText(single_list[mSelectItem]);
		
		return mMainView;
	}
	int mSelectItem;
	final String []single_list = {"Low","Middle, High"};
	private void showSingleChoiceDialog() {
		 
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.subaru_settings_5);
        builder.setSingleChoiceItems(single_list, mSelectItem, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
            	SystemConfig.setIntProperty(getActivity(), MachineConfig.VALUE_CANBOX_OPEL, which);
            	TextView v = (TextView)mMainView.findViewById(R.id.key_type);
            	v.setText(single_list[which]);
            	mSelectItem = which;
                dialog.dismiss();
            }
        });
 
        AlertDialog dialog = builder.create();
        dialog.show();
    }

	
	private void sendCanboxInfo(int d0) {
		byte[] buf = new byte[] {  0x05, 0x6, (byte) d0};
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private int mKeyId;
	View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
//			Log.d("allen3", "onKey!!");
			mKeyId = getKey(v.getId());
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mHandler.removeMessages(0);
				if (mKeyId != 0) {
					sendCanboxInfo(mKeyId);
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				mHandler.removeMessages(0);
				sendCanboxInfo(0);
				mKeyId = 0;
			}

			return false;
		};
	};
	
	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeMessages(0);
	}

	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {			
			super.handleMessage(msg);
			if (mKeyId != 0) {
				sendCanboxInfo(mKeyId);
			}
			mHandler.sendEmptyMessageDelayed(0, 300);
		}
	};
	View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			if (mKeyId != 0) {
				sendCanboxInfo(mKeyId);
			}
			mHandler.sendEmptyMessageDelayed(0, 300);
			return false;
		}
	};
	
	private int getKey(int id){
		int ret = 0;
		for(int i = 0; i<BUTTON_ID.length;++i){
			if(BUTTON_ID[i][0]==id){
				ret = BUTTON_ID[i][1];
				break;
			}
		}
		return ret;
	}
	
	private final static int[][] BUTTON_ID = { 
		{ R.id.ok, 0x1 },
		{ R.id.settings, 0x2 }, 
		{ R.id.bc, 0x3 }, 
		{ R.id.left, 0x4 },
		{ R.id.right, 0x5 }, 
		{ R.id.fam, 0x6 }, 
		{ R.id.cdmp3, 0x7 },
		{ R.id.num1, 0x8 }, 
		{ R.id.num2, 0x9 },
		{ R.id.num3, 0xa }, 
		{ R.id.num4, 0xb }, 
		{ R.id.num5, 0xc },
		{ R.id.num6, 0xd }, 
		{ R.id.num7, 0xe }, 
		{ R.id.num8, 0xf },
		{ R.id.num9, 0x10 }, 
		{ R.id.clock, 0x15 },  
		{ R.id.set, 0x16 }, 
		};
	
	
}
