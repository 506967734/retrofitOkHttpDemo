package com.retrofit.demo.entity.api;


import com.retrofit.demo.netservice.HttpPostService;
import com.zd.retrofitlibrary.api.BaseApi;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * 测试数据
 */
public class VersionPostApi extends BaseApi {
    //    接口需要传入的参数 可自定义不同类型
    private boolean all;

    /**
     * 默认初始化需要给定初始设置
     * 可以额外设置请求设置加载框显示，回调等（可扩展）
     * 设置可查看BaseApi
     */
    public VersionPostApi() {
        setCache(false);
        setMethod("appcenter/api/app/v/android");
    }


    @Override
    public Observable getObservable(Retrofit retrofit) {
        HttpPostService httpService = retrofit.create(HttpPostService.class);
        return httpService.getAllVersion();
    }

}
