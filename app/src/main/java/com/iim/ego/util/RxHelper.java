package com.iim.ego.util;

import com.iim.ego.base.BaseActivity;
import com.iim.ego.base.BaseFragment;
import com.iim.ego.base.BaseFragmentActivity;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

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



}