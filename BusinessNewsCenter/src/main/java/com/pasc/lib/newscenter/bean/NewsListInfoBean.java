package com.pasc.lib.newscenter.bean;


import java.util.List;

/**
 * 新闻列表Bean
 * Created by qinguohuai143 on 2019/01/04.
 */
public class NewsListInfoBean {


    private List<NewsInfoBean> listData;

    public NewsListInfoBean(List<NewsInfoBean> listData) {
        this.listData = listData;
    }

    public List<NewsInfoBean> getListData() {
        return listData;
    }

    public void setListData(List<NewsInfoBean> listData) {
        this.listData = listData;
    }
}
