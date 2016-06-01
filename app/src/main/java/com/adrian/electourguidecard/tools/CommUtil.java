package com.adrian.electourguidecard.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.adrian.electourguidecard.application.MyApplication;

/**
 * Created by adrian on 16-5-30.
 */
public class CommUtil {

    private static final boolean DEBUG = true;

    public static void e(String tag, String msg) {
        if (DEBUG)
            Log.e(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (DEBUG)
            Log.v(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (DEBUG)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (DEBUG)
            Log.d(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (DEBUG)
            Log.w(tag, msg);
    }

    public static void showToast(String msg) {
        Toast.makeText(MyApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(int msgId) {
        Toast.makeText(MyApplication.getInstance(), msgId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取网络状态
     *
     * @param ctx
     * @return -1:无网络;0:移动网络;1:wifi网络;2:以太网
     */
    public static int getNetworkStatus(Context ctx) {
        int status = -1;
        ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        // NetworkInfo mobileInfo =
        // manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        // NetworkInfo wifiInfo =
        // manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo netInfo = manager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isAvailable()) {
            if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                ///// WiFi网络
                status = 1;
            } else if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                ///// 有线网络
                status = 2;
            } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                ///////// 3g网络
                status = 0;
            }
        } else {
            status = -1;
        }
        return status;
    }
}
