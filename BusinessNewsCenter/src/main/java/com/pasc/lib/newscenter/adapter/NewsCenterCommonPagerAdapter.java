package com.pasc.lib.newscenter.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;

import com.pasc.lib.newscenter.bean.NewsColumnBean;
import com.pasc.lib.newscenter.fragment.NewsCenterListFragment;

import java.util.ArrayList;
import java.util.List;


public class NewsCenterCommonPagerAdapter extends FragmentStatePagerAdapter {

    private List<NewsColumnBean> tabListBean;

    private List<Fragment> fragmentList;

    public NewsCenterCommonPagerAdapter(FragmentManager fm, List<NewsColumnBean> tabList, List<Fragment> fragmentList) {
        super(fm);
        this.tabListBean = tabList;
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        if (fragmentList != null && fragmentList.size() > 0) {
            return fragmentList.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {

        return fragmentList != null ? fragmentList.size() : 0;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (tabListBean != null && tabListBean.get(position)!= null &&
                !TextUtils.isEmpty(tabListBean.get(position).columnName)){
            return tabListBean.get(position).columnName;
        }
        return "";
    }


}
