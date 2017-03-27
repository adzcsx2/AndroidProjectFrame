package com.iim.ego.exception;

import android.util.Log;

import com.iim.ego.util.ToastUtil;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.functions.Consumer;
import retrofit2.HttpException;

/**
 * Created by Hoyn on 17/3/27.
 */

public class RxException<T extends Throwable> implements Consumer<T> {

    private static final String TAG = "RxException";

    private static final String SOCKETTIMEOUTEXCEPTION = "网络连接超时，请检查您的网络状态，稍后重试";
    private static final String HTTPEXCEPTION = "网络连接异常，请检查您的网络状态";
    private static final String CONNECTEXCEPTION = "网络连接异常，请检查您的网络状态";
    private static final String UNKNOWNHOSTEXCEPTION = "网络异常，请检查您的网络状态";
    private static final String UNKNOWNEXCEPTION = "网络异常，请检查您的网络状态";

    private Consumer<? super Throwable> onError;
    public RxException(Consumer<? super Throwable> onError) {
        this.onError=onError;
    }

    /**
     * Consume the given value.
     *
     * @param t the value
     * @throws Exception on error
     */
    @Override
    public void accept(T t) throws Exception {
        if (t instanceof SocketTimeoutException) {
            Log.e(TAG, "onError: SocketTimeoutException----" + SOCKETTIMEOUTEXCEPTION);
            ToastUtil.show(SOCKETTIMEOUTEXCEPTION,false);
            onError.accept(new Throwable(SOCKETTIMEOUTEXCEPTION));
        } else if (t instanceof HttpException) {
            Log.e(TAG, "onError: HttpException----" + SOCKETTIMEOUTEXCEPTION);
            ToastUtil.show(HTTPEXCEPTION,false);
            onError.accept(new Throwable(SOCKETTIMEOUTEXCEPTION));
        } else if (t instanceof ConnectException) {
            Log.e(TAG, "onError: ConnectException-----" + CONNECTEXCEPTION);
            ToastUtil.show(CONNECTEXCEPTION,false);
            onError.accept(new Throwable(CONNECTEXCEPTION));
        } else if (t instanceof UnknownHostException) {
            Log.e(TAG, "onError: UnknownHostException-----" + UNKNOWNHOSTEXCEPTION);
            ToastUtil.show(UNKNOWNHOSTEXCEPTION,false);
            onError.accept(new Throwable(UNKNOWNHOSTEXCEPTION));
        } else {
            Log.e(TAG, "onError:----" + t.getMessage());
            ToastUtil.show(UNKNOWNEXCEPTION,false);
            onError.accept(t);
        }
    }
}
