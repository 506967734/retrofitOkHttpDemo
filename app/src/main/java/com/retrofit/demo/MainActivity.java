package com.retrofit.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.retrofit.demo.entity.api.VersionPostApi;
import com.retrofit.demo.entity.resulte.Version;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zd.retrofitlibrary.exception.ApiException;
import com.zd.retrofitlibrary.http.HttpManager;
import com.zd.retrofitlibrary.listener.HttpOnNextListener;
import com.zd.retrofitlibrary.utils.JsonUtil;

public class MainActivity extends RxAppCompatActivity  implements View.OnClickListener, HttpOnNextListener {
    private TextView tvMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMsg = (TextView) findViewById(R.id.tv_msg);
        findViewById(R.id.btn_rx).setOnClickListener(this);
        findViewById(R.id.btn_rx_all).setOnClickListener(this);
        findViewById(R.id.btn_rx_mu_down).setOnClickListener(this);
        findViewById(R.id.btn_rx_uploade).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_rx_all:
                //manager.doHttpDeal(new LoginPostApi().setUserCode("CS").setCompanyId("1007").setPassword("uirVkGIR7nZntkDRX8jjcA=="));
                break;
            case R.id.btn_rx:
                HttpManager.getInstance().setAppCompatActivity(this).doHttpDeal(new VersionPostApi(),this);
                //manager.doHttpDeal(new VersionPostApi());
                break;
        }
    }


    @Override
    public void onNext(String resulte, String mothead) {
        /*post返回处理*/
        Version version = (Version) JsonUtil.jsonAnalysisModel(resulte, Version.class);
        tvMsg.setText("post返回：\n" + version.toString());
    }

    @Override
    public void onError(ApiException e, String method) {
        tvMsg.setText("失败：" + method + "\ncode=" + e.getCode() + "\nmsg:" + e.getDisplayMessage());
    }
}
