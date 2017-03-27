package com.iim.ego.util;

import com.iim.ego.base.BaseActivity;
import com.iim.ego.base.BaseFragment;
import com.iim.ego.base.BaseFragmentActivity;
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
     * @param activity/fragment activity/fragment对象，用于绑定生命周期
     * @return Transformer
     */
    public static <T> ObservableTransformer<T, T> io_main(BaseActivity activity) {
        return upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(RxLifecycleAndroid.bindActivity(activity.lifecycle()));
    }
    public static <T> ObservableTransformer<T, T> io_main(BaseFragmentActivity activity) {
        return upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(RxLifecycleAndroid.bindActivity(activity.lifecycle()));
    }
    public static <T> ObservableTransformer<T, T> io_main(BaseFragment fragment) {
        return upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(RxLifecycleAndroid.bindFragment(fragment.lifecycle()));
    }

    /**
     * Rx优雅处理服务器返回
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> handleResult() {
        return upstream ->
                upstream.flatMap(result -> {
                            BaseBean<T> baseResult = (BaseBean<T>) result;
                            if (baseResult.isSuccess()) {
                                return createData(baseResult.data);
                            } else if (baseResult.isTokenInvalid()) {
                                //处理token失效，tokenInvalid方法在BaseActivity 实现
                                tokenInvalid();
                            } else {
                                return Observable.error(new Exception(baseResult.msg));
                            }
                            return Observable.empty();
                        }
                );
    }
    //正常获取数据
    private static <T> Observable<T> createData(final T t) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(t);
                subscriber.onComplete();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
    //RxJava的Token失效
    private  static <T> Observable<T> tokenInvalid() {
        return Observable.create(subscriber -> {
            try {
                //处理token失效逻辑
                subscriber.onComplete();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

}