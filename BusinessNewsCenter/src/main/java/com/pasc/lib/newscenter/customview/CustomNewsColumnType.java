package com.pasc.lib.newscenter.customview;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pasc.lib.base.util.DateUtils;
import com.pasc.lib.newscenter.R;
import com.pasc.lib.newscenter.bean.NewsColumnBean;
import com.pasc.lib.newscenter.net.NewsCenterNetManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * 新闻资讯栏目Type
 * Created by qinguohuai143 on 2019/01/04.
 */
public class CustomNewsColumnType extends BaseCustomAdapter<NewsColumnBean> {

    public CustomNewsColumnType(String type) {
        super(type);
    }

    @Override
    public BaseQuickAdapter<NewsColumnBean,BaseViewHolder> getAdapter() {
        return new BaseQuickAdapter<NewsColumnBean, BaseViewHolder>(R.layout.newscenter_list_item_only_txt) {
            @Override
            protected void convert(BaseViewHolder helper, NewsColumnBean item) {
                helper.setText(R.id.newscenter_list_item_title, item.columnName);
                helper.setText(R.id.newscenter_list_item_time, "类型："+item.sort);
            }
        };
    }

    @Override
    public Single<List<NewsColumnBean>> getData() {
        return NewsCenterNetManager.getNewsColumnList("");
    }

    @Override
    public Flowable<List<NewsColumnBean>> getCacheData() {
        return null;
    }

    @Override
    public String onItemClick(NewsColumnBean item) {
        // TODO 补全url
       return item.columnName;
    }

    @Override
    public boolean isEnableLoadMore() {
        return false;
    }

    @Override
    protected Single<List<NewsColumnBean>> onLoadMoreData() {
        return Single.just(new ArrayList<>());
    }
}
