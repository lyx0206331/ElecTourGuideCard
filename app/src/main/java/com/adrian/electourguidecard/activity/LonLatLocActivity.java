package com.adrian.electourguidecard.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.adrian.electourguidecard.R;
import com.adrian.electourguidecard.tools.CommUtil;
import com.adrian.electourguidecard.tools.Constants;
import com.adrian.electourguidecard.tools.NetworkUtil;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class LonLatLocActivity extends BaseActivity implements OnGetGeoCoderResultListener, NetworkUtil.OnGetServerDataListener {

    private static final String TAG = LonLatLocActivity.class.getName();

    private static final int MSG_IDS_NULL = 0;
    private static final int MSG_GPS_NULL = 1;
    private static final int MSG_ERROR = 3;
    private static final int MSG_REFRESH_DATA = 4;

    private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private BaiduMap mBaiduMap = null;
    private MapView mMapView = null;
    private EditText mLongitudeET;
    private EditText mLatitudeET;
    private Button mSearchBtn;

    private NetworkUtil networkUtil;
    private String id;

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
        setContentView(R.layout.activity_lon_lat_loc);
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mLongitudeET = (EditText) findViewById(R.id.lon);
        mLatitudeET = (EditText) findViewById(R.id.lat);
        mSearchBtn = (Button) findViewById(R.id.reversegeocode);
        mBaiduMap = mMapView.getMap();

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAddr();
            }
        });
    }

    private void searchAddr() {
        if (TextUtils.isEmpty(mLongitudeET.getText()) || TextUtils.isEmpty(mLatitudeET.getText())) {
            CommUtil.showToast("经纬度不能为空");
            return;
        }
        //GPS纠偏
        Ion.with(this).load("http://api.zdoz.net/transgpsbd.aspx?lat=" + mLatitudeET.getText() + "&lng=" + mLongitudeET.getText())
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                CommUtil.e(TAG, result.toString());
                if (result != null) {
                    float lat = result.get("Lat").getAsFloat();
                    float lng = result.get("Lng").getAsFloat();
                    LatLng ptCenter = new LatLng(lat, lng);
                    // 反Geo搜索
                    boolean isSearched = mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                            .location(ptCenter));
                    if (isSearched) {
                        mHandler.sendEmptyMessageDelayed(MSG_REFRESH_DATA, 5000);
                    }
                }
            }
        });
    }

    @Override
    protected void loadData() {
        id = getIntent().getStringExtra("id");
        requestData();
    }

    private void requestData() {
        if (CommUtil.getNetworkStatus(this) != -1) {
            networkUtil.getData(Constants.REQUEST_COOR + id);
        } else {
            CommUtil.showToast("网络异常，请检查网络连接！");
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            CommUtil.showToast("抱歉，未能找到结果");
            return;
        }
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_marka)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));
        CommUtil.showToast(result.getAddress());
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        mSearch.destroy();
        mHandler.removeMessages(MSG_REFRESH_DATA);
        super.onDestroy();
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
            String[] ids = idstr.split(";");
            if (ids != null && ids.length > 0) {
                networkUtil.getData(Constants.REQUEST_COOR + ids[0]);
            }
        } else if (flag.contains(Constants.REQUEST_COOR)) {
            if (TextUtils.isEmpty(data) || data.equals("gps:;")) {
                mHandler.sendEmptyMessage(MSG_GPS_NULL);
                return;
            }
            String gpsStr = data.substring(data.indexOf(":") + 1, data.length() - 1);
            final String[] gps = gpsStr.split(",");
            if (gps != null && gps.length == 2) {
                CommUtil.e(TAG, "gps : " + gps[0] + "/" + gps[1]);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLatitudeET.setText(gps[0]);
                        mLongitudeET.setText(gps[1]);
                        searchAddr();
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
                case MSG_GPS_NULL:
                    CommUtil.showToast("无此ID信息");
                    break;
                case MSG_ERROR:
                    CommUtil.showToast("服务器异常");
                    break;
                case MSG_REFRESH_DATA:
                    requestData();
                    break;
                default:
                    break;
            }
        }
    };
}
