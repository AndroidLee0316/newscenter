package com.pasc.lib.newscenter.data;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.pasc.lib.base.util.SPUtils;
import com.pasc.lib.newscenter.bean.NewsColumnBean;
import com.pasc.lib.newscenter.bean.NewsInfoBean;
import com.pasc.lib.newscenter.bean.NewsListInfoBean;
import com.pasc.lib.storage.fileDiskCache.FileCacheUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

/**
 * 新闻资讯数据Manager
 * Created by qinguohuai143 on 2019/01/11.
 */
public class NewsDataManager {


    /**
     * 缓存头条新闻列表
     * @param preParam
     * @param newsListData
     */
    public static void saveTopNewsData(String preParam, List<NewsInfoBean> newsListData){

        saveNewsListData(NewsCenterConstant.NewsType.TOPNEWS,"", "", "",preParam, newsListData);
    }

    /**
     * 缓存新闻列表数据
     *
     * @param newsType
     * @param newsColumnType
     * @param pageNum
     * @param pageSize
     * @param preParam
     * @param newsListData
     */
    public static void saveNewsListData(String newsType, String newsColumnType, String pageNum, String pageSize, String preParam, List<NewsInfoBean> newsListData) {

        String cacheKey = getNewsListCacheKey(newsType, newsColumnType, pageNum, pageSize, preParam);
        saveNewsData(cacheKey, new Gson().toJson(newsListData));
    }

    /**
     * 保存新闻栏目数据
     *
     * @param preParam
     * @param newsColumnListData
     */
    public static void saveNewsColumnTypeList(String preParam, List<NewsColumnBean> newsColumnListData) {

        String cacheKey = getNewsColumnTypeCacheKey(preParam);
        //saveNewsData(cacheKey, new Gson().toJson(newsColumnListData));

        saveNewsColumnTypeBySp(cacheKey, new Gson().toJson(newsColumnListData));
    }

    /**
     * 调用FileCacheUtils保存数据
     *
     * @param cacheKey
     * @param jsonData
     */
    private static void saveNewsData(String cacheKey, String jsonData) {
        FileCacheUtils.put(cacheKey, jsonData);
    }

    /**
     * 使用SharePreference保存新闻栏目列表
     * @param cacheKey
     * @param jsonData
     */
    private static void saveNewsColumnTypeBySp(String cacheKey, String jsonData){

        SPUtils.getInstance().setParam(cacheKey, jsonData);
    }

    /**
     * 删除cacheKey对应的缓存数据
     *
     * @param cacheKey
     */
    protected static void clearCache(String cacheKey) {
        FileCacheUtils.remove(cacheKey);
    }

    /**
     * 从缓存读取新闻栏目数据
     * FileCacheUtils
     * @param preParam
     * @return
     */
    public static Flowable<List<NewsColumnBean>> getNewsColumnTypeListFromCaches(String preParam) {
        String cacheKey = getNewsColumnTypeCacheKey(preParam);
        return FileCacheUtils.getListFlowable(cacheKey, NewsColumnBean.class);
    }

    /**
     * 从缓存读取新闻栏目数据
     * SharePrefrenceUtils
     * @param preParam
     * @return
     */
    public static List<NewsColumnBean> getNewsColumnTypeListFromSp(String preParam) {
        List<NewsColumnBean> beanList = new ArrayList<>();
        try {
            String cacheKey = getNewsColumnTypeCacheKey(preParam);
            String listStr = (String)SPUtils.getInstance().getParam(cacheKey, "");
            if (!TextUtils.isEmpty(listStr)){
                JsonParser jsonParser = new JsonParser();
                JsonArray jsonArray = jsonParser.parse(listStr).getAsJsonArray();
                Gson gson = new Gson();
                for (JsonElement element : jsonArray){
                    NewsColumnBean columnBean = gson.fromJson(element, NewsColumnBean.class);
                    beanList.add(columnBean);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return beanList;
    }

    /**
     * 从本地缓存读取新闻列表
     * @param newsType
     * @param newsColumnType
     * @param pageNum
     * @param pageSize
     * @param preParam
     * @return
     */
    public static Flowable<List<NewsInfoBean>> getNewsListFromCaches(String newsType, String newsColumnType, String pageNum, String pageSize, String preParam) {

        String cacheKey = getNewsListCacheKey(newsType, newsColumnType, pageNum, pageSize, preParam);

        return FileCacheUtils.getListFlowable(cacheKey, NewsInfoBean.class);
    }


    /**
     * 从本地读取头条新闻
     * Sync
     * @param preParam
     * @return
     */
    public static NewsListInfoBean getTopNewListFromCacheSync(String preParam){

        String cacheKey = getNewsListCacheKey(NewsCenterConstant.NewsType.TOPNEWS,"", "", "", preParam);

        List<NewsInfoBean> newsList = FileCacheUtils.getListSync(cacheKey, NewsInfoBean.class);

        if (newsList == null){
            return null;
        }

        NewsListInfoBean newsListInfoBean = new NewsListInfoBean(newsList);

        return newsListInfoBean;

    }


    /**
     * @param newsType
     * @param newsColumnType
     * @param pageNum
     * @param pageSize
     * @param arg0
     * @return
     */
    public static String getNewsListCacheKey(String newsType, String newsColumnType, String pageNum, String pageSize, String arg0) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("newscenter_")
                .append("newstype_")
                .append(newsType);
        if (!TextUtils.isEmpty(newsColumnType)) {
            stringBuilder.append("_newsColumnType_").append(newsColumnType);
        }
        if (!TextUtils.isEmpty(arg0)) {
            stringBuilder.append("_preParams_").append(arg0);
        }

        stringBuilder
                .append("_pageNum_")
                .append(pageNum)
                .append("_pageSize_")
                .append(pageSize);

        return stringBuilder.toString();

    }

    /**
     * 新闻栏目
     *
     * @param preParams
     * @return
     */
    public static String getNewsColumnTypeCacheKey(String preParams) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("newscenter_")
                .append("newscolumntype_")
                .append("_preParams_")
                .append(preParams);

        return stringBuilder.toString();
    }

}
