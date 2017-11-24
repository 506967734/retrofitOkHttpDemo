package com.retrofit.demo.netservice;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 测试接口service-post相关
 * Created by WZG on 2016/12/19.
 */

public interface HttpPostService {


    @GET("appcenter/api/app/v/android")
    Observable<String> getAllVersion();

    @POST("integration/xml.do?_serviceid=98538&_method=usersLoginProcess1&_service_password=WBSAgciAfDnRdabm&_response_type=json")
    Observable<String> getLogin(@Query("DTPVUSERS.USER_CODE") String userCode, @Query("DTPVUSERS.PASSWORD") String password, @Query("DTPVUSERS.COMPANY_ID") String companyId);

}
