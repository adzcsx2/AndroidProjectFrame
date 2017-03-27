package com.iim.ego.base;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.iim.ego.ui.MainActivity;
import com.iim.ego.util.ToastUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;


/**
 * @author Hoyn
 * @version 1.0
 * @Title：SAFEYE@
 * @Description：
 */
public abstract class BaseActivity extends RxAppCompatActivity {



    /**
     * activity控制
     */
    public static Map<String, WeakReference<Activity>> openedActivitys = new LinkedHashMap<String, WeakReference<Activity>>();// 已经打开的activity

    protected Bundle savedInstanceState;

    //fragment控制(恢复状态)
    protected Fragment mLastFragment;
    private int layoutId;
    protected BaseActivity context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏软键盘
//        getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // 隐藏actionBar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏通知栏
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (!openedActivitys.keySet().contains(getClass().getSimpleName())) {
            openedActivitys.put(getClass().getSimpleName(),
                    new WeakReference<Activity>(this));
        }

        this.savedInstanceState = savedInstanceState;
        setContentView(layoutInit());
        context = this;
        init(savedInstanceState);
        ButterKnife.bind(this);
        bindPresenter();
        viewInit();
        bindEvent();

    }

    protected abstract int layoutInit();

    protected void bindPresenter() {
    }


    /**
     * 处理返回键
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void viewInit() {
        if (!isLoginOut()) {
        }
    }

    ;

    /**
     * 绑定事件
     */
    protected abstract void bindEvent();

    /**
     * TODO:需要考虑savedInstanceState 设置布局文件
     *
     * @author Hoyn
     */
    protected void init(Bundle savedInstanceState) {

    }

    /**
     * 判断是否是debug版本
     *
     * @return
     */
    public boolean isDebugable() {
        try {
            ApplicationInfo info = getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {

        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }


    public Activity getActivity(String className) {
        if (!openedActivitys.isEmpty()) {
            WeakReference<Activity> weakReference = openedActivitys
                    .get(className);
            if (weakReference == null) {
                return null;
            }
            Activity activity = weakReference.get();
            return activity;
        }
        return null;
    }

    /**
     * 关闭某activity
     *
     * @param className
     * @author Hoyn
     */
    public void finishActivity(String className) {
        if (!openedActivitys.isEmpty()) {
            WeakReference<Activity> weakReference = openedActivitys
                    .get(className);
            if (weakReference == null) {
                return;
            }
            Activity activity = weakReference.get();
            if (activity != null) {
                activity.finish();
            }
        }
    }

    /**
     * 关闭所有打开的activity，同时退出自己
     *
     * @author Hoyn
     */
    public void finishAllActivitys() {
        if (!openedActivitys.isEmpty()) {
            for (String clsssName : openedActivitys.keySet()) {
                WeakReference<Activity> weakReference = openedActivitys
                        .get(clsssName);
                Activity activity = weakReference.get();
                if (activity != null) {
                    activity.finish();
                }
            }
//            System.exit(0);
        }
    }


    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if (isLoginOut()) {
            //连续按2次返回键退出
            if ((System.currentTimeMillis() - exitTime) > 1000) {
                ToastUtil.show("再按一次退出",false);
                exitTime = System.currentTimeMillis();
            } else {
                finishAllActivitys();
            }
        } else {

            super.onBackPressed();
        }
    }


    /**
     * 判断是否是主页面
     *
     * @return
     */
    private boolean isLoginOut() {
        return getClass().getSimpleName().equals(MainActivity.class.getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        openedActivitys.remove(getClass().getSimpleName());
    }

    //利用反射获取fragment实例，如果该实例已经创建，则用不再创建。
    protected Fragment getFragmentInstance(Class fragmentClass) {
        FragmentManager fm = getSupportFragmentManager();

        //查找是否已存在,已存在则不需要重发创建,切换语言时系统会自动重新创建并attch,无需手动创建
        Fragment fragment = fm.findFragmentByTag(fragmentClass.getSimpleName());
        if (fragment != null) {
            return fragment;
        } else {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return fragment;
        }
    }

    /**
     * fragment切换
     *
     * @param fragment
     * @param layoutId
     * @return 最后显示的fragment
     */
    protected void switchFragment(Fragment fragment, int layoutId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager == null || layoutId <= 0) {
            return;
        }
        //全局的fragment对象，用于记录最后一次切换的fragment(当前展示的fragment)
        mLastFragment = fragment;
        this.layoutId = layoutId;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragment != null) {
            List<Fragment> fragments = fragmentManager.getFragments();
            //先判断是否为空
            if (fragments != null) {
                for (Fragment mfragment : fragments) {
                    //再把现在显示的fragment 隐藏掉
                    if (!mfragment.isHidden()) {
                        transaction.hide(mfragment);
                    }
                }
            }
            //显示现在的fragment
            if (!fragment.isAdded()) {
                // transaction.addToBackStack(null);
                transaction.add(layoutId, fragment, fragment.getClass().getSimpleName());
            } else {
                transaction.show(fragment);
            }
        }
        transaction.commit();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //存储最后一个fragment对象
        if (mLastFragment != null) {
            outState.putString("fragment", mLastFragment.getClass().getName());
            outState.putInt("layoutId", layoutId);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //取出该对象，由于此时所有的fragment是处于onAttach，hidden状态，所以只需要将它show出来就可以了。
        String fragmentSimpleName = savedInstanceState.getString("fragment");
        if (!TextUtils.isEmpty(fragmentSimpleName)) {
            try {
                Class mClass = Class.forName(fragmentSimpleName);
                //但是不能创建新fragment对象，因为之前的fragment并没有被回收掉，所以需要取出fragmentManager中的fragment，否则会造成错乱
                mLastFragment = getFragmentInstance(mClass);
                if (mLastFragment.isHidden()) {
                    switchFragment(mLastFragment, savedInstanceState.getInt("layoutId"));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    protected void setTextViewSize(TextView tv, int dimenId) {
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(dimenId));
    }
}
