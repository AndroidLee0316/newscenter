package com.pasc.lib.newscenter.customview;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pasc.lib.base.util.DateUtils;
import com.pasc.lib.newscenter.R;
import com.pasc.lib.newscenter.bean.NewsInfoBean;
import com.pasc.lib.newscenter.net.NewsCenterNetManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * 新闻资讯列表Type
 * Created by qinguohuai143 on 2019/01/04.
 */
public class CustomTopNewsListType extends BaseCustomAdapter<NewsInfoBean> {

    public CustomTopNewsListType(String type) {
        super(type);
    }

    @Override
    public BaseQuickAdapter<NewsInfoBean,BaseViewHolder> getAdapter() {
        return new BaseQuickAdapter<NewsInfoBean, BaseViewHolder>(R.layout.newscenter_list_item_only_txt) {
            @Override
            protected void convert(BaseViewHolder helper, NewsInfoBean item) {
                helper.setText(R.id.newscenter_list_item_title, item.title);
                helper.setText(R.id.newscenter_list_item_time, DateUtils.dateFormat(item.updatedDate));
            }
        };
    }

    @Override
    public Single<List<NewsInfoBean>> getData() {

        return NewsCenterNetManager.getTopNewsList("");
    }

    @Override
    public Flowable<List<NewsInfoBean>> getCacheData() {
        return null;
    }

    @Override
    public String onItemClick(NewsInfoBean item) {
        // TODO 补全url
       return item.link;
    }

    @Override
    public boolean isEnableLoadMore() {
        return false;
    }

    @Override
    protected Single<List<NewsInfoBean>> onLoadMoreData() {
        return Single.just(new ArrayList<>());
    }
}
