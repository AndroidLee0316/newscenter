package com.pasc.lib.newscenter.customview;


import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.nativeability.WebStrategy;
import com.pasc.lib.net.resp.BaseRespObserver;
import com.pasc.lib.newscenter.bean.NewsInfoBean;
import com.pasc.lib.newscenter.net.NewsCenterNetManager;
import com.pasc.lib.statistics.StatisticsManager;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * @author Created by buyongyou on 2018/12/6.
 * @des
 */
public abstract class BaseCustomAdapter<T> {
    public static final int PAGE_SIZE = 15; // 产品定的pageSize
    protected int page = 1;
    protected String type;
    public BaseCustomAdapter(String type) {
        this.type = type;
    }

    /**
     * @return 返回该页面锁代表的类型TYPE
     */
    public  String getType(){return type;}


    /**
     * @param adapter
     * @des 请覆盖onLoadMoreData提供数据
     */
    public  void onLoadMore(final BaseQuickAdapter<T,BaseViewHolder> adapter){
        onLoadMoreData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseRespObserver<List<T>>() {
                    @Override
                    public void onSuccess(List<T> list) {
                        if (list == null || list.size() < PAGE_SIZE) {
                            adapter.loadMoreEnd(false);
                        } else {
                            adapter.loadMoreComplete();
                        }
                        if (list != null) {
                            adapter.addData(list);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        adapter.loadMoreFail();
                    }
                });
    }


    public abstract BaseQuickAdapter<T,BaseViewHolder> getAdapter();

    protected  Single<List<T>> onLoadMoreData(){return null;}

    public boolean isEnableLoadMore(){
        return false;
    }

    public boolean isEnableRefresh(){return true;}

    public abstract Single<List<T>>  getData();

    public abstract Flowable<List<T>> getCacheData();

    public  void onItemClick(BaseQuickAdapter<T, BaseViewHolder> adapter, T item, View view, int position, Activity activity){

        if (item != null && item instanceof NewsInfoBean){

            openNewsDetail(activity, ((NewsInfoBean) item).articleLink, ((NewsInfoBean) item).title);
            // 统计
            StatisticsManager.getInstance().onEvent("app_news_item", ((NewsInfoBean) item).title);
        }
    }

    protected abstract String onItemClick(T item);


    private void openNewsDetail(Activity activity, String newsDetailUrl, String title){
        if (!TextUtils.isEmpty(newsDetailUrl)){
            WebStrategy strategy = new WebStrategy ().setUrl (newsDetailUrl).setTitle(title);
            PascHybrid.getInstance ().start (activity, strategy);
        }
    }

}
