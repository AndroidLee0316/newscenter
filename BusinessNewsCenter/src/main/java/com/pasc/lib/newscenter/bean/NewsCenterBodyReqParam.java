package com.pasc.lib.newscenter.bean;


import com.google.gson.annotations.SerializedName;

/**
 * 根据栏目类型获取新闻列表
 * Created by qinguohuai143 on 2019/01/07.
 */
public class NewsCenterBodyReqParam {

    @SerializedName("columnType")
    public String columnType;
    @SerializedName("pageNum")
    public String pageNum;
    @SerializedName("pageSize")
    public String pageSize;

    public NewsCenterBodyReqParam(String columnType, String pageNum, String pageSize) {
        this.columnType = columnType;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
