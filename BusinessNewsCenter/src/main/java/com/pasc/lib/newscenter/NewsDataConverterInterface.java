package com.pasc.lib.newscenter;

import com.pasc.lib.newscenter.bean.NewsColumnBean;
import com.pasc.lib.newscenter.bean.NewsInfoBean;
import com.pasc.lib.newscenter.bean.NewsListInfoBean;

import java.util.List;

/**
 * 文件描述：
 * 作者：zoujianbo345
 * 创建时间：2019/6/20
 * 更改时间：2019/6/20
 */
public interface NewsDataConverterInterface {
    /**
     * 新闻列表数据转换器
     **/
    List<NewsInfoBean> newsListConverter(String json);

    /**
     * 新闻栏目数据转换器
     **/
    List<NewsColumnBean> newsColumnListConverter(String json);


}
