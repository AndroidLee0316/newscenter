package com.pasc.lib.newscenter.activity;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pasc.lib.base.activity.BaseActivity;
import com.pasc.lib.base.util.StatusBarUtils;
import com.pasc.lib.newscenter.NewsCenterManager;
import com.pasc.lib.newscenter.R;
import com.pasc.lib.newscenter.bean.NewsListInfoBean;
import com.pasc.lib.newscenter.fragment.NewsCenterCommonFragment;

/**
 * 新闻资讯主页Activity
 * Created by qinguohuai143 on 2019/01/04.
 */

@Route(path = "/newscenter/main/list")
public class NewsCenterCommonActivity extends BaseActivity {

    private String newsColumnType;// 栏目类型

    private static final String NEWS_CENTER_COLUMN_TYPE_KEY = "newscenter_column_type";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    @Override
    protected void onInit(@Nullable Bundle bundle) {
        // 控制状态栏颜色
        StatusBarUtils.setStatusBarColor(this, true);
        // 竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (getIntent() != null && getIntent().getExtras() != null){
            newsColumnType = getIntent().getExtras().getString(NEWS_CENTER_COLUMN_TYPE_KEY);

            if (TextUtils.isEmpty(newsColumnType)){
                newsColumnType = "1";
            }
        }
        // 添加NewsCenterCommonFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment newsfragment = NewsCenterCommonFragment.newInstance();
        transaction.add(R.id.newscenter_common_activity_container, newsfragment, "NewsCenter");
        //transaction.commitAllowingStateLoss();
        transaction.commit();

        Bundle newsBundle = new Bundle();
        newsBundle.putString(NEWS_CENTER_COLUMN_TYPE_KEY, newsColumnType);
        newsfragment.setArguments(newsBundle);

        // 测试提供给锐汉的数据接口
//        new Thread(){
//
//            @Override
//            public void run() {
//                super.run();
//
//                // 从网络或本地缓存读取
//                NewsListInfoBean newsListInfoBean = NewsCenterManager.getInstance().getTopNewsListFromNet("1");
//
//                Log.e("newsListInfoBean", newsListInfoBean.getListData().size() + "");
//            }
//        }.start();

    }

    @Override
    protected int layoutResId() {
        return R.layout.newscenter_common_activity_layout;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 控制状态栏颜色
        StatusBarUtils.setStatusBarColor(this, true);
    }
}
