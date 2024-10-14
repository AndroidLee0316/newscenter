package com.pasc.lib.newscenter.bean;

import com.google.gson.annotations.SerializedName;


/**
 * 新闻栏目Bean
 * Created by qinguohuai143 on 2019/01/04.
 */
public class NewsColumnBean {

    @SerializedName("columnId")
    public String columnId; //新闻栏目Id
    @SerializedName("columnType")
    public String columnType;  //新闻栏目类型
    @SerializedName("columnName")
    public String columnName; // 新闻栏目名称
    @SerializedName("sort")
    public int sort;// 排序

}
