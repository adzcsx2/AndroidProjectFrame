package com.iim.ego.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iim.ego.ui.MainActivity;
import com.iim.ego.util.ToastUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import hoyn.autolayout.AutoFrameLayout;
import hoyn.autolayout.AutoLinearLayout;
import hoyn.autolayout.AutoRelativeLayout;


/**
 * @author Hoyn
 * @version 1.0
 * @Title：SAFEYE@
 * @Description：
 */
public abstract class BaseActivity extends RxAppCompatActivity {

    private static final String LAYOUT_LINEARLAYOUT = "LinearLayout";
    private static final String LAYOUT_FRAMELAYOUT = "FrameLayout";
    private static final String LAYOUT_RELATIVELAYOUT = "RelativeLayout";

    /**
     * activity控制
     */
    public static Map<String, WeakReference<Activity>> openedActivitys = new LinkedHashMap<String, WeakReference<Activity>>();// 已经打开的activity

    protected Bundle savedInstanceState;

    //fragment控制(恢复状态)
    protected Fragment mLastFragment;
    private int layoutId;
    protected BaseActivity context;
    //状态栏沉浸模式使用
    /*statusbar view*/
    private ViewGroup view;
    /*沉浸颜色*/
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏软键盘
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // 隐藏actionBar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏通知栏
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //强制竖屏
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        //去掉titlebar-全屏模式
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        if (!openedActivitys.keySet().contains(getClass().getSimpleName())) {
            openedActivitys.put(getClass().getSimpleName(),
                    new WeakReference<Activity>(this));
        }

        this.savedInstanceState = savedInstanceState;
        setContentView(layoutInit());
        initStatusbar(setStatusBarColor());
        context = this;
        init(savedInstanceState);
        ButterKnife.bind(this);
        bindPresenter();
        viewInit();
        bindEvent();

    }


    /**
     * 沉浸式状态栏
     * 如果statusBarColorRes为0，则为全屏沉浸式，android5.0以上有效。
     * 如果statusBarColorRes为资源颜色，则添加一个状态栏颜色，android4.4以上有效
     *
     * @param statusBarColorRes 资源文件的颜色(R.color.xx)
     */
    @SuppressLint("NewApi")
    public void initStatusbar(int statusBarColorRes) {
        if (statusBarColorRes == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int statusColor = Color.parseColor("#008000");
                Window window = getWindow();
                //设置透明状态栏,这样才能让 ContentView 向上
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //设置状态栏颜色
                window.setStatusBarColor(statusColor);
                ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
                View mChildView = mContentView.getChildAt(0);
                if (mChildView != null) {
                    //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View .
                    // 使其不为系统 View 预留空间.不预留空间的话 状态栏就会覆盖布局顶部
                    ViewCompat.setFitsSystemWindows(mChildView, false);
                }
            }
        } else {
            //4.4版本及以上可用
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // 状态栏沉浸效果
                Window window = getWindow();
                window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setFlags(
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                //decorview实际上就是activity的外层容器，是一层framlayout
                view = (ViewGroup) getWindow().getDecorView();
                LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight());
                //textview是实际添加的状态栏view，颜色可以设置成纯色，也可以加上shape，添加gradient属性设置渐变色
                textView = new TextView(this);
                textView.setBackgroundResource(statusBarColorRes);
                textView.setLayoutParams(lParams);
                view.addView(textView);
            }
        }
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 如果项目中用到了slidingmenu,根据slidingmenu滑动百分比设置statusbar颜色：渐变色效果
     *
     * @param alpha
     */
    @SuppressLint("NewApi")
    public void changeStatusBarColor(float alpha) {
        //textview是slidingmenu关闭时显示的颜色
        //textview2是slidingmenu打开时显示的颜色
        textView.setAlpha(1 - alpha);

    }

    protected abstract int setStatusBarColor();

    /**
     * 屏幕适配配置属性
     *
     * @param name
     * @param context
     * @param attrs
     * @return
     */
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;
        if (name.equals(LAYOUT_FRAMELAYOUT)) {
            view = new AutoFrameLayout(context, attrs);
        }

        if (name.equals(LAYOUT_LINEARLAYOUT)) {
            view = new AutoLinearLayout(context, attrs);
        }

        if (name.equals(LAYOUT_RELATIVELAYOUT)) {
            view = new AutoRelativeLayout(context, attrs);
        }

        if (view != null) return view;

        return super.onCreateView(name, context, attrs);
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
                ToastUtil.show("再按一次退出");
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
