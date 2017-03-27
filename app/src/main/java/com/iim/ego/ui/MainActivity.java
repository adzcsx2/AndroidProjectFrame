package com.iim.ego.ui;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iim.ego.R;
import com.iim.ego.base.BaseActivity;
import com.iim.ego.exception.RxException;
import com.iim.ego.model.NameBean;
import com.iim.ego.net.HttpUtil;
import com.iim.ego.util.L;
import com.iim.ego.util.RxHelper;
import com.iim.ego.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.lemon.view.RefreshRecyclerView;
import cn.lemon.view.adapter.Action;
import cn.lemon.view.adapter.BaseViewHolder;
import cn.lemon.view.adapter.RecyclerAdapter;

public class MainActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RefreshRecyclerView mRecyclerView;
    @BindView(R.id.tv)
    TextView tv;
    Handler handler;

    @OnClick(R.id.tv)
    public void tv(){
        HttpUtil.getApiService()
                .getWeather("深圳")
                .compose(RxHelper.io_main(MainActivity.this))
//                        .compose(handleResult())
                .subscribe(result ->{
                    L.e(result.getData().getGanmao());
                    ToastUtil.show(result.getData().getGanmao(),false);
                },new RxException<>(e->e.printStackTrace()));
    }
    @Override
    protected int layoutInit() {
        return R.layout.activity_main;
    }

    @Override
    protected void bindEvent() {
        tv.setText("nihao");
        mRecyclerView.setSwipeRefreshColors(0xFF437845, 0xFFE44F98, 0xFF2FAC21);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final List<NameBean> data = new ArrayList<NameBean>();
        data.add(new NameBean("aaa"));
        data.add(new NameBean("aaa"));
        data.add(new NameBean("aaa"));
        data.add(new NameBean("aaa"));
        data.add(new NameBean("aaa"));
        data.add(new NameBean("aaa"));

        for (NameBean x:data) {
            Log.e("TAG",x.getName());
        }


        final MyAdapter myAdapter = new MyAdapter(this,data);
        handler = new Handler(getMainLooper());
        mRecyclerView.setAdapter(myAdapter);
        mRecyclerView.setRefreshAction(new Action() {
            @Override
            public void onAction() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myAdapter.add(new NameBean("hhh"));
                        mRecyclerView.dismissSwipeRefresh();
                    }
                },1500);
            }
        });
        mRecyclerView.setRefreshAction(()->
                handler.postDelayed(()->{
                    myAdapter.add(new NameBean("hhh"));
                    mRecyclerView.dismissSwipeRefresh();
                },1500));


        mRecyclerView.setLoadMoreAction(new Action() {
            @Override
            public void onAction() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myAdapter.add(new NameBean("bbb"));
                    }
                },1500);
            }
        });
    }



    class MyAdapter extends RecyclerAdapter<NameBean> {

        public MyAdapter(Context context) {
            super(context);
        }

        public MyAdapter(Context context, List<NameBean> data) {
            super(context, data);
        }

        @Override
        public BaseViewHolder<NameBean> onCreateBaseViewHolder(ViewGroup parent, int viewType) {
            return new MyHolder(getLayoutInflater().inflate(R.layout.item,parent,false));
        }

          class MyHolder extends BaseViewHolder<NameBean> {
            @BindView(R.id.textView)
            TextView mTextView;

              public MyHolder(View itemView) {
                  super(itemView);
                  ButterKnife.bind(this, itemView);
              }

              @Override
              public void onInitializeView() {
                  super.onInitializeView();
//                   mTextView = findViewById(R.id.textView);
              }

              @Override
            public void setData(NameBean data) {
                super.setData(data);
                mTextView.setText(data.getName());
            }

            @Override
            public void onItemViewClick(NameBean data) {
                super.onItemViewClick(data);
                Log.e("aaa", data.getName());
            }
        }
    }
}
