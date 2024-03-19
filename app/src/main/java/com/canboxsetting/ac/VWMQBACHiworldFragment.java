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

package com.canboxsetting.ac;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.R.array;
import com.canboxsetting.R.drawable;
import com.canboxsetting.R.id;
import com.canboxsetting.R.layout;
import com.canboxsetting.R.string;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * This activity plays a video from a specified URI.
 */
public class VWMQBACHiworldFragment extends MyFragment {
	private static final String TAG = "VWMQBAirControlFragment";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

	}

	private CommonUpdateView mCommonUpdateView;
	private View mMainView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.ac_vwmqb_hiwordl, container,
				false);
		mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);
		return mMainView;
	}

	private void sendCanboxInfoParam2(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0x2, 0x3a, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo0x90(int d0) {
		byte[] buf = new byte[] { (byte) 0x2, 0x0a, (byte) d0, 0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private final static int[][] CMD_ID = new int[][] {
			{ R.id.power, 0x2 },
			{ R.id.inner_loop_auto, 0x0e },
			{ R.id.ac, 0xf },
			{ R.id.sync, 0x11 },
			{ R.id.rear_lock, 0x12 },
			{ R.id.ac_max, 0x8101 },
			{ R.id.ac_auto, 0x8100 },
			{ R.id.max, 0x8102 },
			{ R.id.inner_loop, 0x0113 },

			{ R.id.wind_horizontal1, 0x18 },
			{ R.id.wind_down1, 0x19 },
			{ R.id.wind_up1, 0x1a },

			{ R.id.wind_minus, 0x117 },
			{ R.id.wind_add, 0x117 },
			//
			//
			{ R.id.con_left_temp_up, 0x114 },
			{ R.id.con_left_temp_down, 0x114 },
			{ R.id.con_right_temp_up, 0x115 },
			{ R.id.con_right_temp_down, 0x115 },
			{ R.id.con_left_temp_rear_up, 0x116 },
			{ R.id.con_left_temp_rear_down, 0x116 },

			{ R.id.left_seat_heat, 0x121 },
			{ R.id.right_seat_heat, 0x122 },
			{ R.id.left_seat_refrigeration, 0x125 },
			{ R.id.right_seat_refrigeration, 0x126 },

			// rear

			{ R.id.wheel, 0x23 },
			{ R.id.wind_down1_rear, 0x2a01 },
			{ R.id.wind_horizontal1_rear, 0x2a02 },
			{ R.id.wind_horizontal_down_rear, 0x2a03 },

			{ R.id.wind_minus_rear, 0x129 }, { R.id.wind_add_rear, 0x129 },

			{ R.id.power_rear, 0x27 }, { R.id.ac_auto_rear, 0x28 },

	};

	private int getCmd(int id) {
		for (int i = 0; i < CMD_ID.length; ++i) {
			if (CMD_ID[i][0] == id) {
				return (CMD_ID[i][1] & 0xffffff);
			}
		}
		return 0;
	}

	private int getTemp(boolean add, int style) {
		int param;
		if (style == 0) {
			param = mCommonUpdateView.getTempLeft();
		} else if (style == 1) {
			param = mCommonUpdateView.getTempRight();
		} else {
			param = mCommonUpdateView.getTempRear();
		}

		if (add) {
			if (mCommonUpdateView.getTempUnit() == 0) {
				if (param >= 32 && param < 59) {
					param++;
				} else if (param == 0) {
					param = 32;
				} else {
					param = 0xff;
				}
			} else {
				if (param >= 61 && param < 85) {
					param++;
					param *= 2;
				} else if (param == 0) {
					param = 61;
					param *= 2;
				} else {
					param = 0xff;
				}
			}
		} else {
			if (mCommonUpdateView.getTempUnit() == 0) {
				if (param > 32 && param <= 59) {
					param--;
				} else if (param == 0xff) {
					param = 59;
				} else {
					param = 0;
				}
			} else {
				if (param > 61 && param <= 85) {
					param--;
					param *= 2;
				} else if (param == 0xff) {
					param = 85;
					param *= 2;
				} else {
					param = 0;
				}
			}
		}

		return param;
	}

	private void showRear(boolean show) {
		if (show) {
			mMainView.findViewById(R.id.ac_layout_rear).setVisibility(
					View.VISIBLE);
			mMainView.findViewById(R.id.ac_layout_front).setVisibility(
					View.GONE);
		} else {
			mMainView.findViewById(R.id.ac_layout_rear)
					.setVisibility(View.GONE);
			mMainView.findViewById(R.id.ac_layout_front).setVisibility(
					View.VISIBLE);
		}
	}

	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.air_rear) {
			showRear(true);
		} else if (id == R.id.air_front) {
			showRear(false);
		} else {
			int cmd = getCmd(id);

			int param = 0;
			if ((cmd & 0xff00) == 0) {
				if (!v.isSelected()) {
					param = 1;
				}
				sendCanboxInfoParam2((cmd & 0xff), param);
			} else if ((cmd & 0xff00) == 0x8100 || (cmd & 0xff00) == 0x2a00) {
				sendCanboxInfoParam2((cmd & 0xff00) >> 8, (cmd & 0xff));
			} else {
                if (id == R.id.inner_loop) {
                    if (mCommonUpdateView.getLoopInner() == 0) {
                        param = 1;
                    }
                } else if (id == R.id.wind_minus) {
                    param = mCommonUpdateView.getWind();
                    if (param > 0) {
                        param--;
                    }
                } else if (id == R.id.wind_add) {
                    param = mCommonUpdateView.getWind();
                    if (param < 7) {
                        param++;
                    }
                } else if (id == R.id.wind_minus_rear) {
                    param = mCommonUpdateView.getWindRear();
                    if (param > 0) {
                        param--;
                    }
                } else if (id == R.id.wind_add_rear) {
                    param = mCommonUpdateView.getWindRear();
                    if (param < 7) {
                        param++;
                    }
                } else if (id == R.id.con_left_temp_up) {
                    param = getTemp(true, 0);
                } else if (id == R.id.con_left_temp_down) {
                    param = getTemp(false, 0);
                } else if (id == R.id.con_right_temp_up) {
                    param = getTemp(true, 1);
                } else if (id == R.id.con_right_temp_down) {
                    param = getTemp(false, 1);
                } else if (id == R.id.con_left_temp_rear_up) {
                    param = getTemp(true, 2);
                } else if (id == R.id.con_left_temp_rear_down) {
                    param = getTemp(false, 2);
                } else if (id == R.id.left_seat_heat) {
                    param = mCommonUpdateView.getHeatLeft();
                    param = (param + 1) % 4;
                } else if (id == R.id.right_seat_heat) {
                    param = mCommonUpdateView.getHeatRight();
                    param = (param + 1) % 4;
                } else if (id == R.id.left_seat_refrigeration) {
                    param = mCommonUpdateView.getRefrigerationLeft();
                    param = (param + 1) % 4;
                } else if (id == R.id.right_seat_refrigeration) {
                    param = mCommonUpdateView.getRefrigerationRight();
                    param = (param + 1) % 4;
                }

				sendCanboxInfoParam2((cmd & 0xff), param);
			}
		}

	}

	@Override
	public void onPause() {
		unregisterListener();
		super.onPause();
	}

	@Override
	public void onResume() {
		registerListener();
		sendCanboxInfo0x90(0x31);
		super.onResume();
	}

	private BroadcastReceiver mReceiver;

	private void unregisterListener() {
		if (mReceiver != null) {
			getActivity().unregisterReceiver(mReceiver);
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
						String cmd = intent
								.getStringExtra(MyCmd.EXTRA_COMMON_CMD);
						if ("ac".equals(cmd)) {
							byte[] buf = intent.getByteArrayExtra("buf");
							if (buf != null) {

								mCommonUpdateView.postChanged(
										CommonUpdateView.MESSAGE_AIR_CONDITION,
										0, 0, buf);

							}
						}
					}
				}
			};
			IntentFilter iFilter = new IntentFilter();
			iFilter.addAction(MyCmd.BROADCAST_SEND_FROM_CAN);

			getActivity().registerReceiver(mReceiver, iFilter);
		}
	}
}
