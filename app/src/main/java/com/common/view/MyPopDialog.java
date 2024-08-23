package com.common.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.canboxsetting.R;

public class MyPopDialog extends Dialog {

    private ListView mLv;
    private ArrayAdapter<String> mAdapter;

    private String[] mListString;
    private int[] mListTextID;
    private Handler mHandler;

    public MyPopDialog(Context context) {
        super(context);
    }

    public void updateList(String[] ss) {
        mListString = ss;
        if (mAdapter != null) {

        }
    }

    public void updateList(int[] ii) {
        mListTextID = ii;
        if (mAdapter != null) {

        }
    }

    public void setCallbackHandler(Handler h) {
        mHandler = h;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pop_view);
        Window dialogWindow = getWindow();
        dialogWindow.setBackgroundDrawable(null); // 设置弹出位置
        // dialogWindow.setGravity(Gravity.TOP | Gravity.RIGHT);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);

        mLv = (ListView) findViewById(R.id.pop_views);

        mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        if (mListString != null) {
            for (String s : mListString) {
                mAdapter.add(s);
            }
        }

        if (mListTextID != null) {
            for (int s : mListTextID) {
                mAdapter.add(getContext().getString(s));
            }
        }
        mLv.setAdapter(mAdapter);

        mLv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int postion, long id) {
                mHandler.sendEmptyMessage(postion);
                dismiss();
            }
        });

        findViewById(R.id.pop_window).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dismiss();
            }
        });
        ;
    }

}
