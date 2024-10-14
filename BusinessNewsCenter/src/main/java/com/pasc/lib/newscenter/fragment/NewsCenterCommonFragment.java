package com.pasc.lib.newscenter.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.pasc.lib.base.fragment.BaseFragment;
import com.pasc.lib.base.util.NetworkUtils;
import com.pasc.lib.newscenter.R;
import com.pasc.lib.newscenter.adapter.NewsCenterCommonPagerAdapter;
import com.pasc.lib.newscenter.bean.NewsColumnBean;
import com.pasc.lib.newscenter.data.NewsDataManager;
import com.pasc.lib.newscenter.net.NewsCenterNetManager;
import com.pasc.lib.newscenter.tablayout.TabLayout;
import com.pasc.lib.statistics.StatisticsManager;
import com.pasc.lib.widget.dialog.loading.LoadingDialogFragment;
import com.pasc.lib.widget.toolbar.PascToolbar;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class NewsCenterCommonFragment extends BaseFragment {

    private View mRootView;
    private TabLayout mTabLayout;
    private ViewPager mViewpager;
    private PascToolbar toolbar;
    private LinearLayout wrapLayout;
    private RelativeLayout errorLayout;
    private NewsCenterCommonPagerAdapter adapter;
    private SparseArray<NewsCenterListFragment> fragments = new SparseArray<>();

    private List<NewsColumnBean> newsColumnBeanList = new ArrayList<>();

    private LoadingDialogFragment loadingDialogFragment;
    private String defaultColumnType; // 默认显示栏目

    public static Fragment newInstance() {
        NewsCenterCommonFragment fragment = new NewsCenterCommonFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int layoutResId() {
        return R.layout.newscenter_common_fragment_layout;
    }

    @Override
    protected void onInit(@Nullable Bundle bundle) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = super.onCreateView(inflater, container, savedInstanceState);
            initUIView();
            loadColumnData();
        }

        return mRootView;
    }

    private void initUIView() {

        mTabLayout = mRootView.findViewById(R.id.newscenter_common_fragment_tablayout);
        mViewpager = mRootView.findViewById(R.id.newscenter_common_fragment_viewpager);
        toolbar = mRootView.findViewById(R.id.newscenter_common_fragment_title);

        wrapLayout = mRootView.findViewById(R.id.newscenter_common_wrap_layout);
        errorLayout = mRootView.findViewById(R.id.newscenter_view_empty_view);

        toolbar.addCloseImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        mRootView.findViewById(R.id.newscenter_view_empty_retry_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadColumnData();
            }
        });

        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                getFragment(position).initData();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.mView.getTextView().setTypeface(Typeface.DEFAULT_BOLD);

                mViewpager.setCurrentItem(tab.getPosition());
                // 埋点
                if (newsColumnBeanList != null && newsColumnBeanList.size() > 0
                        && tab.getPosition() <= newsColumnBeanList.size() && newsColumnBeanList.get(tab.getPosition()) != null) {

                    StatisticsManager.getInstance().onEvent("app_news_tab", newsColumnBeanList.get(tab.getPosition()).columnName);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.mView.getTextView().setTypeface(Typeface.DEFAULT);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        initTabLayout();

    }

    private void initTabLayout() {
        // TabLayout 居中显示，针对Tablayout根据Tab项的多少都按MODE_SCROLLABLE模式平均分配宽度显示
        // 修改了TabLayout的源代码
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.setTabModeBySelf(TabLayout.MODE_SCROLLABLE_EVEN_SHORT);
        mTabLayout.setupWithViewPager(mViewpager);
    }

    /**
     * 获取栏目type
     */
    private void loadColumnData() {
        if (getArguments() != null) {
            defaultColumnType = getArguments().getString("newscenter_column_type");
            if (TextUtils.isEmpty(defaultColumnType)) {
                defaultColumnType = "1";
            }
        }

        // 无列表的需单独处理
        if (NetworkUtils.isNetworkConnected(getContext())) {
            showDataUI();
            getNewsColumnTypeFromNet();
        } else {
            getNewsColumnTypeFromCache();
        }

    }

    private void getNewsColumnTypeFromCache() {
        List<NewsColumnBean> cacheList = NewsDataManager.getNewsColumnTypeListFromSp("");
        if (cacheList != null && cacheList.size() > 0) {
            showData(cacheList);
            showDataUI();
        } else {
            showNetErrorUI();
        }
    }

    /**
     * 获取栏目列表
     */
    private void getNewsColumnTypeFromNet() {
        showLoadingDialog();

        Disposable subscribe = NewsCenterNetManager.getNewsColumnList("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<NewsColumnBean>>() {
                    @Override
                    public void accept(List<NewsColumnBean> list) throws Exception {
                        Log.e("load time=1", "==" + System.currentTimeMillis());
//                        dismissLoadingDialog();
                        if (list != null && list.size() > 0) {

                            showData(list);
                            // 缓存新闻栏目类型
                            NewsDataManager.saveNewsColumnTypeList("", list);
                        }else {
                            dismissLoadingDialog();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        dismissLoadingDialog();

                        showNetErrorUI();
                        Log.d("getColumnList", "accept: throwable -> " + throwable.getMessage());
                    }
                });

    }

    /**
     * 显示TabLayout
     */
    private void showTabLayout() {
        if (mTabLayout != null) {
            mTabLayout.setVisibility(View.VISIBLE);
        }
    }


    private void showData(List<NewsColumnBean> list) {
        newsColumnBeanList.clear();
        newsColumnBeanList.addAll(list);

        if (newsColumnBeanList.size() > 0) {
            List<Fragment> fragmentList = new ArrayList<>();
            for (NewsColumnBean newsColumnBean : newsColumnBeanList) {
                if (newsColumnBean != null) {
                    fragmentList.add(NewsCenterListFragment.newInstance(newsColumnBean.columnType));
                } else {
                    fragmentList.add(NewsCenterListFragment.newInstance("1"));
                }
            }
            Log.e("load time=2", "==" + System.currentTimeMillis());

            adapter = new NewsCenterCommonPagerAdapter(getChildFragmentManager(), newsColumnBeanList, fragmentList);
            Log.e("load time=3", "==" + System.currentTimeMillis());
            mViewpager.setAdapter(adapter);
            mViewpager.setOffscreenPageLimit(adapter.getCount());
            dismissLoadingDialog();
            Log.e("load time=4", "==" + System.currentTimeMillis());
            if (newsColumnBeanList.size() > 1) {
                showTabLayout();
            }
            Log.e("load time=5", "==" + System.currentTimeMillis());
            showDefaultColumn();
        }

    }


    /**
     * 显示Type对应的栏目
     */
    private void showDefaultColumn() {
        int currentTabIndex = 0;
        for (int i = 0; i < newsColumnBeanList.size(); i++) {
            if (!TextUtils.isEmpty(defaultColumnType) && defaultColumnType.equals(newsColumnBeanList.get(i).columnType)) {
                currentTabIndex = i;
                break;
            }
        }

        final int defaultTabIndex = currentTabIndex;
        mViewpager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mViewpager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setPagerIndex(defaultTabIndex);
            }
        });

    }

    public void setPagerIndex(int index) {
        if (mViewpager != null && adapter != null && index < adapter.getCount()) {
            mViewpager.setCurrentItem(index);

            getFragment(index).initData();
        }
    }

    private NewsCenterListFragment getFragment(int position) {

        NewsCenterListFragment fragment = (NewsCenterListFragment) adapter.getItem(position);
        if (fragment == null) {
            fragment = NewsCenterListFragment.newInstance("");
            fragments.append(position, fragment);
        }

        return fragment;
    }

    /**
     * 显示网络异常页面
     */
    private void showNetErrorUI() {
        if (wrapLayout != null) {
            wrapLayout.setVisibility(View.GONE);
        }
        if (errorLayout != null) {
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示正常页面
     */
    private void showDataUI() {
        if (wrapLayout != null) {
            wrapLayout.setVisibility(View.VISIBLE);
        }
        if (errorLayout != null) {
            errorLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 显示Loading框
     */
    private void showLoadingDialog() {

        String fragmentTag = "newscentercommonfg";
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment preFragment = getFragmentManager().findFragmentByTag(fragmentTag);
        if (preFragment != null) {
            fragmentTransaction.remove(preFragment);
        }

        loadingDialogFragment = new LoadingDialogFragment.Builder()
                .setCancelable(true)
                .build();
        fragmentTransaction.add(loadingDialogFragment, fragmentTag);
        // 避免出现 IllegalStateException: Can not perform this action after onSaveInstanceState
        fragmentTransaction.commitAllowingStateLoss();
        fragmentTransaction.show(loadingDialogFragment);
    }

    private void dismissLoadingDialog() {
        if (loadingDialogFragment != null) {
            loadingDialogFragment.dismissAllowingStateLoss();
            loadingDialogFragment = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissLoadingDialog();
    }
}
