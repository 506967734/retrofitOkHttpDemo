package com.retrofit.demo.entity.api;


import com.retrofit.demo.netservice.HttpPostService;
import com.zd.retrofitlibrary.api.BaseApi;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * 登录接口
 */
public class LoginPostApi extends BaseApi {
    private String userCode;
    private String companyId;
    private String password;

    /**
     * 默认初始化需要给定初始设置
     * 可以额外设置请求设置加载框显示，回调等（可扩展）
     * 设置可查看BaseApi
     */
    public LoginPostApi() {
        setCache(false);
        setCancel(false);
        setShowProgress(false);
        setMethod("integration/xml.do?_serviceid=98538&_method=usersLoginProcess1&_service_password=WBSAgciAfDnRdabm&_response_type=json");
    }


    public LoginPostApi setUserCode(String userCode) {
        this.userCode = userCode;
        return this;
    }


    public LoginPostApi setCompanyId(String companyId) {
        this.companyId = companyId;
        return this;
    }


    public LoginPostApi setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public Observable getObservable(Retrofit retrofit) {
        HttpPostService httpService = retrofit.create(HttpPostService.class);
        return httpService.getLogin(userCode, password, companyId);
    }

}
