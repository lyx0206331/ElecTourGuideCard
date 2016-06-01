package com.adrian.electourguidecard.application;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by adrian on 16-5-30.
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
