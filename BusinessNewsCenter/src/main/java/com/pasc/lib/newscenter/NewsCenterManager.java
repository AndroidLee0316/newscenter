package com.pasc.lib.newscenter;

import android.content.Context;
import android.text.TextUtils;

import com.pasc.lib.base.util.DeviceUtils;
import com.pasc.lib.base.util.NetworkUtils;
import com.pasc.lib.newscenter.bean.NewsInfoBean;
import com.pasc.lib.newscenter.bean.NewsListInfoBean;
import com.pasc.lib.newscenter.data.NewsDataManager;
import com.pasc.lib.newscenter.net.NewsCenterNetManager;

/**
 * 对外提供数据接口
 * Created by qinguohuai143 on 2019/01/04.
 */
public class NewsCenterManager {
    /**自定义数据转换器**/
    private  NewsDataConverterInterface dataConverter;

    private Context mContext;

    public static NewsCenterManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final NewsCenterManager instance = new NewsCenterManager();
    }

    /**
     *
     * @param dataConverter
     * 数据结构不同时设置自定义数据转换，默认使用市民通的java bean做数据转换
     */
    public void setDataConverter(NewsDataConverterInterface dataConverter){
        this.dataConverter = dataConverter;
    }
    public NewsDataConverterInterface getDataConverter(){
        return dataConverter;
    }

    /**
     * 为首页滚动新闻提供数据
     * 在子线程调用改方法
     * @param arg0
     * @return
     */
    public NewsListInfoBean getTopNewsListFromNet(String arg0) {

        if (TextUtils.isEmpty(arg0)) {
            arg0 = "";
        }else {
            if (!"1".equals(arg0) && !"2".equals(arg0)) {
                arg0 = "";
            }
        }
        // 无网络的时候，从本地获取数据
        if (NetworkUtils.isNetworkAvailable()){
            return NewsCenterNetManager.getTopNewListInfoFromNet(arg0);
        }else {

            return NewsDataManager.getTopNewListFromCacheSync("");
        }

    }

}
