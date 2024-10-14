package com.pasc.lib.newscenter.bean;

import com.google.gson.annotations.SerializedName;


/**
 * 新闻列表详情Bean
 * Created by qinguohuai143 on 2019/01/04.
 */
public class NewsInfoBean {

    @SerializedName("infoId")
    public String infoId; //新闻id
    @SerializedName("link")
    public String link;  //新闻源链接
    @SerializedName("articleLink")
    public String articleLink;  //新闻详情H5链接
    @SerializedName("columnId")
    public String columnId; // 新闻所属分类Id
    @SerializedName("columnType")
    public String columnType; // 新闻所属分类
    @SerializedName("titlePicture")
    public String titlePicture;//新闻标题图片
    @SerializedName("title")
    public String title; //新闻标题
    @SerializedName("richContent")
    public String richContent;
    @SerializedName("issueDate")
    public String issueDate;//新闻发布时间
    @SerializedName("source")
    public String source;//新闻来源
    @SerializedName("countRead")
    public String countRead;
    @SerializedName("isTop")
    public int isTop;//是否置顶（1：是；0否）
    @SerializedName("status")
    public String status;
    @SerializedName("issueStatus")
    public String issueStatus;//发布状态 0：未发布 1：已发布 2：待生效
    @SerializedName("updatedBy")
    public String updatedBy;
    @SerializedName("createdBy")
    public String createdBy;//是否置顶（1：是；0否）
    @SerializedName("createdDate")
    public String createdDate;
    @SerializedName("updatedDate")
    public String updatedDate;

}
