package com.retrofit.demo;

import android.app.Application;

import com.zd.retrofitlibrary.RxRetrofitApp;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RxRetrofitApp.init(this, BuildConfig.DEBUG);
    }
}
