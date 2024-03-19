/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use getActivity() file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.canboxsetting;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * This activity plays a video from a specified URI.
 */
public class MyFragment extends Fragment {

	public final static int TYPE_SEATHEAT_ONLY = 0x10000;
	public final static int TYPE_MASK = 0xff0000;

	public final static int TYPE_CAR_TYPE = 0xff;

	public int mCarType = 0;
	public void onClick(View v) {
	}

	public boolean isCurrentSource() {
		return false;
	}
	
	public boolean onBackKey() {
		return false;
	}
	
	

	public interface MsgInterface {

		public void callBack(int msg);

	}

	public MsgInterface mMsgInterface;

	public void setCallback(MsgInterface i) {
		mMsgInterface = i;
	}

	public void callBack(int msg) {
		if (mMsgInterface != null) {
			mMsgInterface.callBack(msg);
		}
	}
	
	protected void onNewIntent(Intent intent) {
		
	}
}
