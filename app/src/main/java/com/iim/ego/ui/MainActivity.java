package com.iim.ego.ui;

import android.support.v4.app.Fragment;

import com.iim.ego.R;
import com.iim.ego.base.BaseActivity;
import com.iim.ego.ui.fragment.StuffFragment;
import com.iim.ego.widget.tabview.TabView;
import com.iim.ego.widget.tabview.TabViewChild;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity {


    @BindView(R.id.mTabView)
    TabView mTabView;

    Fragment stuffFragment = getFragmentInstance(StuffFragment.class);

    @Override
    protected int setStatusBarColor() {
        return 0;
    }

    @Override
    protected int layoutInit() {
        return R.layout.activity_main;
    }

    @Override
    protected void bindEvent() {
        List<TabViewChild> tabViewChildList=new ArrayList<>();
        TabViewChild tab1 = new TabViewChild(R.drawable.logo,R.drawable.logo,"员工",stuffFragment);
        TabViewChild tab2 = new TabViewChild(R.drawable.logo,R.drawable.logo,"访客",stuffFragment);
        TabViewChild tab3 = new TabViewChild(R.drawable.logo,R.drawable.logo,"监控",stuffFragment);
        tabViewChildList.add(tab1);
        tabViewChildList.add(tab2);
        tabViewChildList.add(tab3);
        mTabView.setTabViewChild(tabViewChildList,getSupportFragmentManager());
    }

}
