package com.zd.retrofitlibrary.subscribers;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.zd.retrofitlibrary.RxRetrofitApp;
import com.zd.retrofitlibrary.api.BaseApi;
import com.zd.retrofitlibrary.exception.ApiException;
import com.zd.retrofitlibrary.exception.CodeException;
import com.zd.retrofitlibrary.exception.FactoryException;
import com.zd.retrofitlibrary.exception.HttpTimeException;
import com.zd.retrofitlibrary.http.cookie.CookieResulte;
import com.zd.retrofitlibrary.listener.HttpOnNextListener;
import com.zd.retrofitlibrary.utils.AppUtil;
import com.zd.retrofitlibrary.utils.CookieDbUtil;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import rx.Subscriber;

/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 * Created by WZG on 2016/7/16.
 */
public class ProgressSubscriber<T> extends Subscriber<T> {
    /*是否弹框*/
    private boolean showPorgress = true;
    //    回调接口
    private HttpOnNextListener mSubscriberOnNextListener;
    //    软引用反正内存泄露
    private Context mActivity;
    //    加载框可自己定义
    private ProgressDialog pd;
    /*请求数据*/
    private BaseApi api;


    /**
     * 构造
     *
     * @param api
     */
    public ProgressSubscriber(BaseApi api, HttpOnNextListener listenerSoftReference, Context
            mActivity) {
        this.api = api;
        this.mSubscriberOnNextListener = listenerSoftReference;
        this.mActivity = mActivity;
        setShowPorgress(api.isShowProgress());
        if (api.isShowProgress()) {
            initProgressDialog(api.isCancel());
        }
    }


    /**
     * 初始化加载框
     */
    private void initProgressDialog(boolean cancel) {
        if (pd == null && mActivity != null) {
            pd = new ProgressDialog(mActivity);
            pd.setCancelable(cancel);
            if (cancel) {
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        onCancelProgress();
                    }
                });
            }
        }
    }


    /**
     * 显示加载框
     */
    private void showProgressDialog() {
        if (!isShowPorgress()) return;
        if (pd == null || mActivity == null) return;
        if (!pd.isShowing()) {
            pd.show();
        }
    }


    /**
     * 隐藏
     */
    private void dismissProgressDialog() {
        if (!isShowPorgress()) return;
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }


    public boolean isShowPorgress() {
        return showPorgress;
    }

    /**
     * 是否需要弹框设置
     *
     * @param showPorgress
     */
    public void setShowPorgress(boolean showPorgress) {
        this.showPorgress = showPorgress;
    }

    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    public void onStart() {
        showProgressDialog();
        /*缓存并且有网*/
        if (api.isCache() && AppUtil.isNetworkAvailable(RxRetrofitApp.getApplication())) {
             /*获取缓存数据*/
            CookieResulte cookieResulte = CookieDbUtil.getInstance().queryCookieBy(api.getUrl());
            if (cookieResulte != null) {
                long time = (System.currentTimeMillis() - cookieResulte.getTime()) / 1000;
                if (time < api.getCookieNetWorkTime()) {
                    if (mSubscriberOnNextListener != null) {
                        handleBaseResult(cookieResulte.getResulte(), api.getMethod());
                    }
                    onCompleted();
                    unsubscribe();
                }
            }
        }
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onCompleted() {
        dismissProgressDialog();
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        /*需要緩存并且本地有缓存才返回*/
        if (api.isCache()) {
            getCache();
        } else {
            errorDo(e);
        }
        dismissProgressDialog();
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
         /*缓存处理*/
        if (api.isCache()) {
            CookieResulte resulte = CookieDbUtil.getInstance().queryCookieBy(api.getUrl());
            long time = System.currentTimeMillis();
            /*保存和更新本地数据*/
            if (resulte == null) {
                resulte = new CookieResulte(api.getUrl(), t.toString(), time);
                CookieDbUtil.getInstance().saveCookie(resulte);
            } else {
                resulte.setResulte(t.toString());
                resulte.setTime(time);
                CookieDbUtil.getInstance().updateCookie(resulte);
            }
        }
        if (mSubscriberOnNextListener != null) {
            handleBaseResult((String) t, api.getMethod());
        }
    }


    /**
     * 处理返回字符串
     *
     * @param result
     */
    private void handleBaseResult(String result, String method) {
        try {
            JSONObject root = new JSONObject(result);
            if (root.getInt("status") == 0) {
                if (root.has("data")) {
                    try {
                        mSubscriberOnNextListener.onNext(root.getString("data"), method);
                    } catch (JSONException e) {
                        errorDo(e);
                    }
                }
            } else {
                //httpOnNextListener.onError(new ApiException(CodeException.UNKNOWN_ERROR, root.getString("msg")), api.getMethod());
                //new ApiException(CodeException.RUNTIME_ERROR, root.getString("msg")), api.getMethod());
                ApiException e = new ApiException(CodeException.FAIL_ERROR, root.getString("msg"));
                errorDo(e);
            }
        } catch (JSONException e) {
            errorDo(FactoryException.analysisExcetpion(e));
        }
    }

    /**
     * 获取cache数据
     */
    private void getCache() {
        Observable.just(api.getUrl()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                errorDo(e);
            }

            @Override
            public void onNext(String s) {
                           /*获取缓存数据*/
                CookieResulte cookieResulte = CookieDbUtil.getInstance().queryCookieBy(s);
                if (cookieResulte == null) {
                    throw new HttpTimeException(HttpTimeException.NO_CHACHE_ERROR);
                }
                long time = (System.currentTimeMillis() - cookieResulte.getTime()) / 1000;
                if (time < api.getCookieNoNetWorkTime()) {
                    if (mSubscriberOnNextListener != null) {
                        handleBaseResult(cookieResulte.getResulte(), api.getMethod());
                    }
                } else {
                    CookieDbUtil.getInstance().deleteCookie(cookieResulte);
                    throw new HttpTimeException(HttpTimeException.CHACHE_TIMEOUT_ERROR);
                }
            }
        });
    }


    /**
     * 错误统一处理
     *
     * @param e
     */
    private void errorDo(Throwable e) {
        if (mActivity == null) return;
        HttpOnNextListener httpOnNextListener = mSubscriberOnNextListener;
        if (httpOnNextListener == null) return;
        if (e instanceof ApiException) {
            httpOnNextListener.onError((ApiException) e, api.getMethod());
        } else if (e instanceof HttpTimeException) {
            HttpTimeException exception = (HttpTimeException) e;
            httpOnNextListener.onError(new ApiException(exception, CodeException.RUNTIME_ERROR, exception.getMessage()), api.getMethod());
        } else {
            httpOnNextListener.onError(new ApiException(e, CodeException.UNKNOWN_ERROR, e.getMessage()), api.getMethod());
        }
    }


}