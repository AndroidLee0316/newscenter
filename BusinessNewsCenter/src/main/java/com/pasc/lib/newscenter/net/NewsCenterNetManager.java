package com.pasc.lib.newscenter.net;

import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.reflect.TypeToken;
import com.pasc.lib.log.PascLog;
import com.pasc.lib.net.ApiGenerator;
import com.pasc.lib.net.resp.BaseResp;
import com.pasc.lib.net.transform.RespTransformer;
import com.pasc.lib.newscenter.NewsCenterManager;
import com.pasc.lib.newscenter.bean.NewsCenterBodyReqParam;
import com.pasc.lib.newscenter.bean.NewsCenterEmptyBodyReqParam;
import com.pasc.lib.newscenter.bean.NewsColumnBean;
import com.pasc.lib.newscenter.bean.NewsInfoBean;
import com.pasc.lib.newscenter.bean.NewsListInfoBean;
import com.pasc.lib.newscenter.bean.TopNewsBodyReqParam;
import com.pasc.lib.newscenter.data.NewsCenterConstant;
import com.pasc.lib.newscenter.data.NewsDataManager;


import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * 网络NetManager
 * Created by qinguohuai143 on 2019/01/04.
 */
public class NewsCenterNetManager {

    // 获取新闻栏目
    public static final String GET_NEWS_COLUMN_LIST = "api/platform/infomanagement/getInfoColumn";
    // 按类型获取新闻列表
    public static final String GET_NEWS_LIST_BY_TYPE = "api/platform/infomanagement/getInfoManagementList";
    // 获取头条新闻列表
    public static final String GET_NEWS_TOP_LIST = "api/platform/infomanagement/getTopInfo";

    //public static final String GO_NEWS_DETAIL_H5_URL = "https://smt-stg.yun.city.pingan.com/basic/stg/app/feature/news/?uiparams=%7B%22title%22:%22%22%7D#/article/";

    /**
     * 获取头条新闻列表
     *
     * @return
     */
    public static Single<List<NewsInfoBean>> getTopNewsList(String arg0) {

//        RespTransformer<List<NewsInfoBean>> respTransformer = RespTransformer.newInstance();

        return ApiGenerator.createApi(NewsCenterApi.class)
                .getTopNewsList(GET_NEWS_TOP_LIST, new NewsCenterEmptyBodyReqParam())
//                .compose(respTransformer)
                .map(new Function<ResponseBody,  List<NewsInfoBean>>() {
                    @Override
                    public List<NewsInfoBean> apply(ResponseBody responseBody) throws Exception {
                        List<NewsInfoBean> datas = null;
                        BaseResp<List<NewsInfoBean>> baseResp = null;
                        String respData = responseBody.string();
                        if (!TextUtils.isEmpty(respData) ) {
                            if (NewsCenterManager.getInstance().getDataConverter() != null) {
                                datas = NewsCenterManager.getInstance().getDataConverter().newsListConverter(respData);
                            }
                            if (datas == null){
                                Type type = new TypeToken<BaseResp<List<NewsInfoBean>>>() {
                                }.getType();
                                baseResp= (new Gson().fromJson(respData, type));
                            }

                        }
                        if (baseResp != null && baseResp.data !=null){
                            datas = baseResp.data;
                        }else {
                            datas = new ArrayList<>();
                        }
                        return datas;
                    }
                })
                .subscribeOn(Schedulers.io());

    }

    /**
     * 根据栏目类型获取新闻列表接口
     *
     * @return
     */
    public static Single<List<NewsInfoBean>> getNewsListByColumn(String columnType, String pageNum, String pageSize) {

        RespTransformer<List<NewsInfoBean>> respTransformer = RespTransformer.newInstance();
        NewsCenterBodyReqParam requestPatam = new NewsCenterBodyReqParam(columnType, pageNum, pageSize);

        return ApiGenerator.createApi(NewsCenterApi.class)
                .getNewsListByColumnType(GET_NEWS_LIST_BY_TYPE, requestPatam)
                .map(new Function<ResponseBody, List<NewsInfoBean>>() {
                    @Override
                    public List<NewsInfoBean> apply(ResponseBody responseBody) throws Exception {
                        List<NewsInfoBean> datas = null;
                        BaseResp<List<NewsInfoBean>> resp = null;
                        String res = responseBody.string();
                        if (!TextUtils.isEmpty(res)) {
                            if (NewsCenterManager.getInstance().getDataConverter() != null) {
                                datas = NewsCenterManager.getInstance().getDataConverter().newsListConverter(res);
                            }
                            if (datas == null) {
                                Log.e("test==", res);
                                Type type = new TypeToken<BaseResp<List<NewsInfoBean>>>() {
                                }.getType();
                                GsonBuilder gb = new GsonBuilder();
                                gb.setLongSerializationPolicy(LongSerializationPolicy.STRING);
                                resp = (new Gson().fromJson(res, type));
                            }

                        }
                        if (resp != null && resp.data != null) {
                            datas = resp.data;
                        } else {
                            datas = new ArrayList<>();
                        }
                        return datas;
                    }
                }).subscribeOn(Schedulers.io());

    }

    /**
     * 获取新闻栏
     *
     * @param arg0
     * @return
     */
    public static Single<List<NewsColumnBean>> getNewsColumnList(String arg0) {

        RespTransformer<List<NewsColumnBean>> respTransformer = RespTransformer.newInstance();

        return ApiGenerator.createApi(NewsCenterApi.class)
                .getNewsColumnList(GET_NEWS_COLUMN_LIST, new NewsCenterEmptyBodyReqParam())
//                .compose(respTransformer)
                .map(new Function<ResponseBody, List<NewsColumnBean>>() {
                    @Override
                    public List<NewsColumnBean> apply(ResponseBody responseBody) throws Exception {
                        List<NewsColumnBean> datas = null;
                        BaseResp<List<NewsColumnBean>> resp = null;
                        String res = responseBody.string();
                        if (!TextUtils.isEmpty(res)) {
                            if (NewsCenterManager.getInstance().getDataConverter() != null) {
                                datas = NewsCenterManager.getInstance().getDataConverter().newsColumnListConverter(res);
                            }
                            if (datas == null) {
                                Log.e("test==", res);
                                Type type = new TypeToken<BaseResp<List<NewsColumnBean>>>() {
                                }.getType();
                                GsonBuilder gb = new GsonBuilder();
                                gb.setLongSerializationPolicy(LongSerializationPolicy.STRING);
                                resp = (new Gson().fromJson(res, type));
                            }

                        }
                        if (resp != null && resp.data != null) {
                            datas = resp.data;
                        } else {
                            datas = new ArrayList<>();
                        }
                        return datas;
                    }
                })
                .subscribeOn(Schedulers.io());


    }


    /**
     * 为首页滚动新闻提供数据
     *
     * @param eachTabCount
     * @return
     */
    public static NewsListInfoBean getTopNewListInfoFromNet(String eachTabCount) {
        try {
            Call<BaseResp<List<NewsInfoBean>>> respCall = null;
            respCall = ApiGenerator.createApi(NewsCenterApi.class)
                    .getTopNewsListOfCall(GET_NEWS_TOP_LIST, new TopNewsBodyReqParam(eachTabCount));

            BaseResp<List<NewsInfoBean>> baseResp = respCall.execute().body();

            if (baseResp == null) {
                return null;
            }

            List<NewsInfoBean> newsList = baseResp.data;

            if (newsList == null) {
                return null;
            }

            // 缓存到本地
            NewsDataManager.saveTopNewsData("", newsList);

            NewsListInfoBean newsListInfoBean = new NewsListInfoBean(newsList);

            return newsListInfoBean;

        } catch (Exception e) {
            PascLog.d("NewsCenter", "getTopNewListInfoFromNet error " + e.toString());
            return null;
        }

    }


}
