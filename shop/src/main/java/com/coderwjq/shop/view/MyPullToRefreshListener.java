package com.coderwjq.shop.view;

import android.content.Context;

import com.coderwjq.shop.utils.UIUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/2/4.
 */

public class MyPullToRefreshListener implements SuperSwipeRefreshLayout.OnPullRefreshListener {

    private final int scrollPx;//需要滑动的距离
    private OnRefreshListener mOnRefreshListener;
    private boolean isRefresh;//是否可以刷新
    private int mScrollDistance;//滑动距离
    private RefreshView refreshView;//刷新的头部
    private int height;//头部高度
    private SuperSwipeRefreshLayout mSwipeRefreshLayout;


    public MyPullToRefreshListener(Context context, SuperSwipeRefreshLayout superSwipeRefreshLayout) {
        this.mSwipeRefreshLayout = superSwipeRefreshLayout;
        setupRefreshView(context);
        scrollPx = UIUtils.dp2px(context, 25);
    }

    private void setupRefreshView(Context context) {
        refreshView = new RefreshView(context);
        mSwipeRefreshLayout.setHeaderView(refreshView);
        height = mSwipeRefreshLayout.getHeaderHeight();
    }

    /**
     * 加载成功之后回调
     */
    public void refreshDone() {
        //延迟700毫秒停止动画
        Observable.timer(700, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        refreshView.stopAnimate();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }


    /**
     * 刷新操作
     */
    @Override
    public void onRefresh() {
        if (mOnRefreshListener != null) {
            mOnRefreshListener.refresh();
            refreshView.startAnimate();
        }
    }

    //根据滑动的距离来对View的进行绘制
    @Override
    public void onPullDistance(int distance) {
        if (mScrollDistance > distance) {
            //当不在刷新状态并且滑动距离超过scrollPx之后才开始变色
            if (!isRefresh && distance > scrollPx) {
//                Log.d("滑动", "onPullDistance: 向上变颜色");

                float scale = ((float) distance - scrollPx) / (((2 * height * scrollPx) / distance));
                refreshView.setProgress(scale <= 0f ? 0f : scale);
            }
        } else {
            if (!isRefresh && distance > scrollPx) {
                float scale = ((float) distance - scrollPx) / (((2 * height * scrollPx) / distance));
                refreshView.setProgress(scale > 1f ? 1f : scale);
//                Log.d("滑动", "onPullDistance: 向下变颜色" + distance + "---" + height + "---scale:" + scale);
            }
        }
        mScrollDistance = distance;
    }

    @Override
    public void onPullEnable(boolean enable) {
        isRefresh = enable;
    }

    public void setOnRefreshListener(OnRefreshListener OnRefreshListener) {
        this.mOnRefreshListener = OnRefreshListener;
    }

    /**
     * 刷新回调，在该方法中执行刷新数据操作
     */
    public interface OnRefreshListener {
        void refresh();
    }

}
