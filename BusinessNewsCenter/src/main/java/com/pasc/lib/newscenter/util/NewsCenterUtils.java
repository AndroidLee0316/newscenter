package com.pasc.lib.newscenter.util;


import android.text.TextUtils;
import android.util.Log;

import com.pasc.lib.newscenter.bean.NewsInfoBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新闻资讯工具类
 * Created by qinguohuai143 on 2019/01/14.
 */
public class NewsCenterUtils {

    /**
     * 计算两个List的差异数量
     *
     * @param shortList
     * @param longList
     * @return
     */
    public static int getStrListDiffNum(List<String> shortList, List<String> longList) {
        int diffNum = 0;
        Map<String, Integer> map = new HashMap<String, Integer>(longList.size());

        for (String string : shortList) {
            map.put(string, 1);
        }

        for (String string : longList) {
            if (map.get(string) == null) {
                map.put(string, 2);
            }
        }

        for (Map.Entry<String, Integer> entry : map.entrySet()){
            if (entry.getValue() == 2){
                diffNum++;
            }
        }

        return diffNum;
    }

    /**
     * 转换为List<String>
     *
     * @param list
     * @return
     */
    public static List<String> getListStr(List<NewsInfoBean> list) {
        List<String> liststr = new ArrayList<>();
        for (NewsInfoBean newsInfoBean : list) {
            if (newsInfoBean != null) {
                liststr.add(newsInfoBean.infoId);
            }
        }

        return liststr;
    }


    /**
     * 拆分除新闻资讯titlePicture地址
     *
     * @param source
     * @param regex
     * @return
     */
    public static String[] splitNewsTitleImgUrl(String source, String regex) {
        if (TextUtils.isEmpty(source) || TextUtils.isEmpty(regex)) {
            return null;
        }

        return source.trim().split(regex);
    }


}
