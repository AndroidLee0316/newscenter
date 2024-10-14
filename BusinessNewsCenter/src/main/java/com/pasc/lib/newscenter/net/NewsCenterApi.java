package com.pasc.lib.newscenter.net;


import com.pasc.lib.net.resp.BaseResp;
import com.pasc.lib.newscenter.bean.NewsCenterBodyReqParam;
import com.pasc.lib.newscenter.bean.NewsCenterEmptyBodyReqParam;
import com.pasc.lib.newscenter.bean.NewsColumnBean;
import com.pasc.lib.newscenter.bean.NewsInfoBean;
import com.pasc.lib.newscenter.bean.TopNewsBodyReqParam;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * 网络接口定义
 * Created by qinguohuai143 on 2019/01/04.
 */
public interface NewsCenterApi {

    //    @POST
//    Single<BaseResp<List<NewsInfoBean>>> getTopNewsList(
//         @Url String url,
//         @Header("token") String token,
//         @Body NewsCenterEmptyBodyReqParam param
//         );
    @POST
    Single<ResponseBody> getTopNewsList(
            @Url String url,
            @Body NewsCenterEmptyBodyReqParam param
    );

//    @POST
//    Single<BaseResp<List<NewsInfoBean>>> getTopNewsList(
//            @Url String url,
//            @Body NewsCenterEmptyBodyReqParam param
//    );

    @POST
    Call<BaseResp<List<NewsInfoBean>>> getTopNewsListOfCall(@Url String url, @Body TopNewsBodyReqParam param);

    @POST
    Single<ResponseBody> getNewsListByColumnType(
            @Url String url,

            @Body NewsCenterBodyReqParam param
    );


    //    @POST
//    Single<BaseResp<List<NewsColumnBean>>> getNewsColumnList(
//            @Url String url,
//            @Body NewsCenterEmptyBodyReqParam param
//    );
    @POST
    Single<ResponseBody> getNewsColumnList(
            @Url String url,
            @Body NewsCenterEmptyBodyReqParam param
    );

}
