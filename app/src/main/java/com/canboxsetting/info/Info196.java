package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.Util;

public class Info196 extends PreferenceFragment {
	private static final String TAG = "Golf7InfoSimpleFragment";

	private View mMainView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.honda_odyssey_vehicle_media_infos, container,
				false);

		setOnClick(R.id.source1);
		setOnClick(R.id.source2);
		return mMainView;
	}

	boolean mPaused = true;

	@Override
	public void onPause() {
		super.onPause();
		mPaused = true;
		unregisterListener();
	}

	@Override
	public void onResume() {
		mPaused = false;
		super.onResume();

		BroadcastUtil
				.sendToCarServiceSetSource(getActivity(), MyCmd.SOURCE_AUX);
		
		registerListener();
		requestInitData();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		BroadcastUtil.sendToCarServiceSetSource(getActivity(),
				MyCmd.SOURCE_MX51);
	}

	private final static int[] INIT_CMDS = { 0x7700,0x7c00,0x7b00, };

	private void requestInitData() {
		// mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
		for (int i = 0; i < INIT_CMDS.length; ++i) {
			mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 200));
		}

	}
	
	private void sendCanboxInfo0x90(int d0) {

		byte[] buf = new byte[] { (byte) 0x90, 0x02,
				(byte) ((d0 & 0xff00) >> 8), (byte) (d0 & 0xff) };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void updateView(byte[] buf) {
		String s = "";
		switch (buf[0]) {
		case 0x7c:
			setVisibleView(R.id.st, buf[2]&0x40);
			setVisibleView(R.id.auto_select, buf[2]&0x80);
			setVisibleView(R.id.scan, buf[2]&0x20);
			setVisibleView(R.id.usb_status, buf[4]&0x80);
			

			setVisibleView(R.id.bt, buf[5]&0x80);
			
			String []ss = getResources().getStringArray(R.array.odd_bnr_play_status);

			((TextView)mMainView.findViewById(R.id.cd_play_status)).setText(ss[buf[3]&0x7]);
			((TextView)mMainView.findViewById(R.id.usb_play_status)).setText(ss[buf[4]&0x7]);

			setVisibleView(R.id.battery, 1);
			setVisibleView(R.id.signal, 1);
			int drawId = 0;
			switch(buf[5]&0x7){
			case 1:
				drawId = R.drawable.icon_44;
				break;
			case 2:
				drawId = R.drawable.icon_45;
				break;
			case 3:
				drawId = R.drawable.icon_46;
				break;
			case 4:
				drawId = R.drawable.icon_47;
				break;
			case 5:
				drawId = R.drawable.icon_48;
				break;
			case 6:
				drawId = R.drawable.icon_49;
				break;
			default:
				drawId = R.drawable.icon_44;
				break;
			}
			((ImageView)mMainView.findViewById(R.id.battery)).setImageResource(drawId);
			
			
			drawId = 0;
			switch((buf[5]&0x38)>> 3){
			case 1:
				drawId = R.drawable.icon_38;
				break;
			case 2:
				drawId = R.drawable.icon_39;
				break;
			case 3:
				drawId = R.drawable.icon_40;
				break;
			case 4:
				drawId = R.drawable.icon_41;
				break;
			case 5:
			case 6:
				drawId = R.drawable.icon_42;
				break;
			default:
				drawId = R.drawable.icon_42;
				break;
			}
			((ImageView)mMainView.findViewById(R.id.signal)).setImageResource(drawId);
			break;
		case 0x79:
			if (buf[2] == 1 || buf[3] == 0){
				s = "MUTE";
			} else {
				s = "" + (buf[3]&0xff);
			}
			((TextView)mMainView.findViewById(R.id.vol)).setText(s);
			break;
		case 0x7b:
			if (buf[2] == 0){
				setSelectedView(R.id.source1, true);
				setSelectedView(R.id.source2, false);
			} else {
				setSelectedView(R.id.source1, false);
				setSelectedView(R.id.source2, true);
			}
			break;
		case 0x77:
			int id = 0;
			int color = 0;
			switch (buf[2]) {
			case 0:
				id = R.id.cmd_id_menu_item;
				break;
			case 1:
				id = R.id.cmd_id_menu_item_line_a;
				break;
			case 2:
				id = R.id.cmd_id_menu_item_line_b;
				break;
			case 3:
				id = R.id.cmd_id_menu_item_line_c;
				break;
			}
			
			if (buf[4] == 1) {
				color = 0xffcccccc;
			} 

			byte[] string = new byte[buf.length - 6];
			Util.byteArrayCopy(string, buf, 0, 5, string.length);
			if (buf[3] == 0) {
				s = new String(string);
			} else {
				try {
					s = new String(string, "UNICODE");
				} catch (Exception e) {

				}
			}
			
			if (id != 0){
				((TextView)mMainView.findViewById(id)).setText(s);
				((TextView)mMainView.findViewById(id)).setBackgroundColor(color);
			}
			break;

		}
	}
	
	private void sendCmd(int cmd) {
		byte[] buf = new byte[] { (byte) 0xef, 0x01, (byte) (cmd)};
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void setSelectedView(int index, boolean sel) {
		if (index != 0) {
			TextView v = (TextView) mMainView.findViewById(index);
			if (v != null) {
				v.setSelected(sel);
			}
		}
	}
	
	private void setVisibleView(int index, int s) {
		if (index != 0) {
			View v = mMainView.findViewById(index);
			if (v != null) {
				v.setVisibility(s == 0 ? View.GONE : View.VISIBLE);
			}
		}
	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			if (id == R.id.source1) {
				sendCmd(0x0);
			} else if (id == R.id.source2) {
				sendCmd(0x1);
			} else {
				return;
			}

			mHandler.removeMessages(0);
			mHandler.sendEmptyMessageDelayed(0, 5000);
		}
	};

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!mPaused) {
				sendCanboxInfo0x90(msg.what);

			}
		
		}
	};
	private void setOnClick(int index) {
		if (index != 0) {
			View v = mMainView.findViewById(index);
			if (v != null) {
				v.setOnClickListener(mOnClickListener);				
			}
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
