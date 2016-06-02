package com.adrian.electourguidecard.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.adrian.electourguidecard.R;
import com.adrian.electourguidecard.adapter.IdsAdapter;
import com.adrian.electourguidecard.tools.CommUtil;
import com.adrian.electourguidecard.tools.Constants;
import com.adrian.electourguidecard.tools.NetworkUtil;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Arrays;

public class IDListActivity extends BaseActivity implements NetworkUtil.OnGetServerDataListener {

    private static final String TAG = IDListActivity.class.getName();

    private static final int MSG_IDS_NULL = 0;
    private static final int MSG_ERROR = 3;

    private ListView mIdsLV;
    private IdsAdapter mAdapter;

    private NetworkUtil networkUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {
        networkUtil = new NetworkUtil(this);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_idlist);
        mIdsLV = (ListView) findViewById(R.id.lv_ids);
        mAdapter = new IdsAdapter(this);
        mIdsLV.setAdapter(mAdapter);
        mIdsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(IDListActivity.this, LonLatLocActivity.class);
                intent.putExtra("id", (String)((IdsAdapter)parent.getAdapter()).getItem(position));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void loadData() {
        if (CommUtil.getNetworkStatus(this) == -1) {
            CommUtil.showToast("网络异常，请检查网络");
        } else {
            networkUtil.getData(Constants.REQUEST_IDS);
        }
    }

    @Override
    public void onGetServerData(String flag, String data) {
        if (flag.equals(Constants.REQUEST_IDS)) {
            if (TextUtils.isEmpty(data)) {
                mHandler.sendEmptyMessage(MSG_IDS_NULL);
                return;
            }
            String idstr = data.substring(data.indexOf(":") + 1, data.length() - 1);
            CommUtil.e(TAG, idstr);
            final String[] ids = idstr.split(";");
//            for (String id : ids) {
//                CommUtil.e(TAG, id);
//            }
            if (ids != null && ids.length > 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setList(Arrays.asList(ids));
                    }
                });
            }
        }
    }

    @Override
    public void onException(int errorCode) {
        if (errorCode == Constants.SERVER_EXC_CODE) {
            mHandler.sendEmptyMessage(MSG_ERROR);
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_IDS_NULL:
                    CommUtil.showToast("id列表为空");
                    break;
                case MSG_ERROR:
                    CommUtil.showToast("服务器异常");
                    break;
                default:
                    break;
            }
        }
    };
}
