package com.zd.retrofitlibrary.listener;

import rx.Observable;

/**
 * 回调ober对象
 * Created by WZG on 2016/12/12.
 */

public interface HttpOnNextSubListener {

    /**
     * ober成功回调
     * @param observable
     * @param method
     */
    void onNext(Observable observable, String method);
}
