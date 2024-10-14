package com.pasc.lib.newscenter.bean;


import com.google.gson.annotations.SerializedName;

/**
 * 获取头条新闻，入参Body参数
 * Created by qinguohuai143 on 2019/01/07.
 */
public class TopNewsBodyReqParam {

    @SerializedName("eachTabCount")
    public String eachTabCount;

    public TopNewsBodyReqParam(String eachTabCount) {
        this.eachTabCount = eachTabCount;
    }
}
