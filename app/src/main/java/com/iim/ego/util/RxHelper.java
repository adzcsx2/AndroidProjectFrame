package com.iim.ego.util;

import com.iim.ego.base.BaseActivity;
import com.iim.ego.model.BaseBean;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Hoyn on 17/3/24.
 */

public class RxHelper<T> {
    /**
     * 后台线程执行同步，主线程执行异步操作
     * 并且拦截所有错误，不让app崩溃
     *
     * @param <T> 数据类型
     * @return Transformer
     */
    public static <T> ObservableTransformer<BaseBean, BaseBean> io_main(BaseActivity activity) {
        return upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(RxLifecycleAndroid.bindActivity(activity.lifecycle()))
                        .flatMap(result -> {
                            int code = result.getCode();
                            if (code == 0) {
                                //成功
                                return success(result);
                            } else if (code == 1) {
                                return activity.tokenInvalid();
                            } else {
                                return Observable.error(new Exception(result.getMsg()));
                            }
                        });
    }
    /**
     * 取得成功的数据
     * @param t
     * @param <T>
     * @return
     */
    public static <T> Observable<T> success(T t) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(t);
                subscriber.onComplete();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }


}