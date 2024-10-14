package com.pasc.lib.newscenter.customview;


import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pasc.lib.base.util.DateUtils;
import com.pasc.lib.imageloader.PascImageLoader;
import com.pasc.lib.newscenter.R;
import com.pasc.lib.newscenter.bean.NewsInfoBean;
import com.pasc.lib.newscenter.data.NewsCenterConstant;
import com.pasc.lib.newscenter.data.NewsDataManager;
import com.pasc.lib.newscenter.net.NewsCenterNetManager;
import com.pasc.lib.newscenter.util.NewsCenterUtils;
import java.util.List;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * 展示新闻资讯列表根据栏目Type
 * Created by qinguohuai143 on 2019/01/04.
 */
public class CustomNewsListByColumnType extends BaseCustomAdapter<NewsInfoBean> {

    private String newsColumnType;

    public CustomNewsListByColumnType(String type, String newsColumnType) {
        super(type);
        this.newsColumnType = newsColumnType;
    }

    @Override
    public BaseQuickAdapter<NewsInfoBean, BaseViewHolder> getAdapter() {
//        return new BaseQuickAdapter<NewsInfoBean, BaseViewHolder>(R.layout.newscenter_list_item_only_txt) {
        return new BaseQuickAdapter<NewsInfoBean, BaseViewHolder>(R.layout.newscenter_common_list_item) {
            @Override
            protected void convert(BaseViewHolder helper, NewsInfoBean item) {

                // 测试数据
//                item.titlePicture ="https://inews.gtimg.com/newsapp_bt/0/2984930578/1000," +
//                        "https://inews.gtimg.com/newsapp_bt/0/6586297426/1000," +
//                        "https://inews.gtimg.com/newsapp_bt/0/6586297472/1000";


                helper.setGone(R.id.newscenter_list_item_more_img, false);
                helper.setGone(R.id.newscenter_list_item_img1, false);

                if (item != null) {
                    String[] imgArray = NewsCenterUtils.splitNewsTitleImgUrl(item.titlePicture, ",");
                    if (imgArray != null && imgArray.length > 0) {
                        if (imgArray.length == 1 || imgArray.length == 2) {
                            helper.setGone(R.id.newscenter_list_item_more_img, false);
                            helper.setVisible(R.id.newscenter_list_item_img1, true);

                            PascImageLoader.getInstance().loadImageUrl(imgArray[0],
                                    helper.getView(R.id.newscenter_list_item_img1), PascImageLoader.SCALE_CENTER_CROP);

                        } else if (imgArray.length >= 3) {
                            helper.setVisible(R.id.newscenter_list_item_more_img, true);
                            helper.setGone(R.id.newscenter_list_item_img1, false);

                            PascImageLoader.getInstance().loadImageUrl(imgArray[0],
                                    helper.getView(R.id.newscenter_list_item_img3_1), PascImageLoader.SCALE_CENTER_CROP);
                            PascImageLoader.getInstance().loadImageUrl(imgArray[1],
                                    helper.getView(R.id.newscenter_list_item_img3_2), PascImageLoader.SCALE_CENTER_CROP);
                            PascImageLoader.getInstance().loadImageUrl(imgArray[2],
                                    helper.getView(R.id.newscenter_list_item_img3_3), PascImageLoader.SCALE_CENTER_CROP);

                        }
                    }

                    helper.setText(R.id.newscenter_list_item_title, item.title);
                    helper.setText(R.id.newscenter_list_item_time, DateUtils.dateFormat(item.issueDate));
                    helper.setText(R.id.newscenter_list_item_source, TextUtils.isEmpty(item.source)? "": item.source);
                }
            }
        };
    }

    @Override
    public Single<List<NewsInfoBean>> getData() {
        page = 1; //
        return NewsCenterNetManager.getNewsListByColumn(newsColumnType, page + "", PAGE_SIZE + "");
    }

    @Override
    public Flowable<List<NewsInfoBean>> getCacheData() {

        return NewsDataManager.getNewsListFromCaches(NewsCenterConstant.NewsType.COLUMNNEWS, newsColumnType, page + "", PAGE_SIZE + "", "");
    }


    @Override
    public String onItemClick(NewsInfoBean item) {
        return item.infoId;
    }

    @Override
    public boolean isEnableLoadMore() {
//        return false;
        return true;
    }

    @Override
    protected Single<List<NewsInfoBean>> onLoadMoreData() {

        return NewsCenterNetManager.getNewsListByColumn(newsColumnType, ++page + "", PAGE_SIZE + "");
    }
}
